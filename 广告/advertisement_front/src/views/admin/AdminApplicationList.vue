<template>
  <div class="application-list">
    <h2>广告申请管理</h2>
    
    <el-table :data="applications" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="storeName" label="网店名称" />
      <el-table-column prop="storeUrl" label="网店链接" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button 
            v-if="scope.row.status === 'PENDING'"
            type="success" 
            size="small" 
            @click="handleApprove(scope.row.id)"
          >
            通过
          </el-button>
          <el-button 
            v-if="scope.row.status === 'PENDING'"
            type="danger" 
            size="small" 
            @click="handleReject(scope.row.id)"
          >
            拒绝
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

export default {
  name: 'AdminApplicationList',
  setup() {
    const router = useRouter()
    const applications = ref([])

    const checkAuth = () => {
      const userStr = localStorage.getItem('user')
      if (!userStr) {
        ElMessage.error('请先登录')
        router.push('/login')
        return false
      }
      const user = JSON.parse(userStr)
      if (user.role !== 'ADMIN') {
        ElMessage.error('无权访问')
        router.push('/ads')
        return false
      }
      return true
    }

    const fetchApplications = async () => {
      if (!checkAuth()) return
      
      try {
        const response = await fetch('/api/ad-applications')
        if (!response.ok) {
          throw new Error('获取数据失败')
        }
        const data = await response.json()
        console.log('获取到的申请数据:', data)
        applications.value = data
      } catch (error) {
        console.error('获取申请列表失败:', error)
        ElMessage.error('获取申请列表失败')
      }
    }

    const handleApprove = async (id) => {
      try {
        const response = await fetch(`/api/ad-applications/${id}/approve`, {
          method: 'PUT'
        })
        if (response.ok) {
          ElMessage.success('已通过申请')
          fetchApplications()
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    const handleReject = async (id) => {
      try {
        const response = await fetch(`/api/ad-applications/${id}/reject`, {
          method: 'PUT'
        })
        if (response.ok) {
          ElMessage.success('已拒绝申请')
          fetchApplications()
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    const getStatusType = (status) => {
      const types = {
        'PENDING': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger'
      }
      return types[status] || 'info'
    }

    const getStatusText = (status) => {
      const texts = {
        'PENDING': '待审核',
        'APPROVED': '已通过',
        'REJECTED': '已拒绝'
      }
      return texts[status] || status
    }

    onMounted(() => {
      fetchApplications()
    })

    return {
      applications,
      handleApprove,
      handleReject,
      getStatusType,
      getStatusText
    }
  }
}
</script>

<style scoped>
.application-list {
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
}
</style> 