<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-section">
        <h3>关于我们</h3>
        <p>文山壮族刺绣网站系统致力于传承和展示壮族非遗刺绣文化，推动非遗文化活态传承。</p>
      </div>

      <div class="footer-section">
        <h3>快速链接</h3>
        <ul>
          <li><router-link to="/">首页</router-link></li>
          <li><router-link to="/artworks">作品展示</router-link></li>
          <li><router-link to="/knowledge">知识科普</router-link></li>
          <li><router-link to="/community">互动交流</router-link></li>
        </ul>
      </div>

      <div class="footer-section">
        <h3>联系我们</h3>
        <p>邮箱：{{ contactEmail }}</p>
        <p>电话：{{ contactPhone }}</p>
      </div>

      <div class="footer-section">
        <h3>关注我们</h3>
        <div class="social-links">
          <a href="#" class="social-icon">微博</a>
          <a href="#" class="social-icon">微信</a>
          <a href="#" class="social-icon">抖音</a>
        </div>
      </div>
    </div>

    <div class="footer-bottom">
      <p>&copy; 2024 文山壮族刺绣网站系统. 保留所有权利。</p>
    </div>
  </footer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import  AdminSystemService  from '../services/AdminSystemService'

const router = useRouter()
const contactEmail = ref('contact@zhuang-embroidery.com')
const contactPhone = ref('0876-1234567')

onMounted(async () => {
  try {
    const response = await AdminSystemService.getSettings()
    if (response.data) {
      contactEmail.value = response.data.contactEmail || contactEmail.value
      contactPhone.value = response.data.contactPhone || contactPhone.value
    }
  } catch (error) {
    console.error('Failed to load system settings:', error)
  }
})
</script>

<style scoped>
.footer {
  background: linear-gradient(135deg, #0066cc 0%, #004499 100%);
  color: white;
  padding: var(--spacing-xl) var(--spacing-lg);
  margin-top: var(--spacing-xl);
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-lg);
  max-width: 1200px;
  margin: 0 auto;
  margin-bottom: var(--spacing-lg);
}

.footer-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.footer-section h3 {
  font-size: var(--font-size-lg);
  font-weight: 600;
  margin-bottom: var(--spacing-sm);
}

.footer-section p {
  font-size: var(--font-size-sm);
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.8);
}

.footer-section ul {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.footer-section a {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: color 0.3s ease;
  font-size: var(--font-size-sm);
}

.footer-section a:hover {
  color: white;
}

.social-links {
  display: flex;
  gap: var(--spacing-md);
}

.social-icon {
  display: inline-block;
  padding: var(--spacing-sm) var(--spacing-md);
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: var(--border-radius-md);
  color: white;
  text-decoration: none;
  transition: all 0.3s ease;
  font-size: var(--font-size-sm);
}

.social-icon:hover {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.footer-bottom {
  text-align: center;
  padding-top: var(--spacing-lg);
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  font-size: var(--font-size-sm);
  color: rgba(255, 255, 255, 0.7);
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: var(--spacing-md);
  }

  .footer {
    padding: var(--spacing-lg) var(--spacing-md);
  }
}
</style>
