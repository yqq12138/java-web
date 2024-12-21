<script setup>
import axios from "axios";
import { ref } from 'vue';

    //用户头像
    const getImgUrl = (user) =>{
      return new URL(`../assets/images/${user}.png`,import.meta.url).href
    }

    //左下角表格数据
    const tableData = ref([
        {        
            name: "Java",
            todayBuy: 100,
            monthBuy:200,
            totalBuy: 300,
        },
        {
            name:"Python",
            todayBuy: 100,
            monthBuy: 200,
            totalBuy: 300,
        }
    ])

    const tableLabel = ref({
        name:"课程",
        todayBuy:"今日购买",
        monthBuy:"本月购买",
        totalBuy:"总购买",
    })

    //axios请求数据
    axios({
        url:'/api/home/getTableData',
        method:'get'
    }).then((res)=>{
        if(res.data.code === 200){
            console.log(res.data);
            tableData.value = res.data.data.tableData;
        }
    })

</script>

<template>
    <el-row class="home" :gutter="20">
        <el-col :span="8" style="margin-top: 20px;">
            <!-- 登录用户信息卡片 -->
            <el-card shadow="hover">
                <div class="user">
                    <img :src="getImgUrl('user')" class="user">
                    <div class="user-info">
                        <p class="user-name">Admin</p>
                        <p class="user-role">超级管理员</p>
                    </div>
                </div>
                <div class="login-info">
                    <p>上次登录时间: <span>2022-12-21</span></p>
                    <p>上次登录地点: <span>上海理工大学</span></p>
                </div>
            </el-card>
            
            <!-- 用户表格 -->
            <el-card shadow="hover" class="user-table">
                <el-table :data="tableData">
                    <el-table-column
                        v-for="(val,key) in tableLabel"
                        :key="key"
                        :prop="key"
                        :label="val"
                    >
                    </el-table-column>
                </el-table>
            </el-card>
        </el-col>
    </el-row>
</template>

<script>

</script>

<style>
/*  左上角卡片的样式  */
.home{
    height: 100%;
    overflow: hidden;
    .user{
        display: flex;
        align-items: center;
        border-bottom: 1px solid #ccc;
        margin-bottom: 20px;
        img{
            width:150px;
            height:150px;
            border-radius: 50%;
            margin-right: 40px;
        }
        .user-info{
            p{
              line-height: 40px;  
            }
        }
        .user-name{
            font-size:35px;
            font-weight:bold;
        }
        .user-role{
            color:#666;
            font-size:14px;
        }
    }
}
.login-info{
        p{
        line-height:30px;
        font-size:14px;
        color:#999;
        span{
            color:#666;
            margin-left:60px;
            }
        }
    }

/*  右下角的表格卡片 */
.user-table{
    margin-top: 20px;
}
</style>
