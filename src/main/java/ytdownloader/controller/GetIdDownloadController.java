package ytdownloader.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.IdGenerator;
import ytdownloader.model.DownloadTask;
import ytdownloader.model.TaskStatus;
import ytdownloader.service.TaskRedisService;

@RestController
public class GetIdDownloadController {

    private final IdGenerator idGenerator;
    private final TaskRedisService taskRedisService;

    public GetIdDownloadController(IdGenerator idGenerator, TaskRedisService taskRedisService) {
        this.idGenerator = idGenerator;
        this.taskRedisService = taskRedisService;
    }

    @GetMapping("/getDownloadID")
    public ResponseEntity<String> getDownloadId() {
        String taskId = idGenerator.generateId().toString();
        
        // Создаем задачу со статусом PENDING (без URL)
        DownloadTask task = DownloadTask.createPending(taskId);
        
        // Сохраняем в Redis
        taskRedisService.saveTask(task);
        
        return ResponseEntity.ok(taskId);
    }
}
// вызывает что то что связано с редис и передает туда айди с флагом еще не дали загрузку
// потом уже в контроллере скачаивания
