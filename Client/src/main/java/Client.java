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
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                Scanner scan = new Scanner(System.in)
        ) {
            while (true) {
                JSONObject jsonToSend = new JSONObject();
                String inputLine;
                System.out.println("Введите арифметическое выражение: ");
                inputLine = scan.nextLine();

                jsonToSend.put(Integer.toString(1), inputLine);

                writer.write(jsonToSend.toString());
                writer.newLine();
                writer.flush();

                String source = reader.readLine();
                JSONObject obj = new JSONObject(source);
                if (obj.has("error")) {
                    System.out.println(obj.getString("error"));
                } else {
                    System.out.println(Double.parseDouble(obj.getString("result")));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
