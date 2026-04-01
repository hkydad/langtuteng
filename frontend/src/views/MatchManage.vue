<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const matches = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  id: null,
  matchDate: '',
  location: ''
})

const fetchMatches = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/matches')
    matches.value = res.data
  } catch (error) {
    ElMessage.error('获取比赛列表失败')
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = { id: null, matchDate: '', location: '' }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.matchDate) {
    ElMessage.warning('请选择比赛日期')
    return
  }
  try {
    if (isEdit.value) {
      await axios.put(`/api/matches/${form.value.id}`, form.value)
      ElMessage.success('更新成功')
    } else {
      await axios.post('/api/matches', form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchMatches()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该比赛吗?', '提示', {
      type: 'warning'
    })
    await axios.delete(`/api/matches/${row.id}`)
    ElMessage.success('删除成功')
    fetchMatches()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(fetchMatches)
</script>

<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span>比赛管理</span>
        <el-button type="primary" @click="openAddDialog">添加比赛</el-button>
      </div>
    </template>

    <el-table :data="matches" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="matchDate" label="比赛日期" />
      <el-table-column prop="location" label="比赛地点" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑比赛' : '添加比赛'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="比赛日期">
          <el-date-picker v-model="form.matchDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="比赛地点">
          <el-input v-model="form.location" placeholder="请输入比赛地点" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
