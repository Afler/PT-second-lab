import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.StringTokenizer;


class Calc {
    private final String OPERATORS = "+-*/^%";
    private final String[] FUNCTIONS = {"sin", "cos", "tan"};
    private final String SEPARATOR = ",";
    //public StringBuilder sbStack =null;
    /* temporary stack that holds operators, functions and brackets */
    public Stack<String> sStack = new Stack<>();
    public StringBuilder sbOut = null;


    /**
     * Преобразовать строку в обратную польскую нотацию
     *
     * @param sIn Входная строка
     * @return Выходная строка в обратной польской нотации
     */
    public String opn(String sIn) throws Exception {

        sbOut = new StringBuilder("");

        /* splitting input string into tokens */
        StringTokenizer stringTokenizer = new StringTokenizer(sIn,
                OPERATORS + " ()", true);

        String cTmp;

        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            if (isOp(token)) {

                while (!sStack.empty()) {
                    if (isOp(sStack.lastElement()) && (opPrior(token) <= opPrior(sStack.lastElement()))) {
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
                if (isFunction(sStack.lastElement())) {
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
    private static boolean isOp(String c) {
        switch (c) {
            case "-":
            case "+":
            case "*":
            case "/":
            case "^":
                return true;
        }
        return false;
    }

    /**
     * Возвращает приоритет операции
     *
     * @param op char
     * @return byte
     */
    private static byte opPrior(String op) {
        switch (op) {
            case "^":
                return 3;
            case "*":
            case "/":
            case "%":
                return 2;
        }
        return 1; // Тут остается + и -
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
     * @param sIn
     * @return double result
     */

    public double calculate(String sIn) throws Exception {
        double dA = 0, dB = 0;
        String sTmp;
        Deque<Double> stack = new ArrayDeque<Double>();
        StringTokenizer st = new StringTokenizer(sIn);
        while (st.hasMoreTokens()) {
            try {

                sTmp = st.nextToken().trim();
                //String tmp=String.valueOf(sTmp.charAt(0));


                if ((1 == sTmp.length() && isOp(sTmp)) || isFunction(sTmp)) {
                    if (isFunction(sTmp)) {
                        dA = stack.pop();
                        switch (sTmp) {
                            case "sin":
                                dA = Math.sin(dA);
                                break;
                            default:
                                throw new Exception("Недопустимая операция " + sTmp);
                        }
                        stack.push(dA);
                    }

                    if (1 == sTmp.length() && isOp(sTmp)) {
                        dB = stack.pop();
                        dA = stack.pop();
                        switch (sTmp) {
                            case "+":
                                dA += dB;
                                break;
                            case "-":
                                dA -= dB;
                                break;
                            case "/":
                                dA /= dB;
                                break;
                            case "*":
                                dA *= dB;
                                break;
                            case "%":
                                dA %= dB;
                                break;
                            case "^":
                                dA = Math.pow(dA, dB);
                                break;
                            default:
                                throw new Exception("Недопустимая операция " + sTmp);
                        }
                        stack.push(dA);
                        //if (stack.size() < 2) {
                        //    throw new Exception("Неверное количество данных в стеке для операции " + sTmp);
                        //}
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

    public static void main(String[] args) {
        BufferedReader d = new BufferedReader(new InputStreamReader(System.in));
        String sIn = "";


        System.out.println("Введте выражение для расчета. Поддерживаются цифры, операции +,-,*,/,^,% и приоритеты в виде скобок ( и ):");
        try {
//            Calc test = new Calc();
//            String sIn = d.readLine();
            evaluate(sIn);
            //System.out.println(test.calculate(test.opn(sIn)));
        } catch (Exception e) {
            e.printStackTrace();
        }

            /*test.opn(sIn);
            System.out.println("Operator Stack: "+test.sStack);
        System.out.println("Output: "+test.sbOut);*/

        //sIn = Calc.opn(sIn);
        //System.out.println(Calc.calculate(sIn));

        //System.out.println(e.getMessage());


    }
    public static double evaluate(String sIn) throws ParserException {
        try {
            Calc test = new Calc();
            return test.calculate(test.opn(sIn));
        } catch (Exception e) {
            throw new ParserException("error");
        }
    }
}