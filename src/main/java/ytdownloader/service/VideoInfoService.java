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
                    "--dump-json", // Изменяем на dump-json для получения метаданных
                    url
            );

            process = builder.start(); // Инициализируем process
            final Process lambdaProcess = process; // Создаем финальную копию для лямбды
            // Читаем stdout yt-dlp для получения JSON
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }

            // Отдельный поток для чтения stderr, чтобы избежать блокировки буфера
            final StringJoiner errorOutput = new StringJoiner("\n"); // Используем StringJoiner
            new Thread(() -> {
                try (InputStream errorStream = lambdaProcess.getErrorStream()) { // Используем lambdaProcess
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorOutput.add(errorLine); // Добавляем строку в StringJoiner
                    }
                } catch (IOException e) {
                    System.err.println("Ошибка чтения stderr: " + e.getMessage());
                }
            }).start();

            int exitCode = 0;
            try {
                exitCode = process.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Процесс yt-dlp был прерван при ожидании.", e);
            }

            if (exitCode != 0) {
                throw new IOException("yt-dlp завершился с ошибкой, код: " + exitCode + ". Ошибка: " + errorOutput.toString()); // Читаем из StringJoiner
            }

            // Парсим JSON вывод yt-dlp
            JsonNode jsonNode = objectMapper.readTree(output.toString());

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
