import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (
                Socket socket = new Socket("127.0.0.1", 8000);
                BufferedWriter serverIn = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader serverOut = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner scan = new Scanner(System.in)
        ) {
            ObjectMapper objectMapper = new ObjectMapper();
            String continueCheck;

            Equation equation = new Equation();
            do {

                // Считать и отправить арифметическое выражение
                System.out.println("Введите арифметическое выражение: ");
                equation.setExpression(scan.nextLine());
                serverIn.write(objectMapper.writeValueAsString(equation));
                serverIn.write("\n");
                serverIn.flush();

                // Получить численный ответ и актуальный индекс
                equation = objectMapper.readValue(serverOut.readLine(), Equation.class);
                System.out.println(equation.getError());

                do {
                    System.out.println("Продолжить? (Y/N)");
                    continueCheck = scan.nextLine();
                    equation.setContinueCheck(continueCheck);
                } while (!(continueCheck.equals("Y") || continueCheck.equals("N")));

                // Отправить информацию о продолжении
                serverIn.write(objectMapper.writeValueAsString(equation));
                serverIn.write("\n");
                serverIn.flush();

            } while (continueCheck.equals("Y"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
