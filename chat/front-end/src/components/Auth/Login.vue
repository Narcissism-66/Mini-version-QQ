<template>
  <contextHolder/>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-indigo-100 to-white py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 bg-white p-8 rounded-xl shadow-2xl">
      <div class="text-center">
        <h2 class="mt-6 text-4xl font-extrabold text-gray-900 bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent">
          欢迎登录
        </h2>
        <p class="mt-2 text-sm text-gray-600">
          或
          <a @click="showRegisterModal" class="font-medium text-indigo-600 hover:text-indigo-500 cursor-pointer">
            注册新账户
          </a>
        </p>
      </div>
      <div class="mt-8 space-y-6">
        <div class="rounded-md shadow-sm space-y-4">
          <div>
            <label for="email" class="block text-sm font-medium text-gray-700 mb-1">账号</label>
            <input
              id="email"
              v-model="form.account"
              name="email"
              type="text"
              autocomplete="email"
              required
              class="appearance-none relative block w-full px-4 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm transition duration-150 ease-in-out"
              placeholder="请输入邮箱地址"
            />
          </div>
          <div>
            <label for="password" class="block text-sm font-medium text-gray-700 mb-1">密码</label>
            <input
              id="password"
              v-model="form.password"
              name="password"
              type="password"
              autocomplete="current-password"
              required
              class="appearance-none relative block w-full px-4 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm transition duration-150 ease-in-out"
              placeholder="请输入密码"
            />
          </div>
        </div>

        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <input
              id="remember-me"
              v-model="form.remember"
              name="remember-me"
              type="checkbox"
              class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
            />
            <label for="remember-me" class="ml-2 block text-sm text-gray-900">
              记住我
            </label>
          </div>

          <div class="text-sm">
            <a href="#" class="font-medium text-indigo-600 hover:text-indigo-500">
              忘记密码?
            </a>
          </div>
        </div>

        <div @click="handleSubmit">
          <button
            type="submit"
            class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-700 hover:to-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transform transition duration-150 ease-in-out hover:scale-[1.02]"
            :disabled="loading"
          >
            <span class="absolute left-0 inset-y-0 flex items-center pl-3 mx-auto">
            </span>
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {reactive, ref} from 'vue'
import {get, post} from "@/net/index.js";
import { message, Modal } from 'ant-design-vue';
import {userUserStore} from "@/stores/userStore.js";
import router from "@/router/index.js";
const [messageApi, contextHolder] = message.useMessage();

const form = reactive({
  account: '',
  password: '',
  remember: false
})

const loading = ref(false)

const showRegisterModal = () => {
  Modal.info({
    title: '注册提示',
    content: '如需注册账号，请联系管理员：admin@example.com',
    okText: '我知道了',
    centered: true,
  });
}

const handleSubmit = async () => {
  post("api/auth/login",{
    account:form.account,
    password:form.password,
  },async (message, data) => {
    messageApi.success(message)
    await localStorage.setItem('authToken', data)
    GetUserInfo();
    await router.push("/");
  })
}
const userStore = userUserStore()
const GetUserInfo = ()=> {
  get('api/user/information', {},(message,data)=>{
    userStore.login(data)
  })
}
</script>
