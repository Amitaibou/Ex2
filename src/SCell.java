import java.util.List;

/**
 * Represents a single cell in the spreadsheet.
 * Each cell can contain a string, a number, or a formula.
 */
public class SCell implements Cell {
    private String line; // the content of the cell
    private int type; // the type of the cell (TEXT, NUMBER, FORM,...)
    private int order; // the evaluation order of the cell
    private String err = "";// error message for the cell
    private Ex2Sheet ex2Sheet;
    private String cellName;

    // constructor to initialize the cell with a given value
    public SCell(String s, Ex2Sheet ex2Sheet, String cellName) {
        //setError(s); // set error message
        this.ex2Sheet = ex2Sheet;
        this.cellName = cellName;
        setData(s); // set the content of the cell
        setOrder(0); // default order is 0

    }

    // set the error message for the cell
    public void setError(String err) {
        this.err = err;
    }

    // get the error message for the cell
    public String getErr() {
        return err;
    }

    // get the evaluation order of the cell
    @Override
    public int getOrder() {
        return order;
    }

    // get the raw content (line) of the cell
    @Override
    public String getLine() {
        return line;
    }

    // return the data of the cell as a string
    @Override
    public String toString() {
        return getData();
    }

    // set the content of the cell and determine its type
    @Override
    public void setData(String s) {
        if (s == null || s.trim().isEmpty()) {
            type = Ex2Utils.TEXT; // if cell is empty its text
            line = Ex2Utils.EMPTY_CELL;
            setError("");

        } else if (Utils.isForm(s, ex2Sheet)) {

            List<String> cells = Utils.extractCellReferences(s);
            if (!cells.isEmpty()) {

                for (String cell : cells) {
                    Cell c = ex2Sheet.get(cell);
                    List<String> destCells = Utils.extractCellReferences(c.getData());
                    for (String destCell : destCells) {
                        if (this.cellName == destCell) {
                            type = Ex2Utils.ERR_CYCLE_FORM;
                            setError(Ex2Utils.ERR_CYCLE);
                            return;
                        } else {
                            type = Ex2Utils.FORM; // formula starts with '='setError("");
                            setError("");

                        }
                    }
                }
                type = Ex2Utils.FORM;
                setError("");
            } else {
                type = Ex2Utils.FORM; // formula starts with '='
                setError("");
            }
            line = s;

        } else if (!Utils.isForm(s, ex2Sheet) && s.startsWith("=")) {
            List<String> cells = Utils.extractCellReferences(s);
            if (!cells.isEmpty()) {
                for (String cell : cells) {
                    if (this.cellName.equals(cell)) {
                        type = Ex2Utils.ERR_CYCLE_FORM;
                        setError(Ex2Utils.ERR_CYCLE);

                    } else {
                        type = Ex2Utils.ERR_FORM_FORMAT;
                        setError(Ex2Utils.ERR_FORM);

                    }
                }
            } else {
                type = Ex2Utils.ERR_FORM_FORMAT;
                setError(Ex2Utils.ERR_FORM);

            }
            line = s;

        } else if (Utils.isNumber(s)) {
            type = Ex2Utils.NUMBER; // cell contains a valid number
            line = Double.valueOf(s).toString();
            setError("");
        } else if (Utils.isText(s)) {
            type = Ex2Utils.TEXT; // valid text content
            line = s;
            setError("");
        } else {
            type = Ex2Utils.ERR_CYCLE_FORM;// invalid format
            line = s;
            setError(Ex2Utils.ERR_CYCLE); // set error message
        }


    }


    // return the data of the cell; prioritize error message if it exists
    @Override
    public String getData() {
        if (err != "") return err;
        return line;
    }

    // get the type of the cell (... TEXT, NUMBER, FORM)
    @Override
    public int getType() {
        return type;
    }

    // set the type of the cell
    @Override
    public void setType(int t) {
        type = t;
    }

    // set the evaluation order of the cell
    @Override
    public void setOrder(int t) {
        order = t;
    }
}