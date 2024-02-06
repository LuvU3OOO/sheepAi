import axios from 'axios'
import { ElMessage } from 'element-plus'
import { get }  from './index'
import { ChatSession } from '../entity/entity'
import router from "../router";

const defaultError = (err) => {
    console.warn(err)
    ElMessage.error("获取用户对话记录错误,请重新登录")
    router.push('/')
    
}
function getSessions(userid,success,failure=defaultError)
{
    let url = `/api/chat/getSession?userid=${userid}`
    get(url,(data)=>{
         
        success(data)

    },failure)
}

function delSession(sessionId,success)
{
    let url = `/api/chat/deleteSession?sessionId=${sessionId}`
    get(url,(data)=>{
        success(data)
        ElMessage.success('删除会话成功')
      
    })
}

function createSession(userid,success)
{
    let url = `/api/chat/createSession?userid=${userid}`
    get(url,(data)=>{
        success(data)
        // ElMessage.success('创建会话成功')
      
    })
}


function getMessages(userid,sessionId,success,failure=defaultError)
{
    
    let url = `/api/chat/getMessages?sessionId=${sessionId}&userid=${userid}`
    get(url,(data)=>{
        if(sessionId)
            {localStorage.setItem("session", sessionId)}
        success(data)
        // console.log(data)
    },failure)
}

function stopResponsing(userid,failure=defaultError)
{   
    
    let url = `/api/chat/cancel?userid=${userid}`
    get(url,()=>{
      
        console.log("停止响应")
    },failure)
}


export {getSessions,getMessages,delSession,stopResponsing,createSession}