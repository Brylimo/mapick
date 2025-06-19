document.write('<script src="/js/namu/NamuUtil.js"></script>');
document.write('<script src="/js/namu/NamuLayer.js"></script>');

var map; //맵 변수 선언 : 지도 객체
var view;
// var mapLayerBase; //맵 레이어 선언 : 지도 그림(타일) 설정
var aKey = "D5152B22-77A2-311E-B514-EFD1422CD4ED";
// var layers = {};

var twLayers = [];
var markerList = [];
var polygonList = [];

function overlayDTO(id, container, type, info) {
    this.id = id;
    this.container = container;
    this.type = type;
    this.info = info;
};

$(() => {
    view = new ol.View({ //뷰 생성
        projection: 'EPSG:3857', //좌표계 설정 (EPSG:3857은 구글에서 사용하는 좌표계)
        center: new ol.geom.Point([126.4989967, 33.4879725])  //처음 중앙에 보여질 경도, 위도
            .transform('EPSG:4326', 'EPSG:3857') //GPS 좌표계 -> 구글 좌표계
            .getCoordinates(), //포인트의 좌표를 리턴함
        zoom: 15.3, //초기지도 zoom의 정도값
        minZoom: 7,
        maxZoom: 20
    });

    map = new ol.Map({ //맵 생성
        target: 'vMap', //html 요소 id 값
        // layers : [mapLayerBase], //레이어
        view: view //뷰
    });

    NamuLayer.setMap(map);
    NamuLayer.executeRealTimeLocation()
    // 기본 layer
    NamuLayer.createVworldMapLayer('Base', 'base', true, 'png');
    NamuLayer.createVworldMapLayer('Satellite', 'satellite', false, 'jpeg');
    NamuLayer.createVworldMapLayer('Hybrid', 'hybrid', false, 'png');

    var acclyrEnName = "ACCIDENT";
    NamuLayer.createLayerCheckbox("PM 사고 정보", acclyrEnName);
    NamuLayer.createLayerImageCheckboxCustom('#pmLayerForm-box',"PM 사고 정보", acclyrEnName);

    var usagelyrEnName = "USAGE";
    NamuLayer.createLayerImageCheckboxCustom('#pmLayerForm-box', "PM 이용 정보", usagelyrEnName);

    //교통약자 보호구역
    NamuLayer.createLayerImageCheckbox('#twLayerForm-box', "어린이 보호구역" ,"childSafetyAreaLayer");
    NamuLayer.createLayerImageCheckbox('#twLayerForm-box', "보호구역내 CCTV" ,"cctvLayer");
    NamuLayer.createLayerImageCheckbox('#twLayerForm-box', "어린이 승/하차구역" ,"childPickupZoneLayer");
    NamuLayer.createLayerImageCheckbox('#twLayerForm-box', "어린이 통학로" ,"childWaySchoolLayer");
    NamuLayer.createLayerImageCheckbox('#twLayerForm-box', "보호구역내 횡단보도" ,"crossWalkLayer");
    NamuLayer.createLayerImageCheckbox('#twLayerForm-box', "보호구역내 교차로" ,"crossWayLayer");
    NamuLayer.createLayerImageCheckbox('#twLayerForm-box', "보호구역내 일방통행도로" ,"oneWayRoadLayer");
    NamuLayer.createLayerImageCheckbox('#twLayerForm-box', "보호구역내 과속방지턱" ,"speedBumpLayer");
    NamuLayer.createLayerImageCheckbox('#twLayerForm-box', "펜스" ,"fenceLayer");

    //도로 교통정보
    NamuLayer.createLayerImageCheckbox('#roadInformationLayerForm-box', "도로위험상황정보" ,"roadSafetyInfoLayer");
    NamuLayer.createLayerImageCheckbox('#roadInformationLayerForm-box', "도로 통제정보" ,"roadControlInfoLayer");
    NamuLayer.createLayerImageCheckbox('#roadInformationLayerForm-box', "도로 작업구간 정보" ,"roadWorkZoneInfoLayer");

    compareLocationInformation();

    NamuLayer.createLayerImageCheckboxCustom('#mapForm-box', "위성영상" ,"SatelliteMap");
    NamuLayer.createLayerImageCheckboxCustom('#mapForm-box', "하이브리드" ,"HybridMap");

    NamuLayer.createLayerImageCheckboxCustom('#pmLayerForm-box', "시간대별 이용 현황", "TIMEBASEDUSAGE");

    NamuLayer.createLayerImageCheckboxCustom('#pmLayerForm-box', "모드락허브" ,"MODLOCKHUB", 'modlockhubMark');
    NamuLayer.createLayerImageCheckboxCustom('#pmLayerForm-box', "소방용수" ,"FIREWATER", 'firewaterMark');
    NamuLayer.createLayerImageCheckboxCustom('#pmLayerForm-box', "버스정류장" ,"STATION", 'stationMark');
    NamuLayer.createLayerImageCheckboxCustom('#pmLayerForm-box', "스마트허브" ,"SMARTHUB", 'smarthubMark');

    NamuLayer.createLayerImageCheckboxCustom('#proneLayerForm-box', "보행자사고 사고다발지역" ,"WALKERACC", 'walkerAccMark');
    NamuLayer.createLayerImageCheckboxCustom('#proneLayerForm-box', "이륜차 사고다발지역" ,"TWOWHEELERACC", 'twowheelerAccMark');
    NamuLayer.createLayerImageCheckboxCustom('#proneLayerForm-box', "세부링크 도로위험지수" ,"ROADIDXACC", 'roadIdxAccMark');
    NamuLayer.createLayerImageCheckboxCustom('#proneLayerForm-box', "링크기반 사고위협지역" ,"LINKACC", 'linkAccMark');
    NamuLayer.createLayerImageCheckboxCustom('#proneLayerForm-box', "사망교통사고 위치정보" ,"DEATHACC", 'deathAccMark');
    NamuLayer.createLayerImageCheckboxCustom('#proneLayerForm-box', "자전거사고 다발지역" ,"BICYCLEACC", 'bicycleAccMark');
    NamuLayer.createLayerImageCheckboxCustom('#proneLayerForm-box', "보행노인사고 다발지역" ,"ELDERACC", 'elderAccMark');
    NamuLayer.createLayerImageCheckboxCustom('#proneLayerForm-box', "보행어린이사고 다발지역" ,"CHILDRENACC", 'childrenAccMark');
    NamuLayer.createLayerImageCheckboxCustom('#proneLayerForm-box', "스쿨존내어린이 사고다발지역" ,"SCHOOLZONEACC", 'schoolzoneAccMark');
    NamuLayer.createLayerImageCheckboxCustom('#proneLayerForm-box', "보행자무단횡단 사고다발지역" ,"JAYWALKACC", 'jaywalkAccMark');

    map.on('singleclick', NamuLayer.mapClickHandler);

    $(".ol-zoom").hide();

    proj4.defs("EPSG:5186", "+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=600000 +ellps=GRS80 +units=m +no_defs");
    ol.proj.proj4.register(proj4);

    //레이어 탭 활성화
    $('#pills-profile-tab').trigger('click');

    $('#registAreaType').on('change', function () {
        if (this.value === "0") { // 직접 등록
            NamuLayer.searchFlag = false;
            NamuLayer.manualFlag = true;
            $('.red-pin-block').show()
            $('.search-block').hide()
            $('.search-comp-block').hide()
        } else if (this.value === "1") { // 검색 등록
            NamuLayer.manualFlag = false;
            NamuLayer.searchFlag = true;
            $('.search-block').show()
            $('.red-pin-block').hide()
        }else {
            NamuLayer.manualFlag = false;
            NamuLayer.searchFlag = false;
            NamuLayer.removeLayer("layer_name", "pin_red_marker")
            $('.search-block').hide()
            $('.red-pin-block').hide()
        }
    })

    $('#red-pin-regist').on('click', function () {
        const path = window.location.pathname; // "/community-attractions/123"
        const pathSegments = path.split('/');  // ["", "community-attractions", "123"]
        const communityId = pathSegments[pathSegments.length - 1];

        const hashtags = [];

        document.querySelectorAll('div.hashtag-pin .hashtg').forEach(input => {
            let value = input.textContent.trim();
            if (value.startsWith('#')) {
                value = value.substring(1);
            }

            if (value) hashtags.push(value);
        });

        const name = $("#red-pin-name").val()
        if (!name) {
            alert("등록하려고 하는 장소의 이름을 작성해주세요!")
            return
        }

        const formData = new FormData();
        formData.append("imgFile", $('input[name="imgFile"]')[0].files[0]);
        formData.append("mapx", $("#red-pin-lat").text());
        formData.append("mapy", $("#red-pin-lon").text());
        formData.append("title", name);
        formData.append("communityid", communityId);
        hashtags.forEach(tag => formData.append("hashtagList", tag));

        // todo 커뮤니티랑 연결시키는 작업 먼저 하고 하기
        $.ajax({
            url: "/community-attractions",
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function (res) {
                var result = res.result;

                alert("등록이 완료되었습니다.")
                /*var locationList = [];
                for(var i = 0; i < result.length; i ++) {
                    if (result[i].location !== undefined && result[i].location !== '') {
                        var location = result[i].location.replace(",", " ").split(" ");
                        if (location.length > 0) {
                            locationList.push({x: location[0], y: location[1], text: result[i].detail + ''});
                        }
                    }
                }
                NamuLayer.createMarkerLayer(locationList, lyrEnName, false, imgSrc, makeTooltip);*/
            }, error: function(error) {
                if(error.status !== 500) alert('마커 데이터 조회에 실패하였습니다.\n해당 문제가 지속될 경우 관리자에게 문의하여 주십시오.');
                console.log(error.code);
            }
        });
    })

    $(".hashtag-pin input[name='hashtag-txt']").on("keyup", function(event){
        if (event.keyCode == 13) {
            const $hashTag = $("<div class='hashtg'></div>");
            $hashTag.text(`#${$(this).val()}`);

            $(".hashtag-pin input[name='hashtag-txt']").before($hashTag);
            $(this).val('');
        }
    });

    $(".hashtag-search input[name='hashtag-txt']").on("keyup", function(event){
        if (event.keyCode == 13) {
            const $hashTag = $("<div class='hashtg'></div>");
            $hashTag.text(`#${$(this).val()}`);

            $(".hashtag-search input[name='hashtag-txt']").before($hashTag);
            $(this).val('');
        }
    });

    $('#search-regist').on('click', function () {
        const path = window.location.pathname; // "/community-attractions/123"
        const pathSegments = path.split('/');  // ["", "community-attractions", "123"]
        const communityId = pathSegments[pathSegments.length - 1];

        const hashtags = [];

        document.querySelectorAll('div.hashtag-search .hashtg').forEach(input => {
            let value = input.textContent.trim();
            if (value.startsWith('#')) {
                value = value.substring(1);
            }

            if (value) hashtags.push(value);
        });

        const name = $("#search-name").val()
        if (!name) {
            alert("등록하려고 하는 장소의 이름을 작성해주세요!")
            return
        }

        const formData = new FormData();
        formData.append("imgFile", null);
        formData.append("mapx", $("#search-lat").text());
        formData.append("mapy", $("#search-lon").text());
        formData.append("title", name);
        formData.append("communityid", communityId);
        hashtags.forEach(tag => formData.append("hashtagList", tag));

        // todo 커뮤니티랑 연결시키는 작업 먼저 하고 하기
        $.ajax({
            url: "/community-attractions",
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function (res) {
                var result = res.result;

                alert("등록이 완료되었습니다.")
                /*var locationList = [];
                for(var i = 0; i < result.length; i ++) {
                    if (result[i].location !== undefined && result[i].location !== '') {
                        var location = result[i].location.replace(",", " ").split(" ");
                        if (location.length > 0) {
                            locationList.push({x: location[0], y: location[1], text: result[i].detail + ''});
                        }
                    }
                }
                NamuLayer.createMarkerLayer(locationList, lyrEnName, false, imgSrc, makeTooltip);*/
            }, error: function(error) {
                if(error.status !== 500) alert('마커 데이터 조회에 실패하였습니다.\n해당 문제가 지속될 경우 관리자에게 문의하여 주십시오.');
                console.log(error.code);
            }
        });
    })

    $('#searchAreaType').on('change', function () {
        const opt = $('#searchAreaType').val()

        if (opt === "0") {
            $('.search-area-block').show()
        }
    })

    $('#search-search').on('click', function () {
        const input = $('#search-attraction').val()

        $.ajax({
            url: `/attractions/keyword?keyword=${input}`,
            type: "GET",
            dataType: "json",
            success: function (res) {
                const dt = res.data

                const point = new ol.geom.Point(ol.proj.transform([dt.mapx * 1, dt.mapy * 1], 'EPSG:4326', 'EPSG:3857'))
                const feature = new ol.Feature({
                    geometry: point
                });

                const currentPointStyle = new ol.style.Style({
                    image: new ol.style.Icon(({
                        anchor: [10, 10],
                        anchorXUnits: 'pixels',
                        anchorYUnits: 'pixels',
                        src: '/images/marker/marker_red.png'
                    }))
                });
                feature.setStyle(currentPointStyle);

                const currentPointVSource = new ol.source.Vector({
                    features: [feature]
                });

                const currentPointLayer = new ol.layer.Vector({
                    source: currentPointVSource
                });

                currentPointLayer.set("marker", "view_marker");

                NamuLayer.map.getView().setCenter(point.getCoordinates());
                NamuLayer.map.getView().setZoom(parseInt(16));

                NamuLayer.removeLayer("marker", "view_marker");
                NamuLayer.map.addLayer(currentPointLayer);

                $('.search-comp-block').show()

                $('#search-name').val(dt.title)
                $('#search-lat').text(dt.mapx)
                $('#search-lon').text(dt.mapy)

            }, error: function (error) {

            }
        })
    })

    $('#pills-home-tab').on('click', function () {
        $('#zoneDetail-location').text('-');
        $('#zoneDetail-limitSpeed').text('-');
        $('#zoneDetail-limitSpeedChaneDate').text('-');

        $('.facility-table span').text('-');
        /*$.ajax({
            url: "/tv/common/safetyAreaInformation.json",
            type: "POST",
            data: {
                safetyAreaType : $("#safetyAreaType").val()
                , safetyAreaName : $("#safetyAreaName").val()
            },
            dataType: "json",
            beforeSend: function () { $('.progress-body').css('display','block') },
            success: function (res) {

                var listSize = 0;
                var areaList = [];
                var resultHtml = '';
                if(res.result.length > 0) {
                    var result = res.result;

                    for(var i = 0; i < result.length; i ++) {
                        var location = result[i].location.split(',').map(arg => parseFloat(arg));
                        areaList.push({x: location[0], y: location[1], address: (result[i].roadNmAddr + ', ' + result[i].schlNm), safetyZoneType: '어린이 보호구역' });
                        resultHtml += '<div class="zoneInfo-zoneList-list-item">';
                        resultHtml += '<input type="hidden" class="location" value="' + result[i].location + '"/>';
                        resultHtml += '<img class="zoneInfo-zoneList-list-item-disabled" src="/images/cms/common/disabled.png">';
                        resultHtml += '<div class="zoneInfo-zoneList-list-item-text">';
                        resultHtml += '<p class="title">' + result[i].schlNm + '</p>';
                        resultHtml += '<p class="address">' + result[i].roadNmAddr + '</p></div></div><div class="zoneInfo-zoneList-list-item-divisionLine"><img src="/images/cms/common/division_line_gray.png"></div>';
                    }
                    listSize = result.length;

                    $('.noSafetyAreaData').hide();
                } else {
                    $('.noSafetyAreaData').show();
                }
                $("#zoneList").html(resultHtml);

                listSize = listSize.toString();

                $(".totalZoneCount").text(listSize);
                if(typeof selectSafetyAreaLocation !== "undefined")
                    searchSafetyAreaCallback(areaList);
                else NamuLayer.map.removeLayer(NamuLayer.getLayer("searchLayer"));
            }, error: function(error) {
                alert('보호구역 조회에 실패하였습니다.\n해당 문제가 지속될 경우 관리자에게 문의하여 주십시오.');
                console.log(error);
            }
        }).done(function () {
            $('.progress-body').css('display','none');
            $("#list-modal").removeClass("modal-none");
            $("#list-modal").addClass("modal-show");
            $(".detailInfo_noData").text('보호구역을 선택해 주십시오.');
            $(".detailInfo_noData").css('display', 'block');
        });*/
    });

    var sheet = document.createElement('style'),
        $rangeInput = $('.gis-range input');

    document.body.appendChild(sheet);

    $rangeInput.on('input', function () {
        sheet.textContent = NamuLayer.getTrackStyle(this);
    });

    $('.range-labels li').on('click', function () {
       var index = $(this).index();

       $rangeInput.val(index + 1).trigger('input');
    });

    $('.gis-control').on("click", function () {
        if ($(this).text() === "pause_circle") {
            NamuLayer.clearRangeTimer();
            $(this).text("play_circle");
        } else if ($(this).text() === "play_circle") {
            NamuLayer.clearRangeTimer();
            NamuLayer.rangePlayer($rangeInput);
            $(this).text("pause_circle");
        }
    });

    $('#mapSearchButton').click(function(){
        const path = window.location.pathname; // "/community-attractions/123"
        const pathSegments = path.split('/');  // ["", "community-attractions", "123"]
        const communityId = pathSegments[pathSegments.length - 1];

        searchCommunityAttraction(communityId);
        NamuLayer.refreshMap([126.4989967, 33.4879725], 15.3);
    });

    $('#layer-navi').click(function(){
        if($('.layer_modal_wrap').hasClass('layer-navi-inactive')) $('.layer_modal_wrap').removeClass('layer-navi-inactive');
        else $('.layer_modal_wrap').addClass('layer-navi-inactive');
    });

    map.on("click", function (evt) {
        var pixel = evt.pixel

        if (NamuLayer.manualFlag) {  // manual location register
            NamuLayer.removeLayer("layer_name", "pin_red_marker")

            const coordinate = ol.proj.transform(map.getCoordinateFromPixel(pixel), 'EPSG:3857', 'EPSG:4326');

            const lon = coordinate[0];
            const lat = coordinate[1];

            // draw marker
            NamuLayer.pinRedMarker(lon, lat);

            $('#red-pin-lon').text(lon)
            $('#red-pin-lat').text(lat)
        }
    });

    map.on("pointermove", function (evt) {
        var pixel = map.getEventPixel(evt.originalEvent);
        var features = map.getFeaturesAtPixel(pixel);

        NamuLayer.removeTooltipAll("accPOI");
        NamuLayer.removeTooltipAll("polygon");

        features.forEach(function (feature) {
            var innerFeatures = feature.get("features");

            if (innerFeatures && innerFeatures.length > 0) {  // 사고 features 처리
                if (!map.getOverlayById(innerFeatures[0].get("id").replace(/[-:]+/g, ""))) {
                    var listArray = innerFeatures.map(function(feature) {
                        return feature.get("data");
                    });

                    var poiOverlayElement = createMouseOverOverlayElement(new overlayDTO(innerFeatures[0].get("id"), "tableListContainer", "accPOI", {listArray: listArray}));

                    if (poiOverlayElement !== null) {
                        var poiOverlayInfo = new ol.Overlay({
                            id: innerFeatures[0].get("id"),
                            element: poiOverlayElement,
                            offset: [10, 15],
                            position: ol.proj.transform([innerFeatures[0].get("acdnt_x") * 1, innerFeatures[0].get("acdnt_y") * 1], 'EPSG:4326', "EPSG:900913")
                        });

                        map.addOverlay(poiOverlayInfo);
                    }
                }
            }

            if (feature.get("type") === "polygon") {
                var acdntNocs = 0;
                var caltCnt = 0;

                if (feature.get("acdntNocs")) {
                    acdntNocs = feature.get("acdntNocs");
                }

                if (feature.get("caltCnt")) {
                    caltCnt = feature.get("caltCnt");
                }

                var listArray = [`사고: ${acdntNocs}건 / 사상자: ${caltCnt}건`];

                var polygonOverlayElement = createMouseOverOverlayElement(new overlayDTO(feature.get("id"), "listContainer", "polygon", {listArray: listArray}));

                if (polygonOverlayElement !== null) {
                    var polygonOverlayInfo = new ol.Overlay({
                        id: feature.get("id"),
                        element: polygonOverlayElement,
                        offset: [0, -70],
                        position: ol.proj.transform([feature.get("point").x * 1, feature.get("point").y * 1], 'EPSG:4326', "EPSG:900913")
                    });

                    map.addOverlay(polygonOverlayInfo);
                }

            }
        })
    });

    $(document).on("click", ".zoneInfo-zoneList-list-item", function() {
        var xy      = $($(this).children('.location')[0]).val().split(',');
        var text    = $($(this).children('.zoneInfo-zoneList-list-item-text')[0]).children('.title').text();
        var address = $($(this).children('.zoneInfo-zoneList-list-item-text')[0]).children('.address').text();
        NamuLayer.createMarkerLayer([{x: parseFloat(xy[0]), y: parseFloat(xy[1]), text: text, address: (address + ', ' + text), safetyZoneType: '어린이 보호구역'}], 'searchLayer', true, '/images/cms/common/disabled.png', false, [60,60]);
        NamuLayer.setCenter(xy);
    });

});

// 커뮤니티 내 attraction을 조회하는 함수
function searchCommunityAttraction(communityId) {
    const opt = $('#searchAreaType').val()

    if (opt === "all") { // 전체 검색
        $.ajax({
            url: `/community-attractions?communityid=${communityId}`,
            type: "GET",
            dataType: "json",
            success: function (res) {
                const list = res.data;

                var areaList = [];
                var resultHtml = '';
                if(list.length > 0) {
                    for(var i = 0; i < list.length; i++) {
                        var data = list[i].attraction

                        var location = [parseFloat(data.longitude), parseFloat(data.latitude)];
                        areaList.push({x: location[0], y: location[1], address: (data.addr1 + ', ' + data.addr2)});
                        resultHtml += '<div class="zoneInfo-zoneList-list-item">';
                        resultHtml += '<input type="hidden" class="location" value="' + location + '"/>';
                        resultHtml += `<img class="zoneInfo-zoneList-list-item-disabled" style="width: 100%;" src="${data.firstImage1}">`;
                        resultHtml += '<div class="zoneInfo-zoneList-list-item-text">';
                        resultHtml += '<p class="title">' + data.title + '</p>';
                        resultHtml += '<p class="address">' + data.addr1 + '</p></div></div><div class="zoneInfo-zoneList-list-item-divisionLine"><img src="/images/cms/common/division_line_gray.png"></div>';
                    }

                    $('.noSafetyAreaData').hide();
                } else {
                    $('.noSafetyAreaData').show();
                }
                $("#zoneList").html(resultHtml);

                $(".totalZoneCount").text(list.length);
                NamuLayer.viewMaker(list)
            }, error: function(error) {
                if(error.status !== 500) alert('마커 데이터 조회에 실패하였습니다.\n해당 문제가 지속될 경우 관리자에게 문의하여 주십시오.');
                console.log(error.code);
            }
        });
    } else if (opt === "0") { // 명칭 검색
        const searchTxt = $('.search-area-block').val()

        if (searchTxt) {
            $.ajax({
                url: `/community-attractions?communityid=${communityId}&query=${searchTxt}`,
                type: "GET",
                dataType: "json",
                success: function (res) {
                    const list = res.data;

                    var areaList = [];
                    var resultHtml = '';
                    if(list.length > 0) {
                        for(var i = 0; i < list.length; i++) {
                            var data = list[i].attraction

                            var location = [parseFloat(data.longitude), parseFloat(data.latitude)];
                            areaList.push({x: location[0], y: location[1], address: (data.addr1 + ', ' + data.addr2)});
                            resultHtml += '<div class="zoneInfo-zoneList-list-item">';
                            resultHtml += '<input type="hidden" class="location" value="' + location + '"/>';
                            resultHtml += `<img class="zoneInfo-zoneList-list-item-disabled" style="width: 100%;" src="${data.firstImage1}">`;
                            resultHtml += '<div class="zoneInfo-zoneList-list-item-text">';
                            resultHtml += '<p class="title">' + data.title + '</p>';
                            resultHtml += '<p class="address">' + data.addr1 + '</p></div></div><div class="zoneInfo-zoneList-list-item-divisionLine"><img src="/images/cms/common/division_line_gray.png"></div>';
                        }

                        $('.noSafetyAreaData').hide();
                    } else {
                        $('.noSafetyAreaData').show();
                    }
                    $("#zoneList").html(resultHtml);

                    $(".totalZoneCount").text(list.length);
                    NamuLayer.viewMaker(list)
                }, error: function(error) {
                    if(error.status !== 500) alert('마커 데이터 조회에 실패하였습니다.\n해당 문제가 지속될 경우 관리자에게 문의하여 주십시오.');
                    console.log(error.code);
                }
            });
        }

    } else if (opt === "1") { // 태그 검색

    }
}

// overlayElement를 생성해주는 함수
function createOverlayElement(overlayDTO) {

    if (typeof overlayDTO === 'object') {
        var overlayElement = document.createElement("div"); // 오버레이 팝업설정
        var tooltip = $('#tooltipFrame').html();
        var body = $('#' + overlayDTO.container).html();

        overlayElement.setAttribute("class", "overlayElement");
        /*overlayElement.setAttribute("style", "background-color: #3399CC; border: 2px solid white; color:white");*/
        overlayElement.setAttribute("style", "background-color: #ffffff; color:#2e3c50; border-radius: 7px; padding: 5px 10px 10px 10px;");
        overlayElement.setAttribute("data-uid", overlayDTO.id);
        overlayElement.setAttribute("data-type", overlayDTO.type);

        // tooltip html element 생성
        var tooltipElement = $(tooltip);

        // 이벤트 추가
        $(tooltipElement).find(".x-mark").click(function() {
            NamuLayer.deleteOverlay(overlayDTO.id.replace(/[-:]+/g, ""));
        })

        $(tooltipElement).find(".tooltip-content").append(body)

        $(overlayElement).append(tooltipElement)

        // 데이터 입력
        if (overlayDTO.info) {
            if (overlayDTO.container === "accTableContainer") {

                if (overlayDTO.info.listArray) {
                    $(overlayElement).find(".title").text(overlayDTO.info.listArray[0].title);
                }

                overlayDTO.info.listArray.forEach(function(element, index) {
                    var tr = $("<tr></tr>");
                    tr.append(`<td>${index + 1}</td>`);
                    if (element.acdnt_type !== null && element.acdnt_type !== "") {
                        tr.append(`<td>${element.acdnt_type}</td>`);
                    } else {
                        tr.append(`<td>-</td>`)
                    }
                    if (element.oper_co !== null && element.oper_co !== "") {
                        tr.append(`<td>${element.oper_co}</td>`);
                    } else {
                        tr.append(`<td>-</td>`)
                    }
                    if (element.pm_type !== null && element.pm_type !== "") {
                        tr.append(`<td>${element.pm_type}</td>`);
                    } else {
                        tr.append(`<td>-</td>`)
                    }
                    if (element.acdnt_ocrn_dt !== null && element.acdnt_ocrn_dt !== "") {
                        var date = new Date(element.acdnt_ocrn_dt);

                        var year = date.getFullYear();
                        var month = date.getMonth() + 1;
                        var day = date.getDate();

                        tr.append(`<td>${year}.${month.toString().padStart(2, '0')}.${day.toString().padStart(2, '0')}</td>`);
                    } else {
                        tr.append(`<td>-</td>`);
                    }
                    if (element.acdnt_ocrn_plc !== null && element.acdnt_ocrn_plc !== "") {
                        tr.append(`<td>${element.acdnt_ocrn_plc}</td>`);
                    } else {
                        tr.append(`<td>-</td>`)
                    }
                    if (element.acdnt_cn !== null && element.acdnt_cn !== "") {
                        tr.append(`<td>${element.acdnt_cn.length > 20 ? element.acdnt_cn.substring(0, 20) : element.acdnt_cn}...</td>`);
                    } else {
                        tr.append(`<td>-</td>`)
                    }

                    $(overlayElement).find(".accTableBody").append(tr);
                });
            } else if (overlayDTO.container === "listContainer") {
                overlayDTO.info.locArray.forEach(function(loc) {
                    $(overlayElement).find(".poiFrame").append("<div style='padding: 2px 3px; font-weight: bold; border-bottom: solid 1px #f7f8f9;'>" + loc + "</div>");
                });
                // $(overlayElement).find(".title").text(overlayDTO.info.title);
            }
        }

        return overlayElement;
    }

    return null;
}

// mouseOverOverlayElement를 생성해주는 함수
function createMouseOverOverlayElement(overlayDTO) {

    if (typeof overlayDTO === 'object') {
        var overlayElement = document.createElement("div"); // 오버레이 팝업설정
        var tooltip = $('#mouseOverTooltipFrame').html();
        var body = $('#' + overlayDTO.container).html();

        overlayElement.setAttribute("class", "overlayElement");
        /*overlayElement.setAttribute("style", "background-color: #3399CC; border: 2px solid white; color:white");*/
        if (overlayDTO.container === "tableListContainer") {
            overlayElement.setAttribute("style", "background-color: #ffffff; color:#2e3c50; border-radius: 7px;");
        } else {
            overlayElement.setAttribute("style", "background-color: #ffffff; color:#2e3c50; border-radius: 7px; padding: 1rem 2rem");
        }
        overlayElement.setAttribute("data-uid", overlayDTO.id);
        overlayElement.setAttribute("data-type", overlayDTO.type);

        // tooltip html element 생성
        var tooltipElement = $(tooltip);

        $(tooltipElement).find(".mouseOverTooltip-content").append(body)

        $(overlayElement).append(tooltipElement)

        // 데이터 입력
        if (overlayDTO.info) {
            if (overlayDTO.container === "tableListContainer") {
                overlayDTO.info.listArray.forEach(function(element, index) {
                    var acdntOcrnDt = element.acdnt_ocrn_dt

                    var tr = $("<tr></tr>");
                    tr.append(`<td>${index + 1}</td>`);
                    if (acdntOcrnDt !== null && acdntOcrnDt !== "") {
                        var date = new Date(acdntOcrnDt);

                        var year = date.getFullYear();
                        var month = date.getMonth() + 1;
                        var day = date.getDate();

                        tr.append(`<td>${year}.${month.toString().padStart(2, '0')}.${day.toString().padStart(2, '0')}</td>`);
                    } else {
                        tr.append(`<td>-</td>`);
                    }
                    if (element.pm_type !== null && element.pm_type !== "") {
                        tr.append(`<td>${element.pm_type}</td>`);
                    } else {
                        tr.append(`<td>-</td>`);
                    }
                    if (element.acdnt_type !== null && element.acdnt_type !== "") {
                        tr.append(`<td>${element.acdnt_type}</td>`);
                    } else {
                        tr.append(`<td>-</td>`);
                    }

                    $(overlayElement).find(".talbeListElement").append(tr);
                });
                // $(overlayElement).find(".title").text(overlayDTO.info.title);
            } else if (overlayDTO.container === "listContainer") {
                overlayDTO.info.listArray.forEach(function(element) {
                    $(overlayElement).find(".poiFrame").append("<div style='padding: 1px 1px; font-weight: bold;'>" + element + "</div>");
                });
            }
        }

        return overlayElement;
    }

    return null;
}

// 교통약자 보호구역 overlayElement를 생성해주는 함수
function createTwOverlayElement(overlayDTO) {

    if (typeof overlayDTO === 'object') {

        var overlayElement = document.createElement("div"); // 오버레이 팝업설정
        var tooltip = $('#tooltipFrame').html();
        var body = $('#' + overlayDTO.container).html();

        overlayElement.setAttribute("class", "overlayElement");
        overlayElement.setAttribute("style", "background-color: #ffffff; color:#2e3c50; border-radius: 7px; padding: 1rem 2rem");
        overlayElement.setAttribute("data-uid", overlayDTO.id);

        // tooltip html element 생성
        var tooltipElement = $(tooltip);

        // 이벤트 추가
        $(tooltipElement).find(".x-mark").click(function() {
            NamuLayer.deleteOverlay(overlayDTO.id);
        })

        $(tooltipElement).find(".tooltip-content").append(body)

        $(overlayElement).append(tooltipElement);

        //보호구역 tooltip
        if(overlayDTO.container === 'twContainer') {
            $(overlayElement).find(".title").text('보호구역/' + overlayDTO.info.safetyZoneType);
            $(overlayElement).find(".address").text(overlayDTO.info.address);
        } else {    //도로작업정보 tooltop
            $(overlayElement).find(".title").text('도로작업정보');
            $(overlayElement).find(".detail").text(overlayDTO.info.text);
        }

        return overlayElement;
    }

    return null;
}

function compareLocationInformation(){

    // 폴리곤 겹치는 지 체크
    for(var i = 0 ; i < polygonList.length - 1 ; i ++ ) {
        for(var j = i + 1; j < polygonList.length; j ++) {

            /** 겹치는 부분에 대한 정보가 intersection에 담깁니다. object 내부를 보면 점접, 선접, 면접 구분 가능합니다. */
            const intersection = turf.intersect(polygonList[i], polygonList[j]);
            if (intersection) {
                console.log('두 개의 폴리곤은 겹칩니다.');
                console.log(intersection);
            }
        }
    }

    // 폴리곤 겹치는 지 체크
    for(var i = 0 ; i < polygonList.length - 1 ; i ++ ) {
        for(var j = 0; j < markerList.length; j ++) {

            /** 겹치는 부분에 대한 정보가 intersection에 담깁니다. object 내부를 보면 점접, 선접, 면접 구분 가능합니다. */
            const intersection2 = turf.intersect(polygonList[i], markerList[j]);
            if (intersection2) {
                console.log('마커와 폴리곤 겹칩니다.')
                console.log(intersection2);
            }
        }
    }
}
//교통 약자 마커 데이터 조회 및 rendering
function getTwMarkerData(url, lyrEnName, imgSrc, makeTooltip){
    //CCTV
    $.ajax({
        url: url,
        type: "POST",
        data: {},
        dataType: "json",
        success: function (res) {
            var result = res.result;
            var locationList = [];
            for(var i = 0; i < result.length; i ++) {
                if (result[i].location !== undefined && result[i].location !== '') {
                    var location = result[i].location.replace(",", " ").split(" ");
                    if (location.length > 0) {
                        locationList.push({x: location[0], y: location[1], text: result[i].detail + ''});
                    }
                }
            }
            NamuLayer.createMarkerLayer(locationList, lyrEnName, false, imgSrc, makeTooltip);
        }, error: function(error) {
            if(error.status !== 500) alert('마커 데이터 조회에 실패하였습니다.\n해당 문제가 지속될 경우 관리자에게 문의하여 주십시오.');
            console.log(error.code);
        }
    });
}