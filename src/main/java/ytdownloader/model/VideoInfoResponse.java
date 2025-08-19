package ytdownloader.model;

import java.util.List;
import java.util.Map;

/**
 * Класс-запись (DTO) для формирования JSON-ответа с информацией о видео.
 */
public record VideoInfoResponse(
    String title,
    List<String> resolutions,
    String thumbnail,
    Map<String, Long> sizeByQualityName,
    Map<String, String> idByQualityName,
    int audioId
) {
}
