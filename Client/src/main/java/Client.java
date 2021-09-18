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
            System.out.println("Connected to server.");
            String request = "Test request";
            writer.write(request);
            writer.newLine();
            writer.flush();

//            JSONObject jsonToSend = new JSONObject();
//            String inputLine;
//            System.out.println("Введите арифметическое выраважение: ");
//            inputLine = scan.nextLine();
//
//            jsonToSend.put(Integer.toString(1), "x");
//            jsonToSend.put(Integer.toString(2), "+");
//            jsonToSend.put(Integer.toString(3), "y");
//
//            writer.write(jsonToSend.toString());
//            writer.newLine();
//            writer.flush();

            String response = reader.readLine();
            System.out.println("Response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
