<template>
  <HomeLayout>
    <div class="category-page">
      <div class="category-container">
        <div class="category-header">
          <h2 class="category-title">热门推荐</h2>
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
              <option value="newest">最新上架</option>
            </select>
          </div>
        </div>

        <div class="main-content">
          <div class="products-section">
            <div v-if="loading" class="loading">
              <div class="loading-spinner"></div>
              <p>正在加载商品...</p>
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
                  <img :src="product.image" :alt="product.name" />
                </div>
                <div class="product-info">
                  <h3 class="product-name">{{ product.name }}</h3>
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
  </HomeLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import HomeLayout from '../components/HomeLayout.vue'
import { trackUserBehavior } from '../utils/tracking'
import { getHomeProducts } from '../API'
import {API_ADVERTISING_BASE_URL} from "../config/api.config";

const router = useRouter()
const products = ref([])
const ads = ref([])
const loading = ref(false)
const adsLoading = ref(false)
const selectedGender = ref('')
const sortBy = ref('default')
const currentPage = ref(1)
const pageSize = ref(20)

// Fisher-Yates 洗牌算法
const shuffleArray = (array) => {
  const result = [...array]
  for (let i = result.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [result[i], result[j]] = [result[j], result[i]]
  }
  return result
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
  }
  
  return result
})

const loadProducts = async () => {
  if (loading.value) return
  
  try {
    loading.value = true
    const result = await getHomeProducts({
      page: currentPage.value,
      limit: pageSize.value,
      random: true
    })
    
    products.value = result.map(product => ({
      id: product.id,
      name: product.title,
      price: product.price,
      image: product.imageUrl?.startsWith('/') 
        ? `http://localhost:8080${product.imageUrl}` //todo改为实际的购物域名
        : product.imageUrl || '/placeholder.svg?height=200&width=200',
      category: product.category,
      gender: product.gender,
      createTime: product.createTime
    }))
  } catch (error) {
    console.error('加载商品失败:', error)
    products.value = []
  } finally {
    loading.value = false
  }
}

const loadAds = async () => {
  if (adsLoading.value) return

  if (!localStorage.getItem('visitor_id')) {
    const visitorId = 'visitor_' + Date.now()
    localStorage.setItem('visitor_id', visitorId)
  }
  
  try {
    adsLoading.value = true
    const visitorId = localStorage.getItem('visitor_id')
    
    const response = await fetch(`${API_ADVERTISING_BASE_URL}/advertisements/recommend?visitorId=${visitorId}`)
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const result = await response.json()
    
    ads.value = result.map(ad => ({
      id: ad.id,
      name: ad.title,
      price: ad.price,
      image: ad.imageUrl?.startsWith('/') 
        ? `http://localhost:8081${ad.imageUrl}`//todo 改为实际的广告域名
        : ad.imageUrl || '/placeholder.svg?height=200&width=200',
      category: ad.category,
      gender: ad.gender
    }))
  } catch (error) {
    console.error('加载广告失败:', error)
    ads.value = []
  } finally {
    adsLoading.value = false
  }
}

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
        ? `http://localhost:8081${ad.image}`//todo 改为实际的广告域名
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
      name: product.name,
      price: product.price,
      image: product.image,
      category: product.category,
      gender: product.gender
    }
  })
}

onMounted(() => {
  loadProducts()
  loadAds()
})
</script>

<style scoped>
.category-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px 0;
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
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

.product-card {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 4px;
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
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  height: 40px;
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

.personalized-ads {
  width: 240px;
  flex-shrink: 0;
  background: #fff;
  padding: 16px;
  border-radius: 4px;
}

.personalized-ads h3 {
  font-size: 16px;
  margin: 0 0 16px;
  color: #333;
}

.ad-item {
  margin-bottom: 16px;
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
  border-radius: 4px;
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
  padding: 8px 0;
}

.ad-info h4 {
  margin: 0;
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.loading, .no-products {
  text-align: center;
  padding: 40px;
  color: #666;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  margin: 0 auto 16px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #ff6600;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 1200px) {
  .products-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 992px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
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
  
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .category-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .category-filters {
    width: 100%;
  }
  
  .filter-select {
    flex: 1;
  }
}
</style>

