import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    @Test
    public void testIsNumber() {
        assertTrue(Utils.isNumber("123"));
        assertTrue(Utils.isNumber("123.45"));
        assertFalse(Utils.isNumber("abc"));
        assertFalse(Utils.isNumber(null));
        assertFalse(Utils.isNumber(""));
    }

    @Test
    public void testIsText() {
        assertTrue(Utils.isText("hello"));
        assertTrue(Utils.isText("A1B2"));
        assertTrue(Utils.isText("hello123"));
        assertFalse(Utils.isText("123"));
        assertFalse(Utils.isText("=A1+5"));
        assertFalse(Utils.isText("123.45"));
        assertTrue(Utils.isText("!@#$%^"));
    }

    @Test
    public void testIsForm() {
        assertTrue(Utils.isForm("=A1+5"));
        assertFalse(Utils.isForm("A1+5"));
        assertFalse(Utils.isForm("=5++5"));
        assertTrue(Utils.isForm("=5-(-A1)"));
    }

    @Test
    public void testComputeForm() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "10");
        sheet.set(1, 0, "-5");
        String form = "=A0+5";
        assertEquals(15.0, Utils.computeForm(form, sheet));

        form = "=A0+B0";
        assertEquals(5.0, Utils.computeForm(form, sheet));
    }

    @Test
    public void testReplaceCellReferences() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "10.0");
        sheet.set(1, 0, "-5");
        String replaced = Utils.replaceCellReferences("A0+B0", sheet);
        assertEquals("10.0+-5.0", replaced);
    }

    @Test
    public void testParenthesesBalanced() {
        assertTrue(Utils.areParenthesesBalanced("(5+3)*(2-1)"));
        assertFalse(Utils.areParenthesesBalanced("(5+3)*2-1)"));
        assertFalse(Utils.areParenthesesBalanced("(5+3*(2-1"));
    }

    @Test
    public void testEmptyCellReference() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        String form = "=A1+5"; // A1 is empty
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Utils.computeForm(form, sheet);
        });
        assertTrue(exception.getMessage().contains("empty cell"));
    }

    @Test
    public void testCircularReference() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "=a0");
        sheet.set(1, 0, "=bo");
        String result = sheet.eval(0, 0);
        assertEquals(Ex2Utils.ERR_CYCLE, result);
    }

    @Test
    public void testNegativeNumbersInFormula() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "-10");
        sheet.set(1, 0, "=A0+5");
        String result = sheet.eval(1, 0);
        assertEquals("-5.0", result);
    }

    @Test
    public void testCaseInsensitiveCellNames() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "10");
        sheet.set(1, 0, "=a0+5"); // Lowercase reference
        assertEquals("15.0", sheet.eval(1, 0));

        sheet.set(2, 0, "=A0+5"); // Uppercase reference
        assertEquals("15.0", sheet.eval(2, 0));
    }

    @Test
    public void testComplexFormula() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "10.0");
        sheet.set(1, 0, "5.0");
        sheet.set(2, 0, "=A0*B0+5");
        assertEquals("55.0", sheet.eval(2, 0));
    }

    @Test
    public void testInvalidFormulaCharacters() {
        String invalidFormula = "=A1+5&";
        assertFalse(Utils.isForm(invalidFormula));

        invalidFormula = "=A1@5";
        assertFalse(Utils.isForm(invalidFormula));
    }
}
