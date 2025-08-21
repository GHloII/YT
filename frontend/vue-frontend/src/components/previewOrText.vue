<script setup lang="ts">
import previewAndDownloadControls from './previewAndDownloadControls.vue'
defineProps<{
    videoTitle?: string
    author?: string
    qualityOptions?: Record<string, number>
    thumbnailUrl?: string
    videoUrl?: string
}>()
</script>
<template>
    <div
        style="
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 200px;
            width: 100%;
            margin-bottom: 1em;
        "
    >
        <preview-and-download-controls
            v-if="videoTitle"
            :quality-options="qualityOptions"
            :title="videoTitle"
            :author="author"
            :thumbnail-url="thumbnailUrl"
            :video-url="videoUrl"
        ></preview-and-download-controls>
        <h1 v-else id="preview-placeholder">Видео с Ютуба в файл</h1>
    </div>
</template>
