<template>
  <div class="search-box">
    <input 
      type="text" 
      v-model="keyword" 
      @keyup.enter="handleSearch"
      placeholder="搜索商品..."
      class="search-input"
    />
    <button @click="handleSearch" class="search-button">
      搜索
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const keyword = ref('')

const handleSearch = () => {
  if (keyword.value.trim()) {
    router.push({
      path: '/search',
      query: { keyword: keyword.value.trim() }
    })
    keyword.value = ''
  }
}
</script>

<style scoped>
.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-input {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 200px;
  font-size: 14px;
}

.search-input:focus {
  outline: none;
  border-color: #ff6600;
}

.search-button {
  padding: 6px 12px;
  background: #ff6600;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.search-button:hover {
  background: #ff8533;
}
</style> 