<template>
  <div class="main-content">
    <div style="display: flex; grid-gap: 20px">
      <!-- 左侧部分开始-->
      <div style="flex: 2">
        <el-carousel height="350px">
          <el-carousel-item v-for="item in bannerList" :key="item.id">
            <a @click="recordAndRedirect(item.newsId)" target="_blank">
              <img :src="item.img" alt="" style="width: 100%">
            </a>
          </el-carousel-item>
        </el-carousel>

        <div style="margin: 20px 0; display: flex">
          <div @click="loadCategoryNews(null)" class="category-item" :class="{ 'category-item-active' : category === null }">全部</div>
          <div @click="loadCategoryNews(item.name)" class="category-item" :class="{ 'category-item-active' : category === item.name }" v-for="item in categoryList"
               :key="item.id">{{ item.name }}</div>
        </div>
        <div>
          <div @click="recordAndRedirect(item.id)" class="card" v-for="item in tableData" :key="item.id" style="display: flex; cursor: pointer; grid-gap: 15px; margin-bottom: 5px">
            <img :src="item.img" alt="" style="width: 150px; height: 100px; border-radius: 5px; display: block">
            <div style="flex: 1">
              <div style="font-size: 20px; margin-bottom: 10px" class="line1">{{ item.title }}</div>
              <div style="color: #666; height: 40px; margin-bottom: 5px" class="line2">{{item.descr }}</div>
              <div style="color: #666">
                <span style="margin-right: 20px"><i class="el-icon-time"></i> {{ item.time }}</span>
                <span><i class="el-icon-s-comment"></i> {{ item.commentCount }}</span>
              </div>
            </div>
          </div>
        </div>

        <div style="margin: 15px 0" v-if="total > pageSize">
          <el-pagination
              background
              @current-change="handleCurrentChange"
              :current-page="pageNum"
              :page-sizes="[5, 10, 20]"
              :page-size="pageSize"
              layout="total, prev, pager, next"
              :total="total">
          </el-pagination>
        </div>

      </div>
      <!-- 左侧部分结束-->

      <!-- 右侧部分开始-->
      <div style="flex: 1; width: 0">
        <hot />

        <div style="margin: 20px 0">
          <div style="font-size: 24px; margin-bottom: 20px">精选视频</div>
          <div style="margin-bottom: 10px">
            <video controls style="width: 100%" :src="video.file"></video>
          </div>
          <div @click="loadVideo(item)" v-for="item in videoList" :key="item.id" class="video-item"
               :class="{'video-item-active': video.id === item.id}" >
            <img src="@/assets/imgs/play.gif" alt="" style="width: 15px; margin-right: 10px" v-if="video.id === item.id">
            <span>{{ item.name }}</span>
          </div>
        </div>
      </div>
      <!-- 右侧部分结束-->
    </div>
  </div>
</template>

<script>

import Hot from "@/components/Hot";
export default {
  components: {Hot},
  data() {
    return {
      bannerList: [],
      categoryList: [],
      category: null,
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      video: {},
      videoList: []
    }
  },
  mounted() {
    this.loadBanner()
    this.loadCategory()
    this.load(1)
    this.loadVideo(null)
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadVideo(item) {
      this.$request.get('/video/selectPage', {
        params: {
          pageNum: 1
        }
      }).then(res => {
        this.videoList = res.data?.list || []
        if (item) {
          this.video = item
        } else {
          this.video = this.videoList.length ? this.videoList[0] : {}
        }
      })
    },

    loadBanner() {
      this.$request.get('/banner/selectAll').then(res => [
        this.bannerList = res.data || []
      ])
    },
    loadCategory() {
      this.$request.get('/category/selectAll').then(res => [
        this.categoryList = res.data || []
      ])
    },
    loadCategoryNews(name) {
      this.category = name
      this.load(1)
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/news/selectFrontPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          category: this.category,
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data?.list
          this.total = res.data?.total
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    /**
     * 记录浏览并跳转到新闻详情页
     */
    recordAndRedirect(newsId) {
      // 发送记录浏览请求
      this.$request.post('/news/recordView', {
        newsId: newsId
      }).then(res => {
        if (res.code === '200') {
          // 浏览记录成功后跳转
          this.$router.push('/front/newsDetail?id=' + newsId)
        } else {
          this.$message.error(res.msg)
          // 仍然跳转
          this.$router.push('/front/newsDetail?id=' + newsId)
        }
      }).catch(() => {
        // 发送请求失败后仍然跳转
        this.$router.push('/front/newsDetail?id=' + newsId)
      })
    }
  }
}
</script>

<style scoped>
.category-item {
  border: 1px solid #2a60c9;
  color: #2a60c9;
  padding: 5px 10px;
  border-radius: 5px;
  margin-right: 10px;
  cursor: pointer;
}
.category-item-active {
  background-color: #2a60c9;
  color: #fff;
}
.line1:hover {
  color: #409EFF !important;
}
.video-item {
  margin-bottom: 10px;
  font-size: 16px;
  cursor: pointer
}
.video-item:hover {
  color: #409EFF;
}
.video-item-active {
  color: #409EFF;
}
</style>
