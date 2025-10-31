package ytdownloader.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ytdownloader.model.TaskStatus;
import ytdownloader.service.TaskRedisService;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
public class TaskInfoSSEController {
    private final TaskRedisService taskRedisService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    public TaskInfoSSEController(TaskRedisService taskRedisService) {
        this.taskRedisService = taskRedisService;
    }

    @GetMapping("/events")
    public SseEmitter stream(@RequestParam String taskId) {
        SseEmitter emitter = new SseEmitter(0L); // 0L = бесконечно живое соединение

        // Закрываем scheduler, когда клиент отключается
        //emitter.onCompletion(scheduler::shutdown);
        //emitter.onTimeout(scheduler::shutdown);
        //emitter.onError(e -> scheduler.shutdownNow());

        // 1️⃣ Heartbeat каждые 5 секунд
        scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }, 0, 8, TimeUnit.SECONDS);

        // 2️⃣ Проверка статуса задачи каждые 15 секунд
        scheduler.scheduleAtFixedRate(() -> {
            try {
                TaskStatus status = taskRedisService.getTaskStatus(taskId);
                if (status == TaskStatus.STREAMING) {
                    emitter.send(SseEmitter.event().name("taskUpdate").data("STREAMING"));
                }
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }, 2, 2, TimeUnit.SECONDS);

        return emitter;
    }
}