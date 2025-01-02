// API配置文件 - Vue 2 版本
const config = {
    // 开发环境API地址
    development: {
        shopping: 'http://localhost:8080/api',
        advertising: 'http://10.100.164.30/api'
    },

    // 测试环境API地址
    test: {
        shopping: 'http://test-shopping-api.example.com/api',
        advertising: 'http://10.100.164.30/api'
    },

    // 生产环境API地址
    production: {
        shopping: '/api/shopping',
        advertising: 'http://10.100.164.30/api'
    }
}

// Vue 2 中获取环境变量的方式
const getBaseUrl = (type) => {
    // Vue 2 中使用 process.env.NODE_ENV 获取环境
    const env = process.env.NODE_ENV || 'development'
    
    // 在 Vue 2 项目中，可以通过 Vue.config.devtools 判断是否为开发环境
    if (process.env.VUE_APP_DEBUG === 'true') {
        console.log('当前环境:', env)
        console.log('当前API基础地址:', config[env][type])
    }
    
    return config[env][type]
}

// 导出配置
export const API_SHOPPING_BASE_URL = getBaseUrl('shopping')
export const API_ADVERTISING_BASE_URL = getBaseUrl('advertising')

// 导出其他配置
export const API_CONFIG = {
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
}

// Vue 2 特有的配置
export const VUE2_CONFIG = {
    // 是否启用调试工具
    devtools: process.env.NODE_ENV !== 'production',
    
    // 是否在生产环境下显示警告
    silent: process.env.NODE_ENV === 'production',
    
    // 自定义合并策略
    optionMergeStrategies: {}
} 