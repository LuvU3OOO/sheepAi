import { createApp } from 'vue'
import App from './App.vue'
import router from '@/router'
import axios from 'axios'
import 'element-plus/theme-chalk/dark/css-vars.css'

axios.defaults.baseURL = 'http://localhost:8081'
const app = createApp(App)
const global = window; // fix global is undefined in socketjs-client
app.use(router)


app.mount('#app')
