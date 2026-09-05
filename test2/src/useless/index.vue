<template>
  <div>
    <el-container ref="elContainer">
      <el-header class="el-header" height=100px>模糊测试
        <el-tooltip content="根据输入测试用例进行模糊测试" placement="top">
          <i class="el-icon-question"></i>
        </el-tooltip></el-header>
      <div class="css1">
        <el-form >
          <el-form-item label="参数1" label-width="150px">
            <el-input v-model="form.genNum" placeholder="请输入" style="width: 120px"/>
          </el-form-item>
          <el-form-item label="参数2" label-width="150px">
            <el-switch
              active-value="1"
              inactive-value="0"
              v-model="form.ismul"></el-switch>
            <span slot="label">
              参数2
              <el-tooltip content="说明" placement="top">
                <i class="el-icon-question"></i>
              </el-tooltip>
            </span>
          </el-form-item>
          <el-form-item label="参数3" label-width="150px">
            <el-input style="width: 120px" v-model="form.hRate" v-bind:disabled="disableCheck"/>
            <span slot="label">
              参数3
              <el-tooltip content="说明" placement="top">
                <i class="el-icon-question"></i>
              </el-tooltip>
            </span>
          </el-form-item>
          <el-form-item label="参数4" label-width="150px">
            <el-input style="width: 120px" v-model="form.minNum" v-bind:disabled="disableCheck"/>
            <span slot="label">
              参数4
              <el-tooltip content="说明" placement="top">
                <i class="el-icon-question"></i>
              </el-tooltip>
            </span>
          </el-form-item>
          <el-row :gutter="1">
            <el-col :span="3">
              <el-form-item label="参数1" label-width="150px">
                <el-checkbox v-model="checked1" disabled></el-checkbox>
              </el-form-item>
            </el-col>
            <el-col :span="3">
              <el-form-item label="参数2" label-width="150px">
                <el-checkbox v-model="checked1" disabled></el-checkbox>
              </el-form-item >
            </el-col>
            <el-col :span="3">
          <el-form-item label="参数3" label-width="150px">
            <el-checkbox v-model="form.activity" v-bind:disabled="disableCheck"></el-checkbox>
          </el-form-item>
            </el-col>
            <el-col :span="3">
          <el-form-item label="参数4" label-width="150px">
            <el-checkbox v-model="form.stability" v-bind:disabled="disableCheck"></el-checkbox>

          </el-form-item>
            </el-col>
            <el-col :span="3">
          <el-form-item label="参数5" label-width="170px">
            <el-checkbox v-model="checked1" disabled></el-checkbox>
          </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label-width="100px">
            <el-button
              type="primary"
              @click="submitForm"
            >开始生成</el-button>
          </el-form-item>
        </el-form>
        <el-card class="box-card" style="width:1500px" >
          <div slot="header" class="clearfix">
        <span style="color: white; background-color: dodgerblue; border-radius: 4px; padding: 5px 10px;">
          <template>
            <span1><i class="el-icon-collection"></i> 当前测试情况</span1>
          </template>
        </span>
<!--            <el-button style="float: right; padding: 3px 0" type="text" icon="el-icon-download" @click="extractIP">一键提取</el-button>-->
          </div>
          <el-scrollbar style="height: 150px;">
            <div v-for="(item, index) in ipaddrs" :key="index" class="text item">
              {{ item }}
            </div>
          </el-scrollbar>
        </el-card>
      </div>

    </el-container>

  </div>
</template>

<script>
  import {generate} from "@/api/addrgen/gen";
  export default{
    name:'MyStudent',
    data(){
      return {
        form: {
          fileID: this.$route.params.id,
          fileName: this.$route.params.name,
          fileFormated:this.$route.params.format,
          ismul: "1",
          activity:false,
          stability:false,
          genNum:"",
          hRate:0.2,
          minNum:2
        },
        openMULDia:false,
        value:'',
        dialogVisible: false,
        checked1: true,
      }
    },

    computed: {
      disableCheck(){
        return this.form.ismul === "0";
      }
    },

    methods: {
      submitForm(){
        if (this.form.hRate < 0 || this.form.hRate > 1) {
          this.$message.error('操作失败，无意义的 分类粒度阈值');
          return;
        }
        if (this.form.fileFormated === 'el-icon-check'){
          let loadingInstance = this.$loading({
            text: '正在生成IPv6地址，请稍后...',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
          });

          this.$modal.confirm('确认要生成"' + this.form.genNum + '"条IPv6地址吗？"').then(() => {
            return generate(this.form).then((res) => {
              console.log(res.data)
              if (res.data === 1) {
                // generate()成功解决(resolve)时执行的代码
                this.$modal.msgSuccess("操作成功，结果显示在地址扩展记录中",5000);
              } else if (res.data === -1) {
                // generate()执行失败时执行的代码
                this.$modal.msgError("操作失败,种子文件不能有重复地址",5000);
              }
            }).finally(() => {
              // 无论成功或失败，都会执行的代码
              loadingInstance.close();
            });
          }).catch(() => {
            // 用户取消确认时执行的代码
            this.$modal.msg("操作已取消");
            loadingInstance.close();
          });
        }else {
          this.$modal.msgWarning("请至少先将种子文件完成格式化");
        }
      },
    }
  }
</script>

<style>
  .el-header{
    color: #2cbb79;
    font-weight: bold;
    font-size: 24px;
    margin-top: 50px;
    margin-left: 20px;
  }
  .css1{
    margin-left: 50px;
  }

</style>
