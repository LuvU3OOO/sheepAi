<template>
    <!-- 最外层页面于窗口同宽，使聊天面板居中 -->
    <div class="home-view">
        <!-- 整个聊天面板 -->
        <div class="chat-panel">
            <!-- 左侧的会话列表 -->
            <div class="session-panel">
                <!-- <div class="title">ChatGPT助手</div>
                <div class="description">构建你的AI助手</div> -->
                <div style="margin:0">
                    <el-card class="box-card">
                        <template #header>
                            <div class="card-header">
                                <span>Card name</span>
                                <el-button class="button" @click="handleCreateSession" text>新对话</el-button>
                            </div>
                        </template>
                        <div>
                            <!-- for循环遍历会话列表用会话组件显示，并监听点击事件和删除事件。点击时切换到被点击的会话，删除时从会话列表中提出被删除的会话。 -->

                            <SessionItem
                            v-for="(session, index) in sessionList"
                            :key="session.sessionId"
                            :active="session.sessionId === activeSession.sessionId"
                            :session="sessionList[index]"
                            class="session"
                            @click="handleSessionSwitch(session)"
                            @delete="handleDeleteSession"
                        ></SessionItem>
                        </div>
                        <!-- <template #footer>Footer content</template> -->
                    </el-card>
                </div>
            </div>
            <!-- 右侧的消息记录 -->
            <div class="message-panel">
                <MessageRow 
                v-for="(message,index) in messageList"
                :message="message"
                ></MessageRow>
            </div>
        </div>
    </div>
</template>

<script lang='ts' setup>
import { onMounted, ref } from 'vue';
import { get, post, accessUserId } from '../../net/index.js'
import { getSessions } from '../../net/chat'
import SessionItem from './SessionItem.vue'
import MessageRow from './MessageRow.vue';
import { ChatMessage, ChatSession } from '../../entity/entity';
import { getMessages } from '../../net/chat';
const sessionList = ref([] as ChatSession[]);
const messageList = ref([] as ChatMessage[])
const isEdit = ref(false);
const activeSession = ref(({ messages: [] } as unknown) as ChatSession);
const userid = accessUserId()
onMounted(() => {

    getSessions(userid, (data) => {
        sessionList.value = data
    })
    // 默认选中的聊天会话是第一个
    if (sessionList.value.length > 0) {
        activeSession.value = sessionList.value[0];

    }
});
// 切换会话
const handleSessionSwitch = (session: ChatSession) => {
    getMessages(session.sessionId,(data)=>{
        messageList.value = data
    })
    activeSession.value = session;
};
// 从会话列表中删除会话
const handleDeleteSession = (session: ChatSession) => {
    const index = sessionList.value.findIndex((value) => {
        return value.sessionId === session.sessionId;
    });
    sessionList.value.splice(index, 1);
};
// 新增会话
const handleCreateSession = async () => {
    // 创建一个带初值的ChatSession对象
    const initialChatSession: ChatSession = {
        createdAt: new Date(),
        sessionId: undefined,
        tokens: 0,
        topic: "新的对话",
        userid: userid,
    };

    sessionList.value.push(initialChatSession);
};


</script>
<style lang="scss" scoped>
.home-view {
    display: flex;
    /* 与窗口同宽 */
    width: 100vw;
    height: 95vh;
    /* 水平方向上剧中 */
    justify-content: center;

    .chat-panel {
        /* 聊天面板flex布局，让会话列表和聊天记录左右展示 */
        display: flex;
        /* 让聊天面板圆润一些 */
        border-radius: 20px;
        background-color: white;
        /* 给一些阴影 */
        box-shadow: 0 0 20px 20px rgba(black, 0.05);
        /* 与上方增加一些间距 */
        margin-top: 70px;

        /* 左侧聊天会话面板*/
        .session-panel {
            background-color: rgb(231, 248, 255);
            width: 300px;
            //   border-top-left-radius: 20px;
            //   border-bottom-left-radius: 20px;
            //   padding: 20px;
            position: relative;
            border-right: 1px solid rgba(black, 0.07);

            /* 标题*/
            .title {
                margin-top: 20px;
                font-size: 20px;
            }

            /* 描述*/
            .description {
                color: rgba(black, 0.7);
                font-size: 10px;
                margin-top: 10px;
            }
        }

        /* 右侧消息记录面板*/
        .message-panel {
            width: 80vw;
            height: 80vh;
        }
    }
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: rgb(231, 248, 255);
}

.text {
    font-size: 14px;
}

.item {
    margin-bottom: 18px;
}

.box-card {
    width: 100;
    justify-content: center;
    background-color: rgb(231, 248, 255);
}
</style>
  



