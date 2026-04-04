<script setup>
import { ref, onMounted, watch, inject } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const players = ref([])
const teams = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const seasonState = inject('seasonState')
const form = ref({
  id: null,
  name: '',
  phone: '',
  memberLevel: null,
  teamName: '',
  season: null
})

const fetchPlayers = async () => {
  loading.value = true
  try {
    const season = seasonState?.current || localStorage.getItem('currentSeason')
    const res = await axios.get('/api/players', { params: { season } })
    players.value = res.data
  } catch (error) {
    ElMessage.error('获取球员列表失败')
  } finally {
    loading.value = false
  }
}

const fetchTeams = async () => {
  try {
    const season = seasonState?.current || localStorage.getItem('currentSeason')
    const res = await axios.get('/api/teams', { params: { season } })
    teams.value = res.data
  } catch (error) {
    ElMessage.error('获取队伍列表失败')
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = { id: null, name: '', phone: '', memberLevel: null, teamName: '', season: seasonState.current }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.name) {
    ElMessage.warning('请输入球员姓名')
    return
  }
  try {
    if (isEdit.value) {
      await axios.put(`/api/players/${form.value.id}`, form.value)
      ElMessage.success('更新成功')
    } else {
      form.value.season = seasonState.current
      await axios.post('/api/players', form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchPlayers()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该球员吗?', '提示', {
      type: 'warning'
    })
    await axios.delete(`/api/players/${row.id}`)
    ElMessage.success('删除成功')
    fetchPlayers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

watch(() => seasonState?.current, () => {
  fetchPlayers()
  fetchTeams()
})

onMounted(() => {
  fetchPlayers()
  fetchTeams()
})
</script>

<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span>球员管理</span>
        <el-button type="primary" @click="openAddDialog">添加球员</el-button>
      </div>
    </template>

    <el-table :data="players" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="memberLevel" label="会员等级" width="120">
        <template #default="{ row }">
          {{ row.memberLevel === 500 ? '500' : row.memberLevel === 200 ? '200' : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="teamName" label="所属队伍" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑球员' : '添加球员'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="会员等级">
          <el-select v-model="form.memberLevel" placeholder="请选择会员等级" style="width: 100%">
            <el-option :value="500" label="500" />
            <el-option :value="200" label="200" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属队伍">
          <el-select v-model="form.teamName" placeholder="请选择队伍" style="width: 100%">
            <el-option v-for="t in teams" :key="t.id" :label="t.name" :value="t.name" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
