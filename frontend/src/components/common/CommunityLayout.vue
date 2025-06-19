<template>
  <div class="subWrap">
    <div class="wrapper">
      <header class="header">
        <h1 class="logo">
          <RouterLink :to="`/community/${community.communityId}/community-main`">
            <span class="logo-title">{{ community.communityName }}</span>
          </RouterLink>
        </h1>
        <div class="util">
          <ul class="nav navbar-nav">
            <li class="dropdown user user-menu">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                <span class="hidden-xs">{{ globalStatus.loginUser.name }}님</span>
              </a>
              <ul class="dropdown-menu">
                <li class="user-footer">
                  <div style="padding-bottom: 10px">
                    <RouterLink
                      to="/"
                      class="btn btn-default btn-flat btn-menu-h"
                      style="width: 100%"
                    >
                      메인 페이지
                    </RouterLink>
                  </div>
                  <div>
                    <div
                      class="btn btn-default btn-flat btn-menu-h"
                      style="width: 100%"
                      @click="logout"
                    >
                      로그아웃
                    </div>
                  </div>
                </li>
              </ul>
            </li>
          </ul>
        </div>
        <button class="sub-open-btn">모바일 메뉴 열기</button>
        <div class="m-menu-bg"></div>

        <div class="m-menu-con">
          <div class="m-header-top">
            <p><strong>Mapick</strong></p>
            <button type="button" class="sub-open-close"><span class="skip">메뉴닫기</span></button>
            <div class="m-sub-btn">
              <RouterLink to="/system/mypage/list.mng" class="m-menu-sub-btn"
                >마이페이지</RouterLink
              >
              <div class="m-menu-sub-btn" @click="logout">로그아웃</div>
            </div>
          </div>
        </div>
      </header>

      <section class="contents clear-fix">
        <!-- 좌측 메뉴 -->
        <aside class="lnb">
          <ul class="depth2_ul lnb_depth2__list sidebar sidebar-menu">
            <li class="on">
              <RouterLink :to="`/community/${community.communityId}/community-main`"
                >커뮤니티 지도</RouterLink
              >
            </li>
            <li>
              <RouterLink :to="`/community/${community.communityId}/community-board`"
                >게시판</RouterLink
              >
            </li>
            <!-- 일반 유저 (방장이 아닌 경우) -->
            <li v-if="community.registeredBy !== globalStatus.loginUser?.mno">
              <RouterLink :to="`/community/${community.communityId}/community-regist`"
                >가입하기</RouterLink
              >
            </li>

            <!-- 방장 (등록자) 전용 메뉴 -->
            <li v-if="community.registeredBy === globalStatus.loginUser?.mno">
              <RouterLink :to="`/community/${community.communityId}/community-join-permit`"
                >가입 허가</RouterLink
              >
            </li>
          </ul>

          <button @click.stop="handleCommunityLike" class="sidebar-like-button" :class="{ liked: isLiked }">
            ❤️ 커뮤니티 좋아요
          </button>
        </aside>

        <div class="slot-wrapper">
          <slot />
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { inject, onMounted, ref } from 'vue'
import { useLogoutQuery } from '../../hooks/queries/auth/useLogoutQuery'
import { useRoute, useRouter } from 'vue-router'
import * as communityAPI from '../../lib/api/communityAPI'
import * as communityLikeAPI from '../../lib/api/communityLikeAPI'

const globalStatus = inject('globalStatus')
const { logoutRefetch } = useLogoutQuery()
const route = useRoute()
const router = useRouter()

const isLiked = ref(false)
const community = ref({})
const communityId = route.params.id || route.path.split('/').pop()

const handleCommunityLike = async () => {
    try {
        const res = await communityLikeAPI.checkCommunityLike({ communityId })

        if (res.data.checked) {
            const res = await communityLikeAPI.deleteCommunityLike({ communityId })
            
            if (res.state === 200) { // 성공
                isLiked.value = false
            }
        } else {
            const res = await communityLikeAPI.createCommunityLike({ communityId })
        
            if (res.state === 200) { // 성공
                isLiked.value = true
            }
        }
    } catch (e) {
        alert('좋아요 처리 중 오류 발생')
    }
}

const logout = async () => {
  try {
    const res = await logoutRefetch()

    if (res?.data?.status === 200) {
      globalStatus.value.isLoggedIn = false
      globalStatus.value.loginUser = {}
      alert('로그아웃 되었습니다.')
      router.push({ name: 'home' })
    } else {
      alert('로그아웃에 실패했습니다.')
    }
  } catch (err) {
    console.error('로그아웃 오류:', err)
    alert('서버 오류로 로그아웃에 실패했습니다.')
  }
}

onMounted(async () => {
  try {
    const res = await communityAPI.getCommunity({ communityId })
    community.value = res.data

    const res2 = await communityLikeAPI.checkCommunityLike({ communityId })
    isLiked.value = res2.data.checked
  } catch (error) {
    console.error('❌ community 불러오기 실패', error)
  }
})
</script>

<style scoped>
.sidebar-like-button.liked {
  background-color: #ffefef;
  color: red;
  font-weight: bold;
}

.slot-wrapper {
  position: absolute;
  top: 72px;
  left: 290px;
  width: calc(100% - 290px);
  height: calc(100% - 72px);
}

.sidebar-like-button {
  width: 90%;
  margin: 20px auto;
  display: block;
  padding: 12px;
  font-size: 16px;
  color: white;
  background-color: #1976d2;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  text-align: center;
  transition: background-color 0.3s;
}

.sidebar-like-button:hover {
  background-color: #145ca1;
}

.btn-menu-h {
  color: black !important;
}

/* common.css */
@charset "utf-8";
html,
body {
  width: 100%;
}
html,
body {
  line-height: 1.15;
  -webkit-text-size-adjust: 100%;
}
body {
  margin: 0;
  font-size: 15px;
  font-weight: 400;
  color: #404040;
  letter-spacing: -0.8px;
}
em {
  font-style: normal;
}
main {
  display: block;
}
hr {
  box-sizing: content-box;
  height: 0;
  overflow: visible;
}
pre {
  font-family: 'CoreDream', 'sans-serif';
  font-size: 1em;
}
a {
  background-color: transparent;
}
abbr[title] {
  border-bottom: none;
  text-decoration: underline;
  text-decoration: underline dotted;
}
b,
strong {
  font-weight: bolder;
}
small {
  font-size: 14px;
}
sub,
sup {
  font-size: 14px;
  line-height: 0;
  position: relative;
  vertical-align: baseline;
}
sub {
  bottom: -0.25em;
}
sup {
  top: -0.5em;
}
img {
  border-style: none;
}

button,
input,
optgroup,
select,
textarea {
  font-family: 'CoreDream', 'sans-serif';
  line-height: 1.15;
  margin: 0;
}
button,
input {
  overflow: visible;
}
input {
  height: 35px;
  padding: 0px 10px;
  border: 1px solid rgb(230, 230, 230);
  font-size: 14px;
  vertical-align: middle;
  color: #616161;
  -webkit-border-radius: 0;
  background: #fff;
  border-radius: 5px;
}
input.readOnly {
  font-weight: 500;
  background: #f5f5f5;
}

input:-webkit-autofill {
  -webkit-box-shadow: 0 0 0 30px #edf1f4 inset;
  -webkit-text-fill-color: #3a3a3a;
}
input:-webkit-autofill,
input:-webkit-autofill:hover,
input:-webkit-autofill:focus,
input:-webkit-autofill:active {
  transition: background-color 5000s ease-in-out 0s;
}
button,
select {
  text-transform: none;
}
button {
  cursor: pointer;
}
button,
[type='button'],
[type='reset'],
[type='submit'] {
  -webkit-appearance: button;
}
button::-moz-focus-inner,
[type='button']::-moz-focus-inner,
[type='reset']::-moz-focus-inner,
[type='submit']::-moz-focus-inner {
  border-style: none;
  padding: 0;
}
button:-moz-focusring,
[type='button']:-moz-focusring,
[type='reset']:-moz-focusring,
[type='submit']:-moz-focusring {
  outline: 1px dotted;
}

fieldset {
  padding: 0.35em 0.75em 0.625em;
}
legend {
  box-sizing: border-box;
  color: inherit;
  display: table;
  max-width: 100%;
  padding: 0;
  white-space: normal;
}
progress {
  vertical-align: baseline;
}
textarea {
  overflow: auto;
}

[type='checkbox'],
[type='radio'] {
  box-sizing: border-box;
  padding: 0;
}
[type='number']::-webkit-inner-spin-button,
[type='number']::-webkit-outer-spin-button {
  height: auto;
}
[type='search'] {
  -webkit-appearance: textfield;
  outline-offset: -2px;
}
[type='search']::-webkit-search-decoration {
  -webkit-appearance: none;
}
::-webkit-file-upload-button {
  -webkit-appearance: button;
  font: inherit;
}

details {
  display: block;
}
summary {
  display: list-item;
}
template {
  display: none;
}
[hidden] {
  display: none;
}

* {
  margin: 0;
  padding: 0;
}

/* common css custom */
*,
*:after,
*:before {
  box-sizing: border-box;
}
* {
  padding: 0;
  margin: 0;
  font-family: 'CoreDream', 'sans-serif';
}
li {
  list-style: none;
}
a {
  text-decoration: none;
}
table {
  border-collapse: collapse;
  border-spacing: 0;
  table-layout: fixed;
}
table th,
table td {
  text-overflow: ellipsis;
}
button {
  border: 0;
  background: #fff;
  vertical-align: middle;
  font-size: 13px;
  font-weight: 400;
}
select {
  height: 35px;
  padding: 0px 10px;
  border: 1px solid rgb(230, 230, 230);
  font-size: 14px;
  vertical-align: middle;
  color: #616161;
  -webkit-border-radius: 0;
  background: #fff;
  border-radius: 5px;
}
option {
  padding: 8px;
}
select,
option,
label {
  font-size: 14px;
}
label {
  vertical-align: middle;
}

table input[type='text'] {
  height: 30px;
  font-size: 13px;
}
table select {
  height: 30px;
  font-size: 13px;
}
table button {
  height: 30px;
  font-size: 13px;
}

.clear-fix:after {
  content: '';
  display: block;
  clear: both;
}
.skip {
  position: absolute;
  clip: rect(0 0 0 0);
  width: 1px;
  height: 1px;
  margin: -1px;
  overflow: hidden;
}
.col-empty {
  text-indent: -9999px;
  font-size: 0;
}

/*white space*/
.mg0 {
  margin: 0px auto;
}
.mt5 {
  margin-top: 5px !important;
}
.mt10 {
  margin-top: 10px !important;
}
.mt15 {
  margin-top: 15px !important;
}
.mt20 {
  margin-top: 20px !important;
}
.mt25 {
  margin-top: 25px !important;
}
.mt30 {
  margin-top: 30px !important;
}
.mt35 {
  margin-top: 35px !important;
}
.mt40 {
  margin-top: 40px !important;
}
.mt45 {
  margin-top: 45px !important;
}
.mt50 {
  margin-top: 50px !important;
}
.mt55 {
  margin-top: 55px !important;
}
.mt60 {
  margin-top: 60px !important;
}

.ml5 {
  margin-left: 5px !important;
}
.ml10 {
  margin-left: 10px !important;
}
.ml14 {
  margin-left: 14px !important;
}
.ml15 {
  margin-left: 15px !important;
}
.ml20 {
  margin-left: 20px !important;
}
.ml25 {
  margin-left: 25px !important;
}
.ml30 {
  margin-left: 30px !important;
}
.ml35 {
  margin-left: 35px !important;
}
.ml40 {
  margin-left: 40px !important;
}
.ml45 {
  margin-left: 45px !important;
}
.ml50 {
  margin-left: 50px !important;
}
.ml55 {
  margin-left: 55px !important;
}
.ml60 {
  margin-left: 60px !important;
}

.mr5 {
  margin-right: 5px !important;
}
.mr10 {
  margin-right: 10px !important;
}
.mr15 {
  margin-right: 15px !important;
}
.mr20 {
  margin-right: 20px !important;
}
.mr25 {
  margin-right: 25px !important;
}
.mr30 {
  margin-right: 30px !important;
}
.mr35 {
  margin-right: 35px !important;
}
.mr40 {
  margin-right: 40px !important;
}
.mr45 {
  margin-right: 45px !important;
}
.mr50 {
  margin-right: 50px !important;
}
.mr55 {
  margin-right: 55px !important;
}
.mr60 {
  margin-right: 60px !important;
}

.mb5 {
  margin-bottom: 5px !important;
}
.mb10 {
  margin-bottom: 10px !important;
}
.mb15 {
  margin-bottom: 15px !important;
}
.mb20 {
  margin-bottom: 20px !important;
}
.mb25 {
  margin-bottom: 25px !important;
}
.mb30 {
  margin-bottom: 30px !important;
}
.mb35 {
  margin-bottom: 35px !important;
}
.mb40 {
  margin-bottom: 40px !important;
}
.mb45 {
  margin-bottom: 45px !important;
}
.mb50 {
  margin-bottom: 50px !important;
}
.mb55 {
  margin-bottom: 55px !important;
}
.mb60 {
  margin-bottom: 60px !important;
}

/*padding*/
.pd5 {
  padding: 5px !important;
}
.pd10 {
  padding: 10px !important;
}
.pd20 {
  padding: 20px !important;
}
.pd30 {
  padding: 30px !important;
}
.pd40 {
  padding: 40px !important;
}
.pd50 {
  padding: 50px !important;
}

.pt5 {
  padding-top: 5px !important;
}
.pt10 {
  padding-top: 10px !important;
}
.pt15 {
  padding-top: 15px !important;
}
.pt20 {
  padding-top: 20px !important;
}
.pt25 {
  padding-top: 25px !important;
}
.pt30 {
  padding-top: 30px !important;
}
.pt35 {
  padding-top: 35px !important;
}
.pt40 {
  padding-top: 40px !important;
}
.pt45 {
  padding-top: 45px !important;
}
.pt50 {
  padding-top: 50px !important;
}
.pt55 {
  padding-top: 55px !important;
}
.pt60 {
  padding-top: 60px !important;
}

.pl5 {
  padding-left: 5px !important;
}
.pl10 {
  padding-left: 10px !important;
}
.pl15 {
  padding-left: 15px !important;
}
.pl20 {
  padding-left: 20px !important;
}
.pl25 {
  padding-left: 25px !important;
}
.pl30 {
  padding-left: 30px !important;
}
.pl35 {
  padding-left: 35px !important;
}
.pl40 {
  padding-left: 40px !important;
}
.pl45 {
  padding-left: 45px !important;
}
.pl50 {
  padding-left: 50px !important;
}
.pl55 {
  padding-left: 55px !important;
}
.pl60 {
  padding-left: 60px !important;
}

.pr5 {
  padding-right: 5px !important;
}
.pr10 {
  padding-right: 10px !important;
}
.pr15 {
  padding-right: 15px !important;
}
.pr20 {
  padding-right: 20px !important;
}
.pr25 {
  padding-right: 25px !important;
}
.pr30 {
  padding-right: 30px !important;
}
.pr35 {
  padding-right: 35px !important;
}
.pr40 {
  padding-right: 40px !important;
}
.pr45 {
  padding-right: 45px !important;
}
.pr50 {
  padding-right: 50px !important;
}
.pr55 {
  padding-right: 55px !important;
}
.pr60 {
  padding-right: 60px !important;
}

.pb5 {
  padding-bottom: 5px !important;
}
.pb10 {
  padding-bottom: 10px !important;
}
.pb15 {
  padding-bottom: 15px !important;
}
.pb20 {
  padding-bottom: 20px !important;
}
.pb25 {
  padding-bottom: 25px !important;
}
.pb30 {
  padding-bottom: 30px !important;
}
.pb35 {
  padding-bottom: 35px !important;
}
.pb40 {
  padding-bottom: 40px !important;
}
.pb45 {
  padding-bottom: 45px !important;
}
.pb50 {
  padding-bottom: 50px !important;
}
.pb55 {
  padding-bottom: 55px !important;
}
.pb60 {
  padding-bottom: 60px !important;
}

.colNum {
  width: 5%;
}
.col10 {
  width: 10%;
}
.col20 {
  width: 20%;
}
.col30 {
  width: 30%;
}
.col40 {
  width: 40%;
}
.col50 {
  width: 50%;
}
.colAuto {
  width: auto;
}

.txtLt {
  text-align: left;
}
.txtRt {
  text-align: right;
}
.txtCt {
  text-align: center;
}

.inline-block {
  display: inline-block;
  vertical-align: middle;
}
.floatLt {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 12px;
  float: left;
}
.floatRt {
  float: right;
}

.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* component.css */
@charset "utf-8";

/*공통폰트*/
@font-face {
  font-family: 'CoreDream';
  src:
    url('/fonts/SCDream1.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-1Thin.woff')
      format('woff');
  font-style: normal;
  font-weight: 100;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream2.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-2ExtraLight.woff')
      format('woff');
  font-style: normal;
  font-weight: 200;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream3.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-3Light.woff')
      format('woff');
  font-style: normal;
  font-weight: 300;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream4.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-4Regular.woff')
      format('woff');
  font-style: normal;
  font-weight: 400;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream5.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-5Medium.woff')
      format('woff');
  font-style: normal;
  font-weight: 500;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream6.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-6Bold.woff')
      format('woff');
  font-style: normal;
  font-weight: 600;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream7.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-7ExtraBold.woff')
      format('woff');
  font-style: normal;
  font-weight: 700;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream8.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-8Heavy.woff')
      format('woff');
  font-style: normal;
  font-weight: 800;
}
@font-face {
  font-family: 'CoreDream';
  src:
    url('../fonts/SCDream9.otf'),
    url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_six@1.2/S-CoreDream-9Black.woff')
      format('woff');
  font-style: normal;
  font-weight: 900;
}

/*공통텍스트*/
.subTxt1 {
  font-size: 24px;
  font-weight: 600;
}
.subTxt2 {
  font-size: 18px;
  font-weight: 600;
  display: block;
  margin-bottom: 10px;
  background: url('/img/sub/subtit.png') no-repeat left center;
  padding-left: 28px;
}
.subTxt3 {
  font-size: 20px;
  font-weight: 600;
}
.subTxt4 {
  font-size: 18px;
  font-weight: 600;
}

/*기본버튼*/
.cmnBtn {
  min-width: 80px;
  height: 34px;
  background: #262626;
  color: rgb(255, 255, 255);
  padding: 6px 16px;
  padding-top: 9px;
}
.cmnBtn.register {
  background: #1c8ae8;
  border-radius: 5px;
}
.cmnBtn.save {
  background: #23263e;
}
.cmnBtn.cancel {
  background: #3e3e3e;
  border-radius: 5px;
}
.cmnBtn.list {
  background: #383838;
  border-radius: 5px;
}
.cmnBtn.search {
  background: #0771cb url(/img/cmn/ico_search.png) no-repeat left 16px center;
  background-size: 13px;
  padding-left: 28px;
  border-radius: 5px;
}
.cmnBtn.delete {
  border: 1px solid rgb(33 118 204);
  color: rgb(33 118 204);
  background: rgb(255, 255, 255);
  font-weight: 500;
  border-radius: 5px;
}
.cmnBtn.etc1 {
  background: #5039ad;
}
.cmnBtn.etc2 {
  background: #7ec4ff;
  border-radius: 5px;
}
.excelBtn {
  padding: 10px 14px;
  background: url(/img/cmn/ico_download.png) no-repeat left 13px center;
  height: 34px;
  background-size: 15px;
  color: #008237;
  padding-left: 33px;
  display: inline-block;
  vertical-align: middle;
  font-size: 13px;
  border: 1px solid #3da36a;
  border-radius: 5px;
}
.excelBtn.type2 {
  background-color: #3f4244;
}

/*테이블 셀 안쪽, input 옆 태그*/
.cellFormBtn {
  min-width: 40px;
  height: 30px;
  background: #262626;
  color: rgb(255, 255, 255);
  padding: 5px 10px;
  font-weight: 300;
  line-height: 1.8px;
}
.cellFormBtn.cellFormBtn1 {
  background: #484848;
}
.cellFormBtn.cellFormBtn2 {
  background: rgb(26 99 193);
}
.cellFormBtn.cellFormBtn3 {
  border: 1px solid #3b3b3b;
  background: rgb(255, 255, 255);
  color: #262626;
}
.cellFormBtn.cellFormBtn4 {
  background: #6e6e6e;
}
.cellFormBtn.cellFormBtn5 {
  background: rgb(26 99 193);
}
.cellFormBtn.cellFormBtn6 {
  background: #5557d7;
}

.cellFormButton {
  min-width: 40px;
  height: 30px;
  background: #262626;
  color: rgb(255, 255, 255);
  padding: 5px 5px;
  font-weight: 300;
  line-height: 1.8px;
}
.cellFormButton.cellFormBtn4 {
  background: #6e6e6e;
}

/*폼태그*/
.inputTxt-xs {
  width: 50px;
}
.inputTxt-sm {
  width: 140px;
}
.inputTxt-md {
  width: 160px;
}
.inputTxt-lg {
  width: 180px;
}
.inputTxt-xlg {
  width: 200px;
}
.inputTxt-full {
  width: 100%;
}

.select-xs {
  min-width: 50px;
}
.select-sm {
  min-width: 140px;
}
.select-md {
  min-width: 160px;
}
.select-lg {
  min-width: 180px;
}
.select-xlg {
  min-width: 200px;
}
.select-full {
  min-width: 100%;
}

/*선택버튼*/
.formChoice-box {
  vertical-align: middle;
  display: inline-block;
}
.form-rdo,
.form-chk {
  vertical-align: middle;
  display: inline-block;
  height: inherit;
}
.form-name {
  vertical-align: middle;
  display: inline-block;
  margin: 0px 2px;
  margin-right: 8px;
  font-size: 15px;
}

/*라벨버튼*/
.label-box {
  vertical-align: middle;
  display: inline-block;
  float: left;
  padding-left: 8px;
}
.label .form-chk {
  vertical-align: middle;
  display: inline-block;
  height: inherit;
}
.label-name1 {
  vertical-align: middle;
  display: inline-block;
  margin: 0px 2px;
  margin-right: 8px;
  font-size: 15px;
  background: #777777;
  border-radius: 5px;
  padding: 10px 20px;
}
.label-name2 {
  vertical-align: middle;
  display: inline-block;
  margin: 0px 2px;
  margin-right: 8px;
  font-size: 15px;
  background: #337ab7;
  border-radius: 5px;
  padding: 10px 20px;
}
.label-name3 {
  vertical-align: middle;
  display: inline-block;
  margin: 0px 2px;
  margin-right: 8px;
  font-size: 15px;
  background: #09a55a;
  border-radius: 5px;
  padding: 10px 20px;
}
.label-name4 {
  vertical-align: middle;
  display: inline-block;
  margin: 0px 2px;
  margin-right: 8px;
  font-size: 15px;
  background: #00bfef;
  border-radius: 5px;
  padding: 10px 20px;
}
.label-name5 {
  vertical-align: middle;
  display: inline-block;
  margin: 0px 2px;
  margin-right: 8px;
  font-size: 15px;
  background: #f39c13;
  border-radius: 5px;
  padding: 10px 20px;
}
.label-name6 {
  vertical-align: middle;
  display: inline-block;
  margin: 0px 2px;
  margin-right: 8px;
  font-size: 15px;
  background: #de4c39;
  border-radius: 5px;
  padding: 10px 20px;
}

/*첨부파일*/
.fileBox {
  font-size: 0px;
}
.fileBox .upload {
  border: 0;
  border-bottom: 1px solid #c9c9c9;
  width: 280px;
  display: inline-block;
  vertical-align: middle;
  height: 35px;
  font-size: 15px;
  color: rgb(139, 139, 139);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 7px;
}
.fileBtn-box {
  position: relative;
  display: inline-block;
  vertical-align: middle;
  margin-left: -1px;
  margin-right: 10px;
}
.fileBtn-box .formFile-btn {
  height: 38px;
  padding: 2px 14px 0px 27px;
  color: #282828;
  border: 1px solid #c9c9c9;
  background: url('/img/cmn/ico_clip1.png') left 11px top 12px / 12px no-repeat;
  font-size: 15px;
}
.fileBtn-box .formFile-realBtn {
  position: absolute;
  inset: 0px;
  width: 100%;
  opacity: 0;
  overflow: hidden;
  cursor: pointer;
  z-index: 10;
}

/*달력*/
.calendar-wr {
  display: inline-block;
  vertical-align: middle;
}
.calendar-item {
  border: 1px solid rgb(230, 230, 230);
  display: inline-block;
  vertical-align: middle;
  position: relative;
  border-radius: 5px;
}
.calendar-item .date-picker {
  width: 180px;
  height: 35px;
  font-size: 14px;
  border: 0;
  padding-right: 40px;
}
.calendar-item .date-picker2 {
  width: 180px;
  height: 35px;
  font-size: 14px;
  border: 0;
  padding-right: 40px;
}
.calendar-item .calendar-btn {
  position: absolute;
  top: 10px;
  right: 6px;
}

/*시간*/
.time-wr {
  display: inline-block;
  vertical-align: middle;
}
.time-item {
  border: 1px solid rgb(230, 230, 230);
  display: inline-block;
  vertical-align: middle;
  position: relative;
  border-radius: 5px;
}
.time-item .time-picker {
  width: 120px;
  height: 35px;
  font-size: 14px;
  border: 0;
  padding-right: 40px;
}
.time-item .time-btn {
  position: absolute;
  top: 8px;
  right: 6px;
}

/*텍스트 에어리어*/
.textarea {
  width: 100%;
  border: 1px solid #c9c9c9;
  resize: none;
  padding: 20px;
  font-weight: 300;
}

/*탭*/
.cmnTabs {
  clear: both;
}
.cmnTabs > ul > li {
  float: left;
  border: 1px solid #c9c9c9;
  width: 25%;
  height: 60px;
  margin-left: -1px;
}
.cmnTabs > ul > li.current {
  background: linear-gradient(45deg, #2f7ed6, #622fd6);
  border: 0;
}
.cmnTabs > ul > li > a {
  display: block;
  color: inherit;
  text-align: left;
  font-weight: 400;
  line-height: 60px;
  text-align: center;
}
.tabCon {
  display: none;
  padding-top: 30px;
}
.cmnTabs > ul > li.current a {
  color: white;
  font-weight: 500;
}
.cmnTabs .tabCon.current {
  display: block;
}

/*페이지네이션*/
.pagination {
  width: 100%;
  margin: 20px 0;
}
.pagination ul {
  text-align: center;
  font-size: 0;
}
.pagination ul li {
  display: inline-block;
  vertical-align: middle;
  margin-right: 4px;
}
.pagination ul li.on a {
  color: #fff !important;
  background: linear-gradient(270deg, #055ab5, #1d8be9);
  border: 0;
  border-radius: 20px;
}
.pagination ul li a {
  width: 37px;
  height: 37px;
  line-height: 29px;
  padding: 5px;
  color: #a4a4a4;
  display: block;
  font-size: 15px;
  padding-bottom: 5px;
  border: 1px solid #e6e6e6;
  font-weight: 500;
  transition: 0.3s;
  border-radius: 20px;
}
.pagination ul li a:hover {
  background-color: #dbdbdb;
}
.pagination ul li.first a,
.pagination ul li.prev a,
.pagination ul li.last a,
.pagination ul li.next a {
  font-size: 0;
  overflow: hidden;
}
.pagination ul li.first a,
.pagination ul li.previous a,
.pagination ul li.last a,
.pagination ul li.next a {
  font-size: 0;
  overflow: hidden;
}
.pagination ul li.first a:hover {
  background-color: #efefef;
}
.pagination ul li.prev a:hover {
  background-color: #efefef;
}
.pagination ul li.previous a:hover {
  background-color: #efefef;
}
.pagination ul li.last a:hover {
  background-color: #efefef;
}
.pagination ul li.next a:hover {
  background-color: #efefef;
}

.pagination ul li.first a {
  background: url('/img/cmn/ico_paging_first.png') no-repeat center;
}
.pagination ul li.prev a {
  background: url('/img/cmn/ico_paging_prev.png') no-repeat center;
}
.pagination ul li.previous a {
  background: url('/img/cmn/ico_paging_prev.png') no-repeat center;
}
.pagination ul li.next a {
  background: url(/img/cmn/ico_paging_next.png) no-repeat left 16px center;
}
.pagination ul li.last a {
  background: url('/img/cmn/ico_paging_last.png') no-repeat left 11px center;
}

/*검색박스*/
.search-box {
  margin: 15px 0px 20px 0;
  transition: all 0.3s ease 0s;
  padding: 22px 24px;
  border: 1px solid #f0f0f0;
  background: #fff;
  box-shadow: 0px 5px 0px 0px #f1f1f1;
  border-radius: 5px;
}
.search-box > * {
  margin: 5px 0;
}
.search-box .form-tit {
  font-weight: 500;
  margin-right: 8px;
  display: inline-block;
  vertical-align: middle;
  font-size: 16px;
}
.search-box select {
  padding: 5px 10px;
}
.search-box select,
.search-box option {
  font-size: 14px;
  border: 1px solid rgb(230, 230, 230);
  border-radius: 5px;
}

.search-form {
  border-bottom: 1px solid #a8a8a8;
  display: inline-block;
  vertical-align: middle;
}
.search-form input[type='text'] {
  border: 0;
  font-size: 14px;
}
.search-form input[type='text']::-webkit-input-placeholder {
  font-size: 15px;
  font-weight: 300;
}

/*목록테이블*/
.listTable {
  text-align: center;
  width: 100%;
  margin-top: 10px;
}
.listTable th {
  padding: 15px 5px;
  font-weight: 500;
  font-size: 15px;
  background-color: #f9f9f9;
  border-top: 2px solid #1e3150;
  color: #1e3150;
}
.listTable td {
  font-size: 14px;
  border-bottom: 1px solid #e8e8e8;
  padding: 18px 4px;
  font-weight: 300;
  color: #4d4d4d;
}
.listTable .link {
  color: inherit;
  font-weight: 500;
}
.listTable .link:hover {
  text-decoration: underline;
}
.listTable tbody tr {
  transition: 0.3s;
}
.listTable tbody tr:hover {
  background: #f0f0f0;
}
.listTable .subject a {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: block;
  color: inherit;
}
.listTable .subject a:hover {
  text-decoration: underline;
}

/*리스트 제목 두개이상*/
.listTable.th-double thead th {
  border: 1px solid #dbdbdb;
}
.listTable.th-double thead th:first-child {
  border-left: 0;
}
.listTable.th-double thead tr:first-child th:first-child {
  border-bottom: 1px solid #ebebeb;
}
.listTable.th-double thead tr:last-child th {
  border-bottom: 1px solid #ebebeb;
}
.listTable.th-double thead th:last-child {
  border-right: 0;
}

/*상세조회테이블*/
.detailTable {
  width: 100%;
  margin: 15px 0;
}
.detailTable tr:first-child th,
.detailTable tr:first-child td {
  border-top: 1px solid #bdbdbd;
}
.detailTable th {
  padding: 18px 5px;
  font-weight: 500;
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  background-color: #f8f8f8;
  text-align: center;
  color: #404040;
}
.detailTable td {
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  padding: 12px 5px;
  text-align: center;
}

/*등록테이블*/
.registerTable {
  margin-bottom: 20px;
  width: 100%;
}
.registerTable tr:first-child th,
.registerTable tr:first-child td {
  border-top: 1px solid #bdbdbd;
}
.registerTable th {
  padding: 12px 5px;
  font-weight: 500;
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  background-color: #f9f9f9;
  text-align: left;
  padding-left: 25px;
}
.registerTable td {
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  padding: 12px 5px;
}

/*컬럼 이미지박스*/
.colImgBox {
  border: 1px solid #e5e5e5;
  position: relative;
}
.no-img {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

/*레이어팝업*/
/*.layerPop {width: 810px;position: absolute;top: 120px;left:50%;transform: translateX(-50%);background:#fff;z-index: 100;padding-bottom: 20px;border: 1px solid #e7e7e7;display: block;}*/
.layerPop {
  width: 810px;
  position: absolute;
  top: 40px;
  left: 76%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 142px;
  margin-bottom: 18px;
}

.layerPop2 {
  width: 810px;
  position: absolute;
  top: 45%;
  right: 15%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop2 .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop2 .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop2 .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop2 .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 180px;
  margin-bottom: 18px;
}

.layerPop3 {
  width: 810px;
  position: absolute;
  top: 28%;
  right: 17%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop3 .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop3 .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop3 .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop3 .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 230px;
  margin-bottom: 18px;
}

.layerPop4 {
  width: 810px;
  position: absolute;
  top: 12%;
  right: -16%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop4 .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop4 .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop4 .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop4 .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 230px;
  margin-bottom: 18px;
}

.layerPop5 {
  width: 810px;
  position: absolute;
  top: 12%;
  right: -16%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop5 .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop5 .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop5 .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop5 .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 388px;
  margin-bottom: 18px;
}

.layerPop6 {
  width: 810px;
  position: absolute;
  top: 41%;
  right: -16%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop6 .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop6 .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop6 .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop6 .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 388px;
  margin-bottom: 18px;
}

.layerPop7 {
  width: 810px;
  position: absolute;
  top: 11%;
  right: 19%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop7 .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop7 .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop7 .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop7 .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 230px;
  margin-bottom: 18px;
}

.layerPop8 {
  width: 810px;
  position: absolute;
  top: 11%;
  right: 2%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 100;
  padding-bottom: 20px;
  border: 1px solid #e7e7e7;
  display: block;
}
.layerPop8 .layerPop-header {
  width: 100%;
  min-height: 60px;
  line-height: 60px;
  padding-left: 20px;
  background: #1b88e6;
  position: relative;
}
.layerPop8 .layerPop-header strong {
  float: left;
  font-size: 23px;
  font-weight: 500;
  color: #fff;
}
.layerPop8 .layerPop-header button {
  position: absolute;
  width: 32px;
  height: 32px;
  background: url('/img/cmn/ico_pop_close.png') no-repeat center;
  font-size: 0;
  top: 14px;
  right: 10px;
  background-size: initial;
}
.layerPop8 .layerPop-con {
  width: 100%;
  padding: 20px 20px;
  height: 500px;
  margin-bottom: 18px;
}

.pop-bg {
  display: none;
  width: 100%;
  height: 100%;
  position: fixed;
  background: rgba(0, 0, 0, 0.4);
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}
.pop-table {
  width: 100%;
}
.pop-table thead th {
  font-weight: 500;
  padding: 10px 5px;
  text-align: center;
  background: #505050;
  font-size: 14px;
  color: #fff;
  border-right: 1px solid #707070;
}
.pop-table thead tr th:last-child {
  border-right: 0;
}
.pop-table tbody th {
  background: #f3f3f3;
  border-bottom: 1px solid #dfdfdf;
  padding: 10px 5px;
  border-top: 1px solid #505050;
  font-weight: 500;
}
.pop-table tbody td {
  text-align: center;
  padding: 10px 5px;
  background: #fff;
  font-size: 14px;
  border-bottom: 1px solid #dfdfdf;
  border-right: 1px solid #dfdfdf;
  font-weight: 300;
  border-top: 1px solid #505050;
}
.pop-table tbody tr td:last-child {
  border-right: 0;
}
.pop-search-box {
  padding: 12px 16px;
  margin-bottom: 10px;
  padding-top: 10px;
  border-radius: 5px;
}
.pop-search-box .pop-search-name {
  margin-right: 10px;
  font-weight: 600;
  display: inline-block;
  vertical-align: middle;
  margin-top: 5px;
}
.pop-search-box select {
  background: transparent;
  font-size: 14px;
  border: 0;
  border-bottom: 1px solid #3a3a3a;
  min-width: 104px;
  font-weight: 500;
  color: #545454;
}
.pop-search-box .search-form {
  border-bottom: 1px solid #3a3a3a;
  height: 35px;
}
.pop-search-box .search-form input[type='text'] {
  background: transparent;
  height: 100%;
  width: 180px;
}
.pop-search-box .search-form input[type='text']::-webkit-input-placeholder {
  font-size: 13px;
  font-weight: 500;
  color: #545454;
}

.layerPop .cellFormBtn {
  border-radius: 4px;
  font-size: 12px;
  height: inherit;
  padding: 7px 8px;
  background: #2672d1;
}

/*view Table*/
.viewTable .viewTop {
  border-top: 1px solid #3a3a3a;
  padding: 40px;
}
.viewTable .viewTop .viewTit {
  font-size: 24px;
  margin-bottom: 20px;
  display: block;
}
.viewTable .viewTop .dateInfo ol li {
  float: left;
  margin-right: 50px;
}
.viewTable .viewTop .dateInfo ol li em {
  position: relative;
  margin-right: 40px;
}
.viewTable .viewTop .dateInfo ol li em:after {
  position: absolute;
  content: '';
  top: 5px;
  right: -21px;
  width: 1px;
  height: 14px;
  background: #979797;
}
.viewTable .viewTop .dateInfo ol li span {
  text-align: left;
  color: #787878;
}

.viewTable .viewCon {
  padding: 40px;
  border-top: 1px solid #e8e8e8;
}
.viewTable .viewCon p {
  font-weight: 300;
  font-size: 16px;
  line-height: 2;
}

.view-attachFile {
  background: #f3f3f3;
  padding: 40px;
}
.view-attachFile strong {
  float: left;
  margin-right: 20px;
  font-size: 16px;
  font-weight: 500;
}
.view-attachFile .attachFile {
  float: left;
}
.attachFile {
  display: inline-block;
}
.attachFile .attachFile-name {
  display: inline-block;
  vertical-align: middle;
  font-size: 15px;
  background: url('/img/cmn/ico_clip1.png') no-repeat left center;
  padding-left: 20px;
  background-size: 14px auto;
  color: #787878;
  margin-right: 5px;
}
.attachFile .attachFile-name:hover {
  text-decoration: underline;
}
.attachFile .del-btn {
  display: inline-block;
  vertical-align: middle;
  width: 14px;
  height: 14px;
  font-size: 0;
  background: url('/img/cmn/ico_del_btn.png') no-repeat center;
}

/*write Table*/
.writeTable {
  width: 100%;
}
.writeTable tr:first-child th,
.writeTable tr:first-child td {
  border-top: 1px solid #bdbdbd;
}
.writeTable th {
  padding: 18px 5px;
  font-weight: 500;
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  background-color: #f9f9f9;
  text-align: left;
  padding-left: 25px;
}
.writeTable td {
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  padding: 12px 5px;
  padding-left: 25px;
}

.writeTable .board-attachFile td {
  height: 80px;
}

.board-attachFile .formFile-btn {
  background-image: none;
  padding: 10px 20px;
  display: inline-block;
  vertical-align: middle;
}
.board-attachFile .attachFile {
  display: inline-block;
  vertical-align: middle;
  margin-right: 20px;
}

.writeTable .attachFile-name {
  background-image: none;
  color: #adadad;
}
.writeTable .attachFile-name:hover {
  text-decoration: none;
}

/*edit Table*/
.editTable {
  width: 100%;
}
.editTable tr:first-child th,
.editTable tr:first-child td {
  border-top: 1px solid #bdbdbd;
}
.editTable th {
  padding: 18px 5px;
  font-weight: 500;
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  background-color: #f9f9f9;
  text-align: left;
  padding-left: 25px;
}
.editTable td {
  font-size: 15px;
  border-bottom: 1px solid #dbdbdb;
  padding: 12px 5px;
  padding-left: 25px;
}

.editTable .board-attachFile td {
  height: 80px;
}

.editTable .formFile-btn {
  background-image: none;
  padding: 10px 20px;
  display: inline-block;
  vertical-align: middle;
}
.editTable .attachFile {
  display: inline-block;
  vertical-align: middle;
  margin-right: 20px;
}

.editTable .attachFile-name {
  background-image: none;
  color: #adadad;
  padding: 0;
}
.editTable .attachFile-name:hover {
  text-decoration: none;
}

/*종합현황판*/

.contents_wrap {
  position: relative;
  padding: 10px;
  margin: 35px 0;
}

.subWrap {
  height: auto;
}

.header {
  width: 100%;
  height: 85px;
  position: relative;
  padding: 0 2%;
  background: #3d3d3d;
}

.contents {
  width: 100%;
  height: auto;
  min-height: auto;
  position: relative;
}

.header .logo {
  position: absolute;
  top: 0;
  left: 30px;
  height: 100%;
}

.header .logo a {
  height: 100%;
  display: block;
  width: 100%;
}

.header .logo a img {
  position: absolute;
  top: 19px;
  left: 18px;
}

.header .gnb {
  position: absolute;
  top: 14px;
  left: 515px;
}

.header .gnb > ul {
  text-align: center;
  font-size: 0;
}

.header .gnb > ul > li {
  float: left;
  position: relative;
  height: 100%;
}

.header .gnb > ul > li:hover:after {
  content: '';
  position: absolute;
  bottom: 0px;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  height: 5px;
  background: #41b5b2;
}

.header .gnb > ul > li.on:after {
  content: '';
  position: absolute;
  bottom: 0px;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  height: 5px;
  background: #41b5b2;
  color: #41b5b2;
}

.header .gnb > ul > li > a {
  font-size: 20px;
  color: #000;
  font-weight: 500;
  padding: 0 100px;
  height: 100%;
  line-height: 70px;
}

.header .util {
  position: absolute;
  top: 35px;
  right: 35px;
}

.header .util ul li {
  float: left;
  position: relative;
}

.header .util > ul > li > a {
  font-size: 15px;
  font-weight: 400;
  text-transform: capitalize;
  padding: 4px 27px;
  background: url('/img/system/ico_login_id_white.png') no-repeat;
}

.header .util ul li a span {
  margin-left: 8px;
}

.header .util ul li .login {
  background: url('/img/system/ico_login_id.png') no-repeat;
}

.header .util ul li .data {
  background: #808080;
  padding: 11px 22px 9px;
  border-radius: 20px;
  color: #ffffff;
}

.util ul .user-menu ul {
  position: absolute;
  top: 44px;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  z-index: 1000;
  display: none;
  float: left;
  min-width: 160px;
  padding: 5px 0;
  margin: 2px 0 0;
  font-size: 14px;
  text-align: left;
  list-style: none;
  background-color: #fff;
  -webkit-background-clip: padding-box;
  background-clip: padding-box;
  border: 1px solid rgba(0, 0, 0, 0.15);
  border-radius: 4px;
  -webkit-box-shadow: 0 6px 12px rgba(0, 0, 0, 0.175);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.175);
}

.open > .dropdown-menu {
  display: block;
}

#location {
  position: relative;
  z-index: 20;
  width: 100%;
  margin: 20px 0 5px;
  padding: 10px 0 0px;
  background: url(/common/tmpl/skinD/img/pattern.png) repeat-x left bottom;
  text-align: left;
}

.modal-open #location {
  z-index: 10;
}

#location:after {
  display: block;
  clear: both;
  visibility: hidden;
  content: '';
}

#location .page__title {
  float: left;
  line-height: 1.3;
  margin: 10px 0 0;
  font-size: 24px;
  font-weight: 700;
  font-family: 'Gmarket Sans';
  color: #3e434a;
}

#location .utile_wrap {
  display: block;
  float: right;
  margin: 10px 0 0 20px;
}

#location .utile_wrap:after {
  display: block;
  clear: both;
  visibility: hidden;
  content: '';
}

#location .utile_wrap .print_wrap {
  display: inline-block;
  position: relative;
  top: -3px;
}

#location .utile_wrap .print_wrap:before {
  display: inline-block;
  float: left;
  content: '';
  width: 1px;
  height: 12px;
  margin: 5px 10px 0;
  background: #c0c0c0;
  vertical-align: middle;
}

#location .utile_wrap .print_wrap:after {
  display: block;
  clear: both;
  visibility: hidden;
  content: '';
}

#location .utile_wrap .print_wrap > li {
  float: left;
  margin-left: 7px;
}

#location .utile_wrap .print_wrap > li .tmpl_ir {
  background: url(/common/tmpl/skinD/img/ico_ir.png) no-repeat;
  font-size: 0;
}

#location .utile_wrap .print_wrap > li > button {
  display: block;
  width: 32px;
  height: 20px;
  transition: all 0.3s ease-out;
  -webkit-transition: all 0.3s ease-out;
}

#location .utile_wrap .print_wrap > li.sns {
  position: relative;
}

#location .utile_wrap .print_wrap > li.sns > button {
}

#location .utile_wrap .print_wrap > li.sns > button span {
  display: block;
  width: 17px;
  height: 100%;
  background-position: 0 -108px;
}

#location .utile_wrap .print_wrap > li.sns > div {
  display: none;
  position: absolute;
  right: 6px;
  top: -4px;
  width: 32px;
  text-align: right;
}

#location .utile_wrap .print_wrap > li.sns.on > div {
  display: block;
}

#location .utile_wrap .print_wrap > li.sns > div button {
  display: block;
  width: 32px;
  height: 32px;
  margin: 0 0 10px;
  border-radius: 50%;
}

#location .utile_wrap .print_wrap > li.sns > div .fa {
  background-position: 0 -174px;
}

#location .utile_wrap .print_wrap > li.sns > div .tw {
  background-position: 0 -214px;
}

#location .utile_wrap .print_wrap > li.sns > div .bl {
  background-position: -105px -215px;
}

#location .utile_wrap .print_wrap > li.sns > div .band {
  background-position: -35px -215px;
}

#location .utile_wrap .print_wrap > li.sns > div .ca {
  background-position: -70px -215px;
}

#location .utile_wrap .print_wrap > li.sns > div .kakao {
  background-position: -140px -215px;
}

#location .utile_wrap .print_wrap > li.sns > div .clickClose {
  width: 32px;
  height: 32px;
  margin: 0 0 10px;
  border-radius: 50%;
  background: url(/common/tmpl/skinD/img/ico_ir.png) no-repeat -21px 0;
}

#location .utile_wrap .print_wrap > li.sns > div .clickClose span {
  display: block;
  width: 100%;
  height: 100%;
  background-position: 0 -134px;
}

#location .utile_wrap .print_wrap > li.n2 > button span {
  display: block;
  width: 21px;
  height: 100%;
  background-position: -20px -108px;
}

#location .utile_wrap .print_wrap > li.n3 > button span {
  display: block;
  width: 19px;
  height: 100%;
  background-position: -44px -108px;
}

#location .utile_wrap .location_wrap {
  display: inline-block;
  position: relative;
}

#location .utile_wrap .location_wrap:after {
  display: block;
  clear: both;
  visibility: hidden;
  content: '';
}

#location .utile_wrap .location_wrap li {
  float: left;
  position: relative;
}

#location .utile_wrap .location_wrap li:not(:last-child):after {
  display: inline-block;
  content: '';
  margin: 10px 12px 0;
  border-top: 3px solid transparent;
  border-bottom: 3px solid transparent;
  border-left: 5px solid #c5c5c5;
  vertical-align: top;
}

#location .utile_wrap .location_wrap li a {
  display: inline-block;
  position: relative;
  line-height: 25px;
  vertical-align: top;
  font-size: 15px;
}

#location .utile_wrap .location_wrap li.home a {
  width: 16px;
  height: 17px;
  margin: 4px 0 0;
  background: url(/common/tmpl/skinD/img/ico_ir.png) no-repeat -21px 0;
  font-size: 0;
}

#location .utile_wrap .location_wrap li:last-child a {
  font-weight: bold;
}

.menuLeft.on {
  background: #303030;
  color: #fff;
}

.menuRight.on {
  display: block;
}

.menuAll {
  margin: 15px 0px;
  transition: all 0.3s ease 0s;
  padding: 22px 24px;
  background: #eef7f7;
  border-radius: 5px 5px 0 0;
  width: 59%;
}

.browser {
  margin: 15px 0px;
  transition: all 0.3s ease 0s;
  padding: 25px;
  background: #eef7f7;
  width: 39%;
  border-radius: 5px 5px 0 0;
}

.formhd {
  background: url(/img/sub/subtit.png) no-repeat left;
}
.formhd-tit {
  font-size: 18px;
  padding-left: 23px;
}

.dayAll {
  margin: 15px 0px;
  transition: all 0.3s ease 0s;
  padding: 22px 24px;
  background: #eef7f7;
  border-radius: 5px 5px 0 0;
  width: 32%;
}

.monthAll {
  margin: 15px 0px;
  transition: all 0.3s ease 0s;
  padding: 22px 24px;
  background: #eef7f7;
  border-radius: 5px 5px 0 0;
  width: 32%;
}

.userAll {
  margin: 15px 0px;
  transition: all 0.3s ease 0s;
  padding: 22px 24px;
  background: #eef7f7;
  border-radius: 5px 5px 0 0;
  width: 32%;
}

.statsAll {
  transition: all 0.3s ease 0s;
  padding: 22px 24px;
  background: #1b88e614;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 78px;
}

.toggleBar {
  display: flex;
  align-items: center;
  float: left;
}

.toggleSwitch {
  width: 52px;
  height: 28px;
  display: block;
  position: relative;
  border-radius: 30px;
  background-color: #fff;
  cursor: pointer;
  border: 1px solid #e2e2e2;
  margin-bottom: 3px;
  margin: auto;
}

.toggleSwitch .toggleButton {
  width: 20px;
  height: 20px;
  position: absolute;
  top: 50%;
  left: 2px;
  transform: translateY(-50%);
  border-radius: 50%;
  background: #7e7e7e;
}

.toggleBox {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 3px;
}

.toggleBox span {
  padding-left: 10px;
}

#recSt:checked ~ .toggleSwitch {
  background: #1c8ae8;
}

#recSt:checked ~ .toggleSwitch .toggleButton {
  left: calc(100% - 23px);
  background: #fff;
}

#toggle:checked ~ .toggleSwitch {
  background: #1c8ae8;
}

#toggle:checked ~ .toggleSwitch .toggleButton {
  left: calc(100% - 23px);
  background: #fff;
}

.toggleSwitch,
.toggleButton {
  transition: all 0.2s cubic-bezier(0.55, 0.06, 0.68, 0.19);
}

.inforAll {
  width: 49%;
  box-shadow: 0 0 20px 0px #00000026;
  margin: 15px;
}
.inforAll .inforArea {
  border: 1px solid #fff;
  padding: 20px;
  height: 245px;
  background: #fff;
}

.multi-wrapper {
  border: 1px solid #ccc;
  border-radius: 3px;
  overflow: hidden;
  width: 100%;
  margin: 15px 30px;
}

.multi-wrapper .non-selected-wrapper,
.multi-wrapper .selected-wrapper {
  box-sizing: border-box;
  display: inline-block;
  height: 200px;
  overflow-y: scroll;
  padding: 10px;
  vertical-align: top;
  width: 100%;
}

.multi-wrapper .non-selected-wrapper {
  background: #fafafa;
  border-right: 1px solid #ccc;
}

.multi-wrapper .selected-wrapper {
  background: #fff;
}

.multi-wrapper .item {
  cursor: pointer;
  display: block;
  padding: 5px 10px;
}

.multi-wrapper .item:hover {
  background: #ececec;
  border-radius: 2px;
}

.multi-wrapper .search-input {
  border: 0;
  border-bottom: 1px solid #ccc;
  border-radius: 0;
  display: block;
  font-size: 1em;
  margin: 0;
  outline: 0;
  padding: 10px 20px;
  width: 100%;
}

.multi-wrapper .non-selected-wrapper .item.selected {
  opacity: 0.5;
}

.container {
  box-sizing: border-box;
  margin: 0 auto;
  padding: 0 20px;
  width: 100%;
  background: transparent url(/img/database/switch.png) no-repeat 50% 50%;
  border-top: 2px solid #333333;
}

.multiSection {
  display: flex;
}

.modal div {
  z-index: 9999;
}

.modal {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background-color: rgba(0, 0, 0, 0.4);
}

.modal-none {
  opacity: 0;
  z-index: -999;
}
.modal-show {
  opacity: 1;
  z-index: 9998;
}

/* layout.css */
@charset "utf-8";
html,
body {
  width: 100%;
  height: auto;
}

.subWrap {
  height: auto;
}

.header {
  width: 100%;
  height: 72px;
  position: fixed;
  background: linear-gradient(270deg, #055ab5, #1d8be9);
  z-index: 3000;
}

.contents {
  width: 100%;
  height: auto;
  min-height: 100vh;
  position: relative;
}

.header .logo {
  position: absolute;
  top: 0;
  height: 100%;
}

.header .logo .logo-title {
  color: white;
  font-size: 20px;
}

.header .logo a {
  height: 100%;
  display: block;
  width: 100%;
  display: flex;
  align-items: center;
}

.header .logo a img {
  position: absolute;
  top: 22px;
  left: 15px;
}

.header .gnb {
  position: absolute;
  top: 0;
  left: 410px;
}

.header .gnb > ul {
  text-align: center;
  font-size: 0;
}

.header .gnb > ul > li {
  float: left;
  position: relative;
  height: 100%;
}

.header .gnb > ul > li:hover:after {
  content: '';
  position: absolute;
  bottom: 0px;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  height: 5px;
  background: #3d6ad6;
}

.header .gnb > ul > li.on:after {
  content: '';
  position: absolute;
  bottom: 0px;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  height: 5px;
  background: #3d6ad6;
}

.header .gnb > ul > li > a {
  font-size: 17px;
  color: #000;
  font-weight: 500;
  padding: 0 28px;
  height: 100%;
  line-height: 70px;
}

.header .util {
  position: absolute;
  top: 26px;
  right: 30px;
}

.header .util ul li {
  float: left;
  position: relative;
}

.header .util ul li:after {
  position: absolute;
  content: '';
  width: 1px;
  height: 15px;
  background: #ffffff;
  top: 0px;
  left: 1px;
}

.header .util ul li:nth-child(1):after {
  width: 0;
}

.header .util ul li a {
  font-size: 15px;
  font-weight: 400;
  color: #ffffff;
  text-transform: capitalize;
  padding: 0 15px;
}

.user-footer div a {
  line-height: 40px;
  border-color: white;
}

.sub-open-btn {
  display: none;
  position: absolute;
  top: 25px;
  right: 26px;
  width: 26px;
  height: 22px;
  background: url('/img/mobile/ico_m_btn.png') no-repeat left top;
  font-size: 0;
}

.lnb_depth2__list > li > a {
  position: relative;
  display: block;
  line-height: 1.2;
  padding: 16px 35px 13px 20px;
}

.lnb_depth2__list > li > a:before {
  display: block;
  position: absolute;
  z-index: 1;
  right: 18px;
  top: 22px;
  content: '';
  width: 6px;
  height: 6px;
  border: solid #585c62;
  border-width: 0 1px 1px 0;
}

.lnb_depth2__list > li > a:after {
  display: block;
  position: absolute;
  right: 10px;
  top: 15px;
  content: '';
  width: 19px;
  height: 19px;
  /* border: 1px solid #cacaca; */
  border-radius: 50%;
}

.lnb_depth2__list > li > a[target='_blank']:before {
  display: inline-block;
  content: '';
  right: 15px;
  width: 11px;
  height: 12px;
  border: none;
  background: url(/common/tmpl/skinD/img/target.png) no-repeat;
  vertical-align: middle;
  transform: none;
  -webkit-transform: none;
}

.lnb_depth2__list > li > a[target='_blank']:after {
  display: none;
}

.lnb_depth2__list > li > a > span {
  position: relative;
  display: inline-block;
  line-height: 1.2;
  word-wrap: break-word;
  word-break: keep-all;
  font-size: 17px;
  font-weight: 500;
  color: #808080;
}

.lnb_depth2__list > li.on {
  padding: 0;
  margin-bottom: -2px;
  border-top: 0px;
  border-bottom: 0px;
}

.lnb_depth2__list > li.on > a {
}

.lnb_depth2__list > li.on > a:before {
  border-color: #fff;
  border-width: 0px 1px 1px 0;
}

.lnb_depth2__list > li.on > a > span {
  color: #fff;
  font-weight: 500;
}

.lnb_depth2__list .lnb_depth3__list {
  display: none;
  overflow: hidden;
  height: 0;
  margin: 0 10px 13px;
  text-align: left;
  transition: all 0.4s;
  -webkit-transition: all 0.4s;
  background: #152338;
}

.lnb_depth2__list .lnb_depth3__list > li {
  position: relative;
}

.lnb_depth2__list .lnb_depth3__list > li:before {
  background: url(/img/cmn/lnb3.png) no-repeat top 0px left 0px !important;
}

.lnb_depth2__list .lnb_depth3__list > li:last-child {
  background: none;
}

.lnb_depth2__list .lnb_depth3__list > li.on {
  background: #025bb466;
}

.lnb_depth2__list .lnb_depth3__list > li > a {
  display: block;
  position: relative;
  z-index: 2;
}

.lnb_depth2__list .lnb_depth3__list > li > a:after {
  display: block;
  position: absolute;
  right: 18px;
  top: 50%;
  width: 5px;
  height: 5px;
  margin-top: -3px;
  border: solid #cecfd1;
  border-width: 1px 1px 0 0;
  transform: rotate(45deg);
  -webkit-transform: rotate(45deg);
}

.lnb_depth2__list .lnb_depth3__list > li.on > a:after {
}

.lnb_depth2__list .lnb_depth3__list > li > a[target='_blank']:after {
  right: 5px;
  width: 11px;
  height: 12px;
  margin-top: -6px;
  border: none;
  background: url(/common/tmpl/skinD/img/target.png) no-repeat;
  transform: none;
  -webkit-transform: none;
}

.lnb_depth2__list .lnb_depth3__list > li > span {
  display: inline-block;
  position: relative;
  font-size: 16px;
  color: #3e434a;
}

.lnb_depth2__list .lnb_depth3__list > li.on > a span {
  font-weight: 500;
  color: #2c7b88;
}

.lnb_depth2__list > li > a:hover .lnb_depth3__list,
.lnb_depth2__list > li > a:focus .lnb_depth3__list,
.lnb_depth2__list > li.on .lnb_depth3__list,
.lnb_depth2__list > li.ov .lnb_depth3__list,
.lnb_depth3__list.on,
.lnb_depth3__list.open {
  display: block !important;
  visibility: visible;
  height: auto;
  margin-top: 20px;
}

.lnb_depth2__list > li.on .lnb_depth3__list {
  margin: 0px;
}

.lnb {
  width: 290px;
  background: #1d3150;
  height: 100%;
  position: fixed;
  top: 60px;
  padding-top: 30px;
  z-index: 2000;

  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding-bottom: 100px;
}

.lnb ul li {
  position: relative;
  transition: 0.2s;
}

.lnb > ul > ul > li:not(.on) > a:hover {
  background: #04101c;
}

.lnb ul li a {
  position: relative;
}

.lnb > ul > ul > li.on,
.lnb > ul > ul > li:has(li.on) > a,
.lnb > ul > ul > li:has(li.on) > a:hover {
  background: #045bb4;
}

.treeview-menu > li:not(.on) {
  background: #04101c;
}

.treeview > ul {
  background: #025bb466;
}

.lnb ul li:before {
  position: absolute;
  content: '';
  top: 16px;
  left: 27px;
}

.lnb ul li a:before {
  position: absolute;
  content: '';
  top: 18px;
  left: 59px;
  background: #ffffff;
  width: 1px;
  height: 17px;
}

.lnb ul li.treeview:after {
  position: absolute;
  content: '';
  width: 5px;
  height: 10px;
  background: url('/img/cmn/ico_lnb_arrow.png') no-repeat center;
  top: 23px;
  right: 25px;
}

.lnb ul li.treeview.active:after {
  transform: rotate(90deg);
}

.lnb ul li:nth-child(1):before {
  width: 20px;
  height: 20px;
  background: url('/img/cmn/ico_lnb1.png');
  z-index: 99;
}

.lnb ul li:nth-child(2):before {
  width: 20px;
  height: 20px;
  background: url('/img/cmn/ico_lnb2.png');
  z-index: 99;
}

.lnb ul li:nth-child(3):before {
  width: 20px;
  height: 20px;
  background: url('/img/cmn/ico_lnb3.png');
  z-index: 99;
}

.lnb ul li:nth-child(4):before {
  width: 20px;
  height: 20px;
  background: url('/img/cmn/ico_lnb4.png');
  z-index: 99;
}

.lnb ul li:nth-child(5):before {
  width: 20px;
  height: 20px;
  background: url('/img/cmn/ico_lnb5.png');
  left: 26px;
  z-index: 99;
}

.lnb ul li:nth-child(6):before {
  width: 17px;
  height: 18px;
  background: url('/img/cmn/ico_lnb6.png');
  z-index: 99;
}

.lnb ul li:nth-child(7):before {
  width: 18px;
  height: 20px;
  background: url('/img/cmn/ico_lnb7.png');
  z-index: 99;
}

.lnb ul li:nth-child(8):before {
  width: 19px;
  height: 17px;
  background: url('/img/cmn/ico_lnb8.png');
  z-index: 99;
}

.lnb ul li:nth-child(9):before {
  width: 18px;
  height: 18px;
  background: url('/img/cmn/ico_lnb9.png');
  z-index: 99;
}

.lnb ul li:nth-child(10):before {
  width: 18px;
  height: 18px;
  background: url('/img/cmn/ico_lnb10.png');
  z-index: 99;
}

.lnb ul li:nth-child(11):before {
  width: 18px;
  height: 18px;
  background: url('/img/cmn/ico_lnb11.png');
  z-index: 99;
}

.lnb ul li a {
  color: #fff;
  font-weight: 400;
  padding: 19px 0;
  display: block;
  padding-left: 75px;
}

.contentsBody {
  float: left;
  width: 100%;
  min-height: 100%;
  padding: 115px 60px 30px 350px;
  position: relative;
}

.footer {
  border-top: 1px solid #dfdfdf;
  clear: left;
  background: #ffffff;
  position: relative;
  z-index: 5000;
}

.footer .footerLink {
  padding: 0 2%;
  border-bottom: 1px solid #dfdfdf;
}

.footer .footerLink ul li {
  float: left;
  position: relative;
}

.footer .footerLink ul li:after {
  position: absolute;
  content: '';
  top: 19px;
  left: 0;
  width: 1px;
  height: 12px;
  background: #bdbdbd;
}

.footer .footerLink ul li:first-child:after {
  width: 0;
}

.footer .footerLink ul li a {
  line-height: 50px;
  display: block;
  color: #000;
  padding: 0 15px;
}

.footer .footerInfo {
  padding: 0 2%;
  padding-top: 30px;
  padding-bottom: 30px;
}

.footer .footerInfo img {
  float: left;
  margin-right: 50px;
  margin-top: 22px;
}

.footer .footerInfo .footerTxt {
  float: left;
  font-weight: 300;
  line-height: 1.5;
}

.footer .footerInfo .selectSiteMap {
  float: right;
}

.m-menu-bg {
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  position: fixed;
  top: 0px;
  right: 0px;
  z-index: 3000;
  display: none;
  bottom: 0px;
  left: 0px;
}

.m-menu-con {
  position: fixed;
  top: 0px;
  bottom: 0;
  right: -100%;
  width: 80%;
  max-width: 460px;
  height: 100vh;
  background: rgb(255, 255, 255);
  z-index: 3300;
  display: none;
}

.m-menu-con .m-header-top {
  background: linear-gradient(44deg, #208be9, #055cb5);
  padding: 70px 20px 20px;
  position: relative;
}

.m-menu-con .m-header-top p {
  color: rgb(255, 255, 255);
  font-size: 20px;
  line-height: 27px;
  font-weight: 500;
}

.m-menu-con .m-header-top strong {
  display: block;
}

.m-menu-con .m-header-top .sub-open-close {
  width: 31px;
  height: 31px;
  position: absolute;
  top: 20px;
  right: 20px;
  border-radius: 50px;
  background: url('/img/mobile/ico_close_btn.png') left top no-repeat;
}

.m-menu-con .m-header-top .m-sub-btn {
  margin-top: 20px;
}

.m-menu-con .m-header-top .m-sub-btn a {
  color: rgb(255, 255, 255);
  display: inline-block;
  border: 1px solid rgb(255, 255, 255);
  padding: 7px 20px 4px 20px;
}

.m-menu-con .m-header-top .m-sub-btn .btn-login,
.m-menu-sub-btn {
  margin-right: 8px;
}

.m-menu-con .area-tit {
  width: 100%;
  height: 60px;
  display: block;
  background: rgb(32, 36, 41);
  color: rgb(255, 255, 255);
  text-align: center;
  line-height: 60px;
  font-size: 17px;
}

.m-menu-con .m-menu-area {
  background: rgb(43, 48, 54);
  height: 100%;
  position: relative;
}

.m-menu-area > ul {
  overflow: auto;
  height: 100%;
  background: #e5e5e5;
}

.m-menu-area > ul > li > a {
  background: #e5e5e5;
  display: block;
  width: 150px;
  padding: 17px 15px;
  color: #505050;
  font-weight: 500;
}

.m-menu-area .menuRight {
  position: absolute;
  top: 0;
  left: 150px;
  width: calc(100% - 150px);
  background: #fff;
  height: 100vh;
  padding-top: 12px;
  display: none;
}

.m-menu-area .menuRight > ul > li > a {
  color: #141414;
  font-weight: 500;
  font-size: 15px;
  padding: 14px 30px;
  padding-left: 24px;
  display: block;
  word-break: keep-all;
}

.menuLeft.on {
  background: #303030;
  color: #fff;
}

.menuRight.on {
  display: block;
}

.sidebar-menu {
  list-style: none;
  margin: 0;
  padding: 0;
}
.sidebar-menu > li {
  position: relative;
  margin: 0;
  padding: 0;
}
.sidebar-menu > li > a {
  padding: 12px 5px 12px 15px;
  display: block;
}
.sidebar-menu > li > a > .fa,
.sidebar-menu > li > a > .glyphicon,
.sidebar-menu > li > a > .ion {
  width: 20px;
}
.sidebar-menu > li .label,
.sidebar-menu > li .badge {
  margin-top: 3px;
  margin-right: 5px;
}
.sidebar-menu li.header {
  padding: 10px 25px 10px 15px;
  font-size: 12px;
}
.sidebar-menu li > a > .fa-angle-left {
  width: auto;
  height: auto;
  padding: 0;
  margin-right: 10px;
  margin-top: 3px;
}
.sidebar-menu li.active > a > .fa-angle-left {
  -webkit-transform: rotate(-90deg);
  -ms-transform: rotate(-90deg);
  -o-transform: rotate(-90deg);
  transform: rotate(-90deg);
}
.sidebar-menu li.active > .treeview-menu {
  display: block;
  padding: 0;
}
.sidebar-menu .treeview-menu {
  display: none;
  list-style: none;
  padding: 0;
  margin: 0;
}
.sidebar-menu .treeview-menu .treeview-menu {
  padding-left: 20px;
}
.sidebar-menu .treeview-menu > li {
  margin: 0;
  /* background:url(../images/btn_over.png) 5px 9px no-repeat */
}
/*.sidebar-menu .treeview-menu > li > a {
  padding: 0 3px 5px 18px;
  display: block;
  font-size: 14px;
  background-color: #2c3038;
  margin-top: 5px;
  height: 30px;
}*/
.sidebar-menu .treeview-menu > li > a > .fa,
.sidebar-menu .treeview-menu > li > a > .glyphicon,
.sidebar-menu .treeview-menu > li > a > .ion {
  width: 20px;
}
.sidebar-menu .treeview-menu > li > a > .fa-angle-left,
.sidebar-menu .treeview-menu > li > a > .fa-angle-down {
  width: auto;
}

/* 드롭다운 컨테이너 */
.dropdown-menu {
  position: absolute;
  right: 10px;
  top: 50px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 10px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  min-width: 160px;
  z-index: 1000;
  padding: 12px 0;
  font-size: 14px;
}

/* 내부 항목 공통 */
.dropdown-menu .btn-menu-h {
  display: block;
  width: 100%;
  padding: 12px 16px;
  text-align: left;
  background-color: transparent;
  border: none;
  color: #333;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.25s ease, color 0.25s ease;
}

/* hover 효과 */
.dropdown-menu .btn-menu-h:hover {
  background-color: #f4f6f8;
  color: #007bff;
}

/* 마지막 간격 제거 */
.dropdown-menu .user-footer > div:last-child {
  margin-bottom: 0;
}
</style>
