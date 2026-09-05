<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="测试时间" prop="testTime">
        <el-date-picker clearable
                        v-model="queryParams.testTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择测试时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="输出文件url" prop="fuzzOutUrl">
        <el-input
          v-model="queryParams.fuzzOutUrl"
          placeholder="请输入输出文件url"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="源码文件id" prop="yid">
        <el-input
          v-model="queryParams.yid"
          placeholder="请输入源码文件id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="编译文件id" prop="cid">
        <el-input
          v-model="queryParams.cid"
          placeholder="请输入编译文件id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="输入文件id" prop="uid">
        <el-input
          v-model="queryParams.uid"
          placeholder="请输入输入文件id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="唯一识别标识" prop="uuid">
        <el-input
          v-model="queryParams.uuid"
          placeholder="请输入唯一识别标识"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['afl:record:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['afl:record:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['afl:record:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['afl:record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="模糊测试记录id" align="center" prop="fid" />
      <el-table-column label="测试时间" align="center" prop="testTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.testTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="输出文件url" align="center" prop="fuzzOutUrl" />
      <el-table-column label="源码文件id" align="center" prop="yid" />
      <el-table-column label="编译文件id" align="center" prop="cid" />
      <el-table-column label="输入文件id" align="center" prop="uid" />
      <el-table-column label="唯一识别标识" align="center" prop="uuid" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['afl:record:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['afl:record:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改测试记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="测试时间" prop="testTime">
          <el-date-picker clearable
                          v-model="form.testTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择测试时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="输出文件url" prop="fuzzOutUrl">
          <el-input v-model="form.fuzzOutUrl" placeholder="请输入输出文件url" />
        </el-form-item>
        <el-form-item label="源码文件id" prop="yid">
          <el-input v-model="form.yid" placeholder="请输入源码文件id" />
        </el-form-item>
        <el-form-item label="编译文件id" prop="cid">
          <el-input v-model="form.cid" placeholder="请输入编译文件id" />
        </el-form-item>
        <el-form-item label="输入文件id" prop="uid">
          <el-input v-model="form.uid" placeholder="请输入输入文件id" />
        </el-form-item>
        <el-form-item label="唯一识别标识" prop="uuid">
          <el-input v-model="form.uuid" placeholder="请输入唯一识别标识" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRecord, getRecord, delRecord, addRecord, updateRecord } from "@/api/afl/record";

export default {
  name: "Record",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 测试记录表格数据
      recordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        testTime: null,
        fuzzOutUrl: null,
        yid: null,
        cid: null,
        uid: null,
        uuid: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        yid: [
          { required: true, message: "源码文件id不能为空", trigger: "blur" }
        ],
        cid: [
          { required: true, message: "编译文件id不能为空", trigger: "blur" }
        ],
        uid: [
          { required: true, message: "输入文件id不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询测试记录列表 */
    getList() {
      this.loading = true;
      listRecord(this.queryParams).then(response => {
        this.recordList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        fid: null,
        testTime: null,
        fuzzOutUrl: null,
        yid: null,
        cid: null,
        uid: null,
        uuid: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.fid)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加测试记录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const fid = row.fid || this.ids
      getRecord(fid).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改测试记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.fid != null) {
            updateRecord(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addRecord(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const fids = row.fid || this.ids;
      this.$modal.confirm('是否确认删除测试记录编号为"' + fids + '"的数据项？').then(function() {
        return delRecord(fids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('afl/record/export', {
        ...this.queryParams
      }, `record_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

