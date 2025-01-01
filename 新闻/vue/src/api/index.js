import request from '@/utils/request'

// 商品相关接口
export const fetchProducts = ({ category, page = 1, limit = 20 }) => {
  return request({
    url: '/products',
    method: 'get',
    params: { category, page, limit }
  })
}

export const fetchProductDetail = (productId) => {
  return request({
    url: `/products/${productId}`,
    method: 'get'
  })
}

// 搜索相关接口
export const searchProducts = ({ query, page = 1, limit = 20 }) => {
  return request({
    url: '/products/search',
    method: 'get',
    params: {
      keyword: query,
      page,
      limit
    }
  }).catch(error => {
    console.error('搜索API调用失败:', error)
    return []
  })
}

// 购物车相关接口
export const fetchCartItems = () => {
  return request({
    url: '/cart',
    method: 'get'
  })
}

export const addToCart = (productId, quantity = 1) => {
  return request({
    url: '/cart/add',
    method: 'post',
    data: { productId, quantity }
  })
}

export const updateCartItem = (productId, quantity) => {
  return request({
    url: '/cart/update',
    method: 'put',
    data: { productId, quantity }
  })
}

// 订单相关接口
export const fetchOrders = () => {
  return request({
    url: '/orders',
    method: 'get'
  })
}

export const createOrder = (orderData) => {
  return request({
    url: '/orders/create',
    method: 'post',
    data: orderData
  })
}

// 广告相关接口
export const fetchAds = () => {
  return request({
    url: '/advertisements',
    method: 'get',
    timeout: 5000
  }).catch(error => {
    console.error('获取广告数据失败:', error)
    return { content: [] }
  })
}

export const getAllAds = () => {
  return request({
    url: '/advertisements/all',
    method: 'get',
    timeout: 5000
  }).catch(error => {
    console.error('获取所有广告失败:', error)
    return []
  })
}

export const createAdvertisement = (adData) => {
  return request({
    url: '/advertisements',
    method: 'post',
    data: adData
  })
}

export const updateAdvertisement = (id, adData) => {
  return request({
    url: `/advertisements/${id}`,
    method: 'put',
    data: adData
  })
}

export const deleteAdvertisement = (id) => {
  return request({
    url: `/advertisements/${id}`,
    method: 'delete'
  })
}

export const getMyAdvertisements = (userId, page = 0, size = 10) => {
  return request({
    url: '/advertisements/my',
    method: 'get',
    params: { userId, page, size }
  })
}

// 用户行为追踪接口
export const trackUserBehavior = (action, data) => {
  return request({
    url: '/track',
    method: 'post',
    data: {
      action,
      data,
      timestamp: new Date().toISOString()
    }
  }).catch(error => {
    console.error('行为追踪失败:', error)
    return null
  })
}

// 商品相关接口
export const api = {
  getProducts: (params) => {
    return request({
      url: '/products',
      method: 'get',
      params
    })
  },

  getProduct: (id) => {
    return request({
      url: `/products/${id}`,
      method: 'get'
    })
  },

  getRecommends: (userId) => {
    return request({
      url: '/products/recommends',
      method: 'get',
      params: { userId }
    })
  },

  getSimilar: (productId) => {
    return request({
      url: `/products/similar/${productId}`,
      method: 'get'
    })
  }
}

// 首页商品相关接口
export const getHomeProducts = ({ page = 1, limit = 20, random = true }) => {
  return request({
    url: '/products/category/all',
    method: 'get',
    params: { page, limit, random },
    timeout: 5000
  }).catch(error => {
    console.error('获取首页商品失败:', error)
    return []
  })
}

