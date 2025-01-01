<template>
  <Layout>
    <div class="category-page">
      <div class="category-container">
        <div class="category-header">
          <h2 class="category-title">{{ categoryMap[route.params.category] || '全部商品' }}</h2>
          <div class="category-filters">
            <select v-model="selectedGender" class="filter-select">
              <option value="">全部性别</option>
              <option value="MALE">男士</option>
              <option value="FEMALE">女士</option>
              <option value="ALL">通用</option>
            </select>
            <select v-model="sortBy" class="filter-select">
              <option value="default">默认排序</option>
              <option value="priceAsc">价格从低到高</option>
              <option value="priceDesc">价格从高到低</option>
            </select>
          </div>
        </div>

        <div class="main-content">
          <div class="products-section">
            <div v-if="loading" class="products-grid">
              <div v-for="i in 20" :key="i" class="product-card skeleton">
                <div class="product-image skeleton-image"></div>
                <div class="product-info">
                  <div class="skeleton-text"></div>
                  <div class="skeleton-price"></div>
                </div>
              </div>
            </div>
            
            <div v-else-if="filteredProducts.length === 0" class="no-products">
              <p>暂无商品</p>
            </div>
            
            <div v-else class="products-grid">
              <div 
                v-for="product in filteredProducts" 
                :key="product.id" 
                class="product-card"
                @click="viewProduct(product)"
              >
                <div class="product-image">
                  <img :src="product.imageUrl" :alt="product.title" />
                </div>
                <div class="product-info">
                  <h3 class="product-name">{{ product.title }}</h3>
                  <p class="price">¥{{ product.price.toFixed(2) }}</p>
                </div>
              </div>
            </div>
          </div>

          <aside class="personalized-ads">
            <h3>个性化推荐</h3>
            <div v-if="ads.length === 0" class="no-ads">
              <div class="loading-spinner"></div>
              <p>正在加载推荐...</p>
            </div>
            <div 
              v-for="ad in ads" 
              :key="ad.id" 
              class="ad-item"
              @click="handleAdClick(ad)"
            >
              <div class="ad-image">
                <img :src="ad.image" :alt="ad.name" />
              </div>
              <div class="ad-info">
                <h4>{{ ad.name }}</h4>
                <p class="price">¥{{ ad.price.toFixed(2) }}</p>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Layout from '../components/Layout.vue'
import { trackUserBehavior } from '../utils/tracking'
import {API_ADVERTISING_BASE_URL, API_SHOPPING_BASE_URL} from "../config/api.config";

const route = useRoute()
const router = useRouter()
const products = ref([])
const ads = ref([])
const loading = ref(false)
const adsLoading = ref(false)
const selectedGender = ref('')
const sortBy = ref('default')
const cartCount = ref(0)
const productsCache = ref(new Map())  // 用于缓存不同类目的商品数据

const categoryMap = {
  'digital': '数码产品',
  'accessories': '配饰',
  'beauty': '美妆护肤',
  'shoes': '鞋类',
  'clothing': '服装'
}

const genderMap = {
  'MALE': '男士',
  'FEMALE': '女士',
  'ALL': '通用'
}

const filteredProducts = computed(() => {
  let result = [...products.value]
  
  // 性别筛选
  if (selectedGender.value) {
    result = result.filter(p => p.gender === selectedGender.value)
  }
  
  // 排序
  switch (sortBy.value) {
    case 'priceAsc':
      result.sort((a, b) => a.price - b.price)
      break
    case 'priceDesc':
      result.sort((a, b) => b.price - a.price)
      break
    case 'newest':
      result.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      break
    default:
      // 默认排序
      break
  }
  
  // 只返回前20个产品
  return result.slice(0, 20)
})

const loadProducts = async () => {
  if (loading.value) return
  
  const category = route.params.category
  
  // 检查缓存中是否有数据
  if (productsCache.value.has(category)) {
    console.log('使用缓存数据:', category)
    products.value = productsCache.value.get(category)
    return
  }
  
  try {
    loading.value = true
    console.log('加载分类商品:', category)
    
    const response = await fetch(`${API_SHOPPING_BASE_URL}/products/category/${category}`)
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const result = await response.json()
    console.log('获取到的商品数据:', result)
    
    const processedProducts = result.map(product => ({
      id: product.id,
      title: product.title,
      price: product.price,
      imageUrl: product.imageUrl?.startsWith('/') 
        ? `http://localhost:8080${product.imageUrl}`//todo 改为实际的购物系统域名
        : product.imageUrl || '/placeholder.svg?height=200&width=200',
      category: product.category,
      gender: product.gender
    }))
    
    // 存入缓存
    productsCache.value.set(category, processedProducts)
    products.value = processedProducts
  } catch (error) {
    console.error('加载商品失败:', error)
    products.value = []
  } finally {
    loading.value = false
  }
}

// 添加防抖函数
const debounce = (fn, delay) => {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

// 修改广告加载逻辑
const loadAds = debounce(async () => {
  if (adsLoading.value) {
    console.log('广告正在加载中，跳过重复请求')
    return
  }

  if (!localStorage.getItem('visitor_id')) {
    const visitorId = 'visitor_' + Date.now()
    localStorage.setItem('visitor_id', visitorId)
  }
  
  try {
    adsLoading.value = true
    const visitorId = localStorage.getItem('visitor_id')
    console.log('当前访客ID:', visitorId)
    
    const response = await fetch(`${API_ADVERTISING_BASE_URL}/advertisements/recommend?visitorId=${visitorId}`)
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const result = await response.json()
    console.log('获取到的广告数据:', result)
    
    if (result && result.length > 0) {
      ads.value = result.map(ad => ({
        id: ad.id,
        name: ad.title,
        price: ad.price,
        image: ad.imageUrl?.startsWith('/') 
          ? `http://localhost:8080${ad.imageUrl}`//todo 改为实际的购物系统域名
          : ad.imageUrl || '/placeholder.svg?height=200&width=200',
        category: ad.category,
        gender: ad.gender
      }))
    }
  } catch (error) {
    console.error('加载广告失败:', error)
  } finally {
    adsLoading.value = false
  }
}, 300)  // 300ms 的防抖延迟

const handleAdClick = (ad) => {
  trackUserBehavior('ad_click', {
    category: ad.category,
    gender: ad.gender,
    price: ad.price
  })
  // 通过 query 参数传递完整的广告商品信息
  router.push({
    path: `/product/${ad.id}`,
    query: {
      name: ad.name,
      price: ad.price,
      image: ad.image?.startsWith('/') 
        ? `http://localhost:8081${ad.image}`//todo修改为实际的广告系统域名
        : ad.image || '/placeholder.svg?height=200&width=200',
      category: ad.category,
      gender: ad.gender
    }
  })
}

const viewProduct = (product) => {
  trackUserBehavior('click_product', {
    category: product.category,
    gender: product.gender,
    price: product.price
  })
  // 通过 query 参数传递完整的商品信息
  router.push({
    path: `/product/${product.id}`,
    query: {
      name: product.title,
      price: product.price,
      image: product.imageUrl?.startsWith('/') 
        ? `http://localhost:8080${product.imageUrl}`//todo 改为实际的购物系统域名
        : product.imageUrl || '/placeholder.svg?height=200&width=200',
      category: product.category,
      gender: product.gender
    }
  })
}

const getCartCount = async () => {
  try {
    const response = await fetch(`${API_SHOPPING_BASE_URL}/cart/count`)
    if (response.ok) {
      const data = await response.json()
      cartCount.value = data.count
    }
  } catch (error) {
    console.error('获取购物车数量失败:', error)
  }
}

watch(
  () => route.params.category,
  (newCategory) => {
    console.log('路由参数变化:', newCategory)
    if (newCategory) {
      loadProducts(newCategory)
      loadAds()
    } else {
      loadProducts('all')
      loadAds()
    }
  },
  { immediate: true }
)

onMounted(() => {
  const category = route.params.category
  if (category) {
    loadProducts(category)
  } else {
    loadProducts('all')
  }
  loadAds()
  getCartCount()
})

// 在组件卸载时清理缓存
onUnmounted(() => {
  productsCache.value.clear()
})
</script>

<style scoped>
.category-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 0 0 20px;
}

.category-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.main-content {
  display: flex;
  gap: 20px;
}

.products-section {
  flex: 1;
  min-width: 0;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.category-title {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.category-filters {
  display: flex;
  gap: 15px;
}

.filter-select {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  color: #666;
  background-color: white;
  cursor: pointer;
}

.filter-select:hover {
  border-color: #ff6600;
}

.products-grid {
  display: flex;
  flex-wrap: wrap;
  margin: -8px;
}

.product-card {
  width: calc(20% - 16px);
  margin: 8px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 2px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.product-image {
  position: relative;
  padding-top: 100%;
  background: #f8f8f8;
}

.product-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 8px;
}

.product-name {
  margin: 0;
  font-size: 12px;
  color: #333;
  line-height: 1.4;
  height: 34px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.price {
  color: #ff6600;
  font-size: 16px;
  font-weight: 600;
  margin: 4px 0 0;
}

.price::before {
  content: '¥';
  font-size: 12px;
  margin-right: 2px;
}

.personalized-ads {
  width: 190px;
  flex-shrink: 0;
  background: #fff;
  padding: 12px;
  border-radius: 4px;
}

.personalized-ads h3 {
  font-size: 14px;
  margin: 0 0 12px;
  color: #333;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

.ad-item {
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.ad-item:hover {
  transform: translateY(-2px);
}

.ad-image {
  position: relative;
  padding-top: 100%;
  background: #f8f8f8;
  border-radius: 2px;
  overflow: hidden;
}

.ad-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.ad-info {
  padding: 6px 0;
}

.ad-info h4 {
  margin: 0;
  font-size: 12px;
  color: #333;
  line-height: 1.4;
  height: 32px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.ad-info .price {
  font-size: 14px;
  margin-top: 4px;
}

@media (max-width: 1200px) {
  .product-card {
    width: calc(25% - 16px);
  }
}

@media (max-width: 992px) {
  .product-card {
    width: calc(33.333% - 16px);
  }
}

@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }
  
  .personalized-ads {
    width: 100%;
    margin-top: 20px;
  }
  
  .product-card {
    width: calc(50% - 16px);
  }
  
  .category-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}

.top-nav {
  background: #fff;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  height: 40px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.nav-links {
  display: flex;
  gap: 20px;
}

.nav-link {
  display: flex;
  align-items: center;
  color: #666;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
  position: relative;
}

.nav-link:hover {
  color: #ff6600;
}

.nav-link i {
  margin-right: 4px;
  font-size: 16px;
}

.cart-count {
  position: absolute;
  top: -8px;
  right: -12px;
  background: #ff6600;
  color: #fff;
  font-size: 12px;
  padding: 0 6px;
  border-radius: 10px;
  min-width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.skeleton {
  background: #f5f5f5;
  border: none;
  pointer-events: none;
}

.skeleton-image {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-text {
  height: 16px;
  margin: 8px 0;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-price {
  height: 24px;
  width: 60%;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>


