package intuitive.ans.service;

import org.springframework.stereotype.Service;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileZipper {

    /**
     * Compacta uma lista de arquivos em um único arquivo ZIP.
     *
     * @param zipFilePath Caminho do arquivo ZIP de saída.
     * @param filePaths   Lista de arquivos a serem compactados.
     */
    public void zipFiles(String zipFilePath, List<String> filePaths) {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (String filePath : filePaths) {
                File file = new File(filePath);
                if (!file.exists()) {
                    System.err.println("⚠ Arquivo não encontrado: " + filePath);
                    continue;
                }

                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }
                    zos.closeEntry();
                    System.out.println("✔ Arquivo adicionado ao ZIP: " + filePath);
                }
            }

            System.out.println("✅ Arquivo ZIP criado com sucesso: " + zipFilePath);
        } catch (IOException e) {
            System.err.println("❌ Erro ao criar o ZIP: " + e.getMessage());
        }
    }
}
