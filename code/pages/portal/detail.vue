<template><PageHost v-if="ready && demo.user" :key="JSON.stringify(query)" mode="portalDetail" :query="query" :active="active" /></template>
<script setup lang="ts">
import PageHost from '../../components/PageHost.vue'
import { usePageContext } from '../../navigation/usePageContext'
const {demo,query,active,ready}=usePageContext('portalDetail')
</script>
