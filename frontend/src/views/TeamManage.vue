<script setup>
import { ref, onMounted, watch, inject } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const teams = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const seasonState = inject('seasonState')
const form = ref({
  id: null,
  name: '',
  season: null
})

const fetchTeams = async () => {
  loading.value = true
  try {
    const season = seasonState?.current || localStorage.getItem('currentSeason')
    const res = await axios.get('/api/teams', { params: { season } })
    teams.value = res.data
  } catch (error) {
    ElMessage.error('获取队伍列表失败')
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = { id: null, name: '', season: seasonState.current }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.name) {
    ElMessage.warning('请输入队伍名称')
    return
  }
  try {
    if (isEdit.value) {
      await axios.put(`/api/teams/${form.value.id}`, form.value)
      ElMessage.success('更新成功')
    } else {
      form.value.season = seasonState.current
      await axios.post('/api/teams', form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchTeams()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该队伍吗?', '提示', { type: 'warning' })
    await axios.delete(`/api/teams/${row.id}`)
    ElMessage.success('删除成功')
    fetchTeams()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

watch(() => seasonState?.current, () => {
  fetchTeams()
})

onMounted(() => {
  fetchTeams()
})
</script>

<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span>队伍管理</span>
        <el-button type="primary" @click="openAddDialog">添加队伍</el-button>
      </div>
    </template>

    <el-table :data="teams" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="队伍名称" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑队伍' : '添加队伍'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="队伍名称">
          <el-input v-model="form.name" placeholder="请输入队伍名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
