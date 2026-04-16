<template>
  <div>
    <el-card style="margin-bottom: 20px">
      <template #header>Enter Daily Sales Data</template>
      <el-form :inline="true">
        <el-form-item label="Date">
          <el-date-picker v-model="salesForm.saleDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="Orders">
          <el-input-number v-model="salesForm.orderCount" :min="0" />
        </el-form-item>
        <el-form-item label="Total Amount">
          <el-input-number v-model="salesForm.totalAmount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitSales">Save</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header>Sales Records</template>
      <div style="margin-bottom: 15px">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="to" @change="loadSalesList" />
      </div>
      <el-table :data="salesList" border>
        <el-table-column prop="saleDate" label="Date" />
        <el-table-column prop="orderCount" label="Orders" />
        <el-table-column prop="totalAmount" label="Total Amount" />
        <el-table-column label="Actions">
          <template #default="{ row }">
            <el-button link type="primary" @click="editSales(row)">Edit</el-button>
            <el-button link type="danger" @click="deleteSales(row.id)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="editDialog" title="Edit Sales Record" width="400px">
      <el-form :model="editForm">
        <el-form-item label="Orders"><el-input-number v-model="editForm.orderCount" /></el-form-item>
        <el-form-item label="Total Amount"><el-input-number v-model="editForm.totalAmount" :precision="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialog = false">Cancel</el-button>
        <el-button type="primary" @click="updateSales">Confirm</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const salesForm = ref({ saleDate: '', orderCount: 0, totalAmount: 0 })
const dateRange = ref([])
const salesList = ref([])
const editDialog = ref(false)
const editForm = ref({ id: null, orderCount: 0, totalAmount: 0 })

const loadSalesList = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) return
  const start = dateRange.value[0]
  const end = dateRange.value[1]
  const data = await request.get('/sales', { params: { start, end } })
  salesList.value = data
}

const submitSales = async () => {
  if (!salesForm.value.saleDate) return ElMessage.warning('Please select a date')
  await request.post('/sales', salesForm.value)
  ElMessage.success('Sales data saved successfully')
  salesForm.value = { saleDate: '', orderCount: 0, totalAmount: 0 }
  loadSalesList()
}

const editSales = (row) => {
  editForm.value = { id: row.id, orderCount: row.orderCount, totalAmount: row.totalAmount }
  editDialog.value = true
}

const updateSales = async () => {
  await request.put(`/sales/${editForm.value.id}`, editForm.value)
  ElMessage.success('Sales record updated successfully')
  editDialog.value = false
  loadSalesList()
}

const deleteSales = async (id) => {
  await request.delete(`/sales/${id}`)
  ElMessage.success('Sales record deleted successfully')
  loadSalesList()
}

onMounted(() => {
  const today = new Date()
  const weekAgo = new Date(today)
  weekAgo.setDate(today.getDate() - 7)
  dateRange.value = [weekAgo.toISOString().slice(0,10), today.toISOString().slice(0,10)]
  loadSalesList()
})
</script>