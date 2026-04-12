<template>
  <router-view />
</template>

<script setup>
</script>

<style>
/* Base Premium Styles */
:root {
  --primary-color: #ff6b6b;
  --secondary-color: #4ecdc4;
  --dark-bg: #1a1a2e;
  --light-bg: #f7f9fc;
  --text-main: #2d3436;
}

body {
  margin: 0;
  font-family: 'Inter', 'Roboto', sans-serif;
  background-color: var(--light-bg);
  color: var(--text-main);
  -webkit-font-smoothing: antialiased;
}

/* Glassmorphism utility */
.glass-panel {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.05);
}
</style>
