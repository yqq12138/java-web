<template>
  <Layout>
    <div class="order-page">
      <h1>我的订单</h1>
      
      <div v-if="currentOrder" class="current-order">
        <h2>当前订单</h2>
        <div class="order-items">
          <div v-for="item in currentOrder.items" :key="item.id" class="order-item">
            <img :src="item.image" :alt="item.name" class="item-image" />
            <div class="item-info">
              <h3>{{ item.name }}</h3>
              <p class="item-price">¥{{ item.price }} × {{ item.quantity }}</p>
            </div>
            <div class="item-total">
              ¥{{ item.price * item.quantity }}
            </div>
          </div>
          
          <div class="order-summary">
            <div class="total">总计: ¥{{ currentOrder.total }}</div>
            <div class="order-status">订单状态: {{ currentOrder.status }}</div>
            <button class="pay-button" @click="payOrder" v-if="currentOrder.status === '待支付'">
              立即支付
            </button>
          </div>
        </div>
      </div>

      <div class="order-history">
        <h2>历史订单</h2>
        <div v-if="orderHistory.length > 0" class="history-list">
          <div v-for="order in orderHistory" :key="order.id" class="history-item">
            <div class="order-header">
              <span>订单号: {{ order.id }}</span>
              <span>下单时间: {{ order.createTime }}</span>
              <span class="status">{{ order.status }}</span>
            </div>
            <div class="order-items">
              <div v-for="item in order.items" :key="item.id" class="order-item">
                <img :src="item.image" :alt="item.name" class="item-image" />
                <div class="item-info">
                  <h3>{{ item.name }}</h3>
                  <p class="item-price">¥{{ item.price }} × {{ item.quantity }}</p>
                </div>
                <div class="item-total">
                  ¥{{ item.price * item.quantity }}
                </div>
              </div>
            </div>
            <div class="order-footer">
              <div class="total">总计: ¥{{ order.total }}</div>
            </div>
          </div>
        </div>
        <div v-else class="empty-history">
          暂无历史订单
        </div>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Layout from '../components/Layout.vue'

const currentOrder = ref(null)
const orderHistory = ref([])

// 创建新订单
const createOrder = () => {
  // 从购物车获取商品
  const cartItems = JSON.parse(localStorage.getItem('cart') || '[]')
  if (cartItems.length === 0) return
  
  const total = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0)
  
  // 创建订单
  const order = {
    id: 'ORD' + Date.now(),
    items: cartItems,
    total: total,
    status: '待支付',
    createTime: new Date().toLocaleString()
  }
  
  // 保存订单
  currentOrder.value = order
  
  // 清空购物车
  localStorage.setItem('cart', '[]')
  
  // 保存到历史订单
  const history = JSON.parse(localStorage.getItem('orderHistory') || '[]')
  history.unshift(order)
  localStorage.setItem('orderHistory', JSON.stringify(history))
}

// 支付订单
const payOrder = () => {
  if (currentOrder.value) {
    currentOrder.value.status = '已支付'
    // 更新历史订单中的状态
    const history = JSON.parse(localStorage.getItem('orderHistory') || '[]')
    const orderIndex = history.findIndex(o => o.id === currentOrder.value.id)
    if (orderIndex !== -1) {
      history[orderIndex].status = '已支付'
      localStorage.setItem('orderHistory', JSON.stringify(history))
    }
    alert('支付成功！')
  }
}

onMounted(() => {
  // 如果是从购物车来的，创建新订单
  if (window.location.search.includes('from=cart')) {
    createOrder()
  }
  
  // 加载历史订单
  orderHistory.value = JSON.parse(localStorage.getItem('orderHistory') || '[]')
})
</script>

<style scoped>
.order-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.current-order, .order-history {
  margin-top: 40px;
}

.order-items {
  margin-top: 20px;
}

.order-item {
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
  color: #666;
  margin-top: 5px;
}

.item-total {
  color: #ff6600;
  font-weight: bold;
}

.order-summary {
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

.order-status {
  color: #666;
  margin-bottom: 10px;
}

.pay-button {
  padding: 10px 30px;
  background: #ff6600;
  color: white;
  border: none;
  cursor: pointer;
}

.history-list {
  margin-top: 20px;
}

.history-item {
  margin-bottom: 30px;
  border: 1px solid #eee;
  border-radius: 4px;
}

.order-header {
  padding: 15px;
  background: #f9f9f9;
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid #eee;
}

.status {
  color: #ff6600;
}

.order-footer {
  padding: 15px;
  text-align: right;
  background: #f9f9f9;
}

.empty-history {
  text-align: center;
  padding: 50px;
  color: #666;
}
</style>

