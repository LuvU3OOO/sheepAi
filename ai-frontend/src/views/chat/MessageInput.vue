
<script lang="ts" setup>
import { ref } from "vue";
import { Top,Close } from '@element-plus/icons-vue'
import  {stopResponsing } from '../../net/chat'
import { accessUserId } from "../../net";
const iscontinuous = ref(true)
const prop = defineProps<{ isResponsing:boolean; }>();
// const props = defineProps({
//   iscontinuous:iscontinuous;
// });
// 发送消息消息事件
const emit = defineEmits<{
  send: [message: string, iscontinous: boolean];
}>();


// 输入框内的消息
const message = ref("");

const sendMessage = (event) => {

  if (!event.shiftKey && message.value) {
    // 阻止默认 Enter 键行为
    event.preventDefault();
    // 用户的提问
    emit("send", message.value, iscontinuous.value);
    console.log("input",prop.isResponsing)
    // 发送完清除
    message.value="";
  }

};

const userid = accessUserId()
const cancelResponsing =()=>{

  stopResponsing(userid)
}
</script>

<template>
  <div class="container">
    <div class="swbox">
      <el-switch v-model="iscontinuous" class="switch" inline-prompt style="--el-switch-on-color: #13ce66;"
        active-text="连续对话" inactive-text="单次对话" size="large" />
    </div>
    <div class="message-input">
      <div class="input-wrapper">
        <!-- 按回车键发送，输入框高度三行 -->
        <el-input v-model="message" :autosize="{ minRows: 2, maxRows: 4 }" type="textarea" resize="none"  
          @keydown.enter="sendMessage" placeholder="Please input" />
      </div>
      <div class="button-wrapper" v-if="!prop.isResponsing">
        <el-tooltip class="box-item" effect="dark" content="Send Mseeage" placement="top-start">
          <el-button tag="div" round="true" size="large" :disabled="message === ''"
            :class="['send-button', message === '' ? 'kong' : 'notkong']" @click="sendMessage">

            <Top style="width: 2em; height: 8em; "/>

          </el-button>
        </el-tooltip>
      </div>
      <div class="button-wrapper" v-if="prop.isResponsing">
        <el-tooltip class="box-item" effect="dark" content="Stop" placement="top-start">
          <el-button tag="div" round="true" size="large" class="cancel-button"
            @click="cancelResponsing">
            <Close style="width: 2em; height: 8em;"/>
          </el-button>
        </el-tooltip>
      </div>

    </div>
  </div>
</template>

<style lang="scss" scoped>
.container {
  padding: 0%;
  height: 100px;
  border-top-left-radius: 5px;
  display: flex;
  flex-direction: column;
  /* 设置主轴方向为垂直方向 */
  justify-content: center;
  /* 在主轴上居中 */
  align-items: center;
  /* 在交叉轴上居中 */
  // border: solid brown;
  /* 给一些阴影 */
  position: absolute;
  width: 80vw;
  border-top: 1px solid rgba(black, 0.07);
}

.message-input {
  display: flex;
  // border:solid black;
  width: 60vw;
  margin-top: 5px;
}

.input-wrapper {
  flex: 8;
  height: 60px;
  /* 占据剩余宽度的 9 部分 */

}

.button-wrapper {
  // margin-top: 10px;
  margin-left: 10px;
  align-items: center;
  flex: 2;
  /* 占据剩余宽度的 9 部分 */

}

.send-button {
  height: 52px;

  &.notkong {
    background-color: rgb(231, 248, 255);
  }

}

.cancel-button{
  height: 52px;
  background-color: rgb(240, 217, 42);
}

.swbox {
  width: 60vw;
  // border:solid black;
  height: 30px;
  margin-bottom: 10px;

}

.switch {
  margin-top: 10px;
}
</style>

