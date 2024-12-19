import { createRouter, createWebHashHistory } from 'vue-router'

// 制定路由规则
const routes = [
    {
        path: '/',
        name: 'main',
        component: () => import('@/views/Main.vue')
    }
]

const router = createRouter({
    //创建路由模式
    history: createWebHashHistory(),
    routes,
})

export default router 