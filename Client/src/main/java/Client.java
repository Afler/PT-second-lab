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
            String inputLine;
            do {
                JSONObject jsonRequest = new JSONObject();
                System.out.println("Введите арифметическое выражение: ");
                inputLine = scan.nextLine();

                jsonRequest.put(Integer.toString(1), inputLine);

                Client.writeString(writer, jsonRequest.toString());

                String source = reader.readLine();
                JSONObject jsonResponse = new JSONObject(source);
                if (jsonResponse.has("error")) {
                    System.out.println(jsonResponse.getString("error"));
                } else {
                    System.out.println(Double.parseDouble(jsonResponse.getString("result")));
                }
                do {
                    System.out.println("Продолжить? (Y/N)");
                    inputLine = scan.nextLine();
                } while (!(inputLine.equals("Y") || inputLine.equals("N")));
                Client.writeString(writer, inputLine);
            } while (inputLine.equals("Y"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeString(BufferedWriter writer, String inputLine) throws IOException {
        writer.write(inputLine);
        writer.newLine();
        writer.flush();
    }
}
