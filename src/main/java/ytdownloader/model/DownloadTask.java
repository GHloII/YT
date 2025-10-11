package ytdownloader.model;

public record DownloadTask( 
    String id,
    String url,        // может быть null для PENDING
    TaskStatus status
) {

    public DownloadTask(String id, String url, TaskStatus status) {
        if (url == null) {
            url = "NO_URL";
        }
        this.id = id;
        this.url = url;
        this.status = status;
    }

    // Создаем новую задачу со статусом PENDING (без URL)
    public static DownloadTask createPending(String id) {
        return new DownloadTask(id, null, TaskStatus.PENDING);
    }
    
    // Создаем задачу с URL
    public static DownloadTask createPending(String id, String url, TaskStatus status) {
        return new DownloadTask(id, url, status);
    }
}
