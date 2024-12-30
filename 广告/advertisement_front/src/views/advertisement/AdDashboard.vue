<template>
  <div class="advertiser-dashboard">
    <el-container>
      <el-aside width="200px">
        <el-menu
          :router="true"
          :default-active="$route.path"
          class="advertiser-menu"
        >
          <el-menu-item index="/advertiser/ads">
            <el-icon><Picture /></el-icon>
            <span>我的广告</span>
          </el-menu-item>
          <el-menu-item index="/advertiser/stats">
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
import { Picture, TrendCharts, SwitchButton } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'AdvertiserDashboard',
  components: {
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
.advertiser-dashboard {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.advertiser-menu {
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