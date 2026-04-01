<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const matches = ref([])
const players = ref([])
const selectedMatch = ref(null)
const attendances = ref([])
const loading = ref(false)

const fetchMatches = async () => {
  try {
    const res = await axios.get('/api/matches')
    matches.value = res.data
  } catch (error) {
    ElMessage.error('获取比赛列表失败')
  }
}

const fetchPlayers = async () => {
  try {
    const res = await axios.get('/api/players')
    players.value = res.data
  } catch (error) {
    ElMessage.error('获取球员列表失败')
  }
}

const fetchAttendances = async () => {
  if (!selectedMatch.value) return
  loading.value = true
  try {
    const res = await axios.get(`/api/attendances/match/${selectedMatch.value}`)
    attendances.value = res.data
  } catch (error) {
    ElMessage.error('获取考勤记录失败')
  } finally {
    loading.value = false
  }
}

const getAttendanceStatus = (playerId) => {
  const record = attendances.value.find(a => a.playerId === playerId)
  return record ? record.status : null
}

const getAttendanceRemark = (playerId) => {
  const record = attendances.value.find(a => a.playerId === playerId)
  return record ? record.remark : ''
}

const handleStatusChange = async (playerId, status) => {
  if (!selectedMatch.value) return
  try {
    await axios.post('/api/attendances', {
      matchId: selectedMatch.value,
      playerId,
      status,
      remark: getAttendanceRemark(playerId)
    })
    ElMessage.success('考勤已更新')
    fetchAttendances()
  } catch (error) {
    ElMessage.error('更新考勤失败')
  }
}

const handleRemarkChange = async (playerId, remark) => {
  if (!selectedMatch.value) return
  const status = getAttendanceStatus(playerId) || 'PRESENT'
  try {
    await axios.post('/api/attendances', {
      matchId: selectedMatch.value,
      playerId,
      status,
      remark
    })
  } catch (error) {
    ElMessage.error('更新备注失败')
  }
}

watch(selectedMatch, fetchAttendances)

onMounted(() => {
  fetchMatches()
  fetchPlayers()
})
</script>

<template>
  <el-card>
    <template #header>
      <span>考勤记录</span>
    </template>

    <el-form :inline="true" style="margin-bottom: 20px">
      <el-form-item label="选择比赛">
        <el-select v-model="selectedMatch" placeholder="请选择比赛" style="width: 300px">
          <el-option v-for="match in matches" :key="match.id" :label="`${match.matchDate} - ${match.location}`" :value="match.id" />
        </el-select>
      </el-form-item>
    </el-form>

    <el-empty v-if="!selectedMatch" description="请先选择一场比赛" />
    <el-table v-else :data="players" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="球员姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column label="出勤状态" width="200">
        <template #default="{ row }">
          <el-radio-group :model-value="getAttendanceStatus(row.id)" @update:model-value="(val) => handleStatusChange(row.id, val)">
            <el-radio value="PRESENT">出勤</el-radio>
            <el-radio value="ABSENT">请假</el-radio>
          </el-radio-group>
        </template>
      </el-table-column>
      <el-table-column label="备注">
        <template #default="{ row }">
          <el-input
            :model-value="getAttendanceRemark(row.id)"
            placeholder="输入备注"
            @blur="(e) => handleRemarkChange(row.id, e.target.value)"
          />
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>
