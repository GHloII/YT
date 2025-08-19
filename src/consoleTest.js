const VIDEO_SERVER_ADDRESS = '89.208.96.231'

class DownloadHistory {
    history = []
    historyLocalStorageName
    constructor(storageName) {
        this.historyLocalStorageName = storageName
        const historyFromLocalStorage = JSON.parse(localStorage.getItem(this.historyLocalStorageName))
        this.history = historyFromLocalStorage != null ? historyFromLocalStorage : []
    }

    addItem(item) {
        this.history.push(item)
        this.saveToLocalStorage()
    }

    saveToLocalStorage() {
        localStorage.setItem(this.historyLocalStorageName, JSON.stringify(this.history))
    }
}

const downloadHistory = new DownloadHistory("downloadHistory")

async function getVideoDataFromYoutube(url) {
    const oembedUrl = `https://www.youtube.com/oembed?url=${encodeURIComponent(url)}&format=json`;
    return (await fetch(oembedUrl)).json()
}

function makeurl(url) {
    return `http://${VIDEO_SERVER_ADDRESS}/download?url=${url}`
}
function downloadBlob(blob, filename) {
  const reader = new FileReader();
  reader.onload = function () {
    const a = document.createElement("a");
    a.href = reader.result; // data URL
    a.download = filename;
    a.click();
  };
  reader.readAsDataURL(blob);
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

DownloadVideo("https://youtu.be/vUncEiyXIT4?si=t0vyG1ki9yHBRI1X", downloadHistory)