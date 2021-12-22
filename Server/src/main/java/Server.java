import com.fasterxml.jackson.databind.ObjectMapper;
import net.jimblackler.jsonschemafriend.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Scanner;

public class Server {

    private static final String JSON_SAVE_FILE_PATH = "C:\\Users\\555\\IdeaProjects\\socketTestApp\\Server\\src\\saveStorage\\save.json";
    private static final String JSON_SCHEMA_FILE_PATH = "C:\\Users\\555\\IdeaProjects\\socketTestApp\\Server\\src\\jsonSchema\\schema.json";

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(8000)) {
            System.out.println("Server started.");

            ObjectMapper objectMapper = new ObjectMapper();
            File saveFile = new File(JSON_SAVE_FILE_PATH);

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
                        if (validationCheck(objectMapper.writeValueAsString(history))) {
                            objectMapper.writeValue(saveFile, history);
                            System.out.println("Файл сохранен");
                        } else {
                            System.out.println("Файл не сохранен, неверный формат");
                        }
                    } while (equation.getContinueCheck().equals("Y"));
                }
            }
        } catch (IOException | GenerationException e) {
            e.printStackTrace();
        }
    }

    private static boolean validationCheck(String json) throws IOException, GenerationException {
        SchemaStore schemaStore = new SchemaStore(); // Initialize a SchemaStore.
        Schema schema = schemaStore.loadSchemaJson(getSchema());
        Validator validator = new Validator();
        try {
            validator.validateJson(schema, json);
            return true;
        } catch (ValidationException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String getSchema() throws IOException {
        FileReader fileReader = new FileReader(JSON_SCHEMA_FILE_PATH);
        StringBuilder fileContent = new StringBuilder();
        Scanner scan = new Scanner(fileReader);
        while (scan.hasNextLine()) {
            fileContent.append(scan.nextLine());
        }
        fileReader.close();
        return fileContent.toString();
    }

}
