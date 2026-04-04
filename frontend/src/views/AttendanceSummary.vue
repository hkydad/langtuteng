<script setup>
import { ref, onMounted, watch, inject } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const summaryData = ref([])
const loading = ref(false)
const seasonState = inject('seasonState')

const fetchSummary = async () => {
  loading.value = true
  try {
    const season = seasonState?.current || localStorage.getItem('currentSeason')
    const res = await axios.get('/api/attendances/summary', { params: { season } })
    summaryData.value = res.data
  } catch (error) {
    ElMessage.error('获取考勤汇总失败')
  } finally {
    loading.value = false
  }
}

const getAttendanceRateColor = (rate) => {
  if (rate >= 80) return '#67c23a'
  if (rate >= 60) return '#e6a23c'
  return '#f56c6c'
}

watch(() => seasonState?.current, () => {
  fetchSummary()
})

onMounted(() => {
  fetchSummary()
})
</script>

<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span>考勤汇总排名</span>
        <el-button type="primary" @click="fetchSummary">刷新</el-button>
      </div>
    </template>

    <el-table :data="summaryData" v-loading="loading" stripe>
      <el-table-column prop="rank" label="排名" width="80" />
      <el-table-column prop="playerName" label="球员姓名" />
      <el-table-column prop="seasonMatchCount" label="当赛季比赛场次" width="140" />
      <el-table-column prop="presentCount" label="当赛季参加次数" width="140" />
      <el-table-column label="出勤率" width="120">
        <template #default="{ row }">
          <span :style="{ color: getAttendanceRateColor(row.attendanceRate) }">
            {{ row.attendanceRate.toFixed(2) }}%
          </span>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="summaryData.length === 0 && !loading" description="暂无考勤数据" />
  </el-card>
</template>
