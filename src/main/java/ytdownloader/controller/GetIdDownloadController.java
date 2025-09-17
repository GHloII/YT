package ytdownloader.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.util.IdGenerator;



@RestController
public class GetIdDownloadController {

    private final IdGenerator idGenerator;

    public GetIdDownloadController(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @GetMapping("/getDownloadID")
    public ResponseEntity<String> downloadVideo(
            HttpServletResponse response
    ) throws IOException {

        String id = idGenerator.generateId().toString();
        return ResponseEntity.ok(id);
    }
}
// вызывает что то что связано с редис и передает туда айди с флагом еще не дали загрузку
// потом уже в контроллере скачаивания