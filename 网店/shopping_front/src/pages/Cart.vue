<template>
  <Layout>
    <div class="cart-page">
      <h1>购物车</h1>
      
      <div v-if="cartItems.length > 0" class="cart-items">
        <div v-for="item in cartItems" :key="item.id" class="cart-item">
          <img :src="item.image" :alt="item.name" class="item-image" />
          <div class="item-info">
            <h3>{{ item.name }}</h3>
            <p class="item-price">¥{{ item.price }}</p>
          </div>
          <div class="item-quantity">
            <button @click="updateQuantity(item, -1)">-</button>
            <span>{{ item.quantity }}</span>
            <button @click="updateQuantity(item, 1)">+</button>
          </div>
          <div class="item-total">
            ¥{{ item.price * item.quantity }}
          </div>
          <button class="remove-item" @click="removeItem(item)">删除</button>
        </div>
        
        <div class="cart-summary">
          <div class="total">总计: ¥{{ total }}</div>
          <button class="checkout" @click="checkout">结算</button>
        </div>
      </div>
      
      <div v-else class="empty-cart">
        购物车是空的，去 <router-link to="/">首页</router-link> 看看吧
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Layout from '../components/Layout.vue'

const router = useRouter()
const cartItems = ref([])

// 计算总价
const total = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

// 更新商品数量
const updateQuantity = (item, change) => {
  const index = cartItems.value.findIndex(i => i.id === item.id)
  if (index > -1) {
    const newQuantity = cartItems.value[index].quantity + change
    if (newQuantity > 0) {
      cartItems.value[index].quantity = newQuantity
      // 追踪购物车数量更新行为
      trackUserBehavior('update_cart_quantity', {
        category: item.category,
        gender: item.gender,
        price: item.price,
        quantity: newQuantity
      })
    } else {
      removeItem(item)
    }
    saveCart()
  }
}

// 移除商品
const removeItem = (item) => {
  // 追踪移除购物车行为
  trackUserBehavior('remove_from_cart', {
    category: item.category,
    gender: item.gender,
    price: item.price
  })
  
  cartItems.value = cartItems.value.filter(i => i.id !== item.id)
  saveCart()
}

// 保存购物车
const saveCart = () => {
  localStorage.setItem('cart', JSON.stringify(cartItems.value))
}

// 结算
const checkout = () => {
  router.push({ 
    name: 'Order',
    query: { from: 'cart' }
  })
}

onMounted(() => {
  // 加载购物车数据
  cartItems.value = JSON.parse(localStorage.getItem('cart') || '[]')
})
</script>

<style scoped>
.cart-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.cart-items {
  margin-top: 20px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
  gap: 20px;
}

.item-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
}

.item-info {
  flex: 1;
}

.item-price {
  color: #ff6600;
  font-weight: bold;
}

.item-quantity {
  display: flex;
  align-items: center;
  gap: 10px;
}

.item-quantity button {
  padding: 5px 10px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
}

.remove-item {
  padding: 5px 10px;
  background: #ff4444;
  color: white;
  border: none;
  cursor: pointer;
}

.cart-summary {
  margin-top: 20px;
  text-align: right;
  padding: 20px;
  background: #f9f9f9;
}

.total {
  font-size: 1.2em;
  font-weight: bold;
  margin-bottom: 10px;
}

.checkout {
  padding: 10px 30px;
  background: #ff6600;
  color: white;
  border: none;
  cursor: pointer;
}

.empty-cart {
  text-align: center;
  padding: 50px;
  color: #666;
}
</style>

