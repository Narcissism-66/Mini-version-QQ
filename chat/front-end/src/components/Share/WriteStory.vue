<script setup>
import { ref } from 'vue';
import { MdEditor } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';
import {post} from "@/net/index.js";
import {message} from "ant-design-vue";

const title = ref('');
const content = ref('# 分享你的趣事\n\n在这里写下你想分享的故事...');
const isSubmitting = ref(false);
const [messageApi, contextHolder] = message.useMessage();

const InsertStory = () => {
  post("/api/story/insert",{
    title:title.value,
    content:content.value
  },(message)=>{
    messageApi.success("发布成功！")
  })
}
</script>

<template>
  <contextHolder/>
  <div class="min-h-screen bg-gray-50 py-8 px-4 sm:px-6 lg:px-8">
    <div class="max-w-4xl mx-auto">
      <!-- 标题区域 -->
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-900 mb-2">分享趣事</h1>
        <p class="text-gray-600">写下你想分享的有趣故事，让更多人看到！</p>
      </div>

      <!-- 表单区域 -->
      <div class="bg-white rounded-lg shadow-sm p-6">
        <!-- 标题输入框 -->
        <div class="mb-6">
          <label for="title" class="block text-sm font-medium text-gray-700 mb-2">标题</label>
          <input
            type="text"
            id="title"
            v-model="title"
            placeholder="给你的故事起个吸引人的标题"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
          />
        </div>

        <!-- 编辑器 -->
        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-2">内容</label>
          <MdEditor
            v-model="content"
            class="border border-gray-300 rounded-md"
            :preview="true"
            previewTheme="github"
            :toolbars="[
              'bold', 'italic', 'strikethrough', 'heading',
              'quote', 'code', 'link', 'image', 'table',
              'preview', 'fullscreen'
            ]"
          />
        </div>

        <!-- 发布按钮 -->
        <div class="flex justify-end">
          <button
            @click="InsertStory"
            :disabled="isSubmitting"
            class="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {{ isSubmitting ? '发布中...' : '发布故事' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.md-editor {
  height: 500px !important;
}
</style>
