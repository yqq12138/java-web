<template>
  <div class="ad-stats">
    <h2>广告数据分析</h2>
    
    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6" v-for="(stat, index) in statsData" :key="index">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-title">{{ stat.title }}</div>
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-change" :class="stat.increase ? 'up' : 'down'">
              {{ stat.change }}%
              <i :class="stat.increase ? 'el-icon-top' : 'el-icon-bottom'"></i>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 简单表格展示每日数据 -->
    <el-card class="daily-stats">
      <div class="card-title">最近7天数据</div>
      <el-table :data="dailyData" border stripe>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="views" label="曝光量" />
        <el-table-column prop="clicks" label="点击量" />
        <el-table-column prop="conversions" label="转化量" />
        <el-table-column prop="cost" label="花费(元)" />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'AdStats',
  setup() {
    // 模拟统计数据
    const statsData = ref([
      {
        title: '总曝光量',
        value: Math.floor(Math.random() * 100000),
        change: +(Math.random() * 20).toFixed(1),
        increase: true
      },
      {
        title: '点击量',
        value: Math.floor(Math.random() * 10000),
        change: +(Math.random() * 15).toFixed(1),
        increase: true
      },
      {
        title: '转化量',
        value: Math.floor(Math.random() * 1000),
        change: +(Math.random() * 10).toFixed(1),
        increase: false
      },
      {
        title: '花费(元)',
        value: +(Math.random() * 10000).toFixed(2),
        change: +(Math.random() * 12).toFixed(1),
        increase: true
      }
    ])

    // 生成每日数据
    const dailyData = ref([])
    const generateDailyData = () => {
      const data = []
      for (let i = 6; i >= 0; i--) {
        const date = new Date()
        date.setDate(date.getDate() - i)
        data.push({
          date: date.toLocaleDateString(),
          views: Math.floor(Math.random() * 10000),
          clicks: Math.floor(Math.random() * 1000),
          conversions: Math.floor(Math.random() * 100),
          cost: +(Math.random() * 1000).toFixed(2)
        })
      }
      dailyData.value = data
    }

    onMounted(() => {
      generateDailyData()
    })

    return {
      statsData,
      dailyData
    }
  }
}
</script>

<style scoped>
.ad-stats {
  padding: 20px;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-title {
  color: #666;
  font-size: 14px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin: 10px 0;
}

.stat-change {
  font-size: 12px;
}

.stat-change.up {
  color: #67C23A;
}

.stat-change.down {
  color: #F56C6C;
}

.daily-stats {
  margin-top: 20px;
}

.card-title {
  font-size: 16px;
  margin-bottom: 20px;
  color: #333;
}
</style> 