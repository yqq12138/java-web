// API endpoints for products, advertisements, and user behavior tracking
import { API_SHOPPING_BASE_URL, API_ADVERTISING_BASE_URL } from '../config/api.config'

/**
 * Fetch products based on category or search query
 * @param {Object} params - The query parameters
 * @param {string} [params.category] - The product category
 * @param {string} [params.query] - The search query
 * @param {number} [params.page=1] - The page number for pagination
 * @param {number} [params.limit=20] - The number of items per page
 * @returns {Promise<Object>} - The product data
 */
export const fetchProducts = async ({ category, page = 1, limit = 20 }) => {
  const response = await fetch(`${API_SHOPPING_BASE_URL}/products?category=${category}&page=${page}&limit=${limit}`)
  return response.json()
}

/**
 * Fetch product details by ID
 * @param {number} productId - The ID of the product
 * @returns {Promise<Object>} - The product details
 */
export const fetchProductDetail = async (productId) => {
  const response = await fetch(`${API_SHOPPING_BASE_URL}/products/${productId}`)
  return response.json()
}

// 搜索相关接口
export const searchProducts = async ({ query, page = 1, limit = 20 }) => {
  try {
    console.log('调用搜索API，参数:', { query, page, limit })
    const url = `${API_SHOPPING_BASE_URL}/products/search?keyword=${encodeURIComponent(query)}&page=${page}&limit=${limit}`
    console.log('请求URL:', url)
    
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Accept': 'application/json'
      }
    })
    
    if (!response.ok) {
      const errorData = await response.json()
      console.error('搜索请求失败:', errorData)
      throw new Error(`搜索请求失败: ${response.status}`)
    }
    
    const data = await response.json()
    console.log('搜索API返回数据:', data)
    return data
  } catch (error) {
    console.error('搜索API调用失败:', error)
    return []
  }
}

// 购物车相关接口
export const fetchCartItems = async () => {
  const response = await fetch(`${API_SHOPPING_BASE_URL}/cart`)
  return response.json()
}

export const addToCart = async (productId, quantity = 1) => {
  const response = await fetch(`${API_SHOPPING_BASE_URL}/cart/add`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ productId, quantity })
  })
  return response.json()
}

export const updateCartItem = async (productId, quantity) => {
  const response = await fetch(`${API_SHOPPING_BASE_URL}/cart/update`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ productId, quantity })
  })
  return response.json()
}

// 订单相关接口
export const fetchOrders = async () => {
  const response = await fetch(`${API_SHOPPING_BASE_URL}/orders`)
  return response.json()
}

export const createOrder = async (orderData) => {
  const response = await fetch(`${API_SHOPPING_BASE_URL}/orders/create`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(orderData)
  })
  return response.json()
}

// 广告相关接口
export const fetchAds = async () => {
  try {
    const response = await fetch(`${API_ADVERTISING_BASE_URL}/advertisements`, {
      timeout: 5000,  // 5秒超时
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const data = await response.json();
    return data.content || [];
  } catch (error) {
    console.error('获取广告数据失败:', error);
    // 如果是网络错误，尝试重试
    if (error.name === 'TypeError' || error.message === 'Failed to fetch') {
      console.log('网络错误，3秒后重试...');
      await new Promise(resolve => setTimeout(resolve, 3000));
      return fetchAds();  // 重试
    }
    return [];
  }
}

// 获取所有广告
export const getAllAds = async () => {
  try {
    const response = await fetch(`${API_ADVERTISING_BASE_URL}/advertisements/all`, {
      timeout: 5000
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('获取所有广告失败:', error);
    return [];
  }
}

// 发布广告接口
export const createAdvertisement = async (adData) => {
  const response = await fetch(`${API_ADVERTISING_BASE_URL}/advertisements`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(adData)
  })
  return response.json()
}

// 更新广告接口
export const updateAdvertisement = async (id, adData) => {
  const response = await fetch(`${API_ADVERTISING_BASE_URL}/advertisements/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(adData)
  })
  return response.json()
}

// 删除广告接口
export const deleteAdvertisement = async (id) => {
  const response = await fetch(`${API_ADVERTISING_BASE_URL}/advertisements/${id}`, {
    method: 'DELETE'
  })
  return response.json()
}

// 获取我的广告列表
export const getMyAdvertisements = async (userId, page = 0, size = 10) => {
  const response = await fetch(`${API_ADVERTISING_BASE_URL}/advertisements/my?userId=${userId}&page=${page}&size=${size}`)
  return response.json()
}

// 用户行为追踪接口
export const trackUserBehavior = async (action, data) => {
  try {
    const response = await fetch(`${API_ADVERTISING_BASE_URL}/track`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ 
        action, 
        data, 
        timestamp: new Date().toISOString() 
      })
    })
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    return await response.json()
  } catch (error) {
    console.error('行为追踪失败:', error)
    return null
  }
}

// 商品相关接口
export const api = {
  // 获取商品列表
  getProducts: async (params) => {
    const { category, gender, minPrice, maxPrice } = params
    return await fetch(`${API_SHOPPING_BASE_URL}/products?category=${category}&gender=${gender}&minPrice=${minPrice}&maxPrice=${maxPrice}`)
      .then(res => res.json())
  },

  // 获取商品详情
  getProduct: async (id) => {
    return await fetch(`${API_SHOPPING_BASE_URL}/products/${id}`).then(res => res.json())
  },

  // 获取推荐商品
  getRecommends: async (userId) => {
    return await fetch(`${API_SHOPPING_BASE_URL}/products/recommends?userId=${userId}`).then(res => res.json())
  },

  // 获取相似商品
  getSimilar: async (productId) => {
    return await fetch(`${API_SHOPPING_BASE_URL}/products/similar/${productId}`).then(res => res.json())
  }
}

// 首页商品相关接口
export const getHomeProducts = async ({ page = 1, limit = 20, random = true }) => {
  try {
    const url = `${API_SHOPPING_BASE_URL}/products/category/all?page=${page}&limit=${limit}&random=${random}`
    console.log('请求首页商品URL:', url)
    
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      timeout: 5000
    })
    
    if (!response.ok) {
      console.error('获取首页商品失败, 状态码:', response.status)
      const errorText = await response.text()
      console.error('错误详情:', errorText)
      throw new Error(`获取首页商品失败: ${response.status}`)
    }
    
    const data = await response.json()
    console.log('首页商品数据:', data)
    return Array.isArray(data) ? data.slice(0, limit) : []
  } catch (error) {
    console.error('获取首页商品失败:', error)
    if (error.name === 'TypeError' && error.message === 'Failed to fetch') {
      console.error('无法连接到服务器，请确保后端服务正在运行')
    }
    return []
  }
}

