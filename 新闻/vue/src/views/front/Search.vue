<template>
  <div class="main-content">
    <div style="display: flex; grid-gap: 20px">
      <!--  左侧开始  -->
      <div style="flex: 1">
        <div @click="$router.push('/front/newsDetail?id=' + item.id)" class="card" v-for="item in tableData" :key="item.id" style="display: flex; cursor: pointer; grid-gap: 15px; margin-bottom: 5px">
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
      <!--  左侧结束  -->

      <!--  右侧开始  -->
      <div style="width: 400px">
        <hot />
      </div>
      <!--  右侧结束  -->
    </div>


  </div>
</template>

<script>
import Hot from "@/components/Hot";
export default {
  name: "Search",
  components: {Hot},
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/news/selectFrontPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          title: this.$route.query.title,
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
  }
}
</script>

<style scoped>

</style>