import './base.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'

const app = createApp(App)

if (import.meta.env.DEV) {
    import('mimic')
}

app.use(createPinia())

app.mount('#app')
