<template>
  <div class="sales-daily">
    <!-- Statistics card (retain the original gradient style; do not modify) -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="8">
        <div class="stat-card" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <div class="stat-icon"><el-icon><Money /></el-icon></div>
          <div class="stat-info">
            <div class="stat-label">Total Sales</div>
            <div class="stat-value">¥ {{ totalAmountSum.toFixed(2) }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <div class="stat-icon"><el-icon><Document /></el-icon></div>
          <div class="stat-info">
            <div class="stat-label">Total Orders</div>
            <div class="stat-value">{{ totalOrderCount }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <div class="stat-icon"><el-icon><TrendCharts /></el-icon></div>
          <div class="stat-info">
            <div class="stat-label">Avg Daily Sales</div>
            <div class="stat-value">¥ {{ avgDailySales.toFixed(2) }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <div class="glass-card form-card">
      <div class="card-header">
        <span><el-icon><Edit /></el-icon> Enter Daily Sales Data</span>
      </div>
      <el-form :inline="true" class="modern-form">
        <el-form-item label="Date">
          <input type="date" v-model="salesForm.saleDate" class="modern-input" />
        </el-form-item>
        <el-form-item label="Orders">
          <el-input-number v-model="salesForm.orderCount" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="Total Amount">
          <el-input-number v-model="salesForm.totalAmount" :precision="2" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="gradient-btn" @click="submitSales">Save</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- search -->
    <div class="glass-card table-card">
      <div class="card-header">
        <span><el-icon><Search /></el-icon> Sales Records Query</span>
      </div>
      <div class="search-bar">
        <div class="date-input-group">
          <label>From</label>
          <input type="date" v-model="startDate" class="modern-input" />
        </div>
        <div class="date-input-group">
          <label>To</label>
          <input type="date" v-model="endDate" class="modern-input" />
        </div>
        <el-button type="primary" class="gradient-btn" @click="loadSalesList">Search</el-button>
        <el-button plain class="outline-btn" @click="resetDateRange">Last 7 Days</el-button>
      </div>
      <el-table :data="salesList" stripe class="modern-table" :row-class-name="tableRowClassName">
        <el-table-column prop="saleDate" label="Date" width="120" />
        <el-table-column prop="orderCount" label="Orders" width="100" />
        <el-table-column prop="totalAmount" label="Sales Amount (¥)" width="140" />
        <el-table-column label="Actions" width="150">
          <template #default="{ row }">
            <el-button link type="primary" class="action-btn" @click="editSales(row)">Edit</el-button>
            <el-button link type="danger" class="action-btn" @click="deleteSales(row.id)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- edit -->
    <el-dialog v-model="editDialog" title="Edit Sales Record" width="400px" class="modern-dialog">
      <el-form>
        <el-form-item label="Orders">
          <el-input-number v-model="editForm.orderCount" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="Total Amount">
          <el-input-number v-model="editForm.totalAmount" :precision="2" :min="0" controls-position="right" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialog = false" class="outline-btn">Cancel</el-button>
        <el-button type="primary" class="gradient-btn" @click="updateSales">Confirm</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Money, Document, TrendCharts, Edit, Search } from '@element-plus/icons-vue'

const salesForm = ref({ saleDate: '', orderCount: 0, totalAmount: 0 })
const startDate = ref('')
const endDate = ref('')
const salesList = ref([])
const editDialog = ref(false)
const editForm = ref({ id: null, orderCount: 0, totalAmount: 0 })

const totalAmountSum = computed(() => salesList.value.reduce((sum, item) => sum + item.totalAmount, 0))
const totalOrderCount = computed(() => salesList.value.reduce((sum, item) => sum + item.orderCount, 0))
const avgDailySales = computed(() => salesList.value.length ? totalAmountSum.value / salesList.value.length : 0)

const loadSalesList = async () => {
  if (!startDate.value || !endDate.value) return ElMessage.warning('Please select a date range')
  try {
    const res = await request.get('/sales', { params: { start: startDate.value, end: endDate.value } })
    salesList.value = res
  } catch { ElMessage.error('Failed to load') }
}

const resetDateRange = () => {
  const today = new Date()
  const weekAgo = new Date(today.getTime() - 7 * 24 * 3600 * 1000)
  startDate.value = weekAgo.toISOString().slice(0, 10)
  endDate.value = today.toISOString().slice(0, 10)
  loadSalesList()
}

const submitSales = async () => {
  if (!salesForm.value.saleDate) return ElMessage.warning('Please select a date')
  try {
    await request.post('/sales', salesForm.value)
    ElMessage.success('Saved successfully')
    salesForm.value = { saleDate: '', orderCount: 0, totalAmount: 0 }
    loadSalesList()
  } catch { ElMessage.error('Save failed') }
}

const editSales = (row) => {
  editForm.value = { ...row }
  editDialog.value = true
}
const updateSales = async () => {
  try {
    await request.put(`/sales/${editForm.value.id}`, editForm.value)
    ElMessage.success('Updated successfully')
    editDialog.value = false
    loadSalesList()
  } catch { ElMessage.error('Update failed') }
}
const deleteSales = async (id) => {
  try {
    await request.delete(`/sales/${id}`)
    ElMessage.success('Deleted successfully')
    loadSalesList()
  } catch { ElMessage.error('Delete failed') }
}

const tableRowClassName = ({ rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

onMounted(() => {
  resetDateRange()
})
</script>

<style scoped>
.sales-daily {
  max-width: 1400px;
  margin: 0 auto;
  padding: 4px;
}

/* calculate */
.stat-cards {
  margin-bottom: 24px;
}
.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 16px;
  color: #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transition: transform 0.2s;
}
.stat-card:hover {
  transform: translateY(-3px);
}
.stat-icon {
  font-size: 42px;
  margin-right: 20px;
}
.stat-info {
  flex: 1;
}
.stat-label {
  font-size: 14px;
  opacity: 0.9;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-top: 5px;
}

.glass-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  border-radius: 32px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  border: 1px solid rgba(228, 0, 43, 0.15);
  margin-bottom: 24px;
}
.glass-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 35px -8px rgba(0, 0, 0, 0.12);
  border-color: rgba(228, 0, 43, 0.4);
}

/* head */
.card-header {
  font-size: 18px;
  font-weight: 600;
  padding: 20px 24px 12px 24px;
  border-bottom: 2px solid rgba(228, 0, 43, 0.2);
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #1e293b;
}
.card-header .el-icon {
  color: #e4002b;
}

/* sheet padding */
.modern-form {
  padding: 0 24px 24px 24px;
}

/* search */
.search-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 20px;
  padding: 0 24px 20px 24px;
  border-bottom: 1px solid #eef2ff;
  margin-bottom: 20px;
}
.date-input-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.date-input-group label {
  font-size: 13px;
  font-weight: 500;
  color: #4b5563;
}

/* enter */
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

/* button */
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
.outline-btn {
  border-radius: 40px;
  border: 1px solid #cbd5e1;
  color: #475569;
  background: white;
  padding: 8px 24px;
}
.outline-btn:hover {
  border-color: #e4002b;
  color: #e4002b;
  background: rgba(228, 0, 43, 0.05);
}
.action-btn {
  font-weight: 500;
}
.action-btn:hover {
  opacity: 0.8;
}

/* sheet */
.modern-table {
  border-radius: 20px;
  overflow: hidden;
  margin: 0 24px 24px 24px;
  width: calc(100% - 48px);
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
</style>