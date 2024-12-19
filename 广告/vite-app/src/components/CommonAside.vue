<template>
  <el-aside>
      <el-menu
          text-color="#fff"
          >
        <h3>通用后台管理</h3>
        <el-menu-item 
          v-for="item in noChildren"
          :index="item.path"
          :key="item.path"
        >
          <component class="icons" :is="item.icon"></component>
          <span>{{ item.label }}</span>
        </el-menu-item>
        <el-sub-menu 
        v-for="item in hasChildren"
          :index="item.path"
          :key="item.path"
        >
          <template #title>
            <component class="icons" :is="item.icon"></component>
            <span>{{ item.label }}</span>
          </template>
          <el-menu-item-group>
            <el-menu-item         
            v-for="(subItem,subIndex) in item.children"
              :index="subItem.path"
              :key="subItem.path"
            >
            <component class="icons" :is="subItem.icon"></component>
            <span>{{ subItem.label }}</span>
          </el-menu-item>
            </el-menu-item-group>
        </el-sub-menu>
      </el-menu>
  </el-aside>
</template>

<script>
import { computed, ref } from 'vue';

export default {
  setup() {

const list = ref([
    {
        path:'/home',
        name:'home',
        label:'首页',
        icon:'house',
        url:'Home'
    },
    {
        path:'/mail',
        name:'mail',
        label:'商品管理',
        icon:'video-play',
        url:'Mail'
    },
        {
        path:'/user',
        name:'user',
        label:'用户管理',
        icon:'user',
        url:'User'
    },
        {
        path:'/other',
        label:'其他',
        icon:'location',
        children:[
            {
                path:'/page1',
                name:'page1',
                label:'页面1',
                icon:'setting',
                url:'Page1'
            },
            {
                path:'/page2',
                name:'page2',
                label:'页面2',
                icon:'setting',
                url:'Page2'
            },
        ]
    }
])

const noChildren = computed(() => list.value.filter(item => !item.children) );
const hasChildren = computed(() => list.value.filter(item =>  item.children) );

return {
      noChildren,
      hasChildren,
    };
  }
}
</script>

<style>
.icons {
    width: 18px;
    height: 18px;
}

.el-menu{
    border-right: none;
    background-color: #545c64;
    h3{
      line-height: 48px;
      margin : 0;
      color: #fff;
      text-align: center;
    }
}

.el-aside{
  height: 100%;
  width: 180px;
  background-color: #545c64;
}
</style>