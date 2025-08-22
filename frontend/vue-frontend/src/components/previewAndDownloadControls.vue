<!-- FIX: preview element blinking -->
<script setup lang="ts">
import { ref, computed } from 'vue'
import { DownloadVideo } from '@/scripts/utils'

const props = defineProps<{
    title: string
    author?: string
    qualityOptions?: Record<string, number>
    thumbnailUrl?: string
    videoUrl?: string
}>()
const selectedQuality = ref<number>()
const sizeField = ref<string>('')
const size = computed(function (): number {
    return parseInt(sizeField.value)
})

function download() {
    if (size.value && props.videoUrl) {
        DownloadVideo(props.videoUrl, props.title, size.value)
    }
}
</script>
<template>
    <div id="previewAndDownloadControls">
        <div style="aspect-ratio: 200/113; height: 100%">
            <component :is="videoUrl ? 'a' : 'div'" :href="videoUrl" style="display: inline">
                <img
                    :src="thumbnailUrl"
                    alt="Обложка видео"
                    style="width: 100%; height: 100%; object-fit: cover"
                />
            </component>
        </div>

        <div style="display: flex; flex-direction: column; justify-content: space-between">
            <div>
                <component :is="videoUrl ? 'a' : 'div'" :href="videoUrl">
                    <p style="font-size: 1.5em; margin: 0">{{ title }}</p>
                </component>

                <p style="margin-top: 0.5em">{{ author }}</p>
            </div>

            <div>
                <div v-if="qualityOptions" style="margin-bottom: 1em">
                    <span>Качество: </span>
                    <select v-model="selectedQuality">
                        <option
                            v-for="[key, value] in Object.entries(props.qualityOptions ?? {})"
                            :key="key"
                            :value="value"
                        >
                            {{ key }}
                        </option>
                    </select>
                </div>

                <label for="size">Размер{{ `)))` }} </label>

                <input
                    id="size"
                    type="text"
                    placeholder="байтов"
                    v-model="sizeField"
                    style="border-style: solid; border-width: 1px; border-radius: 0.5em"
                />

                <button v-if="videoUrl" :disabled="!size" @click="download">Скачать</button>
            </div>
        </div>
    </div>
</template>

<style>
#previewAndDownloadControls {
    display: flex;
    flex-direction: row;
    height: 100%;
    justify-content: center;
    gap: 1em;
    max-width: 700px;
    @media (width <= 650px) {
        flex-direction: column;
    }
}
</style>
