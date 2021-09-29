import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (
                Socket socket = new Socket("127.0.0.1", 8000);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner scan = new Scanner(System.in)
        ) {
            JSONObject json;
            int i = 1;

            do {
                json = Client.getJSON(reader);
                if (json.has("lastIndex"))
                    i = json.getInt("lastIndex");
                System.out.println("Введите арифметическое выражение: ");
                json.put("exp" + i, scan.nextLine());
                Client.sendJSON(writer, json);
                json = Client.getJSON(reader);
                if (json.has("error" + i)) {
                    System.out.println(json.getString("error" + i));
                } else {
                    System.out.println(Double.parseDouble(json.getString("result" + i)));
                }
                do {
                    System.out.println("Продолжить? (Y/N)");
                    json.put("continueCheck" + i, scan.nextLine());
                } while (!(json.get("continueCheck" + i).equals("Y") ||
                        json.get("continueCheck" + i).equals("N")));
                Client.sendJSON(writer, json);
            } while (json.get("continueCheck" + i).equals("Y"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JSONObject getJSON(BufferedReader reader) {
        try {
            JSONTokener jsonTokener = new JSONTokener(reader);
            return new JSONObject(jsonTokener);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    private static void sendJSON(BufferedWriter writer, JSONObject json) throws IOException {
        writer.write(json.toString());
        writer.newLine();
        writer.flush();
    }
}
