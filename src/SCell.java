/**
 * Represents a single cell in the spreadsheet.
 * Each cell can contain a string, a number, or a formula.
 */
public class SCell implements Cell {
    private String line; // the content of the cell
    private int type; // the type of the cell (TEXT, NUMBER, FORM,...)
    private int order; // the evaluation order of the cell
    private String err = ""; // error message for the cell

    // constructor to initialize the cell with a given value
    public SCell(String s) {
        //setError(s); // set error message
        setData(s); // set the content of the cell
        setOrder(0); // default order is 0
    }

    // set the error message for the cell
    public void setError(String err) {
        this.err = err;
    }

    // get the error message for the cell
    public String getErr( ) {
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
        } else if (Utils.isForm(s)) {
            type = Ex2Utils.FORM; // formula starts with '='
            line = s;
        } else if (!Utils.isForm(s) && s.startsWith("=")){
            type = Ex2Utils.ERR_FORM_FORMAT;
            line = Ex2Utils.ERR_FORM;
        } else if (Utils.isNumber(s)) {
            type = Ex2Utils.NUMBER; // cell contains a valid number
            line = Double.valueOf(s).toString();
        } else if (Utils.isText(s)) {
            type = Ex2Utils.TEXT; // valid text content
            line = s;
        } else {
            type = Ex2Utils.ERR_FORM_FORMAT; // invalid format
            setError(Ex2Utils.ERR_FORM); // set error message
        }
    }




    // return the data of the cell; prioritize error message if it exists
    @Override
    public String getData() {
        if(err != null) return err == line ? err : line;
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
