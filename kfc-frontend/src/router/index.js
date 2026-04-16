import { createRouter, createWebHistory } from 'vue-router'

// 导入六个页面组件
import ProductList from '@/views/inventory/ProductList.vue'
import StockRecords from '@/views/inventory/StockRecords.vue'
import EmployeeList from '@/views/hr/EmployeeList.vue'
import WorkHourPayroll from '@/views/hr/WorkHourPayroll.vue'
import SalesDaily from '@/views/finance/SalesDaily.vue'
import ProfitReport from '@/views/finance/ProfitReport.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/inventory/products' },
    { path: '/inventory/products', name: 'ProductList', component: ProductList },
    { path: '/inventory/records', name: 'StockRecords', component: StockRecords },
    { path: '/hr/employees', name: 'EmployeeList', component: EmployeeList },
    { path: '/hr/payroll', name: 'WorkHourPayroll', component: WorkHourPayroll },
    { path: '/finance/sales', name: 'SalesDaily', component: SalesDaily },
    { path: '/finance/profit', name: 'ProfitReport', component: ProfitReport }
  ]
})

export default router