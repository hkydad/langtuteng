<script setup>
import { ref, onMounted, watch, inject } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const goalLeaderboard = ref([])
const assistLeaderboard = ref([])
const goalkeeperLeaderboard = ref([])
const teamStandings = ref([])
const loading = ref(false)
const seasonState = inject('seasonState')

const fetchLeaderboard = async () => {
  loading.value = true
  try {
    const season = seasonState?.current || localStorage.getItem('currentSeason')
    const [goalRes, assistRes, gkRes, teamRes] = await Promise.all([
      axios.get('/api/match-stats/leaderboard/goals', { params: { season } }),
      axios.get('/api/match-stats/leaderboard/assists', { params: { season } }),
      axios.get('/api/match-stats/leaderboard/goalkeepers', { params: { season } }),
      axios.get('/api/match-stats/leaderboard/team-standings', { params: { season } })
    ])
    goalLeaderboard.value = goalRes.data
    assistLeaderboard.value = assistRes.data
    goalkeeperLeaderboard.value = gkRes.data
    teamStandings.value = teamRes.data
  } catch (error) {
    ElMessage.error('获取榜单数据失败')
  } finally {
    loading.value = false
  }
}

watch(() => seasonState?.current, () => {
  fetchLeaderboard()
})

onMounted(() => {
  fetchLeaderboard()
})

const handleExport = () => {
  const season = seasonState?.current || localStorage.getItem('currentSeason')
  const seasonText = season ? `${season}月赛总计` : '数据总榜'

  let lines = [`狼图腾${seasonText}\n`]

  // 总积分
  lines.push('总积分：')
  teamStandings.value.forEach(t => {
    lines.push(`${t.teamName} ${t.points}`)
  })
  lines.push('')

  // 进球榜
  lines.push('进球榜')
  goalLeaderboard.value.forEach(g => {
    lines.push(`${g.teamName} ${g.playerName} ${g.goalCount}`)
  })
  lines.push('')

  // 助攻榜
  lines.push('助攻榜')
  assistLeaderboard.value.forEach(a => {
    lines.push(`${a.teamName} ${a.playerName} ${a.assistCount}`)
  })
  lines.push('')

  // 门将榜
  lines.push('门将榜')
  goalkeeperLeaderboard.value.forEach(gk => {
    lines.push(`${gk.teamName} ${gk.playerName} ${gk.totalScore}`)
  })

  const blob = new Blob([lines.join('\n')], { type: 'text/plain;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `狼图腾${seasonText}.txt`
  a.click()
  URL.revokeObjectURL(url)
}
</script>

<template>
  <el-card>
    <template #header>
      <span>数据总榜</span>
      <el-button type="success" size="small" style="margin-left: 20px" @click="handleExport">导出数据</el-button>
    </template>

    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="8">
        <el-table :data="goalLeaderboard" v-loading="loading" stripe>
          <el-table-column prop="goalCount" label="进球数" width="100" sortable>
            <template #default="{ row }">
              <el-tag type="danger" size="large">{{ row.goalCount }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="playerName" label="球员姓名" />
          <el-table-column prop="teamName" label="队伍" />
        </el-table>
        <el-empty v-if="goalLeaderboard.length === 0 && !loading" description="暂无进球数据" />
      </el-col>

      <el-col :span="8">
        <el-table :data="assistLeaderboard" v-loading="loading" stripe>
          <el-table-column prop="assistCount" label="助攻数" width="100" sortable>
            <template #default="{ row }">
              <el-tag type="success" size="large">{{ row.assistCount }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="playerName" label="球员姓名" />
          <el-table-column prop="teamName" label="队伍" />
        </el-table>
        <el-empty v-if="assistLeaderboard.length === 0 && !loading" description="暂无助攻数据" />
      </el-col>

      <el-col :span="8">
        <el-table :data="goalkeeperLeaderboard" v-loading="loading" stripe>
          <el-table-column prop="totalScore" label="积分" width="100" sortable>
            <template #default="{ row }">
              <el-tag type="warning" size="large">{{ row.totalScore }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="playerName" label="球员姓名" />
          <el-table-column prop="teamName" label="队伍" />
          <el-table-column prop="cleanSheetCount" label="零封次数" width="100" />
        </el-table>
        <el-empty v-if="goalkeeperLeaderboard.length === 0 && !loading" description="暂无守门员数据" />
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="24">
        <el-table :data="teamStandings" v-loading="loading" stripe>
          <el-table-column prop="rank" label="名次" width="80" sortable>
            <template #default="{ row }">
              <el-tag :type="row.rank === 1 ? 'danger' : row.rank === 2 ? 'warning' : row.rank === 3 ? 'success' : 'info'" size="large">{{ row.rank }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="teamName" label="队伍" />
          <el-table-column prop="points" label="积分" width="80" sortable />
          <el-table-column prop="goalsScored" label="进球数" width="80" sortable />
          <el-table-column prop="goalsConceded" label="失球数" width="80" sortable />
          <el-table-column prop="goalDifference" label="净胜球" width="80" sortable>
            <template #default="{ row }">
              {{ row.goalDifference >= 0 ? '+' : '' }}{{ row.goalDifference }}
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="teamStandings.length === 0 && !loading" description="暂无积分数据" />
      </el-col>
    </el-row>
  </el-card>
</template>
