package ytdownloader.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ytdownloader.model.DownloadTask;
import ytdownloader.model.TaskStatus;

@Service
public class TaskRedisService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private static final String TASK_PREFIX = "task:";
    
    public TaskRedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    // Сохраняем задачу в Hash
    private void saveTask(String taskId, String url, TaskStatus status) {
        String taskKey = TASK_PREFIX + taskId;

        redisTemplate.opsForHash().put(taskKey, "url", url);
        redisTemplate.opsForHash().put(taskKey, "status", status.getValue());
    }
    
    // Сохраняем задачу из record
    public void saveTask(DownloadTask task) {
        saveTask(task.id(), task.url(), task.status());
    }
    
    // Получаем полную задачу из Hash
    public DownloadTask getTask(String taskId) {
        String taskKey = TASK_PREFIX + taskId;
        if (!taskExists(taskId)) {
            return null;
        }
        String url = (String) redisTemplate.opsForHash().get(taskKey, "url");
        String statusValue = (String) redisTemplate.opsForHash().get(taskKey, "status");
        
        TaskStatus status = TaskStatus.fromString(statusValue);
        return new DownloadTask(taskId, url, status);
    }
    
    // Обновляем статус задачи
    public void updateTaskStatus(String taskId, TaskStatus status) {
        String taskKey = TASK_PREFIX + taskId;
        redisTemplate.opsForHash().put(taskKey, "status", status.getValue());
    }

    public void updateTaskStatus(DownloadTask task) {
        String taskKey = TASK_PREFIX + task.id();
        redisTemplate.opsForHash().put(taskKey, "status", task.status().getValue());
    }

    public void updateTaskStatus(DownloadTask task,TaskStatus status) {
        String taskKey = TASK_PREFIX + task.id();
        redisTemplate.opsForHash().put(taskKey, "status", status.getValue());
    }

    
    // Получаем только статус задачи
    public TaskStatus getTaskStatus(String taskId) {
        String taskKey = TASK_PREFIX + taskId;
        String statusValue = (String) redisTemplate.opsForHash().get(taskKey, "status");
        
        return statusValue != null ? TaskStatus.fromString(statusValue) : null;
    }
    
    // Получаем только URL задачи
    public String getTaskUrl(String taskId) {
        String taskKey = TASK_PREFIX + taskId;
        return (String) redisTemplate.opsForHash().get(taskKey, "url");
    }
    
    // Удаляем задачу
    public void deleteTask(String taskId) {
        String taskKey = TASK_PREFIX + taskId;
        redisTemplate.delete(taskKey);
    }
    
    // Проверяем существование задачи
    public boolean taskExists(String taskId) {
        String taskKey = TASK_PREFIX + taskId;
        return redisTemplate.hasKey(taskKey);
    }
}
