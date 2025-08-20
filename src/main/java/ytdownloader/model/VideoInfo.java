package ytdownloader.model;


import java.util.List;
import java.util.Map;


public record VideoInfo(
        String filename,
        String title,
        String thumbnailUrl,
        Long sizeBytes, // Добавляем обратно sizeBytes, чтобы соответствовать ожиданию компилятора
        Long durationMs,
        List<FormatDetails> videoQualities,
        List<FormatDetails> audioFormats
) {
} 
