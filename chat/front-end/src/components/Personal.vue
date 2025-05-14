<script setup>
import {onMounted, reactive, ref} from 'vue'
import {ImgUrl} from "@/main.js";
import axios from "axios";
import {get, post} from "@/net/index.js";
import {message} from "ant-design-vue";
import {userUserStore} from "@/stores/userStore.js";
import router from "@/router/index.js";
import {RouterView} from "vue-router";
import { marked } from 'marked';
import {formatDateTime} from "@/stores/formatDateTime.js";
import { MdPreview, MdCatalog } from 'md-editor-v3';

const [messageApi, contextHolder] = message.useMessage();
const userStore=userUserStore();
const SelectFunction=ref(1);
const userInfo = reactive({
  avatar: userStore.user.avatar,
  username: userStore.user.username,
  account: userStore.user.account,
  OldPassword: '',
  NewPassword: '',
  bio: '这个人很懒，什么都没写~',
  registerTime: '2025-01-01',
  storyCount: 0,
  likeCount: 0,
  favouriteCount: 0,
  // email: 'example@email.com'
})
const options=reactive({
  story:[],
  group:[]
})


const isEditing = ref(false)


// 头像上传处理
const onUploadImg = async (e, callback) => {
  const file = e.target.files[0]; // 获取上传的第一个文件
  if (!file) return; // 如果没有选择文件，则退出

  const form = new FormData();
  form.append('image', file);

  try {
    const response = await axios.post("api/user/uploadImg", form, {
      headers: {
        "Content-Type": "multipart/form-data",
        "Authorization": `${localStorage.getItem("authToken")}`,
      },
    });

    if (response.data.success) {
      // 上传成功后，更新头像 URL
      userInfo.avatar = ImgUrl+`${response.data.data}`;
      updateAvatar();
      console.log("上传成功，新的头像地址：", userInfo.avatar);
      return ImgUrl+`${response.data.data}`;
    } else {
      throw new Error(response.data.message); // 如果上传失败，抛出错误
    }
  } catch (error) {
    console.error("上传失败：", error);
    callback([]); // 上传失败时传递空的 URL 列表
  }
};
const updateAvatar=()=>{
  post("api/user/updateAvatar",{
    avatar:userInfo.avatar,
  },(message)=>{
    messageApi.success("头像更新成功")
  })
}

const updateUserInfo=()=>{
  post("api/user/updateUserInfo",{
    username:userInfo.username,
    OldPassword:userInfo.OldPassword,
    NewPassword:userInfo.NewPassword,
  },(message)=>{
    messageApi.success("信息更新成功")
  },(message)=>{
    messageApi.error("信息更新失败！")
  })
}

const getStoryByUserId=()=>{
  get("api/story/getStoryByUserId",{},(message,data)=>{
    options.story=data;
  })
}

const GetGroupById=()=>{
  get("api/chat/GetGroupById",{},(message,data)=>{
    options.group = data;
  })
}

// 添加点击事件处理函数
const handleFunctionClick = (functionId) => {
  SelectFunction.value = functionId;
}

// 添加 Markdown 渲染函数
const renderMarkdown = (content) => {
  return marked(content);
}

onMounted(()=>{
  getStoryByUserId();
  GetGroupById();
})

</script>

<template>
  <contextHolder/>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- 个人信息卡片 -->
      <div class="bg-white rounded-2xl shadow-lg overflow-hidden transition-all duration-300 hover:shadow-xl">
        <div class="p-8">
          <div class="flex flex-col md:flex-row gap-8">
            <!-- 左侧头像和基本信息 -->
            <div class="flex flex-col items-center md:w-1/3">
              <div class="relative group">
                <img :src="userInfo.avatar" alt="头像"
                  class="w-32 h-32 rounded-full object-cover border-4 border-blue-100 shadow-lg transition-transform duration-300 group-hover:scale-105">
                <label class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 rounded-full opacity-0 group-hover:opacity-100 cursor-pointer transition-all duration-300">
                  <input type="file" accept="image/*" class="hidden" @change="onUploadImg">
                  <span class="text-white text-sm font-medium">更换头像</span>
                </label>
              </div>
              <h2 class="mt-4 text-2xl font-bold text-gray-800">{{ userInfo.username }}</h2>
              <p class="text-gray-500">账号：{{ userInfo.account }}</p>
              <p class="text-gray-500 mt-1">注册于：{{ userInfo.registerTime }}</p>
            </div>

            <!-- 右侧详细信息 -->
            <div class="flex-1 space-y-6">
              <!-- 用户简介 -->
              <div class="bg-gray-50 rounded-xl p-4">
                <h3 class="text-lg font-semibold text-gray-700 mb-2">个人简介</h3>
                <p class="text-gray-600">{{ userInfo.bio }}</p>
              </div>

              <!-- 数据统计 -->
              <div class="grid grid-cols-3 gap-4">
                <div class="bg-blue-50 rounded-xl p-4 text-center">
                  <div class="text-2xl font-bold text-blue-600">{{ options.story.length }}</div>
                  <div class="text-sm text-gray-600 mt-1">动态</div>
                </div>
                <div class="bg-green-50 rounded-xl p-4 text-center">
                  <div class="text-2xl font-bold text-green-600">{{ userInfo.likeCount }}</div>
                  <div class="text-sm text-gray-600 mt-1">获赞</div>
                </div>
                <div class="bg-yellow-50 rounded-xl p-4 text-center">
                  <div class="text-2xl font-bold text-yellow-600">{{ userInfo.favouriteCount }}</div>
                  <div class="text-sm text-gray-600 mt-1">粉丝</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 编辑按钮 -->
          <div class="flex justify-end mt-6">
            <button @click="isEditing = true" v-if="!isEditing"
              class="inline-flex items-center px-4 py-2 text-sm font-medium text-blue-600 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors duration-300">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"/>
              </svg>
              编辑资料
            </button>
          </div>
        </div>
      </div>

      <!-- 编辑模式表单 -->
      <div v-if="isEditing" class="mt-6 bg-white rounded-2xl shadow-lg p-6 transition-all duration-300">
        <div class="space-y-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">昵称</label>
            <a-input v-model:value="userInfo.username"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-300"
              :placeholder="userInfo.username" />
          </div>

          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">原始密码</label>
              <a-input v-model:value="userInfo.OldPassword" type="password"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-300"
                placeholder="请输入原密码"/>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">新密码</label>
              <a-input v-model:value="userInfo.NewPassword" type="password"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-300"
                placeholder="请输入新密码"/>
            </div>
          </div>

          <div class="flex justify-end space-x-4">
            <button @click="isEditing=false"
              class="px-6 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors duration-300">
              取消
            </button>
            <button @click="updateUserInfo()"
              class="px-6 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors duration-300">
              保存更改
            </button>
          </div>
        </div>
      </div>

      <!-- 功能导航栏 -->
      <div class="mt-8 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div @click="handleFunctionClick(1)" class="bg-white rounded-xl shadow-md p-6 hover:shadow-lg transition-all duration-300 cursor-pointer transform hover:-translate-y-1" :class="{'ring-2 ring-blue-500': SelectFunction === 1}">
          <div class="flex items-center space-x-4">
            <div class="p-3 bg-blue-100 rounded-lg">
              <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 28 28" class="size-6 text-blue-400">
                <path fill="currentColor" d="M14.65 4.127a1.75 1.75 0 0 0-1.3 0l-8.75 3.5a1.75 1.75 0 0 0-1.1 1.625v9.505c0 .716.436 1.36 1.1 1.625l8.75 3.5a1.75 1.75 0 0 0 1.3 0l8.75-3.5a1.75 1.75 0 0 0 1.1-1.625V9.252a1.75 1.75 0 0 0-1.1-1.625zm-1.857-1.392a3.25 3.25 0 0 1 2.414 0l8.75 3.5A3.25 3.25 0 0 1 26 9.252v9.505a3.25 3.25 0 0 1-2.043 3.018l-8.75 3.5a3.25 3.25 0 0 1-2.414 0l-8.75-3.5A3.25 3.25 0 0 1 2 18.757V9.252a3.25 3.25 0 0 1 2.043-3.017zm-5.73 6.72a.75.75 0 0 1 .989-.384L14 11.685l5.948-2.614a.75.75 0 0 1 .604 1.373l-5.802 2.55v6.758a.75.75 0 0 1-1.5 0v-6.758l-5.802-2.55a.75.75 0 0 1-.385-.988"/>
              </svg>
            </div>
            <div>
              <h3 class="text-lg font-semibold text-gray-800">我的空间</h3>
              <p class="text-sm text-gray-500">查看个人动态</p>
            </div>
          </div>
        </div>

        <div @click="router.push('/personal/edit')" class="bg-white rounded-xl shadow-md p-6 hover:shadow-lg transition-all duration-300 cursor-pointer transform hover:-translate-y-1">
          <div class="flex items-center space-x-4 my-auto">
            <div class="p-3 bg-green-100 rounded-lg">
              <svg xmlns="http://www.w3.org/2000/svg" width="8" height="8" viewBox="0 0 24 24" class="size-6 text-green-400">
                <path fill="currentColor" d="M16.61 21q-.994 0-1.687-.695q-.692-.696-.692-1.69q0-.15.132-.757l-7.197-4.273q-.324.374-.793.587t-1.007.213q-.986 0-1.676-.702T3 12t.69-1.683t1.676-.702q.537 0 1.007.213t.793.588l7.198-4.255q-.07-.194-.101-.385q-.032-.192-.032-.392q0-.993.697-1.689Q15.625 3 16.62 3t1.688.697T19 5.389t-.695 1.688t-1.69.692q-.542 0-1-.222t-.78-.597l-7.199 4.273q.07.194.101.386q.032.191.032.391t-.032.391t-.1.386l7.198 4.273q.323-.375.78-.597q.458-.222 1-.222q.994 0 1.69.696q.695.698.695 1.693t-.697 1.688t-1.692.692m.004-1q.589 0 .987-.398t.398-.986t-.398-.987t-.986-.398t-.987.398t-.398.986t.398.987t.987.398m-11.25-6.616q.596 0 1-.398q.403-.398.403-.986t-.403-.986t-1-.398q-.581 0-.973.398T4 12t.393.987t.973.397m11.25-6.615q.588 0 .986-.398T18 5.384t-.398-.986T16.616 4t-.987.398t-.398.987t.398.986t.987.398m0-1.385"/>
              </svg>
            </div>
            <div>
              <h3 class="text-lg font-semibold text-gray-800">发动态</h3>
              <p class="text-sm text-gray-500">分享我的趣事</p>
            </div>
          </div>
        </div>

        <div @click="handleFunctionClick(3)" class="bg-white rounded-xl shadow-md p-6 hover:shadow-lg transition-all duration-300 cursor-pointer transform hover:-translate-y-1" :class="{'ring-2 ring-blue-500': SelectFunction === 3}">
          <div class="flex items-center space-x-4">
            <div class="p-3 bg-purple-100 rounded-lg">
              <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 16 16" class="size-6 text-pink-400">
                <path fill="currentColor" d="M4 5.5a1.5 1.5 0 1 1 3 0a1.5 1.5 0 0 1-3 0M5.5 3a2.5 2.5 0 1 0 0 5a2.5 2.5 0 0 0 0-5m5 3a1 1 0 1 1 2 0a1 1 0 0 1-2 0m1-2a2 2 0 1 0 0 4a2 2 0 0 0 0-4M3 9h4.05a3.5 3.5 0 0 0-.713 1H3a.5.5 0 0 0-.5.5v.097l.004.049a1.85 1.85 0 0 0 .338.857c.326.448 1.036.997 2.658.997q.344 0 .636-.031c.098.34.246.66.437.95A7 7 0 0 1 5.5 13.5c-1.878 0-2.918-.654-3.467-1.409a2.85 2.85 0 0 1-.523-1.342a2 2 0 0 1-.01-.137V10.5A1.5 1.5 0 0 1 3 9m6.5 0a2.5 2.5 0 0 0 0 5h.5a.5.5 0 0 0 0-1h-.5a1.5 1.5 0 0 1 0-3h.5a.5.5 0 0 0 0-1zM13 9a.5.5 0 0 0 0 1h.5a1.5 1.5 0 0 1 0 3H13a.5.5 0 0 0 0 1h.5a2.5 2.5 0 0 0 0-5zm-4 2.5a.5.5 0 0 1 .5-.5h4a.5.5 0 0 1 0 1h-4a.5.5 0 0 1-.5-.5"/>
              </svg>
            </div>
            <div>
              <h3 class="text-lg font-semibold text-gray-800">群组</h3>
              <p class="text-sm text-gray-500">查看加入的群</p>
            </div>
          </div>
        </div>

        <div @click="handleFunctionClick(4)" class="bg-white rounded-xl shadow-md p-6 hover:shadow-lg transition-all duration-300 cursor-pointer transform hover:-translate-y-1" :class="{'ring-2 ring-blue-500': SelectFunction === 4}">
          <div class="flex items-center space-x-4">
            <div class="p-3 bg-orange-100 rounded-lg">
              <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24" class="size-6 text-orange-400">
                <path fill="currentColor" fill-rule="evenodd" d="M18.955 1.25c-.433 0-.83 0-1.152.043c-.356.048-.731.16-1.04.47s-.422.684-.47 1.04c-.043.323-.043.72-.043 1.152v13.09c0 .433 0 .83.043 1.152c.048.356.16.731.47 1.04s.684.422 1.04.47c.323.043.72.043 1.152.043h.09c.433 0 .83 0 1.152-.043c.356-.048.731-.16 1.04-.47s.422-.684.47-1.04c.043-.323.043-.72.043-1.152V3.955c0-.433 0-.83-.043-1.152c-.048-.356-.16-.731-.47-1.04s-.684-.422-1.04-.47c-.323-.043-.72-.043-1.152-.043zm-1.13 1.572l-.002.001l-.001.003l-.005.01a.7.7 0 0 0-.037.167c-.028.21-.03.504-.03.997v13c0 .493.002.787.03.997a.7.7 0 0 0 .042.177l.001.003l.003.001l.003.002l.007.003c.022.009.07.024.167.037c.21.028.504.03.997.03s.787-.002.997-.03a.7.7 0 0 0 .177-.042l.003-.001l.001-.003l.005-.01a.7.7 0 0 0 .037-.167c.028-.21.03-.504.03-.997V4c0-.493-.002-.787-.03-.997a.7.7 0 0 0-.042-.177l-.001-.003l-.003-.001l-.01-.005a.7.7 0 0 0-.167-.037c-.21-.028-.504-.03-.997-.03s-.787.002-.997.03a.7.7 0 0 0-.177.042M11.955 4.25h.09c.433 0 .83 0 1.152.043c.356.048.731.16 1.04.47s.422.684.47 1.04c.043.323.043.72.043 1.152v10.09c0 .433 0 .83-.043 1.152c-.048.356-.16.731-.47 1.04s-.684.422-1.04.47c-.323.043-.72.043-1.152.043h-.09c-.432 0-.83 0-1.152-.043c-.356-.048-.731-.16-1.04-.47s-.422-.684-.47-1.04c-.043-.323-.043-.72-.043-1.152V6.955c0-.433 0-.83.043-1.152c.048-.356.16-.731.47-1.04s.684-.422 1.04-.47c.323-.043.72-.043 1.152-.043m-1.132 1.573l.003-.001l-.003 12.355l-.001-.003l-.005-.01a.7.7 0 0 1-.037-.167c-.028-.21-.03-.504-.03-.997V7c0-.493.002-.787.03-.997a.7.7 0 0 1 .042-.177zm0 12.354l.003-12.355l.003-.002l.007-.003a.7.7 0 0 1 .167-.037c.21-.028.504-.03.997-.03s.787.002.997.03a.7.7 0 0 1 .177.042l.003.001l.001.003l.005.01c.009.022.024.07.037.167c.028.21.03.504.03.997v10c0 .493-.002.787-.03.997a.7.7 0 0 1-.042.177l-.001.003l-.003.001l-.01.005a.7.7 0 0 1-.167.037c-.21.028-.504.03-.997.03s-.787-.002-.997-.03a.7.7 0 0 1-.177-.042zM4.955 8.25c-.433 0-.83 0-1.152.043c-.356.048-.731.16-1.04.47s-.422.684-.47 1.04c-.043.323-.043.72-.043 1.152v6.09c0 .433 0 .83.043 1.152c.048.356.16.731.47 1.04s.684.422 1.04.47c.323.043.72.043 1.152.043h.09c.433 0 .83 0 1.152-.043c.356-.048.731-.16 1.04-.47s.422-.684.47-1.04c.043-.323.043-.72.043-1.152v-6.09c0-.433 0-.83-.043-1.152c-.048-.356-.16-.731-.47-1.04s-.684-.422-1.04-.47c-.323-.043-.72-.043-1.152-.043zm-1.13 1.572l-.002.001l-.001.003l-.005.01a.7.7 0 0 0-.037.167c-.028.21-.03.504-.03.997v6c0 .493.002.787.03.997a.7.7 0 0 0 .042.177v.002l.004.002l.01.005c.022.009.07.024.167.037c.21.028.504.03.997.03s.787-.002.997-.03a.7.7 0 0 0 .177-.042l.003-.001l.001-.003l.002-.004l.003-.006a.7.7 0 0 0 .037-.167c.028-.21.03-.504.03-.997v-6c0-.493-.002-.787-.03-.997a.7.7 0 0 0-.042-.177l-.001-.003l-.003-.001l-.01-.005a.7.7 0 0 0-.167-.037c-.21-.028-.504-.03-.997-.03s-.787.002-.997.03a.7.7 0 0 0-.177.042" clip-rule="evenodd"/><path fill="currentColor" d="M3 21.25a.75.75 0 0 0 0 1.5h18a.75.75 0 0 0 0-1.5z"/>
              </svg>
            </div>
            <div>
              <h3 class="text-lg font-semibold text-gray-800">数据统计</h3>
              <p class="text-sm text-gray-500">查看使用数据</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 功能展示区 -->
      <div class="mt-8  rounded-2xl  ">
        <div v-if="SelectFunction === 1">
          <div v-if="options.story.length === 0" class="flex flex-col items-center justify-center py-12 bg-white shadow-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-gray-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
            </svg>
            <span class="text-xl font-medium text-gray-500">还没有分享过趣事，分享一个？</span>
            <button @click="router.push('/personal/edit')" class="mt-4 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors duration-300">
              立即分享
            </button>
          </div>
          <div v-else class="w-full h-fit space-y-6">
            <div v-for="story in options.story"
                 class="bg-white rounded-xl shadow-md hover:shadow-lg transition-all duration-300  border border-gray-100">
              <!-- 卡片头部 -->
              <div class="p-3 border-b border-gray-100">
                <div class="flex justify-between items-center">
                  <!-- 左侧标题 -->
                  <div class="flex flex-col leading-none">
                    <p class="font-bold text-gray-800 text-2xl">{{ story.title }}</p>
                    <div class="flex items-center gap-1.5 text-sm text-gray-500 ">
                      <span>作者：{{ userStore.user.username }}</span>
                      <span class="text-gray-300">|</span>
                      <span>发表时间：{{ formatDateTime(story.time) }}</span>
                    </div>
                  </div>
                  <!-- 右侧头像 -->
                  <a-avatar :size="36" :src="userStore.user.avatar" class="ring-1 ring-blue-100"></a-avatar>
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
                    <button class="flex items-center gap-2 text-gray-600 hover:text-blue-500 transition-colors duration-200">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" :class="{'text-blue-500': story.isLike}" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                      </svg>
                      <span class="text-sm">{{ story.likes }}</span>
                    </button>

                    <!-- 收藏按钮 -->
                    <button class="flex items-center gap-2 text-gray-600 hover:text-yellow-500 transition-colors duration-200">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" :class="{'text-yellow-500': story.isFavourite}" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"/>
                      </svg>
                      <span class="text-sm">{{ story.favourites }}</span>
                    </button>
                  </div>

                  <!-- 分享按钮 -->
                  <button class="flex items-center gap-2 text-gray-600 hover:text-green-500 transition-colors duration-200">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z"/>
                    </svg>
                    <span class="text-sm">分享</span>
                  </button>
                </div>
              </div>
            </div>
          </div>

        </div>
        <div v-else-if="SelectFunction === 3">
          <!-- 群聊的内容 -->
          <div class="bg-white rounded-xl shadow-lg p-6">
            <div class="flex justify-between items-center mb-6">
              <h2 class="text-2xl font-bold text-gray-800">我的群组</h2>
              <span class="text-sm text-gray-500">共 {{ options.group.length }} 个群组</span>
            </div>

            <div v-if="options.group.length === 0" class="flex flex-col items-center justify-center py-12">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-gray-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
              <span class="text-xl font-medium text-gray-500">还没有加入任何群组</span>
            </div>

            <div v-else class="space-y-4">
              <div v-for="group in options.group" :key="group.groupId"
                   class="flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-all duration-300">
                <div class="flex items-center space-x-4">
                  <img :src="group.avatar" :alt="group.groupName"
                       class="w-12 h-12 rounded-full object-cover border-2 border-blue-100">
                  <div>
                    <h3 class="text-lg font-semibold text-gray-800">{{ group.groupName }}</h3>
                    <p class="text-sm text-gray-500">加入时间：{{ formatDateTime(group.time) }}</p>
                  </div>
                </div>
                <div class="flex items-center space-x-3">
                  <button @click="router.push(`/chat/${group.groupId}`)"
                          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors duration-300">
                    退出群聊
                  </button>
                  <button class="p-2 text-gray-500 hover:text-gray-700 transition-colors duration-300">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                      <path d="M10 6a2 2 0 110-4 2 2 0 010 4zM10 12a2 2 0 110-4 2 2 0 010 4zM10 18a2 2 0 110-4 2 2 0 010 4z" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-else-if="SelectFunction === 4">
          <!-- 数据统计的内容 -->
          <div class="flex flex-col items-center justify-center py-12 bg-white shadow-lg">
            <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 48 48"><g fill="currentColor" class="text-blue-300">
              <path d="M24 6C14.059 6 6 14.059 6 24c0 9.94 8.059 18 18 18s18-8.06 18-18c0-1.799-.264-3.535-.755-5.174l-1.916.575c.437 1.457.671 3 .671 4.599c0 8.836-7.163 16-16 16S8 32.836 8 24S15.163 8 24 8z"/><path d="M18.164 15.317a1 1 0 0 1 .366-1.366l2.669-1.54a1 1 0 0 1 1.492.985l-.451 3.75l1.27-.733a1 1 0 1 1 1 1.732l-3.003 1.734a1 1 0 0 1-1.492-.986l.451-3.75l-.936.54a1 1 0 0 1-1.366-.366m8.36-7.247a1 1 0 1 0 .518 1.93l1.932-.517l-2.049 5.105a1 1 0 0 0 1.187 1.338l4.347-1.165a1 1 0 1 0-.518-1.932l-2.415.647l2.049-5.104a1 1 0 0 0-1.187-1.338zM35.556 7a1 1 0 0 1 1-1H41a1 1 0 0 1 .848 1.53L37.804 14H41a1 1 0 1 1 0 2h-5a1 1 0 0 1-.848-1.53L39.196 8h-2.64a1 1 0 0 1-1-1M14.404 28.648c-.773-.245-1.44-.8-1.516-1.543a.5.5 0 0 1 .545-.548c1.93.181 3.781-.748 5.075-2.86a.5.5 0 0 1 .843-.015c.402.605.442 1.557.152 2.425a3.8 3.8 0 0 1-2.132 2.295c-1.112.481-2.16.503-2.967.246m17.336-4.645c.546-.599.846-1.412.54-2.094a.5.5 0 0 0-.745-.203c-1.582 1.123-3.65 1.244-5.825.062a.5.5 0 0 0-.738.408c-.045.725.396 1.569 1.08 2.176a3.8 3.8 0 0 0 2.995.922c1.203-.14 2.121-.644 2.692-1.27"/><path fill-rule="evenodd" d="M32.448 31.855c-2.251-3.085-5.587-4.715-8.707-3.879s-5.194 3.916-5.601 7.713c-.115 1.069.923 1.823 1.961 1.545l11.42-3.06c1.039-.279 1.56-1.45.927-2.32m-8.19-1.947c1.894-.507 4.215.306 6.09 2.51L20.24 35.126c.521-2.846 2.125-4.71 4.018-5.218" clip-rule="evenodd"/></g>
            </svg>
            <span class="text-xl font-medium text-gray-500">累了，不想写了(就是这么任性)</span>
          </div>
        </div>
      </div>
    </div>
  </div>
  <RouterView />
</template>

<style scoped>
/* 添加一些自定义动画 */
.hover\:scale-105:hover {
  transform: scale(1.05);
}

.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 300ms;
}
</style>

