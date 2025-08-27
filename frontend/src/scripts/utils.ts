import {VIDEO_SERVER_ADDRESS} from '@/config'
import type {videoDataFromBackend} from '@/objects/videoDataFromTheServer'
import type {videoDataFromYoutube} from '@/objects/videoDataFromYoutube'

export function makeurl(url: string) {
    // TODO: replace with https
    return `http://${VIDEO_SERVER_ADDRESS}/download?url=${url}`
}

export function makeurlWithQuality(
    url: string,
    videoQualityId: string,
    audioQualityId: string,
    size: number,
) {
    return (
        `http://${VIDEO_SERVER_ADDRESS}/download?url=${url}&videoId=${videoQualityId}&audioId=${audioQualityId}` +
        `&size=${size}`
    )
}

export async function getVideoDataFromYoutube(url: string): Promise<videoDataFromYoutube> {
    const oembedUrl = `https://www.youtube.com/oembed?url=${encodeURIComponent(url)}&format=json`
    return (await fetch(oembedUrl)).json()
}

export async function videoPlayerElement(url: string) {
    return (await getVideoDataFromYoutube(url)).html
}

export async function getVideoDataFromBackend(url: string): Promise<videoDataFromBackend> {
    // TODO: replace with https, sanitize url
    const fullurl = `http://${VIDEO_SERVER_ADDRESS}/info?url=${url}`
    const data = (await fetch(fullurl)).json()
    return data
}

export async function DownloadVideo(url: string | URL, videoTitle: string, size: number) {

    // const downloadServiceUrl = videoQualityId? makeurlWithQuality(url,videoQualityId, 'bestaudio'): makeurlWithQuality(url,'bestvideo', 'bestaudio')
    const downloadServiceUrl = makeurlWithQuality(url.toString(), 'bestvideo', 'bestaudio', size)
    // const downloadServiceUrl = makeurl(url)

    const a = document.createElement('a')
    a.href = downloadServiceUrl

    a.download = `${videoTitle}.mp4`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
}


export function youTubeVideoId(url: string | URL): string | null {
    try {
        const u = new URL(url);

        if (u.hostname === "youtu.be") {
            return u.pathname.slice(1);
        }

        if (u.hostname.includes("youtube.com") || u.hostname.includes("youtube-nocookie.com")) {
            return u.searchParams.get("v");
        }

        return null;
    } catch {
        return null;
    }
}
