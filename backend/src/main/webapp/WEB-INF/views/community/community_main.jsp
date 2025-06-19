<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">

    <title>지도 커뮤니티</title>

    <link rel="stylesheet" href="/css/fullcalendar.min.css">

    <link rel="stylesheet" href="/js/jquery-ui-1.12.1.custom/jquery-ui.min.css" type="text/css">
    <link rel="stylesheet" href="/css/common.css" type="text/css">
    <link rel="stylesheet" href="/css/component.css" type="text/css">
    <link rel="stylesheet" href="/css/layout.css" type="text/css">
    <link rel="stylesheet" href="/css/modeling.css" type="text/css">
    <link rel="stylesheet" href="/css/sub.css" type="text/css">
    <link rel="stylesheet" href="/css/responsive.css" type="text/css">
    <script src="/js/jquery-1.12.4.min.js"></script>
    <script src="/js/jquery-ui-1.12.1.custom/jquery-ui.min.js"></script>
    <script src="/js/cmn_script.js"></script>

    <script src="/js/jquery/jQuery-2.1.4.min.js"></script>
    <script src="https://code.jquery.com/ui/1.11.4/jquery-ui.min.js"></script>
    <script src="/js/jquery/jquery-ui.js"></script>

    <%-- <link rel="stylesheet" href="/css/_all-skins.min.css">--%>
    <link rel="stylesheet" href="/css/cms.css">
    <link rel="stylesheet" href="/css/pm.css">
    <link rel="stylesheet" href="/css/board.css">
    <link rel="stylesheet" href="/css/selectStyle.css">

    <link rel="stylesheet" href="/css/bootstrap.css">
    <link rel="stylesheet" href="/css/plugins/datatables/dataTables.bootstrap.css">
    <link rel="stylesheet" href="/css/plugins/bootstrapSelect/bootstrap-select.min.css">
    <link rel="stylesheet" href="/css/plugins/datepicker/bootstrap-datepicker3.css">
    <link rel="stylesheet" href="/css/plugins/datepicker/bootstrap-datetimepicker.css"> <!-- css 추가 -->
    <link rel="stylesheet" href="/css/plugins/jstree/style.min.css">
    <link rel="stylesheet" href="/css/plugins/lightbox/lightbox.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">
    <link href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/fullcalendar/lib/cupertino/jquery-ui.min.css"/>
    <link rel="stylesheet" href="/fullcalendar/fullcalendar.css"/>
    <link rel="stylesheet" media="print" href="/fullcalendar/fullcalendar.print.css"/>
    <link rel="stylesheet" type="text/css" href="/css/plugins/morris/morris.css"/>
    <link rel="stylesheet" type="text/css" href="/css/plugins/d3/nv.d3.css"/>
    <link rel="stylesheet" type="text/css" href="/css/plugins/d3/c3.css"/>
    <link rel="stylesheet" type="text/css" href="/css/plugins/d3/c3.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/plugins/d3/nv.d3.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/plugins/datepicker/jqueryDatepickerCustom.css"/>

    <!--[if IE 7]>
    <link rel="stylesheet" href="../css/font-awesome-ie7.min.css">
    <![endif]-->
    <!--[if IE 9]>
    <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <script src="/js/common/bootstrap.js"></script>

</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="subWrap">
    <input type="hidden">
    <div class="wrapper">
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
        <header class="header">
            <h1 class="logo">
                <a href="/communities/main"><span class="logo-title">지도 커뮤니티</span></a>
            </h1>
            <div class="util">
                <ul class="nav navbar-nav">
                    <li class="dropdown user user-menu"> <a href="#" class="dropdown-toggle" data-toggle="dropdown"> <span class="hidden-xs">${user.name}&nbsp님 </span> </a>
                        <ul class="dropdown-menu">
                            <li class="user-footer">
                                <div style="padding-bottom: 10px;"> <a href="/system/mypage/list.mng" class="btn btn-default btn-flat" style="width: 100%;">마이페이지</a> </div>
                                <div> <a href="/logout.mng" class="btn btn-default btn-flat" style="width: 100%;">로그아웃</a> </div>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
            <button class="sub-open-btn">모바일 메뉴 열기</button>
            <div class="m-menu-bg"></div>
            <!-- 모바일 메뉴 시작 -->
            <div class="m-menu-con">
                <div class="m-header-top">
                    <p>제주스마트시티<strong>데이터허브 서비스</strong></p>
                    <button type="button" class="sub-open-close"><span class="skip">메뉴닫기</span></button>
                    <div class="m-sub-btn">
                        <a href="/system/mypage/list.mng" class="m-menu-sub-btn">마이페이지</a>
                        <a href="/logout.mng" class="m-menu-sub-btn">로그아웃</a>
                    </div>
                </div>
            </div>
        </header>
        <form id="excelDownForm" style="display: none;">
            <input type="hidden" id="excelFileName" name="excelFileName">
            <input type="hidden" id="excelFileDownName" name="excelFileDownName">
        </form>
        <section class="contents clear-fix">
            <aside class="lnb">
                <ul class="depth2_ul lnb_depth2__list sidebar">
                    <ul class="sidebar-menu">
                        <li class="on">
                            <a href="/">
                                <span>커뮤니티지도</span>
                            </a>
                        </li>
                        <li>
                            <a href="/boards/list/${communityId}">
                                <span>게시판</span>
                            </a>
                        </li>
                    </ul>
                </ul>
            </aside>
            <!-- openlayers -->
            <link rel="stylesheet" href="/js/gis/ol.css">
            <link rel="stylesheet" href="/css/gis.css">
            <link rel="stylesheet" href="/css/community_main.css">
            <script src="/js/gis/ol.js"></script>
            <script src="/js/gis/proj4.js"></script>
            <script src="/js/pm/gis.js"></script>

            <!-- 레이어 슬라이드-->
            <div class="layer_modal_wrap">
                <div class="layer_modal_con">
                    <strong class="modalTit">커뮤니티지도</strong>
                    <div class="layer-content">
                        <ul class="layer_btn_change" id="pills-tab" role="tablist">
                            <li class="nav-item custom-nav active nav-link" id="pills-profile-tab" data-toggle="pill" href="#pills-profile" role="tab" aria-controls="pills-profile" aria-selected="true">위치등록
                            </li>
                            <li class="nav-item custom-nav nav-link" id="pills-home-tab" data-toggle="pill" href="#pills-home" role="tab" aria-controls="pills-home" aria-selected="false">검색
                            </li>
                        </ul>
                        <div id="pills-home" class="fade tab-pane layer_Zone pr20" aria-labelledby="pills-home-tab">
                            <div class="formItem">
                                <label for="searchAreaType" class="form-tit mr20 floatLt" style="line-height: 35px; margin-bottom: 0px">검색 종류</label>
                                <select name="searchAreaType" id="searchAreaType" class="select-sm floatRt">
                                    <option value="all">전체</option>
                                    <option value="0">명칭</option>
                                    <option value="1">태그</option>
                                </select>
                            </div>

                            <div class="search-area-block">
                                검색 : <input type="text" placeholder="검색어를 입력하세요." />
                            </div>

                            <button class="cmnBtn search" style="min-width: 100%; margin-top: 10px; margin-bottom: 10px;" id="mapSearchButton">검색</button>
                            <div class="zoneInfo">
                                <div class="zoneInfo-zoneList">
                                    <div class="zoneInfo-zoneList-total"><p>전체 건수</p>
                                        <p class="totalZoneCount">0</p>
                                        <p>건</p>
                                    </div>
                                    <img class="zoneInfo-zoneList-list-divisionLine" src="/images/cms/common/division_line.png" alt="division_line">
                                    <div class="zoneInfo-zoneList-list">
                                        <div id="zoneList">
                                        </div>
                                        <div class="noSafetyAreaData">조회결과가 존재하지 않습니다.</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div id="pills-profile" class="fade tab-pane" aria-labelledby="pills-profile-tab">
                            <div class="formItem">
                                <label for="registAreaType" class="form-tit mr20 floatLt" style="line-height: 35px; margin-bottom: 0px">등록 타입</label>
                                <select id="registAreaType" name="registAreaType" class="select-sm floatRt">
                                    <option value="">전체</option>
                                    <option value="0">직접 등록</option>
                                    <option value="1">검색 등록</option>
                                </select>
                            </div>
                            <div class="row red-pin-block" style="display: none; flex-direction: column;">
                                    이미지: <input type="file" name="imgFile" accept="image/*" style="width: 100%;">
                                    이름: <input type="text" id="red-pin-name" />
                                    현재 위도: <span id="red-pin-lat"></span>
                                    현재 경도: <span id="red-pin-lon"></span>

                                    <div style="margin-bottom: 20px;">
                                        태그:
                                        <div class="hashtag-pin">
                                            <input type="text" name="hashtag-txt" placeholder="태그를 입력하세요" />
                                        </div>
                                    </div>

                                <button class="btn btn-primary w-100" id="red-pin-regist">등록</button>
                            </div>

                            <div class="row search-block" style="display: none;">
                                검색 : <input type="text" id="search-attraction" />

                                <button class="btn btn-primary w-100" id="search-search">검색하기</button>
                            </div>
                            <div class="row search-comp-block" style="display: none;">
                                이름: <input type="text" id="search-name" />
                                현재 위도: <span id="search-lat"></span>
                                현재 경도: <span id="search-lon"></span>

                                <div style="margin-bottom: 20px;">
                                    태그:
                                    <div class="hashtag-search">
                                        <input type="text" name="hashtag-txt" placeholder="태그를 입력하세요" />
                                    </div>
                                </div>

                                <button class="btn btn-primary w-100" id="search-regist">등록</button>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="layer-navi"><div></div></div>
            </div>
            <div id="vMap" class="mapArea map" style="height: 100%; width: 100%;">
                <div id="mapModeButton">
                    <div id='2dButton' onclick="NamuLayer.controlLayerHandler('2d-map')"><img src="/images/dash/layer_tit.png"/> 2D</div>
                    <div id='satelliteButton' onclick="NamuLayer.controlLayerHandler('SatelliteMap')"><img src="/images/dash/layer_tit.png"/> 위성영상</div>
                    <div id='hybridButton' onclick="NamuLayer.controlLayerHandler('HybridMap')"><img src="/images/dash/layer_tit.png"/> 하이브리드</div>
                </div>
            </div>
            <div class="popup-wrapper"></div>
            <div id="legend">
                <div class="square-box">
                    <div class="square-box-box">
                        <div class="blue-square"></div>
                    </div>
                    <span>대여</span>
                </div>
                <div class="square-box">
                    <div class="square-box-box">
                        <div class="green-square"></div>
                    </div>
                    <span>반납</span>
                </div>
                <div class="square-box">
                    <div class="square-box-box">
                        <div class="red-square"></div>
                    </div>
                    <span>불법주차</span>
                </div>
            </div>
            <div class="gis-range-wrapper">
                <div style="display: flex; align-items: center;">
                    <div class="gis-control-wrapper">
                        <i class="material-icons gis-control" style="font-size: 50px !important;">pause_circle</i>
                    </div>
                    <div>
                        <div class="gis-range">
                            <input type="range" min="1" max="24" steps="1" value="1">
                        </div>
                        <ul class="range-labels">
                            <li class="active selected">0시</li>
                            <li>1시</li>
                            <li>2시</li>
                            <li>3시</li>
                            <li>4시</li>
                            <li>5시</li>
                            <li>6시</li>
                            <li>7시</li>
                            <li>8시</li>
                            <li>9시</li>
                            <li>10시</li>
                            <li>11시</li>
                            <li>12시</li>
                            <li>13시</li>
                            <li>14시</li>
                            <li>15시</li>
                            <li>16시</li>
                            <li>17시</li>
                            <li>18시</li>
                            <li>19시</li>
                            <li>20시</li>
                            <li>21시</li>
                            <li>22시</li>
                            <li>23시</li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="polygon1"></div>
            <div>
                <div class="polygonSample1"></div><div class="polygonSample2"></div><div class="polygonSample3"></div><div class="polygonSample4"></div>
                <div class="polygonSample5"></div><div class="polygonSample6"></div><div class="polygonSample7"></div><div class="polygonSample8"></div>
            </div>
            <div class="progress-body">
                <div class="progress-content"></div>
            </div>

        </section>

    </div>
</div>
</body>
</html>