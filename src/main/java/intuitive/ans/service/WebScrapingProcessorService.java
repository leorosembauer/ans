package intuitive.ans.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WebScrapingProcessorService {

    private final DownloadAndZipAnexosService downloadService;
    private final PdfToCsvConverter pdfToCsvConverter;
    private final FileZipper fileZipper;

    private static final String FINAL_ZIP_PATH = "downloads/final_package.zip";
    private static final String CSV_FILE_PATH = "downloads/final_data.csv";
    private static final String PDF_ANEXO_I_PATH = "downloads/Anexo I.pdf";
    private static final String PDF_ANEXO_II_PATH = "downloads/Anexo II.pdf";

    public WebScrapingProcessorService(DownloadAndZipAnexosService downloadService,
                                       PdfToCsvConverter pdfToCsvConverter,
                                       FileZipper fileZipper) {
        this.downloadService = downloadService;
        this.pdfToCsvConverter = pdfToCsvConverter;
        this.fileZipper = fileZipper;
    }

    /**
     * Executa o fluxo completo:
     * 1. Baixa os arquivos PDF da ANS
     * 2. Converte o Anexo I para CSV
     * 3. Compacta os arquivos baixados + CSV final em um ZIP
     * @return Mensagem de status da execução
     */
    public String executarProcessoCompleto() {
        try {
            // 1️⃣ Baixar os anexos
            String resultadoDownload = downloadService.baixarECompactarAnexos();
            System.out.println(resultadoDownload);
            if (resultadoDownload.contains("❌")) {
                return "❌ Falha ao baixar os arquivos, processo interrompido.";
            }

            // 2️⃣ Converter o PDF (Anexo I) para CSV
            String resultadoConversao = pdfToCsvConverter.extractPdfToCsv();
            System.out.println(resultadoConversao);
            if (resultadoConversao.contains("❌")) {
                return "❌ Falha na conversão do PDF para CSV, processo interrompido.";
            }

            // 3️⃣ Criar um ZIP com todos os arquivos gerados
            List<String> arquivosParaZipar = new ArrayList<>();
            arquivosParaZipar.add(PDF_ANEXO_I_PATH);
            arquivosParaZipar.add(PDF_ANEXO_II_PATH);
            arquivosParaZipar.add(CSV_FILE_PATH);

            fileZipper.zipFiles(FINAL_ZIP_PATH, arquivosParaZipar);

            return "✅ Processo completo finalizado! Arquivos compactados em: " + FINAL_ZIP_PATH;
        } catch (Exception e) {
            return "❌ Erro inesperado durante o processamento: " + e.getMessage();
        }
    }
}
