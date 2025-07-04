<!--
 * @Author: kaix
 * @Date: 2025-07-04 11:31:30
 * @LastEditTime: 2025-07-04 15:01:31
 * @LastEditors: kaix
 * @Description: 总线测试页面
-->
<template>
  <div class="bus-test">
    <div class="header">
      <h1>全局参数-自定义事件测试页面</h1>
      <div class="nav-buttons">
        <n-button @click="goToHome" type="primary">返回首页</n-button>
      </div>
    </div>
    
    <div class="content">
      <!-- 组件展示区域 -->
      <div class="components-container">
        <div 
          v-for="componentConfig in componentConfigs" 
          :key="componentConfig.name"
          class="component-wrapper"
        >
          <n-card :title="componentConfig.title" class="component-card">
            <component
              v-if="componentConfig.component"
              :is="componentConfig.component"
              :chartConfig="componentConfig.config"
              :themeColor="globalColor"
              :key="componentConfig.name"
              :bus="eventBus"
              :globalParams="globalParams"
            />
          </n-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import { useGlobalParamsStore } from '@/store/Modules/useGlobalParamsStore'
import eventBus from '@/utils/bus'

const router = useRouter()
const globalParams = useGlobalParamsStore()

// ========== 组件配置区域 ==========
// 💡 要切换其他组件，只需要修改下面的基础配置即可！
// 
// 🔄 切换示例：
// 要把 BarCommon 换成 LineCommon，将第一个对象修改为：
// {
//   name: 'LineCommon',
//   path: 'Charts/Lines/LineCommon', 
//   title: '折线图组件'
// }

// 基础组件配置（只需要修改这个数组）
const baseConfigs = [
  {
    name: 'BarCommon',
    path: 'Charts/Bars/BarCommon',
    title: '组件1'
  },
  {
    name: 'TimeCommon', 
    path: 'Decorates/Mores/TimeCommon',
    title: '组件2'
  }
]

// 自动为每个组件创建响应式引用
const componentConfigs = ref(
  baseConfigs.map(config => ({
    ...config,
    component: shallowRef<any>(''),
    config: ref<any>(null)
  }))
)

// 全局颜色配置
const globalColor = ref({
  color: ['#04bcfa', '#0454cb', '#056ff1', '#47dea2', '#16b8d6', '#f1b736']
})

// 导航方法
const goToHome = () => {
  router.push('/')
}

// 通用组件加载函数
const loadComponent = async (componentConfig: any) => {
  try {
    // 动态导入组件
    const componentModule = await import(`@/package/${componentConfig.path}/export`)
    const componentExport = componentModule[componentConfig.name]
    
    // 设置组件
    componentConfig.component = componentExport.component
    
    // 创建配置
    const config = new componentExport.config.default()
    componentConfig.config = config
    
    console.log(`${componentConfig.name} 组件已加载`, componentExport)
  } catch (error) {
    console.error(`${componentConfig.name} 组件加载失败:`, error)
    window.$message?.error(`${componentConfig.name} 组件加载失败`)
  }
}

// 加载所有组件
const loadComponents = async () => {
  for (const componentConfig of componentConfigs.value) {
    await loadComponent(componentConfig)
  }
}

onMounted(() => {
  console.log('BusTest 组件已挂载')
  
  // 加载组件
  loadComponents()
})

onUnmounted(() => {
  console.log('BusTest 组件已卸载')
})
</script>

<style lang="scss" scoped>
.bus-test {
  width: 100%;
  height: 100vh;
  padding: 20px;
  background: rgb(16, 16, 20);
  color: #fff;
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h1 {
      margin: 0;
      font-size: 32px;
      font-weight: bold;
    }
    
    .nav-buttons {
      display: flex;
      gap: 10px;
    }
  }
  
  .content {
    display: flex;
    flex-direction: column;
    gap: 20px;
    
    .components-container {
      display: flex;
      gap: 20px;
      flex-wrap: wrap;
      
      .component-wrapper {
        flex: 1;
        min-width: 500px;
        min-height: 500px;
        
        .component-card {
          background: rgba(255, 255, 255, 0.1);
          backdrop-filter: blur(10px);
          border-radius: 12px;
          border: 1px solid rgba(255, 255, 255, 0.2);
          height: 100%;
          
          :deep(.n-card__content) {
            color: #fff;
            padding: 20px;
          }
          
          :deep(.n-card-header__main) {
            color: #fff;
            font-size: 20px;
            font-weight: bold;
          }
        }
      }
    }
  }
}
</style> 