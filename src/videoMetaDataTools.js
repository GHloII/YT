async function getVideoDataFromYoutube(url) {
    const oembedUrl = `https://www.youtube.com/oembed?url=${encodeURIComponent(url)}&format=json`;
    return (await fetch(oembedUrl)).json()
}
