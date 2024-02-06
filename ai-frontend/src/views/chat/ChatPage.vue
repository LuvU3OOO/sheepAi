<template>
    <!-- 最外层页面于窗口同宽，使聊天面板居中 -->
    <div class="home-view">
        <!-- 整个聊天面板 -->
        <div class="chat-panel">
            <!-- 左侧的会话列表 -->
            <div class="session-panel">
                <!-- <div class="title">ChatGPT助手</div>
                <div class="description">构建你的AI助手</div> -->
                <div class="session-header">
                    <div>
                        <div style="margin-bottom: 5px;"><span
                                style="font-size: 34px;font-weight:bolder;">&nbsp;青羊AI🌱</span></div>
                        <div style="margin: 0%;"><el-button class="new-chat" round @click="handleCreateSession"
                                :icon="CirclePlusFilled" size="large">&nbsp;新&nbsp;对&nbsp;话</el-button></div>

                    </div>
                </div>
                <div class="session-content">
                    <el-scrollbar>
                        <SessionItem v-for="(session, index) in sessionList" :key="session.sessionId"
                            :active="session.sessionId === activeSession.sessionId" :session="sessionList[index]"
                            class="session" @click="handleSessionSwitch(session)" @delete="handleDeleteSession">
                        </SessionItem>
                    </el-scrollbar>
                </div>
                <div class="session-footer">
                    <!-- 
                    <el-popover placement="top" trigger="click" :width="200" disabled="true">
                        <template #reference>
                            <el-button class="user-button" plain>
                                <el-icon size="large">
                                    <Setting />
                                </el-icon>
                                <span style="font-size: 28px; font-weight: 600; margin-left: 15px;">设置</span>
                            </el-button>
                        </template>
                    </el-popover> -->
                    <el-popover placement="top" trigger="click" :width="200">
                        <template #reference>

                            <el-button class="user-button" plain>
                                <el-avatar style="margin-left: 0px; margin-right: 10px;"
                                    src="https://img0.baidu.com/it/u=1417527418,1094663789&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500"></el-avatar>
                                <span style="font-size: 28px; font-weight: 600; margin-left: 15px;">{{ username }}</span>
                            </el-button>

                        </template>

                        <el-button @click="userLogout" class="logout-button">退出登录</el-button>


                    </el-popover>






                </div>
            </div>
            <!-- 右侧的消息记录 -->
            <div class="message-panel">
                <div class="model_select">
                    <el-select v-model="selectModel" placeholder="Select" size="large"
                        style="width: 240px;margin-top: 5px;margin-left: 5px;">
                        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"
                            :disabled="item.disabled" style="font-size: larger;font-weight: 400;" />
                    </el-select>
                </div>
                <div class="message-list" ref="scrollbarRef">
                    <transition-group name="list">

                        <MessageRow v-for="(message, index) in messageList" :message="message"
                            :key="formatDate(message.createdAt)"></MessageRow>

                    </transition-group>
                </div>
                <div class="message-send">
                    <!-- 监听发送事件 -->
                    <message-input @send="handleSendMessage" :isResponsing="isResponsing"></message-input>
                </div>

            </div>

        </div>
    </div>
</template>

<script lang='ts' setup>
import { onMounted, ref, nextTick, Ref } from 'vue';
import { get, post, accessUserId, accessUserName, logout } from '../../net/index.js'
import { getSessions } from '../../net/chat'
import SessionItem from './SessionItem.vue'
import MessageRow from './MessageRow.vue';
import MessageInput from './MessageInput.vue';
import { ChatMessage, ChatSession } from '../../entity/entity';
import { getMessages, createSession } from '../../net/chat';
import { ElScrollbar } from 'element-plus';
import { CirclePlusFilled, Setting } from '@element-plus/icons-vue'
import dayjs from 'dayjs';
import router from '../../router';


const sessionList = ref([] as ChatSession[]);
const messageList = ref([] as ChatMessage[])
// ChatGPT的回复
const responseMessage = ref({} as ChatMessage);
const isEdit = ref(false);
const activeSession = ref({} as ChatSession);
const username = accessUserName()
const userid = accessUserId()
const scrollbarRef = ref<HTMLElement | null>(null);
const isConnected = ref(false);
const isResponsing = ref(false);
const selectModel = ref('GPT-3.5')
const options = [
    {
        value: 'GPT-3.5',
        label: 'GPT-3.5',
    },
    {
        value: 'GPT-4',
        label: 'GPT-4',
        disabled: true
    }
]
onMounted(() => {

    getSessions(userid, (data) => {
        sessionList.value = data
        console.log(sessionList.value)
        if (sessionList.value.length > 0) {
            const sid = localStorage.getItem("session")
            if (!sid || sid === 'tmpsessionId') {
                handleSessionSwitch(sessionList.value[0])
            } else {
                const idx = sessionList.value.findIndex((value) => {
                    return value.sessionId === sid;
                });
                handleSessionSwitch(sessionList.value[idx])
            }
        } else if (sessionList.value.length == 0) {
            // 创建一个带初值的ChatSession对象
            const initialChatSession: ChatSession = {
                createdAt: new Date(),
                sessionId: '',
                tokens: 0,
                topic: "新的对话",
                userid: userid,
            };
            sessionList.value.push(initialChatSession);
            handleSessionSwitch(sessionList.value[0])
        }
    })

    if (!isConnected.value) {
        connectWebSocket();
        isConnected.value = true
    }
    // console.log(sessionList.value.length)
    // 默认选中的聊天会话是第一个

});

function scrollToBottom() {
    const element = scrollbarRef.value;
    nextTick(() => {

        if (element) {
            element.scrollTop = element.scrollHeight;
        }

    })

}

// 切换会话
const handleSessionSwitch = (session: ChatSession) => {
  
        getMessages(userid, session.sessionId, (data) => {
        messageList.value = data
        scrollToBottom()
    })
    activeSession.value = session

};
// 从会话列表中删除会话
const handleDeleteSession = (session: ChatSession) => {
    const index = sessionList.value.findIndex((value) => {
        return value.sessionId === session.sessionId;
    });
    sessionList.value.splice(index, 1);
    if(sessionList.value.length>0)
    {
        handleSessionSwitch(sessionList.value[0])
    }else{
        handleCreateSession()
    }
    
};
// 新增会话
const handleCreateSession = () => {

    if (sessionList.value[0].topic != "新对话") {
        createSession(userid, (data) => {
            // 创建一个带初值的ChatSession对象
            const initialChatSession: ChatSession = {
                createdAt: new Date(),
                sessionId: data,
                tokens: 0,
                topic: "新对话",
                userid: userid,
            };
            sessionList.value.unshift(initialChatSession);
            handleSessionSwitch(sessionList.value[0])
        })

    } else {
        handleSessionSwitch(sessionList.value[0])
    }

};

function formatDate(date) {
    return dayjs(date).format("YYYY-MM-DD HH:mm:ss")
}


let socket;

const connectWebSocket = () => {
    socket = new WebSocket(`ws://127.0.0.1:8081/ws2?userid=${userid}`);

    socket.onopen = () => {
        console.log(`连接成功, userid=${userid}`);
    };

    socket.onmessage = (event) => {
        responseMessage.value.content += event.data
        // 处理从服务器收到的消息，你可以根据需要进行处理
        // console.log(`收到消息: ${event.data}`);
        scrollToBottom()
        isResponsing.value = true
    };

    socket.onclose = () => {
        console.log('连接已关闭');
        isConnected.value = false
    };

    socket.onerror = (error) => {
        console.error('WebSocket 错误:', error);
    };
};

function send(requestMessage, message) {
    post("/api/chat/send", requestMessage, () => {

        if (activeSession.value.sessionId == '') {
            activeSession.value.sessionId = 'tmpsessionId'
            activeSession.value.topic = message.substring(0, 20)
        }
        isResponsing.value = false

    }, () => { })

}

function send2(requestMessage, message) {
    post("/api/chat/send", requestMessage, () => {

        if (activeSession.value.sessionId == '') {
            activeSession.value.sessionId = 'tmpsessionId'
            activeSession.value.topic = message.substring(0, 20)
        }
        isResponsing.value = false

    }, () => { })

}
const handleSendMessage = (message: string, iscontinuous: boolean) => {


    // console.log("message:",message

    const questionMessage = {
        role: "user",
        content: message,
        createdAt: new Date(),
    } as ChatMessage;

    messageList.value.push(questionMessage)
    scrollToBottom()
    // 新建一个ChatGPT回复对象，不能重复使用同一个对象。
    responseMessage.value = {
        role: "assistant",
        content: "",
        // 因为回复的消息没有id，所以统一将创建时间+index当作key
        createdAt: new Date,
    } as ChatMessage;

    const requestMessage = {
        session_Id: activeSession.value.sessionId === '' ? null : activeSession.value.sessionId,
        content: message,
        role: "user",
        userid: userid,
        isContinuous: iscontinuous
    }
    console.log(activeSession.value.sessionId)
    // 确保 WebSocket 已经连接
    if (socket.readyState === WebSocket.OPEN) {
        // 发送消息给服务器
        send(requestMessage, message)

    } else {
        connectWebSocket();
        send(requestMessage, message)
    }
    messageList.value.push(responseMessage.value)
}

function userLogout() {
    logout(() => {
        router.push('/')
    })
}
</script>
<style lang="scss" scoped>
.home-view {
    display: flex;
    /* 与窗口同宽 */
    width: 100vw;
    height: 98vh;
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
        margin-top: 50px;

        /* 左侧聊天会话面板*/
        .session-panel {
            background-color: rgb(231, 248, 255);
            width: 15vw;
            //   border-top-left-radius: 20px;
            //   border-bottom-left-radius: 20px;
            //   padding: 20px;
            position: relative;
            border-right: 1px solid rgba(black, 0.07);
        }

        /* 右侧消息记录面板*/
        .message-panel {
            width: 80vw;
            height: 80vh;

            .message-list {
                height: 70vh;
                padding: 15px;
                // 消息条数太多时，溢出部分滚动
                overflow-y: scroll;

                // 当切换聊天会话时，消息记录也随之切换的过渡效果
                .list-enter-active,
                .list-leave-active {
                    transition: all 0.5s ease;
                }

                .list-enter-from,
                .list-leave-to {
                    opacity: 0;
                    // transform: translateX(30px);
                }
            }
        }
    }
}

.model_select {
    height: 50px;
    border-bottom: 1px solid rgba(black, 0.07);
    // border: solid black;
}

.message-send {
    // height: 100px;
    position: relative;
}

.session-header {
    flex-direction: column;
    /* 设置主轴方向为垂直方向 */
    justify-content: center;
    /* 在主轴上居中 */
    align-items: center;
    /* 在交叉轴上居中 */
    background-color: rgb(231, 248, 255);
    margin-left: 20px;
    height: 110px;
    margin-bottom: 0%;
    padding: 0%;

}

.session-content {
    display: flex;
    justify-content: center;
    /* 在主轴上居中 */
    align-items: center;
    margin-top: 0%;
    height: 66vh;
}

.session-footer {
    display: flex;
    flex-direction: column;
    /* 设置主轴方向为垂直方向 */
    justify-content: center;
    /* 在主轴上居中 */
    align-items: center;
    border-top: 1px solid rgba(black, 0.07);
    height: 20vh;
    margin-top: 18px;
    // border: solid black;
}

.new-chat {
    width: 160px;
    justify-content: left;
    /* 在主轴上居中 */
    align-items: center;
    /* 在交叉轴上居中 */
    font-size: 18px;
    margin-left: 0%;
    padding: 10px;
}

.user-button {
    width: 12vw;
    display: flex;
    justify-content: flex-start;
    background-color: rgb(231, 248, 255);
    height: 50px;
    padding: 10px;
    margin-top: 8px;
}

.logout-button {
    width: 100%;
    background-color: rgb(231, 248, 255);
}
</style>
  



