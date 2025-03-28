package intuitive.ans.controller;

import intuitive.ans.service.PdfToCsvConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    private final PdfToCsvConverter pdfToCsvConverter;

    public PdfController(PdfToCsvConverter pdfToCsvConverter) {
        this.pdfToCsvConverter = pdfToCsvConverter;
    }

    @GetMapping("/convert")
    public String convertPdfToCsv() {
        return pdfToCsvConverter.extractPdfToCsv();
    }
}
