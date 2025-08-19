
function makeurl(url) {
    return `http://${VIDEO_SERVER_ADDRESS}/download?url=${url}`
}
async function DownloadVideo(url, history) {
    const downloadServiceUrl = makeurl(url)

    history.addItem(url)
    const a = document.createElement("a")
    a.href = downloadServiceUrl

    const videoData = await getVideoDataFromYoutube(url)
    const videoTitle = videoData.title

    a.download = `${videoTitle}.mp4`
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
}

async function DownloadWrapper(url, history) {
    console.log('starting download')
    await DownloadVideo(url,history)
    console.log('download started')
}