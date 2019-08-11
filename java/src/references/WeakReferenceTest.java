package references;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class WeakReferenceTest {

    public static Map<A, B> map = new WeakHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        StrongReference.deleteOldDumps();
        references.HeapDump heapDump = new references.HeapDump();
        //WeakHashMap usage
        A a = new A("a");
        List<Object> objects = new ArrayList<>();
        B b = new B(objects);
        map.put(a, b);
        a = null;
        System.gc();
        System.out.println("null key entry? " + map.get(null));
        System.out.println(map.size());
        //expungeStaleEntries method cleaned up the entry with null key
        System.out.println(map.size());

        //WeakReference usage
        //Do not use String a referent of weak ref. Since it is pooled you cannot observe weakreference.
        //WeakReference's referent property is already null as opposed to PhantomReference(prior to java 9)
        A a2 = new A("a");
        ReferenceQueue<A> referenceQueue = new ReferenceQueue<>();
        Reference<A> weakReference = new SoftReference<>(a2, referenceQueue);
        heapDump.dumpHeap("heap-dumps/strongRefBeforeGCEligible.hprof", false);
        a2 = null;
        System.out.println("Reference.get() before gc: " + weakReference.get());
        System.out.println("Referencequeue.poll() before gc: " + referenceQueue.poll());
        System.out.println("is enqueued: " + weakReference.isEnqueued());
        heapDump.dumpHeap("heap-dumps/strongRefBeforeGC.hprof", false);
        System.out.println("Running GC");
        System.gc(); // Hint to run gc
        Thread.sleep(2000L);
        System.out.println("Finished running GC");
        heapDump.dumpHeap("heap-dumps/strongRefAfterGC.hprof", false);
        System.out.println("Reference.get() after gc: " + weakReference.get());
        System.out.println("is enqueued: " + weakReference.isEnqueued());
        Reference polledRef = referenceQueue.poll();
        System.out.println("Refs are equal: " + (weakReference == polledRef));
        System.out.println("ReferenceQueue.poll() after gc: " + polledRef);

    }

    static class A {
        public A(String s) {
            this.s = s;
        }

        String s;
    }

    static class B {
        public B(List<Object> largeObject) {
            this.largeObject = largeObject;
        }

        List<Object> largeObject;
    }
}
