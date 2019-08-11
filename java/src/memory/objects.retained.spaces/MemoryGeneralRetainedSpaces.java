package memory.objects.retained.spaces;

import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import size.SizeUtil;

public class MemoryGeneralRetainedSpaces implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8311567717042461535L;
	private String innerStr;
	private int innerInt;

	public MemoryGeneralRetainedSpaces(String innerStr, int innerInt) {
		this.innerInt = innerInt;
		this.innerStr = innerStr;
	}

	// private static long mySizeOf(Serializable ser) throws IOException {
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// ObjectOutputStream oos = new ObjectOutputStream(baos);
	// oos.writeObject(ser);
	// oos.close();
	// return baos.size();
	// }

	public static void main(String[] args) throws IOException {
		//System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());

		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = runtimeMxBean.getInputArguments();

		/*
		 * 64 bitte header 8 yerine 12 byte geri kalan ayn�
		 * E�er sadece 64 bit JVM de ge�erli olan ve <32GB ram de �al��an XX:-UseCompressedOops VM arg� turn off yap�p s�k��t�rmay� kapat�rsan
		 * 2*8 = 16 header ve member pointer, array elemanlar�na pointer, array length ve stackdeki adres ref de�erleri 8 bytes olur.
		 * String 1.7.0_06 sonras� 1 hashcode int, 1 hash32 int ve char[] i�erir (daha �nce final de�ilken art�k final d�r) 
		 */
		
		boolean bo1 = true;
		System.out.println(SizeUtil.fullSizeOf(bo1));// 32 bit-> 16 bytes(8(header) + 1(data)=9 round to 16)
		// 64 bit-> 16 bytes(12(header) + 1(data)=13 round to 16)
		// 64 bit-> 24 bytes(16(header) + 1(data)=17 round to 24) XX:-UseCompressedOops VM arg

		byte b1 = 3;
		System.out.println(SizeUtil.fullSizeOf(b1));// 32 bit-> 16 bytes(8(header) + 1(data)=9 round to 16)
		// 64 bit-> 16 bytes(12(header) + 1(data)=13 round to 16)
		// 64 bit-> 24 bytes(16(header) + 1(data)=17 round to 24) XX:-UseCompressedOops VM arg

		short sh1 = 3;
		System.out.println(SizeUtil.fullSizeOf(sh1));// 32 bit-> 16 bytes(8(header) + 2(data)=10 round to 16)
		// 64 bit-> 16 bytes(12(header) + 2(data)=14 round to 16)
		// 64 bit-> 24 bytes(16(header) + 2(data)=18 round to 24) XX:-UseCompressedOops VM arg

		int i1 = 3;
		System.out.println(SizeUtil.fullSizeOf(i1));// 32 bit-> 16 bytes(8(header) + 4(data)=12 round to 16)
		// 64 bit-> 16 bytes(12(header) + 4(data)=16 round to 16)
		// 64 bit-> 24 bytes(16(header) + 4(data)=20 round to 24) XX:-UseCompressedOops VM arg

		long l1 = 3L;
		System.out.println(SizeUtil.fullSizeOf(l1));// 32 bit-> 16 bytes(8(header) + 8(data)=16 round to 16)
		// 64 bit-> 24 bytes(12(header) + 8(data)=20 round to 24)
		// 64 bit-> 24 bytes(16(header) + 8(data)=24 round to 24) XX:-UseCompressedOops VM arg

		char c1 = 'A';
		System.out.println(SizeUtil.fullSizeOf(c1));// 32 bit-> 16 bytes(8(header) + 2(data)=10 round to 16)
		// 64 bit-> 16 bytes(12(header) + 2(data)=14 round to 16)
		// 64 bit-> 24 bytes(16(header) + 2(data)=18 round to 24) XX:-UseCompressedOops VM arg

		float f1 = 3f;
		System.out.println(SizeUtil.fullSizeOf(f1));// 32 bit-> 16 bytes(8(header) + 4(data)=12 round to 16)
		// 64 bit-> 16 bytes(12(header) + 4(data)=16 round to 16)
		// 64 bit-> 24 bytes(16(header) + 4(data)=20 round to 24) XX:-UseCompressedOops VM arg

		double d1 = 3;
		System.out.println(SizeUtil.fullSizeOf(d1));// 32 bit-> 16 bytes(8(header) + 8(data)=16 round to 16)
		// 64 bit-> 24 bytes(12(header) + 8(data)=20 round to 24)
		// 64 bit-> 24 bytes(16(header) + 8(data)=24 round to 24) XX:-UseCompressedOops VM arg

		Object o = new Object();
		System.out.println(SizeUtil.fullSizeOf(o));// 32 bit-> 8 bytes(8(header) =8 round to 8)
		// 64 bit-> 16 bytes(12(header) =12 round to 16)
		// 64 bit-> 16 bytes(16(header) =16 round to 16) XX:-UseCompressedOops VM arg

		String st1 = "A";
		System.out.println(SizeUtil.fullSizeOf(st1));// 32 bit-> 40 bytes(8(header for String) +  4(for hashcode val) + 4(for hash32 val) + 4(ref to char[]) + (8(header for char[]) + 4(for len of char[]) + 2(data)->round to 16) = 40)
		// 64 bit-> 48 bytes(12(header for String) +  4(for hashcode val) + 4(for hash32 val)  + 4(ref to char[])  : 24 round to 24 --> + (12(header for char[]) + 4(for len of char[]) + 2(data)->round to 24) = 48 round to 48)
		// 64 bit-> 64 bytes(16(header for String) + 4(for hashcode val) + 4(for hash32 val)  + 8(ref to char[]) : 32  + (16(header for char[]) + 8(for len of char[]) + 2(data)-> 26 round to 32) = 64 round to 64) XX:-UseCompressedOops VM arg
		// her inner obje kendi i�inde 8 in kat�na yuvarlan�p �yle toplan�yor. String 24 byte + char 12 byte round to 16 then 24 + 16 = 40

		//System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());

		byte[] arrByte = new byte[20];// 32 bit-> 32 bytes(8(header) + 4(len of byte[]) + 20(data)=32 round to 32)
		// 64 bit-> 40 bytes(12(header) + 4(len of byte[]) + 20(data)=36 round to 40)
		// 64 bit-> 48 bytes(16(header) + 8(len of byte[]) + 20(data)=44 round to 48) XX:-UseCompressedOops VM arg
		System.out.println(SizeUtil.sizeOf(arrByte));
		System.out.println(SizeUtil.fullSizeOf(arrByte));

		Byte[] arrByteObj = new Byte[] { 3, 4, 5 };// 32 bit-> 72 bytes(8(header) + 4(len of Byte[]) + 3 * 4(4 byte for ref of each element) + 3 * 16(8+1 rounded to 16)(data)=72 round to 72)
		// 64 bit-> 80 bytes(12(header) + 4(len of Byte[]) + 3 * 4(4 byte for ref of each element) + 3 * 16(12+1 rounded to 16)(data)=76 round to 80)
		// 64 bit-> 120 bytes(16(header) + 8(len of Byte[]) + 3 * 8(8 byte for ref of each element) + 3 * 24(16+1 rounded to 24)(data)=120 round to 120) XX:-UseCompressedOops VM arg
		System.out.println(SizeUtil.sizeOf(arrByteObj));
		System.out.println(SizeUtil.fullSizeOf(arrByteObj));

		int[] arrInt = new int[] { 0 };// 32 bit-> 16 bytes(8(header) + 4(len of byte[]) + 4(data)=16 round to 16)
		// 64 bit-> 24 bytes(12(header) + 4(len of byte[]) + 4(data)=20 round to 24)
		// 64 bit-> 24 bytes(16(header) + 8(len of byte[]) + 4(data)=28 round to 32) XX:-UseCompressedOops VM arg
		System.out.println(SizeUtil.sizeOf(arrInt));
		System.out.println(SizeUtil.fullSizeOf(arrInt));

		long[] arrLong = new long[] { 0 };// 32 bit-> 24 bytes(8(header) + 4(len of byte[]) + 8(data)=20 round to 24)
		// 64 bit-> 24 bytes(12(header) + 4(len of byte[]) + 8(data)=24 round to 24)
		// 64 bit-> 24 bytes(16(header) + 8(len of byte[]) + 8(data)=32 round to 32) XX:-UseCompressedOops VM arg
		System.out.println(SizeUtil.sizeOf(arrLong));
		System.out.println(SizeUtil.fullSizeOf(arrLong));

		int[][] arrIntTwoDim = new int[10][10]; // 32 bit-> 616 bytes((8(header of outer arr) + 4(len of byte[]) + 4 * 10(ref to 10 inner rows) = 52 round to 56) + (10 * ((8(header) + 4 (len) + 10 * 4(data)) rounded to 56) = 560) =616 round to 616)
		// 64 bit-> 616 bytes((12(header of outer arr) + 4(len of byte[]) + 4 * 10(ref to 10 inner rows) = 56 round to 56) + (10 * ((12(header) + 4 (len) + 10 * 4(data)) rounded to 56) = 560) =616 round to 616)
		// 64 bit-> 744 bytes((16(header of outer arr) + 8(len of byte[]) + 8 * 10(ref to 10 inner rows) = 104 round to 104) + (10 * ((16(header) + 8 (len) + 10 * 4(data)) rounded to 64) = 640) =744 round to 744) XX:-UseCompressedOops VM arg

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				arrIntTwoDim[i][j] = i * j;
			}
		}
		System.out.println(SizeUtil.sizeOf(arrIntTwoDim));
		System.out.println(SizeUtil.fullSizeOf(arrIntTwoDim));

		String a = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		// 32 bit-> 3848 bytes(8(header for String) + 4(for len of String) +4(for offset of String) + + 4(for hashcode val) + 4(ref to char[]) + (8(header for char[]) + 4(for len of char[]) + (1904 * 2)(data)->3820 round to 3824) = 3848 rounded to 3848)
		// 64 bit-> 3848 bytes(12(header for String) + 4(for len of String) +4(for offset of String) + + 4(for hashcode val) + 4(ref to char[]) + (12(header for char[]) + 4(for len of char[]) + (1904 * 2)(data)->3824 round to 3824) = 3848 rounded to 3848)
		// 64 bit-> 3864 bytes(16(header for String) +  4(for hashcode var) + 4(for hash32 var)  + 8(ref to char[]) = 32 + (16(header for char[]) + 8(for len of char[]) + (1904 * 2)(data)->3832 round to 3832) = 3864 rounded to 3864) XX:-UseCompressedOops VM arg
		
		System.out.println("a len " + a.length());
		System.out.println(SizeUtil.sizeOf(a));
		System.out.println(SizeUtil.fullSizeOf(a));

		String[] arrStr = new String[4];// 32 bit-> 152 bytes((8(header) + 4(len of String[]) + 4*4(ref for elements) = 28 -> round to 32) + 3* (24(string) + (8(header for char[]) + 4(len of char[]) + 2 byte(data)-> round to 16) * 40= 120 )=152 round to 152)
		// 64 bit-> 176 bytes((12(header) + 4(len of String[]) + 4*4(ref for elements) = 32 -> round to 32) + 3* (24(string) + (12(header for char[]) + 4(len of char[]) + 2 byte(data)-> round to 24) = 144 )= 176 round to 176)
		// 64 bit-> 248 bytes((16(header) + 8(len of String[]) + 8*4(ref for elements) = 56 -> round to 56) + 3* (32(string) + (16(header for char[]) + 8(len of char[]) + 2 byte(data)-> round to 32) = 192 )= 248 round to 248) XX:-UseCompressedOops VM arg
		// 4. element null ve yer tutmuyor. (Ancak normal class var null da olsa 32 bitte 8 bytes, 64 bit non compress de 8 bytes yer ay�r�r. 64 bit compress de yer ay�rmaz.)
		arrStr[0] = "1";
		arrStr[1] = "2";
		arrStr[2] = "3";
		arrStr[3] = null;
		System.out.println(SizeUtil.sizeOf(arrStr));
		System.out.println(SizeUtil.fullSizeOf(arrStr));

		MemoryGeneralRetainedSpaces myTest = new MemoryGeneralRetainedSpaces("adasdas423423", 11111111);// 32 bit-> 80 bytes (8(Test) + 4(for pritimive int) + 4(for ref to String) + 24(String) + 40 (char[]))
		// 64 bit-> 96 bytes (12(Test) + 4(for pritimive int) + 4(for ref to String)->round 24 + 24(String) + 48 (char[])) 96 round to 96
		// 64 bit-> 96 bytes (16(Test) + 4(for pritimive int) + 8(for ref to String) -> round to 32 + 32(String) + 56 (char[])) 120 round to 120 XX:-UseCompressedOops VM arg
		// default size of object include object + primitive variables + member pointers(ref to objects here to String)
		System.out.println(SizeUtil.sizeOf(myTest));
		System.out.println(SizeUtil.fullSizeOf(myTest));

		Object obj = new String("dasddddddddddddddddddddddddddddddddddd");// 32 bit-> 112 bytes(8(header for String) + 4(for hash32 val) + 4(for hashcode val) + 4(ref to char[]) + (8(header for char[]) + 4(for len of char[]) + 2*38(data)->88) = 112)
		// 64 bit-> 120 bytes(12(header for String) + 4(for hash32 val) + 4(for hashcode val) + 4(ref to char[]) + (12(header for char[]) + 4(for len of char[]) + 2*38(data)->92 round to 96) = 120 round to 120)
		// 64 bit-> 120 bytes(16(header for String) + 4(for hash32 val) + 4(for hashcode val) + 8(ref to char[])-> 32  + (16(header for char[]) + 8(for len of char[]) + 2*38(data)->100 round to 104) = 136 round to 136) XX:-UseCompressedOops VM arg
		System.out.println(((String) obj).length());
		System.out.println(SizeUtil.sizeOf(obj));
		System.out.println(SizeUtil.fullSizeOf(obj));



	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((innerInt == 0) ? 0 : innerInt);
		result = prime * result + ((innerStr == null) ? 0 : innerStr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MemoryGeneralRetainedSpaces other = (MemoryGeneralRetainedSpaces) obj;
		if (innerInt == 0) {
			if (other.innerInt != 0)
				return false;
		} else if (innerInt != other.innerInt)
			return false;
		if (innerStr == null) {
			if (other.innerStr != null)
				return false;
		} else if (!innerStr.equals(other.innerStr))
			return false;
		return true;
	}

	// @Override
	// public String toString() {
	// return "Test [innerStr=" + innerStr + ", innerInt=" + innerInt + "]";
	// }

}
