import { ref } from 'vue'
import * as ol from 'ol'
import Point from 'ol/geom/Point'
import { fromLonLat, transform } from 'ol/proj'
import VectorLayer from 'ol/layer/Vector'
import VectorSource from 'ol/source/Vector'
import Overlay from 'ol/Overlay'
import Feature from 'ol/Feature'
import Heatmap from 'ol/layer/Heatmap'
import { Cluster } from 'ol/source'
import { Icon, Style, Circle as CircleStyle, Fill, Stroke, Text } from 'ol/style'

import markerDefault from '@/assets/img/marker/marker.png'

const map = ref(null)

export function useVWorldMap() {
  function setMap(newMap) {
    map.value = newMap
  }

  function addLayer(layer) {
    if (!map.value) return
    map.value.addLayer(layer)
  }

  function createTooltipOverlay(coordinate, text, type) {
    // todo overlay 부분 진행하기
    const tooltipElement = document.createElement('div')
    tooltipElement.className = 'ol-tooltip'
    tooltipElement.innerText = text
    tooltipElement.dataset.type = type
    //tooltipElement.style.display = 'none'

    Object.assign(tooltipElement.style, {
      backgroundColor: 'rgba(0, 0, 0, 0.7)',
      color: 'white',
      padding: '4px 8px',
      borderRadius: '4px',
      fontSize: '12px',
      whiteSpace: 'nowrap',
      boxShadow: '0 2px 4px rgba(0,0,0,0.3)',
      pointerEvents: 'none', // 툴팁 위에 마우스 이벤트 비활성화
      transition: 'opacity 0.2s ease',
    })

    const tooltipOverlay = new Overlay({
      element: tooltipElement,
      offset: [2, 26],
      positioning: 'bottom-center',
    })

    tooltipOverlay.setPosition(coordinate)
    return tooltipOverlay
  }

  function removeLayerByProperty(key, value) {
    if (!map.value) return
    const layers = map.value.getLayers().getArray()
    layers.forEach((layer) => {
      if (layer.get(key) === value) {
        map.value.removeLayer(layer)
      }
    })
  }

  function removeAllOverlays(type) {
    if (!map.value) return

    const overlays = map.value.getOverlays().getArray() // 모든 overlay 가져오기

    const overlaysToRemove = overlays.filter((overlay) => {
      const element = overlay.getElement()
      return element?.dataset?.type === type
    })

    overlaysToRemove.forEach((overlay) => {
      map.value.removeOverlay(overlay)
    })
  }

  function getLayer(layerName) {
    return map.value
      ?.getLayers()
      .getArray()
      .find((l) => l.get('title') === layerName)
  }

  function controlLayerVisibility(layerName) {
    const satellite = getLayer('SatelliteMap')
    const hybrid = getLayer('HybridMap')

    if (!satellite || !hybrid) return

    if (layerName === 'HybridMap') {
      satellite.setVisible(true)
      hybrid.setVisible(true)
    } else if (layerName === 'SatelliteMap') {
      satellite.setVisible(true)
      hybrid.setVisible(false)
    } else if (layerName === '2d-map') {
      satellite.setVisible(false)
      hybrid.setVisible(false)
    }
  }

  function pinRedMarker(lon, lat, tooltipText) {
    removeAllOverlays('manual-marker')

    const coordinate = transform([lon, lat], 'EPSG:4326', 'EPSG:3857')

    const feature = new ol.Feature({
      geometry: new Point(coordinate),
    })

    const iconStyle = new Style({
      image: new Icon({
        anchor: [0.5, 1],
        anchorXUnits: 'fraction',
        anchorYUnits: 'fraction',
        src: markerDefault,
        scale: 0.09,
      }),
    })

    feature.setStyle(iconStyle)
    feature.set('type', 'manual-marker')

    const source = new VectorSource({ features: [feature] })
    const layer = new VectorLayer({ source })
    layer.set('layer_name', 'pin_red_marker')

    addLayer(layer)

    if (tooltipText && map.value) {
      const tooltipOverlay = createTooltipOverlay(coordinate, tooltipText, 'manual-marker')
      map.value.addOverlay(tooltipOverlay)
    }
  }

  function moveTo(lon, lat, zoom = 12) {
    if (!map.value) return
    const center = fromLonLat([lon, lat])
    map.value.getView().setCenter(center)
    map.value.getView().setZoom(zoom)
  }

  function addZoneMarkers(markerList, selectedLayer) {
    if (!map.value) return

    // 기존 마커 제거 (선택적으로 제거할 경우)
    removeLayerByProperty('layer_name', 'pin_red_marker')
    removeLayerByProperty('layer_name', 'search_cluster')
    removeLayerByProperty('layer_name', 'search_heatmap')

    removeAllOverlays('manual-marker')

    if (selectedLayer === 'heatmap') {
      addHeatMapLayer(markerList)
    } else if (selectedLayer === 'marker') {
      addMarkerLayer(markerList)
    } else if (selectedLayer === 'cluster') {
      addClusterLayer(markerList)
    }
  }

  function addMarkerLayer(markerList) {
    // 여러 개의 Feature 생성
    const features = markerList
      .filter((z) => z?.attraction?.longitude && z?.attraction?.latitude)
      .map((z) => {
        const lat = parseFloat(z.attraction.latitude)
        const lon = parseFloat(z.attraction.longitude)

        const coordinate = transform([lon, lat], 'EPSG:4326', 'EPSG:3857')

        const feature = new ol.Feature({
          geometry: new Point(transform([lon, lat], 'EPSG:4326', 'EPSG:3857')),
        })

        const iconStyle = new Style({
          image: new Icon({
            anchor: [0.5, 1],
            anchorXUnits: 'fraction',
            anchorYUnits: 'fraction',
            src: markerDefault,
            scale: 0.09,
          }),
        })

        feature.setStyle(iconStyle)
        feature.set('tooltip', '마커입니다')

        const tooltipOverlay = createTooltipOverlay(coordinate, z.attraction.title, 'manual-marker')
        map.value.addOverlay(tooltipOverlay)

        return feature
      })

    const source = new VectorSource({ features })
    const layer = new VectorLayer({ source })
    layer.set('layer_name', 'pin_red_marker')

    addLayer(layer)
  }

  function addHeatMapLayer(markerList) {
    if (!map.value) return

    const features = markerList.map((item) => {
      const feature = new Feature({
        geometry: new Point(
          transform(
            [item.attraction.longitude, item.attraction.latitude],
            'EPSG:4326',
            'EPSG:3857',
          ),
        ),
      })

      // 전체적으로 고르게 퍼지도록 가중치 조정
      const normalizedWeight = markerList.length * 0.08
      feature.set('weight', normalizedWeight)

      return feature
    })

    const heatmapLayer = new Heatmap({
      source: new VectorSource({ features }),
      radius: 25,
      blur: 30,
      weight: (feature) => feature.get('weight'),
    })

    heatmapLayer.set('layer_name', 'search_heatmap')

    map.value.addLayer(heatmapLayer)
  }

  function addClusterLayer(markerList) {
    if (!map.value) return

    const features = markerList.map((item) => {
      const feature = new Feature({
        geometry: new Point(
          transform(
            [item.attraction.longitude, item.attraction.latitude],
            'EPSG:4326',
            'EPSG:3857',
          ),
        ),
      })

      return feature
    })

    const vectorSource = new VectorSource({
      features,
    })

    const clusterSource = new Cluster({
      distance: 30, // 클러스터링 거리 (픽셀 단위)
      source: vectorSource,
    })

    const styleCache = {}

    const clusterLayer = new VectorLayer({
      source: clusterSource,
      style: (feature) => {
        const size = feature.get('features').length
        if (!styleCache[size]) {
          const rValue = Math.max(0, 255 - size * 20)
          styleCache[size] = new Style({
            image: new CircleStyle({
              radius: 15,
              stroke: new Stroke({
                color: '#fff',
              }),
              fill: new Fill({
                color: `rgb(${rValue}, 0, 0)`,
              }),
            }),
            text: new Text({
              text: size.toString(),
              fill: new Fill({
                color: '#fff',
              }),
            }),
          })
        }
        return styleCache[size]
      },
    })

    clusterLayer.set('layer_name', 'search_cluster')
    removeLayerByProperty('layer_name', 'search_cluster')
    map.value.addLayer(clusterLayer)
  }

  function showHeatMapLayer(markerList) {
    if (!map.value) return

    const features = markerList.map((item) => {
      const feature = new Feature({
        geometry: new Point(
          transform(
            [item.longitude, item.latitude],
            'EPSG:4326',
            'EPSG:3857',
          ),
        ),
      })

      // 전체적으로 고르게 퍼지도록 가중치 조정
      const normalizedWeight = 1 / item.likeCount
      feature.set('weight', normalizedWeight)

      return feature
    })

    const heatmapLayer = new Heatmap({
      source: new VectorSource({ features }),
      radius: 25,
      blur: 30,
      weight: (feature) => feature.get('weight'),
    })

    heatmapLayer.set('layer_name', 'search_heatmap')

    map.value.addLayer(heatmapLayer)
  }

  return {
    map,
    setMap,
    addLayer,
    removeLayerByProperty,
    controlLayerVisibility,
    pinRedMarker,
    moveTo,
    addZoneMarkers,
    addHeatMapLayer,
    addClusterLayer,
    showHeatMapLayer
  }
}
