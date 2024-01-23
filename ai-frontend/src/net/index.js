import axios from 'axios'
import { ElMessage } from 'element-plus'

const authItemName = "access_token"
const defaultFailure = (message,code,url) => {
    console.warn(`请求地址:${url},状态码:${code},错误信息:${message}`)
    ElMessage.warning(message)
}
const defaultError = (err) => {
    console.warn(err)
    ElMessage.error("发生了一些错误,请联系管理员")
}
function takeAccessToken(){
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName)
    //变量 str 是否为 falsy。在 JavaScript 中，falsy 值包括 null、undefined、空字符串 ''、数字 0、NaN 和 false。
    if(!str) return null;
    const authObj = JSON.parse(str)
    if(authObj.expire <= new Date()){
        deleAccessToken()
        ElMessage.warning('登录已过期，请重新登录')
        return null;
    }
    
    return authObj.token
    
}
function accessUserId(){
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName)
    //变量 str 是否为 falsy。在 JavaScript 中，falsy 值包括 null、undefined、空字符串 ''、数字 0、NaN 和 false。
    if(!str) return null;
    const authObj = JSON.parse(str)
    if(authObj.expire <= new Date()){
        deleAccessToken()
        ElMessage.warning('登录已过期，请重新登录')
        return null;
    }
    
    return authObj.userid
}
function storeAccessToken(remember, token,userid, expire){
    const authObj = {
        token: token,
        userid:userid,
        expire: expire
    }
    const str = JSON.stringify(authObj)
    if(remember)
        localStorage.setItem(authItemName, str)
    else
        sessionStorage.setItem(authItemName, str)
}
function deleAccessToken(){
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)

}
function accessHeader(){
    const token = takeAccessToken()
  
    return token ? {
        'Authorization':`Bearer ${takeAccessToken()}`
    }:{}
}
function internalPost(url,data,header,success,failure,error=defaultError){
    axios.post(url,data,{headers:header}).then(response => {
        const data = response.data
        if(data.code === 200){
            success(data.data)
        }else{
            failure(data.message,data.code,url)
        }

    }).catch(err => error(err))
}

function internalGet(url,header,success,failure,error=defaultError){
    axios.get(url,{headers:header}).then(({data}) => {
    
        if(data.code ===200){
            success(data.data)
        }else{
            failure(data.message,data.code,url)
        }

    }).catch(err => error(err))
}

function get(url,success,failure=defaultFailure){
    internalGet(url,accessHeader(),success,failure)
}
function post(url,data,success,failure=defaultFailure){
    internalPost(url,data,accessHeader(),success,failure)
}
function login(username,password,remember,success,failure=defaultFailure){
    internalPost('/api/auth/login',{
        username:username,
        password:password
    },{
            'Content-Type': 'application/x-www-form-urlencoded'
    },(data)=>{
            storeAccessToken(remember,data.token,data.id,data.expire)
            ElMessage.success(`登录成功，欢迎${data.username}`)
            success(data)
    },failure
    )
}

function logout(success,failure=defaultFailure){
    get('api/auth/logout',()=>{
        deleAccessToken()
        ElMessage.success('退出登录成功')
        success()
    },failure)

}



function unauthorized(){
    return !takeAccessToken()
}
export {login,logout,get,post,unauthorized,accessHeader,accessUserId}