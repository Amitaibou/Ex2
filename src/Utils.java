import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;


public class Utils {


    public Utils() {
    }

    // check if input is a valid number
    public static boolean isNumber(String input) {
        if (input != null && !input.trim().isEmpty()) {
            try {
                Double.parseDouble(input); // try to parse input as a double
                return true; // valid number
            } catch (NumberFormatException var2) {
                return false; // not a number
            }
        } else {
            return false; // input is null or empty
        }
    }

    // check if input is valid text according to the assignment request
    static boolean isText(String s) {
        return !isNumber(s) && !isForm(s) && !s.startsWith("=");
    }


    // check if input is a valid formula
    public static boolean isForm(String input) {

        String dontAllowedCharacters = "!@#$%^&?";
        if (input != null && input.startsWith("=")) {
            String formula = input.substring(1).trim(); // remove '=' from formula

            // check if parentheses are balanced
            if (!areParenthesesBalanced(formula)) {
                return false; // unbalanced parentheses
            } else if (formula.matches(".*([+\\-*/]{2,}).*")) { // invalid operator sequences
                return false;
            } else {
                for(char c: dontAllowedCharacters.toCharArray()){
                    if(input.contains(c +"")) return false;
                }
                return !formula.isEmpty(); // valid if not empty
            }
        } else {
            return false; // input is not a formula
        }

    }

    public static boolean isValidCell(String cell, Ex2Sheet ex2Sheet){
        try{
            ex2Sheet.parseCoordinates(cell);
        }catch (Exception e){
            return false;
        }
        return true;

    }
    // check if input is a valid formula
    public static boolean isForm(String input, Ex2Sheet ex2Sheet) {
        List<String> destCells = extractCellReferences(input);
        for ( String cell : destCells){
            if(isValidCell(cell, ex2Sheet)) {
                String eval = ex2Sheet.eval(ex2Sheet.parseCoordinates(cell)[0], ex2Sheet.parseCoordinates(cell)[1]);
                if (eval.equals(Ex2Utils.EMPTY_CELL)) {
                    return false;
                } else if (eval.equals(Ex2Utils.ERR_CYCLE)) {
                    return false;
                }
            }
        }

        String dontAllowedCharacters = "!@#$%^&?";
        if (input != null && input.startsWith("=")) {
            String formula = input.substring(1).trim(); // remove '=' from formula

            // check if parentheses are balanced
            if (!areParenthesesBalanced(formula)) {
                return false; // unbalanced parentheses
            } else if (formula.matches(".*([+\\-*/]{2,}).*")) { // invalid operator sequences
                return false;
            } else {
                for(char c: dontAllowedCharacters.toCharArray()){
                    if(input.contains(c +"")) return false;
                }
                return !formula.isEmpty(); // valid if not empty
            }
        } else {
            return false; // input is not a formula
        }

    }



    public static List<String> extractCellReferences(String formula) {
        List<String> cellReferences = new ArrayList<>();
        StringBuilder currentReference = new StringBuilder();

        for (char ch : formula.toCharArray()) {
            // Add letters or digits to the current reference
            if (Character.isLetter(ch) || Character.isDigit(ch)) {
                currentReference.append(ch);
            } else {
                // If we hit a non-alphanumeric character, save the current reference
                if (currentReference.length() > 0) {
                    cellReferences.add(currentReference.toString());
                    currentReference.setLength(0); // Reset for the next reference
                }
            }
        }

        // Add the last reference if there is any
        if (currentReference.length() > 0) {
            cellReferences.add(currentReference.toString());
        }

        return cellReferences;
    }



    // compute the result of a formula
    public static double computeForm(String form, Ex2Sheet sheet) {
        if (form == null || !form.startsWith("=")) {
            throw new IllegalArgumentException("Invalid formula: " + form);
        }

        // remove '=' from formula
        String formula = form.substring(1).trim();

        try {
            // replace cell references (like A1) with actual values
            String replacedFormula = replaceCellReferences(formula, sheet);
            double result = evalFormula(replacedFormula); // evaluate the formula
            return result;
        } catch (Exception e) {
            System.err.println("Error in formula computation: " + e.getMessage());
            throw new IllegalArgumentException("Error computing formula: " + form);
        }
    }

    // replace cell references (e.g., A1) with actual cell values
    public static String replaceCellReferences(String formula, Ex2Sheet sheet) {
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < formula.length()) {
            char c = formula.charAt(i);

            // handle cell references like A1, B2
            if (Character.isLetter(c)) {
                int j = i;
                while (j < formula.length() && Character.isLetterOrDigit(formula.charAt(j))) {
                    j++;
                }

                String cellRef = formula.substring(i, j); // extract cell reference
                cellRef = cellRef.toUpperCase(); // ensure uppercase for consistency

                int[] coords = sheet.parseCoordinates(cellRef); // get cell coordinates
                String cellValue = sheet.eval(coords[0], coords[1]); // evaluate the cell value

                if (cellValue == null || cellValue.trim().isEmpty()) {
                    throw new IllegalArgumentException("Formula contains a reference to an empty cell: " + cellRef);
                }

                result.append(cellValue); // append the cell value
                i = j;
            } else {
                result.append(c); // append non-cell characters
                i++;
            }
        }

        return result.toString(); // return formula with replaced values
    }

    // check if parentheses in formula are balanced
    public static boolean areParenthesesBalanced(String formula) {
        int count = 0;
        for (char c : formula.toCharArray()) {
            if (c == '(') {
                count++;
            } else if (c == ')') {
                count--;
            }

            if (count < 0) { // more closing parentheses than opening
                return false;
            }
        }

        return count == 0; // balanced if count is zero
    }

    // evaluate a formula as a string
    public static double evalFormula(String formula) {
        formula = formula.trim();

        try {
            List<String> tokens = tokenizeFormula(formula); // split formula into tokens
            return evaluateTokens(tokens); // evaluate tokenized formula
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid formula: " + formula);
        }
    }

    // split formula into tokens (numbers, operators, etc.)
    private static List<String> tokenizeFormula(String formula) {
        List<String> tokens = new ArrayList<>();
        StringBuilder numberBuffer = new StringBuilder();

        for (int i = 0; i < formula.length(); ++i) {
            char c = formula.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                numberBuffer.append(c); // append digits or decimals to buffer
            } else {
                if (c == '-' && (tokens.isEmpty() || isOperator(tokens.get(tokens.size() - 1)) || tokens.get(tokens.size() - 1).equals("("))) {
                    numberBuffer.append(c); // handle negative numbers
                } else {
                    if (numberBuffer.length() > 0) {
                        tokens.add(numberBuffer.toString()); // add number to tokens
                        numberBuffer.setLength(0);
                    }

                    if (!Character.isWhitespace(c)) {
                        tokens.add(Character.toString(c)); // add operator or parentheses
                    }
                }
            }
        }

        if (numberBuffer.length() > 0) {
            tokens.add(numberBuffer.toString()); // add the last number
        }

        return tokens;
    }

    // check if token is an operator
    private static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    // evaluate tokens using stacks
    private static double evaluateTokens(List<String> tokens) {
        Stack<Double> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                values.push(Double.parseDouble(token)); // push numbers
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && hasPrecedence(token, operators.peek()) && !operators.peek().equals("(")) {
                    double b = values.pop();
                    double a = values.pop();
                    String op = operators.pop();
                    values.push(applyOperator(a, b, op)); // apply operator
                }
                operators.push(token); // push current operator
            } else if (token.equals("(")) {
                operators.push(token); // push opening parenthesis
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    double b = values.pop();
                    double a = values.pop();
                    String op = operators.pop();
                    values.push(applyOperator(a, b, op)); // apply operator
                }
                if (!operators.isEmpty() && operators.peek().equals("(")) {
                    operators.pop(); // pop opening parenthesis
                }
            } else {
                throw new IllegalArgumentException("Invalid token: " + token);
            }
        }

        while (!operators.isEmpty()) {
            double b = values.pop();
            double a = values.pop();
            String op = operators.pop();
            values.push(applyOperator(a, b, op)); // final operation
        }

        return values.pop(); // final result
    }

    // apply operator to two numbers
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
                    return Double.POSITIVE_INFINITY; // handle division by zero
                }
                return a / b;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    // check operator precedence
    private static boolean hasPrecedence(String op1, String op2) {
        if ((op1.equals("*") || op1.equals("/")) && (op2.equals("+") || op2.equals("-"))) {
            return false;
        }
        return true;
    }
}