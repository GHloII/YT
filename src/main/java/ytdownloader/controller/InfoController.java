package ytdownloader.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ytdownloader.service.VideoInfoService;
import ytdownloader.model.VideoInfo;
import ytdownloader.model.VideoInfoResponse; // Добавляем импорт
import ytdownloader.model.FormatDetails;    // Добавляем импорт

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController 
public class InfoController {

    private final VideoInfoService videoInfoService;

    public InfoController(VideoInfoService videoInfoService) {
        this.videoInfoService = videoInfoService;
    }

    @GetMapping("/info")
    public VideoInfoResponse getInfo(@RequestParam String url) {
        try {
            VideoInfo videoInfo = videoInfoService.getVideoInfo(url);

            // Логика преобразования VideoInfo в VideoInfoResponse
            List<String> resolutions = new ArrayList<>();
            Map<String, Long> sizeByQualityName = new LinkedHashMap<>(); // Сохраняем порядок
            Map<String, String> idByQualityName = new LinkedHashMap<>(); // Сохраняем порядок

            // Порядок предпочтения видеокодеков
            List<String> videoCodecPreference = Arrays.asList("avc1", "vp9", "av1");

            // Группируем видеоформаты по разрешению
            Map<String, List<FormatDetails>> videoFormatsByResolution = videoInfo.videoQualities().stream()
                    .collect(Collectors.groupingBy(FormatDetails::resolution));

            // Для каждого разрешения выбираем формат по предпочтительному кодеку
            videoFormatsByResolution.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparingInt(s -> Integer.parseInt(s.replace("p", ""))))) // Сортируем по разрешению
                    .forEach(entry -> {
                        String resolution = entry.getKey();
                        List<FormatDetails> formatsForResolution = entry.getValue();

                        // Находим лучший формат для данного разрешения согласно предпочтениям кодеков
                        FormatDetails bestFormat = null;
                        for (String preferredCodec : videoCodecPreference) {
                            for (FormatDetails format : formatsForResolution) {
                                if (format.vcodec() != null && format.vcodec().equals(preferredCodec)) {
                                    bestFormat = format;
                                    break;
                                }
                            }
                            if (bestFormat != null) {
                                break;
                            }
                        }
                        // Если не найден предпочтительный кодек, берем первый попавшийся
                        if (bestFormat == null && !formatsForResolution.isEmpty()) {
                            bestFormat = formatsForResolution.get(0);
                        }

                        if (bestFormat != null) {
                            resolutions.add(resolution);
                            if (bestFormat.filesize() != null) {
                                sizeByQualityName.put(resolution, bestFormat.filesize());
                            }
                            idByQualityName.put(resolution, bestFormat.formatId());
                        }
                    });

            // Выбираем лучший аудиоформат (первый доступный) и парсим ID в int
            int audioId = 0; // Значение по умолчанию
            if (!videoInfo.audioFormats().isEmpty()) {
                FormatDetails bestAudioFormat = videoInfo.audioFormats().get(0);
                try {
                    audioId = Integer.parseInt(bestAudioFormat.formatId());
                } catch (NumberFormatException e) {
                    System.err.println("Ошибка парсинга audioId: " + e.getMessage());
                }
            }

            // Создаем и возвращаем VideoInfoResponse
            VideoInfoResponse response = new VideoInfoResponse(
                    videoInfo.title(),
                    resolutions,
                    videoInfo.thumbnailUrl(),
                    sizeByQualityName,
                    idByQualityName,
                    audioId
            );
            
            // Выводим информацию о видео в логи Java (для отладки)
            System.out.println("Получена информация о видео: " + videoInfo.toString());
            System.out.println("Сформирован ответ: " + response.toString());

            return response;

        } catch (IOException e) {
            System.err.println("Ошибка при получении информации о видео: " + e.getMessage());
            // В случае ошибки выбрасываем RuntimeException, который будет обработан Spring Boot
            throw new RuntimeException("Ошибка при получении информации о видео", e);
        }
    }
}
