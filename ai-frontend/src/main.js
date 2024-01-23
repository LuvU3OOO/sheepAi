import { createApp } from 'vue'
import App from './App.vue'
import router from '@/router'
import axios from 'axios'
import {MdPreview} from "md-editor-v3";
import "md-editor-v3/lib/preview.css";
import 'element-plus/theme-chalk/dark/css-vars.css'

axios.defaults.baseURL = 'http://localhost:8081'
const app = createApp(App)

app.use(router)
app.component('MdPreview',MdPreview)
app.mount('#app')
