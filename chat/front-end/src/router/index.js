import { createRouter, createWebHistory } from 'vue-router'
import {userUserStore} from "@/stores/userStore.js";
import {get} from "@/net/index.js";



const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/auth',
      name: 'auth',
      component: () => import('@/components/Auth/Login.vue'),
    },
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/Home.vue'),
    },
  ],
})
//修改网页名
router.beforeEach((to, from, next)=>
{
  const userStore = userUserStore()
  const publicMap = new Map()
  publicMap.set('/auth', 1)
  // publicMap.set('/share', 2)
  publicMap.set('/error/401', 3)
  publicMap.set('/error/404', 4)
  // 检查要访问的路径是否是根路径
  if (to.matched.length === 0) next('/error/404')

  if (!publicMap.has(to.path)) {
    // 不是访问根路径，检查用户状态
    const user = userStore.user; // 假设你的用户状态保存在Vuex的`user`状态中
    console.log(user)
    if (user === null) {
      get('api/user/information', {},
        (message, data) => {
          userStore.login(data);
          next();
        }, (message, data) => {
          next('/auth')
        }, (message, data) => {
          next('/auth')
        }
      )
    } else {
      // 用户已登录，允许路由继续
      next();
    }
  }
  else {
    // 访问的是根路径或其他公开路径，直接放行
    next();
  }
})


export default router
