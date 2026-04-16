<template>
  <div>
    <div style="margin-bottom: 20px">
      <el-button type="primary" @click="openAddDialog">Add Employee</el-button>
    </div>
    <el-table :data="employeeList" border stripe>
      <el-table-column prop="name" label="Name" />
      <el-table-column prop="phone" label="Phone" />
      <el-table-column prop="idCard" label="ID Card" />
      <el-table-column prop="hireDate" label="Hire Date" />
      <el-table-column prop="position" label="Position" />
      <el-table-column prop="monthlySalary" label="Monthly Salary">
        <template #default="{ row }">${{ row.monthlySalary || '-' }}</template>
      </el-table-column>
      <el-table-column label="Actions">
        <template #default="{ row }">
          <el-button link type="primary" @click="editEmployee(row)">Edit</el-button>
          <el-button link type="danger" @click="deleteEmployee(row.id)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="Name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="Phone"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="ID Card"><el-input v-model="form.idCard" /></el-form-item>
        <el-form-item label="Hire Date"><el-date-picker v-model="form.hireDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="Position"><el-input v-model="form.position" /></el-form-item>
        <el-form-item label="Monthly Salary"><el-input-number v-model="form.monthlySalary" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="saveEmployee">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const employeeList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('Add Employee')
const form = ref({})

const loadEmployees = async () => {
  const data = await request.get('/employees')
  employeeList.value = data
}

const openAddDialog = () => {
  dialogTitle.value = 'Add Employee'
  form.value = {}
  dialogVisible.value = true
}

const editEmployee = (row) => {
  dialogTitle.value = 'Edit Employee'
  form.value = { ...row }
  dialogVisible.value = true
}

const saveEmployee = async () => {
  if (form.value.id) {
    await request.put(`/employees/${form.value.id}`, form.value)
    ElMessage.success('Employee updated successfully')
  } else {
    await request.post('/employees', form.value)
    ElMessage.success('Employee added successfully')
  }
  dialogVisible.value = false
  loadEmployees()
}

const deleteEmployee = (id) => {
  ElMessageBox.confirm('Are you sure to delete this employee?', 'Confirm', { type: 'warning' }).then(async () => {
    await request.delete(`/employees/${id}`)
    ElMessage.success('Employee deleted successfully')
    loadEmployees()
  })
}

onMounted(() => loadEmployees())
</script>