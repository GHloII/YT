export function qualityNameIdPairs(qualityIdByName) {
    return Object.entries(qualityIdByName).map(pair => ({
        name: pair[0],
        id: pair[1]
    })).sort((a, b) => parseInt(b.name) - parseInt(a.name))
}

export function videoLinkOfCurrentPreview() {
    return videoTitleLink.href
}

export const videoInfoElement = (imageLink, videoLink, videoTitle, qualityOptions, videoAuthor) => `
    <div class="video-info">
        <div class="video-preview">
            <img src="${imageLink}" class="video-preview-image" />
        </div>
        <div class="video-info-right-half">
            <div class="video-title-and-author-name">
                <a
                    class="video-title-link"
                    id="videoTitleLink"
                    href="${videoLink}"
                >
                    <h1 class="video-title">
                        ${videoTitle}
                    </h1>
                </a>
                <p class="video-author">${videoAuthor ?? ''}</p>
            </div>
            <div class="download-controls">
            ${qualityOptions && qualityOptions.length > 0 ?
        `<select class="quality-select download-control" aria-label="Качество видео">
                ${qualityOptions.map(opt =>
            `<option value="${opt.id}" ${parseInt(opt.name) == 1080 ? 'selected' : ''} > ${opt.name} </option>`
        ).join('')
        }
                </select>`
        : ''} 
            </div>
        </div>
    </div>
    `

export async function getVideoInfo(videoURL) {
    const params = new URLSearchParams({
        url: videoURL
    })
    return (await fetch(`/info?${params}`)).json()
}


export function videoIdByYoutubeUrl(url) {
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

export async function eventsSSESource(taskId) {
    const params = new URLSearchParams({ taskId })
    const source = new EventSource(`/events?${params}`)
    source.addEventListener("heartbeat", event => { console.log("💓 Heartbeat:", event.data) })
    source.addEventListener("taskUpdate", event => { console.log("✅ Task update:", event.data) })
    return source
}

export async function downloadVideo(url, audioId, videoId) {
    downloadButton.classList.add('button-loading')
    const taskId = (await (await fetch('/getDownloadID')).json()).taskId

    const SSESource = await eventsSSESource(taskId)
    SSESource.addEventListener('taskUpdate', event => {
        if (event.data == 'STREAMING') {
            downloadButton.classList.remove('button-loading')
        }
    })

    const params = new URLSearchParams({
        url, videoId, taskId, audioId
    })
    const a = document.createElement('a')
    a.href = `/download?${params}`
    a.download = ``
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
}