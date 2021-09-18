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
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                new Thread(() -> {
                    try {
                        while (true) {
                            String source = reader.readLine();
                            try {
                                JSONObject json = new JSONObject(source);
                                if (source.isEmpty()) {
                                    throw new ParserException("Empty string input");
                                }
                                System.out.println(json);
                                String exp = json.getString("1");
                                double result = Calc.evaluate(exp);
                                System.out.println(result);
                                json.put("result", Double.toString(result));
                                writer.write(json.toString());
                                writer.newLine();
                                writer.flush();
                            } catch (ParserException e) {
                                JSONObject error = new JSONObject();
                                error.put("error", "Incorrect input");
                                writer.write(error.toString());
                                writer.newLine();
                                writer.flush();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
