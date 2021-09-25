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
                        JSONObject json = new JSONObject();
                        int i = 1;
                        do {
                            try {
                                json = new JSONObject(reader.readLine());
                                if (json.getString("exp" + i).isEmpty()) {
                                    throw new ParserException("Пустая строка");
                                }
                                System.out.println(json);
                                String exp = json.getString("exp" + i);
                                double result = Calc.evaluate(exp);
                                System.out.println(result);
                                json.put("result" + i, Double.toString(result));
                                Server.writeJSON(writer, json);
                            } catch (ParserException e) {
                                json.put("error" + i, "Некорректный ввод");
                                Server.writeJSON(writer, json);
                                json = new JSONObject(reader.readLine());
                            }
                        } while (json.getString("continueCheck" + i++).equals("Y"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
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
