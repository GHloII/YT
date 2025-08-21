<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import PreviewOrText from '@/components/previewOrText.vue'
import { type videoDataFromBackend } from '@/objects/videoDataFromTheServer'
import { type videoDataFromYoutube } from '@/objects/videoDataFromYoutube'
import { getVideoDataFromBackend, getVideoDataFromYoutube } from '@/scripts/utils'
const videoURL = ref<string>()
const videoDataFromServer = ref<videoDataFromBackend>()
const videoDataFromYoutube = ref<videoDataFromYoutube>()
const thumbnaill = computed(function () {
    return videoDataFromYoutube.value?.thumbnail_url || videoDataFromServer.value?.thumbnail
})
const debounceTimeout = ref()

const previewURL = ref<string>()

watch(videoURL, async function () {
    clearTimeout(debounceTimeout.value)
    debounceTimeout.value = setTimeout(function () {
        const currentUrl = videoURL.value
        async function updateDataFromYoutube() {
            if (videoURL.value) {
                videoDataFromYoutube.value = undefined
                videoDataFromYoutube.value = await getVideoDataFromYoutube(videoURL.value)
                if (videoDataFromYoutube.value.title) {
                    previewURL.value = currentUrl
                }
            }
        }
        async function upateDataFromBackend() {
            if (videoURL.value) {
                videoDataFromServer.value = undefined
                videoDataFromServer.value = await getVideoDataFromBackend(videoURL.value)
                if (videoDataFromServer.value.title) {
                    previewURL.value = currentUrl
                }
            }
        }
        updateDataFromYoutube()
        upateDataFromBackend()
    })
})
</script>

<template>
    <preview-or-text
        :author="videoDataFromYoutube?.author_name"
        :video-title="videoDataFromYoutube?.title || videoDataFromServer?.title"
        :thumbnail-url="thumbnaill"
        :quality-options="videoDataFromServer?.idByQualityName"
        :video-url="previewURL"
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
