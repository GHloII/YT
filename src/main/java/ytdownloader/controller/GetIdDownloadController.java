package ytdownloader.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.IdGenerator;
import ytdownloader.model.DownloadTask;
import ytdownloader.model.TaskStatus;
import ytdownloader.service.TaskRedisService;

import java.util.Map;

@RestController
public class GetIdDownloadController {

    private final IdGenerator idGenerator;
    private final TaskRedisService taskRedisService;

    public GetIdDownloadController(IdGenerator idGenerator, TaskRedisService taskRedisService) {
        this.idGenerator = idGenerator;
        this.taskRedisService = taskRedisService;
    }

    @GetMapping("/getDownloadID")
    public ResponseEntity<?> getDownloadId() {
        String taskId;
        try {
            taskId = idGenerator.generateId().toString();
        } catch (Exception e) {
            System.err.println("Ошибка при генерации ID: " + e.getMessage());
            e.printStackTrace(System.err);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при генерации ID");
        }

        DownloadTask task = DownloadTask.createPending(taskId);
        try {
            taskRedisService.saveTask(task);
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении задачи в Redis: " + e.getMessage());
            e.printStackTrace(System.err);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при сохранении задачи");
        }

        System.out.println("Создана задача " + taskId +
                " со статусом " + taskRedisService.getTaskStatus(taskId));

        return ResponseEntity.ok(Map.of("taskId", taskId));
    }
}

// вызывает что то что связано с редис и передает туда айди с флагом еще не дали загрузку
// потом уже в контроллере скачаивания
