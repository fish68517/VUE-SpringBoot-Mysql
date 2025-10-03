<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8" v-for="ach in achievements" :key="ach.achievementId">
        <el-card 
          class="ach-card" 
          :class="{ 'is-earned': ach.isEarned }"
          shadow="hover"
        >
          <div class="ach-icon">
            <img :src="ach.achieveIconUrl" :alt="ach.achieveNameText" />
          </div>
          <div class="ach-info">
            <h3>{{ ach.achieveNameText }}</h3>
            <p>{{ ach.achieveDescriptionText }}</p>
          </div>
          <el-tag v-if="ach.isEarned" type="success" class="earned-tag">已获得</el-tag>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import api from '../api/NetWorkApi.js';

const currentUserId = 1;
const achievements = ref([]);
const loading = ref(true);

const fetchAchievements = async () => {
  loading.value = true;
  try {
    // 并发获取所有成就和用户已获得的成就
    const [allAchRes, userAchRelRes] = await Promise.all([
      api.achievementApi.list(),
      api.userAchieveRelApi.list()
    ]);

    const allAchievements = allAchRes.data.data || [];
    const userAchRels = userAchRelRes.data.data || [];
    
    // 筛选出当前用户获得的成就ID列表
    const earnedIds = userAchRels
      .filter(rel => rel.campusUserId === currentUserId)
      .map(rel => rel.achievementId);
      
    // 合并数据，标记哪些是已获得的
    achievements.value = allAchievements.map(ach => ({
      ...ach,
      isEarned: earnedIds.includes(ach.achievementId)
    }));

  } catch (error) {
    ElMessage.error('成就列表加载失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

onMounted(fetchAchievements);
</script>

<style scoped>
.ach-card {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  position: relative;
}
.ach-card.is-earned {
  border-color: #67c23a;
}
/* 对未获得的徽章应用灰度滤镜 */
.ach-card:not(.is-earned) .ach-icon img {
  filter: grayscale(100%);
  opacity: 0.5;
}
.ach-icon img {
  width: 60px;
  height: 60px;
  margin-right: 15px;
}
.ach-info h3 {
  margin: 0 0 5px;
  font-size: 16px;
}
.ach-info p {
  margin: 0;
  font-size: 14px;
  color: #666;
}
.earned-tag {
  position: absolute;
  top: 10px;
  right: 10px;
}
</style>