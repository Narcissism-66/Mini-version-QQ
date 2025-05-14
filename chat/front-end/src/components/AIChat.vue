<script setup>
import {nextTick, onMounted, ref, watch} from 'vue'
import {get} from "@/net/index.js";
import {useWebSocketStore} from "@/stores/WebSocketStor.js";
import MarkdownIt from 'markdown-it';
import 'github-markdown-css/github-markdown.css'
import {userUserStore} from "@/stores/userStore.js";
import {BaseUrl} from "@/main.js";

const messages = ref([
  // { role: 'ai', content: '你好！我是AI助手，有什么可以帮你的吗？' },
])

const newMessage = ref('')
const userStore=userUserStore();

const getAIChatByUserId=()=>{
  get("/api/AI/getAIChatByUserId",{
    userId:userStore.user.id
  },(message,data)=>{
    const SessionRecord=Array.isArray(data)?data:[data];
    if(SessionRecord.length>0){
      SessionRecord.forEach((item)=>{
        messages.value.push({ role: "user", content: item.question })
        messages.value.push({ role: "ai", content: item.reply })
      })
    }
    else messages.value.push({ role: 'ai', content: '你好！我是AI助手，有什么可以帮你的吗？' });

  })
}

const sendMessage = () => {
  if (!newMessage.value.trim()) {
      IsLoading.value=true;
      get("/api/AI/chat1",{
      message:newMessage.value
    },(message,data)=>{
        messages.value.push({ role: 'user', content: newMessage.value })
        messages.value.push({ role: 'ai', content: data })
        newMessage.value = ''
        IsLoading.value=false;
    })
  }
}

const getDate=()=>{
  const now = new Date();
  // 提取年、月、日、时、分、秒
  const year = now.getFullYear(); // 获取年份
  const month = String(now.getMonth() + 1).padStart(2, '0'); // 获取月份，+1是因为月份从0开始，padStart确保两位数
  const day = String(now.getDate()).padStart(2, '0'); // 获取日期，padStart确保两位数
  const hours = String(now.getHours()).padStart(2, '0'); // 获取小时，padStart确保两位数
  const minutes = String(now.getMinutes()).padStart(2, '0'); // 获取分钟，padStart确保两位数
  const seconds = String(now.getSeconds()).padStart(2, '0'); // 获取秒数，padStart确保两位数

// 拼接成年月日时分秒的字符串
  return `${year}${month}${day}${hours}${minutes}${seconds}`;
}

const md = new MarkdownIt({
  html: true,      // 允许 HTML 标签
  linkify: true,   // 自动转换链接
  typographer: true // 优化排版
});

const IsLoading = ref(false);
const chatContainer = ref(null) // 添加对聊天容器的引用
const sendMessageStream = () => {
  if (!newMessage.value.trim()) {
    IsLoading.value = true;
    return;
  }

  // const message = "你的问题"; // 替换为实际消息
  console.log(userStore.user.id)
  const url = BaseUrl+`api/AI/chat3?message=${encodeURIComponent(newMessage.value)}&userId=${userStore.user.id}`;

  const eventSource = new EventSource(url, {
    withCredentials: true // 如果需要发送cookie
  });

  let id=getDate();
  // 追加到消息列表
  messages.value.push({ role: 'user', content: newMessage.value });
  newMessage.value='';

  eventSource.onmessage = (event) => {
    const data = event.data;

    // 或者更优雅的方式 - 只更新最后一条消息的内容
    const lastMessage = messages.value[messages.value.length - 1];
    if (lastMessage.role === 'ai'&& lastMessage.id===id) {
      lastMessage.content += data;
    } else {
      id=getDate();
      IsLoading.value=false;
      messages.value.push({ role: 'ai', content: data ,id:id});
    }
  };

  eventSource.onerror = () => {
    eventSource.close();
  };
}

const scrollContent=()=>{
  const observer = new MutationObserver(() => {
    // 在 DOM 更新后滚动到底部
    nextTick(() => {
      if (chatContainer.value) {
        chatContainer.value.scrollTop = chatContainer.value.scrollHeight
      }
    })
  })

  // 监听聊天容器的子元素变化
  if (chatContainer.value) {
    observer.observe(chatContainer.value, {
      childList: true,    // 监听子元素增减
      subtree: true       // 监听所有后代元素
    })
  }

  // 初始滚动到底部
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

// 监听输入框变化
watch(newMessage, (newValue) => {
  if (!newValue.trim()) {
    IsLoading.value = true;
  } else {
    IsLoading.value = false;
  }
});

onMounted(()=>{
  scrollContent();
  getAIChatByUserId();
})
</script>

<template>
  <div class="flex flex-col  w-full max-w-4xl  mx-auto bg-white rounded-lg shadow-lg h-screen p-2  ">
    <!-- 聊天记录区域 -->
    <div ref="chatContainer" class="flex-1 mt-4 flex-nowrap overflow-y-auto p-4 space-y-4 scrollbar-hide" style="max-height: 800px">
      <div v-for="message in messages" :class="['w-full flex', message.role === 'user' ? 'justify-end' : 'justify-start']">
        <svg v-if="message.role!== 'user'" xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 48 48" class="size-6 mt-2">
          <path fill="none" stroke="currentColor" stroke-linejoin="round" d="M18.38 27.94v-14.4l11.19-6.46c6.2-3.58 17.3 5.25 12.64 13.33"/><path fill="none" stroke="currentColor" stroke-linejoin="round" d="m18.38 20.94l12.47-7.2l11.19 6.46c6.2 3.58 4.1 17.61-5.23 17.61"/><path fill="none" stroke="currentColor" stroke-linejoin="round" d="m24.44 17.44l12.47 7.2v12.93c0 7.16-13.2 12.36-17.86 4.28"/><path fill="none" stroke="currentColor" stroke-linejoin="round" d="M30.5 21.2v14.14L19.31 41.8c-6.2 3.58-17.3-5.25-12.64-13.33"/><path fill="none" stroke="currentColor" stroke-linejoin="round" d="m30.5 27.94l-12.47 7.2l-11.19-6.46c-6.21-3.59-4.11-17.61 5.22-17.61"/><path fill="none" stroke="currentColor" stroke-linejoin="round" d="m24.44 31.44l-12.47-7.2V11.31c0-7.16 13.2-12.36 17.86-4.28"/>
        </svg>
        <div class="w-fit max-w-[80%] rounded-lg flex">
          <p class="flex flex-wrap p-2 markdown-body" v-html="md.render(message.content)"></p>
          <a-avatar v-if="message.role==='user'" size="large" src="https://ts1.tc.mm.bing.net/th/id/R-C.aa304f97225bb6a872206d010a46b085?rik=VZ27kKN1P1DlBQ&riu=http%3a%2f%2fn.sinaimg.cn%2fsinakd20241112ac%2f560%2fw1080h1080%2f20241112%2f2d8e-e5eb58d7228f84648cae3d8ee1886b0b.jpg&ehk=a75ATBJJ%2fkQ%2fmocbqzBNSfi7jbhnV3nmxRAa9CbtwNI%3d&risl=&pid=ImgRaw&r=0" class="select-none cursor-pointer"></a-avatar>
        </div>
      </div>
      <div v-if="IsLoading" class="w-fit h-fit loader"></div>
    </div>

    <!-- 输入区域 -->
    <div class="border-t p-4 ">
      <div class="flex space-x-2">
        <input
          v-model="newMessage"
          @keyup.enter="sendMessageStream"
          type="text"
          placeholder="输入消息..."
          class="flex-1 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <button
          @click="sendMessageStream"
          class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          发送
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 隐藏所有浏览器滚动条 */
.scrollbar-hide::-webkit-scrollbar {
  display: none; /* Chrome/Safari/Edge */
}

.scrollbar-hide {
  -ms-overflow-style: none;  /* IE/Edge */
  scrollbar-width: none;     /* Firefox */
}
/* HTML: <div class="loader"></div> */
.loader {
  width: 30px;
  aspect-ratio: 1;
  border-radius: 50%;
  border: 8px solid #0000;
  border-right-color: rgba(228, 206, 165, 0.59);
  position: relative;
  animation: l24 1s infinite linear;
}
.loader:before,
.loader:after {
  content: "";
  position: absolute;
  inset: -8px;
  border-radius: 50%;
  border: inherit;
  animation: inherit;
  animation-duration: 2s;
}
.loader:after {
  animation-duration: 4s;
}
@keyframes l24 {
  100% {transform: rotate(1turn)}
}
</style>
