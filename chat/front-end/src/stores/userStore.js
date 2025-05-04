import { defineStore } from "pinia";

/**
 * 用户认证状态存储
 * 管理应用中的用户登录状态及相关操作
 */
export const userUserStore = defineStore('user', {
    // 状态定义
    state: () => ({
        user: null, // 当前登录用户信息，null表示未登录
    }),
    // 操作方法
    actions: {
        login(user) {
            this.user = user;
        },
        logout() {
            this.user = null;
        }
    }
});
