<template>
  <div>
    <!-- CREATE -->
    <el-card style="margin-bottom: 20px">
      <template #header>Enter Daily Sales Data</template>
      <el-form :inline="true">

        <el-form-item label="Date">
          <input type="date" v-model="salesForm.saleDate" />
        </el-form-item>

        <el-form-item label="Orders">
          <el-input-number v-model="salesForm.orderCount" :min="0" />
        </el-form-item>

        <el-form-item label="Total Amount">
          <el-input-number v-model="salesForm.totalAmount" :precision="2" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitSales">Save</el-button>
        </el-form-item>

      </el-form>
    </el-card>

    <!-- SEARCH -->
    <el-card>
      <template #header>Sales Records</template>

      <div style="margin-bottom: 15px">
        <input type="date" v-model="startDate" />
        <span style="margin: 0 10px">to</span>
        <input type="date" v-model="endDate" />

        <el-button type="primary" @click="loadSalesList">
          Search
        </el-button>
      </div>

      <el-table :data="salesList" border>
        <el-table-column prop="saleDate" label="Date" />
        <el-table-column prop="orderCount" label="Orders" />
        <el-table-column prop="totalAmount" label="Total Amount" />
        <el-table-column label="Actions">
          <template #default="{ row }">
            <el-button link @click="editSales(row)">Edit</el-button>
            <el-button link type="danger" @click="deleteSales(row.id)">
              Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- EDIT -->
    <el-dialog v-model="editDialog" title="Edit Sales Record">
      <el-form>
        <el-form-item label="Orders">
          <el-input-number v-model="editForm.orderCount" />
        </el-form-item>
        <el-form-item label="Amount">
          <el-input-number v-model="editForm.totalAmount" />
        </el-form-item>
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

// STATE
const salesForm = ref({
  saleDate: '',
  orderCount: 0,
  totalAmount: 0
})

const startDate = ref('')
const endDate = ref('')
const salesList = ref([])

const editDialog = ref(false)
const editForm = ref({
  id: null,
  orderCount: 0,
  totalAmount: 0
})

// LOAD
const loadSalesList = async () => {
  try {
    if (!startDate.value || !endDate.value) {
      return ElMessage.warning('Select date range')
    }

    const url = `/sales?start=${startDate.value}&end=${endDate.value}`

    console.log(url)

    const res = await request.get(url)
    salesList.value = res
  } catch (e) {
    console.error(e)
    ElMessage.error('Load failed')
  }
}

// CREATE
const submitSales = async () => {
  try {
    await request.post('/sales', salesForm.value)
    ElMessage.success('Saved')
    loadSalesList()
  } catch {
    ElMessage.error('Save failed')
  }
}

// EDIT
const editSales = (row) => {
  editForm.value = { ...row }
  editDialog.value = true
}

// UPDATE
const updateSales = async () => {
  try {
    await request.put(`/sales/${editForm.value.id}`, editForm.value)
    ElMessage.success('Updated')
    editDialog.value = false
    loadSalesList()
  } catch {
    ElMessage.error('Update failed')
  }
}

// DELETE
const deleteSales = async (id) => {
  try {
    await request.delete(`/sales/${id}`)
    ElMessage.success('Deleted')
    loadSalesList()
  } catch {
    ElMessage.error('Delete failed')
  }
}

// INIT
onMounted(() => {
  const today = new Date().toISOString().slice(0, 10)
  const weekAgo = new Date(Date.now() - 7 * 86400000)
    .toISOString()
    .slice(0, 10)

  startDate.value = weekAgo
  endDate.value = today

  loadSalesList()
})
</script>