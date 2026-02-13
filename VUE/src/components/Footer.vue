<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="section">
          <h3>关于平台</h3>
          <p>纳西族纹样数字化展示平台致力于传播和保护纳西族文化遗产。</p>
        </div>
        <div class="section">
          <h3>快速链接</h3>
          <ul>
            <li><router-link to="/">首页</router-link></li>
            <li><router-link to="/patterns">资源库</router-link></li>
            <li><router-link to="/science">科普</router-link></li>
            <li><router-link to="/works">原创作品</router-link></li>
          </ul>
        </div>
        <div class="section">
          <h3>联系我们</h3>
          <p>邮箱: info@naxi-pattern.com</p>
          <p>电话: +86-123-4567-8900</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2024 纳西族纹样数字化展示平台. 保留所有权利。</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.footer {
  background-color: #333;
  color: white;
  padding: 2rem 0;
  margin-top: auto;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.section h3 {
  margin-bottom: 1rem;
  color: #ffd700;
}

.section ul {
  list-style: none;
  padding: 0;
}

.section ul li {
  margin-bottom: 0.5rem;
}

.section a {
  color: #ddd;
  text-decoration: none;
  transition: color 0.3s;
}

.section a:hover {
  color: #ffd700;
}

.footer-bottom {
  text-align: center;
  padding-top: 1rem;
  border-top: 1px solid #555;
  color: #999;
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
  }
}
</style>
