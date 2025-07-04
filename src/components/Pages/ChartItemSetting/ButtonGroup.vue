<template>
    <n-space style="margin: 8px 0 0 5px">
        <n-tooltip trigger="hover" v-for="(item, index) in buttonArrs" :key="index">
            <template #trigger>
                <custom-icon class="cursor" :icon="item.icon" :width="item.size[0]" :height="item.size[1]"
                    @click="clickFunction(item.action)" />
            </template>
            <span>{{ item.tooltip }}</span>
        </n-tooltip>
    </n-space>
</template>

<script setup lang="ts">
const props = defineProps({
    buttonArrs: {
        type: Array<any>,
        default: () => [],
    }
})

const clickFunction = (func: Function) => {
    func()
}

</script>

<style scoped></style>