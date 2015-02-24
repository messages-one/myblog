package memory;

public class MemoryUseAddress {

	private String innerStr;
	private int innerInt;

	public MemoryUseAddress(String innerStr, int innerInt) {
		this.innerInt = innerInt;
		this.innerStr = innerStr;
	}

	public static void main(String[] args) throws Exception {

		MemoryUseAddress test = new MemoryUseAddress("a", 1);
		System.out.println(test);
		System.out.println(System.identityHashCode(test));
		System.out.println(Integer.toHexString(System.identityHashCode(test)));

		long addressOfSampleClass = addressOf(test);
		System.out.println(addressOfSampleClass + " - " + Long.toHexString(addressOfSampleClass));

		Object mine = "Hi there".toCharArray();
		long address = addressOf(mine);
		System.out.println("Addess: " + address);
		// Verify address works - should see the characters in the array in the output
		System.out.println(address + " - " + Long.toHexString(address));
		printBytes(address, 31);
		Object[] array = new Object[] { test };
		long addressOfSampleClass2 = DirectMemoryUtil.unsafe.getInt(array, 12L);
		System.out.println(addressOfSampleClass2 + " - " + Long.toHexString(addressOfSampleClass2));

		addressOfSampleClass2 = DirectMemoryUtil.unsafe.getInt(test.innerInt, 4L);
		System.out.println(addressOfSampleClass2 + " - " + Long.toHexString(addressOfSampleClass2));

		addressOfSampleClass = DirectMemoryUtil.unsafe.getInt(test.innerStr, 4L);
		System.out.println(addressOfSampleClass + " - " + Long.toHexString(addressOfSampleClass));

	}

	public static void printBytes(long objectAddress, int num) {
		for (long i = 0; i < num; i++) {
			int cur = DirectMemoryUtil.unsafe.getByte(objectAddress + i);
			System.out.print((char) cur);
		}
		System.out.println();
	}

	public static long addressOf(Object o) throws Exception {
		Object[] array = new Object[] { o };

		long baseOffset = DirectMemoryUtil.unsafe.arrayBaseOffset(Object[].class);
		// int addressSize = DirectMemoryUtil.unsafe.addressSize(); Bu compress 64 bit de bile 8 döner arrayIndexScale kullan
		int addressSize = DirectMemoryUtil.unsafe.arrayIndexScale(Object[].class);
		long objectAddress;
		switch (addressSize) {
		case 4:
			objectAddress = DirectMemoryUtil.unsafe.getInt(array, baseOffset);
			break;
		case 8:
			objectAddress = DirectMemoryUtil.unsafe.getLong(array, baseOffset);
			break;
		default:
			throw new Error("unsupported address size: " + addressSize);
		}

		return (objectAddress);
	}

}
