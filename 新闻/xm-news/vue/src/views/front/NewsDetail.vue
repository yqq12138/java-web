<template>
  <div style="width: 50%; margin: 10px auto">
    <div class="card" style="margin-bottom: 10px">
      <div style="font-size: 24px; font-weight: bold; text-align: center; margin-bottom: 10px">{{ news.title }} </div>
      <div style="text-align: center; color: #888; margin-bottom: 20px">
        <span>发布时间：{{ news.time }}</span>
        <span style="margin-left: 20px">浏览量：{{ news.readCount }}</span>
      </div>
      <div class="w-e-text">
        <div v-html="news.content"></div>
      </div>
    </div>

    <div class="card">
      <Comment :fid="id" module="news" />
    </div>
  </div>
</template>

<script>
import E from 'wangeditor'
import Comment from "@/components/Comment";
export default {
  name: "NewsDetail",
  components: {Comment},
  data() {
    return {
      id: this.$route.query.id,
      news: {}
    }
  },
  created() {
    this.$request.put('/news/updateCount/' + this.id).then(() => {
      this.load()
    })
  },
  methods: {
    load() {
      this.$request.get('/news/selectById/' + this.id).then(res => {
        this.news = res.data || {}
      })
    }
  }
}
</script>

<style scoped>

</style>