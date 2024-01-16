<template>

    <div>
        <el-button @click="userLogout()">退出登录</el-button>
    </div>

</template>

<script setup>
import { ref, reactive } from 'vue'
import {logout}  from '@/net'
import router from '@/router';
const global = window; // fix global is undefined in socketjs-client
function userLogout(){
   logout(()=>router.push('/'))
}
</script>

<style scoped>

</style>