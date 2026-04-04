<script setup>
import { ref, onMounted, watch, inject, computed } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const matches = ref([])
const allPlayers = ref([])
const selectedMatch = ref(null)
const attendances = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const selectedPlayerIds = ref([])
const seasonState = inject('seasonState')
const searchName = ref('')

const fetchMatches = async () => {
  try {
    const season = seasonState?.current || localStorage.getItem('currentSeason')
    const res = await axios.get('/api/matches', { params: { season } })
    matches.value = res.data
  } catch (error) {
    ElMessage.error('获取比赛列表失败')
  }
}

const fetchPlayersWithAttendance = async () => {
  try {
    const season = seasonState?.current || localStorage.getItem('currentSeason')
    const [playersRes, summaryRes] = await Promise.all([
      axios.get('/api/players', { params: { season } }),
      axios.get('/api/attendances/summary', { params: { season } })
    ])

    const summaryMap = {}
    summaryRes.data.forEach(s => {
      summaryMap[s.playerId] = s.presentCount || 0
    })

    allPlayers.value = playersRes.data.map(p => ({
      ...p,
      presentCount: summaryMap[p.id] || 0
    })).sort((a, b) => b.presentCount - a.presentCount)
  } catch (error) {
    ElMessage.error('获取球员列表失败')
  }
}

const filteredPlayers = computed(() => {
  if (!searchName.value) return allPlayers.value
  return allPlayers.value.filter(p => p.name.includes(searchName.value))
})

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

const openAttendanceDialog = () => {
  if (!selectedMatch.value) {
    ElMessage.warning('请先选择一场比赛')
    return
  }
  searchName.value = ''
  selectedPlayerIds.value = attendances.value.map(a => a.playerId)
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!selectedMatch.value) return
  try {
    await axios.post('/api/attendances/batch', {
      matchId: selectedMatch.value,
      playerIds: selectedPlayerIds.value
    })
    ElMessage.success('考勤保存成功')
    dialogVisible.value = false
    fetchAttendances()
  } catch (error) {
    console.error('保存考勤失败:', error)
    ElMessage.error('保存考勤失败: ' + (error.response?.data || error.message))
  }
}

const handleMatchChange = () => {
  attendances.value = []
  fetchAttendances()
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该考勤记录吗?', '提示', {
      type: 'warning'
    })
    await axios.delete(`/api/attendances/${row.id}`)
    ElMessage.success('删除成功')
    fetchAttendances()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

watch(() => seasonState?.current, () => {
  selectedMatch.value = null
  attendances.value = []
  fetchMatches()
})

onMounted(() => {
  fetchMatches()
  fetchPlayersWithAttendance()
})
</script>

<template>
  <el-card>
    <template #header>
      <span>考勤记录</span>
    </template>

    <el-form :inline="true" style="margin-bottom: 20px">
      <el-form-item label="选择比赛">
        <el-select v-model="selectedMatch" placeholder="请选择比赛" style="width: 300px" @change="handleMatchChange">
          <el-option v-for="match in matches" :key="match.id" :label="`${match.matchDate} - ${match.location}`" :value="match.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :disabled="!selectedMatch" @click="openAttendanceDialog">新增考勤</el-button>
      </el-form-item>
    </el-form>

    <el-empty v-if="!selectedMatch" description="请先选择一场比赛" />
    <el-table v-else :data="attendances" v-loading="loading" stripe>
      <el-table-column prop="playerId" label="ID" width="80" />
      <el-table-column prop="memberLevel" label="会员等级" width="120">
        <template #default="{ row }">
          {{ row.memberLevel === 500 ? '500' : row.memberLevel === 200 ? '200' : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="playerName" label="球员姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="考勤登记" width="600px">
      <div style="margin-bottom: 15px">
        <el-input v-model="searchName" placeholder="搜索球员姓名" style="width: 200px" clearable />
      </div>
      <div style="max-height: 400px; overflow-y: auto">
        <el-checkbox-group v-model="selectedPlayerIds">
          <el-checkbox v-for="player in filteredPlayers" :key="player.id" :value="player.id" style="margin: 5px 10px; display: flex">
            <span style="min-width: 80px">{{ player.name }}</span>
            <span style="color: #999; font-size: 12px; margin-left: 10px">出勤{{ player.presentCount }}次</span>
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
