import org.json.JSONObject;

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
            JSONObject json = new JSONObject();
            int i = 1;
            do {
                System.out.println("Введите арифметическое выражение: ");
                json.put("exp" + i, scan.nextLine());
                Client.writeJSON(writer, json);

                String source = reader.readLine();
                JSONObject jsonResponse = new JSONObject(source);
                if (jsonResponse.has("error" + i)) {
                    System.out.println(jsonResponse.getString("error"));
                } else {
                    System.out.println(Double.parseDouble(jsonResponse.getString("result" + i)));
                }
                do {
                    System.out.println("Продолжить? (Y/N)");
                    json.put("continueCheck" + i, scan.nextLine());
                } while (!(json.get("continueCheck" + i).equals("Y") ||
                        json.get("continueCheck" + i).equals("N")));
                Client.writeJSON(writer, json);
            } while (json.get("continueCheck" + i++).equals("Y"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeJSON(BufferedWriter writer, JSONObject json) throws IOException {
        writer.write(json.toString());
        writer.newLine();
        writer.flush();
    }
}
