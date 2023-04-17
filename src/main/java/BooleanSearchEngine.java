import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private Map<String, List<PageEntry>> map = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        // прочтите тут все pdf и сохраните нужные данные,
        // тк во время поиска сервер не должен уже читать файлы

        map.clear();

        //перебрать все файлы
        List<File> listOfPDFFiles = List.of(Objects.requireNonNull(pdfsDir.listFiles())); // список всех PDF-файлов

        for (File pdf : listOfPDFFiles) { // перебираем все PDF-файлы
            var doc = new PdfDocument(new PdfReader(pdf)); // создаем PDF-объект из каждого PDF-файла
            for (int i = 0; i < doc.getNumberOfPages(); i++) { //проходимся по каждой странице

                String text = PdfTextExtractor.getTextFromPage(doc.getPage(i + 1));
                String[] words = text.split("\\P{IsAlphabetic}+"); //получили массив из слов

                Map<String, Integer> freqs = new HashMap<>(); // мапа, где ключом будет слово, а значением - частота
                for (var word : words) { // перебираем слова
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }

                for (Map.Entry<String, Integer> kv : freqs.entrySet()) {
                    String word = kv.getKey();
                    // добавляем в большую мапу это слово с параметрами PageEntry, если слово уже есть, то пополняем список

                    List<PageEntry> listOfWordOnPage = new ArrayList<>();
                    if (map.containsKey(word)) listOfWordOnPage = map.get(word);
                    listOfWordOnPage.add(new PageEntry(pdf.getName(), i + 1, kv.getValue()));
                    map.put(word, listOfWordOnPage);
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        if (map.containsKey(word)) return map.get(word);
        else return Collections.emptyList();
    }

}
