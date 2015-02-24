import java.math.BigDecimal;

public class Rounding {
	static double round(Double d, int decimalPlace) {   
		   BigDecimal bd = new BigDecimal(d.toString());
		   bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_EVEN);
		   d = bd.doubleValue();
		   return d;
		}
	static final double DECIMAL_PLACE_MULTIPLIER = 100.0, DECIMAL_PLACE_CARPANI = 100.0;
	static double round2(Double d) {   
        d = Math.round(d * DECIMAL_PLACE_CARPANI ) / DECIMAL_PLACE_CARPANI ;
       return d;
 }
	
	static double round3(double d) {
		   long l  =(long) (d < 0 ? d * DECIMAL_PLACE_MULTIPLIER - 0.5 : d *DECIMAL_PLACE_MULTIPLIER + 0.5);
		   return l / DECIMAL_PLACE_MULTIPLIER ;
		}
	public static void main(String[] args) {
		
		System.out.println(System.getProperty("user.dir"));
		
		double elimizdekiTutar = 7.0; 
		int satinAlinanUrunSayisi = 0;
		// decimal place 2 ise 100, 3 ise  1000 vb.     
		for (double urunTutar = 0.7; elimizdekiTutar >= urunTutar;) {
		 elimizdekiTutar = round3(elimizdekiTutar -= urunTutar);
		 satinAlinanUrunSayisi++; 
		 }              
		System.out.println("elimizdekiTutar="+elimizdekiTutar);
		System.out.println("satinAlinanUrunSayisi="+satinAlinanUrunSayisi);
		
		
		
		
		System.out.println(round(0.685, 2));
		
		BigDecimal totalValue = new BigDecimal("7.0");
		int numberOfOrders = 0;
		final BigDecimal ORDER_VALUE = new BigDecimal("0.7");
		for (BigDecimal orderVal = ORDER_VALUE ;                             
		  totalValue.compareTo(orderVal) >= 0;) {
		  totalValue = totalValue.subtract(orderVal);
		  numberOfOrders ++;
		} 
		System.out.println("totalValue =" + totalValue.setScale(2) );
		System.out.println("numberOfOrders =" +numberOfOrders );

		double d1 = 0.09999999999999999167332731531132594682276248931884765625;
		double d2 = 0.1000000000000000055511151231257827021181583404541015625;
		if (1 - d1 > d2 - 1)
			System.out.println("d2 close");
		else
			System.out.println("d1 close");
		
		
		d1 = 0.6999999999999999555910790149937383830547332763671875;
		d2 = 0.70000000000000006661338147750939242541790008544921875;
		if (1 - d1 > d2 - 1)
			System.out.println("d2 close");
		else
			System.out.println("d1 close");
		
		float f1 = 0.7000000178813934326171875f;
		float f2 = 0.70000000298023223876953125f;
		if (1 - f1 > f2 - 1)
			System.out.println("f2 close");
		else
			System.out.println("f1 close");
		
		
		
		


		String s = "a";

		if (true && true || false)
			System.out.println("sdfa");

		System.out.println(System.getProperty("user.dir"));

		String test2 = "D:\\workspaces\\documenter\\documenter\\WebContent\\workedDocFolder\\deneme\\ch01\\ch01.1\\174.full - Kopya (4).pdf";

		String test4 = test2.replace("\\\\", "\\");

		System.out.println(test2.substring(test2.indexOf("workedDocFolder") + 16, test2.length()));

		System.out.println(test2.lastIndexOf("\\"));
		String test3 = test2.substring(test2.lastIndexOf("WebContent") + 11, test2.length()).replace("\\\\", "/");

		String test = "Desss.pdf";
		String[] tt = test.split("\\\\.");

		String s2 = "dasdasd.docx";

		if (!s2.endsWith("docx"))
			System.out.println(s2 + ".docx");

		// TODO Auto-generated method stub

		String s3 = "";
		String s1[] = s3.split(",");
		System.out.println(s1.length);

	}

}
