package ytdownloader.model;

public class VideoInfo {
    private final String filename;
    private final Long sizeBytes;

    public VideoInfo(String filename, Long sizeBytes) {
        this.filename = filename;
        this.sizeBytes = sizeBytes;
    }

    public String getFilename() {
        return filename;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }
}


