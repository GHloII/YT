function optionFromValueAndLabel(value,label) {
    return `<option value=${value}>${label}</option>`
}

function qualityNameIdPairs(qualityIdByName) {
    return Object.entries(qualityIdByName).map(pair => ({
        name: pair[0],
        id: pair[1]
    }))
}


const videoInfoElement = (imageLink, videoLink, videoTitle, qualityOptions, videoAuthor) => `
    <div class="video-info">
        <div class="video-preview">
            <img src="${imageLink}" class="video-preview-image" />
        </div>
        <div class="video-info-right-half">
            <div class="video-title-and-author-name">
                <a
                    class="video-title-link"
                    href="${videoLink}"
                >
                    <h1 class="video-title">
                        ${videoTitle}
                    </h1>
                </a>
                <p class="video-author">${videoAuthor}</p>
            </div>
            <div class="download-controls">
                <select class="quality-select download-control">
                ${qualityOptions.map(opt => optionFromValueAndLabel(opt.id, opt.name))}
                </select>
                <button class="download-control download-button" onclick="downloadVideo()">
                    Скачать
                </button>
            </div>
        </div>
    </div>
    `

const mainVideoLinkInput = document.getElementById('main-video-link-input')
const videoInfoContainer = document.getElementById('video-info-container')
const qualitySelect = () => document.getElementsByClassName('quality-select')[0]

async function getVideoInfo(videoURL) {
    const params = new URLSearchParams({
        url: videoURL
    })
    return (await fetch(`/info?${params}`)).json()
}


function videoIdByYoutubeUrl(url) {
    const parsed = new URL(url)

    const hostname = parsed.hostname.replace(/^www\./, '');
    if (!(hostname === 'youtube.com' || hostname === 'youtu.be' || hostname == 'm.youtube.com')) {
        return null
    }

    if (parsed.pathname == '/watch' && parsed.searchParams.has('v')) {
        return parsed.searchParams.get('v')
    }
    return parsed.pathname.slice(1)
    
}

function videoInfo(youtubeVideoId) {
}

function currentLinkInInput() {
    return mainVideoLinkInput.value
}

async function downloadVideo() {
    const taskId = (await (await fetch('/getDownloadID')).json()).taskId
    const params = new URLSearchParams({
        url: mainVideoLinkInput.value,
        videoId: qualitySelect().value,
        taskId:taskId,
        audioId:'bestaudio'
    })
    const a = document.createElement('a')
    a.href = `/download?${params}`
    console.log(a.href)
    a.download = ``
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
}


mainVideoLinkInput.addEventListener('input', () => {
    const videoLink = mainVideoLinkInput.value
    getVideoInfo(videoLink).then((res) => {
        const {thumbnail, resolutions, title, idByQualityName} = res
        if (thumbnail == undefined || resolutions == undefined || title == undefined || idByQualityName == undefined) {
            throw new Error('no for this url')
        }
        videoInfoContainer.innerHTML = videoInfoElement(thumbnail, videoLink, title, qualityNameIdPairs(idByQualityName))
    })
})
