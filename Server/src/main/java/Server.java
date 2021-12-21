import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(8000)) {
            System.out.println("Server started.");
            History history = new History();
            history.setEquations(new ArrayList<>());
            while (true) {
                Socket socket = server.accept();

                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    JSONObject json = new JSONObject();
                    int i;
                    try {
                        json = Server.loadJSONFromFile(new File("Server/src/saveStorage/test.json"));
                    } catch (FileNotFoundException e) {
                        System.out.println("Файл не найден");
                    } catch (JSONException e) {
                        System.out.println("Не удалось считать JSONObject иза файла");
                    }
                    if (json.has("lastIndex")) i = json.getInt("lastIndex") + 1;
                    else i = 1;

                    do {
                        //json.put("lastIndex", i);
                        history.setLastIndex(i);
                        Server.sendJSON(writer, json);
                        try {
                            json = Server.getJSON(reader);
                            Gson gson = new Gson();
                            Equation recvEq = gson.fromJson(json.getString("eq"), Equation.class);
                            //Equation recEq = json.getString("eq");
                            if (recvEq.getExpression().isEmpty()) {
                                throw new ParserException("Пустая строка");
                            }
                            //String exp = json.getString("exp" + i);
                            Equation equation = recvEq;
                            double result = Calc.evaluate(equation.getExpression());
                            System.out.println(result);
                            //json.put("result" + i, Double.toString(result));
                            //Equation equation = new Equation();
                            //equation.setExpression(exp);
                            equation.setResult(Double.toString(result));
                            //json.put("eq" + i, equation);
                            history.getEquations().add(equation);
                            json.put("eq", gson.toJson(equation));
                            Server.sendJSON(writer, json);
                        } catch (ParserException e) {
                            json.put("error" + i, "Некорректный ввод");
                            history.setError("Некорректный ввод");
                            Server.sendJSON(writer, json);
                        }
                        json = Server.getJSON(reader);
                        json.put("history", history);
                        Server.saveJSONToFile(json, i);
                    } while (json.getString("continueCheck" + i++).equals("Y"));
                }
            }
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

    private static void saveJSONToFile(JSONObject json, int i) throws IOException {
        try (FileWriter saveFile = new FileWriter("Server/src/saveStorage/test.json")) {
            //json.put("lastIndex", i);
            saveFile.write(json.get("history").toString());
        }
    }

    private static JSONObject loadJSONFromFile(File file) throws IOException, JSONException {
        try (FileReader loadFile = new FileReader(file)) {
            BufferedReader reader = new BufferedReader(loadFile);
            return getJSON(reader);
        }
    }
}
