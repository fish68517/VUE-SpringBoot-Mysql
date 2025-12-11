<template>
  <div class="footprints-container">
    <el-card class="control-panel" shadow="hover">
      <div class="header">
        <h2>我的旅行足迹</h2>
        <el-tag type="success">已记录足迹: {{ footprints.length }} 个</el-tag>
      </div>
    </el-card>

    <div id="container" class="map-container"></div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref, shallowRef } from 'vue';
import AMapLoader from '@amap/amap-jsapi-loader';


// 1. 定义地图实例 (使用 shallowRef 优化性能，避免 Vue 深度监听地图对象)
const map = shallowRef(null);
const AMapObj = shallowRef(null); // 存储 AMap 对象

// 2. 定义足迹数据 (响应式)
const footprints = ref([]);

// ---------------------------------------------------------
// 3. 模拟数据 (Mock Data) - 对应你的数据库表结构
// ---------------------------------------------------------
// 表结构字段: id, travel_record_id, location_name, latitude, longitude, visit_date
const mockDatabaseData = [
  {
    id: 1,
    travel_record_id: 101,
    location_name: '温州大学 (Wenzhou University)',
    latitude: 27.932378,
    longitude: 120.703299,
    visit_date: '2025-09-01',
    created_at: '2025-09-01 10:00:00'
  },
  {
    id: 2,
    travel_record_id: 101,
    location_name: '五马街 (Wuma Street)',
    latitude: 28.016335,
    longitude: 120.654876,
    visit_date: '2025-10-05',
    created_at: '2025-10-05 14:30:00'
  },
  {
    id: 3,
    travel_record_id: 102,
    location_name: '雁荡山 (Yandang Mountain)',
    latitude: 28.370000,
    longitude: 121.050000,
    visit_date: '2025-11-20',
    created_at: '2025-11-20 09:00:00'
  }
];

// ---------------------------------------------------------
// 4. 获取数据方法 (预留 API 接入点)
// ---------------------------------------------------------
const fetchFootprints = async () => {
  // TODO: 后续接入 SpringBoot 接口
  // const res = await axios.get('/api/footprints/user/current');
  // footprints.value = res.data;
  
  // 目前使用本地 Mock 数据
  footprints.value = mockDatabaseData;
  console.log('足迹数据加载完成:', footprints.value);
};

// ---------------------------------------------------------
// 5. 地图初始化与渲染逻辑
// ---------------------------------------------------------
const initMap = () => {
  // 替换为你申请的高德地图 Key 和 安全密钥 (SecurityCode)
  // 注意：2021年12月02日以后申请的key需要配合安全密钥一起使用
  window._AMapSecurityConfig = {
    securityJsCode: '76805393edb2f03827a55eafa36fc6d2', 
  };

  AMapLoader.load({
    key: "e2706bc1e334def5699349076d5f6d58", // 申请好的Web端开发者Key
    version: "2.0",      // 指定要加载的 JSAPI 的版本
    plugins: ['AMap.ToolBar', 'AMap.Scale', 'AMap.Marker', 'AMap.InfoWindow'], 
  }).then((AMap) => {
    AMapObj.value = AMap;
    
    // 创建地图实例
    map.value = new AMap.Map("container", {
      viewMode: "3D",    // 是否为3D地图模式
      zoom: 11,          // 初始化地图级别
      center: [120.699361, 27.994267], // 初始化地图中心点 (默认温州)
    });

    // 添加控件
    map.value.add(new AMap.ToolBar());
    map.value.add(new AMap.Scale());

    // 渲染足迹点
    renderMarkers();

  }).catch(e => {
    console.error('地图加载失败:', e);
  });
};

const renderMarkers = () => {
  if (!map.value || !AMapObj.value) return;

  footprints.value.forEach(item => {
    // 创建标记
    const marker = new AMapObj.value.Marker({
      position: [item.longitude, item.latitude], // 高德地图坐标顺序是 [经度, 纬度]
      title: item.location_name,
      map: map.value
    });

    // 创建点击弹窗内容 (InfoWindow)
    // 这里可以使用 HTML 字符串构建丰富的内容
    const infoContent = `
  <div style="padding:5px; min-width: 200px;">
    <h4 style="margin:0 0 5px 0;">📍 ${item.location_name}</h4>
    <p style="margin:5px 0; font-size:13px; color:#666;">
      📅 打卡日期: ${item.visit_date}<br/>
      🆔 关联游记ID: ${item.travel_record_id}
    </p>
    <button onclick="alert('跳转到详情页逻辑...')" 
            style="display:none; cursor:pointer; color:#409EFF; border:none; background:none; padding:0;">
      查看详情 >>
    </button>
  </div>
`;

    const infoWindow = new AMapObj.value.InfoWindow({
      content: infoContent,
      offset: new AMapObj.value.Pixel(0, -30)
    });

    // 绑定点击事件
    marker.on('click', () => {
      infoWindow.open(map.value, marker.getPosition());
    });
  });
  
  // 自动调整地图视野以包含所有点
  map.value.setFitView();
};

// ---------------------------------------------------------
// 生命周期
// ---------------------------------------------------------
onMounted(async () => {
  await fetchFootprints(); // 先加载数据
  initMap(); // 再渲染地图
});

onUnmounted(() => {
  if (map.value) {
    map.value.destroy(); // 销毁地图，释放内存
  }
});
</script>

<style scoped>
.footprints-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.control-panel {
  flex-shrink: 0; /* 防止卡片被压缩 */
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.map-container {
  flex-grow: 1; /* 填满剩余空间 */
  width: 100%;
  min-height: 500px; /* 最小高度 */
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  overflow: hidden;
}
</style>