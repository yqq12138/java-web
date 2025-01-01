import { createRouter, createWebHistory } from 'vue-router'
import Home from './pages/Home.vue'
import Category from './pages/Category.vue'
import SearchResults from './pages/SearchResults.vue'
import Order from './pages/Order.vue'
import Cart from './pages/Cart.vue'
import ProductDetail from './pages/ProductDetail.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/category/:category', name: 'Category', component: Category },
  { path: '/search', name: 'SearchResults', component: SearchResults },
  { path: '/order', name: 'Order', component: Order },
  { path: '/cart', name: 'Cart', component: Cart },
  { path: '/product/:id', name: 'ProductDetail', component: ProductDetail }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

