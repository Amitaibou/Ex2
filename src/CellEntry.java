/**
 * Represents a specific entry in a spreadsheet.
 * Each entry is defined by a 2D index (x, y) and can be validated based on spreadsheet rules.
 */
public class CellEntry implements Index2D {
    private int x; // The x-coordinate (column index) of the cell.
    private int y; // The y-coordinate (row index) of the cell.
    private Cell cell; // The cell object associated with this entry.


    /**
     * Constructs a CellEntry with the specified coordinates and associated Cell object.
     *
     * @param xx The x-coordinate (column index) of the cell.
     * @param yy The y-coordinate (row index) of the cell.
     *           //     * @param c The Cell object associated with this entry.
     */
    public CellEntry(int xx, int yy) {
        this.x = xx;
        this.y = yy;
//        this.cell = c;
    }

    /**
     * Returns the cell index in spreadsheet format (e.g., "B3").
     *
     * @return The string representation of this cell's index in spreadsheet format.
     */
//    @Override
//    public String toString() {
//        // Convert x (0-based) to a letter ('A'-'Z').
//        char columnLetter = (char) ('A' + x);
//        return columnLetter + String.valueOf(y);
//    }

    /**
     * Checks if the string representation of this index is valid in the form "XY",
     * where X is a letter ('A'-'Z') and Y is an integer in the range [0-99].
     *
     * @return True if the index is valid, otherwise false.
     */
    @Override
    public boolean isValid() {
        // Check x is within valid column range ('A' to 'Z').
//        if (x < 0 || x > 25)
        {
            return false;
        }
        // Check y is within valid row range [0-99].
//        return y >= 0 && y <= 99;
    }

    /**
     * Returns the x-coordinate (column index) of this cell.
     *
     * @return The x-coordinate of this cell.
     */
    @Override
    public int getX() {
        return this.x;
    }

    /**
     * Returns the y-coordinate (row index) of this cell.
     *
     * @return The y-coordinate of this cell.
     */
    @Override
    public int getY() {
        return this.y;
    }

    public String toString() {
        return Ex2Utils.ABC[this.x] + this.y;
    }
}
//    /**
//     * Returns the associated Cell object.
//     *
//     * @return The Cell object associated with this entry.
//     */
//    public Cell getCell() {
//        return cell;
//    }
//
//    /**
//     * Sets a new Cell object for this entry.
//     *
//     * @param cell The new Cell object to associate with this entry.
//     */
//    public void setCell(Cell cell) {
//        this.cell = cell;
//    }
//
//    /**
//     * Checks if the current entry depends on another cell.
//     *
//     * @param other The other CellEntry to check dependency against.
//     * @return True if this entry depends on the other, otherwise false.
//     */
//    public boolean dependsOn(CellEntry other) {
//        if (this.cell == null || other == null || !Utils.isForm(this.cell.getData())) {
//            return false; // If this cell or the other is not a formula, there is no dependency.
//        }
//        String formula = this.cell.getData().substring(1); // Remove '='.
//        String otherCoordinates = other.toString(); // Get the string representation of the other cell (e.g., "B3").
//        return formula.contains(otherCoordinates); // Check if the formula contains a reference to the other cell.
//    }

