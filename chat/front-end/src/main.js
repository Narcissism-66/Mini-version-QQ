import './assets/css/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import axios from "axios";

axios.defaults.baseURL = 'http://localhost:8081'//设置全局的基础路径
const ImgUrl="http://localhost:8081/";
const BaseUrl="http://localhost:8081/";
const WsUrl="ws://localhost:8081/";
// axios.defaults.baseURL = 'https://www.aiitfztest.xyz'
// const ImgUrl="https://www.aiitfztest.xyz/api/";
// const BaseUrl='https://www.aiitfztest.xyz/';
// const WsUrl="wss://www.aiitfztest.xyz/";

axios.defaults.withCredentials=true;
//后端基础url 之后在请求时只用填写路径 Axios会自动以该url为基础添加路径

const app = createApp(App)
app.config.globalProperties.$axios = axios
app.use(createPinia())
app.use(router).use(ElementPlus).use(Antd);

app.mount('#app')
export {ImgUrl,BaseUrl,WsUrl}
