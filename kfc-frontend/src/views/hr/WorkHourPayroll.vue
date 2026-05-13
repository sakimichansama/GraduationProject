<template>
  <div class="workhour-payroll">
    <!-- 录入工时卡片（毛玻璃风格） -->
    <div class="glass-card form-card">
      <div class="card-header">
        <span><el-icon><Timer /></el-icon> Log Daily Work Hours</span>
      </div>
      <el-form :inline="true" class="modern-form">
        <el-form-item label="Employee">
          <el-select v-model="hourForm.employeeId" placeholder="Select employee" class="modern-select">
            <el-option v-for="e in employeeList" :key="e.id" :label="e.name" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="Date">
          <el-date-picker v-model="hourForm.workDate" type="date" value-format="YYYY-MM-DD" class="modern-datepicker" />
        </el-form-item>
        <el-form-item label="Hours">
          <el-input-number v-model="hourForm.hours" :min="0" :step="0.5" controls-position="right" class="modern-number" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="gradient-btn" @click="submitWorkHour">Log Hours</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 月度薪资卡片（毛玻璃风格） -->
    <div class="glass-card payroll-card">
      <div class="card-header">
        <span><el-icon><Money /></el-icon> Monthly Payroll (with Attendance Bonus)</span>
      </div>
      <div class="search-bar">
        <div class="date-input-group">
          <label>Select Month</label>
          <el-date-picker v-model="month" type="month" value-format="YYYY-MM" placeholder="Choose month" @change="loadPayroll" class="modern-picker" />
        </div>
      </div>
      <el-table :data="payrollList" stripe class="modern-table" :row-class-name="tableRowClassName">
        <el-table-column prop="employeeName" label="Name" min-width="120" />
        <el-table-column prop="totalHours" label="Total Hours" width="120" />
        <el-table-column prop="baseSalary" label="Base Salary" width="130" />
        <el-table-column prop="fullAttendanceBonus" label="Attendance Bonus" width="150">
          <template #default="{ row }">¥{{ row.fullAttendanceBonus || 0 }}</template>
        </el-table-column>
        <el-table-column prop="totalSalary" label="Net Pay" width="130" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Timer, Money } from '@element-plus/icons-vue'

const employeeList = ref([])
const hourForm = ref({ employeeId: null, workDate: '', hours: 0 })
const month = ref('')
const payrollList = ref([])

const loadEmployees = async () => {
  const data = await request.get('/employees')
  employeeList.value = data
}

const submitWorkHour = async () => {
  if (!hourForm.value.employeeId) return ElMessage.warning('Please select an employee')
  await request.post('/workhours', null, { params: hourForm.value })
  ElMessage.success('Work hours logged successfully')
  hourForm.value = { employeeId: null, workDate: '', hours: 0 }
  if (month.value) loadPayroll()
}

const loadPayroll = async () => {
  if (!month.value) return
  const data = await request.get('/payroll/month', { params: { month: month.value } })
  payrollList.value = data
}

const tableRowClassName = ({ rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

onMounted(() => {
  loadEmployees()
  const now = new Date()
  month.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  loadPayroll()
})
</script>

<style scoped>
.workhour-payroll {
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
  margin-bottom: 28px;
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
  gap: 8px;
  color: #1e293b;
}
.card-header .el-icon {
  color: #e4002b;
}

/* 表单区域 */
.modern-form {
  padding: 12px 24px 24px 24px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}
.modern-select {
  width: 180px;
}
.modern-datepicker {
  width: 160px;
}
.modern-number {
  width: 130px;
}
.gradient-btn {
  background: linear-gradient(95deg, #e4002b 0%, #ff5e7e 100%);
  border: none;
  border-radius: 40px;
  padding: 8px 24px;
  font-weight: 600;
  box-shadow: 0 4px 10px rgba(228, 0, 43, 0.3);
  transition: all 0.2s;
}
.gradient-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 6px 14px rgba(228, 0, 43, 0.4);
}

/* 查询栏（月份选择） */
.search-bar {
  padding: 12px 24px 12px 24px;
}
.date-input-group {
  display: flex;
  align-items: center;
  gap: 12px;
}
.date-input-group label {
  font-size: 14px;
  font-weight: 500;
  color: #4b5563;
}
.modern-picker {
  border-radius: 20px;
  width: 160px;
}

/* 表格样式 */
.modern-table {
  margin: 0 24px 24px 24px;
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
}
:deep(.el-table td) {
  border-bottom: 1px solid #eef2ff;
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
</style>