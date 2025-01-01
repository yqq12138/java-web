<script>
import { API_ADVERTISING_BASE_URL } from "../config/api.config";
import { trackUserBehavior } from '../utils/tracking';

export default {
  name: 'Ad',
  data() {
    return {
      ads: [],
      adsLoading: false
    }
  },
  methods: {
    handleAdClick(ad) {
      trackUserBehavior('ad_click', {
        category: ad.category,
        gender: ad.gender,
        price: ad.price
      })
      this.$router.push({
        path: `/product/${ad.id}`,
        query: {
          name: ad.name,
          price: ad.price,
          image: ad.image?.startsWith('/') 
            ? `http://localhost:8081${ad.image}`
            : ad.image || '/placeholder.svg?height=200&width=200',
          category: ad.category,
          gender: ad.gender
        }
      })
    },
    async loadAds() {
      if (this.adsLoading) return
    
      if (!localStorage.getItem('visitor_id')) {
        const visitorId = 'visitor_' + Date.now()
        localStorage.setItem('visitor_id', visitorId)
      }
      
      try {
        this.adsLoading = true
        const visitorId = localStorage.getItem('visitor_id')
        
        const response = await fetch(`${API_ADVERTISING_BASE_URL}/advertisements/recommend?visitorId=${visitorId}`)
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        
        const result = await response.json()
        
        this.ads = result.map(ad => ({
          id: ad.id,
          name: ad.title,
          price: ad.price,
          image: ad.imageUrl?.startsWith('/') 
            ? `http://localhost:8081${ad.imageUrl}`
            : ad.imageUrl || '/placeholder.svg?height=200&width=200',
          category: ad.category,
          gender: ad.gender
        }))
      } catch (error) {
        console.error('加载广告失败:', error)
        this.ads = []
      } finally {
        this.adsLoading = false
      }
    }
  },
  mounted() {
    this.loadAds()
  }
}
</script>

<template>
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
</template>

<style scoped>
@import "@/assets/css/ad.css";
</style> 