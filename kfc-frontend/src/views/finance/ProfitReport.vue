<template>
  <div class="profit-report">
    <!-- Control Bar Glass Card -->
    <div class="glass-card control-bar">
      <div class="date-picker-area">
        <el-date-picker v-model="month" type="month" value-format="YYYY-MM" placeholder="Select Month" @change="loadReport" class="modern-picker" />
      </div>
      <el-button type="primary" class="gradient-btn" @click="exportExcel">
        <el-icon><Download /></el-icon> Export Excel
      </el-button>
    </div>

    <!-- KPI Grid - Frosted glass cards, uniform light colors with red accents -->
    <div class="kpi-grid">
      <div class="kpi-card glass-card">
        <div class="kpi-icon"><el-icon><Money /></el-icon></div>
        <div class="kpi-value">¥{{ summary.totalSales || 0 }}</div>
        <div class="kpi-label">Total Sales</div>
      </div>
      <div class="kpi-card glass-card">
        <div class="kpi-icon"><el-icon><Box /></el-icon></div>
        <div class="kpi-value">¥{{ summary.materialCost || 0 }}</div>
        <div class="kpi-label">Material Cost</div>
      </div>
      <div class="kpi-card glass-card">
        <div class="kpi-icon"><el-icon><User /></el-icon></div>
        <div class="kpi-value">¥{{ summary.laborCost || 0 }}</div>
        <div class="kpi-label">Labor Cost</div>
      </div>
      <div class="kpi-card glass-card">
        <div class="kpi-icon"><el-icon><Delete /></el-icon></div>
        <div class="kpi-value">¥{{ summary.wasteCost || 0 }}</div>
        <div class="kpi-label">Waste Cost</div>
      </div>
      <div class="kpi-card glass-card">
        <div class="kpi-icon"><el-icon><TrendCharts /></el-icon></div>
        <div class="kpi-value">¥{{ summary.grossProfit || 0 }}</div>
        <div class="kpi-label">Gross Profit</div>
      </div>
      <div class="kpi-card glass-card">
        <div class="kpi-icon"><el-icon><Money /></el-icon></div>
        <div class="kpi-value">¥{{ summary.netProfit || 0 }}</div>
        <div class="kpi-label">Net Profit</div>
      </div>
      <div class="kpi-card glass-card">
        <div class="kpi-icon"><el-icon><TrendCharts /></el-icon></div>
        <div class="kpi-value">{{ summary.profitMargin || 0 }}%</div>
        <div class="kpi-label">Profit Margin</div>
      </div>
    </div>

    <!-- Dual Chart Area - Styling Container -->
    <div class="charts-grid">
      <div class="glass-card chart-card">
        <div class="chart-header">
          <span><el-icon><TrendCharts /></el-icon> Daily Profit Trend (This Month)</span>
        </div>
        <div ref="dailyChartRef" style="height: 360px; width: 100%;"></div>
      </div>
      <div class="glass-card chart-card">
        <div class="chart-header">
          <span><el-icon><PieChart /></el-icon> Cost Breakdown</span>
        </div>
        <div ref="costChartRef" style="height: 360px; width: 100%;"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import request from '@/utils/request'
import * as echarts from 'echarts'
import { Download, TrendCharts, PieChart, Money, Box, User, Delete } from '@element-plus/icons-vue'

const month = ref('')
const summary = ref({})
const dailyChartRef = ref(null)
const costChartRef = ref(null)
let dailyChart = null, costChart = null

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
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { top: 40, left: 50, right: 20, bottom: 30, containLabel: true },
    xAxis: {
      type: 'category',
      data: dailyList.map(d => d.date),
      axisLabel: { rotate: 30, fontWeight: 500, color: '#4b5563' },
      axisLine: { lineStyle: { color: '#cbd5e1' } }
    },
    yAxis: {
      type: 'value',
      name: 'Profit (¥)',
      nameTextStyle: { fontWeight: 600, color: '#475569' },
      splitLine: { lineStyle: { type: 'dashed', color: '#e2e8f0' } }
    },
    series: [{
      type: 'line',
      data: dailyList.map(d => d.profit),
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { width: 4, color: '#e4002b', shadowBlur: 10, shadowColor: '#e4002b' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(228, 0, 43, 0.4)' },
          { offset: 1, color: 'rgba(228, 0, 43, 0.02)' }
        ])
      },
      itemStyle: { color: '#e4002b', borderColor: '#fff', borderWidth: 2 }
    }]
  })
}

const renderCostChart = (costBreakdown) => {
  if (costChart) costChart.dispose()
  if (!costChartRef.value) return
  costChart = echarts.init(costChartRef.value)
  costChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {d}% (¥{c})' },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: { show: true, formatter: '{b}', fontWeight: 500, position: 'outside', fontFamily: 'system-ui, -apple-system, "Segoe UI", Roboto' },
      emphasis: { scale: true, label: { show: true } },
      data: [
        { value: costBreakdown.materialCost, name: 'Material Cost', itemStyle: { color: '#f97316' } },
        { value: costBreakdown.laborCost, name: 'Labor Cost', itemStyle: { color: '#3b82f6' } },
        { value: costBreakdown.wasteCost, name: 'Waste Cost', itemStyle: { color: '#ef4444' } }
      ]
    }]
  })
}

const exportExcel = () => {
  if (month.value) window.open(`/api/profit/export?month=${month.value}`)
}

onMounted(() => {
  const now = new Date()
  month.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  loadReport()
  window.addEventListener('resize', () => {
    dailyChart?.resize()
    costChart?.resize()
  })
})

onUnmounted(() => {
  if (dailyChart) dailyChart.dispose()
  if (costChart) costChart.dispose()
})
</script>

<style scoped>
.profit-report {
  max-width: 1400px;
  margin: 0 auto;
  padding: 4px;
}


.glass-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  border-radius: 32px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  border: 1px solid rgba(228, 0, 43, 0.15);
}
.glass-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 35px -8px rgba(0, 0, 0, 0.12);
  border-color: rgba(228, 0, 43, 0.4);
}

/* Control Bar */
.control-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  margin-bottom: 32px;
  flex-wrap: wrap;
  gap: 16px;
}
.modern-picker {
  border-radius: 40px;
  --el-date-editor-border-radius: 40px;
}
.gradient-btn {
  background: linear-gradient(95deg, #e4002b 0%, #ff5e7e 100%);
  border: none;
  border-radius: 40px;
  padding: 10px 28px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(228, 0, 43, 0.2);
}
.gradient-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 6px 16px rgba(228, 0, 43, 0.3);
}

/* KPI Grid - Frosted Cards, Uniform Light Color */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(170px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}
.kpi-card {
  padding: 20px 16px;
  text-align: center;
  position: relative;
  overflow: hidden;
  cursor: default;
}
.kpi-icon {
  font-size: 36px;
  color: #e4002b;
  margin-bottom: 12px;
}
.kpi-value {
  font-size: 28px;
  font-weight: 800;
  color: #1e293b;
  letter-spacing: -0.5px;
  margin-bottom: 8px;
}
.kpi-label {
  font-size: 14px;
  font-weight: 500;
  color: #5b6e8c;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Charts Section */
.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}
.chart-card {
  padding: 16px 20px 20px 20px;
  transition: all 0.3s;
}
.chart-header {
  font-size: 18px;
  font-weight: 600;
  padding-bottom: 12px;
  margin-bottom: 16px;
  border-bottom: 2px solid rgba(228, 0, 43, 0.2);
  display: flex;
  align-items: center;
  gap: 8px;
  color: #1e293b;
}
.chart-header .el-icon {
  color: #e4002b;
  font-size: 20px;
}

/* Responsive */
@media (max-width: 900px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
  .kpi-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>