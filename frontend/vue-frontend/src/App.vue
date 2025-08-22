<script setup lang="ts">
import { ref, watch } from 'vue'
import PreviewOrText from '@/components/previewOrText.vue'
import { getVideoDataFromBackend, getVideoDataFromYoutube } from '@/scripts/utils'

const videoURL = ref<string>()

const confirmedURL = ref<string>()
const thumbnail = ref<string>()
const videoTitle = ref<string>()
const authorName = ref<string>()
const qualityOptions = ref<Record<string, number>>()

watch(videoURL, async function () {
    const currentUrl = videoURL.value

    async function updateDataFromYoutube() {
        if (videoURL.value) {
            const videoDataFromYoutube = await getVideoDataFromYoutube(videoURL.value)
            if (videoDataFromYoutube.title) {
                confirmedURL.value = currentUrl
            }
            if (videoDataFromYoutube.thumbnail_url) {
                thumbnail.value = videoDataFromYoutube.thumbnail_url
            }
            if (videoDataFromYoutube.title) {
                videoTitle.value = videoDataFromYoutube.title
            }
            if (videoDataFromYoutube.author_name) {
                authorName.value = videoDataFromYoutube.author_name
            }
        }
    }

    async function updateDataFromBackend() {
        if (videoURL.value) {
            const videoDataFromServer = await getVideoDataFromBackend(videoURL.value)
            if (videoDataFromServer.title) {
                confirmedURL.value = currentUrl
            }
            if (videoDataFromServer.thumbnail) {
                thumbnail.value = videoDataFromServer.thumbnail
            }
            if (videoDataFromServer.title) {
                videoTitle.value = videoDataFromServer.title
            }
            if (videoDataFromServer.idByQualityName) {
                qualityOptions.value = videoDataFromServer.idByQualityName
            }
        }
    }

    updateDataFromYoutube()
    updateDataFromBackend()
})
</script>

<template>
    <preview-or-text
        :author="authorName"
        :video-title="videoTitle"
        :thumbnail-url="thumbnail"
        :quality-options="qualityOptions"
        :video-url="confirmedURL"
    ></preview-or-text>
    <div style="display: flex; flex-direction: row; justify-content: center">
        <!-- FIX: size of the field -->
        <input
            type="text"
            placeholder="Ссылка на видео"
            v-model="videoURL"
            style="
                font-size: 2rem;
                border-style: solid;
                border-width: 1px;
                border-radius: 0.5em;
                padding: 0.1em;
                padding-left: 0.5em;
                background-color: transparent;
                font-weight: 300;
                max-width: 100%;
                width: 1024px;
            "
        />
    </div>
</template>
