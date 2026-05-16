<template>
  <AppLayout :show-header="true" :show-nav="true">
    <router-view />
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/components/layout/AppLayout.vue'
</script>
