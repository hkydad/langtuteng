<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const matches = ref([])
const players = ref([])
const teams = ref([])
const selectedMatch = ref(null)
const quarters = ref([])
const selectedQuarter = ref(null)
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const quarterForm = ref({
  id: null,
  matchId: null,
  quarterNum: 1,
  team1Name: '',
  team2Name: '',
  team1Score: 0,
  team2Score: 0
})
const goalDialogVisible = ref(false)
const assistDialogVisible = ref(false)
const goalkeeperDialogVisible = ref(false)
const selectedPlayer = ref(null)
const selectedTeam = ref('')
const goalRecords = ref([])
const assistRecords = ref([])
const goalkeeperRecords = ref([])

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

const fetchTeams = async () => {
  try {
    const res = await axios.get('/api/teams')
    teams.value = res.data
  } catch (error) {
    ElMessage.error('获取队伍列表失败')
  }
}

const fetchQuarters = async () => {
  if (!selectedMatch.value) return
  try {
    const res = await axios.get(`/api/match-stats/quarter/match/${selectedMatch.value}`)
    quarters.value = res.data
  } catch (error) {
    ElMessage.error('获取小节数据失败')
  }
}

const fetchQuarterRecords = async () => {
  if (!selectedQuarter.value) return
  try {
    const [goalRes, assistRes, gkRes] = await Promise.all([
      axios.get(`/api/match-stats/goal/quarter/${selectedQuarter.value}`),
      axios.get(`/api/match-stats/assist/quarter/${selectedQuarter.value}`),
      axios.get(`/api/match-stats/goalkeeper/quarter/${selectedQuarter.value}`)
    ])
    goalRecords.value = goalRes.data
    assistRecords.value = assistRes.data
    goalkeeperRecords.value = gkRes.data
  } catch (error) {
    ElMessage.error('获取记录失败')
  }
}

const openAddQuarterDialog = () => {
  isEdit.value = false
  quarterForm.value = {
    id: null,
    matchId: selectedMatch.value,
    quarterNum: quarters.value.length + 1,
    team1Name: '',
    team2Name: '',
    team1Score: 0,
    team2Score: 0
  }
  dialogVisible.value = true
}

const openEditQuarterDialog = (row) => {
  isEdit.value = true
  quarterForm.value = { ...row }
  dialogVisible.value = true
}

const handleSaveQuarter = async () => {
  if (!quarterForm.value.team1Name || !quarterForm.value.team2Name) {
    ElMessage.warning('请填写两支队伍名称')
    return
  }
  try {
    await axios.post('/api/match-stats/quarter', quarterForm.value)
    ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
    dialogVisible.value = false
    fetchQuarters()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleDeleteQuarter = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该小节吗?', '提示', { type: 'warning' })
    await axios.delete(`/api/match-stats/quarter/${row.id}`)
    ElMessage.success('删除成功')
    if (selectedQuarter.value === row.id) {
      selectedQuarter.value = null
      goalRecords.value = []
      assistRecords.value = []
      goalkeeperRecords.value = []
    }
    fetchQuarters()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSelectQuarter = (quarter) => {
  selectedQuarter.value = quarter.id
  fetchQuarterRecords()
}

const openGoalDialog = () => {
  selectedPlayer.value = null
  selectedTeam.value = ''
  goalDialogVisible.value = true
}

const openAssistDialog = () => {
  selectedPlayer.value = null
  selectedTeam.value = ''
  assistDialogVisible.value = true
}

const openGoalkeeperDialog = () => {
  selectedPlayer.value = null
  selectedTeam.value = ''
  goalkeeperDialogVisible.value = true
}

watch(selectedPlayer, (newVal) => {
  if (newVal && newVal.teamName) {
    selectedTeam.value = newVal.teamName
  }
})

const handleSaveGoal = async () => {
  if (!selectedPlayer.value || !selectedTeam.value) {
    ElMessage.warning('请选择球员和队伍')
    return
  }
  try {
    await axios.post('/api/match-stats/goal', {
      quarterId: selectedQuarter.value,
      playerId: selectedPlayer.value.id,
      playerName: selectedPlayer.value.name,
      teamName: selectedTeam.value
    })
    ElMessage.success('添加成功')
    goalDialogVisible.value = false
    fetchQuarterRecords()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const handleSaveAssist = async () => {
  if (!selectedPlayer.value || !selectedTeam.value) {
    ElMessage.warning('请选择球员和队伍')
    return
  }
  try {
    await axios.post('/api/match-stats/assist', {
      quarterId: selectedQuarter.value,
      playerId: selectedPlayer.value.id,
      playerName: selectedPlayer.value.name,
      teamName: selectedTeam.value
    })
    ElMessage.success('添加成功')
    assistDialogVisible.value = false
    fetchQuarterRecords()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const handleSaveGoalkeeper = async () => {
  if (!selectedPlayer.value || !selectedTeam.value) {
    ElMessage.warning('请选择球员和队伍')
    return
  }
  try {
    await axios.post('/api/match-stats/goalkeeper', {
      quarterId: selectedQuarter.value,
      playerId: selectedPlayer.value.id,
      playerName: selectedPlayer.value.name,
      teamName: selectedTeam.value
    })
    ElMessage.success('添加成功')
    goalkeeperDialogVisible.value = false
    fetchQuarterRecords()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const handleDeleteGoal = async (row) => {
  try {
    await axios.delete(`/api/match-stats/goal/${row.id}`)
    ElMessage.success('删除成功')
    fetchQuarterRecords()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const handleDeleteAssist = async (row) => {
  try {
    await axios.delete(`/api/match-stats/assist/${row.id}`)
    ElMessage.success('删除成功')
    fetchQuarterRecords()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const handleDeleteGoalkeeper = async (row) => {
  try {
    await axios.delete(`/api/match-stats/goalkeeper/${row.id}`)
    ElMessage.success('删除成功')
    fetchQuarterRecords()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

watch(selectedMatch, () => {
  selectedQuarter.value = null
  quarters.value = []
  goalRecords.value = []
  assistRecords.value = []
  goalkeeperRecords.value = []
  fetchQuarters()
})

onMounted(() => {
  fetchMatches()
  fetchPlayers()
  fetchTeams()
})
</script>

<template>
  <el-card>
    <template #header>
      <span>比赛数据录入</span>
    </template>

    <el-form :inline="true" style="margin-bottom: 20px">
      <el-form-item label="选择比赛">
        <el-select v-model="selectedMatch" placeholder="请选择比赛" style="width: 300px">
          <el-option v-for="match in matches" :key="match.id" :label="`${match.matchDate} - ${match.location}`" :value="match.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :disabled="!selectedMatch" @click="openAddQuarterDialog">新增小节</el-button>
      </el-form-item>
    </el-form>

    <el-empty v-if="!selectedMatch" description="请先选择一场比赛" />

    <div v-else style="display: flex; gap: 20px">
      <div style="width: 350px">
        <el-table :data="quarters" stripe @row-click="handleSelectQuarter">
          <el-table-column prop="quarterNum" label="小节" width="80" />
          <el-table-column prop="team1Name" label="队伍1" />
          <el-table-column prop="team2Name" label="队伍2" />
          <el-table-column label="比分" width="100">
            <template #default="{ row }">
              {{ row.team1Score }}-{{ row.team2Score }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" @click.stop="openEditQuarterDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click.stop="handleDeleteQuarter(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-if="selectedQuarter" style="flex: 1">
        <el-form :inline="true" style="margin-bottom: 15px">
          <el-form-item label="进球">
            <el-button size="small" type="primary" @click="openGoalDialog">+ 添加进球</el-button>
          </el-form-item>
          <el-form-item label="助攻">
            <el-button size="small" type="success" @click="openAssistDialog">+ 添加助攻</el-button>
          </el-form-item>
          <el-form-item label="守门">
            <el-button size="small" type="warning" @click="openGoalkeeperDialog">+ 添加守门</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-card shadow="hover">
              <template #header>进球</template>
              <div v-for="g in goalRecords" :key="g.id" style="display: flex; justify-content: space-between; margin-bottom: 5px">
                <span>{{ g.playerName }} ({{ g.teamName }})</span>
                <el-button size="small" type="danger" @click="handleDeleteGoal(g)">删除</el-button>
              </div>
              <el-empty v-if="goalRecords.length === 0" description="暂无数据" :image-size="50" />
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover">
              <template #header>助攻</template>
              <div v-for="a in assistRecords" :key="a.id" style="display: flex; justify-content: space-between; margin-bottom: 5px">
                <span>{{ a.playerName }} ({{ a.teamName }})</span>
                <el-button size="small" type="danger" @click="handleDeleteAssist(a)">删除</el-button>
              </div>
              <el-empty v-if="assistRecords.length === 0" description="暂无数据" :image-size="50" />
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover">
              <template #header>守门</template>
              <div v-for="gk in goalkeeperRecords" :key="gk.id" style="display: flex; justify-content: space-between; margin-bottom: 5px">
                <span>{{ gk.playerName }} ({{ gk.teamName }})</span>
                <el-button size="small" type="danger" @click="handleDeleteGoalkeeper(gk)">删除</el-button>
              </div>
              <el-empty v-if="goalkeeperRecords.length === 0" description="暂无数据" :image-size="50" />
            </el-card>
          </el-col>
        </el-row>
      </div>

      <div v-else style="flex: 1; display: flex; align-items: center; justify-content: center; color: #999">
        请选择左侧小节查看详情
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑小节' : '新增小节'" width="450px">
      <el-form :model="quarterForm" label-width="80px">
        <el-form-item label="小节">
          <el-input-number v-model="quarterForm.quarterNum" :min="1" />
        </el-form-item>
        <el-form-item label="队伍1">
          <el-select v-model="quarterForm.team1Name" placeholder="请选择队伍" style="width: 100%">
            <el-option v-for="t in teams" :key="t.id" :label="t.name" :value="t.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="队伍2">
          <el-select v-model="quarterForm.team2Name" placeholder="请选择队伍" style="width: 100%">
            <el-option v-for="t in teams" :key="t.id" :label="t.name" :value="t.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="比分">
          <el-input-number v-model="quarterForm.team1Score" :min="0" style="width: 100px" /> :
          <el-input-number v-model="quarterForm.team2Score" :min="0" style="width: 100px" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveQuarter">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="goalDialogVisible" title="添加进球" width="400px">
      <el-form label-width="80px">
        <el-form-item label="球员">
          <el-select v-model="selectedPlayer" placeholder="请选择球员" style="width: 100%">
            <el-option v-for="p in players" :key="p.id" :label="p.name" :value="p" />
          </el-select>
        </el-form-item>
        <el-form-item label="队伍">
          <el-select v-model="selectedTeam" placeholder="请选择队伍" style="width: 100%">
            <el-option v-if="selectedQuarter && quarters.find(q => q.id === selectedQuarter)" :label="quarters.find(q => q.id === selectedQuarter).team1Name" :value="quarters.find(q => q.id === selectedQuarter).team1Name" />
            <el-option v-if="selectedQuarter && quarters.find(q => q.id === selectedQuarter)" :label="quarters.find(q => q.id === selectedQuarter).team2Name" :value="quarters.find(q => q.id === selectedQuarter).team2Name" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="goalDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveGoal">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="assistDialogVisible" title="添加助攻" width="400px">
      <el-form label-width="80px">
        <el-form-item label="球员">
          <el-select v-model="selectedPlayer" placeholder="请选择球员" style="width: 100%">
            <el-option v-for="p in players" :key="p.id" :label="p.name" :value="p" />
          </el-select>
        </el-form-item>
        <el-form-item label="队伍">
          <el-select v-model="selectedTeam" placeholder="请选择队伍" style="width: 100%">
            <el-option v-if="selectedQuarter && quarters.find(q => q.id === selectedQuarter)" :label="quarters.find(q => q.id === selectedQuarter).team1Name" :value="quarters.find(q => q.id === selectedQuarter).team1Name" />
            <el-option v-if="selectedQuarter && quarters.find(q => q.id === selectedQuarter)" :label="quarters.find(q => q.id === selectedQuarter).team2Name" :value="quarters.find(q => q.id === selectedQuarter).team2Name" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assistDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveAssist">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="goalkeeperDialogVisible" title="添加守门" width="400px">
      <el-form label-width="80px">
        <el-form-item label="球员">
          <el-select v-model="selectedPlayer" placeholder="请选择球员" style="width: 100%">
            <el-option v-for="p in players" :key="p.id" :label="p.name" :value="p" />
          </el-select>
        </el-form-item>
        <el-form-item label="队伍">
          <el-select v-model="selectedTeam" placeholder="请选择队伍" style="width: 100%">
            <el-option v-if="selectedQuarter && quarters.find(q => q.id === selectedQuarter)" :label="quarters.find(q => q.id === selectedQuarter).team1Name" :value="quarters.find(q => q.id === selectedQuarter).team1Name" />
            <el-option v-if="selectedQuarter && quarters.find(q => q.id === selectedQuarter)" :label="quarters.find(q => q.id === selectedQuarter).team2Name" :value="quarters.find(q => q.id === selectedQuarter).team2Name" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="goalkeeperDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveGoalkeeper">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
