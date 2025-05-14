<script setup>

import router from "@/router/index.js";
import {RouterView} from "vue-router";
import {userUserStore} from "@/stores/userStore.js";

const userStore = userUserStore();
const logout=()=>{
  router.push("/auth")
  userStore.logout();
}
const SeeMessage=()=>{
  router.push("/chat")
}

</script>

<template>
  <div class="h-screen w-full fixed flex flex-wrap py-8 border-r-2 border-blue-500  sidebar">
    <div class="flex flex-col w-full space-y-6">
      <!-- 个人空间 -->
      <div @click="router.push('/')" class="nav-item">
        <div class="flex items-center">
          <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 48 48" class="nav-icon">
            <g fill="none" stroke="currentColor" stroke-linejoin="round" stroke-width="4"><path d="M6 9.256L24.009 4L42 9.256v10.778C42 31.362 34.75 40.419 24.003 44C13.253 40.42 6 31.36 6 20.029z"/><circle cx="24" cy="18" r="5" stroke-linecap="round"/><path stroke-linecap="round" d="M32 31a8 8 0 1 0-16 0"/></g>
          </svg>
          <span class="nav-text">个人空间</span>
        </div>
      </div>

      <!-- 消息中心 -->
      <div @click="SeeMessage()" class="nav-item">
        <div class="flex items-center">
          <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24" class="nav-icon">
            <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" color="currentColor"><path d="m7 8.5l2.942 1.74c1.715 1.014 2.4 1.014 4.116 0L17 8.5"/><path d="M2.016 13.476c.065 3.065.098 4.598 1.229 5.733c1.131 1.136 2.705 1.175 5.854 1.254c1.94.05 3.862.05 5.802 0c3.149-.079 4.723-.118 5.854-1.254c1.131-1.135 1.164-2.668 1.23-5.733c.02-.986.02-1.966 0-2.952c-.066-3.065-.099-4.598-1.23-5.733c-1.131-1.136-2.705-1.175-5.854-1.254a115 115 0 0 0-5.802 0c-3.149.079-4.723.118-5.854 1.254c-1.131 1.135-1.164 2.668-1.23 5.733a69 69 0 0 0 0 2.952"/></g>
          </svg>
          <span class="nav-text">消息中心</span>
        </div>
      </div>

      <div @click="router.push('/AIChat')" class="nav-item ">
        <div class="flex items-center">
          <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 48 48" class="nav-icon">
            <path fill="none" stroke="currentColor" stroke-linejoin="round" d="M18.38 27.94v-14.4l11.19-6.46c6.2-3.58 17.3 5.25 12.64 13.33"/><path fill="none" stroke="currentColor" stroke-linejoin="round" d="m18.38 20.94l12.47-7.2l11.19 6.46c6.2 3.58 4.1 17.61-5.23 17.61"/><path fill="none" stroke="currentColor" stroke-linejoin="round" d="m24.44 17.44l12.47 7.2v12.93c0 7.16-13.2 12.36-17.86 4.28"/><path fill="none" stroke="currentColor" stroke-linejoin="round" d="M30.5 21.2v14.14L19.31 41.8c-6.2 3.58-17.3-5.25-12.64-13.33"/><path fill="none" stroke="currentColor" stroke-linejoin="round" d="m30.5 27.94l-12.47 7.2l-11.19-6.46c-6.21-3.59-4.11-17.61 5.22-17.61"/><path fill="none" stroke="currentColor" stroke-linejoin="round" d="m24.44 31.44l-12.47-7.2V11.31c0-7.16 13.2-12.36 17.86-4.28"/>
          </svg>
          <span class="nav-text">AI对话</span>
        </div>
      </div>

      <!-- 设置 -->
      <div @click="router.push('/FriendStory')" class="nav-item ">
        <div class="flex items-center">
          <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 28 28" class="nav-icon">
            <path fill="currentColor" d="M14.65 4.127a1.75 1.75 0 0 0-1.3 0l-8.75 3.5a1.75 1.75 0 0 0-1.1 1.625v9.505c0 .716.436 1.36 1.1 1.625l8.75 3.5a1.75 1.75 0 0 0 1.3 0l8.75-3.5a1.75 1.75 0 0 0 1.1-1.625V9.252a1.75 1.75 0 0 0-1.1-1.625zm-1.857-1.392a3.25 3.25 0 0 1 2.414 0l8.75 3.5A3.25 3.25 0 0 1 26 9.252v9.505a3.25 3.25 0 0 1-2.043 3.018l-8.75 3.5a3.25 3.25 0 0 1-2.414 0l-8.75-3.5A3.25 3.25 0 0 1 2 18.757V9.252a3.25 3.25 0 0 1 2.043-3.017zm-5.73 6.72a.75.75 0 0 1 .989-.384L14 11.685l5.948-2.614a.75.75 0 0 1 .604 1.373l-5.802 2.55v6.758a.75.75 0 0 1-1.5 0v-6.758l-5.802-2.55a.75.75 0 0 1-.385-.988"/>
          </svg>
          <span class="nav-text">空间</span>
        </div>
      </div>

      <!--      退出登陆-->
      <div @click="logout()" class="nav-item ">
        <div  class="flex items-center">
          <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24" class="nav-icon">
            <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-width="1.5"><path stroke-linejoin="round" d="M10 12h10m0 0l-3-3m3 3l-3 3"/><path d="M4 12a8 8 0 0 1 8-8m0 16a7.99 7.99 0 0 1-6.245-3"/></g>
          </svg>
          <span class="nav-text">退出</span>
        </div>
      </div>
    </div>
  </div>

  <RouterView />
</template>

<style scoped>


.sidebar {
  width: 5rem;  /* 默认宽度为5rem（约80px） */
  border-right: 2px solid #b0c4e4;  /* 右侧蓝色边框 */
  border-radius: 0.75rem;  /* 圆角 */
  transition: all 0.3s ease;  /* 所有属性变化都有0.3秒的过渡动画 */
  overflow: hidden;  /* 隐藏溢出内容 */
  display: flex;
  flex-direction: column;  /* 垂直布局 */
  //margin: 5vh 0;  /* 上下边距 */
  background: rgba(255, 255, 255, 0.9);  /* 半透明白色背景 */
  backdrop-filter: blur(10px);  /* 毛玻璃效果 */
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);  /* 阴影效果 */
  //height: calc(100vh - 10vh);  /* 设置高度为视口高度减去上下边距 */
}

.sidebar:hover {
  width: 12rem;  /* 鼠标悬停时宽度变为12rem（约192px） */
}

.nav-item {
  display: flex;
  align-items: center;  /* 垂直居中对齐 */
  padding: 0.75rem 1rem;  /* 内边距 */
  margin: 0 0.5rem;  /* 外边距 */
  border-radius: 0.5rem;  /* 圆角 */
  cursor: pointer;  /* 鼠标指针变为手型 */
  transition: all 0.2s ease;  /* 过渡动画 */
}

.nav-item:hover {
  background-color: rgba(59, 130, 246, 0.1);  /* 悬停时淡蓝色背景 */
  transform: translateX(5px);  /* 向右移动5px */
}

.nav-icon {
  width: 1.5rem;  /* 图标大小 */
  height: 1.5rem;
  color: #3b82f6;  /* 图标颜色 */
  transition: all 0.3s ease;  /* 过渡动画 */
  margin: 0 auto;  /* 默认居中 */
}

.nav-item:hover .nav-icon {
  transform: scale(1.1);  /* 悬停时图标放大1.1倍 */
  color: #2563eb;  /* 悬停时图标颜色变深 */
  margin: 0;  /* 取消居中 */
}

.nav-text {
  margin-left: 1rem;  /* 左边距 */
  font-size: 0.875rem;  /* 文字大小 */
  color: #4b5563;  /* 文字颜色 */
  white-space: nowrap;  /* 文字不换行 */
  opacity: 0;  /* 默认透明 */
  width: 0;  /* 默认宽度为0 */
  overflow: hidden;  /* 隐藏溢出内容 */
  transition: all 0.3s ease;  /* 过渡动画 */
}

.sidebar:hover .nav-text {
  opacity: 1;  /* 完全不透明 */
  width: auto;  /* 宽度自适应 */
}
</style>
