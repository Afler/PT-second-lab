import com.google.gson.Gson;
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
                Equation equation = new Equation();
                json = Client.getJSON(reader);
                if (json.has("lastIndex"))
                    i = json.getInt("lastIndex");
                System.out.println("Введите арифметическое выражение: ");
                //json.put("exp" + i, scan.nextLine());
                equation.setExpression(scan.nextLine());
                Gson gson = new Gson();

                json.put("eq", gson.toJson(equation));
                Client.sendJSON(writer, json);
                json = Client.getJSON(reader);
                Equation recvEq = gson.fromJson(json.getString("eq"), Equation.class);
                if (json.has("error" + i)) {
                    System.out.println(json.getString("error" + i));
                } else {
                    //Equation recvEq = (Equation) json.get("eq");
                    equation.setResult(recvEq.getResult());
                    System.out.println(Double.parseDouble(equation.getResult()));
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
