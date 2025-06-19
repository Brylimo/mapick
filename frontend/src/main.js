import { createApp, ref } from 'vue'
import App from './App.vue'
import { VueQueryPlugin } from '@tanstack/vue-query'
import router from './router'
import * as tokenAPI from './lib/api/tokenAPI'
import 'normalize.css'

const app = createApp(App)

const globalStatus = ref({
  isLoggedIn: false,
  loginUser: {}
})

app.provide(
  'globalStatus',
  globalStatus
)
app.use(router)
app.use(VueQueryPlugin)

// 전역 네비게이션 가드 추가
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !globalStatus.value.isLoggedIn) {

    // 새로 고침하면 로그인 안되어 있는 것처럼 보일 수 있음 -> 백엔드에 api call을 보내 확인
    const fetchData = async () => {
        try {
            const response = await tokenAPI.validate();

            if (response.status === 403) {
                next("/login");
            } else if (response.status === 200) {
                globalStatus.value.loginUser = response?.data?.data
				        globalStatus.value.isLoggedIn = true

                next()
            }
        } catch (e) {
            console.error(e)

            next("/login");
        }
    };

    fetchData();
  } else if (from.path === "/outh-success") {

     const fetchData = async () => {
        try {
            const response = await tokenAPI.validate();

            if (response.status === 403) {
                next("/login");
            } else if (response.status === 200) {
                globalStatus.value.loginUser = response?.data?.data
                globalStatus.value.isLoggedIn = true

                next()
            }
        } catch (e) {
            console.error(e)

            next("/login");
        }
    };

    fetchData();
  
  } else {
    next() // 통과
  }
})

app.mount('#app')
