<template>
  <el-aside width="sidewidth">
      <el-menu
          text-color="#fff"
          :collapse="isCollapse"
          :collapse-transition="false"
          >
        <h3 v-show="!isCollapse">通用后台管理</h3>
        <h3 v-show="isCollapse">后台</h3>
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

<script setup>
import { useAllDataStore } from '@/stores';
import { computed, ref } from 'vue';

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
const store = useAllDataStore();
const isCollapse = computed(() => store.state.isCollapse);
const sidewidth = computed(() => store.state.isCollapse ? '60px' : '180px');

// return {
//       noChildren,
//       hasChildren,
//     }
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
  background-color: #545c64;
}
</style>