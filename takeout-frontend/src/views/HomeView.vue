<template>
  <div class="home glass-panel">
    <h1>Takeout Platform</h1>
    <p>Welcome to the ultimate food delivery experience.</p>
    <router-link to="/auth/login" class="btn">Login to Continue</router-link>
  </div>
</template>

<style scoped>
.home {
  max-width: 800px;
  margin: 100px auto;
  padding: 40px;
  text-align: center;
}
.btn {
  display: inline-block;
  margin-top: 20px;
  padding: 12px 24px;
  background-color: var(--primary-color);
  color: white;
  text-decoration: none;
  border-radius: 8px;
  transition: transform 0.2s, box-shadow 0.2s;
}
.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}
</style>
