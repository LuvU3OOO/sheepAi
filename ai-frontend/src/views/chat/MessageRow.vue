
<script lang="ts" setup>
  import {PropType} from "vue";
  import {ChatMessage} from "../../entity/entity";
  import TextLoading from "./TextLoading.vue"
  // message：接受消息对象，展示消息内容和头像，并且根据角色调整消息位置。
  // avatar：用户头像，如果角色是 Assistant则使用 logo。
  const props = defineProps({
    message: {type: Object as PropType<ChatMessage>, required: true},
    avatar: {type: String, default: "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsafe-img.xhscdn.com%2Fbw1%2F03360f8c-9b2f-4534-b2f3-320a405b9ed8%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fsafe-img.xhscdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1709280792&t=5d22ba60745c41f9cd9f2464a3cf8d40"},
  });
  const logo = "https://sf1-cdn-tos.bdxiguastatic.com/img/mosaic-legacy/2e2f10008b90ada01b8bf~300x300.image"
</script>

<!-- 整个div是用来调整内部消息的位置，每条消息占的空间都是一整行，然后根据right还是left来调整内部的消息是靠右边还是靠左边 -->
<template>
  <div :class="['message-row', message.role === 'user' ? 'right' : 'left']">
    <!-- 消息展示，分为上下，上面是头像，下面是消息 -->
    <div class="row">
      <!-- 头像， -->
      <div class="avatar-wrapper">
        <el-avatar
            v-if="message.role === 'user'"
            :src="avatar"
            class="avatar"
            shape="square"
        />
        <el-avatar v-else-if="message.role==='assistant'" :src="logo" class="avatar" shape="square"/>
      </div>
      <!-- 发送的消息或者回复的消息 -->
      <div class="message" v-if="message.role==='user'|| message.role==='assistant'">
        <!-- 预览模式，用来展示markdown格式的消息 -->
        <MdPreview
            :id="'preview-only'"
            :preview-theme="'smart-blue'"
            :model-value="message.content"
            :style="{
            backgroundColor:
              message.role === 'user' ? 'rgb(231, 248, 255)' : '#f4f4f5',
          }"
            v-if="message.content"
        ></MdPreview>
        <!-- 如果消息的内容为空则显示加载动画 -->
        <TextLoading v-else></TextLoading>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
  .message-row {
    display: flex;

    &.right {

      // 消息显示在右侧
      justify-content: flex-end;

      .row {
        // 头像也要靠右侧
        .avatar-wrapper {
          display: flex;
          justify-content: flex-end;
          margin-right: 15px;

        }

        // 用户回复的消息和ChatGPT回复的消息背景颜色做区分
        .message {
          margin-right: 45px;
          background-color: rgb(231, 248, 255);
        }
      }
    }

    // 默认靠左边显示
    .row {
      .avatar-wrapper {
        .avatar {
          box-shadow: 20px 20px 20px 3px rgba(0, 0, 0, 0.03);
          margin-bottom: 20px;
          margin-left: 15px;
        }
      }

      .message {
        font-size: 15px;
        padding: 1.5px;
        // 限制消息展示的最大宽度
        max-width: 700px;
        // 圆润一点
        border-radius: 7px;
        // 给消息框加一些描边，看起来更加实一些，要不然太扁了轻飘飘的。
        border: 1px solid rgba(black, 0.1);
        // 增加一些阴影看起来更加立体
        box-shadow: 20px 20px 20px 1px rgba(0, 0, 0, 0.01);
        margin-left: 45px;
      }
    }
  }

  // 调整markdown组件的一些样式，deep可以修改组件内的样式，正常情况是scoped只能修改本组件的样式。
  :deep(.md-editor-preview-wrapper) {
    padding: 0 10px;

    .smart-blue-theme p {
      line-height: unset;
    }
  }
</style>
