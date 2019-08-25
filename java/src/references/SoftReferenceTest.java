package references;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

import static references.StrongReference.deleteOldDumps;
import static references.StrongReference.runGC;

public class SoftReferenceTest {

    public static void main(String[] args) throws InterruptedException {
        deleteOldDumps();
        A a = new A("a");
        ReferenceQueue<A> referenceQueue = new ReferenceQueue<>();
        Reference<A> softReference = new SoftReference<>(a, referenceQueue);
        HeapDump.dumpHeap("java/heap-dumps/softRefBeforeGCEligible.hprof", false);
        a = null;
        System.out.println("Reference.get() before gc: " + softReference.get());
        System.out.println("Referencequeue.poll() before gc: " + referenceQueue.poll());
        System.out.println("is enqueued: " + softReference.isEnqueued());
        HeapDump.dumpHeap("java/heap-dumps/softRefBeforeGC.hprof", false);
        runGC();
        HeapDump.dumpHeap("java/heap-dumps/softRefAfterGC.hprof", false);
        System.out.println("Reference.get() after gc: " + softReference.get());
        System.out.println("is enqueued: " + softReference.isEnqueued());
        Reference polledRef = referenceQueue.poll();
        System.out.println("ReferenceQueue.poll() after gc: " + polledRef);
    }

    static class A {
        public A(String s) {
            this.s = s;
        }
        String s;
    }
}
