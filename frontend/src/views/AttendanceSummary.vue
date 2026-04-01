<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const summaryData = ref([])
const loading = ref(false)

const fetchSummary = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/attendances/summary')
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

onMounted(fetchSummary)
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
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="totalMatches" label="参与场次" width="100" />
      <el-table-column prop="presentCount" label="出勤次数" width="100" />
      <el-table-column prop="absentCount" label="请假次数" width="100" />
      <el-table-column label="出勤率" width="150">
        <template #default="{ row }">
          <el-progress
            :percentage="Number(row.attendanceRate.toFixed(1))"
            :color="getAttendanceRateColor(row.attendanceRate)"
          />
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="summaryData.length === 0 && !loading" description="暂无考勤数据" />
  </el-card>
</template>
