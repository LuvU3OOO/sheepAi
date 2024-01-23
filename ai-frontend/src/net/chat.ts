import axios from 'axios'
import { ElMessage } from 'element-plus'
import { get }  from './index'
import { ChatSession } from '../entity/entity'

const defaultError = (err) => {
    console.warn(err)
    ElMessage.error("获取用户对话记录错误,请联系管理员")
}
function getSessions(userid,success,failure=defaultError)
{
    let url = `/ChatMessage/getSession?userid=${userid}`
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
    let url = `/ChatMessage/deleteSession?sessionId=${sessionId}`
    get(url,(data)=>{
        success(data)
        ElMessage.success('删除会话成功')
      
    })
}

export {getSessions,delSession}