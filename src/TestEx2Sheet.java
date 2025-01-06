public class TestEx2Sheet {
        public static void main(String[] args) {
            // יצירת אובייקט גיליון
            Ex2Sheet sheet = new Ex2Sheet();

            // בדיקת ערכים פשוטים ותאים
            sheet.set(0, 0, "10"); // A0 = 10
            sheet.set(0, 1, "20"); // A1 = 20
            sheet.set(0, 2, "=A0+A1"); // A2 = =A0+A1

            System.out.println("Value of A0: " + sheet.eval(0, 0)); // צפי: 10
            System.out.println("Value of A1: " + sheet.eval(0, 1)); // צפי: 20
            System.out.println("Value of A2: " + sheet.eval(0, 2)); // צפי: 30

            // נוסחאות מורכבות
            sheet.set(0, 3, "=A2*2"); // A3 = =A2*2
            System.out.println("Value of A3: " + sheet.eval(0, 3)); // צפי: 60

            // טיפול בשגיאות
            sheet.set(0, 4, "=A3+A5"); // A4 = =A3+A5 (A5 לא מוגדר)
            try {
                System.out.println("Value of A4: " + sheet.eval(0, 4));
            } catch (Exception e) {
                System.out.println("Error in A4: " + e.getMessage());
            }
        }
    }

