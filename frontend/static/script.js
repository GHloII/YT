
const videoInfoElement = (imageLink, videoLink, videoTitle, videoAuthor, qualityOptions) => `
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
                    <option>1080p</option>
                    <option>360p</option>
                </select>
                <button class="download-control download-button">
                    Скачать
                </button>
            </div>
        </div>
    </div>
    `

const mainVideoLinkInput = document.getElementById('main-video-link-input')

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
