<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const goalLeaderboard = ref([])
const assistLeaderboard = ref([])
const goalkeeperLeaderboard = ref([])
const loading = ref(false)

const fetchLeaderboard = async () => {
  loading.value = true
  try {
    const [goalRes, assistRes, gkRes] = await Promise.all([
      axios.get('/api/match-stats/leaderboard/goals'),
      axios.get('/api/match-stats/leaderboard/assists'),
      axios.get('/api/match-stats/leaderboard/goalkeepers')
    ])
    goalLeaderboard.value = goalRes.data
    assistLeaderboard.value = assistRes.data
    goalkeeperLeaderboard.value = gkRes.data
  } catch (error) {
    ElMessage.error('获取榜单数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchLeaderboard()
})
</script>

<template>
  <el-card>
    <template #header>
      <span>数据总榜</span>
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
  </el-card>
</template>
