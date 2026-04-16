<template>
  <el-container style="height: 100vh">
    <!-- 侧边栏 -->
    <el-aside width="220px" style="background-color: #304156">
      <div class="logo" style="height: 60px; line-height: 60px; text-align: center; color: white; font-weight: bold">
        KFC 管理系统
      </div>
      <el-menu
        :default-active="$route.path"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-sub-menu index="inventory">
          <template #title><span>库存管理</span></template>
          <el-menu-item index="/inventory/products">商品档案</el-menu-item>
          <el-menu-item index="/inventory/records">出入库记录</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="hr">
          <template #title><span>人事管理</span></template>
          <el-menu-item index="/hr/employees">员工档案</el-menu-item>
          <el-menu-item index="/hr/payroll">工时与工资</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="finance">
          <template #title><span>销售与财务</span></template>
          <el-menu-item index="/finance/sales">销售日报</el-menu-item>
          <el-menu-item index="/finance/profit">成本利润报表</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 主要内容区 -->
    <el-container>
      <el-header style="background-color: #fff; border-bottom: 1px solid #e6e6e6; line-height: 60px">
        <span>欢迎回来，店长</span>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute } from 'vue-router'
const $route = useRoute()
</script>

<style scoped>
.el-menu {
  border-right: none;
}
</style>