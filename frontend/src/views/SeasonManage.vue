<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const seasons = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({
  id: null,
  year: null,
  name: ''
})

const fetchSeasons = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/seasons')
    seasons.value = res.data
  } catch (error) {
    ElMessage.error('获取赛季列表失败')
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  form.value = { id: null, year: null, name: '' }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.year) {
    ElMessage.warning('请输入赛季年份')
    return
  }
  try {
    await axios.post('/api/seasons', {
      year: form.value.year,
      name: form.value.name || (form.value.year + '赛季')
    })
    ElMessage.success('添加成功')
    dialogVisible.value = false
    fetchSeasons()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该赛季吗?', '提示', { type: 'warning' })
    await axios.delete(`/api/seasons/${row.id}`)
    ElMessage.success('删除成功')
    fetchSeasons()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(fetchSeasons)
</script>

<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span>赛季管理</span>
        <el-button type="primary" @click="openAddDialog">添加赛季</el-button>
      </div>
    </template>

    <el-table :data="seasons" v-loading="loading" stripe>
      <el-table-column prop="year" label="年份" width="120" />
      <el-table-column prop="name" label="赛季名称" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="添加赛季" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="年份">
          <el-input-number v-model="form.year" :min="2000" :max="2100" placeholder="请输入年份" style="width: 100%" />
        </el-form-item>
        <el-form-item label="赛季名称">
          <el-input v-model="form.name" :placeholder="form.year ? form.year + '赛季' : '请输入赛季名称'" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
