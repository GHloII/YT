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