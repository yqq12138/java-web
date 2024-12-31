// API配置文件
const config = {
    // 开发环境API地址
    development: {
        shopping: 'http://localhost:8080/api',
        advertising: 'http://localhost:8081/api'
    },

    // 测试环境API地址
    test: {
        shopping: 'http://test-shopping-api.example.com/api',
        advertising: 'http://test-advertising-api.example.com/api'
    },

    // 生产环境API地址
    production: {
        shopping: '/api/shopping',
        advertising: '/api/advertising'
    }
}

// 根据当前环境获取API基础URL
const getBaseUrl = (type) => {
    const env = process.env.NODE_ENV || 'development'
    console.log('当前环境:', env)
    const baseUrl = config[env][type]
    console.log('当前API基础地址:', baseUrl)
    return baseUrl
}

// 导出配置
export const API_SHOPPING_BASE_URL = getBaseUrl('shopping');
export const API_ADVERTISING_BASE_URL = getBaseUrl('advertising');

// 导出其他配置
export const API_CONFIG = {
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
}