import axios from 'axios'
import { ElMessage } from 'element-plus'
import { get }  from './index'
import { ChatSession } from '../entity/entity'
import router from "../router";

const defaultError = (err) => {
    console.warn(err)
    ElMessage.error("获取用户对话记录错误,请重新登录")
    // router.push('/index')
    
}
function getSessions(userid,success,failure=defaultError)
{
    let url = `/api/chat/getSession?userid=${userid}`
    get(url,(data)=>{
            // const mappedData:ChatSession[] = data.map(item => ({
            //     createdAt: new Date(item.createdAt),
            //     sessionId: item.sessionId,
            //     tokens: item.tokens,
            //     topic: item.topic,
            //     userid: item.userid,
            // }));
        success(data)
        // console.log(data)
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


function getMessages(sessionId,success,failure=defaultError)
{
    let url = `/api/chat/getMessages?sessionId=${sessionId}`
    get(url,(data)=>{
            // const mappedData:ChatSession[] = data.map(item => ({
            //     createdAt: new Date(item.createdAt),
            //     sessionId: item.sessionId,
            //     tokens: item.tokens,
            //     topic: item.topic,
            //     userid: item.userid,
            // }));
        success(data)
        // console.log(data)
    },failure)
}

export {getSessions,getMessages,delSession}