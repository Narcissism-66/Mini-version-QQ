<script setup>
import { onMounted, reactive, ref } from 'vue'
import { get } from "@/net/index.js"
import { message } from "ant-design-vue"
import { userUserStore } from "@/stores/userStore.js"
import { formatDateTime } from "@/stores/formatDateTime.js"
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

const [messageApi, contextHolder] = message.useMessage()
const userStore = userUserStore()
const options = reactive({
  story:[]
})


// 获取朋友们的说说列表
const getAllStory = () => {
  get("api/story/getAllStory",{},(message, data) => {
    options.story=data;
  })
}

// 点赞功能
const handleLike = (story) => {
  // get("api/story/like", {
  //   storyId: story.id
  // }, (message, data) => {
  //   story.isLike = !story.isLike
  //   story.likes = data
  //   messageApi.success(message)
  // })
}

// 收藏功能
const handleFavourite = (story) => {
  // get("api/story/favourite", {
  //   storyId: story.id
  // }, (message, data) => {
  //   story.isFavourite = !story.isFavourite
  //   story.favourites = data
  //   messageApi.success(message)
  // })
}

onMounted(() => {
  getAllStory()
})
</script>

<template>
  <contextHolder/>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- 标题区域 -->
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-900">朋友们的动态</h1>
        <p class="text-gray-600 mt-2">看看朋友们最近分享了什么有趣的事情</p>
      </div>

      <!-- 空状态 -->
      <div v-if="options.story.length === 0" class="flex flex-col items-center justify-center py-12 bg-white rounded-xl shadow-lg">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-gray-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
        </svg>
        <span class="text-xl font-medium text-gray-500">暂时还没有朋友的动态</span>
      </div>

      <!-- 说说列表 -->
      <div v-else class="space-y-6">
        <div v-for="story in options.story"
             class="bg-white rounded-xl shadow-md hover:shadow-lg transition-all duration-300 border border-gray-100">
          <!-- 卡片头部 -->
          <div class="p-3 border-b border-gray-100">
            <div class="flex justify-between items-center">
              <!-- 左侧标题和作者信息 -->
              <div class="flex flex-col leading-none">
                <p class="font-bold text-gray-800 text-2xl">{{ story.title }}</p>
                <div class="flex items-center gap-1.5 text-sm text-gray-500">
                  <span>作者：{{ story.username }}</span>
                  <span class="text-gray-300">|</span>
                  <span>发表时间：{{ formatDateTime(story.time) }}</span>
                </div>
              </div>
              <!-- 右侧头像 -->
              <a-avatar :size="36" :src="story.avatar" class="ring-1 ring-blue-100"></a-avatar>
            </div>
          </div>

          <!-- 卡片内容 -->
          <div class="p-4">
            <MdPreview :modelValue="story.content" class="prose max-w-none" />
          </div>

          <!-- 卡片底部互动栏 -->
          <div class="px-4 py-3 bg-gray-50 border-t border-gray-100">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-6">
                <!-- 点赞按钮 -->
                <button @click="handleLike(story)"
                        class="flex items-center gap-2 text-gray-600 hover:text-blue-500 transition-colors duration-200">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5"
                       :class="{'text-blue-500': story.isLike}"
                       viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                  </svg>
                  <span class="text-sm">{{ story.likes }}</span>
                </button>

                <!-- 收藏按钮 -->
                <button @click="handleFavourite(story)"
                        class="flex items-center gap-2 text-gray-600 hover:text-yellow-500 transition-colors duration-200">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5"
                       :class="{'text-yellow-500': story.isFavourite}"
                       viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"/>
                  </svg>
                  <span class="text-sm">{{ story.favourites }}</span>
                </button>
              </div>

              <!-- 分享按钮 -->
              <button class="flex items-center gap-2 text-gray-600 hover:text-green-500 transition-colors duration-200">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z"/>
                </svg>
                <span class="text-sm">分享</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

@keyframes l24 {
  100% {transform: rotate(1turn)}
}

/* 过渡动画 */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 300ms;
}
</style>
