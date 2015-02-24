package hastakabul;

public class TCKNoValidation {
    public TCKNoValidation() {
        super();
    }    
    public static boolean  validate(String tckno) throws Exception {
        if (tckno == null)
            return false;
        if (tckno.length() != 11)
            return false;

        long tckNo = 0L;

        try {
            tckNo = Long.parseLong(tckno);
        } catch (NumberFormatException nfe) {
            return false;
        }

        if (tckNo <= 0L)
            return false;

        long ilk8hane = tckNo / 100L;
        long son2hane = tckNo % 100L;
        String strIlk8Hane = Long.toString(tckNo);
        long odd_sum = Long.parseLong(strIlk8Hane.substring(8, 9));
        odd_sum += Long.parseLong(strIlk8Hane.substring(6, 7));
        odd_sum += Long.parseLong(strIlk8Hane.substring(4, 5));
        odd_sum += Long.parseLong(strIlk8Hane.substring(2, 3));
        odd_sum += Long.parseLong(strIlk8Hane.substring(0, 1));
        long even_sum = Long.parseLong(strIlk8Hane.substring(7, 8));
        even_sum += Long.parseLong(strIlk8Hane.substring(5, 6));
        even_sum += Long.parseLong(strIlk8Hane.substring(3, 4));
        even_sum += Long.parseLong(strIlk8Hane.substring(1, 2));
        long cdigit_1 = (10L - (odd_sum * 3L + even_sum) % 10L) % 10L;
        long cdigit_2 = (10L - ((cdigit_1 + even_sum) * 3L + odd_sum) % 10L) % 10L;

        if ((cdigit_1 * 10L + cdigit_2) == son2hane)
            return true;
        else
            return false;
    }
}
