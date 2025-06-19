import { createRouter, createWebHistory } from 'vue-router'

//attraction
import AttractionMain from '../views/attraction/AttractionMain.vue'

// notice
import NoticeCreate from '@/views/notice/NoticeCreate.vue'
import NoticeList from '@/views/notice/NoticeList.vue'
import NoticeDetail from '@/views/notice/NoticeDetail.vue'
import NoticeUpdate from '@/views/notice/NoticeUpdate.vue'

// hotplace
import HotplaceList from '@/views/hotplace/Hotplace.vue'

// aichat
import AiChatMain from '@/views/aichat/AiChatMain.vue'

// auth
import Login from '@/views/member/LoginForm.vue'
import Register from '@/views/member/MemberJoin.vue'
import OAuthSuccess from '@/views/oauth/OAuthSuccess.vue'

// member
import MemberDetail from '@/views/member/MemberDetail.vue'
import MemberUpdate from '@/views/member/MemberUpdate.vue'
import MemberFindPw from '@/views/member/MemberFindPw.vue'
import MemberChangePw from '@/views/member/MemberChangePw.vue'
import MemberChangePwResult from '@/views/member/MemberChangePwResult.vue'

// community
import CommunityCreate from '@/views/community/CommunityCreate.vue'
import CommunityList from '@/views/community/CommunityList.vue'
import CommunityMain from '@/views/community/CommunityMain.vue'
import CommunityBoard from '@/views/community/CommunityBoard.vue'
import CommunityBoardCreate from '@/views/community/CommunityBoardCreate.vue'
import CommunityRegist from '@/views/community/CommunityRegist.vue'
import CommunityBoardDetail from '@/views/community/CommunityBoardDetail.vue'
import CommunityBoardUpdate from '@/views/community/CommunityBoardUpdate.vue'
import communityJoinPermit from '@/views/community/CommunityJoinPermit.vue'

// plan
import PlanList from '@/views/plan/PlanList.vue'
import PlanDetail from '@/views/plan/PlanDetail.vue'
import Timeline from '@/views/plan/Timeline.vue'

import MainView from '@/views/MainView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: MainView,
  },
  {
    path: '/outh-success',
    name: 'oauthSuccess',
    component: OAuthSuccess,
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
  },
  {
    path: '/register',
    name: 'register',
    component: Register,
  },
  {
    path: '/community',
    children: [
      {
        path: ':id/community-board/',
        name: 'communityBoard',
        component: CommunityBoard,
        meta: { requiresAuth: true },
      },
      {
        path: ':id/community-board/create',
        name: 'CommunityBoardCreate',
        component: CommunityBoardCreate,
        meta: { requiresAuth: true },
      },
      {
        path: ':id/community-regist',
        name: 'CommunityRegist',
        component: CommunityRegist,
        meta: { requiresAuth: true },
      },
      {
        path: ':id/community-board/detail/:bid',
        name: 'CommunityDetail',
        component: CommunityBoardDetail,
        meta: { requiresAuth: true },
      },
      {
        path: ':id/community-board/update/:bid',
        name: 'CommunityBoardUpdate',
        component: CommunityBoardUpdate,
        meta: { requiresAuth: true },
      },
      {
        path: ':id/community-main',
        name: 'communityMain',
        component: CommunityMain,
        meta: { requiresAuth: true },
      },
      {
        path: 'community-form',
        name: 'communityForm',
        component: CommunityCreate,
        meta: { requiresAuth: true },
      },
      {
        path: 'community-list',
        name: 'communityList',
        component: CommunityList,
        meta: { requiresAuth: true },
      },
      {
      path: ':id/community-join-permit',
      name: 'communityJoinPermit',
      component: communityJoinPermit,
      meta: { requiresAuth: true },
      },
    ],
  },
  {
    path: '/members',
    children: [
      {
        path: 'member-detail',
        name: 'memberDetail',
        component: MemberDetail,
        meta: { requiresAuth: true },
      },
      {
        path: 'member-update',
        name: 'memberUpdate',
        component: MemberUpdate,
        meta: { requiresAuth: true },
      },
      {
        path: '/find-password',
        name: 'MemberFindPw',
        component: MemberFindPw,
      },
      {
        path: '/change-password',
        name: 'MemberChangePw',
        component: MemberChangePw,
      },
      {
        path: '/change-password-result',
        name: 'MemberChangePwResult',
        component: MemberChangePwResult,
      },
    ],
  },
  { path: '/attraction', name: 'Attraction', component: AttractionMain },
  {
    path: '/notice',
    children: [
      { path: '', name: 'NoticeList', component: NoticeList },
      { path: ':id', name: 'NoticeDetail', component: NoticeDetail },
      { path: 'create', name: 'NoticeCreate', component: NoticeCreate },
      { path: 'update/:id', name: 'NoticeUpdate', component: NoticeUpdate },
    ],
  },
  { path: '/hotplaces', name: 'HotplaceList', component: HotplaceList },
  { path: '/aichat', name: 'AiChat', component: AiChatMain },
  {
    path: '/plan',
    children: [
      { path: '', name: 'PlanList', component: PlanList },
      { path: 'detail', name: 'PlanDetail', component: PlanDetail },
      { path: 'timeline', name: 'Timeline', component: Timeline },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
