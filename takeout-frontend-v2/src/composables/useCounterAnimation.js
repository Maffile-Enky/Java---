import { onMounted, onUnmounted } from 'vue'

export function useCounterAnimation(selector = '.stats-bar') {
  let observer = null

  onMounted(() => {
    observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.querySelectorAll('[data-target]').forEach(num => {
            const target = parseInt(num.dataset.target)
            const start = performance.now()
            const animate = (now) => {
              const progress = Math.min((now - start) / 2000, 1)
              const eased = 1 - Math.pow(1 - progress, 4)
              const current = Math.floor(target * eased)
              num.textContent = target > 1000
                ? current.toLocaleString() + '+'
                : current + (target < 100 ? '' : '+')
              if (progress < 1) requestAnimationFrame(animate)
            }
            requestAnimationFrame(animate)
          })
          observer.unobserve(entry.target)
        }
      })
    }, { threshold: 0.3 })
    document.querySelectorAll(selector).forEach(el => observer.observe(el))
  })

  onUnmounted(() => {
    observer?.disconnect()
  })
}
