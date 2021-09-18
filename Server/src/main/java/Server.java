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
                    String request = null;
                    try {
                        request = reader.readLine();
                        System.out.println("Request: " + request);
                        String response = "Hello from server: " + request.length();
                        System.out.println("Response: " + response);
                        writer.write(response);
                        writer.newLine();
                        writer.flush();

//                        JSONObject json = new JSONObject(reader.readLine());
//                        System.out.println(json);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
