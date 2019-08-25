package references;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import static references.StrongReference.deleteOldDumps;
import static references.StrongReference.runGC;

public class WeakReferenceTest {

    public static void main(String[] args) throws InterruptedException {
        deleteOldDumps();

        System.out.println("1.) WeakHashMap example:");
        //1.) WeakHashMap example
        A a = new A("a");
        List<Object> objects = new ArrayList<>();
        B b = new B(objects);
        Map<A, B> map = new WeakHashMap<>();
        map.put(a, b);
        a = null;
        b = null;
        //Note that B will be GC'ed after the second GC because expungeStaleEntries needed to remove entry first
        //as it holds a ref to b
        System.out.println("Map size before gc: " + map.size());
        runGC();
        //expungeStaleEntries method cleaned up the entry with null key.
        //We expect map size as 0
        System.out.println("Map size after gc: " + map.size());

        System.out.println("--------------------------------------------------------");

        System.out.println("2.) WeakReference example:");
        //2.) WeakReference example
        //Do not use String a referent of weak ref.
        //Since it is pooled you cannot observe weakreference.
        //WeakReference's referent property is already null after GC
        //as opposed to PhantomReference(prior to java 9)
        A a2 = new A("a");
        ReferenceQueue<A> referenceQueue = new ReferenceQueue<>();
        Reference<A> weakReference = new WeakReference<>(a2, referenceQueue);
        HeapDump.dumpHeap("java/heap-dumps/weakRefBeforeGCEligible.hprof", false);
        a2 = null;
        System.out.println("Reference.get() before gc: " + weakReference.get());
        System.out.println("Referencequeue.poll() before gc: " + referenceQueue.poll());
        System.out.println("is enqueued: " + weakReference.isEnqueued());
        HeapDump.dumpHeap("java/heap-dumps/weakRefBeforeGC.hprof", false);
        runGC();
        HeapDump.dumpHeap("java/heap-dumps/weakRefAfterGC.hprof", false);
        System.out.println("Reference.get() after gc: " + weakReference.get());
        System.out.println("is enqueued: " + weakReference.isEnqueued());
        Reference polledRef = referenceQueue.poll();
        System.out.println("Refs are equal: " + (weakReference == polledRef));
        System.out.println("ReferenceQueue.poll() after gc: " + polledRef);
        //referent is already cleared and we expect null here
        System.out.println("Referent inside reference after gc: " + polledRef.get());

        System.out.println("--------------------------------------------------------");

        System.out.println("3.) Getting inner information from weak reference" +
                " after the referent garbage collected");
        //3.) Reaching some inner information through weakreference after
        //referent the object garbage collected
        ReferenceQueue<A> referenceQueue2 = new ReferenceQueue<>();
        A a3 = new A("a3");
        String innerInfo = "Inner info";
        WeakA weakA = new WeakA(a3, innerInfo, referenceQueue2);
        a3 = null;
        runGC();
        //should not be null but the referent inside it automatically
        //removed as it is garbage collected
        polledRef = referenceQueue2.poll();
        System.out.println("Referent inside reference after gc: " + polledRef.get());
        weakA = (WeakA) polledRef;
        System.out.println("Get inner information from the polled reference: "
                + weakA.innerInfo);
    }

    static class A {
        public A(String s) {
            this.s = s;
        }
        String s;
    }

    static class WeakA extends WeakReference<A> {
        String innerInfo;

        public WeakA(A a, String innerInfo, ReferenceQueue<A> queue) {
            super(a, queue);
            this.innerInfo = innerInfo;
        }
    }

    static class B {
        List<Object> largeObject;
        public B(List<Object> largeObject) {
            this.largeObject = largeObject;
        }
    }

}
