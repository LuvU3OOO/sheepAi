<template>
    <div>
      <el-form>
        <el-form-item label="结果">
          <el-input v-model="result" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="提问">
          <el-input v-model="prompt"></el-input>
        </el-form-item>
        <el-button type="primary" @click="sendMessage">发送</el-button>
      </el-form>
    </div>
  </template>
  
  <script setup>
import { ref, onMounted } from 'vue';
import { accessUserId,get,post } from "@/net";
import SockJS from 'sockjs-client/dist/sockjs.min.js';
import Stomp from 'stompjs'
// const headers = accessHeader();
const result = ref("");
const prompt = ref("");
const isConnected = ref(false);
const userid = accessUserId();
let socket;

const connectWebSocket = () => {
  socket = new WebSocket(`ws://127.0.0.1:8081/ws2?userid=${userid}`);
  
  socket.onopen = () => {
    console.log(`连接成功, userid=${userid}`);
  };

  socket.onmessage = (event) => {
    result.value += event.data
    // 处理从服务器收到的消息，你可以根据需要进行处理
    console.log(`收到消息: ${event.data}`);
  };
  
  // socket.onclose = () => {
  //   console.log('连接已关闭');
  // };

  socket.onerror = (error) => {
    console.error('WebSocket 错误:', error);
  };
};


const sendMessage = () => {

  // 确保 WebSocket 已经连接
  if (socket.readyState === WebSocket.OPEN) {
    // 发送消息给服务器
    post("/chatMessage/send",{
      session_Id: null,
      content: prompt.value,
      role: "user",
      userid:userid
    },()=>{
      console.log(111)},()=>{})
   
  } else {
    console.error('WebSocket 连接尚未打开');
  }

  // 发送后重置数值
  result.value = "";
  prompt.value = "";
};

onMounted(() => {
  if (!isConnected.value) {
    connectWebSocket();
  }
});


</script>

  