<template>
  <section class="login-required-card">
    <span class="login-required-card__badge">{{ badge }}</span>
    <h2>{{ title }}</h2>
    <p>{{ description }}</p>

    <div class="login-required-card__actions">
      <router-link :to="loginTarget" class="login-required-card__btn login-required-card__btn--primary">
        {{ buttonText }}
      </router-link>
      <router-link v-if="secondaryText" :to="secondaryTarget" class="login-required-card__btn login-required-card__btn--ghost">
        {{ secondaryText }}
      </router-link>
    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  badge: {
    type: String,
    default: '游客访问',
  },
  title: {
    type: String,
    default: '请登录后访问',
  },
  description: {
    type: String,
    default: '当前功能需要登录后的用户信息，登录后即可继续使用。',
  },
  redirect: {
    type: String,
    default: '/login',
  },
  buttonText: {
    type: String,
    default: '前往登录',
  },
  secondaryText: {
    type: String,
    default: '',
  },
  secondaryTarget: {
    type: [String, Object],
    default: '/home',
  },
})

const loginTarget = computed(() => {
  if (!props.redirect || props.redirect === '/login') {
    return '/login'
  }

  return {
    path: '/login',
    query: {
      redirect: props.redirect,
    },
  }
})
</script>

<style scoped>
.login-required-card {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 28px;
  border-radius: 24px;
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.18), transparent 30%),
    linear-gradient(135deg, #0b4a8c 0%, #166b9d 48%, #2d8f83 100%);
  color: #ffffff;
  box-shadow: 0 24px 44px rgba(11, 74, 140, 0.18);
}

.login-required-card__badge {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.06em;
}

.login-required-card h2 {
  margin: 0;
  font-size: 28px;
  line-height: 1.25;
}

.login-required-card p {
  margin: 0;
  max-width: 720px;
  color: rgba(255, 255, 255, 0.82);
  line-height: 1.85;
}

.login-required-card__actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 4px;
}

.login-required-card__btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 44px;
  padding: 10px 18px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 700;
  text-decoration: none;
}

.login-required-card__btn--primary {
  background: #ffffff;
  color: #0b4a8c;
}

.login-required-card__btn--ghost {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.18);
  color: #ffffff;
}

@media (max-width: 768px) {
  .login-required-card {
    padding: 22px;
    border-radius: 20px;
  }

  .login-required-card h2 {
    font-size: 24px;
  }

  .login-required-card__actions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
