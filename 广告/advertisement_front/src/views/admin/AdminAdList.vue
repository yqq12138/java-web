<template>
  <div class="admin-ad-list">
    <h2>广告管理</h2>
    
    <el-table :data="advertisements" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="category" label="类别" />
      <el-table-column prop="price" label="价格" />
      <el-table-column prop="gender" label="目标性别" />
      <el-table-column label="图片">
        <template #default="scope">
          <el-image 
            :src="getImageUrl(scope.row.imageUrl)" 
            :preview-src-list="[getImageUrl(scope.row.imageUrl)]"
            style="width: 100px; height: 100px"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button 
            type="primary" 
            size="small" 
            @click="editAd(scope.row)"
          >
            编辑
          </el-button>
          <el-button 
            type="danger" 
            size="small" 
            @click="deleteAd(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible"
    >
      <el-form :model="adForm" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="adForm.title" />
        </el-form-item>
        
        <el-form-item label="类别">
          <el-select v-model="adForm.category">
            <el-option label="电子产品" value="电子产品" />
            <el-option label="服装" value="服装" />
            <el-option label="食品" value="食品" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="价格">
          <el-input-number v-model="adForm.price" :precision="2" :step="0.1" />
        </el-form-item>
        
        <el-form-item label="目标性别">
          <el-select v-model="adForm.gender">
            <el-option label="全部" value="ALL" />
            <el-option label="男" value="MALE" />
            <el-option label="女" value="FEMALE" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'AdminAdList',
  setup() {
    const advertisements = ref([])
    const dialogVisible = ref(false)
    const dialogTitle = ref('编辑广告')
    const adForm = ref({
      title: '',
      category: '',
      price: '',
      gender: '',
      imageUrl: ''
    })

    // 获取广告列表
    const fetchAds = async () => {
      try {
        const response = await fetch('/api/advertisements')
        const data = await response.json()
        advertisements.value = Array.isArray(data) ? data : (data.content || [])
      } catch (error) {
        console.error('获取广告列表失败:', error)
        advertisements.value = []
      }
    }

    // 编辑广告
    const editAd = async (ad) => {
      dialogTitle.value = '编辑广告'
      adForm.value = { ...ad }
      dialogVisible.value = true
    }

    // 删除广告
    const deleteAd = async (id) => {
      try {
        const response = await fetch(`/api/advertisements/${id}`, {
          method: 'DELETE'
        })
        if (response.ok) {
          ElMessage.success('删除成功')
          fetchAds()
        } else {
          throw new Error('删除失败')
        }
      } catch (error) {
        console.error('删除广告失败:', error)
        ElMessage.error('删除广告失败')
      }
    }

    // 提交编辑
    const handleSubmit = async () => {
      try {
        const response = await fetch(`/api/advertisements/${adForm.value.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(adForm.value)
        })

        if (response.ok) {
          ElMessage.success('更新成功')
          dialogVisible.value = false
          fetchAds()
        } else {
          throw new Error('更新失败')
        }
      } catch (error) {
        console.error('更新广告失败:', error)
        ElMessage.error('更新失败')
      }
    }

    // 获取完整图片URL
    const getImageUrl = (url) => {
      if (!url) return '';
      if (url.startsWith('http')) return url;
      return `http://localhost:8080${url}`;
    }

    onMounted(() => {
      fetchAds()
    })

    return {
      advertisements,
      dialogVisible,
      dialogTitle,
      adForm,
      editAd,
      deleteAd,
      handleSubmit,
      getImageUrl
    }
  }
}
</script>

<style scoped>
.admin-ad-list {
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
}
</style> 