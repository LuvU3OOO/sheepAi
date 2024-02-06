import { createRouter, createWebHistory } from "vue-router";
import { unauthorized } from "../net";
import { ElMessage } from "element-plus";
const global = window; // fix global is undefined in socketjs-client
const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path:'/',
            name:'welcome',
            component:() => import('@/views/WelcomeView.vue'),
            children:[
                {
                    path:'',
                    name:'welcome-login',
                    component:()=>import('@/views/welcome/LoginPage.vue')
                },{
                    path:'register',
                    name:'welcom-register',
                    component:()=>import('@/views/welcome/RegisterPage.vue')
                },{
                    path:'reset',
                    name:'welcome-reset',
                    component:()=>import('@/views/welcome/ResetPage.vue')
                },
            ]
        },
        {
            path:'/index',
            name:'index',
            component:()=>import('@/views/HomeView.vue')
        },
        {
            path:'/chat',
            name:'chat',
            component:()=>import('@/views/chat/ChatPage.vue')
        }

    ]
})

router.beforeEach((to,from,next)=>{
    const isUnauthorized = unauthorized()
    // console.log(isUnauthorized)
    if(to.name.startsWith('welcome-') && !isUnauthorized){   //已登录不可再访问登陆页面,name表示路由名称在router里配置的
            next('/chat')
    }else if(to.fullPath.startsWith('/chat') && isUnauthorized) {// 没有验证不可访问登录才可访问的页面,fullpath表示目标路由的完整路径
        next('/')
        ElMessage.warning("验证信息失效，请重新登录")
    }else
    {
        next()
    }
     
})

export default router