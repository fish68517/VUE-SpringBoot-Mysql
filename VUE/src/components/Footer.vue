<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-section">
        <h4>About</h4>
        <p>旅行日记 helps you plan, record, and share your travel experiences.</p>
      </div>

      <div class="footer-section">
        <h4>Quick Links</h4>
        <ul>
          <li><router-link to="/dashboard">Dashboard</router-link></li>
          <li><router-link to="/records">My Records</router-link></li>
          <li><router-link to="/plans">My Plans</router-link></li>
          <li><router-link to="/social">Social Feed</router-link></li>
        </ul>
      </div>

      <div class="footer-section" v-if="false">
        <h4>Support</h4>
        <ul>
          <li><a href="#" target="_blank">Help Center</a></li>
          <li><a href="#" target="_blank">Contact Us</a></li>
          <li><a href="#" target="_blank">Privacy Policy</a></li>
          <li><a href="#" target="_blank">Terms of Service</a></li>
        </ul>
      </div>

      <div class="footer-section" v-if="false">
        <h4>Follow Us</h4>
        <div class="social-links">
          <a href="#" class="social-link" title="Facebook">f</a>
          <a href="#" class="social-link" title="Twitter">𝕏</a>
          <a href="#" class="social-link" title="Instagram">📷</a>
        </div>
      </div>
    </div>

    <div class="footer-bottom">
      <p>&copy; 2024 旅行日记. All rights reserved.</p>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.footer {
  background: #2c3e50;
  color: #ecf0f1;
  padding: 40px 20px 20px;
  margin-top: 60px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto 30px;
}

.footer-section h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: bold;
  color: #fff;
}

.footer-section p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: #bdc3c7;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section li {
  margin-bottom: 10px;
}

.footer-section a {
  color: #bdc3c7;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.footer-section a:hover {
  color: #667eea;
}

.social-links {
  display: flex;
  gap: 15px;
}

.social-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background-color: rgba(102, 126, 234, 0.2);
  border-radius: 50%;
  color: #667eea;
  text-decoration: none;
  font-weight: bold;
  transition: all 0.3s ease;
}

.social-link:hover {
  background-color: #667eea;
  color: white;
  transform: translateY(-2px);
}

.footer-bottom {
  border-top: 1px solid #34495e;
  padding-top: 20px;
  text-align: center;
}

.footer-bottom p {
  margin: 0;
  font-size: 13px;
  color: #95a5a6;
}

/* Responsive design */
@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }

  .footer {
    padding: 30px 15px 15px;
    margin-top: 40px;
  }

  .footer-section h4 {
    font-size: 14px;
  }

  .footer-section p,
  .footer-section a {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .footer {
    padding: 20px 10px 10px;
    margin-top: 30px;
  }

  .footer-section h4 {
    font-size: 13px;
    margin-bottom: 10px;
  }

  .social-links {
    gap: 10px;
  }

  .social-link {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }
}
</style>
