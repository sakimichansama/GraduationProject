<template>
  <div class="product-manager">
    <div class="glass-card">
      <div class="card-header">
        <span><el-icon><Box /></el-icon> Product List</span>
        <el-button class="gradient-btn" @click="openAddDialog">
          <el-icon><Plus /></el-icon> Add Product
        </el-button>
      </div>
      <el-table :data="productList" stripe class="modern-table" :row-class-name="tableRowClassName">
        <el-table-column prop="name" label="Name" min-width="150" />
        <el-table-column prop="specification" label="Spec" min-width="120" />
        <el-table-column prop="category" label="Category" min-width="100" />
        <el-table-column prop="unit" label="Unit" width="80" />
        <el-table-column prop="purchasePrice" label="Purchase Price" width="130">
          <template #default="{ row }">¥{{ row.purchasePrice }}</template>
        </el-table-column>
        <el-table-column prop="salePrice" label="Sale Price" width="120">
          <template #default="{ row }">¥{{ row.salePrice }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="Stock" width="100" />
        <el-table-column prop="minStock" label="Min Stock" width="100" />
        <el-table-column label="Actions" width="160" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button class="action-btn edit-btn" @click="editProduct(row)">Edit</el-button>
              <el-button class="action-btn delete-btn" @click="deleteProduct(row.id)">Delete</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" class="modern-dialog">
      <el-form :model="form" label-width="120px" class="dialog-form">
        <el-form-item label="Name">
          <el-input v-model="form.name" class="modern-input" placeholder="Product name" />
        </el-form-item>
        <el-form-item label="Specification">
          <el-input v-model="form.specification" class="modern-input" placeholder="e.g., 500ml" />
        </el-form-item>
        <el-form-item label="Category">
          <el-input v-model="form.category" class="modern-input" placeholder="e.g., Burger, Beverage" />
        </el-form-item>
        <el-form-item label="Unit">
          <el-input v-model="form.unit" class="modern-input" placeholder="bottle, bag, box" />
        </el-form-item>
        <el-form-item label="Purchase Price">
          <el-input-number v-model="form.purchasePrice" :min="0" :precision="2" controls-position="right" class="modern-number" />
        </el-form-item>
        <el-form-item label="Sale Price">
          <el-input-number v-model="form.salePrice" :min="0" :precision="2" controls-position="right" class="modern-number" />
        </el-form-item>
        <el-form-item label="Min Stock">
          <el-input-number v-model="form.minStock" :min="0" controls-position="right" class="modern-number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" class="outline-btn">Cancel</el-button>
        <el-button type="primary" class="gradient-btn" @click="saveProduct">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Box, Plus } from '@element-plus/icons-vue'

const productList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('Add Product')
const form = ref({})

const loadProducts = async () => {
  const data = await request.get('/products')
  productList.value = data
}

const openAddDialog = () => {
  dialogTitle.value = 'Add Product'
  form.value = {}
  dialogVisible.value = true
}

const editProduct = (row) => {
  dialogTitle.value = 'Edit Product'
  form.value = { ...row }
  dialogVisible.value = true
}

const saveProduct = async () => {
  if (form.value.id) {
    await request.put(`/products/${form.value.id}`, form.value)
    ElMessage.success('Product updated successfully')
  } else {
    await request.post('/products', form.value)
    ElMessage.success('Product added successfully')
  }
  dialogVisible.value = false
  loadProducts()
}

const deleteProduct = (id) => {
  ElMessageBox.confirm('Are you sure to delete this product?', 'Confirm', { type: 'warning' }).then(async () => {
    await request.delete(`/products/${id}`)
    ElMessage.success('Product deleted successfully')
    loadProducts()
  })
}

const tableRowClassName = ({ rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.product-manager {
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

/* 表格样式：统一行高和内边距 */
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
.modern-number :deep(.el-input-number__input) {
  border-radius: 20px;
}
.dialog-form {
  margin-top: 8px;
}
</style>
