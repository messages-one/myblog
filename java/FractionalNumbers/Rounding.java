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
	}
}
