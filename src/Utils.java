import java.util.*;

public class Utils {

    /**
     * Checks if the input string is a valid number.
     * @param input The string to validate.
     * @return True if the input is a valid number, otherwise false.
     */
    public static boolean isNumber(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    /**
     * Checks if the input string is valid text (not a number, not a formula).
     * @param input The string to validate.
     * @return True if the input is valid text, otherwise false.
     */
    public static boolean isText(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        // בדיקה אם יש רצף לא חוקי של אופרטורים
        if (input.matches(".*([+\\-*/]{2,}).*")) {
            return false;
        }

        // בדיקה אם הטקסט מכיל מספרים עשרוניים או שלמים עם פעולות חוקיות
        if (input.matches("[0-9.]+([+\\-*/][0-9.]+)*")) {
            return true;
        }

        // בדיקה אם זה טקסט שאינו מספר או פורמולה
        return input.matches("[A-Za-z0-9]+") && !isNumber(input) && !input.startsWith("=");
    }





    /**
     * Validates if the input string is a valid formula.
     * @param input The formula to validate.
     * @return True if the formula is valid, otherwise false.
     */
    public static boolean isForm(String input) {
        if (input == null || !input.startsWith("=")) {
            return false;
        }
        String formula = input.substring(1).trim();

        // בדיקה אם סוגריים לא מאוזנים
        if (!areParenthesesBalanced(formula)) {
            return false;
        }

        // בדיקה אם יש רצף לא חוקי של אופרטורים
        if (formula.matches(".*([+\\-*/]{2,}).*")) {
            return false;
        }

        return !formula.isEmpty();
    }

    /**
     * Replaces all cell references in the formula with their actual values from the spreadsheet.
     * @param formula The formula containing cell references.
     * @param sheet The spreadsheet to retrieve cell values from.
     * @return The formula with cell references replaced by their values.
     */
    public static String replaceCellReferences(String formula, Ex2Sheet sheet) {
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < formula.length()) {
            char c = formula.charAt(i);

            if (Character.isLetter(c)) {
                // Start of a cell reference
                int j = i;
                while (j < formula.length() && (Character.isLetterOrDigit(formula.charAt(j)))) {
                    j++;
                }

                String cellRef = formula.substring(i, j);
                int[] coords = sheet.parseCoordinates(cellRef);
                String cellValue = sheet.value(coords[0], coords[1]); // Use value to fetch the evaluated value

                if (cellValue == null || cellValue.trim().isEmpty()) {
                    throw new IllegalArgumentException("Invalid or empty reference in formula: " + cellRef);
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


    /**
     * Checks if parentheses in a formula are balanced.
     * @param formula The formula to check.
     * @return True if parentheses are balanced, otherwise false.
     */
    private static boolean areParenthesesBalanced(String formula) {
        int count = 0;
        for (char c : formula.toCharArray()) {
            if (c == '(') count++;
            else if (c == ')') count--;
            if (count < 0) return false;
        }
        return count == 0;
    }

    /**
     * Evaluates a mathematical formula as a string and returns its result.
     * @param formula The formula to evaluate.
     * @return The result of the formula.
     */
    public static double evalFormula(String formula) {
        formula = formula.trim();

        try {
            // Tokenize the formula and evaluate it
            List<String> tokens = tokenizeFormula(formula);
            return evaluateTokens(tokens);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid formula: " + formula);
        }
    }



    /**
     * Tokenizes a formula into numbers, operators, and parentheses.
     * @param formula The formula to tokenize.
     * @return A list of tokens.
     */
    private static List<String> tokenizeFormula(String formula) {
        List<String> tokens = new ArrayList<>();
        StringBuilder numberBuffer = new StringBuilder();

        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                numberBuffer.append(c);
            } else {
                if (numberBuffer.length() > 0) {
                    tokens.add(numberBuffer.toString());
                    numberBuffer.setLength(0);
                }

                if (!Character.isWhitespace(c)) {
                    tokens.add(Character.toString(c));
                }
            }
        }

        if (numberBuffer.length() > 0) {
            tokens.add(numberBuffer.toString());
        }

        return tokens;
    }

    /**
     * Evaluates a list of tokens representing a mathematical formula.
     * @param tokens The list of tokens.
     * @return The result of the formula.
     */
    private static double evaluateTokens(List<String> tokens) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                numbers.push(Double.parseDouble(token));
            } else if (token.equals("(")) {
                operators.push('(');
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    processTopOperator(numbers, operators);
                }
                if (!operators.isEmpty() && operators.peek() == '(') {
                    operators.pop();
                } else {
                    throw new IllegalArgumentException("Mismatched parentheses in formula.");
                }
            } else if (isOperator(token.charAt(0))) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token.charAt(0))) {
                    processTopOperator(numbers, operators);
                }
                operators.push(token.charAt(0));
            }
        }

        while (!operators.isEmpty()) {
            processTopOperator(numbers, operators);
        }

        if (numbers.size() != 1) {
            throw new IllegalArgumentException("Invalid formula structure.");
        }

        return numbers.pop();
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return -1;
        }
    }

    private static void processTopOperator(Stack<Double> numbers, Stack<Character> operators) {
        if (numbers.size() < 2) {
            throw new IllegalArgumentException("Invalid formula: missing operands.");
        }
        double b = numbers.pop();
        double a = numbers.pop();
        char op = operators.pop();
        numbers.push(applyOperator(op, a, b));
    }

    private static double applyOperator(char operator, double left, double right) {
        switch (operator) {
            case '+':
                return left + right;
            case '-':
                return left - right;
            case '*':
                return left * right;
            case '/':
                if (right == 0) {
                    throw new ArithmeticException("Division by zero is not allowed");
                }
                return left / right;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}
