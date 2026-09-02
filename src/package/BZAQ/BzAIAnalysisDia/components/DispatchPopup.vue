<template>
  <div class="dispatch-popup">
    <div class="popup-header">
      <span class="popup-title">一键调度</span>
      <button class="popup-close" @click="$emit('close')">×</button>
    </div>
    
    <div class="popup-body">
      <div v-if="props.riskData" class="risk-info">
        <span class="risk-info__label">风险标题：</span>
        <span class="risk-info__title">{{ props.riskData.title }}</span>
      </div>
      <div class="dispatch-item">
        <div class="dispatch-label-row">
          <span class="dispatch-label">已选择调度对象：</span>
          <div class="selected-tags" v-if="selectedStreets.length > 0">
            <div 
              v-for="street in selectedStreets" 
              :key="street" 
              class="selected-tag"
            >
              <span class="tag-name">{{ street }}</span>
              <span class="tag-remove" @click.stop="removeStreet(street)">×</span>
            </div>
          </div>
        </div>
        <div class="dispatch-select-wrapper">
          <div 
            class="dispatch-select" 
            @click="toggleDropdown"
            :class="{ 'dispatch-select-active': isDropdownOpen }"
          >
            <span v-if="selectedStreets.length === 0" class="placeholder">请选择调度对象</span>
            <span v-else class="selected-count">已选择 {{ selectedStreets.length }} 个调度对象</span>
            <span class="select-arrow" :class="{ 'select-arrow-up': isDropdownOpen }">▼</span>
          </div>
          
          <div v-if="isDropdownOpen" class="dispatch-dropdown">
            <div 
              v-for="street in streetList" 
              :key="street.value" 
              class="dropdown-item"
              :class="{ 'dropdown-item-active': selectedStreets.includes(street.value) }"
              @click="toggleStreet(street.value)"
            >
              <span class="checkbox" :class="{ 'checkbox-checked': selectedStreets.includes(street.value) }">
                ✓
              </span>
              <span class="dropdown-label">{{ street.label }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="popup-footer">
      <button class="confirm-btn" @click="handleConfirm">确认调度</button>
      <button class="cancel-btn" @click="$emit('close')">取消</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useMessage } from 'naive-ui'
import axios from 'axios'

const props = defineProps({
  riskData: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'confirm'])

const message = useMessage()

const selectedStreets = ref<string[]>([])
const isDropdownOpen = ref(false)

const streetList = ref([
  { value: '政府办公室', label: '政府办公室' },
  { value: '发改委', label: '发改委' },
  { value: '教委', label: '教委' },
  { value: '科技局', label: '科技局' },
  { value: '经济信息委', label: '经济信息委' },
  { value: '民族宗教委', label: '民族宗教委' },
  { value: '公安分局', label: '公安分局' },
  { value: '民政局', label: '民政局' },
  { value: '司法局', label: '司法局' },
  { value: '财政局', label: '财政局' },
  { value: '人力社保局', label: '人力社保局' },
  { value: '规划自然资源局', label: '规划自然资源局' },
  { value: '生态环境局', label: '生态环境局' },
  { value: '住房城市建委', label: '住房城市建委' },
  { value: '城管局', label: '城管局' },
  { value: '交通运输委', label: '交通运输委' },
  { value: '商务委', label: '商务委' },
  { value: '文化旅游委', label: '文化旅游委' },
  { value: '卫生健康委', label: '卫生健康委' },
  { value: '退役军人事务局', label: '退役军人事务局' },
  { value: '应急管理局', label: '应急管理局' },
  { value: '审计局', label: '审计局' },
  { value: '政府外办', label: '政府外办' },
  { value: '国资委', label: '国资委' },
  { value: '产业促进局', label: '产业促进局' },
  { value: '市场监管局', label: '市场监管局' },
  { value: '统计局', label: '统计局' },
  { value: '信访办', label: '信访办' },
  { value: '医保局', label: '医保局' },
  { value: '大数据发展局', label: '大数据发展局' },
  { value: '国动办', label: '国动办' },
  { value: '解放碑街道办', label: '解放碑街道办' },
  { value: '朝天门街道办', label: '朝天门街道办' },
  { value: '七星岗街道办', label: '七星岗街道办' },
  { value: '南纪门街道办', label: '南纪门街道办' },
  { value: '菜园坝街道办', label: '菜园坝街道办' },
  { value: '大溪沟街道办', label: '大溪沟街道办' },
  { value: '两路口街道办', label: '两路口街道办' },
  { value: '上清寺街道办', label: '上清寺街道办' },
  { value: '大坪街道办', label: '大坪街道办' },
  { value: '石油路街道办', label: '石油路街道办' },
  { value: '化龙桥街道办', label: '化龙桥街道办' }
])

onMounted(() => {
  if (typeof window !== 'undefined' && !window['$message']) {
    window['$message'] = message
  }
  const depts = props.riskData?.coordinateDepartments
  if (depts) {
    selectedStreets.value = depts.split(',').map(d => d.trim()).filter(Boolean)
  }
})

const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value
}

const toggleStreet = (value: string) => {
  const index = selectedStreets.value.indexOf(value)
  if (index > -1) {
    selectedStreets.value.splice(index, 1)
  } else {
    selectedStreets.value.push(value)
  }
}

const removeStreet = (value: string) => {
  const index = selectedStreets.value.indexOf(value)
  if (index > -1) {
    selectedStreets.value.splice(index, 1)
  }
}

const handleConfirm = async () => {
  if (selectedStreets.value.length === 0) {
    message.warning('请至少选择一个调度对象')
    return
  }
  const selectedNames = [...selectedStreets.value]

  try {
    const rd = props.riskData || {}
    const params = {
      source: rd.source || '',
      eventType: rd.eventType || '',
      eventAddress: rd.eventAddress || '',
      eventDesc: rd.eventDesc || '',
      handleStatus: rd.handleStatus || '',
      referenceLevel: rd.referenceLevel || '',
      coordinateDepartments: selectedNames.join(',')
    }
    const res = await axios.post('http://23.99.16.179:11001/api/boot/system/rescue/notify', params)
    if (res.data.code === '000000') {
      message.success('调度成功')
      emit('confirm', {
        streets: selectedStreets.value,
        streetNames: selectedNames,
        riskData: props.riskData
      })
    } else {
      message.error('调度失败：' + (res.data.message || '未知错误'))
    }
  } catch (err) {
    console.error('请求调度接口失败', err)
    message.error('调度失败，请稍后重试')
  }
}
</script>

<script lang="ts">
export default {
  name: "DispatchPopup",
  version: "1.0.0"
}
</script>

<style lang="scss" scoped>
.dispatch-popup {
  width: 312px;
  min-height: 294px;
  background: rgba(5, 33, 62, 0.98);
  border: 1px solid rgba(33, 145, 244, 0.5);
  border-radius: 6px;
  overflow: hidden;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 1002;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
}

.popup-header {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 10px 36px;
  background: linear-gradient(90deg, rgba(17, 84, 139, 0.9) 0%, rgba(6, 47, 78, 0.8) 100%);
  border-bottom: 1px solid rgba(33, 145, 244, 0.3);
  position: relative;
}

.popup-title {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  text-align: center;
}

.popup-close {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  color: #fff;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;

  &:hover {
    color: #ff6b6b;
  }
}

.popup-body {
  flex: 1;
  padding: 16px;
}

.risk-info {
  display: flex;
  align-items: flex-start;
  gap: 4px;
  margin-bottom: 10px;
  padding: 6px 8px;
  background: rgba(71, 168, 255, 0.1);
  border: 1px solid rgba(71, 168, 255, 0.2);
  border-radius: 4px;
}

.risk-info__label {
  font-size: 12px;
  color: #8bbce6;
  flex-shrink: 0;
}

.risk-info__title {
  font-size: 12px;
  color: #e8f8ff;
  line-height: 1.4;
  word-break: break-all;
}

.dispatch-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.dispatch-label-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  flex-wrap: wrap;
}

.dispatch-label {
  font-size: 13px;
  color: #8bbce6;
  flex-shrink: 0;
  padding-top: 2px;
}

.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  flex: 1;
}

.selected-tag {
  position: relative;
  display: flex;
  align-items: center;
  padding: 3px 20px 3px 8px;
  background: rgba(71, 168, 255, 0.2);
  border: 1px solid rgba(71, 168, 255, 0.4);
  border-radius: 3px;
  font-size: 12px;
  color: #e8f8ff;
  line-height: 1.4;
}

.tag-name {
  white-space: nowrap;
}

.tag-remove {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 14px;
  height: 14px;
  background: rgba(71, 168, 255, 0.8);
  border-radius: 50%;
  font-size: 12px;
  line-height: 14px;
  text-align: center;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  user-select: none;

  &:hover {
    background: #ff6b6b;
  }
}

.dispatch-select-wrapper {
  position: relative;
  width: 100%;
}

.dispatch-select {
  width: 100%;
  height: 34px;
  padding: 0 32px 0 12px;
  background: rgba(71, 168, 255, 0.1);
  border: 1px solid rgba(71, 168, 255, 0.3);
  border-radius: 4px;
  color: #e8f8ff;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: all 0.3s ease;
  box-sizing: border-box;

  &:hover {
    border-color: rgba(71, 168, 255, 0.5);
  }

  &.dispatch-select-active {
    border-color: #47a8ff;
    background: rgba(71, 168, 255, 0.15);
  }
}

.placeholder {
  color: #5a7a9a;
}

.selected-count {
  color: #47a8ff;
  font-weight: 500;
}

.select-arrow {
  font-size: 10px;
  color: #8bbce6;
  transition: transform 0.3s ease;

  &.select-arrow-up {
    transform: rotate(180deg);
  }
}

.dispatch-dropdown {
  position: absolute;
  top: 38px;
  left: 0;
  right: 0;
  max-height: 108px;
  overflow-y: auto;
  padding-bottom: 4px;
  background: #0a2138;
  border: 1px solid rgba(71, 168, 255, 0.3);
  border-radius: 4px;
  z-index: 10;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(71, 168, 255, 0.4);
    border-radius: 3px;
  }
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  cursor: pointer;
  transition: background 0.2s ease;

  &:hover {
    background: rgba(71, 168, 255, 0.1);
  }

  &.dropdown-item-active {
    background: rgba(71, 168, 255, 0.15);
  }
}

.checkbox {
  width: 14px;
  height: 14px;
  border: 1px solid rgba(71, 168, 255, 0.5);
  border-radius: 2px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: transparent;
  flex-shrink: 0;

  &.checkbox-checked {
    background: #47a8ff;
    color: #fff;
  }
}

.dropdown-label {
  font-size: 13px;
  color: #e8f8ff;
}

.popup-footer {
  display: flex;
  gap: 10px;
  padding: 10px 16px;
  background: rgba(5, 33, 62, 0.5);
}

.confirm-btn,
.cancel-btn {
  flex: 1;
  padding: 8px 0;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.confirm-btn {
  background: linear-gradient(180deg, #47a8ff 0%, #0454cb 100%);
  color: #fff;

  &:hover {
    opacity: 0.9;
    box-shadow: 0 4px 12px rgba(71, 168, 255, 0.4);
  }
}

.cancel-btn {
  background: rgba(71, 168, 255, 0.15);
  color: #8bbce6;
  border: 1px solid rgba(71, 168, 255, 0.3);

  &:hover {
    background: rgba(71, 168, 255, 0.25);
  }
}

::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.2);
}

::-webkit-scrollbar-thumb {
  background: rgba(33, 145, 244, 0.5);
  border-radius: 3px;
}
</style>
