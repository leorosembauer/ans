package intuitive.ans.controller;

import intuitive.ans.service.WebScrapingProcessorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/processo")
public class WebScrapingController {

    private final WebScrapingProcessorService processorService;

    public WebScrapingController(WebScrapingProcessorService processorService) {
        this.processorService = processorService;
    }

    @GetMapping("/executar")
    public String executarProcessoCompleto() {
        return processorService.executarProcessoCompleto();
    }
}
