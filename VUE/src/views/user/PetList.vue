<template>
  <div class="pet-list-container">
    <el-card class="pet-card">
      <template #header>
        <div class="card-header">
          <span>我的宠物档案</span>
          <el-button type="primary" @click="handleAddPet">+ 添加宠物</el-button>
        </div>
      </template>

      <!-- 宠物列表 -->
      <div v-if="petList.length > 0" class="pet-list">
        <div v-for="pet in petList" :key="pet.id" class="pet-card-item">
          <div class="pet-avatar">
            <img v-if="pet.avatar" :src="pet.avatar" :alt="pet.name" class="avatar-img" />
            <div v-else class="avatar-placeholder">
              <span>{{ pet.species }}</span>
            </div>
          </div>

          <div class="pet-info">
            <h3 class="pet-name">{{ pet.name }}</h3>
            <div class="pet-details">
              <span class="detail-item">
                <strong>种类：</strong>{{ pet.species }}
              </span>
              <span class="detail-item">
                <strong>年龄：</strong>{{ pet.age !== null ? pet.age + '岁' : '未知' }}
              </span>
              <span class="detail-item">
                <strong>性别：</strong>{{ pet.gender }}
              </span>
            </div>
          </div>

          <div class="pet-actions">
            <el-button type="primary" size="small" @click="handleEditPet(pet)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeletePet(pet.id)">删除</el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <div class="empty-icon">🐾</div>
        <p>还没有添加宠物档案</p>
        <el-button type="primary" @click="handleAddPet">立即添加</el-button>
      </div>
    </el-card>

    <!-- 宠物表单对话框 -->
    <PetForm
      v-model:visible="formVisible"
      :petData="selectedPet"
      @success="loadPetList"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getPetList, deletePet } from "@/api/pet";
import { useUserStore } from "@/store/userStore";
import PetForm from "./PetForm.vue";

const userStore = useUserStore();
const petList = ref([]);
const formVisible = ref(false);
const selectedPet = ref(null);
const loading = ref(false);

onMounted(() => {
  loadPetList();
});

async function loadPetList() {
  if (!userStore.userInfo?.id) {
    ElMessage.error("用户信息未加载");
    return;
  }

  loading.value = true;
  try {
    const data = await getPetList(userStore.userInfo.id);
    petList.value = data || [];
  } catch (error) {
    ElMessage.error(error.message || "加载宠物列表失败");
  } finally {
    loading.value = false;
  }
}

function handleAddPet() {
  selectedPet.value = null;
  formVisible.value = true;
}

function handleEditPet(pet) {
  selectedPet.value = pet;
  formVisible.value = true;
}

function handleDeletePet(petId) {
  ElMessageBox.confirm("确定要删除这个宠物档案吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  })
    .then(async () => {
      try {
        await deletePet(petId, userStore.userInfo.id);
        ElMessage.success("宠物档案删除成功");
        loadPetList();
      } catch (error) {
        ElMessage.error(error.message || "删除失败");
      }
    })
    .catch(() => {
      // 用户取消删除
    });
}
</script>

<style scoped>
.pet-list-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.pet-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pet-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.pet-card-item {
  display: flex;
  gap: 20px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background-color: #fafafa;
  transition: all 0.3s ease;
}

.pet-card-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  background-color: #fff;
}

.pet-avatar {
  flex-shrink: 0;
}

.avatar-img {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100px;
  height: 100px;
  background-color: #f5f7fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 12px;
}

.pet-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.pet-name {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #303133;
}

.pet-details {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.detail-item {
  font-size: 14px;
  color: #606266;
}

.pet-actions {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-shrink: 0;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.empty-state p {
  color: #909399;
  margin-bottom: 20px;
}
</style>
