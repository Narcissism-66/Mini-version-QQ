<script setup>
import { RouterLink, RouterView } from 'vue-router'
import {useWebSocketStore} from "@/stores/WebSocketStor.js";
import {userUserStore} from "@/stores/userStore.js";
import {onUnmounted, watch, ref} from "vue";
const wsStore = useWebSocketStore();
const userStore = userUserStore();

// 添加自动重连机制
const reconnectInterval = ref(null);
const maxReconnectAttempts = 5;
let reconnectAttempts = 0;

const startReconnect = () => {
  if (reconnectAttempts < maxReconnectAttempts) {
    reconnectAttempts++;
    console.log(`尝试重新连接WebSocket (${reconnectAttempts}/${maxReconnectAttempts})`);
    wsStore.initWebSocket(userStore.user.id);
  } else {
    console.error("WebSocket重连失败，已达到最大重试次数");
    clearInterval(reconnectInterval.value);
  }
};

// 添加beforeunload事件处理
const handleBeforeUnload = () => {
  wsStore.closeWebSocket();
  clearInterval(reconnectInterval.value);
};

// 监听用户信息变化
watch(() => userStore.user, (newUser) => {
  if (newUser) {
    wsStore.initWebSocket(newUser.id);
    window.addEventListener('beforeunload', handleBeforeUnload);
    
    // 设置自动重连
    reconnectInterval.value = setInterval(() => {
      if (!wsStore.ws || wsStore.ws.readyState !== WebSocket.OPEN) {
        startReconnect();
      } else {
        reconnectAttempts = 0; // 重置重连次数
      }
    }, 5000); // 每5秒检查一次连接状态
  }
}, { immediate: true });

//-------------------------------------------------
onUnmounted(()=>{
  // 移除beforeunload事件监听器
  window.removeEventListener('beforeunload', handleBeforeUnload);
  wsStore.closeWebSocket();
  clearInterval(reconnectInterval.value);
})
</script>

<template>
  <RouterView />
</template>


