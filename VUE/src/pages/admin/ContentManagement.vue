<template>
  <div class="page-container">
    <el-tabs tab-position="left" style="height: 500px;">
      <el-tab-pane label="轮播图管理">
        <h3>首页轮播图配置</h3>
        <el-button type="primary" class="mb-20">上传新图片</el-button>
        <div class="placeholder-box">轮播图列表（图片+跳转链接）</div>
      </el-tab-pane>
      
      <el-tab-pane label="新闻公告">
        <h3>新闻公告发布</h3>
        <el-button type="primary" class="mb-20">发布公告</el-button>
        <div class="placeholder-box">富文本编辑器集成区域</div>
      </el-tab-pane>
      
      <el-tab-pane label="艺人信息">
        <h3>艺人阵容维护</h3>
        <el-alert title="提示" type="warning" :closable="false" class="mb-20">
          需重点标注“沈阳本土乐队”。
        </el-alert>
        <div class="placeholder-box">艺人列表（头像+简介+标签）</div>
      </el-tab-pane>
      
      <el-tab-pane label="演出日程">
        <h3>时间轴维护</h3>
        <div class="placeholder-box">拖拽式日程排期界面</div>
      </el-tab-pane>

      <el-tab-pane label="静态页面">
        <h3>交通/入场须知</h3>
        <div class="placeholder-box">静态页面编辑器</div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.page-container { background: white; padding: 20px; border-radius: 8px; }
.mb-20 { margin-bottom: 20px; }
.placeholder-box { height: 100px; border: 1px dashed #ccc; display: flex; align-items: center; justify-content: center; background: #fafafa; }
</style>