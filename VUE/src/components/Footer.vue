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
  font-size: 1.1rem;
}

.section ul {
  list-style: none;
  padding: 0;
}

.section ul li {
  margin-bottom: 0.5rem;
}

.section p {
  margin-bottom: 0.5rem;
  line-height: 1.6;
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
  font-size: 0.9rem;
}

@media (max-width: 1199px) {
  .footer {
    padding: 1.75rem 0;
  }

  .footer-content {
    gap: 1.75rem;
    margin-bottom: 1.75rem;
  }

  .section h3 {
    font-size: 1.05rem;
    margin-bottom: 0.9rem;
  }

  .section p,
  .section ul li {
    font-size: 0.95rem;
  }
}

@media (max-width: 991px) {
  .footer {
    padding: 1.5rem 0;
  }

  .footer-content {
    gap: 1.5rem;
    margin-bottom: 1.5rem;
  }

  .section h3 {
    font-size: 1rem;
    margin-bottom: 0.8rem;
  }

  .section p,
  .section ul li {
    font-size: 0.9rem;
  }

  .footer-bottom {
    padding-top: 0.9rem;
    font-size: 0.85rem;
  }
}

@media (max-width: 767px) {
  .footer {
    padding: 1.25rem 0;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 1.25rem;
    margin-bottom: 1.25rem;
  }

  .section h3 {
    font-size: 0.95rem;
    margin-bottom: 0.7rem;
  }

  .section p,
  .section ul li {
    font-size: 0.85rem;
  }

  .footer-bottom {
    padding-top: 0.75rem;
    font-size: 0.8rem;
  }
}

@media (max-width: 479px) {
  .footer {
    padding: 1rem 0;
  }

  .footer-content {
    gap: 1rem;
    margin-bottom: 1rem;
  }

  .section h3 {
    font-size: 0.9rem;
    margin-bottom: 0.6rem;
  }

  .section p,
  .section ul li {
    font-size: 0.8rem;
  }

  .footer-bottom {
    padding-top: 0.6rem;
    font-size: 0.75rem;
  }
}
</style>
