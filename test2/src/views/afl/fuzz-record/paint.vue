<template>
  <div>
    <el-form
      :model="paintList"
      style="font-family: 'Trebuchet MS', 'Tahoma', 'Arial', 'Helvetica'"
      label-width="80px"
    >
      <el-form-item label="Banner:" label-width="150px" label-suffix=" ">
        {{ paintList.banner }}
      </el-form-item>
      <el-form-item label="Directory:" label-width="150px" label-suffix=" ">
        {{ paintList.directory }}
      </el-form-item>
      <el-form-item label="Generated on:" label-width="150px" label-suffix=" ">
        {{ paintList.generatedTime }}
      </el-form-item>
    </el-form>

    <div class="block">
      <el-image style="width: 1000px; height: 300px" :src="url1"></el-image>
    </div>
    <div class="block">
      <el-image style="width: 1000px; height: 200px" :src="url2"></el-image>
    </div>
    <div class="block">
      <el-image style="width: 1000px; height: 200px" :src="url3"></el-image>
    </div>
  </div>
</template>

<script>
import { getRecord } from "@/api/afl/record";
import { getCompileInfo } from "@/api/afl/code";

export default {
  data() {
    return {
      paintList: {
        banner: "",
        directory: "",
        generatedTime: "",
      },
      url1: "http://localhost:8080/ruoyi/Out-HTML/high_freq.png?t=", // 63342是idea内置的web服务器的http端口号，开启springboot后可以看到这个url下的图片
      url2: "http://localhost:8080/ruoyi/Out-HTML/low_freq.png?t=",
      url3: "http://localhost:8080/ruoyi/Out-HTML/exec_speed.png?t=",
    };
  },
  created() {
    const fid = this.$route.query.fid; // 23
    const currentTime = this.$route.query.currentTime; //  Mon Nov 13 2023 21:21:07 GMT+0800 (中国标准时间)
    console.log("路由参数：", fid, currentTime);
    this.url1 = this.url1 + this.getTime();
    this.url2 = this.url2 + this.getTime();
    this.url3 = this.url3 + this.getTime();
    this.getCompileFuzzOutTestTime(fid); // 得到参数
  },
  methods: {
    getCompileFuzzOutTestTime(params) {
      console.log(params);
      // 显示编译文件名，输出文件生成目录，当前时间  fid => cid,fuzz_out_url
      getRecord(this.$route.query.fid).then((res) => {
        this.paintList.directory = res.data.fuzzOutUrl;
        let cid = res.data.cid;
        getCompileInfo(cid).then((resp) => {
          this.paintList.banner = resp.data.compiledName;
        });
      });
      this.paintList.generatedTime = this.$route.query.currentTime;
    },
     getTime() {
    return Date.now();
  }
  },
 
};
</script>

<style scoped></style>
