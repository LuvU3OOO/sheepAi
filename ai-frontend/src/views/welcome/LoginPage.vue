<template>
  <div class="login-page">
    <div style="margin-top: 150px;">
      <h1>登录</h1>
      <span>请先登录</span>

    </div>
    <div style="margin-top: 50px;">
      <el-form :model="form" :rules="rule" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="20" type="text" placeholder="用户名/邮箱">
            <template #prefix>
              <el-icon><User/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" maxlength="10" type="password" placeholder="密码">
            <template #prefix>
              <el-icon><Lock/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-row>
          <el-col :span="12"  style="text-align: left;">
            <el-form-item prop="remermber">
              <el-checkbox label="记住我" v-model="form.remember"></el-checkbox>
            </el-form-item> 
          </el-col>

          <el-col :span="12" style="text-align: right;">
            <el-link @click="router.push('/reset')">忘记密码?</el-link>
          </el-col>
        </el-row>
      </el-form>
      <div style="margin-top: 40px;">
           <el-button type="success" style="width: 270px;" plain @click="userLogin">立即登录</el-button>
      </div>
      <el-divider>
          <span style="font-size: 13px; color: grey;">没有账号</span>
      </el-divider>
      <div>
           <el-button @click= "router.push('/register')" style="width: 270px;" type="warning" plain>立即注册</el-button>
      </div>

    </div>
  </div>
</template>

<script setup>
  import {reactive,ref}  from "vue"
  import {User,Lock} from '@element-plus/icons-vue'
  import {login} from '@/net'
  import router from "@/router";
  const form=reactive({
    username:'',
    password:'',
    remember:false 
   })
  const formRef = ref()
  const rule = {
    username:[
      {required:true,message:"请输入用户名"}
    ],
    password:[
      {required:true,message:"请输入密码"}
    ]
  }

  function userLogin(){
    formRef.value.validate((valid)=>{
      if(valid) {
        login(form.username,form.password,form.remember,()=>{router.push('/index')})
      }
    })
  }
</script>

<style scoped>
.login-page {
  text-align: center;
  margin: 0 20px;
 
}

</style>