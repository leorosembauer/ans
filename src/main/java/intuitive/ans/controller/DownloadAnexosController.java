package intuitive.ans.controller;

import intuitive.ans.service.DownloadAndZipAnexosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/anexos")
public class DownloadAnexosController {

    private final DownloadAndZipAnexosService downloadService;

    public DownloadAnexosController(DownloadAndZipAnexosService downloadService) {
        this.downloadService = downloadService;
    }

    @GetMapping("/zip-download")
    public ResponseEntity<String> baixarAnexos() {
        String mensagem = downloadService.baixarECompactarAnexos();
        return ResponseEntity.ok(mensagem);
    }
}
