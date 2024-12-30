<template>
  <div class="ad-application">
    <h2>广告申请</h2>
    
    <el-form :model="applicationForm" label-width="100px">
      <el-form-item label="网店名称">
        <el-input v-model="applicationForm.storeName" placeholder="请输入您的网店名称" />
      </el-form-item>
      
      <el-form-item label="网店链接">
        <el-input v-model="applicationForm.storeUrl" placeholder="请输入您的网店链接" />
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="submitApplication">提交申请</el-button>
      </el-form-item>
    </el-form>

    <!-- 申请历史记录 -->
    <div class="history">
      <h3>申请历史</h3>
      <el-table :data="applications" border>
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
      </el-table>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'AdApplication',
  setup() {
    const applicationForm = ref({
      storeName: '',
      storeUrl: ''
    })
    const applications = ref([])

    const submitApplication = async () => {
      try {
        // 从localStorage获取登录用户信息
        const userStr = localStorage.getItem('user');
        if (!userStr) {
          ElMessage.error('请先登录');
          return;
        }
        
        const user = JSON.parse(userStr);
        
        const response = await fetch('/api/ad-applications', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            userId: user.id.toString(),  // 使用登录用户的ID
            storeName: applicationForm.value.storeName,
            storeUrl: applicationForm.value.storeUrl
          })
        });

        if (response.ok) {
          ElMessage.success('申请提交成功')
          applicationForm.value = { storeName: '', storeUrl: '' }
          fetchApplications()
        } else {
          ElMessage.error('申请提交失败')
        }
      } catch (error) {
        ElMessage.error('申请提交失败')
      }
    }

    const fetchApplications = async () => {
      try {
        const userStr = localStorage.getItem('user');
        if (!userStr) return;
        
        const user = JSON.parse(userStr);
        const response = await fetch(`/api/ad-applications?userId=${user.id}`)
        const data = await response.json()
        applications.value = data
      } catch (error) {
        console.error('获取申请历史失败:', error)
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
      applicationForm,
      applications,
      submitApplication,
      getStatusType,
      getStatusText
    }
  }
}
</script>

<style scoped>
.ad-application {
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
}

.history {
  margin-top: 40px;
}

h3 {
  margin-bottom: 20px;
}
</style> 