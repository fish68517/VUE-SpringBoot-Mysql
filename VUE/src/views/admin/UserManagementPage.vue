<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>用户账户管理</span>
        <el-button type="primary" @click="openDialog(null)">
          <el-icon><Plus /></el-icon> 新增用户
        </el-button>
      </div>
    </template>

    <!-- 用户列表表格 -->
    <el-table :data="users" v-loading="loading" style="width: 100%" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="fullName" label="全名" width="150" />
      <el-table-column label="角色" width="150">
        <template #default="scope">
          <el-tag effect="plain">{{ scope.row.role?.roleName || '未知角色' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="部门" width="150">
        <template #default="scope">
          {{ scope.row.department?.name || '未分配' }}
        </template>
      </el-table-column>
      <el-table-column prop="isActive" label="状态" width="100">
        <template #default="scope">
          <el-switch
            v-model="scope.row.isActive"
            inline-prompt
            active-text="启用"
            inactive-text="禁用"
            :loading="scope.row.statusLoading"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="200">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="openDialog(scope.row)">
            <el-icon><Edit /></el-icon> 编辑
          </el-button>
          <el-popconfirm 
            title="确定要永久删除此用户吗？" 
            confirm-button-text="删除"
            cancel-button-text="取消"
            confirm-button-type="danger"
            @confirm="handleDelete(scope.row)"
          >
            <template #reference>
              <el-button size="small" type="danger" link>
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑用户' : '新增用户'" 
      width="500px"
      @close="resetForm"
    >
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="登录账号" />
        </el-form-item>
        
        <el-form-item label="全名" prop="fullName">
          <el-input v-model="form.fullName" placeholder="员工真实姓名" />
        </el-form-item>

        <el-form-item 
          label="密码" 
          prop="passwordHash"
          :rules="isEdit ? [] : rules.passwordHash"
        >
          <el-input 
            v-model="form.passwordHash" 
            type="password" 
            show-password
            :placeholder="isEdit ? '留空则不修改密码' : '请输入初始密码'" 
          />
        </el-form-item>

        <el-form-item label="角色" prop="roleId">
          <el-select v-model="form.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="role in roleList"
              :key="role.id"
              :label="role.roleName"
              :value="role.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="部门" prop="departmentId">
          <el-select v-model="form.departmentId" placeholder="请选择部门" style="width: 100%">
            <el-option
              v-for="dept in departmentList"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="账户状态" prop="isActive">
           <el-radio-group v-model="form.isActive">
              <el-radio :value="true">启用</el-radio>
              <el-radio :value="false">禁用</el-radio>
            </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            确认{{ isEdit ? '修改' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus, Edit, Delete } from '@element-plus/icons-vue';
import api from '../../api/NetWorkApi.js';

// --- 状态变量 ---
const users = ref([]);
const roleList = ref([]);
const departmentList = ref([]);
const loading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref(null);

// --- 表单数据 ---
const form = reactive({
  id: null,
  username: '',
  fullName: '',
  passwordHash: '', // 对应后端的密码字段
  roleId: null,
  departmentId: null,
  isActive: true,
});

// --- 校验规则 ---
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  fullName: [
    { required: true, message: '请输入全名', trigger: 'blur' }
  ],
  passwordHash: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  departmentId: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ]
};

// --- 初始化加载 ---
const loadData = async () => {
  loading.value = true;
  try {
    // 并行获取用户、角色、部门数据
    const [usersRes, rolesRes, deptsRes] = await Promise.all([
      api.usersApi.list(),
      api.rolesApi.list(),
      api.departmentsApi.list()
    ]);

    const rawUsers = usersRes.data || [];
    roleList.value = rolesRes.data || [];
    departmentList.value = deptsRes.data || [];

    // 构建映射 Map 以便快速查找名称
    const roleMap = new Map(roleList.value.map(r => [r.id, r]));
    const deptMap = new Map(departmentList.value.map(d => [d.id, d]));

    // 组装显示数据
    users.value = rawUsers.map(u => ({
      ...u,
      role: roleMap.get(u.roleId) || { roleName: '未知' },
      department: deptMap.get(u.departmentId) || { name: '未分配' },
      statusLoading: false // 添加一个临时状态用于控制 Switch 的 loading
    }));

  } catch (error) {
    console.error(error);
    ElMessage.error("加载数据失败");
  } finally {
    loading.value = false;
  }
};

onMounted(loadData);

// --- 操作逻辑 ---

// 打开对话框 (新增/编辑)
const openDialog = (row) => {
  isEdit.value = !!row;
  dialogVisible.value = true;
  
  if (row) {
    // 编辑模式：回填数据
    form.id = row.id;
    form.username = row.username;
    form.fullName = row.fullName;
    form.passwordHash = ''; // 编辑时不回显密码，留空代表不修改
    form.roleId = row.roleId;
    form.departmentId = row.departmentId;
    form.isActive = row.isActive;
  } else {
    // 新增模式：重置数据
    resetFormModel();
  }
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true;
      try {
        const payload = { ...form };
        
        // 如果是编辑模式且密码为空，则删除密码字段，避免误将其置空
        if (isEdit.value && !payload.passwordHash) {
          delete payload.passwordHash;
        }

        if (isEdit.value) {
          await api.usersApi.update(payload);
          ElMessage.success("更新成功");
        } else {
          await api.usersApi.create(payload);
          ElMessage.success("创建成功");
        }
        
        dialogVisible.value = false;
        loadData(); // 刷新列表
      } catch (error) {
        console.error(error);
        ElMessage.error(isEdit.value ? "更新失败" : "创建失败");
      } finally {
        submitLoading.value = false;
      }
    }
  });
};

// 删除用户
const handleDelete = async (row) => {
  try {
    await api.usersApi.delete(row.id);
    ElMessage.success("删除成功");
    loadData();
  } catch (error) {
    console.error(error);
    // 捕获可能的关联数据约束错误
    if (error.response && error.response.status === 500) {
       ElMessage.error("删除失败：该用户可能存在关联的业务数据（如入库单、日志等），请先清理相关数据或选择禁用账户。");
    } else {
       ElMessage.error("删除失败");
    }
  }
};

// 快速切换状态 (Switch)
const handleStatusChange = async (row) => {
  row.statusLoading = true;
  try {
    // 构造只包含必要字段的对象进行更新
    const payload = { 
      id: row.id, 
      isActive: row.isActive 
      // 注意：后端如果全量更新，可能需要补全其他字段。
      // 这里假设后端支持部分更新或者对象合并。
      // 如果后端是 MyBatis-Plus 的 updateById，它通常只更新非空字段，
      // 但为了保险，建议传完整对象，或者后端改为 updateById(entity) 逻辑。
      // 为安全起见，这里传入完整对象的副本：
      // ...row 
    };
    
    // 由于 row 包含 role 和 department 对象，传给后端可能会报错，最好只传基本字段
    const safePayload = {
        id: row.id,
        username: row.username,
        fullName: row.fullName,
        roleId: row.roleId,
        departmentId: row.departmentId,
        isActive: row.isActive
    };

    await api.usersApi.update(safePayload);
    ElMessage.success(`用户 ${row.username} 已${row.isActive ? '启用' : '禁用'}`);
  } catch (error) {
    console.error(error);
    row.isActive = !row.isActive; // 恢复状态
    ElMessage.error("状态修改失败");
  } finally {
    row.statusLoading = false;
  }
};

// 重置表单模型
const resetFormModel = () => {
  form.id = null;
  form.username = '';
  form.fullName = '';
  form.passwordHash = '';
  form.roleId = null; // 默认值可以设为 3 (操作员)
  form.departmentId = null;
  form.isActive = true;
};

// 关闭对话框时的回调
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  resetFormModel();
};
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>