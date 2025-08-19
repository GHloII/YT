package ytdownloader.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ytdownloader.service.DownloadService;
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
    public void downloadVideo(
            @RequestParam String url,
            HttpServletResponse response
    ) throws IOException {

        VideoInfo videoInfo = null;
        try {
            videoInfo = videoInfoService.getVideoInfo(url);
        } catch(IOException e){
            // Более специфичная обработка ошибок, если нужно
            if (!e.getMessage().contains("Broken pipe")) {
                throw e;
            }
        }

        // TODO: Использовать videoInfo для динамического определения Content-Type и filename
        // Например:
        // if (videoInfo != null) {
        //     response.setHeader("Content-Disposition", "attachment; filename=\"" + videoInfo.filename() + ".mp4\"");
        // }

        // Динамическое определение Content-Type
        response.setContentType("video/mp4");
        response.setHeader("Content-Disposition", "attachment; filename=\"video.mp4\"");

        // Добавляем заголовки для потоковой передачи fMP4
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        // Потоковая передача
        try {
            downloadService.streamVideo(url, response.getOutputStream());
        } catch (IOException e) {
            if (!e.getMessage().contains("Broken pipe")) {
                throw e;
            }
            // Логировать разрыв соединения (не критичная ошибка)
        }
    }
}