<template>
  <div class="main-content">
    <div style="margin-bottom: 20px">
      <el-input size="medium" placeholder="请输入新闻标题关键字查询" style="width: 300px" v-model="title"></el-input>
      <el-button size="medium" type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button size="medium" type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>
    <div class="card" v-if="total > 0">
      <el-row :gutter="10">
        <el-col :span="8" v-for="item in tableData" :key="item.id" class="item" @click.native="$router.push('/front/newsDetail?id=' + item.id)">
          <img style="width: 100%; height: 240px; border-radius: 5px" :src="item.img" alt="">
          <div style="margin: 10px 0; font-size: 18px" class="line1">{{item.title }}</div>
          <div style="color: #666">
            <span style="margin-right: 20px"><i class="el-icon-eye"></i> {{ item.readCount }}</span>
            <span style="margin-right: 20px"><i class="el-icon-time"></i> {{ item.time }}</span>
            <span><i class="el-icon-s-comment"></i> {{ item.commentCount }}</span>
          </div>
        </el-col>
      </el-row>
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
  </div>
</template>

<script>
export default {
  name: "LocalNews",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      title: null,
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/news/selectLocalPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          title: this.title,
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
    reset() {
      this.title = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style scoped>
.item {
  cursor: pointer;
  margin-bottom: 20px;
}
.item:hover .line1 {
  color: red;
}
</style>