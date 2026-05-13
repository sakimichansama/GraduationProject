<template>
  <div class="employee-manager">
    <div class="glass-card">
      <div class="card-header">
        <span><el-icon><User /></el-icon> Employee List</span>
        <el-button class="gradient-btn" @click="openAddDialog">
          <el-icon><Plus /></el-icon> Add Employee
        </el-button>
      </div>
      <el-table :data="employeeList" stripe class="modern-table" :row-class-name="tableRowClassName">
        <el-table-column prop="name" label="Name" min-width="120" />
        <el-table-column prop="phone" label="Phone" min-width="130" />
        <el-table-column prop="idCard" label="ID Card" min-width="180" />
        <el-table-column prop="hireDate" label="Hire Date" width="120" />
        <el-table-column prop="position" label="Position" min-width="120" />
        <el-table-column prop="monthlySalary" label="Monthly Salary" width="140">
          <template #default="{ row }">¥{{ row.monthlySalary || '-' }}</template>
        </el-table-column>
        <el-table-column label="Actions" width="160" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button class="action-btn edit-btn" @click="editEmployee(row)">Edit</el-button>
              <el-button class="action-btn delete-btn" @click="deleteEmployee(row.id)">Delete</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" class="modern-dialog">
      <el-form :model="form" label-width="100px" class="dialog-form">
        <el-form-item label="Name">
          <el-input v-model="form.name" placeholder="Enter name" class="modern-input" />
        </el-form-item>
        <el-form-item label="Phone">
          <el-input v-model="form.phone" placeholder="Phone number" class="modern-input" />
        </el-form-item>
        <el-form-item label="ID Card">
          <el-input v-model="form.idCard" placeholder="ID card number" class="modern-input" />
        </el-form-item>
        <el-form-item label="Hire Date">
          <el-date-picker v-model="form.hireDate" type="date" value-format="YYYY-MM-DD" class="modern-datepicker" />
        </el-form-item>
        <el-form-item label="Position">
          <el-input v-model="form.position" placeholder="Position" class="modern-input" />
        </el-form-item>
        <el-form-item label="Monthly Salary">
          <el-input-number v-model="form.monthlySalary" :min="0" controls-position="right" class="modern-number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" class="outline-btn">Cancel</el-button>
        <el-button type="primary" class="gradient-btn" @click="saveEmployee">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, User } from '@element-plus/icons-vue'

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

const tableRowClassName = ({ rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

onMounted(() => loadEmployees())
</script>

<style scoped>
.employee-manager {
  max-width: 1400px;
  margin: 0 auto;
  padding: 4px;
}

/* 毛玻璃卡片 */
.glass-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  border-radius: 32px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  border: 1px solid rgba(228, 0, 43, 0.15);
  margin-bottom: 24px;
  overflow: hidden;
}
.glass-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 35px -8px rgba(0, 0, 0, 0.12);
  border-color: rgba(228, 0, 43, 0.4);
}

/* 卡片头部 */
.card-header {
  font-size: 18px;
  font-weight: 600;
  padding: 20px 24px 12px 24px;
  border-bottom: 2px solid rgba(228, 0, 43, 0.2);
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #1e293b;
}
.card-header .el-icon {
  color: #e4002b;
  margin-right: 8px;
}
.card-header span {
  display: flex;
  align-items: center;
}

/* 渐变按钮（白色文字） */
.gradient-btn {
  background: linear-gradient(95deg, #e4002b 0%, #ff5e7e 100%);
  border: none;
  border-radius: 40px;
  padding: 8px 20px;
  font-weight: 600;
  color: white !important;
  box-shadow: 0 4px 10px rgba(228, 0, 43, 0.3);
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.gradient-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 6px 14px rgba(228, 0, 43, 0.4);
  color: white;
}
.outline-btn {
  border-radius: 40px;
  border: 1px solid #cbd5e1;
  color: #475569;
  background: white;
  padding: 8px 20px;
}
.outline-btn:hover {
  border-color: #e4002b;
  color: #e4002b;
  background: rgba(228, 0, 43, 0.05);
}

/* 表格样式：统一内边距和行高 */
.modern-table {
  margin: 16px 24px 24px 24px;
  width: calc(100% - 48px);
  border-radius: 20px;
  overflow: hidden;
}
:deep(.el-table th) {
  background-color: #f1f5f9;
  color: #1e293b;
  font-weight: 600;
  font-size: 14px;
  border-bottom: none;
  padding: 14px 0; /* 统一表头内边距 */
}
:deep(.el-table td) {
  border-bottom: 1px solid #eef2ff;
  padding: 14px 0; /* 统一单元格内边距，增大垂直间距，与其他模块一致 */
}
:deep(.el-table .cell) {
  padding: 0 16px; /* 左右内边距保持舒适 */
}
:deep(.el-table .even-row) {
  background-color: #ffffff;
}
:deep(.el-table .odd-row) {
  background-color: #fafcff;
}
:deep(.el-table__row:hover > td) {
  background-color: #ffefef !important;
}
:deep(.el-table__inner-wrapper::before) {
  display: none;
}

/* 操作按钮组容器 */
.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}
/* 基础按钮样式：圆角、浅色背景、过渡 */
.action-btn {
  border-radius: 24px;
  padding: 5px 14px;
  font-size: 13px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  background-color: #f1f5f9;
  color: #334155;
}
.action-btn:hover {
  transform: translateY(-1px);
}
/* 编辑按钮 */
.edit-btn {
  background-color: #eef2ff;
  color: #4f46e5;
}
.edit-btn:hover {
  background-color: #e0e7ff;
  color: #4338ca;
  box-shadow: 0 2px 6px rgba(79, 70, 229, 0.2);
}
/* 删除按钮 */
.delete-btn {
  background-color: #fef2f2;
  color: #e4002b;
}
.delete-btn:hover {
  background-color: #fee2e2;
  color: #c0001f;
  box-shadow: 0 2px 6px rgba(228, 0, 43, 0.2);
}

/* 对话框毛玻璃效果 */
:deep(.modern-dialog .el-dialog) {
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
}
:deep(.modern-dialog .el-dialog__header) {
  border-bottom: 1px solid rgba(228, 0, 43, 0.2);
  padding: 20px 24px;
}
:deep(.modern-dialog .el-dialog__title) {
  font-weight: 700;
  color: #1e293b;
}
:deep(.modern-dialog .el-dialog__body) {
  padding: 24px;
}
:deep(.modern-dialog .el-dialog__footer) {
  border-top: 1px solid #eef2ff;
  padding: 16px 24px;
}

/* 表单内输入框样式 */
.modern-input {
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 14px;
  background: white;
  transition: all 0.2s;
  outline: none;
}
.modern-input:focus {
  border-color: #e4002b;
  box-shadow: 0 0 0 3px rgba(228, 0, 43, 0.2);
}
.modern-datepicker {
  border-radius: 20px;
}
.modern-number :deep(.el-input-number__input) {
  border-radius: 20px;
}
.dialog-form {
  margin-top: 8px;
}
</style>