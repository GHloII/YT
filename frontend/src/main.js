// @ts-check
import { VIDEO_SERVER_ADDRESS } from "./config.js";
const logoPreviewElement = document.createElement("h1")
logoPreviewElement.innerText = "Видео с Ютуба в файл"

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



async function thumbnailElemenet(previewLink, imageWidth, imageHeight) {
    return `<img src="${previewLink} width=${imageWidth} height=${imageHeight}">`
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
    const btn = document.getElementById('downloadButton')
    
    if (btn == null || !(btn instanceof HTMLButtonElement)) {
        throw new Error('No download button')
    }
    return btn
}



const downloadHistory = new DownloadHistory('downloadHistory')


downloadButton().addEventListener('click',
    function () {
        const url = videoURLBar().value
        DownloadVideo(url)
        downloadHistory.addItem(url)
    }
)