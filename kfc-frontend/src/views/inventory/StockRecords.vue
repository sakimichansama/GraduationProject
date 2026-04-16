<template>
  <div>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="Inbound" name="in">
        <el-form :inline="true" style="margin-top: 20px">
          <el-form-item label="Product">
            <el-select v-model="inForm.productId" placeholder="Select product">
              <el-option v-for="p in productList" :key="p.id" :label="p.name" :value="p.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="Quantity">
            <el-input-number v-model="inForm.quantity" :min="1" />
          </el-form-item>
          <el-form-item label="Unit Price">
            <el-input-number v-model="inForm.price" :min="0" :precision="2" />
          </el-form-item>
          <el-form-item label="Supplier">
            <el-input v-model="inForm.supplier" style="width: 180px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitInStock">Confirm Inbound</el-button>
          </el-form-item>
        </el-form>
        <el-divider>Inbound History</el-divider>
        <el-table :data="inRecords" border>
          <el-table-column label="Product">
            <template #default="{ row }">{{ getProductName(row.productId) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="Quantity" />
          <el-table-column prop="price" label="Unit Price" />
          <el-table-column prop="supplier" label="Supplier" />
          <el-table-column prop="createTime" label="Time" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="Outbound" name="out">
        <el-form :inline="true" style="margin-top: 20px">
          <el-form-item label="Product">
            <el-select v-model="outForm.productId" placeholder="Select product">
              <el-option v-for="p in productList" :key="p.id" :label="p.name" :value="p.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="Quantity">
            <el-input-number v-model="outForm.quantity" :min="1" />
          </el-form-item>
          <el-form-item label="Reason">
            <el-select v-model="outForm.reason">
              <el-option label="Sales" value="销售" />
              <el-option label="Giveaway" value="赠送" />
              <el-option label="Internal Use" value="内部领用" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitOutStock">Confirm Outbound</el-button>
          </el-form-item>
        </el-form>
        <el-divider>Outbound History</el-divider>
        <el-table :data="outRecords" border>
          <el-table-column label="Product">
            <template #default="{ row }">{{ getProductName(row.productId) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="Quantity" />
          <el-table-column label="Reason">
            <template #default="{ row }">{{ getReasonLabel(row.reason) }}</template>
          </el-table-column>
          <el-table-column prop="createTime" label="Time" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

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

// 将中文原因映射为英文显示
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

onMounted(() => {
  loadProducts()
  loadRecords()
})
</script>



