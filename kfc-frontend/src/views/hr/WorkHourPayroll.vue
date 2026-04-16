<template>
  <div>
    <el-card style="margin-bottom: 20px">
      <template #header>Log Daily Work Hours</template>
      <el-form :inline="true">
        <el-form-item label="Employee">
          <el-select v-model="hourForm.employeeId" placeholder="Select employee">
            <el-option v-for="e in employeeList" :key="e.id" :label="e.name" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="Date">
          <el-date-picker v-model="hourForm.workDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="Hours">
          <el-input-number v-model="hourForm.hours" :min="0" :step="0.5" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitWorkHour">Log Hours</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header>Monthly Payroll (with Attendance Bonus)</template>
      <div style="margin-bottom: 15px">
        <el-date-picker v-model="month" type="month" value-format="YYYY-MM" @change="loadPayroll" />
      </div>
      <el-table :data="payrollList" border>
        <el-table-column prop="employeeName" label="Name" />
        <el-table-column prop="totalHours" label="Total Hours" />
        <el-table-column prop="baseSalary" label="Base Salary" />
        <el-table-column prop="fullAttendanceBonus" label="Attendance Bonus">
          <template #default="{ row }">${{ row.fullAttendanceBonus || 0 }}</template>
        </el-table-column>
        <el-table-column prop="totalSalary" label="Net Pay" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

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

onMounted(() => {
  loadEmployees()
  const now = new Date()
  month.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  loadPayroll()
})
</script>