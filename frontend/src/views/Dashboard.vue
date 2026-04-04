<script setup>
import { ref, onMounted, watch, inject } from 'vue'
import axios from 'axios'

const stats = ref({
  playerCount: 0,
  matchCount: 0,
  totalAttendance: 0,
  avgAttendanceRate: 0
})

const recentMatches = ref([])
const seasonState = inject('seasonState')

const fetchData = async () => {
  try {
    const season = seasonState?.current || localStorage.getItem('currentSeason')
    const [players, matches, summary] = await Promise.all([
      axios.get('/api/players', { params: { season } }),
      axios.get('/api/matches', { params: { season } }),
      axios.get('/api/attendances/summary', { params: { season } })
    ])

    stats.value.playerCount = players.data.length
    stats.value.matchCount = matches.data.length

    let totalPresent = 0
    let totalMatches = 0
    summary.data.forEach(s => {
      totalPresent += s.presentCount || 0
      totalMatches += s.totalMatches || 0
    })
    stats.value.totalAttendance = totalPresent
    stats.value.avgAttendanceRate = totalMatches > 0 ? (totalPresent / totalMatches * 100).toFixed(1) : 0

    recentMatches.value = matches.data.slice(0, 5)
  } catch (error) {
    console.error('Failed to fetch data:', error)
  }
}

watch(() => seasonState?.current, () => {
  fetchData()
})

onMounted(fetchData)
</script>

<template>
  <div>
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <el-icon size="40" color="#409eff"><User /></el-icon>
            <div style="margin-top: 10px; font-size: 24px; font-weight: bold">{{ stats.playerCount }}</div>
            <div style="color: #666">球员总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <el-icon size="40" color="#67c23a"><Calendar /></el-icon>
            <div style="margin-top: 10px; font-size: 24px; font-weight: bold">{{ stats.matchCount }}</div>
            <div style="color: #666">比赛场次</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <el-icon size="40" color="#e6a23c"><SuccessFilled /></el-icon>
            <div style="margin-top: 10px; font-size: 24px; font-weight: bold">{{ stats.totalAttendance }}</div>
            <div style="color: #666">总出勤人次</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <el-icon size="40" color="#f56c6c"><DataLine /></el-icon>
            <div style="margin-top: 10px; font-size: 24px; font-weight: bold">{{ stats.avgAttendanceRate }}%</div>
            <div style="color: #666">平均出勤率</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <span>最近比赛</span>
      </template>
      <el-empty v-if="recentMatches.length === 0" description="暂无比赛记录" />
      <el-table v-else :data="recentMatches" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="matchDate" label="比赛日期" />
        <el-table-column prop="location" label="比赛地点" />
      </el-table>
    </el-card>
  </div>
</template>
