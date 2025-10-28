package ytdownloader.service;

import org.springframework.stereotype.Service;
import ytdownloader.model.VideoInfo;
import ytdownloader.model.FormatDetails; // Добавляем импорт для FormatDetails

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.StringJoiner;

@Service 
public class VideoInfoService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public VideoInfo getVideoInfo(String url) throws IOException {
        Process process = null; // Объявляем process здесь (не final)
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "yt-dlp",
                    "--dump-json", // Получение чистого JSON без отладочного вывода
                    url
            );
            // Не смешиваем потоки: stderr отдельно от stdout

            process = builder.start(); // Инициализируем process
            // Читаем stdout (JSON) полностью
            String jsonOutput;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append('\n');
                }
                jsonOutput = output.toString();
            }
            // Читаем stderr полностью
            String errorText;
            try (BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder err = new StringBuilder();
                String eline;
                while ((eline = errReader.readLine()) != null) {
                    err.append(eline).append('\n');
                }
                errorText = err.toString();
            }

            int exitCode = 0;
            try {
                exitCode = process.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Процесс yt-dlp был прерван при ожидании.", e);
            }

            if (exitCode != 0) {
                String errSnippet = errorText.length() > 4000 ? errorText.substring(0, 4000) + "..." : errorText;
                throw new IOException("yt-dlp завершился с ошибкой, код: " + exitCode + ". stderr:\n" + errSnippet);
            }

            // Парсим JSON вывод yt-dlp
            String trimmed = jsonOutput.trim();
            if (!trimmed.startsWith("{")) {
                String outSnippet = trimmed.length() > 4000 ? trimmed.substring(0, 4000) + "..." : trimmed;
                throw new IOException("yt-dlp не вернул JSON. stdout:\n" + outSnippet);
            }
            JsonNode jsonNode = objectMapper.readTree(trimmed);

            // Извлекаем данные и создаем объект VideoInfo
            String filename = jsonNode.has("id") ? jsonNode.get("id").asText() : "unknown";
            String title = jsonNode.has("title") ? jsonNode.get("title").asText() : "Unknown Title";
            String thumbnailUrl = jsonNode.has("thumbnail") ? jsonNode.get("thumbnail").asText() : null;
            // Удаляем объявление sizeBytes, так как оно больше не используется
            Long durationMs = jsonNode.has("duration") ? (long) (jsonNode.get("duration").asDouble() * 1000) : null;

            List<FormatDetails> videoQualities = new ArrayList<>();
            if (jsonNode.has("formats")) {
                for (JsonNode format : jsonNode.get("formats")) {
                    if (format.has("vcodec") && !format.get("vcodec").asText().equals("none") && format.has("format_id") && format.has("height")) {
                        Long formatFilesize = null;
                        if (format.has("filesize") && format.get("filesize").isIntegralNumber()) {
                            formatFilesize = format.get("filesize").asLong();
                        } else if (format.has("filesize_approx") && format.get("filesize_approx").isIntegralNumber()) {
                            formatFilesize = format.get("filesize_approx").asLong();
                        }
                        videoQualities.add(new FormatDetails(format.get("format_id").asText(), format.get("height").asText() + "p", null, format.has("vcodec") ? format.get("vcodec").asText() : null, null, formatFilesize));
                    }
                }
            }

            List<FormatDetails> audioFormats = new ArrayList<>();
            if (jsonNode.has("formats")) {
                for (JsonNode format : jsonNode.get("formats")) {
                    if (format.has("acodec") && !format.get("acodec").asText().equals("none") && format.has("format_id") && format.has("ext")) {
                        Long formatFilesize = null;
                        if (format.has("filesize") && format.get("filesize").isIntegralNumber()) {
                            formatFilesize = format.get("filesize").asLong();
                        } else if (format.has("filesize_approx") && format.get("filesize_approx").isIntegralNumber()) {
                            formatFilesize = format.get("filesize_approx").asLong();
                        }
                        audioFormats.add(new FormatDetails(format.get("format_id").asText(), null, format.get("ext").asText(), null, format.has("acodec") ? format.get("acodec").asText() : null, formatFilesize));
                    }
                }
            }
            
            return new VideoInfo(filename, title, thumbnailUrl, null, durationMs, videoQualities, audioFormats);

        } finally {
            if (process != null && process.isAlive()) { // process доступна здесь
                destroyProcess(process);
            }
        }
    }

    private void destroyProcess(Process process) {
        try {
            process.destroy();
            if (!process.waitFor(2, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                process.waitFor(1, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
        }
    }
}
