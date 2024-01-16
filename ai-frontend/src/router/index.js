import { createRouter, createWebHistory } from "vue-router";
import { unauthorized } from "../net";
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
            component:()=>import('@/views/chat/test.vue')
        }

    ]
})

router.beforeEach((to,from,next)=>{
    const isUnauthorized = unauthorized()
    if(to.name.startsWith('welcome-') && !isUnauthorized){   //已登录不可再访问登陆页面,name表示路由名称在router里配置的
            next('/index')
    }else if(to.fullPath.startsWith('/index') && isUnauthorized) {// 没有验证不可访问登录才可访问的页面,fullpath表示目标路由的完整路径
        next('/')
    }else{
        next()
    }
     
})

export default router