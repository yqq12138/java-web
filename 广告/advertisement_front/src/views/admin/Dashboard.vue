<template>
  <div class="admin-dashboard">
    <el-container>
      <el-aside width="200px">
        <el-menu
          :router="true"
          :default-active="$route.path"
          class="admin-menu"
        >
          <el-menu-item index="/admin/applications">
            <el-icon><Document /></el-icon>
            <span>广告申请管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/ads">
            <el-icon><Picture /></el-icon>
            <span>广告管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/stats">
            <el-icon><TrendCharts /></el-icon>
            <span>数据分析</span>
          </el-menu-item>
          <el-menu-item @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { Document, Picture, TrendCharts, SwitchButton } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'AdminDashboard',
  components: {
    Document,
    Picture,
    TrendCharts,
    SwitchButton
  },
  setup() {
    const router = useRouter()

    const handleLogout = () => {
      localStorage.removeItem('user')
      ElMessage.success('退出登录成功')
      router.push('/login')
    }

    return {
      handleLogout
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.admin-menu {
  height: 100%;
  border-right: none;
}

.el-menu-item {
  display: flex;
  align-items: center;
}

.el-icon {
  margin-right: 8px;
}
</style> 