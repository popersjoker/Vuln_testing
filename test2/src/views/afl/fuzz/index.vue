<template>
  <div class="app-container">
    <div style="float: left">
      <el-button
        @click="startAfluzz"
      >
        测试
      </el-button>
      <el-button
        @click="stopAflFuzz"
      >
        STOP
      </el-button>

      <el-button
        @click="deleteFuzzOut"
      >
        清理
      </el-button>
    </div>
    <div style="float: left">
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span>破解结果</span>
          <!--<el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button>-->
        </div>
        <div class="scroll-container"
             v-loading="loading"
             element-loading-text="破解中"
             element-loading-spinner="el-icon-loading"
             element-loading-background="rgba(0, 0, 0, 0.5)">
          <div v-html="this.aflResult" class="scroll-content">
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>
<script>
import axios from "axios";

;
import {startAfl, getResults, stopAFl, ClearFuzzOut} from "@/api/afl/fuzz";
import {getToken} from "@/utils/auth";

export default {
  name: "Cracking",
  components: {},
  props: [],
  data() {
    return {
      formData: {
        uuid: '',
        result: '',
      },
      aflResult: "",
      timeout: null,
    }

  },
  computed: {},
  watch: {},
  created() {

  },
  mounted() {
  },
  methods: {
    startAfluzz() {
      var uuidx = this.uuid(8, 10);
      this.formData.uuid = uuidx;
      this.aflResult = "";
      this.timeout = setInterval(this.getCResult, 1000);
      this.loading = true;
      startAfl(this.formData).then(response => {
        this.loading = false;
      });
    },
      async deleteFuzzOut() {
        try {
          await ClearFuzzOut(); // 调用ClearFuzzOut函数发送HTTP请求
          // 请求成功的处理逻辑，可以根据需要进行操作
          console.log('请求成功');
        } catch (error) {
          // 请求失败的处理逻辑，可以根据需要进行操作
          console.error('请求失败', error);
      }
    },

    getCResult() {
      getResults().then(response => {
        console.log(response);
        this.aflResult = response.data.result;
        this.$nextTick(() => {
          if (!response.data.flag) {
            const container = this.$el.querySelector('.scroll-container');
            container.scrollTop = container.scrollHeight;
            console.log(container.scrollHeight);
          } else {
            const container = this.$el.querySelector('.scroll-container');
            container.scrollTop = container.scrollHeight;
            console.log(container.scrollHeight);
            clearInterval(this.timeout);
          }
        });
      });
    },
    stopAflFuzz() {
      stopAFl(this.formData.uuid);
    },
    uuid(len, radix) {
      var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
      var uuid = [], i;
      radix = radix || chars.length;

      if (len) {
        // Compact form
        for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random() * radix];
      } else {
        // rfc4122, version 4 form
        var r;
        // rfc4122 requires these characters
        uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
        uuid[14] = '4';
        // Fill in random data.  At i==19 set the high bits of clock sequence as
        // per rfc4122, sec. 4.1.5
        for (i = 0; i < 36; i++) {
          if (!uuid[i]) {
            r = 0 | Math.random() * 16;
            uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
          }
        }
      }
      return uuid.join('');
    },
  }
}

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
  width: 1000px;
  height: 600px;
}

.scroll-container {
  height: 500px;
  overflow-y: scroll;
}

.scroll-content {
  height: 500px;
}
</style>
