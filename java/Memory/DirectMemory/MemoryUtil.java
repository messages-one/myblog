package tr.com.aligelenler;

public interface MemoryUtil {

	public long alloc(Object o);

	public void free(long address);
	
	public Object get(Class cls, String key);

	public void put(Object o, String key);

}
