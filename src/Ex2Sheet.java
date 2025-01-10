import java.io.*;
import java.util.*;

public class Ex2Sheet implements Sheet {
    private Cell[][] table;
    private int width;
    private int height;

    // constructor to initialize the sheet with the given width and height
    public Ex2Sheet(int width, int height) {
        this.width = width;
        this.height = height;
        this.table = new Cell[width][height];

        // initialize all cells with empty content
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
    }

    // check if a given cell is within the valid range
    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // return the width of the sheet
    @Override
    public int width() {
        return width;
    }

    // return the height of the sheet
    @Override
    public int height() {
        return height;
    }

    // set a value to a specific cell
    @Override
    public void set(int x, int y, String c) {
        if (!isIn(x, y)) throw new IllegalArgumentException("Invalid cell coordinates");

        // שימוש ב-convertCoordinatesToCellName להדפסת שם התא
        String cellName = convertCoordinatesToCellName(x, y);
        System.out.println("Updating cell " + cellName + " with value: " + c);

        table[x][y] = new SCell(c); // update the cell content
        eval(); // reevaluate all cells in the sheet
    }



    // get a cell by its coordinates
    @Override
    public Cell get(int x, int y) {
        if (!isIn(x, y)) return null;
        return table[x][y];
    }

    // get a cell by its name ("b1","A1")
    @Override
    public Cell get(String entry) {
        int[] coords = parseCoordinates(entry); // parse name to coordinates
        return get(coords[0], coords[1]); // use get(int x, int y)
    }


    // return the evaluated value of a cell
    @Override
    public String value(int x, int y) {
        Cell cell = get(x, y);
        if (cell == null) return Ex2Utils.EMPTY_CELL; // return empty if the cell is null
        return eval(x, y); // evaluate the cell and return its value
    }

    private Set<String> evaluatingCells = new HashSet<>(); // track cells currently being evaluated

    // evaluate a specific cell
    @Override
    public String eval(int x, int y) {
        Cell cell = get(x, y);
        String cellName = convertCoordinatesToCellName(x, y);

        // check for circular references
        if (evaluatingCells.contains(cellName)) {
            cell.setType(Ex2Utils.ERR_CYCLE_FORM); // set the cell type to cycle error
            return Ex2Utils.ERR_CYCLE; // return cycle error
        }
        evaluatingCells.add(cellName); // mark the cell as being evaluated

        if (cell == null || cell.getData().isEmpty()) {
            evaluatingCells.remove(cellName); // remove the cell from evaluation tracking
            return Ex2Utils.EMPTY_CELL; // return empty value
        }

        // if the cell contains a formula, calculate its value
        if (cell.getType() == Ex2Utils.FORM) {
            try {
                double result = Utils.computeForm(cell.getData(), this); // calculate the formula
                evaluatingCells.remove(cellName);  // remove from tracking
                return Double.toString(result); // return the result as string
            } catch (IllegalArgumentException e) {
                handleInvalidFormula(cell, cellName); // handle invalid formulas
                return Ex2Utils.ERR_FORM; // return formula error
            }
        }
        evaluatingCells.remove(cellName); // remove from tracking after processing
        return cell.getData(); // return raw data for non-formula cells
    }


    // handle invalid formulas
    private void handleInvalidFormula(Cell cell, String cellName) {
        if (get(cellName) != cell) {
            System.out.println("Invalid formula in cell " + cellName);
            cell.setType(Ex2Utils.ERR_FORM_FORMAT); // set the type to formula format error
            cell.setData(Ex2Utils.ERR_FORM); // set the cell content to error
            evaluatingCells.remove(cellName); // remove from evaluation tracking
        }
    }


    @Override
    // reevaluate all cells in the sheet
    public void eval() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                eval(i, j); // reevaluate each cell
            }
        }
    }

    @Override
    // calculate the depth of dependencies for all cells
    public int[][] depth() {
        int[][] depths = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                depths[i][j] = computeDepth(i, j, new HashSet<>());
            }
        }
        return depths;
    }

    // recursively compute the depth of dependencies for a cell
    private int computeDepth(int x, int y, Set<String> visited) {
        if (!isIn(x, y)) return 0;
        Cell cell = get(x, y);
        if (cell == null || cell.getType() != Ex2Utils.FORM) return 0;

        String cellKey = x + "," + y;
        if (visited.contains(cellKey)) return -1; // detect circular references

        visited.add(cellKey);
        String formula = cell.getData().substring(1); // remove '=' from the formula
        int maxDepth = 0;
        // split formula into references
        for (String ref : formula.split("[^A-Za-z0-9]+")) {
            // check if valid cell reference
            if (ref.matches("[A-Z][0-9]+")) {
                int[] coords = parseCoordinates(ref);
                int depth = computeDepth(coords[0], coords[1], visited);
                if (depth == -1) return -1; // propagate circular reference
                maxDepth = Math.max(maxDepth, depth);
            }
        }

        visited.remove(cellKey); // remove from visited set
        return maxDepth + 1; // return the depth
    }

    @Override
    // save the sheet to a file
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
    // load a sheet from a file
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length >= 3) {
                    try {
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        String data = parts[2];
                        set(x, y, data); // set the value to the cell
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        eval(); // reevaluate after loading
    }

    // parse cell reference (e.g., "A1") into coordinates
    public int[] parseCoordinates(String cellRef) {
        if (cellRef == null || cellRef.trim().isEmpty()) {
            throw new IllegalArgumentException("Cell reference cannot be null or empty.");
        }

        char col = cellRef.charAt(0);
        if (col < 'A' || col > 'Z') {
            throw new IllegalArgumentException("Invalid column in cell reference: " + cellRef);
        }

        String rowPart = cellRef.substring(1); // extract the row part
        try {
            int row = Integer.parseInt(rowPart); // parse the row
            int colIndex = col - 'A';// calculate the column index
            return new int[]{colIndex, row}; // return as [column, row]
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row in cell reference: " + cellRef);
        }
    }

    public static String convertCoordinatesToCellName(int x, int y) {
        char column = (char) ('A' + x); // col by x
        return column + Integer.toString(y); // row by y
    }

}