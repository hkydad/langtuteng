<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const activeIndex = computed(() => route.path)

const menuItems = [
  { path: '/', label: '首页', icon: 'Odometer' },
  { path: '/players', label: '球员管理', icon: 'User' },
  { path: '/matches', label: '比赛管理', icon: 'Calendar' },
  { path: '/attendance', label: '考勤记录', icon: 'Edit' },
  { path: '/report', label: '考勤汇总', icon: 'DataLine' }
]

const handleMenuSelect = (path) => {
  router.push(path)
}
</script>

<template>
  <el-container style="height: 100vh">
    <el-aside width="200px" style="background-color: #304156">
      <div style="padding: 20px; color: #fff; font-size: 18px; font-weight: bold; text-align: center">
        ⚽ 足球考勤系统
      </div>
      <el-menu
        :default-active="activeIndex"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        @select="handleMenuSelect"
      >
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="background-color: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center">
        <span style="font-size: 16px; color: #333">足球球队考勤管理系统</span>
      </el-header>
      <el-main style="background-color: #f0f2f5">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
