<template>
  <div style="width: 50%; margin: 10px auto">
    <div class="card" style="margin-bottom: 10px">
      <div style="font-size: 24px; margin-bottom: 20px">{{ question.title }}</div>
      <div style="margin-bottom: 20px; color: #444; font-size: 16px">{{ question.descr }}</div>
      <div style="display: flex">
        <div style="flex: 1; color: #888">
          <span style="margin-right: 20px">发布日期：{{ question.date }}</span>
          <span>阅读量：{{ question.readCount }}</span>
        </div>
        <el-button type="primary" icon="el-icon-edit" @click="handleAdd">回答问题</el-button>
      </div>
    </div>

    <div class="card">
      <div style="font-size: 24px; margin-bottom: 20px">回答列表 {{ total }}</div>
      <div>
        <div v-for="item in answerList" :key="item.id" style="display: flex; grid-gap: 10px; padding-bottom: 20px; border-bottom: 1px solid #ddd; margin-bottom: 20px">
          <div style="color: #666; padding-top: 20px; width: 60px; cursor: pointer">
            <span @click="addLikes(item.id)" :style="{ 'color' : item.likes ? 'red' : '#666' }"><i style="font-size: 20px" class="el-icon-like"></i>{{ item.likesCount }}</span>
          </div>
          <div style="flex: 1;">
           <div style="display: flex; grid-gap: 10px; margin-bottom: 10px">
             <img :src="item.userAvatar" alt="" style="width: 50px; height: 50px; border-radius: 50%">
             <div style="color: #888;">
               <div style="margin-bottom: 10px">{{ item.userName }}</div>
               <div>{{ item.time }}</div>
             </div>
           </div>

            <div class="w-e-text">
              <div v-html="item.content"></div>
            </div>

            <div style="margin-top: 10px" v-if="item.userId === user.id">
              <span @click="del(item.id)" style="color: #666; cursor: pointer"><i class="el-icon-delete"></i>删除</span>
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

      <el-dialog title="回答内容" :visible.sync="fromVisible" width="50%" :close-on-click-modal="false" destroy-on-close>
        <el-form :model="form" label-width="100px" style="padding-right: 50px" ref="formRef">
          <el-form-item label="内容" prop="content">
            <div id="editor"></div>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="fromVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </div>
      </el-dialog>


    </div>
  </div>
</template>

<script>
import E from "wangeditor"

export default {
  name: "QuestionDetail",
  data() {
    return {
      id: this.$route.query.id,
      question: {},
      answerList: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      form: {},
      fromVisible: false,
      editor: null,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    }
  },
  created() {
    this.$request.put('/question/updateCount/' + this.id).then(() => {
      this.load()
    })

    this.loadAnswer(1)
  },
  methods: {
    addLikes(answerId) {
      this.$request.post('/likes/add', { fid: answerId, module: 'answer' }).then(res => {
        if (res.code === '200') {  // 表示成功保存
          this.$message.success('操作成功')
          this.loadAnswer(1)
        } else {
          this.$message.error(res.msg)  // 弹出错误的信息
        }
      })
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.form.questionId = this.id
      this.form.content = this.editor.txt.html()
      this.$request.post('/answer/add', this.form).then(res => {
        if (res.code === '200') {  // 表示成功保存
          this.$message.success('保存成功')
          this.loadAnswer(1)
          this.fromVisible = false
        } else {
          this.$message.error(res.msg)  // 弹出错误的信息
        }
      })
    },
    handleAdd() {   // 新增数据
      this.form = {}  // 新增数据的时候清空数据
      this.fromVisible = true   // 打开弹窗
      this.setRichText('')
    },
    loadAnswer(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/answer/selectFrontPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          questionId: this.id
        }
      }).then(res => {
        this.answerList = res.data?.list || []
        this.total = res.data?.total || 0
      })
    },
    load() {
      this.$request.get('/question/selectById/' + this.id).then(res => {
        this.question = res.data || {}
      })
    },
    handleCurrentChange(pageNum) {
      this.loadAnswer(pageNum)
    },
    setRichText(html) {
      this.$nextTick(() => {
        this.editor = new E(`#editor`)
        this.editor.config.uploadImgServer = this.$baseUrl + '/files/editor/upload'
        this.editor.config.uploadFileName = 'file'
        this.editor.config.uploadImgHeaders = {
          token: this.user.token
        }
        this.editor.config.uploadImgParams = {
          type: 'img',
        }
        this.editor.config.zIndex = 0
        this.editor.create()  // 创建
        this.editor.txt.html(html)
      })
    },
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/answer/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.loadAnswer(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
  }
}
</script>

<style scoped>

</style>