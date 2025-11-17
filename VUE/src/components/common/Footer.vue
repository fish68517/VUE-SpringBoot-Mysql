<template>
  <footer class="footer-container">
    <div class="footer-content">
      <div class="footer-section">
        <p class="copyright">
          © 2024 小鲨鱼健身管理系统. All rights reserved.
        </p>
      </div>

      <div class="footer-section footer-links">
        <a href="#" class="footer-link" @click.prevent="handleLinkClick('about')">关于我们</a>
        <span class="separator">|</span>
        <a href="#" class="footer-link" @click.prevent="handleLinkClick('contact')">联系我们</a>
        <span class="separator">|</span>
        <a href="#" class="footer-link" @click.prevent="handleLinkClick('terms')">服务条款</a>
        <span class="separator">|</span>
        <a href="#" class="footer-link" @click.prevent="handleLinkClick('privacy')">隐私政策</a>
      </div>

      <div class="footer-section social-icons">
        <el-icon class="social-icon" title="微信"><ChatDotRound /></el-icon>
        <el-icon class="social-icon" title="微博"><Share /></el-icon>
        <el-icon class="social-icon" title="邮箱"><Message /></el-icon>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ChatDotRound, Share, Message } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const handleLinkClick = (type) => {
  const messages = {
    about: '关于我们页面开发中...',
    contact: '联系我们页面开发中...',
    terms: '服务条款页面开发中...',
    privacy: '隐私政策页面开发中...'
  }
  
  ElMessage.info(messages[type] || '页面开发中...')
}
</script>

<style scoped>
.footer-container {
  background-color: #fff;
  border-top: 1px solid #e6e6e6;
  padding: 20px;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.footer-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.copyright {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.footer-links {
  display: flex;
  align-items: center;
  gap: 10px;
}

.footer-link {
  color: #606266;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.footer-link:hover {
  color: #409eff;
}

.separator {
  color: #dcdfe6;
}

.social-icons {
  display: flex;
  gap: 15px;
}

.social-icon {
  font-size: 20px;
  color: #909399;
  cursor: pointer;
  transition: color 0.3s;
}

.social-icon:hover {
  color: #409eff;
}

/* Responsive design */
@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }

  .footer-section {
    justify-content: center;
  }

  .footer-links {
    flex-wrap: wrap;
    justify-content: center;
  }

  .copyright {
    font-size: 12px;
  }

  .footer-link {
    font-size: 12px;
  }
}
</style>
