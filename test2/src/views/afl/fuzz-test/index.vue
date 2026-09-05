<template>
  <div>
    <el-container ref="elContainer">
      <el-header class="el-header" height=100px>模糊测试
        <el-tooltip content="根据输入测试用例进行模糊测试" placement="top">
          <i class="el-icon-question"></i>
        </el-tooltip>
      </el-header>
      <div class="css1">
        <el-form>
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
              </el-form-item>
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
            >开始生成
            </el-button>
          </el-form-item>
        </el-form>
        <el-card class="box-card" style="width:1500px">
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
export default {
  name: 'MyStudent',
  data() {
    return {
      form: {
        fileID: this.$route.params.id,
        fileName: this.$route.params.name,
        fileFormated: this.$route.params.format,
        ismul: "1",
        activity: false,
        stability: false,
        genNum: "",
        hRate: 0.2,
        minNum: 2
      },
      openMULDia: false,
      value: '',
      dialogVisible: false,
      checked1: true,
    }
  },
  methods: {}
}
</script>

<style>
.el-header {
  color: #2cbb79;
  font-weight: bold;
  font-size: 24px;
  margin-top: 50px;
  margin-left: 20px;
}

.css1 {
  margin-left: 50px;
}
</style>
