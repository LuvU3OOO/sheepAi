<template>

    <div style="text-align: center;">
        <div style="margin-top: 30px;">
            <el-steps :active="active" finish-status="success"  align-center>
                <el-step title="验证电子邮件"/>
                <el-step title="重新设定密码"/>
            </el-steps>
        </div>
        <div v-if="active === 0" style="margin: 0 20px;">
            <div style="margin-top: 80px;">
                <div style="font-size: 25px;font-weight: bold;">重置密码</div>
                <div style="font-size: 14px;color:gray">请输入需要重置密码的邮箱地址</div>
            </div>
            <div style="margin-top: 50px;">
                <el-form :model="form" :rules="rule" ref="formRef">
                    <el-form-item prop="email">
                        <el-input v-model="form.email" type="email" placeholder="电子邮件地址">
                            <template #prefix>
                                <el-icon><Message/></el-icon>
                            </template>
                        </el-input>
                    </el-form-item>
                    <el-form-item prop="code">
                        <el-row :gutter="10" style="width: 100%;">
                            <el-col :span="17">
                                    <el-input v-model="form.code" maxlength="6" type="text" placeholder="请输入验证码">
                                        <template #prefix>
                                            <el-icon><EditPen/></el-icon>
                                        </template>
                                    </el-input>
                            </el-col>
                            <el-col :span="5">
                                <el-button type="success" plain @click="askCode()" :disabled="coldTime>0 || !isEmailValid"> 
                                    {{coldTime>0? `请稍后${coldTime}秒`:'获取验证码'}}
                                </el-button>
                            </el-col>
                        </el-row>
                    </el-form-item>
                </el-form>
            </div>
            <div style="margin-top: 80px;">
                <el-button type="warning" style="width: 270px;" @click="confirmReset()" plain>开始重置密码</el-button>
            </div>
        </div>
        <div v-if="active === 1" style="margin: 0 20px;">
            <div style="margin-top: 80px;">
                <div style="font-size: 25px;font-weight: bold;">重置密码</div>
                <div style="font-size: 14px;color:gray">请填写你的新密码，务必牢记，防止丢失</div>
            </div>
            <div style="margin-top:50px">
                <el-form :model="form" :rules="rule" ref="formRef">
                    <el-form-item prop="password">
                        <el-input v-model="form.password" maxlength="20" type="password" placeholder="输入密码">
                            <template #prefix>
                                <el-icon><Lock/></el-icon>
                            </template>
                        </el-input>
                    </el-form-item>
                    <el-form-item prop="password_repeat">
                        <el-input v-model="form.password_repeat" maxlength="20" type="password" placeholder="重复输入密码">
                            <template #prefix>
                                <el-icon><Lock/></el-icon>
                            </template>
                        </el-input>
                    </el-form-item>
                </el-form>

            </div>
            <div style="margin-top: 80px;">
                <el-button type="danger" style="width: 270px;" @click="doReset()" plain>立即重置密码</el-button>
            </div>
        </div>

    </div>

</template>

<script setup>
import { User,Lock,Message, EditPen} from '@element-plus/icons-vue';
import { ref, reactive,computed } from 'vue'
import router from "@/router";
import { get,post } from "@/net";
import { ElMessage } from 'element-plus'

const active = ref(0)
const formRef = ref()
const form = reactive({
    email:'',
    code:'',
    password:'',
    password_repeat:''
})
const validatePassword = (rule, value, callback)=>{
    if(value === ''){
        callback(new Error('请再次输入密码'));
    }else if(value !== form.password){
        callback(new Error('两次输入的密码不一致'));
    }else{
        callback();
    }
}
// 定义一个规则对象
const rule = {
    password:[
        {min:6,max:16,message:'密码的长度必须在6-16个字符之间',trigger:['blur','change']}
    ],
    password_repeat:[
        {
            validator:validatePassword,
            trigger: ['blur', 'change']
        }
    ],
    email:[
        {required:true,message:'请输入邮件地址',trigger:['blur','change']},
        {type:'email',message:'请输入合法的电子邮件地址',trigger:['blur','change']}
    ],
    code:[
        {required:true,message:'请输入获取的验证码',trigger:['blur']}
    ]
};
const isEmailValid = computed(()=>/^[\w.-]+@[\w.-]+\.\w+$/.test(form.email))
const coldTime = ref(0);
function askCode(){
    if(isEmailValid)
    {
        coldTime.value=60
        get(`/api/auth/ask-code?email=${form.email}&type=reset`,()=>{
        ElMessage.success(`验证码已发送到邮箱${form.email},请注意查收`)
        setInterval(()=>coldTime.value--,1000) //定时任务每秒执行
    }),(message)=>{
            ElMessage.warning(message)
            coldTime.value=0
    }
    }else{
        ElMessage.warning('请输入正确的电子邮件')
    }
}

function confirmReset() {
    formRef.value.validate((valid)=>{
        if(valid){
            post('/api/auth/reset-confirm',{
                email:form.email,
                code:form.code
            },()=>{active.value++})
        }
    })
}

function doReset(){
    formRef.value.validate((valid)=>{
        if(valid){
            post('/api/auth/reset-password',{...form},()=>{
                ElMessage.success('密码更改成功，请重新登录')
                router.push('/')
            })
        }
    })
}
</script>

<style scoped>

</style>