export class DownloadHistory {
    history: string[] = []
    historyLocalStorageName: string
    constructor(storageName: string) {
        this.historyLocalStorageName = storageName
        const historyFromLocalStorage = JSON.parse(
            localStorage.getItem(this.historyLocalStorageName) || '',
        )
        this.history = historyFromLocalStorage != null ? historyFromLocalStorage : []
    }

    addItem(item: string) {
        this.history.push(item)
        this.saveToLocalStorage()
    }

    saveToLocalStorage() {
        localStorage.setItem(this.historyLocalStorageName, JSON.stringify(this.history))
    }
    clear() {
        this.history = []
        this.saveToLocalStorage()
    }
}
