<template>
  <div style="width: 60%; margin: 10px auto">
    <div style="display: flex; align-items: flex-start; grid-gap: 15px">
      <div style="flex: 1" class="card">
        <div @click="$router.push('/front/questionDetail?id=' + item.id)" v-for="(item, index) in tableData" :key="item.id" class="item">
          <div style="width: 20px; padding-top: 10px">
            <div v-if="index < 3" style="width: 10px; height: 10px; background-color: orangered; border-radius: 50%"></div>
            <div v-else style="width: 10px; height: 10px; background-color: #2a60c9; border-radius: 50%"></div>
          </div>
          <div style="flex: 1; width: 0">
            <div style="font-size: 20px; margin-bottom: 10px" class="title line2">{{ item.title }}</div>
            <div style="color: #666" class="line2">{{ item.title }}</div>
          </div>
          <div>
            <img :src="item.img" alt="" style="width: 150px; height: 100px; border-radius: 5px">
          </div>
        </div>

        <div style="margin: 20px 0">
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

      <div style="width: 300px">
        <div style="display: flex; margin-bottom: 10px">
          <img src="@/assets/imgs/问.png" alt="" style="width: 20px; height: 20px; margin-top: 5px; margin-right: 5px">
          <span style="font-size: 20px">等你回答</span>
        </div>
        <div>
          <div class="card" v-for="item in noAnswerList" :key="item.id" style="margin-bottom: 10px">
            <div style="margin-bottom: 10px; font-size: 16px">{{ item.title }}</div>
            <div style="color: #888; display: flex">
              <div style="flex: 1">{{ item.date }}</div>
              <el-button @click="$router.push('/front/questionDetail?id=' + item.id)" type="primary" size="mini" icon="el-icon-edit">写回答</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Question",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      noAnswerList: []
    }
  },
  created() {
    this.load(1)
    this.loadNoAnswer()
  },
  methods: {
    loadNoAnswer() {
      this.$request.get('/question/selectNoAnswer').then(res => {
        this.noAnswerList = res.data || []
      })
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/question/selectFrontPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
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
.item {
  display: flex;
  margin-bottom: 15px;
  cursor: pointer;
}
.item:hover .title {
  color: orangered;
}
</style>