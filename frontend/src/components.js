
function videoElement(name, previewLink, imageWidth, imageHeight) {
    const elem = document.createElement("div")
    elem.innerHTML = `
        <img src="${previewLink} width=${imageWidth} height=${imageHeight}">
        <h3>${name}</h3>
    `
    return elem
}