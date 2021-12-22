import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final String JSON_FILE_PATH = "Server/src/saveStorage/test.json";

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(8000)) {
            System.out.println("Server started.");

            ObjectMapper objectMapper = new ObjectMapper();
            File saveFile = new File(JSON_FILE_PATH);

            while (true) {
                Socket socket = server.accept();

                try (BufferedWriter clientIn = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                     BufferedReader clientOut = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    // Загрузить предыдущие записи
                    History history;
                    try {
                        history = objectMapper.readValue(saveFile, History.class);
                    } catch (IOException e) {
                        history = new History();
                    }

                    Equation equation = new Equation();
                    do {
                        try {
                            // Получить арифметическое выражение
                            equation = objectMapper.readValue(clientOut.readLine(), Equation.class);
                            if (equation.getExpression().isEmpty()) {
                                throw new ParserException("Пустая строка");
                            }

                            // Посчитать ответ
                            double result = Calc.evaluate(equation.getExpression());
                            equation.setResult(Double.toString(result));
                            equation.setError("Ошибок нет");

                        } catch (ParserException e) {
                            // Если ответ не посчитан
                            equation.setError("Некорректный ввод");
                        }

                        // Отправить численный ответ и актуальный индекс
                        clientIn.write(objectMapper.writeValueAsString(equation));
                        clientIn.write("\n");
                        clientIn.flush();

                        // Получить информацию о продолжении
                        equation = objectMapper.readValue(clientOut.readLine(), Equation.class);

                        // Обновить сохраненнные записи
                        history.getEquations().add(equation);
                        history.setLastIndex(history.getLastIndex() + 1);
                        if (validationCheck(objectMapper.writeValueAsString(history))){
                            objectMapper.writeValue(saveFile, history);
                        }
                    } while (equation.getContinueCheck().equals("Y"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean validationCheck(String json) {
        return false;
    }

}
