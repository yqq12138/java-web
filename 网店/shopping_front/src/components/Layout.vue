<template>
  <div class="layout">
    <header class="header">
      <div class="header-container">
        <nav class="main-nav">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/category/digital" class="nav-item">数码</router-link>
          <router-link to="/category/accessories" class="nav-item">配饰</router-link>
          <router-link to="/category/beauty" class="nav-item">美妆</router-link>
          <router-link to="/category/shoes" class="nav-item">鞋类</router-link>
          <router-link to="/category/clothing" class="nav-item">服装</router-link>
          <router-link to="/order" class="nav-item">订单</router-link>
          <router-link to="/cart" class="nav-item cart">
            购物车
            <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
          </router-link>
        </nav>
      </div>
    </header>
    
    <main class="main">
      <slot></slot>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {API_SHOPPING_BASE_URL} from "../config/api.config";

const cartCount = ref(0)

const getCartCount = async () => {
  try {
    const response = await fetch(`${API_SHOPPING_BASE_URL}/api/cart/count`)
    if (response.ok) {
      const data = await response.json()
      cartCount.value = data.count
    }
  } catch (error) {
    console.error('获取购物车数量失败:', error)
  }
}

onMounted(() => {
  getCartCount()
})
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #ff6600;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-nav {
  display: flex;
  gap: 30px;
  align-items: center;
  justify-content: center;
  width: 100%;
  max-width: 1000px;
}

.nav-item {
  color: #fff;
  text-decoration: none;
  font-size: 16px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
  white-space: nowrap;
}

.nav-item:hover {
  background: rgba(255,255,255,0.1);
}

.nav-item.router-link-active {
  background: rgba(255,255,255,0.2);
}

.cart {
  position: relative;
  margin-left: auto;
}

.cart-badge {
  position: absolute;
  top: -8px;
  right: -12px;
  background: #fff;
  color: #ff6600;
  font-size: 12px;
  padding: 0 6px;
  border-radius: 10px;
  min-width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.main {
  flex: 1;
}

@media (max-width: 768px) {
  .header-container {
    height: auto;
    padding: 10px 20px;
  }
  
  .main-nav {
    flex-wrap: wrap;
    gap: 15px;
    justify-content: center;
  }
  
  .nav-item {
    font-size: 14px;
  }
  
  .cart {
    margin-left: 0;
    order: 1;
  }
}
</style>

