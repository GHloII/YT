function optionFromValueAndLabel(value, label) {
    return `<option value=${value}>${label}</option>`
}

function qualityNameIdPairs(qualityIdByName) {
    return Object.entries(qualityIdByName).map(pair => ({
        name: pair[0],
        id: pair[1]
    }))
}

function thisOptionShouldBeSelectedAmongQualityOptions(qualityOptions, option) {
    if (qualityOptions.map(qualityOption => qualityOption.name).includes('1080p')) {
        if (option.name == '1080p') {
            return true
        }
        return false
    }
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
                <p class="video-author">${videoAuthor ?? ''}</p>
            </div>
            <div class="download-controls">
            ${
                qualityOptions && qualityOptions.length > 0 ?
                `<select class="quality-select download-control" aria-label="Качество видео">
                    ${
                        qualityOptions.map(opt => `<option value="${opt.id}" ${ thisOptionShouldBeSelectedAmongQualityOptions(qualityOptions, opt) ? 'selected' : ''}
                        > ${opt.name} </option>`).join('')
                    }
                </select>`
            : ''} 
                <button class="download-control download-button" onclick="downloadVideo()">
                    Скачать
                </button>
            </div>
        </div>
    </div>
    `

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

async function eventsSSESource(taskId) {
    const params = new URLSearchParams({ taskId })
    const source = new EventSource(`/events?${params}`)
    source.addEventListener("heartbeat", event => { console.log("💓 Heartbeat:", event.data) })
    source.addEventListener("taskUpdate", event => { console.log("✅ Task update:", event.data) })
    return source
}

async function downloadVideo() {
    const taskId = (await (await fetch('/getDownloadID')).json()).taskId

    const SSESource = await eventsSSESource(taskId)

    const params = new URLSearchParams({
        url: mainVideoLinkInput.value,
        videoId: qualitySelect().value,
        taskId: taskId,
        audioId: 'bestaudio'
    })
    const a = document.createElement('a')
    a.href = `/download?${params}`
    a.download = ``
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
}


mainVideoLinkInput.addEventListener('input', () => {
    const videoLink = mainVideoLinkInput.value.trim()
    videoInfoContainer.innerHTML = `<div class='skeleton'></div>`
    getVideoInfo(videoLink).then((res) => {
        const { thumbnail, resolutions, title, idByQualityName } = res
        if (thumbnail == undefined || resolutions == undefined || title == undefined || idByQualityName == undefined) {
            if (videoLink == '') {
                videoInfoContainer.innerHTML = ''
            }
            else {
                videoInfoContainer.innerHTML = `<h1>Ничего неизвестно про видео по этой ссылке</h1>`
                throw new Error('no video info for this url')
            }
        }
        videoInfoContainer.innerHTML = videoInfoElement(thumbnail, videoLink, title, qualityNameIdPairs(idByQualityName))
    })
})
