
public class Test2 {

	public static void main(String[] args) {
		
		int a = 2;//0010
		int b = -4;//0111		
		//00000000000000000000000000000100
		//11111111111111111111111111111100
		int result = a & b;
		 System.out.println("a & b = " +result); // prints two
		
		result = a | b;
		System.out.println(result);		
		
		result = a ^ b;
		System.out.println(result);		
		
		result = ~a;
		System.out.println(result);
		
		result = ~b;
		System.out.println(result);
		
		result = a << 2;
		System.out.println(result);
		
		result = a >>> 1;
		System.out.println(result);
		
		result = b >> 2;
		System.out.println(result);
		
		result = b >>> 1;
		System.out.println(result);
		
	    byte admin = 1;//00000001
        byte role1 = 1;//00000001 --> admin
        byte role2 = 2;//00000010 --> not admin
        byte role3 = 3;//00000011  --> admin
        byte readRoleFromDatabase = 3;//say we read 3 from the database for the user's role.
        
        if ((readRoleFromDatabase  & admin ) > 0) {
            System.out.println("admin");
         }
      
        
        byte oddVal = 3;//00000011
        byte evenVal = 6;//00000011
        byte compareVal = 1;//00000001
        if ((oddVal & compareVal) > 0)  {
            System.out.println(oddVal + " odd value");
        }
        if ((evenVal & compareVal) == 0)  {
            System.out.println(evenVal + " even value");
        }

	}

}
