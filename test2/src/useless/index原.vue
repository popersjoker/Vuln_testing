<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="源码文件" prop="addrSeedfileName">
        <el-input
          v-model="queryParams.addrSeedfileName"
          placeholder="请输入源码文件名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="上传时间" prop="addrSeedfileUploadtime">
        <el-date-picker clearable
                        v-model="queryParams.addrSeedfileUploadtime"
                        type="date"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        placeholder="请选择上传时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['addrgen:handleseedfile:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['addrgen:handleseedfile:export']"
        >导出
        </el-button>
      </el-col>
      <el-upload
        v-hasPermi="['addrgen:handleseedfile:importSeedFile']"
        style="padding-left: 30px"
        ref="upload"
        :limit="1"
        name="file"
        :data="fileInfo"
        accept=".txt"
        :action="upload.url"
        :headers="upload.headers"
        :file-list="upload.fileList"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :on-change="handleChange"
        :before-remove="handleRemove"
        :before-upload="handleBeforeUpload"
        :auto-upload="false">

        <el-button slot="trigger" size="mini" type="primary">选择源码文件</el-button>
        <el-button style="margin-left: 10px;" size="mini" type="success" :loading="upload.isUploading"
                   @click="submitUpload">上传到服务器
        </el-button>
        <div slot="tip" class="el-upload__tip" style="font-size: 14px;color: red">
          提示：选择种子地址文件，只能上传txt文件！上传文件后，请先进行格式化再进行其他操作！
        </div>
      </el-upload>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <el-table v-loading="loading" :data="handleseedfileList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" type="index" width="50">
        <template slot-scope="scope">
          {{ (scope.$index + 1) + (updateCurrentPage() - 1) * queryParams.pageSize }}
        </template>
      </el-table-column>

      <el-table-column label="源码文件" align="center" prop="addrSeedfileName"/>
      <el-table-column label="上传时间" align="center" prop="addrSeedfileUploadtime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.addrSeedfileUploadtime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="查看源码" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <div>
            <router-link :to="'/addrgen/details/'+scope.row.addrSeedfileId" style="color: #00afff;font-size: smaller">
              查看
            </router-link>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="插桩编译" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <div>
            <el-button
              size="mini"
              type="text"
              :icon="scope.row.formaticon"
              @click="formatAddr(scope.row)"
              v-hasPermi="['addrgen:handleseedfile:formatAddr']"
            >插桩编译
            </el-button>
            &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
            <router-link :to="'/addrgen/format/'+scope.row.addrSeedfileId" style="color: #00afff;font-size: smaller">
              编译详情
            </router-link>
          </div>
        </template>
      </el-table-column>
      <!--<el-table-column label="采集IPv6地址" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <router-link :to="'/addrgen/collect/'+scope.row.addrSeedfileId" style="color: #00afff;font-size: smaller">设置</router-link>
        </template>
      </el-table-column>-->
      <el-table-column label="测试用例" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <router-link :to="'/addrgen/detect/'+scope.row.addrSeedfileId" style="color: #00afff;font-size: smaller">
            上传输入
          </router-link>
          &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
          <router-link :to="'/addrgen/detect/'+scope.row.addrSeedfileId" style="color: #00afff;font-size: smaller">
            详情
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="处理" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['addrgen:handleseedfile:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="模糊测试" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <router-link
            :to="'/fuzz/fuzz-test/'+scope.row.addrSeedfileId+'/'+scope.row.addrSeedfileName+'/'+scope.row.formaticon"
            tag="button" style="background-color: orangered;font-size: 12px;color: white;border: none">Fuzz Testing
          </router-link>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      ref="page"
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

  </div>
</template>

<script>
import {
  updateHandleseedfile2,
  runJobforDetect,
  runJob,
  formatIPv6,
  listHandleseedfile,
  getHandleseedfile,
  delHandleseedfile,
  addHandleseedfile,
  updateHandleseedfile
} from "@/api/addrgen/handleseedfile";
import {getToken} from "@/utils/auth";

export default {
  name: "Handleseedfile",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedAddrgenSeedfileDetails: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 种子地址文件总览表格数据
      handleseedfileList: [],
      // 种子文件详情表格数据
      addrgenSeedfileDetailsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      openCollect: false,
      openActive: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        addrSeedfileName: null,
        addrSeedfileUploadtime: null,
        addrSeedfileSize: null
      },
      //文件信息
      fileInfo: {},
      // 上传参数
      upload: {
        // 是否禁用上传
        isUploading: false,
        // 设置上传的请求头部
        headers: {Authorization: "Bearer " + getToken()},
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/addrgen/handleseedfile/importSeedFile",
        // 上传的文件列表
        fileList: []
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        addrSeedfileName: [
          {required: true, message: "文件名不能为空", trigger: "blur"}
        ],
        addrSeedfileUploadtime: [
          {required: true, message: "上传时间不能为空", trigger: "blur"}
        ],
        addrSeedfileSize: [
          {required: true, message: "文件大小不能为空", trigger: "blur"}
        ]
      }
    };
  },
  created() {
    this.getList();
    //this.timeout = setInterval(this.getList,30000);//注意this.getCommands不加括号
  },
  methods: {
    updateCurrentPage() {
      return this.$refs["page"].currentPage;
    },
    /** 查询种子地址文件总览列表 */
    getList() {
      this.loading = true;
      listHandleseedfile(this.queryParams).then(response => {
        this.handleseedfileList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.openCollect = false;
    },
    cancelactiveStartForm() {
      this.openActive = false;
    },
    // 表单重置
    reset() {
      this.form = {
        addrSeedfileId: null,
        addrSeedfileName: null,
        addrSeedfileUploadtime: null,
        addrSeedfileSize: null
      };
      this.addrgenSeedfileDetailsList = [];
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
      this.ids = selection.map(item => item.addrSeedfileId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加种子地址文件总览";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const addrSeedfileId = row.addrSeedfileId || this.ids
      getHandleseedfile(addrSeedfileId).then(response => {
        this.form = response.data;
        this.addrgenSeedfileDetailsList = response.data.addrgenSeedfileDetailsList;
        this.open = true;
        this.title = "修改种子地址文件总览";
      });
    },
    /** 提交按钮 */
    /*submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.addrgenSeedfileDetailsList = this.addrgenSeedfileDetailsList;
          if (this.form.addrSeedfileId != null) {
            updateHandleseedfile(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addHandleseedfile(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },*/

    /* 格式化 */
    formatAddr(row) {
      row.formaticon = 'el-icon-loading';
      formatIPv6(row).then(reponse => {
        if (reponse.data === 1) {
          this.$set(row, 'formaticon', 'el-icon-check');
          this.$set(this, 'formaticon', 'el-icon-check');
          this.$modal.msgSuccess("格式化完成", 5000);
          console.log(row.formaticon)
        } else if (reponse.data === -1) {
          this.$modal.msgError("操作失败,种子地址输入格式有错", 5000);
          this.$set(row, 'formaticon', 'el-icon-document-checked');
          this.$set(this, 'formaticon', 'el-icon-document-checked');
        }
        return row;
      });
      this.$message({
        showClose: true,
        message: '格式化已开始，格式化会生成首选IPv6地址，ASN、BGP前缀、Interface ID和响应类型'
      });
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const addrSeedfileIds = row.addrSeedfileId || this.ids;
      this.$modal.confirm('是否确认删除种子地址文件总览编号为"' + addrSeedfileIds + '"的数据项？').then(function () {
        return delHandleseedfile(addrSeedfileIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 种子文件详情序号 */
    rowAddrgenSeedfileDetailsIndex({row, rowIndex}) {
      row.index = rowIndex + 1;
    },

    /** 种子文件详情删除按钮操作 */
    handleDeleteAddrgenSeedfileDetails() {
      if (this.checkedAddrgenSeedfileDetails.length == 0) {
        this.$modal.msgError("请先选择要删除的种子文件详情数据");
      } else {
        const addrgenSeedfileDetailsList = this.addrgenSeedfileDetailsList;
        const checkedAddrgenSeedfileDetails = this.checkedAddrgenSeedfileDetails;
        this.addrgenSeedfileDetailsList = addrgenSeedfileDetailsList.filter(function (item) {
          return checkedAddrgenSeedfileDetails.indexOf(item.index) == -1
        });
      }
    },
    /** 复选框选中数据 */
    handleAddrgenSeedfileDetailsSelectionChange(selection) {
      this.checkedAddrgenSeedfileDetails = selection.map(item => item.index)
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('addrgen/handleseedfile/export', {
        ...this.queryParams
      }, `handleseedfile_${new Date().getTime()}.xlsx`)
    },
    // 文件提交处理
    submitUpload() {
      this.$refs.upload.submit()
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.$refs.upload.clearFiles();
      this.getList();
      this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "提示", {dangerouslyUseHTMLString: true});
      this.upload.isUploading = false;
    },
    handleChange(file, fileList) {

    },
    handleRemove(file, fileList) {

    },
    handleBeforeUpload(file) {
      console.log(file.name);
      console.log(file.size);
      this.fileInfo.addrSeedfileName = file.name;
      this.fileInfo.addrSeedfileSize = file.size;
    },
  }
};
</script>
<style>
.el-dropdown {
  vertical-align: top;
}

.el-dropdown + .el-dropdown {
  margin-left: 15px;
}

.el-icon-arrow-down {
  font-size: 12px;
}
</style>
