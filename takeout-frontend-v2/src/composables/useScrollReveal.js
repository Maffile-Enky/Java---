import { onMounted, onUnmounted } from 'vue'

export function useScrollReveal(selector = '.reveal', options = {}) {
  let observer = null

  onMounted(() => {
    const els = document.querySelectorAll(selector)
    observer = new IntersectionObserver((entries) => {
      entries.forEach((entry, i) => {
        if (entry.isIntersecting) {
          setTimeout(() => entry.target.classList.add('visible'), i * (options.stagger || 80))
          observer.unobserve(entry.target)
        }
      })
    }, { threshold: 0.1, rootMargin: '0px 0px -40px 0px', ...options })
    els.forEach(el => observer.observe(el))
  })

  onUnmounted(() => {
    observer?.disconnect()
  })
}
