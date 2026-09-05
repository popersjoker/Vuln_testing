import Vue from 'vue';
import VueRouter from 'vue-router';

// 导入目标组件
import Fuzz from '@/views/afl/fuzz/index.vue';

Vue.use(VueRouter);

const routes = [
  // 其他路由配置
  {
    path: '/afl/fuzz',
    name: 'Fuzz',
    component: Fuzz,
    props: true
  }
];

const router = new VueRouter({
  routes
});

export default router;
