import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import PlayerManage from '../views/PlayerManage.vue'
import MatchManage from '../views/MatchManage.vue'
import AttendanceRecord from '../views/AttendanceRecord.vue'
import AttendanceSummary from '../views/AttendanceSummary.vue'
import MatchStats from '../views/MatchStats.vue'
import StatsLeaderboard from '../views/StatsLeaderboard.vue'
import TeamManage from '../views/TeamManage.vue'

const routes = [
  { path: '/', name: 'Dashboard', component: Dashboard },
  { path: '/players', name: 'PlayerManage', component: PlayerManage },
  { path: '/matches', name: 'MatchManage', component: MatchManage },
  { path: '/attendance', name: 'AttendanceRecord', component: AttendanceRecord },
  { path: '/report', name: 'AttendanceSummary', component: AttendanceSummary },
  { path: '/match-stats', name: 'MatchStats', component: MatchStats },
  { path: '/stats-leaderboard', name: 'StatsLeaderboard', component: StatsLeaderboard },
  { path: '/teams', name: 'TeamManage', component: TeamManage }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
