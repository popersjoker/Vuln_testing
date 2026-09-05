<template>
  <div class="app-container">
    <div class="css1">
      <el-form :model="nameList">
        <el-form-item label="源码文件" label-width="150px">
          <el-input v-model="nameList.fileName" placeholder="" style="width: 250px"/>
        </el-form-item>
        <el-form-item label="编译文件" label-width="150px">
          <el-input v-model="nameList.compileName" placeholder="" style="width: 250px"/>
        </el-form-item>
        <el-form-item label="输入文件" label-width="150px">
          <el-input v-model="nameList.inputName" placeholder="" style="width: 250px"/>
        </el-form-item>
      </el-form>
    </div>
    <div style="display: flex; align-items: flex-start;flex-direction: column;">
      <div style="float: left; margin-right: 20px;padding-left: 150px; padding-bottom: 20px" id="buttonThree">
        <el-button @click="startAflFuzz">
          测试
        </el-button>
        <el-button @click="stopAflFuzz">
          STOP And Save
        </el-button>
      </div>
      <div style="flex:1;padding-left: 50px">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>破解结果</span>
          </div>
          <div class="scroll-container"
               v-loading="loading1"
               element-loading-text="破解中"
               element-loading-spinner="el-icon-loading"
               element-loading-background="rgba(0, 0, 0, 0.5)">
            <div v-html="this.aflResult" class="scroll-content">
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>
<script>

import {startAfl, getResults, stopAFl, ClearFuzzOut} from "@/api/afl/fuzz";
import {getCompile, getInputInfo} from "@/api/afl/code";
import {getFile} from "@/api/afl/file";
import moment from 'moment';
import {parseTime} from "@/utils/ruoyi";

export default {
  name: "Cracking",
  components: {},
  props: [],

  data() {
    return {
      loading1: false,
      formData: {
        uuid: '',
        result: '',
        yid: '',
        testTime: null,
      },
      aflResult: '',
      timeout: null,
      nameList: {
        fileName: '',
        compileName: '',
        inputName: '',
      },
    }
  },
  computed: {},
  watch: {},
  created() {
    const params = this.$route.query.yid
    console.log('路由参数：', params)
    this.getNameList(params) // 得到参数
  },
  mounted() {
  },
  methods: {
    getNameList(params) {
      // 根据yid查询 file_name，compiled_name，input_file_name  查三个表  table_file，table_compile，table_upload
      // 我应该是写过根据yid查询的方法的，一会看一下  2023-11-3 14：28 （已解决）
      getCompile(params).then(response => {
        this.nameList.compileName = response.data.compiledName1 // compiledName1 是前面查看编译详情时记录的数据
        console.log(this.nameList.compileName)
      })
      getFile(params).then(response => {
        this.nameList.fileName = response.data.fileName // fileName 是前面上传源码文件时记录的数据
        console.log(this.nameList.fileName)
      })
      getInputInfo(params).then(response => {
        this.nameList.inputName = response.data.name122  // name122 来自数据库 我觉得数据都来自数据库更合理
        console.log(this.nameList.inputName)
      })
    },
    startAflFuzz() {       // 测试方法 startAfl: this.loading = false
      let uuidx = this.uuid(8, 10);
      this.formData.uuid = uuidx
      // console.log(this.nameList) // 观察一下nameList中有没有东西
      this.formData.yid = this.$route.query.yid

      this.formData.testTime = parseTime(new Date(),"{y}-{m}-{d} {h}:{i}:{s}")
      console.log(this.formData.testTime)
      // console.log(this.$route.query.yid)
      this.aflResult = ""
      this.timeout = setInterval(this.getCResult, 1000) // 隔一秒触发一次getCResult，每次触发
      this.loading1 = true
      startAfl(this.formData).then(() => {
        this.loading1 = false
      })
    },
    getCResult() {
      getResults().then(response => {
        console.log(response)
        this.aflResult = response.data.result.replace(/\x1B\[[0-9;]*[JKmsu]/g, '');
        this.$nextTick(() => {
          if (!response.data.flag) {
            const container = this.$el.querySelector('.scroll-container')
            container.scrollTop = container.scrollHeight
            console.log(container.scrollHeight)
          } else {
            const container = this.$el.querySelector('.scroll-container')
            container.scrollTop = container.scrollHeight
            console.log(container.scrollHeight)
            clearInterval(this.timeout)
          }
        })
      })
    },
    stopAflFuzz() {
      stopAFl(this.formData.uuid).then(res => {
        this.$message.success('保存成功！')
      }).catch(err=>{
        console.log('保存失败：',err)
        this.$message.error('保存失败！'+err)
      })

    },
    uuid(len, radix) {
      var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('')
      var uuid = [], i
      radix = radix || chars.length

      if (len) {
        // Compact form
        for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random() * radix]
      } else {
        // rfc4122, version 4 form
        var r
        // rfc4122 requires these characters
        uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-'
        uuid[14] = '4'
        // Fill in random data.  At i==19 set the high bits of clock sequence as
        // per rfc4122, sec. 4.1.5
        for (i = 0; i < 36; i++) {
          if (!uuid[i]) {
            r = 0 | Math.random() * 16
            uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r]
          }
        }
      }
      return uuid.join('')
    }, // uuid()
  }, // methods
} // export default

</script>
<style>
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both
}

.box-card {
  width: 1200px;
  height: 600px;
}

.scroll-container {
  height: 500px;
  overflow-y: scroll;
}

.scroll-content {
  height: 500px;
}

.css1 {
  margin-left: 50px;
}
</style>
