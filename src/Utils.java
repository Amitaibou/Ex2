//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class Utils {
    public Utils() {
    }

    public static boolean isNumber(String input) {
        if (input != null && !input.trim().isEmpty()) {
            try {
                Double.parseDouble(input);
                return true;
            } catch (NumberFormatException var2) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isText(String input) {
        if (input != null && !input.trim().isEmpty()) {
            if (input.matches(".*([+\\-*/]{2,}).*")) {
                return false;
            } else if (input.matches("[0-9.]+([+\\-*/][0-9.]+)*")) {
                return true;
            } else {
                return input.matches("[A-Za-z0-9]+") && !isNumber(input) && !input.startsWith("=");
            }
        } else {
            return false;
        }
    }

    public static boolean isForm(String input) {
        if (input != null && input.startsWith("=")) {
            String formula = input.substring(1).trim();
            if (!areParenthesesBalanced(formula)) {
                return false;
            } else if (formula.matches(".*([+\\-*/]{2,}).*")) {
                return false;
            } else {
                return !formula.isEmpty();
            }
        } else {
            return false;
        }
    }
    public static double computeForm(String form, Ex2Sheet sheet) {
        if (form == null || !form.startsWith("=")) {
            throw new IllegalArgumentException("Invalid formula: " + form);
        }

        // הסרת ה- "=" מהפורמולה
        String formula = form.substring(1).trim();
        System.out.println("Original formula: " + formula); // מעקב אחר הפורמולה המקורית

        try {
            // החלפת הפניות של תאים בערכים שלהם
            String replacedFormula = replaceCellReferences(formula, sheet);
            System.out.println("Replaced formula: " + replacedFormula); // מעקב אחרי פורמולה שהוחלפה

            // חישוב הערך הסופי של הפורמולה
            double result = evalFormula(replacedFormula);
            System.out.println("Evaluation result: " + result); // הדפסת התוצאה הסופית

            return result;
        } catch (Exception e) {
            System.err.println("Error in formula computation: " + e.getMessage());
            throw new IllegalArgumentException("Error computing formula: " + form);
        }
    }

    public static String replaceCellReferences(String formula, Ex2Sheet sheet) {
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < formula.length()) {
            char c = formula.charAt(i);

            if (Character.isLetter(c)) {
                int j = i;
                while (j < formula.length() && (Character.isLetterOrDigit(formula.charAt(j)))) {
                    j++;
                }

                String cellRef = formula.substring(i, j);
                int[] coords = sheet.parseCoordinates(cellRef);
                String cellValue = sheet.eval(coords[0], coords[1]);

                if (cellValue == null || cellValue.trim().isEmpty()) {
                    throw new IllegalArgumentException("Formula contains a reference to an empty cell: " + cellRef);
                }

                result.append(cellValue);
                i = j;
            } else {
                result.append(c);
                i++;
            }
        }

        return result.toString();
    }


    private static boolean areParenthesesBalanced(String formula) {
        int count = 0;
        char[] var2 = formula.toCharArray();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            char c = var2[var4];
            if (c == '(') {
                ++count;
            } else if (c == ')') {
                --count;
            }

            if (count < 0) {
                return false;
            }
        }

        return count == 0;
    }

    public static double evalFormula(String formula) {
        formula = formula.trim();

        try {
            List<String> tokens = tokenizeFormula(formula);
            return evaluateTokens(tokens);
        } catch (Exception var2) {
            throw new IllegalArgumentException("Invalid formula: " + formula);
        }
    }

    private static List<String> tokenizeFormula(String formula) {
        List<String> tokens = new ArrayList();
        StringBuilder numberBuffer = new StringBuilder();

        for(int i = 0; i < formula.length(); ++i) {
            char c = formula.charAt(i);
            if (!Character.isDigit(c) && c != '.') {
                if (numberBuffer.length() > 0) {
                    tokens.add(numberBuffer.toString());
                    numberBuffer.setLength(0);
                }

                if (!Character.isWhitespace(c)) {
                    tokens.add(Character.toString(c));
                }
            } else {
                numberBuffer.append(c);
            }
        }

        if (numberBuffer.length() > 0) {
            tokens.add(numberBuffer.toString());
        }

        return tokens;
    }

    private static double evaluateTokens(List<String> tokens) {
        Stack<Double> numbers = new Stack();
        Stack<Character> operators = new Stack();
        Iterator var3 = tokens.iterator();

        while(true) {
            while(true) {
                while(true) {
                    while(var3.hasNext()) {
                        String token = (String)var3.next();
                        if (!isNumber(token)) {
                            if (!token.equals("(")) {
                                if (token.equals(")")) {
                                    while(!operators.isEmpty() && (Character)operators.peek() != '(') {
                                        processTopOperator(numbers, operators);
                                    }

                                    if (operators.isEmpty() || (Character)operators.peek() != '(') {
                                        throw new IllegalArgumentException("Mismatched parentheses in formula.");
                                    }

                                    operators.pop();
                                } else if (isOperator(token.charAt(0))) {
                                    while(!operators.isEmpty() && precedence((Character)operators.peek()) >= precedence(token.charAt(0))) {
                                        processTopOperator(numbers, operators);
                                    }

                                    operators.push(token.charAt(0));
                                }
                            } else {
                                operators.push('(');
                            }
                        } else {
                            numbers.push(Double.parseDouble(token));
                        }
                    }

                    while(!operators.isEmpty()) {
                        processTopOperator(numbers, operators);
                    }

                    if (numbers.size() != 1) {
                        throw new IllegalArgumentException("Invalid formula structure.");
                    }

                    return (Double)numbers.pop();
                }
            }
        }
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static int precedence(char operator) {
        switch (operator) {
            case '*':
            case '/':
                return 2;
            case '+':
            case '-':
                return 1;
            case ',':
            case '.':
            default:
                return -1;
        }
    }

    private static void processTopOperator(Stack<Double> numbers, Stack<Character> operators) {
        if (numbers.size() < 2) {
            throw new IllegalArgumentException("Invalid formula: missing operands.");
        } else {
            double b = (Double)numbers.pop();
            double a = (Double)numbers.pop();
            char op = (Character)operators.pop();
            numbers.push(applyOperator(op, a, b));
        }
    }

    private static double applyOperator(char operator, double left, double right) {
        switch (operator) {
            case '*':
                return left * right;
            case '+':
                return left + right;
            case ',':
            case '.':
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
            case '-':
                return left - right;
            case '/':
                if (right == 0.0) {
                    throw new ArithmeticException("Division by zero is not allowed");
                } else {
                    return left / right;
                }
        }
    }
}
