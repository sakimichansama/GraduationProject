<template>
  <div>
    <el-card style="margin-bottom: 20px">
      <div style="margin-bottom: 15px">
        <el-date-picker v-model="month" type="month" value-format="YYYY-MM" @change="loadReport" />
        <el-button style="margin-left: 15px" @click="exportExcel">Export Excel</el-button>
      </div>
      <el-descriptions title="Monthly Profit Summary" :column="2" border>
        <el-descriptions-item label="Total Sales">${{ summary.totalSales || 0 }}</el-descriptions-item>
        <el-descriptions-item label="Material Cost">${{ summary.materialCost || 0 }}</el-descriptions-item>
        <el-descriptions-item label="Labor Cost">${{ summary.laborCost || 0 }}</el-descriptions-item>
        <el-descriptions-item label="Waste Cost">${{ summary.wasteCost || 0 }}</el-descriptions-item>
        <el-descriptions-item label="Gross Profit">${{ summary.grossProfit || 0 }}</el-descriptions-item>
        <el-descriptions-item label="Net Profit">${{ summary.netProfit || 0 }}</el-descriptions-item>
        <el-descriptions-item label="Profit Margin">{{ summary.profitMargin || 0 }}%</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>Daily Profit Trend (This Month)</template>
          <div ref="dailyChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>Cost Breakdown</template>
          <div ref="costChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import request from '@/utils/request'
import * as echarts from 'echarts'

const month = ref('')
const summary = ref({})
const dailyChartRef = ref(null)
const costChartRef = ref(null)
let dailyChart = null
let costChart = null

const loadReport = async () => {
  if (!month.value) return
  const data = await request.get('/profit/month', { params: { month: month.value } })
  summary.value = data.summary
  renderDailyChart(data.dailyList)
  renderCostChart(data.costBreakdown)
}

const renderDailyChart = (dailyList) => {
  if (dailyChart) dailyChart.dispose()
  if (!dailyChartRef.value) return
  dailyChart = echarts.init(dailyChartRef.value)
  dailyChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: dailyList.map(d => d.date) },
    yAxis: { type: 'value', name: 'Profit ($)' },
    series: [{ type: 'line', data: dailyList.map(d => d.profit), smooth: true, areaStyle: {} }]
  })
}

const renderCostChart = (costBreakdown) => {
  if (costChart) costChart.dispose()
  if (!costChartRef.value) return
  costChart = echarts.init(costChartRef.value)
  costChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '50%',
      data: [
        { name: 'Material Cost', value: costBreakdown.materialCost },
        { name: 'Labor Cost', value: costBreakdown.laborCost },
        { name: 'Waste Cost', value: costBreakdown.wasteCost }
      ]
    }]
  })
}

const exportExcel = async () => {
  window.open(`/api/profit/export?month=${month.value}`)
}

onMounted(() => {
  const now = new Date()
  month.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  loadReport()
})

onUnmounted(() => {
  if (dailyChart) dailyChart.dispose()
  if (costChart) costChart.dispose()
})
</script>