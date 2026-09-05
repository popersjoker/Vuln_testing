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
      <el-form-item label="上传时间">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="yyyy-MM-dd"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 240px">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- Stage 1 End ////////////////////////////////////////////////////////-->

    <el-row :gutter="10" class="mb8">
      <!--      这里的:action :headers :file-list我都没写，分别等于upload.url,upload.headers,upload.fileList-->
      <!--      <el-upload-->
      <!--        :limit="1"-->
      <!--        name="file"-->
      <!--        :data="fileInfo"-->
      <!--        accept=".txt"-->
      <!--        :action="upload.url"-->
      <!--        :headers="upload.headers"-->
      <!--        :file-list="upload.fileList"-->
      <!--        :on-progress="handleFileUploadProgress"-->
      <!--        :on-success="handleFileSuccess"-->
      <!--        :on-change="handleChange"-->
      <!--        :before-remove="handleRemove"-->
      <!--        :before-upload="handleBeforeUpload"-->
      <!--        :auto-upload="false">-->
      <el-button
        type="danger"
        plain
        icon="el-icon-delete"
        size="mini"
        :disabled="multiple"
        @click="fileDelete"
      >删除
      </el-button>
      <el-button size="mini" type="primary" @click="showSelectForm">选择源码文件</el-button>
      <!--      </el-upload>-->

      <!-- ///////////////////////////////////////////////////////////////////////////////////-->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="list123"></right-toolbar>
    </el-row>

    <el-table :data="handlefileList" @selection-change="handleSelectionChange" v-loading="loading">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" align="center" type="index" width="50" prop="yid"/>
      <el-table-column label="源码文件" align="center" prop="fileName"/>
      <el-table-column label="上传时间" align="center" prop="uploadTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.uploadTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <!-- 查看源码  列 使用template来定义列中的数据-->
      <el-table-column label="查看源码" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-query"
            @click="codeQuery(scope.row)">
            查看
          </el-button>
        </template>
      </el-table-column>

      <el-table-column label="插桩编译" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <div>
            <el-button
              size="mini"
              type="text"
              :icon="scope.row.formaticon"
              @click="gccCompile(scope.row)"
            >插桩编译
            </el-button>
            &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="compileQuery(scope.row)">
              详情
            </el-button>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="测试用例" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <!--          点击这个链接可以弹出文件上传框，选择要上传的txt，确定后，该txt被复制到fuzz-in下-->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="inputFileUpload(scope.row)">
            上传输入
          </el-button>
          &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="inputFileQuery(scope.row)">
            详情
          </el-button>
        </template>
      </el-table-column>

      <el-table-column label="处理" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="fileUpdate(scope.row)">
            修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="fileDelete(scope.row)"
          >删除
          </el-button>
        </template>
      </el-table-column>

      <el-table-column label="模糊测试" align="center" class-name="small-padding fixed-width">
        <!--    跳到fuzz-test.vue中 跳转之后在fuzz-test可以显示compiled文件名以及fuzz-in的url，这里要填入fuzz-out的url，-->
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="aflFuzzTest(scope.row)">
            点击测试
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
      @pagination="list123"
    />

    <!--    所有的弹窗我打算都写在下面-->
    <el-dialog title="选择源码文件" :visible.sync="selectDialogVisible" :close-on-click-modal="false">
      <el-form ref="form" :model="selectFormData" label-width="80px">
        <el-form-item label="源码文件">
          <el-input v-model="selectFormData.fileName" readonly>
            <template slot="append">
              <el-button icon="el-icon-folder-opened" v-if="showFileChooserIcon" @click="openFileChooser"></el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="输入文件">
          <el-input v-model="selectFormData.inputFileName" readonly>
            <template slot="append">
              <el-button icon="el-icon-folder-opened" @click="openInputFileChooser"
                         v-if="showInputFileChooserIcon"></el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog :visible="codePopupVisible" title="源码查看" @close="closeCodePopup">
      <div class="popup-text-container">
        <pre class="language-c"><code>{{ backendTextData }}</code></pre>
      </div>
    </el-dialog>
    <!--666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666-->
    <!--2023-11-1 17:14-->
    <el-dialog :visible="inputPopupVisible" title="输入文件详情" @close="closeInputPopup">
      <div class="popup-text-container">
        <pre class="language-c"><code>{{ ShowInputFileText }}</code></pre>
      </div>
    </el-dialog>
    <el-dialog :visible="xqDialogVisible" title="详情" @close="xqDialogVisible = false" width="40%"
               :style="{'max-height': '70vh','overflow-y': 'auto'}">
      <pre>文件名: {{ fileName1 }}</pre>
      <pre>上传路径: {{ uploadUrl1 }}</pre>
      <pre>编译文件名: {{ compiledName1 }}</pre>
      <pre>编译路径: {{ compiledUrl1 }}</pre>
    </el-dialog>
  </div>
</template>

<script>

import {getToken} from "@/utils/auth";
import {delFile, getFile, listFile, listFileInfo3, updateFiles, uploadFiles, uploadInput} from "@/api/afl/file";
import {parseTime} from "@/utils/ruoyi";
import {AflGcc, getCode, getCompile, getInputFile, getInputInfo} from "@/api/afl/code";
import 'prismjs/themes/prism.css'
import 'prismjs'
import 'prismjs/components/prism-c'
import customRoutes from '@/api/afl/customRoutes.js';
import redirect from "@/views/redirect.vue";

export default {
  name: "Handlefile",
  components: {},
  data() {
    return {
      o: null,
      dateRange: [],
      multiple: false, // 不禁用删除按钮的意思
      upload: {
        isUploading: false, // 是否禁用上传
        headers: {Authorization: "Bearer" + getToken()},  // 设置上传的请求头Authorization 头部用于传递身份验证信息，以便服务器能够验证请求的合法性。其中，Bearer 是一种常见的身份验证方案，用于传递令牌（Token）。 getToken() 是一个函数，用于获取身份验证令牌。在代码执行时，getToken() 函数会被调用，并将其返回值与字符串 "Bearer " 进行拼接，以构成完整的身份验证头部值。
        url: process.env.VUE_APP_BASE_API + "/home/zyz/Desktop",  // 上传的地址，还没指定上传到具体的位置
        fileList: []  // 上传的文件列表 ? 什么东西不懂
      },

      handlefileList: [],  // 显示yid ， file_name ， upload_time
      showSearch: true,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        fileName: null,
        uploadTime: null,
        yid: null, // 这里写一下序号id，不知道后面用到哪里
      },
      ids: [],
      total: 0,
      selectDialogVisible: false,
      selectFormData: {
        fileName: '',
        inputFileName: '',
        file: null,
        inputFile: null,
        yid: null
      },
      codePopupVisible: false, // 控制弹窗显示与隐藏的变量 --- 源码详情窗口
      backendTextData: '',  // 用于展示后端文本数据的变量

      inputPopupVisible: false,  // 控制弹窗显示与隐藏的变量 --- 输入文件内容详情窗口
      ShowInputFileText: '',    // 用于展示后端文本数据的变量
      // msg: '',
      xqDialogVisible: false,
      fileName1: '',
      uploadUrl1: '',
      compiledName1: '',
      compiledUrl1: '',
      loading: true,
      showFileChooserIcon: true,
      showInputFileChooserIcon: true,
    }
  },
  created() {
    this.list123()
  },
  methods: {
    parseTime,
    handleQuery() { // 处理搜索事件，在文本框中输入文件名按enter和点击搜索按钮都能触发
      this.queryParams.pageNum = 1
      console.log(this.queryParams.uploadTime)  // 可以查到日期信息，想想如何根据日期查询
      this.list123()
    },
    resetQuery() { // 重置按钮触发，可以清除input中的内容和选择的上传时间
      this.queryParams.fileName = ''
      // 清空日期选择框
      this.dateRange = []
      this.handleQuery()
    },

    list123() {
      this.loading = true
      listFileInfo3(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        console.log(response)
        this.handlefileList = response.rows
        this.total = response.total
        this.loading = false
      })
    },

    handleSelectionChange(selection) {  // 多选框选中数据
      this.ids = selection.map(item => item.yid)
      this.single = selection.length !== 1
      this.multiple = !selection.length
      console.log(this.ids)
    },

    updateCurrentPage() {
      return this.$refs["page"].currentPage
    },

    gccCompile(row) {
      // 点击这里触发事件，请求后台做一个命令：afl-gcc -g -o 生成文件名，把路径也带上 要编译的文件,所以事件应该是一个请求js，js中负责把文件的url传过去，后台执行gcc需要知道对谁做，在哪做，gcc后生成的文件放在哪
      // 过程：点击触发，找到这行的yid，然后select upload_url, file_name by yid，根据上传路径和文件名共同确定要编译的的东西，根据上传路径这个位置设置为编译目录，此目录下编译文件自动生成
      row.formaticon = 'el-icon-loading'
      console.log(row.yid)
      AflGcc(row.yid).then(response => {
        // 如果能从后端响应回一条成功的消息，就弹提示框，提示编译成功
        // 否则就把响应得到的失败消息也用提示框弹出来
        if (response === '成功') {
          this.$message({
            message: '成功',
            type: 'success',
            duration: 3000
          })
        } else {
          this.$message({
            message: '失败',
            type: 'failure',
            duration: 3000
          })
        }
      })
    },
    compileQuery(row) {
      getCompile(row.yid.toString()).then(response => {
        console.log(response)
        this.fileName1 = response.data.fileName1
        this.uploadUrl1 = response.data.uploadUrl1
        this.compiledName1 = response.data.compiledName1
        this.compiledUrl1 = response.data.compiledUrl1
        console.log(this.fileName1)
        console.log(this.uploadUrl1)
        console.log(this.compiledName1)
        console.log(this.compiledUrl1)

        this.xqDialogVisible = true
      })
    },
    showSelectForm() {
      this.selectDialogVisible = true // 展示弹窗
      this.showFileChooserIcon = true // 展示文件选择的图标
      this.showInputFileChooserIcon = true
    },
    openFileChooser() {
      // 打开源码文件选择器
      const fileInput = document.createElement('input')
      fileInput.type = 'file'
      fileInput.accept = '.c,.cpp,.rar,.zip'
      fileInput.addEventListener('change', (event) => {
        const file = event.target.files[0]
        if (file) {
          this.selectFormData.file = file
          this.selectFormData.fileName = file.name
        }
      })
      fileInput.click()
    },
    openInputFileChooser() {
      // 打开输入文件选择器
      const fileInput = document.createElement('input')
      fileInput.type = 'file'
      fileInput.accept = '.txt'
      fileInput.addEventListener('change', (event) => {
        const inputFile = event.target.files[0]
        if (inputFile) {
          this.selectFormData.inputFile = inputFile
          this.selectFormData.inputFileName = inputFile.name
        }
      })
      fileInput.click()
    },
    handleCancel() {
      // 处理取消按钮点击事件
      this.selectDialogVisible = false
      this.$refs.form.resetFields()
    },

    handleConfirm() {
      this.queryParams.uploadTime = new Date().toISOString()
      const formattedTime = parseTime(this.queryParams.uploadTime, '{y}-{m}-{d} {h}:{i}:{s}')
      // 处理确认按钮点击事件
      if (!this.selectFormData.fileName) {
        // 如果源码文件名为空，显示错误提示
        this.$message.error('源码文件不为空！')
      } else if (!this.isSupportedFileType(this.selectFormData.fileName, ['.c', '.cpp','.rar','.zip'])) {
        // 如果源码文件类型不支持，显示错误提示
        this.$message.error('源码文件类型不支持!')
      } else if (/\s/.test(this.selectFormData.fileName)) {
        // 如果源码文件名包含空格，显示错误提示
        this.$message.error('源码文件名不能包含空格！')
      } else if (this.selectFormData.inputFileName && !this.isSupportedFileType(this.selectFormData.inputFileName, ['.txt'])) {
        this.$message.error('输入文件类型不支持！')
      } else if (/\s/.test(this.selectFormData.inputFileName)) {
        // 如果输入文件名包含空格，显示错误提示
        this.$message.error('输入文件名不能包含空格！')
      } else {
        // 进行文件上传等操作
        console.log('选择的源码文件名：', this.selectFormData.fileName)
        console.log('选择的输入文件名：', this.selectFormData.inputFileName)
        const formdata = new FormData()
        if (this.showFileChooserIcon && this.showInputFileChooserIcon) { // 选择源码文件的按钮触发 修改按钮也能触发
          formdata.append('sourceFile', this.selectFormData.file)
          formdata.append('inputFile', this.selectFormData.inputFile)
          formdata.append('currentTime', formattedTime)
          uploadFiles(formdata)
          this.$message.success('源码文件上传成功！')
        } else if (!this.showFileChooserIcon && this.showInputFileChooserIcon) { // 上传输入的按钮触发
          formdata.append('inputFile', this.selectFormData.inputFile)
          formdata.append('yid', this.selectFormData.yid)
          uploadInput(formdata)
          this.$message.success('输入文件上传成功！')
        } else if (this.showFileChooserIcon && !this.showInputFileChooserIcon) {
          formdata.append('yid', this.o)
          formdata.append('sourceFile', this.selectFormData.file)
          formdata.append('inputFile', this.selectFormData.inputFile)
          formdata.append('currentTime', formattedTime)
          updateFiles(formdata)
          this.$message.success('源码文件修改成功！')
        }
        this.selectDialogVisible = false
        this.$refs.form.resetFields()
      }
    },

    isSupportedFileType(fileName, supportedTypes) {
      // 判断文件类型是否支持
      const extension = fileName.substring(fileName.lastIndexOf('.')).toLowerCase()
      return supportedTypes.includes(extension)
    },
    codeQuery(currentRow) {
      // 这里功能用于显示弹窗，点击之后弹窗显示代码  需要写一下弹窗的效果
      console.log(currentRow.yid)
      const Stryid = (String(currentRow.yid))
      console.log(Stryid)
      this.backendTextData = 'xswl'
      // 这里向后端发送请求 传参 yid ---> 数据库查询 upload_url,file_name ---> 根据查询到的url执行命令：在前端显示ubuntu的.c文件内容
      // 相当于 终端下访问 cat /upload_url/file_name
      getCode(Stryid).then(response => {
        // 这里写把内容显示出来
        this.backendTextData = response
        console.log(response)
        this.$nextTick(() => {
          this.codePopupVisible = true
        })
      }).catch(error => {
        console.error(error)
      })
    },
    closeCodePopup() {
      this.backendTextData = ''
      this.codePopupVisible = false
    },
    inputFileUpload(CurrentRow) {
      this.selectFormData.fileName = CurrentRow.fileName
      this.selectDialogVisible = true
      this.showFileChooserIcon = false
      this.selectFormData.yid = CurrentRow.yid
    },
    inputFileQuery(CurrentRow) {
      // 输入文件内容展示
      // 这里要写 2023.11.1 17:19  将后端响应的数据显示到前端
      console.log(CurrentRow.yid)
      this.ShowInputFileText = 'xxxx'
      getInputFile(CurrentRow.yid).then(response => {
        this.ShowInputFileText = response.msg
        console.log(response)
        this.$nextTick(() => {
          this.inputPopupVisible = true
        })
      }).catch(error => {
        console.error(error)
      })
    },
    closeInputPopup() {
      this.inputPopupVisible = false
      this.ShowInputFileText = ''
    },
    aflFuzzTest(CurrentRow) {
      // 点击afl测试后，会跳转到fuzz-test.vue，页面上显示编译文件名，输入的文件名，点击测试按钮，执行afl-fuzz，将输出结果保存在一个目录下，随机生成
      // const rowId = CurrentRow.yid
      console.log(this.$router)
      const queryRowId = {yid: CurrentRow.yid.toString()}
      // 跳转页面的方法
      this.$router.push(
        {
          path: "/afl/fuzz",
          query: queryRowId
        })
    },
    fileUpdate(CurrentRow) {
      this.selectDialogVisible = true // 展示弹窗
      this.showFileChooserIcon = true // 展示文件选择的图标
      this.showInputFileChooserIcon = false // 不显示选择输入文件的图标
      this.selectFormData.yid = CurrentRow.yid
      this.selectFormData.fileName = CurrentRow.fileName
      // console.log(CurrentRow)
      // console.log(this.selectFormData.inputFileName)
      // getCompile(CurrentRow.yid).then(response => {
      //   this.nameList.compileName = response.data.compiledName1 // compiledName1 是前面查看编译详情时记录的数据
      //   console.log(this.nameList.compileName)
      // })
      // getFile(CurrentRow.yid).then(response => {
      //   this.nameList.fileName = response.data.fileName // fileName 是前面上传源码文件时记录的数据
      //   console.log(this.nameList.fileName)
      // })
      this.o = CurrentRow.yid
      getInputInfo(CurrentRow.yid).then(response => {
        if (response.data.name122 !== null)
          this.selectFormData.inputFileName = response.data.name122  // name122 来自数据库 我觉得数据都来自数据库更合理
      })
    },
    fileDelete(CurrentRow) {
      const yids = CurrentRow.yid || this.ids
      this.$modal.confirm('确认删除源码文件信息吗？此操作不可逆').then(function () {
        return delFile(yids)
      }).then(() => {
        this.list123()
        this.$modal.msgSuccess('删除成功！')
      }).catch(() => {
      })
    },

  }
}
</script>
<style scoped>
.popup-text-container {
  max-height: 100px;
  overflow-y: auto;
}


</style>
