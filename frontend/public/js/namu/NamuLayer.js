var NamuLayer = {
    map: null,
    timeBasedUsageResponseDto: null,
    illegalFlag: false,
    manualFlag: false,
    searchFlag: false,
    timers: [],
    isUsageLayerAlive: false,
    isAccidentLayerAlive: false,
    setMap: (map) => {
        NamuLayer.map = map;
    },
    pinCurrentPoint: (lon, lat) => {
        const feature = new ol.Feature({
            geometry: new ol.geom.Point(ol.proj.transform([lon, lat], 'EPSG:4326', "EPSG:900913"))
        });

        const currentPointStyle = new ol.style.Style({
            image: new ol.style.Circle(({
                radius: 3,
                stroke: new ol.style.Stroke({
                    color: "deeppink"
                }),
                fill: new ol.style.Fill({
                    color: "deeppink"
                })
            }))
        });
        feature.setStyle(currentPointStyle);

        const currentPointVSource = new ol.source.Vector({
            features: [feature]
        });

        const currentPointLayer = new ol.layer.Vector({
            source: currentPointVSource
        });

        currentPointLayer.set("layer_name", "current_point_marker");

        NamuLayer.map.addLayer(currentPointLayer);
    },
    pinRedMarker: (lon, lat) => {
        const feature = new ol.Feature({
            geometry: new ol.geom.Point(ol.proj.transform([lon, lat], 'EPSG:4326', "EPSG:900913"))
        });

        const iconStyle = new ol.style.Style({
            image: new ol.style.Icon(({
                anchor: [10, 10],
                anchorXUnits: 'pixels',
                anchorYUnits: 'pixels',
                src: '/images/marker/marker_red.png'
            }))
        });
        feature.setStyle(iconStyle);

        const markerRedSource = new ol.source.Vector({
            features: [feature]
        });

        const markerRedLayer = new ol.layer.Vector({
            source: markerRedSource
        });

        markerRedLayer.set("layer_name", "pin_red_marker");

        NamuLayer.map.addLayer(markerRedLayer);
    },
    executeRealTimeLocation: () => {
        if (!navigator.geolocation) {
            console.log("실시간 위치 정보가 지원되지 않습니다.")
        }
        navigator.geolocation.watchPosition(NamuLayer.watchPositionHandler)
    },
    watchPositionHandler: (rtData) => {
        const { coords } = rtData;

        NamuLayer.removeLayer("layer_name", "current_point_marker");
        NamuLayer.pinCurrentPoint(coords.longitude, coords.latitude);
    },
    createLayerCheckbox: (lyrKoName, lyrEnName, isLayerImg) => {
        console.dir(this);
        var html = '<div class="row" id="row' + lyrEnName + '"><label for="labelForId1"><input type="checkbox" class="layerChk" id="chk' + lyrEnName + '" value="' + lyrEnName + '" onchange="NamuLayer.layerCheckHandler(this.value)"/><span>' + lyrKoName + '</span></label></div>';
        if (isLayerImg) {
            var imgUrl = "https://gis.jeju.go.kr/arcgis/services/JJGWIMS/JJGWIMS/MapServer/WmsServer?request=GetLegendGraphic&version=1.3.0&format=image/png&layer=" + lyrEnName;
            html += '<div class="row" id="layerImg' + lyrEnName + '" style="display:none;"><label for="labelForId1"><img src="' + imgUrl + '" style="margin-left:40px;" alt="레이어이미지"></label></div>';
        }
        $(".layerForm-box").append(html);
    },
    createLayerImageCheckbox: (layerBoxName, lyrKoName, lyrEnName) => {
        var html ='<label for="chk'+lyrEnName+'"><input type="checkbox" class="layerChk" id="chk'+lyrEnName+'" value="'+lyrEnName+'" onchange="NamuLayer.layerCheckHandler(this.value)"/>';
        html+='<span>'+lyrKoName+'</span></label>';

        $(layerBoxName).append(html);
    },
    createLayerImageCheckboxCustom: (layerBoxName, lyrKoName, lyrEnName, layerImgClass) => {
        var html ='<div id="row' + lyrEnName + '"><label for="chk'+lyrEnName+'"><input type="checkbox" class="layerChk" id="chk'+lyrEnName+'" value="'+lyrEnName+'" onchange="NamuLayer.layerCheckHandler(this.value)"/>';
        if (layerImgClass !== undefined)
            html+='<span>'+lyrKoName+'</span></label></div>';
        else html+='<span style="margin-left: 5px;">'+lyrKoName+'</span></label></div>';

        $(layerBoxName).append(html);
    },
    createVworldMapLayer: (title, type, visible, imgType) => {
        console.dir(this);
        var layer = new ol.layer.Tile({ //타일 생성
            title: title + 'Map', //이름
            visible: visible, //보여짐 여부
            type: type, //지도 종류(일반) ---(야간(midnight), 위성(satellite) 등)
            source: new ol.source.XYZ({ //vworld api 사용
                url: 'http://api.vworld.kr/req/wmts/1.0.0/' + aKey + '/' + title + '/{z}/{y}/{x}.' + imgType
            })
        });
        NamuLayer.map.addLayer(layer);
        return layer;
    },
    createGisMapLayer: (lyrKoName, lyrEnName) => {
        console.dir(this);
        var layer = new ol.layer.Tile({
            title: lyrEnName, //이름
            source: new ol.source.TileWMS({
                url: "https://gis.jeju.go.kr/arcgis/services/JJGWIMS/JJGWIMS/MapServer/WMSServer?1",
                params: {
                    'VERSION': '1.1.0',
                    'LAYERS': lyrEnName,
                    'BBOX': [195386.296875, 451467.875, 202027.125, 458928.375],
                    'SRS': 'EPSG:5186',
                    'FORMAT': 'image/png'
                },
                serverType: 'mapserver'
            }),
            visible: false
        });

        if (lyrEnName == "UWL_WATERS_AS") {
            layer.setOpacity(0.3);
        }

        NamuLayer.map.addLayer(layer);
        NamuLayer.createLayerCheckbox(lyrKoName, lyrEnName, true);
        return layer;
    },
    createMarkerLayer: (list, lyrEnName, visible, markerImageSrc, makeTooltip, markerSize) => {

        //기존에 lyrEnName으로 그려진 layer 존재시 제거
        if(NamuLayer.getLayer(lyrEnName) !== undefined) {
            NamuLayer.map.removeLayer(NamuLayer.getLayer(lyrEnName));
        }

        var markerSource =  new ol.source.Vector({features: NamuLayer.makeMarkerSource(list, lyrEnName, visible, markerImageSrc, makeTooltip, markerSize)});

        // 마커 레이어 생성
        var markerLayer = new ol.layer.Vector({
            source: markerSource,
            title : lyrEnName,
            visible: visible
        });

        NamuLayer.map.addLayer(markerLayer);
        if(typeof twLayers !== 'undefined' && !twLayers.includes(lyrEnName)) twLayers.push(lyrEnName);
    },
    createPolygonLayer: (object, lyrEnName, visible, imgClass) => {
        var polygonSource =  new ol.source.Vector({features: NamuLayer.makePolygonSource(object, imgClass)});

        var polygonLayer = new ol.layer.Vector({
            source : polygonSource,
            title : lyrEnName,
            visible : visible
        });
        NamuLayer.map.addLayer(polygonLayer);
        if(typeof twLayers !== 'undefined' && !twLayers.includes(lyrEnName)) twLayers.push(lyrEnName);
    },
    createMultiLayer: (object, lyrEnName, visible, imgClass, markerImageSrc) => {
        var multiSource =  new ol.source.Vector({features: NamuLayer.makePolygonSource(object, imgClass)});

        var polygonLayer = new ol.layer.Vector({
            source : multiSource,
            title : lyrEnName,
            visible : visible
        });

        if(object.markerList.length > 0)
            polygonLayer.getSource().addFeatures(NamuLayer.makeMarkerSource(object.markerList, lyrEnName, visible, markerImageSrc));


        NamuLayer.map.addLayer(polygonLayer);
        if(typeof twLayers !== 'undefined' && !twLayers.includes(lyrEnName)) twLayers.push(lyrEnName);

    },
    //클릭한 레이어 정보 조회
    mapClickHandler: (evt) => {
        NamuLayer.map.forEachLayerAtPixel(evt.pixel, function (layer) {
            var layername = layer.getProperties().title;
            console.dir(layername);

            if (layername == "BaseMap")
                return;

            else if (twLayers.includes(layername))
                return;

            var viewResolution = (NamuLayer.map.getView().getResolution());

            var point = new ol.geom.Point(evt.coordinate);
            var bbox;

            bbox = new ol.extent.buffer(point.getExtent(), 12 * viewResolution / 2);

            var bbox2 = new ol.geom.Polygon.fromExtent(bbox);

            var query = bbox2.getCoordinates()[0][0].join(' ') + ' ' + bbox2.getCoordinates()[0][1].join(' ') + ' ' + bbox2.getCoordinates()[0][2].join(' ') + ' ' + bbox2.getCoordinates()[0][3].join(' ') + ' ' + bbox2.getCoordinates()[0][4].join(' ');

            var url = "https://gis.jeju.go.kr/arcgis/services/JJGWIMS/JJGWIMS/MapServer/WFSServer?VERSION=1.1.0&SERVICE=WFS&REQUEST=GetFeature&typename=" + layername;

            var filter = "<ogc:Filter xmlns:ogc='http://www.opengis.net/ogc' xmlns:gml='http://www.opengis.net/gml'>" +
                "<ogc:Intersects>" +
                "<ogc:PropertyName>SHAPE</ogc:PropertyName>" +
                "<gml:Polygon srsName='urn:x-ogc:def:crs:EPSG:3857'>" +
                "<gml:outerBoundaryIs>" +
                "<gml:LinearRing>" +
                "<gml:coordinates>" + query + "</gml:coordinates>" +
                "</gml:LinearRing>" +
                "</gml:outerBoundaryIs>" +
                "</gml:Polygon>" +
                "</ogc:Intersects>" +
                "</ogc:Filter>";

            $.ajax({
                type: "GET",
                url: url,
                data: {
                    filter: encodeURI(filter)
                },
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Origin", $(location).attr('host'));
                },
            }).then(function (response) {
                console.log(response);
                var g = new ol.format.GML3();
                var features = g.readFeatures(response);
                console.log(features);
            }, function (response) {
                console.log(response);
            });
        });
    },
    layerCheckHandler: (target) => {
        console.dir(target);
        if (target == "POI") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.searchPoi();
            } else {
                NamuLayer.removeLayer("cluster", "search_cluster");
                NamuLayer.removeTooltipAll("poi");
            }
            return;
        } else if (target == "MARKER") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.searchMarker();
            } else {
                NamuLayer.removeLayer("vector", "search_vector");
            }
            return;
        } else if (target == "VIEWMAKER") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.viewMaker(data);
            } else {
                NamuLayer.removeLayer("marker", "view_marker");
            }
            return;
        } else if (target == "ACCIDENT") {
            if ($("#chk" + target).is(':checked')) {
                new Promise(resolve => {
                    const checkAccidentLayer = () => {
                        if (NamuLayer.isAccidentLayerAlive) {
                            if ($("#chk" + target).is(':checked')) {
                                $("#pmAcc").show();
                            }
                            resolve();
                        } else {
                            setTimeout(checkAccidentLayer, 100); // 적절한 시간 간격으로 체크
                        }
                    };
                    checkAccidentLayer();
                });
            } else {
                $("#pmAcc").hide();
                NamuLayer.removeLayer("cluster", "search_cluster");
                NamuLayer.removeTooltipAll("tableAccPOI");
            }
            return;
        } else if (target == "USAGE") {
            if ($("#chk" + target).is(':checked')) {
                $("#legend").show();
                new Promise(resolve => {
                    const checkUsageLayer = () => {
                        if (NamuLayer.isUsageLayerAlive) {
                            if ($("#chk" + target).is(':checked')) {
                                $("#pmUsage").show();
                            }
                            resolve();
                        } else {
                            setTimeout(checkUsageLayer, 100); // 적절한 시간 간격으로 체크
                        }
                    };
                    checkUsageLayer();
                });
            } else {
                $("#pmUsage").hide();
                NamuLayer.removeLayer("allUsage", "view_all_usage");
                NamuLayer.removeLayer("rentUsage", "view_rent_usage");
                NamuLayer.removeLayer("returnUsage", "view_return_usage");
                $("#vw-notice").hide();
                NamuLayer.delLayer("park");
                $("#legend").hide();
                if (NamuLayer.illegalFlag) {
                    NamuLayer.illegalFlag = false;
                }
            }
            return;
        } else if (target == "TIMEBASEDUSAGE") {
            if ($("#chk" + target).is(':checked')) {
                $('.gis-range-wrapper').show();

                // 디폴트로 쓸 날짜 가져오기
                var currentDate = new Date();
                var previousYearDate = new Date(currentDate.getFullYear() - 1, currentDate.getMonth(), currentDate.getDate());

                var formatString = 'YYYY-MM-DD HH:mm:ss';

                var dataObj = {};
                dataObj.operator= "";
                dataObj.retf = "rent";
                dataObj.isIPChecked = null;
                dataObj.startSearchDt = previousYearDate.getFullYear() + '-' + (previousYearDate.getMonth() + 1) + '-' + previousYearDate.getDate() + ' 00:00:00';
                dataObj.endSearchDt = dataObj.endSearchDt = currentDate.getFullYear() + '-' + (currentDate.getMonth() + 1) + '-' + currentDate.getDate() + ' 23:59:59';

                $.ajax({
                    headers: {
                        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                    },
                    type: "POST",
                    url: "/pm/driving/getPolygonInfo.json",
                    dataType: "json",
                    data: $.param(dataObj),
                    success: function (result) {

                        NamuLayer.timeBasedUsageResponseDto = result;

                        NamuLayer.getTrackStyle(document.querySelector(".gis-range input"));

                        $('.gis-control').text("pause_circle");

                        NamuLayer.rangePlayer($('.gis-range input'));

                    }, error: function (error) {
                        alert('정보 조회에 실패했습니다.\n해당 문제가 지속될 경우 관리자에게 문의하여 주십시요.');
                        console.log(error.code);
                    }
                });

            } else {
                NamuLayer.removeLayer("circlePolygon", "view_circle_polygon");
                $('.gis-range-wrapper').hide();
                NamuLayer.clearRangeTimer();
                $('.gis-range input').val(1); // 초기화
            }
            return;
        } else if (target == "MODLOCKHUB") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.gInfraMarker(target);
            } else {
                var GInfra = NamuLayer.gInfraType[target];

                NamuLayer.removeLayer(GInfra.source, GInfra.target);
            }
            return;
        } else if (target == "SMARTHUB") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.gInfraMarker(target);
            } else {
                var GInfra = NamuLayer.gInfraType[target];

                NamuLayer.removeLayer(GInfra.source, GInfra.target);
            }
            return;
        } else if (target == "FIREWATER") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.gInfraMarker(target);
            } else {
                var GInfra = NamuLayer.gInfraType[target];

                NamuLayer.removeLayer(GInfra.source, GInfra.target);
            }
            return;
        } else if (target == "STATION") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.gInfraMarker(target);
            } else {
                var GInfra = NamuLayer.gInfraType[target];

                NamuLayer.removeLayer(GInfra.source, GInfra.target);
            }
            return;
        } else if (target == "BICYCLEACC") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.polygonMaker(NamuLayer.accType.Bicycle);
            } else {
                NamuLayer.removeLayer(NamuLayer.accType.Bicycle.name, NamuLayer.accType.Bicycle.subName);
            }
            return;
        } else if (target == "ELDERACC") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.polygonMaker(NamuLayer.accType.Elder);
            } else {
                NamuLayer.removeLayer(NamuLayer.accType.Elder.name, NamuLayer.accType.Elder.subName);
            }
            return;
        } else if (target == "CHILDRENACC") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.polygonMaker(NamuLayer.accType.Children);
            } else {
                NamuLayer.removeLayer(NamuLayer.accType.Children.name, NamuLayer.accType.Children.subName);
            }
            return;
        } else if (target == "JAYWALKACC") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.polygonMaker(NamuLayer.accType.Jaywalk);
            } else {
                NamuLayer.removeLayer(NamuLayer.accType.Jaywalk.name, NamuLayer.accType.Jaywalk.subName);
            }
            return;
        } else if (target == "SCHOOLZONEACC") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.polygonMaker(NamuLayer.accType.SchoolZone);
            } else {
                NamuLayer.removeLayer(NamuLayer.accType.SchoolZone.name, NamuLayer.accType.SchoolZone.subName);
            }
            return;
        } else if (target == "TWOWHEELERACC") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.polygonMaker(NamuLayer.accType.TwoWheeler);
            } else {
                NamuLayer.removeLayer(NamuLayer.accType.TwoWheeler.name, NamuLayer.accType.TwoWheeler.subName);
            }
            return;
        } else if (target == "LINKACC") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.polygonMaker(NamuLayer.accType.Link);
            } else {
                NamuLayer.removeLayer(NamuLayer.accType.Link.name, NamuLayer.accType.Link.subName);
            }
            return;
        } else if (target == "ROADIDXACC") {
            if ($("#chk" + target).is(':checked')) {
               NamuLayer.lineStringMaker();
            } else {
                NamuLayer.removeLayer("roadIdx", "view_roadIdx");
            }
            return;
        } else if (target == "WALKERACC") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.polygonMaker(NamuLayer.accType.WalkerAcc);
            } else {
                NamuLayer.removeLayer(NamuLayer.accType.WalkerAcc.name, NamuLayer.accType.WalkerAcc.subName);
            }
            return;
        } else if (target == "DEATHACC") {
            if ($("#chk" + target).is(':checked')) {
                NamuLayer.gInfraMarker(target);
            } else {
                var GInfra = NamuLayer.gInfraType[target];

                NamuLayer.removeLayer(GInfra.source, GInfra.target);
            }
            return;
        }

        var selectLayer = NamuLayer.getLayer(target);
        selectLayer.setVisible($("#chk" + target).is(':checked'));

        if ($("#chk" + target).is(':checked')) {
            if (target == "HybridMap") {
                NamuLayer.getLayer("SatelliteMap").setVisible(true);
            }
        } else {
            if (target == "HybridMap") {
                NamuLayer.getLayer("SatelliteMap").setVisible($("#chkSatelliteMap").is(':checked'));
            } else if (target == "SatelliteMap") {
                NamuLayer.getLayer("SatelliteMap").setVisible($("#chkHybridMap").is(':checked'));
            }
        }
    },
    controlLayerHandler: (layerName) => {
        if (layerName == "HybridMap") {
            NamuLayer.getLayer("SatelliteMap").setVisible(true);
            NamuLayer.getLayer("HybridMap").setVisible(true);
        } else if (layerName == "SatelliteMap") {
            NamuLayer.getLayer("SatelliteMap").setVisible(true);
            NamuLayer.getLayer("HybridMap").setVisible(false);
        } else if (layerName == "2d-map") {
            NamuLayer.getLayer("HybridMap").setVisible(false);
            NamuLayer.getLayer("SatelliteMap").setVisible(false);
        }
    },
    addLayer: (layerName, layer) => {
        layer.set("title", layerName);
        NamuLayer.map.addLayer(layer);
    },
    getLayer: (layerName) => {
        return NamuLayer.map.getLayers().getArray().find((item) => {
            if (item.get("title") == layerName) {
                return true;
            }
        });
    },
    delLayer: (layerName) => {
        var layer = NamuLayer.getLayer(layerName);
        NamuLayer.map.removeLayer(layer);
    },
    removeLayer: (source, target) => {
        var array = [];

        NamuLayer.map.getLayers().forEach(function (layer) {
            if (layer.get(source) == target) {
                array.push(layer);
            }
        });

        while (array.length) {
            var lastElement = array.pop();
            NamuLayer.map.removeLayer(lastElement);
        }
    },
    layerDivControl: () => {
        if ($("#layerDiv").css("display") == "none") {
            $("#layerDiv").show();
            $("#layerDivControlDiv").css("left", "280px");
            $("#layerDivControl").attr("src", "<c:url value='/images/front/back.png'/>")
        } else {
            $("#layerDiv").hide();
            $("#layerDivControlDiv").css("left", "0");
            $("#layerDivControl").attr("src", "<c:url value='/images/front/next.png'/>")
        }
    },
    setSearchList: (gbn) => {
        var url = "";

        if (gbn == "uw") {
            url = "<c:url value='/ip/uwObsrvtInfo/selectList.json' />";
        } else if (gbn == "sw") {
            url = "<c:url value='/ip/swObsrvtInfo/selectList.json' />";
        } else if (gbn == "sp") {
            url = "<c:url value='/ip/swPntrtnObsrvtInfo/selectList.json' />";
        } else if (gbn == "frf") {
            url = "<c:url value='/ip/fndstRdctnFcltyInfo/selectList.json' />";
        } else if (gbn == "pump") {
            url = "<c:url value='/ip/pumpCntrlDvcInfo/selectList.json' />";
        } else if (gbn == "3d") {
            url = "<c:url value='/ip/hlgrmDvcInfo/selectList.json' />";
        } else if (gbn == "rw") {
            url = "<c:url value='/ip/roadWtrngDvcInfo/selectList.json' />";
        }

        $.ajax({
            url: url,
            type: "POST",
            data: {
                "searchKey1": "all",
                "pagingYn": false
            },
            dataType: "json",
            success: function (res) {
                var list = res.data;
                var html = "";
                var leng = list.length;

                for (var i = 0; i < leng; i++) {
                    var data = list[i];
                    if (gbn == "uw") {
                        html += "<option value='" + data.siteCode + "'>" + data.siteName + "</option>";
                    } else if (gbn == "sw") {
                        html += "<option value='" + data.siteCode + "'>" + data.siteName + "</option>";
                    } else if (gbn == "sp") {
                        html += "<option value='" + data.siteCode + "'>" + data.siteName + "</option>";
                    } else if (gbn == "frf") {
                        html += "<option value='" + data.fndstRdctnFcltySn + "'>" + data.fcltyNm + "</option>";
                    } else if (gbn == "pump") {
                        html += "<option value='" + data.pumpCntrlDvcSn + "'>" + data.dvcNm + "</option>";
                    } else if (gbn == "3d") {
                        html += "<option value='" + data.d3HlgrmDvcSn + "'>" + data.dvcNm + "</option>";
                    } else if (gbn == "rw") {
                        html += "<option value='" + data.roadWtrngDvcSn + "'>" + data.dvcNm + "</option>";
                    }
                }
                $("#searchKey2").empty();
                $("#searchKey2").html(html);
            }
        });
    },
    goPoint: (x, y, layer) => {
        if (x != null && x != "" && y != null && y != "") {
            x = Number(x);
            y = Number(y);
            var coordTransAddr = new ol.geom.Point([x, y]).transform('EPSG:5186', 'EPSG:3857').getCoordinates()

            NamuLayer.map.getView().setCenter(coordTransAddr);
            NamuLayer.map.getView().setZoom(parseInt(16));
            $("#chk" + layer).prop("checked", true);
            NamuLayer.layerCheckHandler(layer);
        }
    },
    searchAddress: (x, y) => {
        if (x != null && x != "" && y != null && y != "") {
            x = Number(x);
            y = Number(y);
            var coordTransAddr = new ol.geom.Point([x, y]).transform('EPSG:5186', 'EPSG:3857').getCoordinates()

            NamuLayer.map.getView().setCenter(coordTransAddr);
            NamuLayer.map.getView().setZoom(parseInt(16));
        }
        alert("지도상에서 등록할 위치를 클릭하여 주십시오.");
        var type = 'Point';
        addressActivation = new ol.interaction.Draw({
            type: /** @type {ol.geom.GeometryType} */ (type),
            style: new ol.style.Style({
                fill: new ol.style.Fill({
                    color: 'rgba(255, 255, 255, 0.2)'
                }),
                stroke: new ol.style.Stroke({
                    color: 'rgba(0, 0, 0, 0.5)',
                    lineDash: [10, 10],
                    width: 2
                }),
                image: new ol.style.Circle({
                    radius: 5,
                    stroke: new ol.style.Stroke({
                        color: 'rgba(0, 0, 0, 0.7)'
                    }),
                    fill: new ol.style.Fill({
                        color: 'rgba(255, 255, 255, 0.2)'
                    })
                })
            })
        });

        NamuLayer.map.addInteraction(addressActivation);

        addressActivation.on('drawstart',
            function (evt) {
            }, this);

        addressActivation.on('drawend',
            function (evt) {
                var point = new ol.geom.Point(evt.feature.getGeometry().getCoordinates());
                var resultPoint = new ol.Feature({
                    geometry: point,
                    id: "influe"
                });
                search_intersect(point);
            }, this);
    },
    //주소 가져오기
    search_intersect: (point) => {
        var x = point.getCoordinates()[0];
        var y = point.getCoordinates()[1];

        var coordTransAddr = new ol.geom.Point([point.getCoordinates()[0], point.getCoordinates()[1]]).transform('EPSG:3857', 'EPSG:5186').getCoordinates()

        x = coordTransAddr[0];
        y = coordTransAddr[1];

        var url = 'https://gis.jeju.go.kr/arcgis/rest/services/JJGWIMS/JJGWIMS/MapServer/2/query?where=1%3D1&text=&objectIds=&time=&geometry=' + x + '%2C' + y + '%2C' + x + '%2C' + y + '&geometryType=esriGeometryEnvelope&inSR=&spatialRel=esriSpatialRelIntersects&relationParam=&outFields=*&returnGeometry=true&returnTrueCurves=false&maxAllowableOffset=&geometryPrecision=&outSR=&returnIdsOnly=false&returnCountOnly=false&orderByFields=&groupByFieldsForStatistics=&outStatistics=&returnZ=false&returnM=false&gdbVersion=&returnDistinctValues=false&resultOffset=&resultRecordCount=&queryByDistance=&returnExtentsOnly=false&datumTransformation=&parameterValues=&rangeValues=&f=pjson';

        $.ajax({
            dataType: 'json',
            type: "GET",
            url: url,
            success: function (result) {
                if (result.features.length == 0) {
                    alert('해당 지역에는 지번이 없습니다.');
                } else {
                    var brd_nm = result.features[0].attributes.BRD_NAM;
                    var bjr_nm = result.features[0].attributes.BJR_NAM;
                    var bjd_nm = result.features[0].attributes.BJD_NAM;

                    var pnuFull = result.features[0].attributes.PNU;
                    var pnu = result.features[0].attributes.PNU_EDIT;

                    var jibun = result.features[0].attributes.JIBUN_EDIT;
                    var jibunAndHo = result.features[0].attributes.JIBUN;

                    var juso = "제주특별자치도 " + brd_nm + " " + bjr_nm + " " + bjd_nm + " " + jibunAndHo;

                    x = x.toFixed(4);
                    y = y.toFixed(4);

                    if (confirm(juso + " 해당 위치를 전송하시겠습니까?")) {
                        opener.parent.getJuso(pnuFull, x, y, juso);
                        self.close();
                    }
                }

            },
            fail: function (res) {
                if (res.readyState != 0 || res.status != 0) {
                    if (!(res.status == 0 && res.statusText == 'abort'))
                        alert('관리자에게 다음사항을 문의하세요.DataList (fail): ' + res.status + " - " + res.statusText);
                }
            },
            error: function (res) {
                if (res.readyState != 0 || res.status != 0) {
                    if (!(res.status == 0 && res.statusText == 'abort'))
                        alert('관리자에게 다음사항을 문의하세요.DataList (error): ' + res.status + " - " + res.statusText);
                }
            }
        });
    },
    searchPoi: () => {
        var features = new Array();
        var styleCache = new Array();

        $.ajax({
            type: "get",
            url: "http://api.vworld.kr/req/search",
            data: {
                "apiKey": "CEB52025-E065-364C-9DBA-44880E3B02B8",
                "type": "PLACE",
                "request": "search",
                "query": "제주",
            },
            dataType: 'jsonp',
            async: false,
            success: function (data) {
                console.dir(data);

                for (var poi in data.response.result.items) {
                    //Feature 객체에 저장하여 활용
                    features[poi] = new ol.Feature({
                        geometry: new ol.geom.Point(ol.proj.transform([data.response.result.items[poi].point.x * 1, data.response.result.items[poi].point.y * 1], 'EPSG:4326', "EPSG:900913")),
                        title: data.response.result.items[poi].title,
                        parcel: data.response.result.items[poi].address.parcel,
                        road: data.response.result.items[poi].address.road,
                        category: data.response.result.items[poi].category,
                        point: data.response.result.items[poi].point,
                        type: "poi"
                    });
                    features[poi].set("id", data.response.result.items[poi].id);
                }

                var vectorSource = new ol.source.Vector({
                    features: features
                });

                var clusterSource = new ol.source.Cluster({
                    distance: parseInt(30, 10),
                    source: vectorSource
                });

                var clusters = new ol.layer.Vector({
                    source: clusterSource,
                    style: function (feature) {
                        var size = feature.get('features').length;
                        var rValue = 255 - parseInt(size.toString()) * 20;
                        rValue = rValue > 0 ? rValue : 0;

                        var style = styleCache[size];
                        if (!style) {
                            style = new ol.style.Style({
                                image: new ol.style.Circle({
                                    radius: 15,
                                    stroke: new ol.style.Stroke({
                                        color: '#fff'
                                    }),
                                    fill: new ol.style.Fill({
                                        color: 'rgb(' + rValue + ', 0, 0)'
                                    })
                                }),
                                text: new ol.style.Text({
                                    text: size.toString(),
                                    fill: new ol.style.Fill({
                                        color: '#fff'
                                    })
                                })
                            });
                            styleCache[size] = style;
                        }
                        return style;
                    }
                });
                /*
                    기존검색결과를 제거하기 위해 키 값 생성
                */
                clusters.set("cluster", "search_cluster");

                NamuLayer.removeLayer("cluster", "search_cluster");

                NamuLayer.map.addLayer(clusters);
            },
            error: function (xhr, stat, err) {
                console.dir(err);
            },
        });
    },
    //오버레이 삭제
    deleteOverlay: function(id) {
        this.map.removeOverlay(this.map.getOverlayById(id));
    },
    move: (x, y) => {
        NamuLayer.map.getView().setCenter(ol.proj.transform([x, y], 'EPSG:4326', "EPSG:900913")); // 지도 이동
        NamuLayer.map.getView().setZoom(12);
    },
    searchMarker: () => {
        var success = (data) => {
            var features = new Array();
            console.dir(data);
            for (var item in data.response.result.items) {
                /*
                                if(o==0){
                                    NAMU.move(data.response.result.items[o].point.x*1,data.response.result.items[o].point.y*1);
                                }
                */
                //Feature 객체에 저장하여 활용
                features[item] = new ol.Feature({
                    geometry: new ol.geom.Point(ol.proj.transform([data.response.result.items[item].point.x * 1, data.response.result.items[item].point.y * 1], 'EPSG:4326', "EPSG:900913")),
/*
                    title: data.response.result.items[item].title,
                    parcel: data.response.result.items[item].address.parcel,
                    road: data.response.result.items[item].address.road,
                    category: data.response.result.items[item].category,
                    point: data.response.result.items[item].point
*/
                });
                // features[item].set("id", data.response.result.items[item].id);

                var iconStyle = new ol.style.Style({
                    image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
                        anchor: [10, 10],
                        anchorXUnits: 'pixels',
                        anchorYUnits: 'pixels',
                        src: '/images/marker/marker_blue.png'
                    }))
                });
                features[item].setStyle(iconStyle);
            }

            var vectorSource = new ol.source.Vector({
                features: features
            });
            /*
                기존검색결과를 제거하기 위해 키 값 생성
            */
            var vectorLayer = new ol.layer.Vector({
                source: vectorSource
            });

            /*
                기존검색결과를 제거하기 위해 키 값 생성
            */
            vectorLayer.set("vector", "search_vector")

            NamuLayer.removeLayer("vector", "search_vector");

            NamuLayer.map.addLayer(vectorLayer);
        };

        $.ajax({
            type: "get",
            url: "http://api.vworld.kr/req/search",
            data: {
                page: 1,
                type: "PLACE",
                request: "search",
                apiKey: "CEB52025-E065-364C-9DBA-44880E3B02B8",
                query: "제주",
            },
            dataType: 'jsonp',
            async: false,
            success: success,
            error: (xhr, stat, err) => console.error(err),
        });
    },
    viewMaker: (data) => {
        var features = [];
        console.dir(data);
        var items = data.slice();
        console.dir(items);

        for (const item of items) {
            const feature = new ol.Feature({
                geometry: new ol.geom.Point(
                    ol.proj.transform(
                        [item.attraction.latitude * 1, item.attraction.longitude * 1],
                        'EPSG:4326',
                        'EPSG:900913'
                    )
                ),
            });

            const iconStyle = new ol.style.Style({
                image: new ol.style.Icon({
                    anchor: [10, 10],
                    anchorXUnits: 'pixels',
                    anchorYUnits: 'pixels',
                    src: '/images/marker/marker_blue.png'
                })
            });

            feature.setStyle(iconStyle);
            features.push(feature);
        }

        const vectorSource = new ol.source.Vector({
            features: features
        });

        const vectorLayer = new ol.layer.Vector({
            source: vectorSource
        });

        vectorLayer.set("marker", "view_marker");
        NamuLayer.removeLayer("marker", "view_marker");
        NamuLayer.map.addLayer(vectorLayer);
    },
    setSearchModal: () => {
        setData($("#searchKey1").val(), $("#searchKey2").val(), "search");
    },
    polygonMaker: function(TypeClass) {

        if (TypeClass.code === "07") {
            $.ajax({
                url: "/pm/apa/getLnkgBaseAcdntRiskRngList.json",
                type: "GET",
                success: function (res) {
                    (async () => {
                        await NamuLayer.showGisPopup(res.length);

                        res.forEach(function (element) {
                            var coordinateList = NamuLayer.wktToGeoJSON(element.acdntRiskRng).coordinates;
                            NamuLayer.drawPolygon(coordinateList, "polygonLayer", true, 'polygon1', element, TypeClass);
                        });
                    })();
                },
                error: function(error) {
                    alert("사고 다발 지역 정보 조회에 실패했습니다.\n해당 문제가 지속될 경우 관리자에게 문의하여 주십시요.");
                    console.log(error.code);
                }
            })
        } else {
            $.ajax({
                url: "/pm/apa/getAfosList.json",
                type: "POST",
                data: {
                    type: TypeClass.code
                },
                dataType: "json",
                success: function (res) {

                    (async () => {
                        await NamuLayer.showGisPopup(res.length);

                        res.forEach(function (element) {
                            var coordinateList = JSON.parse(element.geom_json).coordinates;
                            NamuLayer.drawPolygon(coordinateList, "polygonLayer", true, 'polygon1', element, TypeClass);
                        });
                    })();
                },
                error: function(error) {
                    alert("사고 다발 지역 정보 조회에 실패했습니다.\n해당 문제가 지속될 경우 관리자에게 문의하여 주십시요.");
                    console.log(error.code);
                }
            })
        }

    },
    drawPolygon: (list, title, visible, className, data, TypeClass) => {
        // Define the coordinate system variables
        var epsg5179 = '+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=bessel +units=m +no_defs';
        var epsg4326 = '+proj=longlat +datum=WGS84 +no_defs';

        var pointObj = { x: 0, y: 0 }

        var size = "";
        var loc = "";
        var acdntNocs = 0;
        var caltCnt = 0;
        var polygonLayer = null;

        if (TypeClass.code === "00") {

            var feature = new ol.Feature({
                id: data.j_50_cd,
                geometry: new ol.geom.Polygon(
                    list
                ),
                type: "grid"
            });

            feature.getGeometry().transform('EPSG:4326', 'EPSG:3857');

            var vectorSource = new ol.source.Vector({
                features: [feature]
            });

            polygonLayer = new ol.layer.Vector({
                source : vectorSource,
                style : [
                    // 스타일 지정
                    new ol.style.Style({
                        fill: new ol.style.Fill({
                            color: `rgba(${TypeClass.rgba[0]},${TypeClass.rgba[1]},${TypeClass.rgba[2]},${TypeClass.rgba[3]})`
                        })
                    })
                ],
                title : title,
                visible : visible
            });

        } else {
            if (TypeClass.code === "07") { // Link
                if (data.ceptCrd) {
                    var numbers = data.ceptCrd.split(", ").map(Number);
                    var point = proj4(epsg5179, epsg4326, [numbers[0], numbers[1]]);

                    pointObj.x = point[0];
                    pointObj.y = point[1];
                }

                if (data.acdntRiskRngNm) {
                    loc = data.acdntRiskRngNm;
                }

                acdntNocs = data.acdntNocs;
                caltCnt = data.dcsdCnt + data.swsnCnt + data.sijnCnt + data.injDclCnt;

            } else {
                if (data.ocfrRngLotLat) {
                    var point = data.ocfrRngLotLat.split(",").map(Number);

                    pointObj.x = point[0];
                    pointObj.y = point[1];
                }

                if (data.brnchNm) {
                    loc = data.brnchNm;
                }

                acdntNocs = data.acdntNocs;
                caltCnt = data.caltCnt;

            }

            var feature = new ol.Feature({
                id: data.id,
                geometry: new ol.geom.Polygon( //geometry: new ol.geom.Circle(ol.proj.fromLonLat([lon, lat]), 600), 이런식으로 원형으로도 그릴수 있음
                    list
                ),
                point: pointObj,
                loc: loc,
                acdntNocs: acdntNocs,
                caltCnt: caltCnt,
                type: "polygon"
            });

            feature.getGeometry().transform('EPSG:4326', 'EPSG:3857');

            var vectorSource = new ol.source.Vector({
                features: [feature]
            });

            size = acdntNocs;

            polygonLayer = new ol.layer.Vector({
                source : vectorSource,
                style : [

                    // 스타일 지정
                    new ol.style.Style({
                        stroke: new ol.style.Stroke({
                            color: TypeClass.color,
                            width: 1
                        }),
                        fill: new ol.style.Fill({
                            color: `rgba(${TypeClass.rgba[0]},${TypeClass.rgba[1]},${TypeClass.rgba[2]},${TypeClass.rgba[3]})`
                        }),
                        text: new ol.style.Text({
                            text: size.toString(),
                            fill: new ol.style.Fill({
                                color: '#000000'
                            }),
                            font: '20px Arial'
                        })
                    })
                ],
                title : title,
                visible : visible
            });
        }

        polygonLayer.set(TypeClass.name, TypeClass.subName);
        //지도에 circle 레이어 추가
        map.addLayer(polygonLayer);
    },
    drawPolygon2: (list, title, visible, className, data, TypeClass) => {
        // Define the coordinate system variables
        var epsg5179 = '+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=bessel +units=m +no_defs';
        var epsg4326 = '+proj=longlat +datum=WGS84 +no_defs';

        var pointObj = { x: 0, y: 0 }

        var size = "";
        var loc = "";
        var acdntNocs = 0;
        var caltCnt = 0;
        var polygonLayer = null;

        if (TypeClass.code === "00") {

            var feature = new ol.Feature({
                id: data.j_50_cd,
                geometry: new ol.geom.Polygon(
                    list
                ),
                type: "grid"
            });

            feature.getGeometry().transform('EPSG:4326', 'EPSG:3857');

            var vectorSource = new ol.source.Vector({
                features: [feature]
            });

            polygonLayer = new ol.layer.Vector({
                source : vectorSource,
                style : [
                    // 스타일 지정
                    new ol.style.Style({
                        fill: new ol.style.Fill({
                            color: `rgba(${TypeClass.rgba[0]},${TypeClass.rgba[1]},${TypeClass.rgba[2]},${TypeClass.rgba[3]})`
                        })
                    })
                ],
                title : title,
                visible : visible
            });

        } else {
            if (TypeClass.code === "07") { // Link
                if (data.ceptCrd) {
                    var numbers = data.ceptCrd.split(", ").map(Number);
                    var point = proj4(epsg5179, epsg4326, [numbers[0], numbers[1]]);

                    pointObj.x = point[0];
                    pointObj.y = point[1];
                }

                if (data.acdntRiskRngNm) {
                    loc = data.acdntRiskRngNm;
                }

                acdntNocs = data.acdntNocs;
                caltCnt = data.dcsdCnt + data.swsnCnt + data.sijnCnt + data.injDclCnt;

            } else {
                if (data.ocfrRngLotLat) {
                    var point = data.ocfrRngLotLat.split(",").map(Number);

                    pointObj.x = point[0];
                    pointObj.y = point[1];
                }

                if (data.brnchNm) {
                    loc = data.brnchNm;
                }

                acdntNocs = data.acdntNocs;
                caltCnt = data.caltCnt;

            }

            var feature = new ol.Feature({
                id: data.id,
                geometry: new ol.geom.Polygon( //geometry: new ol.geom.Circle(ol.proj.fromLonLat([lon, lat]), 600), 이런식으로 원형으로도 그릴수 있음
                    list
                ),
                point: pointObj,
                loc: loc,
                acdntNocs: acdntNocs,
                caltCnt: caltCnt,
                type: "polygon"
            });

            feature.getGeometry().transform('EPSG:4326', 'EPSG:3857');

            var vectorSource = new ol.source.Vector({
                features: [feature]
            });

            size = acdntNocs;

            polygonLayer = new ol.layer.Vector({
                source : vectorSource,
                style : [

                    // 스타일 지정
                    new ol.style.Style({
                        stroke: new ol.style.Stroke({
                            color: TypeClass.color,
                            width: 1
                        }),
                        fill: new ol.style.Fill({
                            color: `rgba(${TypeClass.rgba[0]},${TypeClass.rgba[1]},${TypeClass.rgba[2]},${TypeClass.rgba[3]})`
                        }),
                        text: new ol.style.Text({
                            text: size.toString(),
                            fill: new ol.style.Fill({
                                color: '#000000'
                            }),
                            font: '20px Arial'
                        })
                    })
                ],
                title : title,
                visible : visible
            });
        }

        polygonLayer.set(TypeClass.name, TypeClass.subName);
        //지도에 circle 레이어 추가
        map.addLayer(polygonLayer);
    },
    removeTooltipAll: function (type) {
        var array = [];
        var tooltips = document.querySelectorAll(".overlayElement");

        tooltips.forEach((tooltip) => {
            if (tooltip.dataset.type === type) {
                array.push(tooltip);
            }
        })

        while (array.length) {
            var lastTooltip = array.pop();
            this.deleteOverlay(lastTooltip.dataset.uid);
        }
    },
    createAccLayer: function (lyrEnName) {
        var url = "/pm/gis/accidentLayer.mng";

        (async () => {
            try {
                const response = await $.ajax({
                    type: "GET",
                    url: url,
                    dataType: "text"
                });

                $("#row" + lyrEnName).append(response);
                this.isAccidentLayerAlive = true;
            } catch (error) {
                alert('서버 에러가 발생했습니다.\n해당 문제가 지속될 경우 관리자에게 문의하여 주십시오.');
                console.log(error.code);
            }
        })();
    },
    createUsageLayer: function (lyrEnName) {
        var url = "/pm/gis/usageLayer.mng";

        (async () => {
            try {
                const response = await $.ajax({
                    type: "GET",
                    url: url,
                    dataType: "text"
                });

                $("#row" + lyrEnName).append(response);
                this.isUsageLayerAlive = true;
            } catch (error) {
                alert('서버 에러가 발생했습니다.\n해당 문제가 지속될 경우 관리자에게 문의하여 주십시오.');
                console.log(error.code);
            }
        })();
    },
    makeMarkerSource: function (list, title, visible, markerImageSrc, makeTooltip, markerSize) {
        let features = [];
        for(var i = 0; i < list.length; i ++ ) {
            var marker = new ol.Feature({
                geometry: new ol.geom.Point(ol.proj.fromLonLat([parseFloat(list[i].x), parseFloat(list[i].y)])),    //마커 좌표 설정
                safetyZoneType: list[i].safetyZoneType ? list[i].safetyZoneType : null ,
                address: list[i].address ? list[i].address : null,
                text: makeTooltip && list[i].text ? list[i].text : null,
                zIndex: 50
            });
            var markerStyle = new ol.style.Style({
                image: new ol.style.Icon({
                    anchor: markerSize ? [30,30] : [10, 10],
                    anchorXUnits: 'pixels',
                    anchorYUnits: 'pixels',
                    opacity: 1,                                              // 마커 투명도 1=100%
                    scale: 0.8,                                              // 크기 1=100%
                    src: markerImageSrc,                                     // 마커 이미지
                    size: markerSize ? markerSize: [20, 20]                  // 마커 사이즈 (기본 [20, 20])
                }),
                text: list[i].text !== undefined && list[i].text !=='undefined' && !makeTooltip ?  new ol.style.Text({
                    text: list[i].text,                                      //텍스트를 넘겨주면 넘겨준 텍스트 표시
                    scale: markerSize ? 1 : 0.8,                             //마커 텍스트 크기
                    offsetY:  markerSize ? 25 : 10,
                    fill: new ol.style.Fill({                                //마커 텍스트 색
                        color: "black"
                    }),
                    stroke: new ol.style.Stroke({                            //마커 텍스트 테두리
                        color: "#fff",
                        width: 2
                    })
                }) : null
            });
            marker.setStyle(markerStyle);

            features.push(marker);
            markerList.push(turf.point([list[i].x, list[i].y]));
        }

        return features; //feature의 집합
    },
    makePolygonSource: function (object, imgClass) {
        var styleSelector = document.querySelector('.'+ imgClass);
        var multiPolygon = new ol.geom.MultiPolygon(object.polygonList);

        var feature = new ol.Feature({
           geometry: multiPolygon,
           zIndex: 3
        });

        var polygonStyle = [
                // 스타일 지정
                new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: styleSelector !== undefined ? getComputedStyle(styleSelector).color : 'blue',
                        width: 1
                    }),
                    fill: new ol.style.Fill({
                        color: styleSelector !== undefined ? getComputedStyle(styleSelector).fill : 'rgba(0,0,255,0.1)'
                    })
                })];
        feature.setStyle(polygonStyle);

        feature.getGeometry().transform('EPSG:4326', 'EPSG:3857');
        return [feature];
    },
    setCenter: function (xy) {
        var coordTransAddr = new ol.geom.Point(xy).transform('EPSG:4326', 'EPSG:3857').getCoordinates();

        NamuLayer.map.getView().setCenter(coordTransAddr);
        NamuLayer.map.getView().setZoom(parseInt(16));
    },
    setCenterCustom: function (xy, zoom) {
        var coordTransAddr = new ol.geom.Point(xy).transform('EPSG:4326', 'EPSG:3857').getCoordinates();

        NamuLayer.map.getView().setCenter(coordTransAddr);
        NamuLayer.map.getView().setZoom(zoom);
    },
    setMarker: function (x, y, color, category) {
        var iconStyle = null;

        var feature = new ol.Feature({
            geometry: new ol.geom.Point(ol.proj.transform([y * 1, x * 1], 'EPSG:4326', "EPSG:900913")),
        });

        if (color === "red") {
            var text = "";
            if (category === "driving") {
                text = "반납위치"
            } else if (category === "accident") {
                text = "사고위치"
            }

            iconStyle = new ol.style.Style({
                image: new ol.style.Icon(({
                    anchor: [10, 10],
                    anchorXUnits: 'pixels',
                    anchorYUnits: 'pixels',
                    src: '/images/marker/marker_red.png'
                })),
                text: new ol.style.Text({
                    text: text,
                    offsetY: 25,
                    fill: new ol.style.Fill({
                        color: "black"
                    })
                }),
                font: 'bold 13px Arial'
            });
        } else if (color === "green") {
            iconStyle = new ol.style.Style({
                image: new ol.style.Icon(({
                    anchor: [10, 10],
                    anchorXUnits: 'pixels',
                    anchorYUnits: 'pixels',
                    src: '/images/marker/marker_green.png'
                })),
                text: new ol.style.Text({
                    text: "반납위치",
                    offsetY: 25,
                    fill: new ol.style.Fill({
                        color: "black"
                    })
                }),
                font: 'bold 13px Arial'
            });
        } else if (color === "blue") {
            iconStyle = new ol.style.Style({
                image: new ol.style.Icon(({
                    anchor: [10, 10],
                    anchorXUnits: 'pixels',
                    anchorYUnits: 'pixels',
                    src: '/images/marker/marker_blue.png'
                })),
                text: new ol.style.Text({
                    text: "대여위치",
                    offsetY: 25,
                    fill: new ol.style.Fill({
                        color: "black"
                    })
                }),
                font: 'bold 13px Arial'
            });
        }

        feature.setStyle(iconStyle);

        var vectorSource = new ol.source.Vector({
            features: [feature]
        });

        var vectorLayer = new ol.layer.Vector({
            source: vectorSource
        });

        vectorLayer.set("vector", "search_vector")

        NamuLayer.map.addLayer(vectorLayer);
    },
    refreshMap: function (xy, zoomLevel) {
        var coordTransAddr = new ol.geom.Point(xy).transform('EPSG:4326', 'EPSG:3857').getCoordinates();

        NamuLayer.map.getView().setCenter(coordTransAddr);
        NamuLayer.map.getView().setZoom(zoomLevel);
    },
    searchIllegalParkingMarker: function() {

        $.ajax({
            type: "GET",
            url: "/pm/driving/getIllegalParkingList.json",
            success: function (data) {
                console.dir(data)

                var features = new Array();

                data.forEach(function(datum, index) {

                    features[index] = new ol.Feature({
                        id: datum.id,
                        geometry: new ol.geom.Point(ol.proj.transform([datum.epoint_x * 1, datum.epoint_y * 1], 'EPSG:4326', "EPSG:900913")),
                        type: "illegalParking"
                    });

                    var iconStyle = new ol.style.Style({
                        image: new ol.style.Icon(({
                            anchor: [10, 10],
                            anchorXUnits: 'pixels',
                            anchorYUnits: 'pixels',
                            src: '/images/marker/marker_red.png'
                        }))
                    });
                    features[index].setStyle(iconStyle);

                    var illegalParkingVSource = new ol.source.Vector({
                        features: features
                    });

                    var illegalParkingLayer = new ol.layer.Vector({
                        source: illegalParkingVSource
                    });

                    illegalParkingLayer.set("illegalParking", "search_illegal_parking")

                    NamuLayer.removeLayer("illegalParking", "search_illegal_parking");

                    NamuLayer.map.addLayer(illegalParkingLayer);
                });
            }
        });
    },
    checkAcc: function (target, color) {
        $("#row" + target).append(`<div><div style='width: 17px; height: 17px; background-color: ${color}; border: solid 1px black; margin-left: 50px;'></div></div>`);
    },
    uncheckAcc: function (target) {
        $("#row" + target).find("div:last-child").remove();
    },
    wktToGeoJSON: function (geomWKT) {
        var epsg5179 = '+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=bessel +units=m +no_defs';
        var epsg4326 = '+proj=longlat +datum=WGS84 +no_defs';

        var polygonCoordinates = [];
        var coordinates = geomWKT
            .match(/\(\((.*?)\)\)/)[1]
            .split(',')
            .map(function(coords) {
                return coords.trim().split(' ').map(parseFloat);
            })
            .map(function (coords) {
                var point = proj4(epsg5179, epsg4326, [coords[0], coords[1]]);

                return [point[0], point[1]];
            });

        polygonCoordinates.push(coordinates);

        return {
            type: 'Polygon',
            coordinates: polygonCoordinates,
        };
    },
    pmAccPoiSearch: function(accList) {
        var pmAccfeatures = new Array();
        var styleCache = new Array();

        accList.forEach(function(accItem, idx) {
            pmAccfeatures[idx] = new ol.Feature({
                geometry: new ol.geom.Point(ol.proj.transform([accItem.acdnt_x * 1, accItem.acdnt_y * 1], 'EPSG:4326', "EPSG:900913")),
                id: accItem.id,
                data: accItem,
                acdnt_x: accItem.acdnt_x,
                acdnt_y: accItem.acdnt_y,
                type: "accPOI"
            });
        });

        var pmAccVectorSource = new ol.source.Vector({
            features: pmAccfeatures,
        });

        var pmAccClusterSource = new ol.source.Cluster({
            distance: parseInt(30, 10),
            source: pmAccVectorSource
        });

        var clusters = new ol.layer.Vector({
            source: pmAccClusterSource,
            style: function (feature) {
                var size = feature.get('features').length;
                var rValue = 255 - parseInt(size.toString()) * 20;
                rValue = rValue > 0 ? rValue : 0;

                var style = styleCache[size];
                if (!style) {
                    style = new ol.style.Style({
                        image: new ol.style.Circle({
                            radius: 15,
                            stroke: new ol.style.Stroke({
                                color: '#fff'
                            }),
                            fill: new ol.style.Fill({
                                color: 'rgb(' + rValue + ', 0, 0)'
                            })
                        }),
                        text: new ol.style.Text({
                            text: size.toString(),
                            fill: new ol.style.Fill({
                                color: '#fff'
                            })
                        })
                    });
                    styleCache[size] = style;
                }
                return style;
            }
        });

        clusters.set("cluster", "search_cluster");

        NamuLayer.removeLayer("cluster", "search_cluster");

        NamuLayer.map.addLayer(clusters);
    },
    gInfraMarker: function(gInfraType) {
        var GInfra = NamuLayer.gInfraType[gInfraType];
        var gInfraIconStyle = null;

        $.ajax({
            type: "GET",
            url: GInfra.url,
            dataType: "json",
            success: function (list) {
                (async () => {
                    await NamuLayer.showGisPopup(list.length);

                    var gInfraFeatures = new Array();

                    if (list.length === 0) {
                        NamuLayer.removeLayer(GInfra.source, GInfra.target);
                    }

                    list.forEach(function (datum, index) {
                        var location_x = gInfraType === "DEATHACC" ? datum.rngX : datum.location_x;
                        var location_y = gInfraType === "DEATHACC" ? datum.rngY : datum.location_y;

                        gInfraFeatures[index] = new ol.Feature({
                            id: datum.id,
                            geometry: new ol.geom.Point(ol.proj.transform([location_x * 1, location_y * 1], 'EPSG:4326', "EPSG:900913")),
                            lon: location_x,
                            lat: location_y,
                            type: gInfraType
                        });

                        if (gInfraType === "MODLOCKHUB" || gInfraType === "DEATHACC") {
                            // 원형
                            gInfraIconStyle = new ol.style.Style({
                                image: new ol.style.Circle(({
                                    radius: 3,
                                    stroke: new ol.style.Stroke({
                                        color: GInfra.color
                                    }),
                                    fill: new ol.style.Fill({
                                        color: GInfra.color
                                    })
                                }))
                            });
                        } else if (gInfraType === "FIREWATER") {
                            // 사각형
                            gInfraIconStyle = new ol.style.Style({
                                image: new ol.style.RegularShape({
                                    radius: 3,
                                    points: 4,
                                    rotation: Math.PI / 4,
                                    stroke: new ol.style.Stroke({
                                        color: GInfra.color
                                    }),
                                    fill: new ol.style.Fill({
                                        color: GInfra.color
                                    })
                                })
                            });
                        } else if (gInfraType === "SMARTHUB") {
                            // 삼각형
                            gInfraIconStyle = new ol.style.Style({
                                image: new ol.style.RegularShape({
                                    radius: 3,
                                    points: 3,
                                    angle: Math.PI / 2,
                                    stroke: new ol.style.Stroke({
                                        color: GInfra.color
                                    }),
                                    fill: new ol.style.Fill({
                                        color: GInfra.color
                                    })
                                })
                            });
                        } else if (gInfraType === "STATION") {
                            // 마름모
                            gInfraIconStyle = new ol.style.Style({
                                image: new ol.style.RegularShape({
                                    radius: 3,
                                    points: 4,
                                    stroke: new ol.style.Stroke({
                                        color: GInfra.color
                                    }),
                                    fill: new ol.style.Fill({
                                        color: GInfra.color
                                    })
                                })
                            });
                        }

                        gInfraFeatures[index].setStyle(gInfraIconStyle);
                    });

                    var gInfraVSource = new ol.source.Vector({
                        features: gInfraFeatures
                    });

                    var gInfraLayer = new ol.layer.Vector({
                        source: gInfraVSource
                    });

                    gInfraLayer.set(GInfra.source, GInfra.target);
                    NamuLayer.removeLayer(GInfra.source, GInfra.target);

                    NamuLayer.map.addLayer(gInfraLayer);
                })();
            }, error: function (error) {
                alert('정보 조회에 실패했습니다.\n해당 문제가 지속될 경우 관리자에게 문의하여 주십시요.');
                console.log(error.code);
            }
        });
    },
    getDistance: function (lat1, lon1, lat2, lon2) {  // 두 위도 경도 사이의 길이를 구하는 함수
        var earthRadius = 6371;

        var lat1Rad = lat1 * (Math.PI / 180);
        var lon1Rad = lon1 * (Math.PI / 180);
        var lat2Rad = lat2 * (Math.PI / 180);
        var lon2Rad = lon2 * (Math.PI / 180);

        var latDiff = lat2Rad - lat1Rad;
        var lonDiff = lon2Rad - lon1Rad;

        // Haversine
        var a = Math.sin(latDiff / 2) ** 2 + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(lonDiff / 2) ** 2;
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        var distance = earthRadius * c;

        return distance;
    },
    showGisPopup: function (length) {
        if (length === 0) {
            $(".popup-wrapper").text("조회된 데이터가 없습니다.");
        } else {
            $(".popup-wrapper").text(`${length}건이 조회되었습니다.`);
        }

        $(".popup-wrapper").show();

        return new Promise((resolve, reject) => {
            setTimeout(function() {
                try {
                    $(".popup-wrapper").hide();
                    resolve();
                } catch (e) {
                    reject();
                }
            }, 1000);
        });
    },
    lineStringMaker: function () {
        $.ajax({
            url: "/pm/apa/getDtlLnkgRoadRiskIdxGrdKinds.json",
            type: "GET",
            success: function (result) {

                var array = result;

                var gradientColors = [];

                var startColor = "#FFED9F";
                var endColor = "#FF4500";
                var steps = array.length;

                if (steps <= 1) {
                   gradientColors.push(endColor);
                } else {
                    var start = parseInt(startColor.substring(1), 16);
                    var end = parseInt(endColor.substring(1), 16);
                    var step = (end - start) / (steps - 1);

                    for (var i = 0; i < steps; i++) {
                        var value = Math.round(start + step * i);

                        var color = `#${value.toString(16)}`;
                        gradientColors.push(color);
                    }
                }

                $.ajax({
                    url: "/pm/apa/getDtlLnkgRoadRiskIdxList.json",
                    type: "GET",
                    success: function (res) {
                        (async () => {
                            await NamuLayer.showGisPopup(res.length);

                            var lineStringFeatures = new Array();

                            res.forEach(function (element, index) {
                                var coordinates = [];
                                var lineString = element['line_string'].slice(1, -1).split(/\s+/);

                                for (var i = 0; i < lineString.length; i += 2) {
                                    var lon = parseFloat(lineString[i]);
                                    var lat = parseFloat(lineString[i + 1]);
                                    coordinates.push([lon, lat]);
                                }

                                var lineString = new ol.geom.LineString(coordinates).transform('EPSG:4326', 'EPSG:900913');

                                lineStringFeatures[index] = new ol.Feature({
                                    id: element.id,
                                    geometry: lineString,
                                    type: "LINESTRING"
                                });

                                var idxx = array.indexOf(element.dggrGrd);

                                var lineStyle = new ol.style.Style({
                                    stroke: new ol.style.Stroke({
                                        color: gradientColors[idxx],
                                        width: 5
                                    })
                                });

                                lineStringFeatures[index].setStyle(lineStyle);
                            });

                            var lineStringSource = new ol.source.Vector({
                                features: lineStringFeatures
                            });

                            var lineStringLayer = new ol.layer.Vector({
                                source: lineStringSource
                            });

                            lineStringLayer.set("roadIdx", "view_roadIdx");
                            NamuLayer.removeLayer("roadIdx", "view_roadIdx");

                            NamuLayer.map.addLayer(lineStringLayer);
                        })();
                    },
                    error: function(xhr, status, error) {
                        console.log('Request failed.');
                        console.log(error);
                    }
                });
            },
            error: function(xhr, status, error) {
                console.log('Request failed.');
                console.log(error);
            }
        });
    },
    drawCirclePolygon: function(element, color) {
        /*var circle = new ol.Feature({
            geometry: new ol.geom.Circle(
                ol.proj.transform([element.center.point[0], element.center.point[1]], 'EPSG:4326', 'EPSG:3857'), // 좌표 변환
                1000  // 반지름 (미터 단위)  180미터 정도의 오차가 있음
            ),
        });*/
        var circle = new ol.Feature({
            geometry: new ol.geom.Circle(
                ol.proj.transform([element.spoint_x, element.spoint_y], 'EPSG:4326', 'EPSG:3857'), // 좌표 변환
                1000  // 반지름 (미터 단위)  180미터 정도의 오차가 있음
            ),
        });

        var fill = new ol.style.Fill({
            color: "rgba(0, 0, 255, 0.1)"
        });

        var stroke = new ol.style.Stroke({
            color: 'transparent',
        });

        var style = new ol.style.Style({
            fill: fill,
            stroke: stroke,
        });

        circle.setStyle(style);

        var vectorSource = new ol.source.Vector({
            features: [circle],
        });

        var vectorLayer = new ol.layer.Vector({
            source: vectorSource,
        });

        vectorLayer.set("circlePolygon", "view_circle_polygon");
        map.addLayer(vectorLayer);
    },
    getTrackStyle: function (el) {
        NamuLayer.removeLayer("circlePolygon", "view_circle_polygon");
        var prefs = ['webkit-slider-runnable-track', 'moz-range-track', 'ms-track'];

        var curVal = el.value,
            val = (curVal - 1) * 16.66666667,
            style = '';

        if (NamuLayer.timeBasedUsageResponseDto) {
            for (var key in NamuLayer.timeBasedUsageResponseDto.rentTimeMap) {
                if (parseInt(key) + 1 === parseInt(el.value)) {
                    var list = NamuLayer.timeBasedUsageResponseDto.rentTimeMap[key];

                    list.forEach(function(object) {
                        NamuLayer.drawCirclePolygon(object, "blue");
                    });
                    break;
                }
            }
        }

        $('.range-labels li').removeClass('active selected');

        var curLabel = $('.range-labels').find('li:nth-child('+curVal+')');

        curLabel.addClass('active selected');
        /*curLabel.prevAll().addClass('selected');*/

        for (var i = 0; i < prefs.length; i++) {
            style += '.range {background: linear-gradient(to right, #37adbf 0%, #37adbf ' + val + '%, #fff '
                + val + '%, #fff 100%)}';
            style += '.range input::-' + prefs[i] + '{background: linear-gradient(to right, #37adbf 0%, #37adbf ' + val + '%, #b2b2b2 ' + val + '%, #b2b2b2 100%)}';
        }

        return style;
    },
    rangePlayer: function (rangeInput) { // 시간대별 이용 현황 range 자동 재생시 사용되는 함수
            var timerId = setTimeout(() => {
                var value = parseInt(rangeInput.val());
                if (value === 24) {
                    rangeInput.val(1).trigger('input');
                } else {
                    rangeInput.val(value + 1).trigger('input');
                }

                NamuLayer.timers.splice(0);

                NamuLayer.rangePlayer(rangeInput);
            }, 3000);

            NamuLayer.timers.push(timerId);
    },
    clearRangeTimer: function() {
        for (var i = 0; i < NamuLayer.timers.length; i++) {
            clearTimeout(NamuLayer.timers[i]);
        }
    },
    drawMultiPolygon: (list, title, visible, className, data, TypeClass) => {
        var colors = [[255, 0, 0, 0.3], [255, 38, 0, 0.3], [255, 76, 0, 0.3], [255, 114, 0, 0.3], [255, 152, 0, 0.3], [255, 190, 0, 0.3], [255, 228, 0, 0.3], [255, 240, 0, 0.3], [255, 248, 0, 0.3], [255, 255, 0, 0.3]];

        var multiPolygonCoords = list.map(polygonCoords => {
           return polygonCoords.map(coord => ol.proj.transform(coord, 'EPSG:4326', 'EPSG:3857'));
        });

        var feature = new ol.Feature({
            geometry: new ol.geom.MultiPolygon(
                multiPolygonCoords
            ),
            type: "grid"
        });

        var vectorSource = new ol.source.Vector({
            features: [feature]
        });

        var polygonLayer = new ol.layer.Vector({
            source : vectorSource,
            style : [
                // 스타일 지정
                new ol.style.Style({
                    fill: new ol.style.Fill({
                        color: `rgba(${TypeClass.rgba[0]},${TypeClass.rgba[1]},${TypeClass.rgba[2]},${TypeClass.rgba[3]})`
                    })
                })
            ],
            title : title,
            visible : visible
        });

        polygonLayer.set(TypeClass.name, TypeClass.subName);
        map.addLayer(polygonLayer);
    }
};
