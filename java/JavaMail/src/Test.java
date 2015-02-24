
public class Test {
	public static final int SHOW_MESSAGES = 1;//0001
	public static final int CLEAR_MESSAGES = 2;//0010
	public static final int SHOW_AND_CLEAR = SHOW_MESSAGES + CLEAR_MESSAGES;//0011
	public static void main(String[] args) {
		int mode = 7;
		
		boolean show = (mode & SHOW_MESSAGES) > 0;
		boolean clear = (mode & CLEAR_MESSAGES) > 0;
		
		System.out.println("show: " + show);
		System.out.println("clear: " + clear);
	}

}
