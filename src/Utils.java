
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class Utils {
    public Utils() {
    }
    // check if is number
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
    // check if is text and convert it to string
    public static boolean isText(String input) {
        if (input != null && !input.trim().isEmpty()) {
            if (!input.startsWith("="))
                return true;
            if(input.matches(".*([+\\-*/]{2,}).*")) {
                return false;
            } else if (input.matches("[0-9.]+([+\\-*/][0-9.]+)*")) {
                return true;
            } else if(input.matches("[A-Za-z0-9]+") && !isNumber(input) && !input.startsWith("="))
                return false;
            return true;
        } else {
            return false;
        }
    }
    // checks if is formula or not
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
    // calculate formulas
    public static double computeForm(String form, Ex2Sheet sheet) {
        if (form == null || !form.startsWith("=")) {
            throw new IllegalArgumentException("Invalid formula: " + form);
        }

        // remove '='
        String formula = form.substring(1).trim();

        try {
            // switching cells in their values
            String replacedFormula = replaceCellReferences(formula, sheet);
            double result = evalFormula(replacedFormula);

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

            // recognize cells with both lower cases and upper cases
            if (Character.isLetter(c)) {
                int j = i;
                while (j < formula.length() && Character.isLetterOrDigit(formula.charAt(j))) {
                    j++;
                }

                String cellRef = formula.substring(i, j);

                // convert first letter to upper case
                cellRef = cellRef.toUpperCase();

                // evaulating coordinates to their cells
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

    // checks legit use of parentheses
    public static boolean areParenthesesBalanced(String formula) {
        int count = 0;
        char[] var2 = formula.toCharArray();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
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
        List<String> tokens = new ArrayList<>();
        StringBuilder numberBuffer = new StringBuilder();

        for (int i = 0; i < formula.length(); ++i) {
            char c = formula.charAt(i);

            // If the character is a digit or a decimal point, add it to the number buffer
            if (Character.isDigit(c) || c == '.') {
                numberBuffer.append(c);
            } else {
                // Handle negative numbers and multiplication by negative numbers
                if (c == '-' && (tokens.isEmpty() || isOperator(tokens.get(tokens.size() - 1)) || tokens.get(tokens.size() - 1).equals("("))) {
                    numberBuffer.append(c); // Treat as part of a negative number
                } else {
                    // If we encounter an operator or other character, flush the number buffer
                    if (numberBuffer.length() > 0) {
                        tokens.add(numberBuffer.toString());
                        numberBuffer.setLength(0);
                    }

                    // Add the current character as a token (operator, parentheses, etc.)
                    if (!Character.isWhitespace(c)) {
                        tokens.add(Character.toString(c));
                    }
                }
            }
        }

        // Add the last number in the buffer as a token
        if (numberBuffer.length() > 0) {
            tokens.add(numberBuffer.toString());
        }

        return tokens;
    }

    private static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }



    private static double evaluateTokens(List<String> tokens) {
        Stack<Double> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                values.push(Double.parseDouble(token));
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && hasPrecedence(token, operators.peek())) {
                    double b = values.pop();
                    double a = values.pop();
                    String op = operators.pop();
                    values.push(applyOperator(a, b, op));
                }
                operators.push(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    double b = values.pop();
                    double a = values.pop();
                    String op = operators.pop();
                    values.push(applyOperator(a, b, op));
                }
                if (!operators.isEmpty() && operators.peek().equals("(")) {
                    operators.pop();
                }
            } else {
                throw new IllegalArgumentException("Invalid token: " + token);
            }
        }

        while (!operators.isEmpty()) {
            double b = values.pop();
            double a = values.pop();
            String op = operators.pop();
            values.push(applyOperator(a, b, op));
        }

        return values.pop();
    }

    private static double applyOperator(double a, double b, String operator) {
        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) {
                    return Double.POSITIVE_INFINITY; // Handle division by zero
                }
                return a / b;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private static boolean hasPrecedence(String op1, String op2) {
        if ((op1.equals("*") || op1.equals("/")) && (op2.equals("+") || op2.equals("-"))) {
            return false;
        }
        return true;
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
            double b = (Double) numbers.pop();
            double a = (Double) numbers.pop();
            char op = (Character) operators.pop();
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
