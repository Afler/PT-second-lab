import java.util.*;
import java.util.StringTokenizer;


class Calc {
    private final String OPERATORS = "+-*/^%";
    private final String[] FUNCTIONS = {"sin", "cos", "tan"};
    private final String SEPARATOR = ",";
    /* temporary stack that holds operators, functions and brackets */
    public Stack<String> sStack = new Stack<>();
    public StringBuilder sbOut = null;


    /**
     * Преобразует строку в обратную польскую нотацию
     *
     * @param expr Входная строка
     * @return Выходная строка в обратной польской нотации
     */
    public String getReversePolishNotation(String expr) throws Exception {

        sbOut = new StringBuilder("");

        /* Разбиение входной строик на токены */
        StringTokenizer stringTokenizer = new StringTokenizer(expr,
                OPERATORS + " ()", true);

        String cTmp;

        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            if (isOperator(token)) {

                while (!sStack.empty()) {
                    if (isOperator(sStack.lastElement()) && (getOperatorPrior(token) <= getOperatorPrior(sStack.lastElement()))) {
                        sbOut.append(" ").append(sStack.pop()).append(" ");
                    } else {
                        sbOut.append(" ");
                        break;
                    }
                }


                sStack.push(token);


            } else if (token.equals("(")) {
                sStack.push(token);
            } else if (token.equals(")")) {
                cTmp = sStack.lastElement();
                while (!cTmp.equals("(")) {
                    if (sStack.size() < 1) {
                        throw new Exception("Ошибка разбора скобок. Проверьте правильность выражения.");
                    }
                    sbOut.append(" ").append(sStack.pop()).append(" ");


                    cTmp = sStack.lastElement();

                }

                sStack.remove(cTmp);
                if (!sStack.empty() && isFunction(sStack.lastElement())) {
                    sbOut.append(" ").append(sStack.lastElement()).append(" ");
                    sStack.remove(sStack.lastElement());
                }
            } else if (isFunction(token)) {
                sStack.push(token);
            } else {
                // Если символ не оператор - добавляем в выходную последовательность
                sbOut.append(" ").append(token).append(" ");
            }


        }
        while (!sStack.empty()) {
            sbOut.append(" ").append(sStack.pop());
        }

        return sbOut.toString();
    }

    /**
     * Функция проверяет, является ли текущий символ оператором
     */
    private static boolean isOperator(String c) {
        return switch (c) {
            case "-", "+", "*", "/", "^" -> true;
            default -> false;
        };
    }

    /**
     * Возвращает приоритет операции
     *
     * @param op char
     * @return byte
     */
    private static byte getOperatorPrior(String op) {
        return switch (op) {
            case "^" -> (byte) 3;
            case "*", "/", "%" -> (byte) 2;
            default -> (byte) 1;
        };
        // Тут остается + и -
    }

    /**
     * Check if the token is function (e.g. "sin")
     *
     * @param token Input <code>String</code> token
     * @return <code>boolean</code> output
     * @since 1.0
     */

    private boolean isFunction(String token) {
        for (String item : FUNCTIONS) {
            if (item.equals(token)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Считает выражение, записанное в обратной польской нотации
     *
     * @param expr Expression to compute
     * @return double result
     */

    public double calculate(String expr) throws Exception {
        double dA = 0, dB = 0;
        String sTmp;
        Deque<Double> stack = new ArrayDeque<Double>();
        StringTokenizer st = new StringTokenizer(expr);
        while (st.hasMoreTokens()) {
            try {

                sTmp = st.nextToken().trim();


                if ((1 == sTmp.length() && isOperator(sTmp)) || isFunction(sTmp)) {
                    if (isFunction(sTmp)) {
                        dA = stack.pop();
                        if ("sin".equals(sTmp)) {
                            dA = Math.sin(dA);
                        } else {
                            throw new Exception("Недопустимая операция " + sTmp);
                        }
                        stack.push(dA);
                    }

                    if (1 == sTmp.length() && isOperator(sTmp)) {
                        dB = stack.pop();
                        dA = stack.pop();
                        switch (sTmp) {
                            case "+" -> dA += dB;
                            case "-" -> dA -= dB;
                            case "/" -> dA /= dB;
                            case "*" -> dA *= dB;
                            case "%" -> dA %= dB;
                            case "^" -> dA = Math.pow(dA, dB);
                            default -> throw new Exception("Недопустимая операция " + sTmp);
                        }
                        stack.push(dA);
                    }
                } else {
                    dA = Double.parseDouble(sTmp);
                    stack.push(dA);
                }

            } catch (Exception e) {
                throw new Exception("Недопустимый символ в выражении");
            }

        }


        if (stack.size() > 1) {
            throw new Exception("Количество операторов не соответствует количеству операндов");
        }

        return stack.pop();
    }

    public static double evaluate(String expr) throws ParserException {
        try {
            Calc calc = new Calc();
            return calc.calculate(calc.getReversePolishNotation(expr));
        } catch (Exception e) {
            throw new ParserException("error");
        }
    }
}