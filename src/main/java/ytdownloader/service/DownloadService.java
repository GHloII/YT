package ytdownloader.service;

import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class DownloadService {

    public void streamVideo(String url, OutputStream output) throws IOException {
        Process process = null;
        try {

            ProcessBuilder builder = new ProcessBuilder(
                    "yt-dlp",
                    "--quiet",
                    "--no-progress",
                    "-f", "bestvideo+bestaudio/best",
                    "--remux-video", "mp4",  // Важно оставить ремуксинг
                    "--downloader", "ffmpeg",
                    "--downloader-args", "ffmpeg:-movflags frag_keyframe+empty_moov -f mp4 -c copy -map 0 -strict experimental -max_muxing_queue_size 9999",
                    "--no-part",
                    "--no-cache-dir",
                    "-o", "-", // Снова стримим в stdout
                    url
            );



            process = builder.start(); // Сначала запускаем процесс

            // Создаем финальную копию process для использования в лямбде
            final Process finalProcessForStderr = process;

            // Отдельный поток для чтения stderr, чтобы избежать блокировки буфера
            // Это не выводит ошибки в HTTP-ответ, но предотвращает зависание процесса
            new Thread(() -> {
                try (InputStream errorStream = finalProcessForStderr.getErrorStream()) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = errorStream.read(buffer)) != -1) {
                        // Выводим содержимое stderr в System.err для отладки
                        System.err.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    // Логировать ошибку чтения stderr, если необходимо
                    System.err.println("Ошибка чтения stderr: " + e.getMessage());
                }
            }).start();

            // Читаем из stdout yt-dlp и стримим в output
            try (InputStream processOut = process.getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = processOut.read(buffer)) != -1) {
                    try {
                        output.write(buffer, 0, bytesRead);
                        output.flush();
                    } catch (IOException e) {
                        // Обработка разрыва соединения
                        throw e;
                    }
                }
            }
            // Проверяем код завершения yt-dlp после того, как все данные прочитаны
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("yt-dlp завершился с ошибкой, код: " + exitCode);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Процесс yt-dlp был прерван.", e);
        } finally {

            if (process != null && process.isAlive()) {
                destroyProcess(process);
            }
        }
    }

    private void destroyProcess(Process process) {
        try {
            // Мягкое завершение
            process.destroy();

            // Ожидание завершения
            if (!process.waitFor(2, TimeUnit.SECONDS)) {
                // Принудительное завершение
                process.destroyForcibly();
                process.waitFor(1, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
        }
    }
}
