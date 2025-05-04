import { defineStore } from 'pinia'
import { ref } from 'vue'
import { post } from "@/net/index.js"

export const useWebSocketStore = defineStore('websocket', () => {
  const ws = ref(null)
  const messageCallback=ref(null);

  const initWebSocket = (userId) => {
    // 如果已经存在连接且连接状态正常，则不重新连接
    if (ws.value && ws.value.readyState === WebSocket.OPEN) {
      return;
    }
    console.log("开始连接WebSocket")
    ws.value = new WebSocket(`ws://localhost:8080/myWs?userId=${userId}`)
    ws.value.onopen = () => {
      console.log("WebSocket连接成功！")
    }
    // 在 WebSocket 连接初始化时添加消息接收处理
    ws.value.onmessage = (event) => {
      console.log("收到WebSocket消息:", event.data)
      //检查是否已经设置了回调函数。如果已经设置了回调函数，那么就执行这个回调函数，并将收到的消息数据传递给它。
      if (messageCallback.value) {
        messageCallback.value(event.data)//调用 messageCallback.value 函数，并将 receivedData（收到的消息数据）作为参数传递给它。
      }
    }
  }

  const sendMsg = (toUserId,question) => {
    if (!ws.value || ws.value.readyState !== WebSocket.OPEN) {
      console.error("WebSocket连接未建立或已断开");
      // 尝试重新连接
      initWebSocket(userStore.user.id);
      return;
    }
    const privateMsg = {
      toUserId: toUserId,
      content: question
    };
    console.log(toUserId+" "+question);
    ws.value.send(JSON.stringify(privateMsg));
  }

  const closeWebSocket = () => {
    if (ws.value) {
      ws.value.close();
      ws.value = null;
    }
  }
  // 添加设置回调函数的方法
  const setMessageCallback = (callback) => {
    messageCallback.value = callback//可以理解为messageCallback现在就是传入的回调函数
  }


  return {
    ws,
    initWebSocket,
    closeWebSocket,
    sendMsg,
    setMessageCallback
  }
})
