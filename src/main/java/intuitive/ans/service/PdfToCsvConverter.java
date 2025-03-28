package intuitive.ans.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfToCsvConverter {
    private static final String PDF_PATH = "/home/leonidas/Área de Trabalho/intuitive/ans/downloads/Anexo I.pdf";
    private static final String CSV_PATH = "downloads/final_data.csv";
    private static final String ZIP_PATH = "downloads/Teste_Leonidas.zip";

    private final FileZipper fileZipper;

    public PdfToCsvConverter(FileZipper fileZipper) {
        this.fileZipper = fileZipper;
    }

    public String extractPdfToCsv() {
        File outputFile = new File(CSV_PATH);
        outputFile.getParentFile().mkdirs(); // Criar diretório se não existir

        try (FileWriter fileWriter = new FileWriter(CSV_PATH);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            PDDocument document = PDDocument.load(new File(PDF_PATH));
            ObjectExtractor extractor = new ObjectExtractor(document);
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();

            for (int pageNumber = 1; pageNumber <= document.getNumberOfPages(); pageNumber++) {
                Page page = extractor.extract(pageNumber);
                List<Table> tables = sea.extract(page);

                for (Table table : tables) {
                    List<String[]> tableData = processTable(table);
                    csvWriter.writeAll(tableData);
                }
            }

            document.close();
            System.out.println("✅ CSV gerado com sucesso em: " + CSV_PATH);

            // Após gerar o CSV, chamamos a função para zipar o arquivo
            List<String> filesToZip = List.of(CSV_PATH);
            fileZipper.zipFiles(ZIP_PATH, filesToZip);

            return "✅ Processo concluído! Arquivo ZIP criado em: " + ZIP_PATH;

        } catch (IOException e) {
            return "❌ Erro ao processar o PDF: " + e.getMessage();
        }
    }

    private List<String[]> processTable(Table table) {
        List<String[]> formattedTable = new ArrayList<>();

        for (List<RectangularTextContainer> row : table.getRows()) {
            String[] rowData = row.stream()
                    .map(RectangularTextContainer::getText)
                    .map(this::replaceAbbreviations) // Substitui abreviações
                    .toArray(String[]::new);

            formattedTable.add(rowData);
        }

        return formattedTable;
    }

    private String replaceAbbreviations(String text) {
        if (text.equalsIgnoreCase("OD")) return "Odontologia";
        if (text.equalsIgnoreCase("AMB")) return "Ambulatorial";
        return text;
    }
}
