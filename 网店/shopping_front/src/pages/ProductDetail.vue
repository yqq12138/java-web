<template>
  <Layout>
    <div class="product-detail">
      <div class="detail-container">
        <div class="product-left">
          <div class="product-main">
            <div class="product-image">
              <img :src="'/placeholder.svg?height=400&width=400&text=' + encodeURIComponent(product.name)" :alt="product.name" />
            </div>
            <div class="product-info">
              <h1 class="product-name">{{ product.name }}</h1>
              <div class="product-meta">
                <span class="tag gender-tag" :class="product.gender?.toLowerCase()">
                  {{ genderMap[product.gender] || '通用' }}
                </span>
                <span class="tag category-tag">
                  {{ categoryMap[product.category] || product.category }}
                </span>
              </div>
              <div class="price">¥{{ product.price }}</div>
              <div class="actions">
                <button class="btn-return" @click="goBack">返回</button>
                <button class="add-to-cart" @click="addToCart">加入购物车</button>
              </div>
            </div>
          </div>
          <div class="product-details">
            <h2>商品详情</h2>
            <div class="detail-images">
              <img src="/placeholder.svg?height=800&width=800&text=商品展示图1" alt="详情图1" />
              <img src="/placeholder.svg?height=800&width=800&text=商品展示图2" alt="详情图2" />
              <img src="/placeholder.svg?height=800&width=800&text=商品参数说明" alt="参数说明" />
              <img src="/placeholder.svg?height=800&width=800&text=尺寸说明" alt="尺寸说明" />
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
              <p class="price">¥{{ ad.price }}</p>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Layout from '../components/Layout.vue'
import { trackUserBehavior } from '../utils/tracking'
import {API_ADVERTISING_BASE_URL} from "../config/api.config";

const route = useRoute()
const router = useRouter()
const ads = ref([])

// 直接使用路由参数创建商品对象
const product = ref({
  id: route.params.id,
  name: route.query.name,
  price: Number(route.query.price),
  image: route.query.image,
  category: route.query.category,
  gender: route.query.gender
})

// 添加路由监听
watch(
  () => route.params.id,
  (newId) => {
    if (newId) {
      // 更新商品数据
      product.value = {
        id: route.params.id,
        name: route.query.name,
        price: Number(route.query.price),
        image: route.query.image,
        category: route.query.category,
        gender: route.query.gender
      }
      // 记录商品浏览行为
      trackUserBehavior('view_product', {
        category: product.value.category,
        gender: product.value.gender,
        price: product.value.price
      })
      // 重新加载广告
      loadAds()
    }
  }
)

const genderMap = {
  'MALE': '男士',
  'FEMALE': '女士',
  'ALL': '通用'
}

const categoryMap = {
  'digital': '数码',
  'accessories': '配饰',
  'beauty': '美妆',
  'shoes': '鞋类',
  'clothing': '服装'
}

const goBack = () => {
  router.back()
}

const addToCart = () => {
  // 获取现有购物车数据
  let cart = JSON.parse(localStorage.getItem('cart') || '[]')
  
  // 检查商品是否已在购物车中
  const existingItem = cart.find(item => item.id === product.value.id)
  if (existingItem) {
    existingItem.quantity += 1
  } else {
    cart.push({
      id: product.value.id,
      name: product.value.name,
      price: product.value.price,
      image: product.value.image,
      quantity: 1
    })
  }
  
  // 保存购物车数据
  localStorage.setItem('cart', JSON.stringify(cart))
  
  // 记录添加购物车行为
  trackUserBehavior('add_to_cart', {
    category: product.value.category,
    gender: product.value.gender,
    price: product.value.price
  })
  
  // 提示用户
  alert('已添加到购物车')
}

const handleAdClick = (ad) => {
  trackUserBehavior('ad_click', {
    category: ad.category,
    gender: ad.gender,
    price: ad.price
  })
  router.push({
    path: `/product/${ad.id}`,
    query: {
      name: ad.name,
      price: ad.price,
      image: ad.image,
      category: ad.category,
      gender: ad.gender
    }
  })
}

const loadAds = async () => {
  try {
    const visitorId = localStorage.getItem('visitor_id')
    console.log('当前访客ID:', visitorId)
    
    const response = await fetch(`${API_ADVERTISING_BASE_URL}/advertisements/recommend?visitorId=${visitorId}`)
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const result = await response.json()
    console.log('获取到的广告数据:', result)
    
    ads.value = result.map(ad => ({
      id: ad.id,
      name: ad.title,
      price: ad.price,
      image: ad.imageUrl?.startsWith('/') 
        ? `http://localhost:8081${ad.imageUrl}`//todo改为实际的广告域名
        : ad.imageUrl || '/placeholder.svg?height=200&width=200',
      category: ad.category,
      gender: ad.gender
    }))
  } catch (error) {
    console.error('加载广告失败:', error)
    ads.value = []
  }
}

onMounted(() => {
  // 记录商品浏览行为
  trackUserBehavior('view_product', {
    category: product.value.category,
    gender: product.value.gender,
    price: product.value.price
  })
  
  // 加载个性化广告
  loadAds()
})
</script>

<style scoped>
.product-detail {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px 0;
}

.detail-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  gap: 20px;
}

.product-left {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-main {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  display: flex;
  gap: 40px;
}

.product-image {
  width: 400px;
  height: 400px;
  background: #f8f8f8;
  border-radius: 4px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 24px;
  color: #333;
  margin: 0 0 16px;
  line-height: 1.4;
}

.product-meta {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
  color: #fff;
}

.gender-tag {
  background: rgba(0, 0, 0, 0.5);
}

.gender-tag.male {
  background: rgba(0, 122, 255, 0.8);
}

.gender-tag.female {
  background: rgba(255, 45, 85, 0.8);
}

.gender-tag.all {
  background: rgba(88, 86, 214, 0.8);
}

.category-tag {
  background: rgba(255, 102, 0, 0.8);
}

.price {
  font-size: 28px;
  color: #ff6600;
  font-weight: 600;
  margin: 20px 0;
}

.actions {
  display: flex;
  gap: 16px;
  margin-top: 30px;
}

button {
  padding: 12px 32px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-return {
  background: #f5f5f5;
  color: #666;
}

.btn-return:hover {
  background: #e8e8e8;
}

.add-to-cart {
  background: #ff6600;
  color: #fff;
}

.add-to-cart:hover {
  background: #ff8533;
}

.personalized-ads {
  width: 240px;
  flex-shrink: 0;
  background: #fff;
  padding: 16px;
  border-radius: 4px;
  position: sticky;
  top: 20px;
  align-self: flex-start;
  max-height: calc(100vh - 40px);
  overflow-y: auto;
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

.no-ads {
  text-align: center;
  padding: 20px;
  color: #666;
}

@media (max-width: 1200px) {
  .detail-container {
    padding: 0 16px;
  }
  
  .product-main {
    padding: 16px;
    gap: 20px;
  }
  
  .product-image {
    width: 300px;
    height: 300px;
  }
}

@media (max-width: 992px) {
  .detail-container {
    flex-direction: column;
  }
  
  .personalized-ads {
    width: 100%;
    margin-top: 20px;
  }
}

@media (max-width: 768px) {
  .product-main {
    flex-direction: column;
  }
  
  .product-image {
    width: 100%;
    height: auto;
    aspect-ratio: 1;
  }
  
  .product-name {
    font-size: 20px;
  }
  
  .price {
    font-size: 24px;
  }
  
  .actions {
    flex-direction: column;
  }
  
  button {
    width: 100%;
  }
}

.product-details {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}

.product-details h2 {
  font-size: 18px;
  color: #333;
  margin: 0 0 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.detail-images {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-images img {
  width: 100%;
  height: auto;
  border-radius: 4px;
  background: #f8f8f8;
}
</style>



