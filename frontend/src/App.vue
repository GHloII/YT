<script setup lang="ts">
import {computed, ref, watch} from 'vue'
import {DownloadVideo, getVideoDataFromBackend, getVideoDataFromYoutube} from '@/scripts/utils'

class VideoData {
    title: string
    thumbnail: string
    url: string
    authorName?: string
    qualityOptions?: Record<string, number>
    authorUrl?: string
    downloadable: boolean = false

    constructor(data: {
        title: string,
        thumbnail: string,
        url: string,
        authorName?: string,
        qualityOptions?: Record<string, number>,
        authorUrl?: string
    }) {
        const {title, thumbnail, url, authorName, qualityOptions, authorUrl} = data
        this.title = title
        this.thumbnail = thumbnail
        this.url = url


        if (authorName) {
            this.addAuthorName(authorName, title)
        }

        if (qualityOptions) {
            this.addQualityOptions(qualityOptions, title)
        }

        if (authorUrl) {
            this.addAuthorUrl(authorUrl, title)
        }
        console.log(this)

    }


    dataIsFromTheSameVideo(title: string): boolean {
        if (title != this.title) {
            throw new Error("data is for another video")
        }
        return true
    }

    addQualityOptions(qualityOptions: Record<string, number>, title: string) {
        this.dataIsFromTheSameVideo(title)
        this.qualityOptions = qualityOptions
    }

    addAuthorUrl(url: string, title: string) {
        this.dataIsFromTheSameVideo(title)
        this.authorUrl = url
    }

    addAuthorName(authorName: string, title: string) {
        this.dataIsFromTheSameVideo(title)
        this.authorName = authorName
    }

    makeDownloadable(title: string) {
        if (title == this.title) {
            this.downloadable = true
        }
    }


}


const videoURL = ref<string>()

const videoData = ref<VideoData>()


watch(videoURL, async function () {
    const currentUrl = videoURL.value
    if (!currentUrl) {
        return
    }

    getVideoDataFromBackend(currentUrl).then(function (data) {

        if (videoData.value?.dataIsFromTheSameVideo(data.title)) {
            videoData.value.addQualityOptions(data.idByQualityName, data.title)
        } else {
            videoData.value = new VideoData({
                title: data.title,
                qualityOptions: data.idByQualityName,
                thumbnail: data.thumbnail,
                url: currentUrl
            })
        }
        videoData.value?.makeDownloadable(data.title)
    })

    getVideoDataFromYoutube(currentUrl).then(function (data) {
        console.log(data)
        if (videoData.value?.dataIsFromTheSameVideo(data.title)) {
            videoData.value.addAuthorName(data.author_name, data.title)
        } else {
            console.log("creating object")
            videoData.value = new VideoData({
                title: data.title,
                thumbnail: data.thumbnail_url,
                authorName: data.author_name,
                authorUrl: data.author_url,
                url: currentUrl
            })
        }
    })
})


const selectedQuality = ref<number>()
const sizeField = ref<string>('')
const size = computed(function (): number {
    return parseInt(sizeField.value)
})

function download() {
    if (videoData.value?.downloadable) {
        DownloadVideo(videoData.value.url, videoData.value.title, size.value)
    }
}
</script>

<template>

    <div
        style="
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 200px;
            max-width: 100%;
            margin-bottom: 1em;
            max-height: fit-content;
        "
    >

        <div v-if="videoData" id="previewAndDownloadControls">
            <div style="aspect-ratio: 200/113; height: 100%">
                <component :is="videoData.url ? 'a' : 'div'" :href="videoData.url"
                           style="display: inline">
                    <img
                        :src="videoData.thumbnail"
                        alt="Обложка видео"
                        style="width: 100%; height: 100%; object-fit: cover"
                    />
                </component>
            </div>

            <div style="display: flex; flex-direction: column; justify-content: space-between">
                <div>
                    <component :is="videoData.url ? 'a' : 'div'" :href="videoData.url">
                        <p style="font-size: 1.5em; margin: 0">{{ videoData.title }}</p>
                    </component>

                    <component :is="videoData.authorUrl? 'a' : 'p'" :href="videoData.authorUrl"
                               style="margin-top: 0.5em">
                        {{ videoData.authorName }}
                    </component>
                </div>

                <div>
                    <div v-if="videoData.qualityOptions" style="margin-bottom: 1em">
                        <span>Качество: </span>
                        <select v-model="selectedQuality">
                            <option
                                v-for="[key, value] in Object.entries(videoData.qualityOptions ?? {})"
                                :key="key"
                                :value="value"
                            >
                                {{ key }}
                            </option>
                        </select>
                    </div>

                    <div v-if="videoData.downloadable">
                        <label for="size">Размер{{ `)))` }} </label>

                        <input
                            id="size"
                            type="text"
                            placeholder="байтов"
                            v-model="sizeField"
                            style="border-style: solid; border-width: 1px; border-radius: 0.5em"
                        />
                    </div>

                    <button v-if="videoData.url && videoData.downloadable" :disabled="!size"
                            @click="download">
                        Скачать
                    </button>
                </div>
            </div>
        </div>
        <h1 v-else id="preview-placeholder" style="height: fit-content">Видео с Ютуба в файл</h1>
    </div>

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
