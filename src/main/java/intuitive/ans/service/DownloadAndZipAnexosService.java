package intuitive.ans.extracao.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DownloadAndZipAnexosService {
    private static final String URL_ANS = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";
    private static final String DOWNLOAD_DIR = "downloads/";
    private static final String ZIP_FILE = "downloads/anexos.zip";

    public String baixarECompactarAnexos() {
        try {
            Files.createDirectories(Paths.get(DOWNLOAD_DIR));

            Document doc = Jsoup.connect(URL_ANS)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10_000) // Timeout de 10 segundos
                    .get();

            Elements links = doc.select("a[href$=.pdf]");

            List<String> arquivosBaixados = new ArrayList<>();
            boolean anexoIEncontrado = false;
            boolean anexoIIEncontrado = false;

            for (Element link : links) {
                String fileUrl = link.absUrl("href");
                String linkText = link.text().toLowerCase();

                if (!anexoIEncontrado && (fileUrl.toLowerCase().contains("anexo-i") || linkText.contains("anexo i"))) {
                    String filePath = DOWNLOAD_DIR + "Anexo I.pdf";
                    baixarArquivo(fileUrl, filePath);
                    arquivosBaixados.add(filePath);
                    anexoIEncontrado = true;
                }

                if (!anexoIIEncontrado && (fileUrl.toLowerCase().contains("anexo-ii") || linkText.contains("anexo ii"))) {
                    String filePath = DOWNLOAD_DIR + "Anexo II.pdf";
                    baixarArquivo(fileUrl, filePath);
                    arquivosBaixados.add(filePath);
                    anexoIIEncontrado = true;
                }

                if (anexoIEncontrado && anexoIIEncontrado) {
                    break;
                }
            }

            if (!anexoIEncontrado || !anexoIIEncontrado) {
                return "❌ Não foi possível encontrar todos os anexos.";
            }

            compactarEmZip(ZIP_FILE, arquivosBaixados);
            return "✅ Download concluído! Arquivos compactados em: " + ZIP_FILE;

        } catch (IOException e) {
            return "❌ Erro ao executar web scraping: " + e.getMessage();
        }
    }

    private void baixarArquivo(String fileUrl, String filePath) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (InputStream in = connection.getInputStream();
                 FileOutputStream out = new FileOutputStream(filePath)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Erro ao baixar o arquivo: " + fileUrl + " - " + e.getMessage());
        }
    }

    private void compactarEmZip(String zipFilePath, List<String> filePaths) {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (String filePath : filePaths) {
                File file = new File(filePath);
                if (!file.exists()) continue;

                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Erro ao compactar arquivo ZIP: " + e.getMessage());
        }
    }
}
