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
const sizefield = ref<string>('')
const size = computed(function (): number {
    return parseInt(sizefield.value)
})
</script>
<template>
    <div id="previewAndDownloadControls">
        <div style="aspect-ratio: 200/113; height: 100%">
            <img
                :src="thumbnailUrl"
                alt="Обложка видео"
                style="width: 100%; height: 100%; object-fit: cover"
            />
        </div>

        <div style="display: flex; flex-direction: column; justify-content: space-between">
            <div>
                <p style="font-size: 1.5em; margin: 0">{{ title }}</p>
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
                    v-model="sizefield"
                    style="border-style: solid; border-width: 1px; border-radius: 0.5em"
                />

                <button
                    v-if="videoUrl"
                    :disabled="!size"
                    @click="
                        function () {
                            if (size && videoUrl) {
                                DownloadVideo(videoUrl, title, size)
                            }
                        }
                    "
                >
                    Скачать
                </button>
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
