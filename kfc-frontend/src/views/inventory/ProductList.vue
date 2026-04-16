<template>
  <div>
    <div style="margin-bottom: 20px">
      <el-button type="primary" @click="openAddDialog">Add Product</el-button>
    </div>

    <el-table :data="productList" border stripe style="width: 100%">
      <el-table-column prop="name" label="Name" width="150" />
      <el-table-column prop="specification" label="Spec" width="120" />
      <el-table-column prop="category" label="Category" width="100" />
      <el-table-column prop="unit" label="Unit" width="80" />
      <el-table-column prop="purchasePrice" label="Purchase Price" width="120">
        <template #default="{ row }">${{ row.purchasePrice }}</template>
      </el-table-column>
      <el-table-column prop="salePrice" label="Sale Price" width="120">
        <template #default="{ row }">${{ row.salePrice }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="Stock" width="100" />
      <el-table-column prop="minStock" label="Min Stock" width="100" />
      <el-table-column label="Actions" fixed="right" width="150">
        <template #default="{ row }">
          <el-button link type="primary" @click="editProduct(row)">Edit</el-button>
          <el-button link type="danger" @click="deleteProduct(row.id)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="Name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="Specification">
          <el-input v-model="form.specification" />
        </el-form-item>
        <el-form-item label="Category">
          <el-input v-model="form.category" />
        </el-form-item>
        <el-form-item label="Unit">
          <el-input v-model="form.unit" placeholder="e.g., bottle, bag, box" />
        </el-form-item>
        <el-form-item label="Purchase Price">
          <el-input-number v-model="form.purchasePrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="Sale Price">
          <el-input-number v-model="form.salePrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="Min Stock">
          <el-input-number v-model="form.minStock" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="saveProduct">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

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

onMounted(() => {
  loadProducts()
})
</script>
