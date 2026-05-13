<template>
  <div class="stock-record-manager">
    <el-tabs v-model="activeTab" class="modern-tabs">
      <!-- 入库 tab -->
      <el-tab-pane label="Inbound" name="in">
        <div class="glass-card form-card">
          <div class="card-header">
            <span><el-icon><Upload /></el-icon> Add Inbound Record</span>
          </div>
          <el-form :inline="true" class="modern-form">
            <el-form-item label="Product">
              <el-select v-model="inForm.productId" placeholder="Select product" class="modern-select">
                <el-option v-for="p in productList" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="Quantity">
              <el-input-number v-model="inForm.quantity" :min="1" controls-position="right" class="modern-number" />
            </el-form-item>
            <el-form-item label="Unit Price">
              <el-input-number v-model="inForm.price" :min="0" :precision="2" controls-position="right" class="modern-number" />
            </el-form-item>
            <el-form-item label="Supplier">
              <el-input v-model="inForm.supplier" placeholder="Supplier name" class="modern-input" style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="gradient-btn" @click="submitInStock">
                <el-icon><Check /></el-icon> Confirm Inbound
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="glass-card table-card">
          <div class="card-header">
            <span><el-icon><List /></el-icon> Inbound History</span>
          </div>
          <el-table :data="inRecords" stripe class="modern-table" :row-class-name="tableRowClassName">
            <el-table-column label="Product" min-width="150">
              <template #default="{ row }">{{ getProductName(row.productId) }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="Quantity" width="100" />
            <el-table-column prop="price" label="Unit Price" width="120">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column prop="supplier" label="Supplier" min-width="150" />
            <el-table-column prop="createTime" label="Time" min-width="170" />
          </el-table>
        </div>
      </el-tab-pane>

      <!-- 出库 tab -->
      <el-tab-pane label="Outbound" name="out">
        <div class="glass-card form-card">
          <div class="card-header">
            <span><el-icon><Download /></el-icon> Add Outbound Record</span>
          </div>
          <el-form :inline="true" class="modern-form">
            <el-form-item label="Product">
              <el-select v-model="outForm.productId" placeholder="Select product" class="modern-select">
                <el-option v-for="p in productList" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="Quantity">
              <el-input-number v-model="outForm.quantity" :min="1" controls-position="right" class="modern-number" />
            </el-form-item>
            <el-form-item label="Reason">
              <el-select v-model="outForm.reason" class="modern-select">
                <el-option label="Sales" value="销售" />
                <el-option label="Giveaway" value="赠送" />
                <el-option label="Internal Use" value="内部领用" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="gradient-btn" @click="submitOutStock">
                <el-icon><Check /></el-icon> Confirm Outbound
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="glass-card table-card">
          <div class="card-header">
            <span><el-icon><List /></el-icon> Outbound History</span>
          </div>
          <el-table :data="outRecords" stripe class="modern-table" :row-class-name="tableRowClassName">
            <el-table-column label="Product" min-width="150">
              <template #default="{ row }">{{ getProductName(row.productId) }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="Quantity" width="100" />
            <el-table-column label="Reason" min-width="130">
              <template #default="{ row }">{{ getReasonLabel(row.reason) }}</template>
            </el-table-column>
            <el-table-column prop="createTime" label="Time" min-width="170" />
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Upload, Download, List, Check } from '@element-plus/icons-vue'

const activeTab = ref('in')
const productList = ref([])
const inForm = ref({ productId: null, quantity: 1, price: 0, supplier: '' })
const outForm = ref({ productId: null, quantity: 1, reason: '销售' })
const inRecords = ref([])
const outRecords = ref([])

const loadProducts = async () => {
  const data = await request.get('/products')
  productList.value = data
}

const loadRecords = async () => {
  inRecords.value = await request.get('/inventory/in-records')
  outRecords.value = await request.get('/inventory/out-records')
}

const getProductName = (productId) => {
  const product = productList.value.find(p => p.id === productId)
  return product ? product.name : 'Unknown'
}


const getReasonLabel = (reason) => {
  const map = {
    '销售': 'Sales',
    '赠送': 'Giveaway',
    '内部领用': 'Internal Use'
  }
  return map[reason] || reason
}

const submitInStock = async () => {
  if (!inForm.value.productId) return ElMessage.warning('Please select a product')
  await request.post('/inventory/in', null, { params: inForm.value })
  ElMessage.success('Inbound successful')
  inForm.value = { productId: null, quantity: 1, price: 0, supplier: '' }
  loadRecords()
}

const submitOutStock = async () => {
  if (!outForm.value.productId) return ElMessage.warning('Please select a product')
  await request.post('/inventory/out', null, { params: outForm.value })
  ElMessage.success('Outbound successful')
  outForm.value = { productId: null, quantity: 1, reason: '销售' }
  loadRecords()
}

const tableRowClassName = ({ rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

onMounted(() => {
  loadProducts()
  loadRecords()
})
</script>

<style scoped>
.stock-record-manager {
  max-width: 1400px;
  margin: 0 auto;
  padding: 4px;
}

/* 现代 Tabs 样式 */
.modern-tabs :deep(.el-tabs__header) {
  margin: 0 0 20px 0;
  background: transparent;
}
.modern-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}
.modern-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
  padding: 0 24px;
  height: 48px;
  line-height: 48px;
  color: #5b6e8c;
}
.modern-tabs :deep(.el-tabs__item.is-active) {
  color: #e4002b;
}
.modern-tabs :deep(.el-tabs__active-bar) {
  background-color: #e4002b;
  height: 3px;
  border-radius: 3px;
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
  gap: 8px;
  color: #1e293b;
}
.card-header .el-icon {
  color: #e4002b;
}
.card-header span {
  display: flex;
  align-items: center;
}

/* 表单区域 */
.modern-form {
  padding: 16px 24px 24px 24px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}
.modern-select {
  width: 180px;
}
.modern-number {
  width: 140px;
}
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

/* 渐变按钮 */
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

/* 表格样式 */
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
  padding: 14px 0;
}
:deep(.el-table td) {
  border-bottom: 1px solid #eef2ff;
  padding: 14px 0;
}
:deep(.el-table .cell) {
  padding: 0 16px;
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



