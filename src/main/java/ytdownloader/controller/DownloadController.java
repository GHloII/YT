package ytdownloader.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ytdownloader.service.DownloadService;
import ytdownloader.service.UrlValidator;
import ytdownloader.service.VideoInfoService;
import ytdownloader.model.VideoInfo;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class DownloadController {

    private final DownloadService downloadService;
    private final VideoInfoService videoInfoService;
    // private VideoInfo video;

    public DownloadController(DownloadService downloadService, VideoInfoService videoInfoService) {
        this.downloadService = downloadService;
        this.videoInfoService = videoInfoService;
    }

    @GetMapping("/download")
    public ResponseEntity<String> downloadVideo(
            @RequestParam String url,
            @RequestParam String videoId, // Добавляем videoId
            @RequestParam String audioId, // Добавляем audioId
            HttpServletResponse response
    ) throws IOException {

        if (url.isEmpty()) {
            return ResponseEntity.badRequest().body("URL is empty");
        }

        if (!UrlValidator.isTrusted(url)){
            return ResponseEntity.badRequest().body("URL is not trusted");
        }

        if (audioId.isEmpty() && videoId.isEmpty()){
            audioId = "bestaudio";
            videoId = "bestvideo";
        }
        if (audioId == null || audioId.isEmpty()){
            return ResponseEntity.badRequest().body("audioId == null or audioId.isEmpty");
        }


        // Динамическое определение Content-Type
        response.setContentType("video/mp4");
        response.setHeader("Content-Disposition", "attachment; filename=\"video.mp4\"");

        // Добавляем заголовки длsя потоковой передачи fMP4
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        // Потоковая передача
        try {
            downloadService.streamVideo(url, videoId, audioId, response.getOutputStream()); // Передаем ID форматов
            return ResponseEntity.ok("well cum.");
        } catch (IOException e) {
            if (!e.getMessage().contains("Broken pipe")) {
                System.err.println("IOException "+ e);
                return ResponseEntity.badRequest().body("Broken pipe");
            }
            // Логировать разрыв соединения (не критичная ошибка)
        }
        return null;
    }
}