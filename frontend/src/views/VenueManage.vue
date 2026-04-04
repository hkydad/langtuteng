<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const venues = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  id: null,
  name: '',
  address: ''
})

const fetchVenues = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/venues')
    venues.value = res.data
  } catch (error) {
    ElMessage.error('获取场地列表失败')
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = { id: null, name: '', address: '' }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.name) {
    ElMessage.warning('请输入场地名称')
    return
  }
  try {
    if (isEdit.value) {
      await axios.put(`/api/venues/${form.value.id}`, form.value)
      ElMessage.success('更新成功')
    } else {
      await axios.post('/api/venues', form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchVenues()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该场地吗?', '提示', { type: 'warning' })
    await axios.delete(`/api/venues/${row.id}`)
    ElMessage.success('删除成功')
    fetchVenues()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(fetchVenues)
</script>

<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span>场地管理</span>
        <el-button type="primary" @click="openAddDialog">添加场地</el-button>
      </div>
    </template>

    <el-table :data="venues" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="场地名称" />
      <el-table-column prop="address" label="地址" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑场地' : '添加场地'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="场地名称">
          <el-input v-model="form.name" placeholder="请输入场地名称" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
