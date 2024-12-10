<template>
  <div>
    <div style="display: flex; grid-gap: 15px; margin-bottom: 15px">
      <div class="card item">
        <div style="flex: 1; text-align: center">
          <img src="@/assets/imgs/新闻.png" alt="" style="width: 60px; height: 60px">
        </div>
        <div style="flex: 1; font-size: 20px">
          <div style="margin-bottom: 5px">新闻总数</div>
          <div style="font-weight: bold">{{ count.newsCount }}</div>
        </div>
      </div>
      <div class="card item">
        <div style="flex: 1; text-align: center">
          <img src="@/assets/imgs/问答.png" alt="" style="width: 60px; height: 60px">
        </div>
        <div style="flex: 1; font-size: 20px">
          <div style="margin-bottom: 5px">问题总数</div>
          <div style="font-weight: bold">{{ count.questionCount }}</div>
        </div>
      </div>
      <div class="card item">
        <div style="flex: 1; text-align: center">
          <img src="@/assets/imgs/反馈.png" alt="" style="width: 60px; height: 60px">
        </div>
        <div style="flex: 1; font-size: 20px">
          <div style="margin-bottom: 5px">反馈总数</div>
          <div style="font-weight: bold">{{ count.feedbackCount }}</div>
        </div>
      </div>
      <div class="card item">
        <div style="flex: 1; text-align: center">
          <img src="@/assets/imgs/用户.png" alt="" style="width: 60px; height: 60px">
        </div>
        <div style="flex: 1; font-size: 20px">
          <div style="margin-bottom: 5px">用户总数</div>
          <div style="font-weight: bold"> {{ count.userCount }}</div>
        </div>
      </div>
    </div>

    <div style="display: flex; grid-gap: 15px">
      <div class="card" id="line" style="flex: 1; height: 500px"></div>
      <div class="card" id="pie" style="flex: 1; height: 500px"></div>
    </div>
  </div>
</template>

<script>
const lineOption = {
  title: {
    text: '答题数量的趋势图',
    left: 'center'
  },
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    left: 'left'
  },
  xAxis: {
    type: 'category',
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      data: [],
      type: 'line',
      smooth: true
    },
  ]
}
const pieOption = {
  title: {
    text: '订单销售统计',
    subtext: '比例图',
    left: 'center'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      type: 'pie',
      center: ['50%', '60%'],
      radius: '50%',
      data: [],
      label: {
        show: true,
        formatter(param) {
          return param.name + ' (' + param.percent + '%)';
        }
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}

import * as echarts from 'echarts'

export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      count: {}

    }
  },
  mounted() {  // 等页面dom元素加载完成再设置数据
    // 折线图
    let linetDom = document.getElementById('line');
    let lineChart = echarts.init(linetDom);

    // 饼图
    let pieDom = document.getElementById('pie');
    let pieChart = echarts.init(pieDom);

    this.$request.get('/selectLine').then(res => {
      lineOption.xAxis.data = res.data?.map(v => v.name) || []
      lineOption.series[0].data = res.data?.map(v => v.value) || []
      lineChart.setOption(lineOption)
    })

    this.$request.get('/selectPie').then(res => {
      pieOption.series[0].data = res.data || []
      pieChart.setOption(pieOption)
    })

  },
  created() {
    this.$request.get('/count').then(res => {
      this.count = res.data || {}
    })
  }
}
</script>

<style scoped>
.item {
  flex: 1;
  display: flex;
  align-items: center;
}
</style>
