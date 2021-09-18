public class Parser {

    //  Объявление лексем
    final int NONE = 0;         //  FAIL
    final int DELIMITER = 1;    //  Разделитель: +-*/^=, ")", "("
    final int VARIABLE = 2;     //  Переменная
    final int NUMBER = 3;       //  Число

    //  Объявление констант синтаксических ошибок
    final int SYNTAXERROR = 0;  //  Синтаксическая ошибка (10 + 5 6 / 1)
    final int UNBALPARENS = 1;  //  Несовпадение количества открытых и закрытых скобок
    final int NOEXP = 2;        //  Отсутствует выражение при запуске анализатора
    final int DIVBYZERO = 3;    //  Ошибка деления на ноль

    //  Лексема, определяющая конец выражения
    final String EOF = "\0";

    private String exp;     //  Ссылка на строку с выражением
    private int expIndex;     //  Текущий индекс в выражении
    private String token;   //  Сохранение текущей лексемы
    private int tokenType;    //  Сохранение типа лексемы


    public String toString(){
        return String.format("Exp = {0}\nexplds = {1}\nToken = {2}\nTokType = {3}", exp.toString(), expIndex,
                token.toString(), tokenType);
    }

    //  Получить следующую лексему
    private void getToken(){
        tokenType = NONE;
        token = "";

        //  Проверка на окончание выражения
        if(expIndex == exp.length()){
            token = EOF;
            return;
        }
        //  Проверка на пробелы, если есть пробел - игнорируем его.
        while(expIndex < exp.length() && Character.isWhitespace(exp.charAt(expIndex)))
            ++expIndex;
        //  Проверка на окончание выражения
        if(expIndex == exp.length()){
            token = EOF;
            return;
        }
        if(isDelim(exp.charAt(expIndex))){
            token += exp.charAt(expIndex);
            expIndex++;
            tokenType = DELIMITER;
        }
        else if(Character.isLetter(exp.charAt(expIndex))){
            while(!isDelim(exp.charAt(expIndex))){
                token += exp.charAt(expIndex);
                expIndex++;
                if(expIndex >= exp.length())
                    break;
            }
            tokenType = VARIABLE;
        }
        else if (Character.isDigit(exp.charAt(expIndex))){
            while(!isDelim(exp.charAt(expIndex))){
                token += exp.charAt(expIndex);
                expIndex++;
                if(expIndex >= exp.length())
                    break;
            }
            tokenType = NUMBER;
        }
        else {
            token = EOF;
        }
    }

    private boolean isDelim(char charAt) {
        return (" +-/*%^=()".indexOf(charAt)) != -1;
    }

    //  Точка входа анализатора
    public double evaluate(String expstr) throws ParserException{

        double result;

        exp = expstr;
        expIndex = 0;
        getToken();

        if(token.equals(EOF))
            handleErr(NOEXP);   //  Нет выражения

        //  Анализ и вычисление выражения
        result = evalExp2();

        if(!token.equals(EOF))
            handleErr(SYNTAXERROR);

        return result;
    }

    //  Сложить или вычислить два терма
    private double evalExp2() throws ParserException{

        char op;
        double result;
        double partialResult;
        result = evalExp3();
        while((op = token.charAt(0)) == '+' ||
                op == '-'){
            getToken();
            partialResult = evalExp3();
            switch (op) {
                case '-' -> result -= partialResult;
                case '+' -> result += partialResult;
            }
        }
        return result;
    }

    //  Умножить или разделить два фактора
    private double evalExp3() throws ParserException{

        char op;
        double result;
        double partialResult;

        result = evalExp4();
        while((op = token.charAt(0)) == '*' ||
                op == '/' | op == '%'){
            getToken();
            partialResult = evalExp4();
            switch (op) {
                case '*' -> result *= partialResult;
                case '/' -> {
                    if (partialResult == 0.0)
                        handleErr(DIVBYZERO);
                    result /= partialResult;
                }
                case '%' -> {
                    if (partialResult == 0.0)
                        handleErr(DIVBYZERO);
                    result %= partialResult;
                }
            }
        }
        return result;
    }

    //  Выполнить возведение в степень
    private double evalExp4() throws ParserException{

        double result;
        double partialResult;
        double ex;
        int t;
        result = evalExp5();
        if(token.equals("^")){
            getToken();
            partialResult = evalExp4();
            ex = result;
            if(partialResult == 0.0){
                result = 1.0;
            }else
                for(t = (int)partialResult - 1; t >  0; t--)
                    result *= ex;
        }
        return result;
    }

    //  Определить унарные + или -
    private double evalExp5() throws ParserException{
        double result;

        String op;
        op = " ";

        if((tokenType == DELIMITER) && token.equals("+") ||
                token.equals("-")){
            op = token;
            getToken();
        }
        result = evalExp6();
        if(op.equals("-"))
            result =  -result;
        return result;
    }

    //  Обработать выражение в скобках
    private double evalExp6() throws ParserException{
        double result;

        if(token.equals("(")){
            getToken();
            result = evalExp2();
            if(!token.equals(")"))
                handleErr(UNBALPARENS);
            getToken();
        }
        else
            result = atom();
        return result;
    }

    //  Получить значение числа
    private double atom()   throws ParserException{

        double result = 0.0;
        if (tokenType == NUMBER) {
            try {
                result = Double.parseDouble(token);
            } catch (NumberFormatException exc) {
                handleErr(SYNTAXERROR);
            }
            getToken();
        } else {
            handleErr(SYNTAXERROR);
        }
        return result;
    }

    //  Кинуть ошибку
    private void handleErr(int nOEXP2) throws ParserException{

        String[] err  = {
                "Syntax error",
                "Unbalanced Parentheses",
                "No Expression Present",
                "Division by zero"
        };
        throw new ParserException(err[nOEXP2]);
    }


}