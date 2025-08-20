package ytdownloader.model;


public record FormatDetails(
    String formatId,
    String resolution, // Для видеоформатов (например, "1080p")
    String audioExt,   // Для аудиоформатов (например, "m4a", "opus")
    String vcodec,     // Видеокодек (например, "avc1", "vp9")
    String acodec,     // Аудиокодек (например, "mp4a.40.2", "opus")
    Long filesize      // Размер файла в байтах, если доступен
) {
}
