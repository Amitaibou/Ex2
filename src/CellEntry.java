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
    }

    /**
     * Returns the cell index in spreadsheet format (e.g., "B3").
     *
     * @return The string representation of this cell's index in spreadsheet format.
     */
//    }

    /**
     * Checks if the string representation of this index is valid in the form "XY",
     * where X is a letter ('A'-'Z') and Y is an integer in the range [0-99].
     *
     * @return True if the index is valid, otherwise false.
     */
    @Override
    public boolean isValid() {
        {
            return false;
        }
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
