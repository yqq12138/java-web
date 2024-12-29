<template>
  <div class="ad-list">
    <div class="header">
      <h2>广告列表</h2>
      <div class="header-right">
        <el-button 
          v-if="isApprovedAdvertiser" 
          type="primary" 
          @click="showCreateDialog"
        >
          发布广告
        </el-button>
        <el-button 
          v-else
          type="primary" 
          @click="$router.push('/advertiser/ad-application')"
        >
          申请广告主
        </el-button>
      </div>
    </div>

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
      <el-table-column 
        v-if="isApprovedAdvertiser"
        label="操作" 
        width="200"
      >
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

    <el-dialog 
      v-if="isApprovedAdvertiser"
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
        
        <el-form-item label="图片">
          <el-upload
            action="/api/upload"
            :on-success="(res) => adForm.imageUrl = res.url"
            :show-file-list="false"
          >
            <el-button type="primary">上传图片</el-button>
          </el-upload>
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
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'AdList',
  setup() {
    const router = useRouter()
    const advertisements = ref([])
    const isApprovedAdvertiser = ref(false)
    const currentUserId = ref(null)

    const dialogVisible = ref(false)
    const dialogTitle = ref('发布广告')
    const adForm = ref({
      title: '',
      category: '',
      price: '',
      gender: '',
      imageUrl: ''
    })

    // 检查广告主状态
    const checkAdvertiserStatus = async () => {
      try {
        const userStr = localStorage.getItem('user')
        if (!userStr) {
          router.push('/login')
          return
        }
        const user = JSON.parse(userStr)
        currentUserId.value = user.id
        
        // 检查申请状态
        const response = await fetch(`/api/ad-applications/status?userId=${user.id}`)
        const data = await response.json()
        const wasApproved = isApprovedAdvertiser.value
        isApprovedAdvertiser.value = data.status === 'APPROVED'
        
        // 如果状态从未通过变为通过，刷新广告列表
        if (!wasApproved && isApprovedAdvertiser.value) {
          fetchAds()
        }
        
        // 如果未申请或申请被拒绝，提示用户
        if (data.status === 'NOT_APPLIED') {
          ElMessage.warning('请先申请成为广告主')
        } else if (data.status === 'REJECTED') {
          ElMessage.error('您的广告主申请已被拒绝')
        } else if (data.status === 'PENDING') {
          ElMessage.info('您的广告主申请正在审核中')
        }
      } catch (error) {
        console.error('获取广告主状态失败:', error)
      }
    }

    // 获取广告列表
    const fetchAds = async () => {
      try {
        const userStr = localStorage.getItem('user')
        if (!userStr) return
        
        const user = JSON.parse(userStr)
        const response = await fetch(`/api/advertisements/my?userId=${user.id}`)
        const data = await response.json()
        advertisements.value = Array.isArray(data) ? data : (data.content || [])
      } catch (error) {
        console.error('获取广告列表失败:', error)
        advertisements.value = []
      }
    }

    // 添加获取完整图片URL的方法
    const getImageUrl = (url) => {
      if (!url) return '';
      if (url.startsWith('http')) return url;
      return `http://localhost:8080${url}`;
    }

    const showCreateDialog = () => {
      dialogTitle.value = '发布广告'
      adForm.value = {
        title: '',
        category: '',
        price: '',
        gender: '',
        imageUrl: ''
      }
      dialogVisible.value = true
    }

    // 添加编辑广告方法
    const editAd = async (ad) => {
      try {
        dialogTitle.value = '编辑广告'
        adForm.value = { ...ad }  // 复制广告数据到表单
        dialogVisible.value = true
      } catch (error) {
        console.error('编辑广告失败:', error)
        ElMessage.error('编辑广告失败')
      }
    }

    // 添加删除广告方法
    const deleteAd = async (id) => {
      try {
        const response = await fetch(`/api/advertisements/${id}`, {
          method: 'DELETE'
        })
        if (response.ok) {
          ElMessage.success('删除成功')
          fetchAds()  // 刷新列表
        } else {
          ElMessage.error('删除失败')
        }
      } catch (error) {
        console.error('删除广告失败:', error)
        ElMessage.error('删除失败')
      }
    }

    // 修改提交方法，支持新增和编辑
    const handleSubmit = async () => {
      try {
        const userStr = localStorage.getItem('user')
        if (!userStr) return
        
        const user = JSON.parse(userStr)
        const method = adForm.value.id ? 'PUT' : 'POST'
        const url = adForm.value.id ? `/api/advertisements/${adForm.value.id}` : '/api/advertisements'
        
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            ...adForm.value,
            userId: user.id
          })
        })

        if (response.ok) {
          ElMessage.success(adForm.value.id ? '更新成功' : '发布成功')
          dialogVisible.value = false
          fetchAds()  // 刷新列表
        } else {
          throw new Error(adForm.value.id ? '更新失败' : '发布失败')
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      }
    }

    onMounted(() => {
      checkAdvertiserStatus()
      fetchAds()
    })

    return {
      advertisements,
      getImageUrl,
      isApprovedAdvertiser,
      currentUserId,
      dialogVisible,
      dialogTitle,
      adForm,
      showCreateDialog,
      handleSubmit,
      editAd,
      deleteAd
    }
  }
}
</script>

<style scoped>
.ad-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-right {
  display: flex;
  gap: 10px;
}
</style> 