<template>
  <div class="app-container">
    <el-form ref="queryForm" size="small" :inline="true" label-width="68px" v-show="showSearch">
      <el-form-item label="源码文件" prop="fileName">
        <el-input
          v-model="queryParams.fileName"
          placeholder="请输入源码文件名"
          clearable
          @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="测试时间">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          style="width: 240px"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd">
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
          @click="recordDelete"
        >删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="RecordList"></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="handleRecordList"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column prop="fid" label="序号" width="100" align="center" type="index"/>
      <el-table-column prop="fileName" label="源码文件" align="center"/>
      <el-table-column prop="testTime" label="测试时间" align="center" width="200">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.testTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="crashes" label="Crashes" align="center">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-search"
            @click="crashesQuery(scope.row)"
          >查看详情
          </el-button>
        </template>
      </el-table-column>
      <el-table-column prop="hangs" label="Hangs" align="center">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-search"
            @click="hangsQuery(scope.row)"
          >查看详情
          </el-button>
        </template>
      </el-table-column>
      <el-table-column prop="queue" label="Queue" align="center">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-search"
            @click="queueQuery(scope.row)"
          >查看详情
          </el-button>
        </template>
      </el-table-column>
      <el-table-column prop="paint" label="绘图" align="center">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-search"
            @click="paintQuery(scope.row)"
          >查看详情
          </el-button>
        </template>
      </el-table-column>
      <el-table-column prop="fuzzerStats" label="fuzzer-stats" align="center">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-search"
            @click="fuzzerStatsQuery(scope.row)"
          >查看详情
          </el-button>
        </template>
      </el-table-column>
      <el-table-column prop="handle" label="处理" align="center">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="recordDelete(scope.row)"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      ref="page"
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="RecordList"/>
    <!--所有弹窗我打算写在这里，主要包括：查看详情：crashes hangs queue 绘图 fuzzer-stats-->

    <!-- 弹窗1 : crashes -->
    <el-dialog :visible="crashesVisible" title="crashes" @close="closeCrashesPopup">
      <div class="popup-text-container"></div>
      <el-table
        :data="crashesList"
        v-loading="loading">
        <el-table-column prop="crashesName" label="crashes文件名" align="center" show-overflow-tooltip
                         style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"></el-table-column>
        <el-table-column prop="crashesFileQuery" label="操作" width="80" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-search"
              @click="crashesFileQuery(scope.row)"
            >查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
    <el-dialog :visible="CrashesContentVisible" title="Content" @close="closeCrashesPopup">
      <pre style="white-space: pre-wrap;">{{texxt}}</pre>
    </el-dialog>

    <!-- 弹窗2 : hangs -->
    <el-dialog :visible="hangsVisible" title="hangs" @close="closeHangsPopup">
      <div class="popup-text-container">
        <el-table
          :data="hangsList"
          v-loading="loading">
          <el-table-column prop="hangsName" label="hangs文件名" align="center" show-overflow-tooltip
                           style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"></el-table-column>
          <el-table-column prop="hangsFileQuery" label="操作" width="80" align="center">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-search"
                @click="hangsFileQuery(scope.row)"
              >查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
    <el-dialog :visible="HangsContentVisible" title="Content" @close="closeHangsPopup">
      <pre style="white-space: pre-wrap;">{{texxt2}}</pre>
    </el-dialog>
    <!-- 弹窗3 : queue -->
    <el-dialog :visible="queueVisible" title="queue" @close="closeQueuePopup">
      <div class="popup-text-container">
        <el-table
          :data="queueList"
          v-loading="loading">
          <el-table-column prop="queueName" label="queue文件名" align="center" show-overflow-tooltip
                           style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"></el-table-column>
          <el-table-column prop="queueFileQuery" label="操作" width="80" align="center">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-search"
                @click="queueFileQuery(scope.row)"
              >查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
    <el-dialog :visible="QueueContentVisible" title="Content" @close="closeQueuePopup">
      <pre style="white-space: pre-wrap;">{{texxt3}}</pre>
    </el-dialog>
    <!-- 弹窗4 : fuzzer-stats -->
    <el-dialog :visible="fuzzerstatsVisible" title="fuzzer-stats" @close="closeStatsPopup">
      <div class="popup-text-container">
        <pre style="white-space: pre-wrap;">{{ fuzzerstatsText }}</pre>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {parseTime} from "../../../utils/ruoyi";
import {
  delRecord,
  getCrashesFile,
  getHangsFile, getPaintFile, getPaintHtml, getQueueFile,
  getRecord,
  getRecordCrashes, getRecordFuzzerStats,
  getRecordHangs,
  getRecordList, getRecordQueue
} from "@/api/afl/record";
import {getFile, getFileNameAndTwoTime, getYidByFileName, listFileInfo3} from "@/api/afl/file";
import data from "@/views/system/dict/data.vue";

export default {
  computed: {
    data() {
      return data
    }
  },
  data() {
    return {
      imageUrl: '',
      texxt: '',
      texxt2: '',
      texxt3: '',
      fuzzerstatsText: '',
      crashesList: [],
      hangsList: [],
      queueList: [],
      showSearch: true,
      searchkeyword: '',
      originalList: [],
      handleRecordList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        testTime: null,
        fileName: '',
        fid: null,
        yid: null,
      },
      ids: [],
      single: true,
      multiple: false,
      total: 0,
      recordList: [],
      loading: true,
      crashesVisible: false,
      hangsVisible: false,
      queueVisible: false,
      fuzzerstatsVisible: false,
      globalFid: null,
      CrashesContentVisible: false,
      HangsContentVisible: false,
      QueueContentVisible: false,
      fuzzerStatsVisible: false,
      paintVisible: false,
      paintList: [],
      dateRange: [],
      paintFileVisible: false,
    }

  },
  created() {
    this.RecordList()
  },
  mounted() {
    this.RecordList()
  },
  methods: {

    RecordList() {
      this.loading = true
      getRecordList(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.handleRecordList = response.rows.map(row => {
          return {
            testTime: row.testTime,
            fid: row.fid,
            yid: row.yid,
            fileName: row.tableFile.fileName
          }
        })
        this.total = response.total
        this.loading = false
      })
    },
    parseTime,
    handleQuery() {
      this.loading = true
      this.queryParams.pageNum = 1
      console.log(this.queryParams)
      // 有一个bug，当我什么都不输入时，以及只输入文件名时会报错
      // 改正一下，把前端逻辑改动一下，如果只输入文件名会怎样？
      // 如果空输入会怎么样？
      if (this.queryParams.fileName === '' && (this.dateRange && this.dateRange.length !== 2)) {
        this.resetQuery()  // 如果空输入会刷新表格
      } else if (this.dateRange && this.dateRange.length !== 2) {  // 如果空日期输入但是不空文本输入
        getYidByFileName(this.addDateRange(this.queryParams, this.dateRange)).then(res => {
          console.log(88888)
          console.log(res)
          this.handleRecordList = res.rows
            .filter(row => row.tableRecord.tableFile && row.tableRecord.tableFile.fileName.includes(row.fileName)) // 添加过滤条件
            .map(row => {
              return {
                testTime: row.tableRecord.testTime,
                fid: row.tableRecord.fid,
                yid: row.tableRecord.yid,
                fileName: row.tableRecord.tableFile.fileName,
              }
            })
          this.total = res.total
          this.loading = false
        })
        // this.queryParams.fileName = ''
      } else { // 如果日期不是空输入
        getFileNameAndTwoTime(this.addDateRange(this.queryParams, this.dateRange)).then(res => {
          console.log(88888)
          console.log(res)
          this.handleRecordList = res.rows.map(row => {
            return {
              testTime: row.tableRecord.testTime,
              fid: row.tableRecord.fid,
              yid: row.tableRecord.yid,
              fileName: row.fileName,
            }
          })
          this.total = res.total
          this.loading = false
        })
        // this.queryParams.fileName = ''
      }
    },
    resetQuery() {
      this.queryParams.fileName = ''
      this.dateRange = []
      this.RecordList()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.fid)
      this.single = selection.length !== 1
      this.multiple = !selection.length
      console.log(this.ids)
    },
    updateCurrentPage() {
      return this.$refs["page"].currentPage
    },
    crashesQuery(row) { // 根据fid查询数据库的fuzz-out的path fuzz_out_url
      console.log('理塘丁真')
      console.log(row) // fid testTime yid fileName
      this.globalFid = row.fid
      getRecordCrashes(row.fid).then(res => {
        console.log(res)
        this.crashesList = res.data.map(fileName => ({crashesName: fileName}))
        console.log('why you always so cool')
        console.log(this.crashesList)
        this.crashesVisible = true
      })
    },
    crashesFileQuery(CurrentRow) {
      console.log(this.globalFid.type)
      console.log(CurrentRow.type) // 只有一个crashesName
      const params = {Fid: this.globalFid, CrashesName: CurrentRow.crashesName}
      console.log(CurrentRow.crashesName)
      getCrashesFile(params).then(response => {
        // 再写一个窗口，可以查看具体的输出文件内容
        this.CrashesContentVisible = true
        console.log('百度地图我给你指路！')
        console.log(response.msg)
        this.texxt = response.msg
      })
    },
    closeCrashesPopup() {
      this.crashesVisible = false
      this.CrashesContentVisible = false // 临时测试使用
    },

    hangsQuery(CurrentRow) { // 页面表格上的查看详情
      this.hangsVisible = true
      this.globalFid = CurrentRow.fid
      getRecordHangs(CurrentRow.fid).then(res => {
        console.log(res)
        this.hangsList = res.data.map(fileName => ({hangsName: fileName}))
        this.hangsVisible = true
      })
    },
    hangsFileQuery(CurrentRow) {
      const params = {Fid: this.globalFid, HangsName: CurrentRow.hangsName}
      getHangsFile(params).then(response => {
        this.HangsContentVisible = true
        this.texxt2 = response.msg
      })
    },
    closeHangsPopup() {
      this.hangsVisible = false
      this.HangsContentVisible = false
    },

    queueQuery(CurrentRow) {
      this.queueVisible = true
      this.globalFid = CurrentRow.fid
      console.log(this.globalFid)
      getRecordQueue(CurrentRow.fid).then(res => {
        console.log(res)
        this.queueList = res.data
          .filter(fileName => !fileName.includes('.state'))
          .map(fileName => ({queueName: fileName}));
        this.queueVisible = true
      })
    },
    queueFileQuery(CurrentRow) {
      const params = {Fid: this.globalFid, QueueName: CurrentRow.queueName}
      getQueueFile(params).then(response => {
        this.QueueContentVisible = true
        this.texxt3 = response.msg
      })
    },
    closeQueuePopup() {
      this.queueVisible = false
      this.QueueContentVisible = false
    },

    fuzzerStatsQuery(CurrentRow) {
      this.fuzzerstatsVisible = true
      getRecordFuzzerStats(CurrentRow.fid).then(res => {
        // 处理文本内容，将换行符替换为换行标签，并对每一行进行格式化
        this.fuzzerstatsText = res.msg
        console.log(this.fuzzerstatsText)
      })
    },
    closeStatsPopup() {
      this.fuzzerstatsVisible = false
    },
    paintQuery(CurrentRow) {
      const currentTime = new Date().toLocaleString()
      getPaintHtml(CurrentRow.fid).then(res => { // 此时的Out-HTML目录下就是该行运行结果生成的，放心传递吧
        console.log(res)

        this.$router.push(
          {
            path: "/afl/paint",
            query: {
              currentTime: currentTime,
              fid: CurrentRow.fid,
            }
          })
          
      })
    },
    recordDelete(CurrentRow) {
      const fids = CurrentRow.fid || this.ids
      console.log(fids)
      this.$modal.confirm('确认删除测试记录信息吗？此操作不可逆').then(function () {
        console.log(fids)
        return delRecord(fids)
      }).then(() => {
        this.RecordList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {

      })
    },
  }
}
</script>
<style>
.popup-text-container {
  max-width: 600px;
}
</style>
