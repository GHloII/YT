// @ts-check
import { VIDEO_SERVER_ADDRESS } from "./config.js";
const logoPreviewElement = document.createElement("h1")
logoPreviewElement.innerText = "Видео с Ютуба в файл"

const previewPlaceholderText = ` <h1 id="preview-placeholder">Видео с Ютуба в файл</h1>`

export class DownloadHistory {
    history = []
    historyLocalStorageName
    constructor(storageName) {
        this.historyLocalStorageName = storageName
        const historyFromLocalStorage = JSON.parse(localStorage.getItem(this.historyLocalStorageName) || '')
        this.history = historyFromLocalStorage != null ? historyFromLocalStorage : []
    }

    addItem(item) {
        this.history.push(item)
        this.saveToLocalStorage()
    }

    saveToLocalStorage() {
        localStorage.setItem(this.historyLocalStorageName, JSON.stringify(this.history))
    }
    clear() {
        this.history = []
        this.saveToLocalStorage()
    }
}



/**
 * @param {string} thumbnailUrl 
 * @param {string} title 
 * @param {object} qualityOptions
 * @returns {HTMLDivElement}
 */
function thumbnailAndDownloadControls(thumbnailUrl, title, author, qualityOptions) {
    const elementText = `<div style="display:flex; flex-direction: row; height: 100%; justify-content: center; gap:1em; max-width: 700px;"

            id="thumbnail-metadata-and-download-controls">

            <div class="thumbnail-container" style="aspect-ratio: 200/113; height: 100%;">
                <img src="${thumbnailUrl}" alt="Обложка видео"
                    style="width:100%; height: 100%; object-fit: cover;">
            </div>

            <div id="meta-data-and-download-controls"
                style="display: flex; flex-direction: column; justify-content: space-between;">

                <div id="video-meta-data">
                    <p class="video-title" style="font-size: 1.5em; margin:0">${title}</p>
                    <p class="video-author" style="margin-top:0.5em">${author}</p>
                </div>
                <div id="download-controls">

                    <span>Качество: </span>
                    <select class="quality-selector">
                    </select>

                    <button id="download-button"
                        style="display:block; margin:0; padding:0; margin-top:1em">Скачать</button>
                </div>
            </div>

        </div>

`

    const domparser = new DOMParser();
    const elem = domparser.parseFromString(elementText, 'text/html').getElementsByTagName('div')[0]
    console.log(elem)
    if (elem == null || !(elem instanceof HTMLDivElement)) {
        throw new Error('element is not div')

    }

    if (qualityOptions) {
        const qualitySelect = elem.getElementsByClassName('quality-selector')[0]
        console.log(qualityOptions)
        console.log(elem)
        for (const qualityname in qualityOptions) {
            const qualityId = qualityOptions[qualityname]
            const opt = document.createElement('option')
            opt.value = qualityId
            opt.text = qualityname
            console.log(qualitySelect)
            qualitySelect.appendChild(opt)
        }
    }
    return elem
}





function makeurl(url) {
    // TODO: replace with https
    return `http://${VIDEO_SERVER_ADDRESS}/download?url=${url}`
}

async function DownloadVideo(url) {
    const downloadServiceUrl = makeurl(url)

    const a = document.createElement("a")
    a.href = downloadServiceUrl

    const videoData = await getVideoDataFromYoutube(url)
    const videoTitle = videoData.title

    a.download = `${videoTitle}.mp4`
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
}



/**
 * @param {string} url 
 * @returns {Promise<{
 *                     title: string,
 *                     author_name:string,
 *                     author_url:string,
 *                     type:string,
 *                     height: int,
 *                     width:int,
 *                     version:string,
 *                     provider_name:string,
 *                     provider_url:string,
 *                     thumbnail_height: int,
 *                     thumbnail_width: int,
 *                     thumbnail_url:string,
 *                     html:string 
 * }>}
 */
export async function getVideoDataFromYoutube(url) {
    const oembedUrl = `https://www.youtube.com/oembed?url=${encodeURIComponent(url)}&format=json`;
    return (await fetch(oembedUrl)).json()
}

/**
 * 
 * @param {string} url 
 * @returns {Promise<string>}
 */
async function videoPlayerElement(url) {
    return (await getVideoDataFromYoutube(url)).html
}

/**
 * @param {string} url 
 * @returns {Promise<{title:string, resolutions:string[], thumbnail:string, sizeByQualityName:object, idByQualityName:object, audioId:string}>}
 */
export async function getVideoDataFromBackend(url) {
    // TODO: replace with https, sanitize url
    const fullurl = `http://${VIDEO_SERVER_ADDRESS}/info?url=${url}`
    return (await fetch(fullurl)).json()
}

/**
 * @param {string} url 
 * @returns {Promise<string>}
 */
async function videoPlayerFromYoutube(url) {
    return (await getVideoDataFromYoutube(url)).html
}


// Element Getters

/**
 * 
 * @returns {HTMLElement}
 */
function previewContainer() {
    const elem = document.getElementById('previewContainer')
    if (elem == null) {
        throw new Error('no preview container')
    }
    return elem
}




/**
 * 
 * @returns {HTMLInputElement}
 */
function videoURLBar() {
    const bar = document.getElementById('videoURL')
    if (bar == null || !(bar instanceof HTMLInputElement)) {
        throw new Error('no url bar')
    }
    return bar
}


/**
 * 
 * @returns {HTMLButtonElement}
 */
function downloadButton() {
    const btn = document.getElementById('download-button')

    if (btn == null || !(btn instanceof HTMLButtonElement)) {
        throw new Error('No download button')
    }
    return btn
}



const downloadHistory = new DownloadHistory('downloadHistory')


function enableDownloadbutton() {
    downloadButton().addEventListener('click',
        function () {
            const url = videoURLBar().value
            DownloadVideo(url)
            downloadHistory.addItem(url)
        }
    )
}

videoURLBar().addEventListener('input', async function () {
    const videoData = await getVideoDataFromBackend(videoURLBar().value)
    if (videoData) {
        previewContainer().innerHTML = ''
        previewContainer().appendChild(thumbnailAndDownloadControls(videoData.thumbnail, videoData.title, '', videoData.idByQualityName))
        console.log(previewContainer().firstChild)
        enableDownloadbutton()
    }
})

/**
 * @returns {HTMLHeadingElement}
 */
function previewPlaceholder() {
    const elem = document.getElementById('preview-placeholder')
    if (elem == null || !(elem instanceof HTMLHeadingElement)) {
        throw new Error('no preview placeholder')
    }
    return elem
}



