<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑宠物档案' : '添加宠物档案'"
    width="500px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="宠物名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入宠物名称" />
      </el-form-item>

      <el-form-item label="宠物种类" prop="species">
        <el-select v-model="form.species" placeholder="请选择宠物种类">
          <el-option label="猫" value="猫" />
          <el-option label="狗" value="狗" />
          <el-option label="兔子" value="兔子" />
          <el-option label="鸟" value="鸟" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>

      <el-form-item label="年龄" prop="age">
        <el-input-number v-model="form.age" :min="0" :max="50" />
      </el-form-item>

      <el-form-item label="性别" prop="gender">
        <el-select v-model="form.gender" placeholder="请选择性别">
          <el-option label="公" value="公" />
          <el-option label="母" value="母" />
          <el-option label="未知" value="未知" />
        </el-select>
      </el-form-item>

      <el-form-item label="头像">
        <div class="avatar-upload-section">
          <div v-if="form.avatar" class="avatar-preview">
            <img :src="form.avatar" alt="宠物头像" class="avatar-img" />
            <el-button type="danger" size="small" @click="handleRemoveAvatar">删除</el-button>
          </div>
          <el-upload
            v-else
            action="http://localhost:8080/api/pet/avatar"
            :headers="uploadHeaders"
            :on-success="handleAvatarSuccess"
            :on-error="handleAvatarError"
            :show-file-list="false"
          >
            <el-button type="primary">上传头像</el-button>
          </el-upload>
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">
        {{ isEdit ? '更新' : '添加' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { ElMessage } from "element-plus";
import { createPet, updatePet } from "@/api/pet";
import { useUserStore } from "@/store/userStore";

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  petData: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(["update:visible", "success"]);

const userStore = useUserStore();
const formRef = ref(null);
const loading = ref(false);
const isEdit = ref(false);

const form = ref({
  name: "",
  species: "",
  age: null,
  gender: "",
  avatar: ""
});

const uploadHeaders = computed(() => ({
  Authorization: userStore.token
}));

const rules = {
  name: [
    { required: true, message: "请输入宠物名称", trigger: "blur" },
    { max: 50, message: "宠物名称长度不能超过50个字符", trigger: "blur" }
  ],
  species: [{ required: true, message: "请选择宠物种类", trigger: "change" }],
  gender: [{ required: true, message: "请选择性别", trigger: "change" }]
};

const visible = computed({
  get: () => props.visible,
  set: (val) => emit("update:visible", val)
});

watch(
  () => props.petData,
  (newVal) => {
    if (newVal) {
      isEdit.value = true;
      form.value = {
        name: newVal.name || "",
        species: newVal.species || "",
        age: newVal.age || null,
        gender: newVal.gender || "",
        avatar: newVal.avatar || ""
      };
    } else {
      isEdit.value = false;
      resetForm();
    }
  }
);

function resetForm() {
  form.value = {
    name: "",
    species: "",
    age: null,
    gender: "",
    avatar: ""
  };
}

function handleClose() {
  visible.value = false;
  resetForm();
}

async function handleSubmit() {
  if (!formRef.value) return;

  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  loading.value = true;
  try {
    const data = {
      name: form.value.name,
      species: form.value.species,
      age: form.value.age,
      gender: form.value.gender,
      avatar: form.value.avatar
    };

    if (isEdit.value) {
      await updatePet(props.petData.id, data);
      ElMessage.success("宠物档案更新成功");
    } else {
      data.userId = userStore.userInfo.id;
      await createPet(data);
      ElMessage.success("宠物档案添加成功");
    }

    emit("success");
    handleClose();
  } catch (error) {
    ElMessage.error(error.message || "操作失败");
  } finally {
    loading.value = false;
  }
}

function handleAvatarSuccess(response) {
  form.value.avatar = response.data;
  ElMessage.success("头像上传成功");
}

function handleAvatarError(error) {
  ElMessage.error("头像上传失败");
}

function handleRemoveAvatar() {
  form.value.avatar = "";
}
</script>

<style scoped>
.avatar-upload-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar-preview {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar-img {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
}
</style>
