import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(8000)) {

            System.out.println("Server started.");

            while (true) {

                Socket socket = server.accept();

                new Thread(() -> {
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                         BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                        String stringFromClient;
                        do {
                            stringFromClient = reader.readLine();
                            try {
                                JSONObject json = new JSONObject(stringFromClient);
                                if (stringFromClient.isEmpty()) {
                                    throw new ParserException("Пустая строка");
                                }
                                System.out.println(json);
                                String exp = json.getString("1");
                                double result = Calc.evaluate(exp);
                                System.out.println(result);
                                json.put("result", Double.toString(result));
                                Server.writeString(writer, json);
                                stringFromClient = reader.readLine();
                            } catch (ParserException e) {
                                JSONObject error = new JSONObject();
                                error.put("error", "Некорректный ввод");
                                Server.writeString(writer, error);
                            }
                        } while (stringFromClient.equals("Y"));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeString(BufferedWriter writer, JSONObject json) throws IOException {
        writer.write(json.toString());
        writer.newLine();
        writer.flush();
    }

    private static void writeString(BufferedWriter writer, String string) throws IOException {
        writer.write(string);
        writer.newLine();
        writer.flush();
    }
}
