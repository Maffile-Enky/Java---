import { onMounted, onUnmounted, nextTick } from 'vue'

export function useScrollReveal(selector = '.reveal', options = {}) {
  let observer = null

  function createObserver() {
    if (observer) return observer
    observer = new IntersectionObserver((entries) => {
      entries.forEach((entry, i) => {
        if (entry.isIntersecting) {
          setTimeout(() => entry.target.classList.add('visible'), i * (options.stagger || 80))
          observer.unobserve(entry.target)
        }
      })
    }, { threshold: 0.1, rootMargin: '0px 0px -40px 0px', ...options })
    return observer
  }

  function observe() {
    const els = document.querySelectorAll(selector + ':not(.visible)')
    if (els.length) {
      const obs = createObserver()
      els.forEach(el => obs.observe(el))
    }
  }

  onMounted(observe)

  onUnmounted(() => {
    observer?.disconnect()
    observer = null
  })

  // Call after async data loads to observe newly rendered elements
  function reobserve() {
    nextTick(observe)
  }

  return { reobserve }
}
