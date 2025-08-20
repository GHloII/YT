package ytdownloader.model;


import java.util.List;



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
