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
    }

    @Test
    public void testComputeForm() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "10");
        String form = "=A0+5";
        double result = Utils.computeForm(form, sheet);
        assertEquals(15.0, result);
    }

    @Test
    public void testReplaceCellReferences() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "10.0");
        String replaced = Utils.replaceCellReferences("A0+5", sheet);
        assertEquals("10.0 +5", replaced);
    }

    @Test
    public void testParenthesesBalanced() {
        assertTrue(Utils.areParenthesesBalanced("(5+3)*(2-1)"));
        assertFalse(Utils.areParenthesesBalanced("(5+3)*2-1)"));
        assertFalse(Utils.areParenthesesBalanced("(5+3*(2-1"));
    }
}
