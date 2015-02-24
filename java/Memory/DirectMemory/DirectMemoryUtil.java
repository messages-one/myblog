package tr.com.havelsan.util;

import java.lang.reflect.Field;


import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import size.SizeUtil;
import sun.misc.Unsafe;
import tr.com.havelsan.entity.GenericEntity;

/*
 * The sun.misc.Unsafe class allows you to allocate and deallocate native memory from Java like you were calling malloc and free from C. 
 * The memory you create goes off the heap and are not managed by the garbage collector so it becomes your responsibility to deallocate the memory after 
 * you are done with it. Here is the utility class to gain access to the Unsafe class.
 * 
 * Terracotta Big memory yerine Unsafe kütüphanesi ile low level olarak kendimiz ram'e yazýp okuma yazýyoruz. Bu en baþta heap kullanýmýna göre de terracotta ya göre de
 * yavaþ olacaktýr. Heap ve terracotta nýn edindiði tecrübeler elde edildikçe, ve alloc dealloc düzgün yapýldýkça performans artacaktýr.
 * 
 * free yi uygulama kapatýlýrken ya da belli aralýklarla yapmak lazým.
 * 
 * Þu an pritimives, String ve GenericEntity için çalýþýyor
 */

public class DirectMemoryUtil implements MemoryUtil {

	private static final Logger log = Logger.getLogger(DirectMemoryUtil.class.getName());

	private static Unsafe unsafe;
	private static boolean AVAILABLE = false;
	public static MemoryUtil INSTANCE = new DirectMemoryUtil();
	private Map<String, Long> rammedObjects = new HashMap<String, Long>();

	private final static String REGEX = "[A-Za-z0-9_\\-]+";

	static {
		try {
			Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			unsafe = (Unsafe) field.get(null);
			/*
			 * Unsafe ile objelerin address bilgilerini okuyabilirsin. /* SampleClass sampleClassObject = new SampleClass(); int addressOfSampleClass = unsafe.getInt(sampleClassObject, 4L); int addressOfSampleClass = unsafe.getInt(SampleClass.class, 80L);
			 */
			AVAILABLE = true;
		} catch (Exception e) {
			// NOOP: throw exception later when allocating memory
		}
	}

	public static boolean isAvailable() {
		return AVAILABLE;
	}

	private DirectMemoryUtil() {

	}

	/*
	 * The first trick we will do is C-like sizeof() function, i.e. function that returns shallow object size in bytes. Inspecting JVM sources of JDK6 and JDK7, in particular src/share/vm/oops/oop.hpp and src/share/vm/oops/klass.hpp, and reading comments in the code, we can notice that size of class
	 * instance is stored in _layout_helper which is the fourth field in C structure that represents Java class. Similarly, /src/share/vm/oops/oop.hpp shows that each instance (i.e. object) stores pointer to a class structure in its second field. For 32-bit JVM this means that we can first take
	 * class structure address as 4-8 bytes in the object structure and next shift by 3×4=12 bytes inside class structure to capture_layout_helper field which is instance size in bytes.
	 */
	// private static long sizeOf(Object object) {
	// return unsafe.getAddress(normalize(unsafe.getInt(object, 4L)) + 12L);
	// }

	/*
	 * We need to use normalize() function because addresses between 2^31 and 2^32 will be automatically converted to negative integers, i.e. stored in complement form. Let’s test it on 32-bit JVM (JDK 6 or 7):
	 */
	// private static long normalize(int value) {
	// if (value >= 0)
	// return value;
	// return (~0L >>> 32) & value;
	// }

	@Override
	public long alloc(Object object) {

		if (!AVAILABLE) {
			throw new IllegalStateException("sun.misc.Unsafe is not accessible!");
		}
		return unsafe.allocateMemory(SizeUtil.fullSizeOf(object));
	}

	@Override
	public void free(long address) {
		unsafe.freeMemory(address);
	}

	@Override
	public Object get(Class cls, String key) {
		// rammedObjects ile key-address arasýnda lookup tablo oluþturduk. Kullanýcý key (classname+id) ile çaðýrcak ama arkada addressden deðeri çekicez
		Long address = rammedObjects.get(key);
		if (address == null)
			return null;
		try {
			return read(cls, address, null);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	@Override
	public void put(Object o, String key) {

		if (!rammedObjects.containsKey(key)) {
			long address = alloc(o);
			rammedObjects.put(key, address);
			try {
				place(o, address);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}

	private void place(Object o, long address) throws Exception {

		Class clazz = o.getClass();

		do {

			for (Field f : clazz.getDeclaredFields()) {

				if (!Modifier.isStatic(f.getModifiers())) {

					long offset = unsafe.objectFieldOffset(f);
					long newAddress = address + offset;

					if (f.getType() == long.class) {
						unsafe.putLong(newAddress, unsafe.getLong(o, offset)); // Þu adrese (alloc olan memorynin baþlangýç noktasý) þu datayý yaz
					} else if (f.getType() == int.class) {
						unsafe.putInt(newAddress, unsafe.getInt(o, offset));
					} else if (f.getType() == byte.class) {
						unsafe.putByte(newAddress, unsafe.getByte(o, offset));
					} else if (f.getType() == short.class) {
						unsafe.putShort(newAddress, unsafe.getShort(o, offset));
					} else if (f.getType() == double.class) {
						unsafe.putDouble(newAddress, unsafe.getDouble(o, offset));
					} else if (f.getType() == float.class) {
						unsafe.putFloat(newAddress, unsafe.getFloat(o, offset));
					} else if (f.getType() == boolean.class) {
						unsafe.putByte(newAddress, unsafe.getByte(o, offset));// boolean one byte
					} else if (f.getType() == char.class) {
						unsafe.putChar(newAddress, unsafe.getChar(o, offset));
					} else if (f.getType() == String.class) {
						f.setAccessible(true);
						String str = f.get(o).toString();
						long baseoffsetOfString = getBaseOffset(String.class);
						for (int i = 0; i < str.length(); i++) {
							unsafe.putChar(newAddress + (i * 2), str.charAt(i));
							// unsafe.putChar(address + offset + (i * 2), unsafe.getChar(str, baseoffsetOfString++)); Bu da olur, String in içinde baseOffset den sonra char lar baþlar
						}
					} else if (f.getType() == Date.class) {
						throw new Exception("Date is not supported in Direct Memory");
//						f.setAccessible(true);
//						Date d = (Date)f.get(o);
//						long baseoffsetOfString = getBaseOffset(Date.class);//Date için base offseti hesapla, bir long (fastTime) var içinde
//						unsafe.putLong(newAddress + (i * 2), ...);												
					} else if (f.getType() == GenericEntity.class || f.getType().getGenericSuperclass() == GenericEntity.class) { // Primitive dýþýnda sadece GenericEntity den extend eden entityleri kullan ve kullaným olarak
						// GenericEntity obj = new ...(); þeklinde tanýmla ki getType dan yakalayýp asýl objeyi recurisve çaðýralým.
						// getType eþitliðin solunu(stack), getClass saðýný (heap - runtime class) verir. Field de f.getType() yine soldakini verir iken getClass() Field getDeclaringClass() ise
						// asýl heapdeki objeyi verir.
						// System.out.println(f.getType());
						// System.out.println(f.getDeclaringClass());
						// recursively call place method,ancak class yerine instance gitmeli, ayný instance'a put edicez.
						f.setAccessible(true);
						place(f.get(o), newAddress);
					}
				}
			}
		} while ((clazz = clazz.getSuperclass()) != null);
	}

	private Object read(Class clazz, long address, Object createdInstance) throws Exception {
		Object instance = null;
		if (createdInstance != null)
			instance = createdInstance;
		else
			instance = unsafe.allocateInstance(clazz);

		do {

			for (Field f : clazz.getDeclaredFields()) {

				if (!Modifier.isStatic(f.getModifiers())) {

					long offset = unsafe.objectFieldOffset(f);
					long newAddress = address + offset;
					if (f.getType() == long.class) {
						unsafe.putLong(instance, offset, unsafe.getLong(newAddress)); // þu instance ýn offset ine address + offset de ki
						// daha önce yazdýðýmýz deðeri ata
					} else if (f.getType() == int.class) {
						unsafe.putInt(instance, offset, unsafe.getInt(newAddress));
					} else if (f.getType() == byte.class) {
						unsafe.putByte(instance, offset, unsafe.getByte(newAddress));
					} else if (f.getType() == short.class) {
						unsafe.putShort(instance, offset, unsafe.getShort(newAddress));
					} else if (f.getType() == double.class) {
						unsafe.putDouble(instance, offset, unsafe.getDouble(newAddress));
					} else if (f.getType() == float.class) {
						unsafe.putDouble(instance, offset, unsafe.getDouble(newAddress));
					} else if (f.getType() == boolean.class) {
						unsafe.putByte(instance, offset, unsafe.getByte(newAddress));// boolean one byte
					} else if (f.getType() == char.class) {
						unsafe.putChar(instance, offset, unsafe.getChar(newAddress));
					} else if (f.getType() == String.class) {
						int i = 0;
						char temp = ' ';
						long baseOffset = getBaseOffset(String.class);
						StringBuilder sb = new StringBuilder();
						while (Pattern.matches(REGEX, (String.valueOf(temp = unsafe.getChar(address + offset + (i++ * 2)))))) {// REGEX yerine String len de tutulabilir.O kadar dönülür.Thread local or local var olabilir
							// unsafe.putChar(instance, offset + baseOffset + ++i, temp);//32 String in offseti + baseOffset 36, char[] in deðerlerinin baþladýðý yer. Buraya put yapsakta obje null o yüzden
							// set edemiyoruz.
							sb.append(temp);
						}
						f.setAccessible(true);
						f.set(instance, sb.toString());
						// unsafe.getChar(((WrappedObject2)instance).getS1(), baseOffset++); Bu þekilde gerçekten baseoffset den sonra char larýn baþladýðýný teyi
					}
					else if (f.getType() == Date.class) {
						throw new Exception("Date is not supported in Direct Memory");
						//TODO Date impl
					}
					else if (f.getType() == GenericEntity.class || f.getType().getGenericSuperclass() == GenericEntity.class) { // Primitive dýþýnda sadece GenericEntity den extend eden entityleri kullan ve kullaným olarak
						// GenericEntity obj = new ...(); þeklinde tanýmla ki getType dan yakalayýp asýl objeyi recurisve çaðýralým. f.getType().getGenericSuperclass() == GenericEntity.class da olur
						//O zaman f.getDeclaringClass() ile alabilirsin. annotationa gerek kalmaz.
						// getType eþitliðin solunu(stack), getClass saðýný (heap - runtime class) verir. Field de f.getType() yine soldakini verir iken getClass() Field getDeclaringClass() ise
						// asýl heapdeki objeyi verir.
						// System.out.println(f.getType());
						// System.out.println(f.getDeclaringClass());
						// recursively call place method,ancak class yerine instance gitmeli, ayný instance'a put edicez.
						f.setAccessible(true);
						ClassDefinition clsDef = f.getAnnotation(ClassDefinition.class);//Bunun yerine getDeclaringClass()  kullanýlabilir
						Object obj = DirectMemoryUtil.unsafe.allocateInstance(clsDef.classDef());
						f.set(instance, obj);
						read(obj.getClass(), newAddress, obj);
					}
				}

			}

		} while ((clazz = clazz.getSuperclass()) != null);

		return instance;

	}

	private long getBaseOffset(Class cls) throws Exception {
		int addressSize = DirectMemoryUtil.unsafe.arrayIndexScale(Object[].class);
		long baseOffset = 0L;
		switch (addressSize) {
		case 4:
			baseOffset = 8L + 4L + 4L + 4L;
			break;
		case 8:
			baseOffset = 12L + 4L;
			break;
		default:
			break;
		}
		if (cls == String.class) {
			while (baseOffset % 8 != 0)
				baseOffset++;
			baseOffset += DirectMemoryUtil.unsafe.arrayBaseOffset(char[].class);
		}
		return baseOffset;
	}

}
