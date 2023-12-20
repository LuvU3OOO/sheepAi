<template>
    <div class="page">
      <div style="flex: 1;background-color: black;">
        <el-image style="width: 100%;height: 100%;" fit="cover" src="https://p1.itc.cn/images01/20230515/bc33079b8609422589131d12b926ee44.jpeg"/>
        
      </div>
      <div class="title">
        <div style="font-size: 30px;font-weight: bold;">welcome sheep-ai</div>
      </div>
      <div class="right-card">
        <router-view v-slot="{Component}">
          <Transition name="el-fade-in-linear"  mode="out-in">
              <component :is="Component"/>
          </Transition>
        </router-view>
      </div>
     
    </div>
  </template>
  
  <script setup>
  // No script logic for this example
  </script>
  
  <style scoped>
  .page {
    width: 100vw;
    height: 100vh;
    display: flex;
    overflow: hidden;
  }
  .title{
    position: absolute;
    bottom: 30px;
    left:30px;
    color:white;
    text-shadow: 0 0 10px blacks;
  }
  
  .right-card{
    width: 400px;
    z-index: 1;
    background-color: white;
  }
  
  
  </style>