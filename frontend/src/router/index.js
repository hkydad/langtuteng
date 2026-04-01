import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import PlayerManage from '../views/PlayerManage.vue'
import MatchManage from '../views/MatchManage.vue'
import AttendanceRecord from '../views/AttendanceRecord.vue'
import AttendanceSummary from '../views/AttendanceSummary.vue'

const routes = [
  { path: '/', name: 'Dashboard', component: Dashboard },
  { path: '/players', name: 'PlayerManage', component: PlayerManage },
  { path: '/matches', name: 'MatchManage', component: MatchManage },
  { path: '/attendance', name: 'AttendanceRecord', component: AttendanceRecord },
  { path: '/report', name: 'AttendanceSummary', component: AttendanceSummary }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
