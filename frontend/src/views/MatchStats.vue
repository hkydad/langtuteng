<script setup>
import { ref, onMounted, watch, inject, computed } from 'vue'
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
const currentSeason = inject('seasonState')
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
const selectedPlayerId = ref(null)
const selectedTeam = ref('')
const goalRecords = ref([])
const assistRecords = ref([])
const goalkeeperRecords = ref([])

const filteredPlayersByTeam = computed(() => {
  if (!selectedTeamName.value) return []
  return players.value.filter(p => p.teamName === selectedTeamName.value)
})

const currentQuarter = computed(() => {
  return quarters.value.find(q => q.id === selectedQuarter.value)
})

const selectedTeamName = computed(() => {
  if (!selectedTeam.value) return ''
  const q = currentQuarter.value
  if (!q) return ''
  if (selectedTeam.value === q.id + '-team1') return q.team1Name
  if (selectedTeam.value === q.id + '-team2') return q.team2Name
  return selectedTeam.value
})

const matchStandings = computed(() => {
  const map = {}
  for (const q of quarters.value) {
    for (const [name, scored, conceded] of [[q.team1Name, q.team1Score, q.team2Score], [q.team2Name, q.team2Score, q.team1Score]]) {
      if (!name) continue
      if (!map[name]) map[name] = { teamName: name, points: 0, goalsScored: 0, goalsConceded: 0 }
      map[name].goalsScored += scored
      map[name].goalsConceded += conceded
      if (scored > conceded) map[name].points += 3
      else if (scored === conceded) map[name].points += 1
    }
  }
  return Object.values(map).sort((a, b) => b.points - a.points || (b.goalsScored - b.goalsConceded) - (a.goalsScored - a.goalsConceded) || b.goalsScored - a.goalsScored)
})

const fetchMatches = async () => {
  try {
    const res = await axios.get('/api/matches', { params: { season: currentSeason?.current } })
    matches.value = res.data
  } catch (error) {
    ElMessage.error('获取比赛列表失败')
  }
}

const fetchPlayers = async () => {
  try {
    const season = currentSeason?.current || localStorage.getItem('currentSeason')
    const res = await axios.get('/api/players', { params: { season } })
    players.value = res.data
  } catch (error) {
    ElMessage.error('获取球员列表失败')
  }
}

const fetchTeams = async () => {
  try {
    const season = currentSeason?.current || localStorage.getItem('currentSeason')
    const res = await axios.get('/api/teams', { params: { season } })
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
  selectedPlayerId.value = null
  selectedTeam.value = ''
  goalDialogVisible.value = true
}

const openAssistDialog = () => {
  selectedPlayerId.value = null
  selectedTeam.value = ''
  assistDialogVisible.value = true
}

const openGoalkeeperDialog = () => {
  selectedPlayerId.value = null
  selectedTeam.value = ''
  goalkeeperDialogVisible.value = true
}

const handleSaveGoal = async () => {
  if (!selectedPlayerId.value || !selectedTeam.value) {
    ElMessage.warning('请选择球员和队伍')
    return
  }
  try {
    const player = players.value.find(p => p.id === selectedPlayerId.value)
    await axios.post('/api/match-stats/goal', {
      quarterId: selectedQuarter.value,
      playerId: player.id,
      playerName: player.name,
      teamName: selectedTeamName.value
    })
    ElMessage.success('添加成功')
    goalDialogVisible.value = false
    fetchQuarterRecords()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const handleSaveAssist = async () => {
  if (!selectedPlayerId.value || !selectedTeam.value) {
    ElMessage.warning('请选择球员和队伍')
    return
  }
  try {
    const player = players.value.find(p => p.id === selectedPlayerId.value)
    await axios.post('/api/match-stats/assist', {
      quarterId: selectedQuarter.value,
      playerId: player.id,
      playerName: player.name,
      teamName: selectedTeamName.value
    })
    ElMessage.success('添加成功')
    assistDialogVisible.value = false
    fetchQuarterRecords()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const handleSaveGoalkeeper = async () => {
  if (!selectedPlayerId.value || !selectedTeam.value) {
    ElMessage.warning('请选择球员和队伍')
    return
  }
  try {
    const player = players.value.find(p => p.id === selectedPlayerId.value)
    await axios.post('/api/match-stats/goalkeeper', {
      quarterId: selectedQuarter.value,
      playerId: player.id,
      playerName: player.name,
      teamName: selectedTeamName.value
    })
    ElMessage.success('添加成功')
    goalkeeperDialogVisible.value = false
    fetchQuarterRecords()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

watch(selectedTeam, () => {
  selectedPlayerId.value = null
})

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

watch(() => currentSeason?.current, () => {
  selectedMatch.value = null
  selectedQuarter.value = null
  quarters.value = []
  goalRecords.value = []
  assistRecords.value = []
  goalkeeperRecords.value = []
  fetchMatches()
  fetchPlayers()
  fetchTeams()
})

const selectedMatchData = computed(() => {
  return matches.value.find(m => m.id === selectedMatch.value)
})

const handleExport = async () => {
  if (!selectedMatch.value || quarters.value.length === 0) {
    ElMessage.warning('请先选择比赛并添加小节数据')
    return
  }
  try {
    const lines = []
    const m = selectedMatchData.value
    lines.push(m?.matchDate || '')
    lines.push('')

    for (const q of quarters.value) {
      const score = `${q.team1Score}-${q.team2Score}`
      lines.push(`  第${q.quarterNum}节 ${q.team1Name} vs ${q.team2Name} ${score}`)

      // Fetch records for this quarter
      const [goals, assists, gkRecords] = await Promise.all([
        axios.get(`/api/match-stats/goal/quarter/${q.id}`),
        axios.get(`/api/match-stats/assist/quarter/${q.id}`),
        axios.get(`/api/match-stats/goalkeeper/quarter/${q.id}`)
      ])

      const goalList = goals.data
      const assistList = assists.data
      const gkList = gkRecords.data

      if (goalList.length > 0) {
        const byTeam = {}
        goalList.forEach(g => {
          if (!byTeam[g.teamName]) byTeam[g.teamName] = []
          byTeam[g.teamName].push(g.playerName)
        })
        for (const [team, players] of Object.entries(byTeam)) {
          lines.push(`进球 ${players.map(p => `${team} ${p}`).join('，')}`)
        }
      }

      if (assistList.length > 0) {
        const byTeam = {}
        assistList.forEach(a => {
          if (!byTeam[a.teamName]) byTeam[a.teamName] = []
          byTeam[a.teamName].push(a.playerName)
        })
        for (const [team, players] of Object.entries(byTeam)) {
          lines.push(`助攻 ${players.map(p => `${team} ${p}`).join('，')}`)
        }
      }

      if (gkList.length > 0) {
        const byTeam = {}
        gkList.forEach(g => {
          if (!byTeam[g.teamName]) byTeam[g.teamName] = []
          byTeam[g.teamName].push(g.playerName)
        })
        for (const [team, players] of Object.entries(byTeam)) {
          lines.push(`守门 ${players.map(p => `${team} ${p}`).join('，')}`)
        }
      }
      lines.push('')
    }

    if (matchStandings.value.length > 0) {
      const rankLabels = ['冠军', '亚军', '季军']
      matchStandings.value.forEach((t, i) => {
        const label = rankLabels[i] || (i + 1)
        lines.push(`${label} ${t.teamName} ${t.points}`)
      })
    }

    const blob = new Blob([lines.join('\n')], { type: 'text/plain;charset=utf-8' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `比赛数据_${m?.matchDate || '未知'}.txt`
    a.click()
    URL.revokeObjectURL(url)
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

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
      <el-form-item>
        <el-button type="success" :disabled="!selectedMatch || quarters.length === 0" @click="handleExport">导出数据</el-button>
      </el-form-item>
    </el-form>

    <el-empty v-if="!selectedMatch" description="请先选择一场比赛" />

    <template v-if="selectedMatch">
      <el-card v-if="matchStandings.length > 0" style="margin-bottom: 20px" shadow="never">
        <template #header>当前比赛积分排名</template>
        <el-table :data="matchStandings" stripe size="small">
          <el-table-column prop="teamName" label="队伍" />
          <el-table-column prop="points" label="积分" width="80" sortable />
          <el-table-column prop="goalsScored" label="进球" width="80" />
          <el-table-column prop="goalsConceded" label="失球" width="80" />
          <el-table-column label="净胜球" width="80">
            <template #default="{ row }">{{ row.goalsScored - row.goalsConceded >= 0 ? '+' : '' }}{{ row.goalsScored - row.goalsConceded }}</template>
          </el-table-column>
        </el-table>
      </el-card>

      <div style="display: flex; gap: 20px">
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
    </template>

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
        <el-form-item label="队伍">
          <el-select v-model="selectedTeam" placeholder="请选择队伍" style="width: 100%">
            <el-option v-if="currentQuarter" :label="currentQuarter.team1Name" :value="currentQuarter.id + '-team1'" />
            <el-option v-if="currentQuarter" :label="currentQuarter.team2Name" :value="currentQuarter.id + '-team2'" />
          </el-select>
        </el-form-item>
        <el-form-item label="球员">
          <el-select v-model="selectedPlayerId" placeholder="请先选择队伍" style="width: 100%" :disabled="!selectedTeam">
            <el-option v-for="p in filteredPlayersByTeam" :key="p.id" :label="p.name" :value="p.id" />
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
        <el-form-item label="队伍">
          <el-select v-model="selectedTeam" placeholder="请选择队伍" style="width: 100%">
            <el-option v-if="currentQuarter" :label="currentQuarter.team1Name" :value="currentQuarter.id + '-team1'" />
            <el-option v-if="currentQuarter" :label="currentQuarter.team2Name" :value="currentQuarter.id + '-team2'" />
          </el-select>
        </el-form-item>
        <el-form-item label="球员">
          <el-select v-model="selectedPlayerId" placeholder="请先选择队伍" style="width: 100%" :disabled="!selectedTeam">
            <el-option v-for="p in filteredPlayersByTeam" :key="p.id" :label="p.name" :value="p.id" />
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
        <el-form-item label="队伍">
          <el-select v-model="selectedTeam" placeholder="请选择队伍" style="width: 100%">
            <el-option v-if="currentQuarter" :label="currentQuarter.team1Name" :value="currentQuarter.id + '-team1'" />
            <el-option v-if="currentQuarter" :label="currentQuarter.team2Name" :value="currentQuarter.id + '-team2'" />
          </el-select>
        </el-form-item>
        <el-form-item label="球员">
          <el-select v-model="selectedPlayerId" placeholder="请先选择队伍" style="width: 100%" :disabled="!selectedTeam">
            <el-option v-for="p in filteredPlayersByTeam" :key="p.id" :label="p.name" :value="p.id" />
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
