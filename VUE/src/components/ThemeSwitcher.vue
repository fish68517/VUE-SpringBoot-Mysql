<template>
  <div class="theme-switcher">
    <el-dropdown @command="handleThemeChange" trigger="click">
      <span class="theme-button">
        <i :class="themeIcon"></i>
        <span class="theme-label">{{ themeLabel }}</span>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="light">
            <i class="icon-sun"></i>
            {{ $t('theme.light') || 'Light' }}
          </el-dropdown-item>
          <el-dropdown-item command="dark">
            <i class="icon-moon"></i>
            {{ $t('theme.dark') || 'Dark' }}
          </el-dropdown-item>
          <el-dropdown-item command="auto">
            <i class="icon-auto"></i>
            {{ $t('theme.auto') || 'Auto' }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useTheme } from '../composables/useTheme'
import { THEMES } from '../utils/theme'

const { currentTheme, themePreference, changeTheme } = useTheme()

const themeIcon = computed(() => {
  if (currentTheme.value === THEMES.DARK) {
    return 'el-icon-moon'
  }
  return 'el-icon-sunny'
})

const themeLabel = computed(() => {
  if (themePreference.value === THEMES.AUTO) {
    return 'Auto'
  }
  return currentTheme.value === THEMES.DARK ? 'Dark' : 'Light'
})

const handleThemeChange = (command) => {
  changeTheme(command)
}
</script>

<style scoped>
.theme-switcher {
  display: flex;
  align-items: center;
}

.theme-button {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-3);
  cursor: pointer;
  border-radius: var(--radius-lg);
  transition: all var(--transition-base);
  color: var(--color-text-primary);
  background-color: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
}

.theme-button:hover {
  background-color: var(--color-bg-tertiary);
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.theme-label {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
}

@media (max-width: 767px) {
  .theme-label {
    display: none;
  }

  .theme-button {
    padding: var(--spacing-2);
  }
}
</style>
