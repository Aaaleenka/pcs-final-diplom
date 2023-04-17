import com.google.gson.Gson;
import org.json.simple.JSONArray;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));

        try (ServerSocket serverSocket = new ServerSocket(ServerConfig.PORT);) { // стартуем сервер один раз

            while (true) { // в цикле принимаем подключения
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter writer = new PrintWriter(socket.getOutputStream());) {
                    String word = reader.readLine();
                    List<PageEntry> list = engine.search(word);

                    if (list.isEmpty()) {
                        System.out.println("такого слова в файлах нет");
                        writer.println("такого слова в файлах нет");
                    } else {
                        Collections.sort(list);
                        Collections.reverse(list);
                        JSONArray answerArray = listToJson(list);
                        for (int i = 0; i < answerArray.size(); i++) {
                            System.out.println(answerArray.get(i).toString());
                            writer.println(answerArray.get(i).toString());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }

    }

    public static JSONArray listToJson(List<PageEntry> list) {
        JSONArray answerArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            answerArray.add(new Gson().toJson(list.get(i)));
        }
        return answerArray;
    }
}
