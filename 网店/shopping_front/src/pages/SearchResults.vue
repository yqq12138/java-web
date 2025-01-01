<template>
  <Layout>
    <div class="search-results">
      <div class="results-header">
        <h2>搜索结果: "{{ $route.query.keyword }}"</h2>
        <button class="btn-return" @click="goBack">返回</button>
      </div>
      
      <div class="content">
        <div class="products-grid">
          <div 
            v-for="product in searchResults" 
            :key="product.id" 
            class="product-card"
            @click="viewProduct(product)"
          >
            <img :src="product.image" :alt="product.name" />
            <h3>{{ product.name }}</h3>
            <p class="price">¥{{ product.price }}</p>
          </div>
        </div>
        
        <div class="personalized-ads">
          <h3>个性化推荐</h3>
          <div class="ads-container">
            <div 
              v-for="ad in ads" 
              :key="ad.id" 
              class="ad-card"
              @click="handleAdClick(ad)"
            >
              <img :src="ad.imageUrl" :alt="ad.title" />
              <div class="ad-info">
                <h4>{{ ad.title }}</h4>
                <p class="price">¥{{ ad.price }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Layout from '../components/Layout.vue'
import { fetchProducts, fetchAds, trackUserBehavior, searchProducts } from '../api'

const route = useRoute()
const router = useRouter()
const searchResults = ref([])
const ads = ref([])

const performSearch = async (query) => {
  if (!query?.trim()) {
    console.log('搜索关键词为空')
    searchResults.value = []
    return
  }

  try {
    console.log('执行搜索，关键词:', query)
    const result = await searchProducts({ query: query.trim() })
    console.log('搜索返回结果:', result)
    
    // 确保result是数组
    const products = Array.isArray(result) ? result : (result.content || [])
    
    searchResults.value = products.map(product => ({
      id: product.id,
      name: product.title || '',
      title: product.title || '',
      price: product.price || 0,
      image: product.imageUrl?.startsWith('/') 
        ? `http://localhost:8080${product.imageUrl}`//todo 改为实际的购物域名
        : product.imageUrl || '/placeholder.svg?height=200&width=200',
      category: product.category || '',
      gender: product.gender || ''
    }))

    // 追踪搜索行为
    trackUserBehavior('search', {
      keyword: query,
      resultCount: searchResults.value.length
    })
  } catch (error) {
    console.error('搜索失败:', error)
    searchResults.value = []
  }
}

const loadAds = async () => {
  try {
    const result = await fetchAds()
    console.log('获取到的广告数据:', result)
    
    // 简化广告处理逻辑，移除严格的过滤条件
    ads.value = (Array.isArray(result) ? result : [])
      .filter(ad => ad && ad.id) // 只保留有效的广告
      .map(ad => ({
        id: ad.id,
        name: ad.title || '',
        title: ad.title || '',
        price: ad.price || 0,
        imageUrl: ad.imageUrl?.startsWith('/') 
          ? `http://localhost:8081${ad.imageUrl}`//todo 改为实际的广告域名
          : ad.imageUrl || '/placeholder.svg?height=200&width=200',
        category: ad.category || '',
        gender: ad.gender || ''
      }))

    console.log('处理后的广告数据:', ads.value)
  } catch (error) {
    console.error('加载广告失败:', error)
    ads.value = []
  }
}

const goBack = () => {
  router.back()
}

const handleAdClick = (ad) => {
  trackUserBehavior('ad_click', { 
    adId: ad.id,
    searchQuery: route.query.keyword
  })
  // 跳转到详情页
  router.push({
    path: `/product/${ad.id}`,
    query: {
      title: ad.title,
      price: ad.price,
      image: ad.imageUrl,
      category: ad.category || '通用',
      gender: ad.gender || '通用'
    }
  })
}

const viewProduct = (product) => {
  trackUserBehavior('view_product', { 
    productId: product.id,
    fromSearch: true,
    searchQuery: route.query.keyword
  })
  // 跳转到详情页
  router.push({
    path: `/product/${product.id}`,
    query: {
      title: product.name,
      price: product.price,
      image: product.image,
      category: product.category || '通用',
      gender: product.gender || '通用'
    }
  })
}

watch(() => route.query.keyword, (newQuery) => {
  if (newQuery) {
    performSearch(newQuery)
    trackUserBehavior('search_results_view', { query: newQuery })
  }
})

onMounted(() => {
  if (route.query.keyword) {
    performSearch(route.query.keyword)
    loadAds()
    trackUserBehavior('search_results_view', { query: route.query.keyword })
  }
})
</script>

<style scoped>
.search-results {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.btn-return {
  padding: 5px 15px;
  background: #f0f0f0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.content {
  display: grid;
  grid-template-columns: 1fr 250px;
  gap: 20px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.product-card {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: center;
  cursor: pointer;
}

.product-card img {
  width: 100%;
  height: auto;
}

.price {
  color: #ff6600;
  font-weight: bold;
}

.personalized-ads {
  border-left: 1px solid #eee;
  padding-left: 20px;
  width: 250px;
}

.ads-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.ad-card {
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 10px;
  cursor: pointer;
  transition: transform 0.2s;
}

.ad-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.ad-card img {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
}

.ad-info {
  padding: 8px 0;
}

.ad-info h4 {
  margin: 0 0 5px;
  font-size: 14px;
  color: #333;
}
</style>

