<template>
  <div class="main-content" style="width: 50%">
    <el-timeline style="margin-top: 20px" reverse>
      <el-timeline-item :color="'#0bbd87'" :timestamp="item.time" placement="top" v-for="item in noticeList" :key="item.id">
        <el-card>
          <p style="line-height: 24px">{{ item.content }}</p>
        </el-card>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>

<script>
export default {
  name: "FNotice",
  data() {
    return {
      noticeList: []
    }
  },
  created() {
    this.$request.get('/notice/selectAll').then(res => {
      this.noticeList = res.data || []
    })
  },
  methods: {}
}
</script>

<style scoped>

</style>