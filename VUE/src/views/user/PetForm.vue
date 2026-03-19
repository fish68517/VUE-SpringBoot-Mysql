<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑宠物档案' : '添加宠物档案'"
    width="520px"
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
          <img
            :src="avatarPreviewUrl"
            alt="宠物头像"
            class="avatar-img"
            @error="handlePreviewError"
          />
          <div class="avatar-actions">
            <el-upload
              :show-file-list="false"
              :http-request="handleAvatarUpload"
              accept="image/*"
            >
              <el-button type="primary" :loading="uploading">上传头像</el-button>
            </el-upload>
            <el-button text @click="handleRemoveAvatar">使用默认图</el-button>
            <p class="upload-tip">图片将保存到 SpringBoot/uploads/pets，数据库保存文件名</p>
          </div>
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">
        {{ isEdit ? "更新" : "添加" }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { ElMessage } from "element-plus";
import { createPet, updatePet, uploadPetAvatar } from "@/api/pet";
import { useUserStore } from "@/store/userStore";
import defaultPetImage from "@/assets/bg.jpg";

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
const uploading = ref(false);
const isEdit = ref(false);

const form = ref({
  name: "",
  species: "",
  age: null,
  gender: "",
  avatar: ""
});

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

const avatarPreviewUrl = computed(() => {
  if (!form.value.avatar) return defaultPetImage;
  if (form.value.avatar.startsWith("http")) return form.value.avatar;
  return `http://localhost:8080/uploads/${form.value.avatar}`;
});

watch(
  () => props.visible,
  (val) => {
    if (!val) return;
    if (props.petData) {
      isEdit.value = true;
      form.value = {
        name: props.petData.name || "",
        species: props.petData.species || "",
        age: props.petData.age ?? null,
        gender: props.petData.gender || "",
        avatar: props.petData.avatar || ""
      };
    } else {
      isEdit.value = false;
      resetForm();
    }
  },
  { immediate: true }
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

async function handleAvatarUpload(options) {
  try {
    const file = options.file;
    if (!file?.type?.startsWith("image/")) {
      ElMessage.warning("请选择图片文件");
      return;
    }

    uploading.value = true;
    const avatarPath = await uploadPetAvatar(file);
    form.value.avatar = avatarPath;
    options.onSuccess?.(avatarPath);
    ElMessage.success("头像上传成功");
  } catch (error) {
    console.error(error);
    options.onError?.(error);
    ElMessage.error(error.message || "头像上传失败");
  } finally {
    uploading.value = false;
  }
}

function handlePreviewError(event) {
  const img = event?.target;
  if (!img || img.dataset.fallbackApplied === "1") return;
  img.dataset.fallbackApplied = "1";
  img.src = defaultPetImage;
}

function handleRemoveAvatar() {
  form.value.avatar = "";
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
</script>

<style scoped>
.avatar-upload-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar-img {
  width: 86px;
  height: 86px;
  border-radius: 8px;
  object-fit: cover;
  border: 1px solid #e5e7eb;
  background: #f3f4f6;
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.upload-tip {
  margin: 0;
  font-size: 12px;
  color: #6b7280;
}
</style>
