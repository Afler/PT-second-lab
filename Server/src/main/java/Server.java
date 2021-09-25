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
                        int i;
                        try {
                            File file = new File("Server/src/saveStorage/test.json");
                            //File file = new File("Server/src/saveStorage/test.txt");
                            json = Server.loadFile(json, file);
                            if (json.has("finalIndex")) {
                                i = json.getInt("finalIndex") + 1;
                            } else
                                throw new NullPointerException();
                        } catch (NullPointerException e) {
                            i = 1;
                            json.put("finalIndex", i);
                        }
                        do {
                            json.put("finalIndex", i);
                            Server.writeJSON(writer, json);
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
                            }
                            json = new JSONObject(reader.readLine());
                            Server.saveFile(json, i);
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

    private static void saveFile(JSONObject json, int i) throws IOException {
        FileWriter saveFile = new FileWriter("Server/src/saveStorage/test.json");
        //FileWriter saveFile = new FileWriter("Server/src/saveStorage/test.txt");
        try {
            json.put("finalIndex", i);
            saveFile.write(json.toString());
            System.out.println("Save file complete");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            saveFile.flush();
            saveFile.close();
        }
    }

    private static JSONObject loadFile(JSONObject json, File file) throws IOException {
        try (FileReader loadFile = new FileReader(file)) {
            BufferedReader reader = new BufferedReader(loadFile);
            json = new JSONObject(reader.readLine());
            return json;
        } catch (IOException e) {
            e.printStackTrace();
            return json;
        }
    }
}
