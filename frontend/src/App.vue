<script setup>
import { ref, computed, onMounted, provide, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const activeIndex = computed(() => route.path)

const seasonState = reactive({
  current: null,
  list: []
})

const menuItems = [
  { path: '/', label: '首页', icon: 'Odometer' },
  { path: '/players', label: '球员管理', icon: 'User' },
  { path: '/teams', label: '队伍管理', icon: 'Grid' },
  { path: '/venues', label: '场地管理', icon: 'Location' },
  { path: '/seasons', label: '赛季管理', icon: 'Timer' },
  { path: '/matches', label: '比赛管理', icon: 'Calendar' },
  { path: '/attendance', label: '考勤记录', icon: 'Edit' },
  { path: '/report', label: '考勤汇总', icon: 'DataLine' },
  { path: '/match-stats', label: '比赛数据录入', icon: 'DataAnalysis' },
  { path: '/stats-leaderboard', label: '数据总榜', icon: 'TrendCharts' }
]

const handleMenuSelect = (path) => {
  router.push(path)
}

const fetchSeasons = async () => {
  try {
    const res = await axios.get('/api/seasons')
    if (res.data && res.data.length > 0) {
      seasonState.list = res.data
      const savedSeason = localStorage.getItem('currentSeason')
      if (savedSeason) {
        const found = seasonState.list.find(s => s.year === parseInt(savedSeason))
        if (found) {
          seasonState.current = found.year
        } else {
          seasonState.current = seasonState.list[0].year
        }
      } else {
        seasonState.current = seasonState.list[0].year
      }
      localStorage.setItem('currentSeason', seasonState.current)
    } else {
      const currentYear = new Date().getFullYear()
      seasonState.list = [{ year: currentYear, name: currentYear + '赛季' }]
      seasonState.current = currentYear
    }
  } catch (error) {
    const currentYear = new Date().getFullYear()
    seasonState.list = [{ year: currentYear, name: currentYear + '赛季' }]
    seasonState.current = currentYear
  }
}

const handleSeasonChange = () => {
  localStorage.setItem('currentSeason', seasonState.current)
}

provide('seasonState', seasonState)

onMounted(() => {
  fetchSeasons()
})
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
      <el-header style="background-color: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; justify-content: space-between">
        <span style="font-size: 16px; color: #333">足球球队考勤管理系统</span>
        <div style="display: flex; align-items: center; gap: 10px">
          <span style="font-size: 14px; color: #666">当前赛季:</span>
          <el-select v-model="seasonState.current" placeholder="选择赛季" style="width: 120px" @change="handleSeasonChange">
            <el-option v-for="s in seasonState.list" :key="s.year" :label="s.name || (s.year + '赛季')" :value="s.year" />
          </el-select>
        </div>
      </el-header>
      <el-main style="background-color: #f0f2f5">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
