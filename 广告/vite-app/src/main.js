import "@/assets/less/index.less";
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

const app = createApp(App)
app.use(router).mount('#app');
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
