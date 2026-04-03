<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const matches = ref([])
const players = ref([])
const selectedMatch = ref(null)
const attendances = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const selectedPlayerIds = ref([])

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

const openAttendanceDialog = () => {
  if (!selectedMatch.value) {
    ElMessage.warning('请先选择一场比赛')
    return
  }
  selectedPlayerIds.value = []
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
      <div style="margin-bottom: 10px">
        <el-checkbox-group v-model="selectedPlayerIds">
          <el-checkbox v-for="player in players" :key="player.id" :value="player.id" style="margin: 5px 10px">
            {{ player.name }} ({{ player.memberLevel === 500 ? '500' : player.memberLevel === 200 ? '200' : '-' }})
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
