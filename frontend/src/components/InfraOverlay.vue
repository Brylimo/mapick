<template>
  <div
    class="overlay-element"
    :data-id="info.data.id"
    :data-type="info.type"
    style="background-color: #ffffff; padding: 1rem 2rem"
  >
    <div class="overlay-wrapper">
      <div v-if="info.type === 'infra'" class="infra-overlay-header-wrapper">
        <div class="infra-overlay-title">{{ info.data.place_name }}</div>
        <div class="infra-overlay-category">{{ info.data.category_group_name }}</div>
      </div>
      <div v-if="info.type === 'infra'" class="infra-overlay-content-wrapper">
        <div class="infra-overlay-address">{{ info.data.road_address_name }}</div>
        <div class="infra-overlay-phone">{{ info.data.phone }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  info: Object, // OverlayInfo 객체
})
</script>
