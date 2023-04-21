import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import netscape.javascript.JSObject;
import org.json.simple.JSONArray;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));

        //считываем стоп слова
        List<String> listStopWords = loadFromTxtFile(new File("stop-ru.txt"));

        try (ServerSocket serverSocket = new ServerSocket(ServerConfig.PORT);) { // стартуем сервер один раз

            while (true) { // в цикле принимаем подключения
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter writer = new PrintWriter(socket.getOutputStream());) {

                    String word = reader.readLine();
                    String[] words = word.split("\\P{IsAlphabetic}+"); // массив для слов из запроса

                    //сделаем массив мапой - тем самым удалим повторения и сразу проверим на слова из стоп-списка
                    Map<String, List<PageEntry>> mapAllRequest = new HashMap<>();

                    for (int i = 0; i < words.length; i++) {
                        if (!listStopWords.contains(words[i])) {
                            List<PageEntry> list = engine.search(words[i]);
                            mapAllRequest.put(words[i], list);
                        }
                    }

                    //создадим большой список состоящий из всех вхождений PageEntry
                    List<PageEntry> answer = new ArrayList<>();

                    for (Map.Entry<String, List<PageEntry>> kv : mapAllRequest.entrySet()) {
                        answer.addAll(kv.getValue());
                    }

                    Collections.sort(answer);

                    //сдвоим повторы если название файла и страница одинаковые, а count увеличим
                    for (int i = 0; i < answer.size() - 1; i++) {
                        PageEntry pageEntry1 = answer.get(i);
                        for (int j = i + 1; j < answer.size(); j++) {
                            PageEntry pageEntry2 = answer.get(j);
                            if (pageEntry1.getPdfName().equals(pageEntry2.getPdfName()) &&
                                    (pageEntry1.getPage() == pageEntry2.getPage())) {
                                int allCount = pageEntry1.getCount() + pageEntry2.getCount();
                                pageEntry1.setCount(allCount);
                                pageEntry2.setCount(0);
                            }
                        }
                    }

                    if (answer.isEmpty()) {
                        System.out.println("такого слова в файлах нет");
                        writer.println("такого слова в файлах нет");
                    } else {
                        Comparator<PageEntry> comparator = (o1, o2) -> Integer.compare(o1.getCount(), o2.getCount());
                        answer.sort(comparator);
                        Collections.reverse(answer);

                        System.out.println(listToJson(answer));
                        writer.println(listToJson(answer));

                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }

    }

    public static String listToJson(List<PageEntry> list) {
        JSONArray answerArray = new JSONArray();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCount() != 0) {
                String json = new Gson().toJson(list.get(i));
                PageEntryJson pageEntryJson = gson.fromJson(json, PageEntryJson.class);
                answerArray.add(pageEntryJson);
            }
        }
        String prettyJsonString = gson.toJson(answerArray);
        return prettyJsonString;
    }

    public static List<String> loadFromTxtFile(File textFile) throws IOException {
        List<String> list = new ArrayList<>();
        try (BufferedReader input = new BufferedReader(new FileReader(textFile))) {

            String s = input.readLine();
            while (s != null) {
                list.add(s);
                s = input.readLine();
            }
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        return list;
    }
}
