import java.io.*;
import java.util.*;

public class Ex2Sheet implements Sheet {
    private Cell[][] table;
    private int width;
    private int height;

    public Ex2Sheet(int width, int height) {
        this.width = width;
        this.height = height;
        this.table = new Cell[width][height];

        // Initialize all cells to be empty
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public void set(int x, int y, String c) {
        if (!isIn(x, y)) throw new IllegalArgumentException("Invalid cell coordinates");
        table[x][y] = new SCell(c);
        eval(); // Recalculate the spreadsheet values after setting
    }

    @Override
    public Cell get(int x, int y) {
        if (!isIn(x, y)) return null;
        return table[x][y];
    }

    @Override
    public Cell get(String entry) {
        int[] coords = parseCoordinates(entry);
        return get(coords[0], coords[1]);
    }

    @Override
    public String value(int x, int y) {
        Cell cell = get(x, y);
        if (cell == null) return Ex2Utils.EMPTY_CELL;
        return eval(x, y); // Evaluate the cell and return its value
    }

    private Set<String> evaluatingCells = new HashSet<>();

    @Override
    public String eval(int x, int y) {
        Cell cell = get(x, y);
        if (cell == null || cell.getData() == null || cell.getData().isEmpty()) {
            return Ex2Utils.EMPTY_CELL;
        }

        if (cell.getType() == Ex2Utils.FORM) {
            try {
                String formula = cell.getData().substring(1).trim(); // Remove '='
                String replacedFormula = Utils.replaceCellReferences(formula, this);
                double result = Utils.evalFormula(replacedFormula);
                return Double.toString(result); // Return the computed result as a string
            } catch (IllegalArgumentException e) {
                cell.setType(Ex2Utils.ERR_FORM_FORMAT); // Mark as format error
                return Ex2Utils.ERR_FORM;
            }
        }
        return cell.getData(); // Return raw data for non-formula cells
    }





    @Override
    public void eval() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                eval(i, j); // Reevaluate each cell
            }
        }
    }

    @Override
    public int[][] depth() {
        int[][] depths = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                depths[i][j] = computeDepth(i, j, new HashSet<>());
            }
        }
        return depths;
    }

    private int computeDepth(int x, int y, Set<String> visited) {
        if (!isIn(x, y)) return 0;
        Cell cell = get(x, y);
        if (cell == null || cell.getType() != Ex2Utils.FORM) return 0;

        String cellKey = x + "," + y;
        if (visited.contains(cellKey)) return -1; // Circular reference detected

        visited.add(cellKey);
        String formula = cell.getData().substring(1); // Remove '=' from the formula
        int maxDepth = 0;

        for (String ref : formula.split("[^A-Za-z0-9]+")) {
            if (ref.matches("[A-Z][0-9]+")) {
                int[] coords = parseCoordinates(ref);
                int depth = computeDepth(coords[0], coords[1], visited);
                if (depth == -1) return -1; // Propagate circular reference
                maxDepth = Math.max(maxDepth, depth);
            }
        }

        visited.remove(cellKey);
        return maxDepth + 1;
    }

    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment - this line should be ignored\n");
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Cell cell = table[i][j];
                    if (cell != null && cell.getData() != null && !cell.getData().trim().isEmpty()) {
                        writer.write(i + "," + j + "," + cell.getData() + "\n");
                    }
                }
            }
        }
    }

    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length >= 3) {
                    try {
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        String data = parts[2];
                        set(x, y, data);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        eval(); // Recalculate after loading
    }

    public int[] parseCoordinates(String cellRef) {
        if (cellRef == null || cellRef.trim().isEmpty()) {
            throw new IllegalArgumentException("Cell reference cannot be null or empty.");
        }

        char col = cellRef.charAt(0);
        if (col < 'A' || col > 'Z') {
            throw new IllegalArgumentException("Invalid column in cell reference: " + cellRef);
        }

        String rowPart = cellRef.substring(1);
        int row = Integer.parseInt(rowPart);

        return new int[]{row, col - 'A'};
    }
}
