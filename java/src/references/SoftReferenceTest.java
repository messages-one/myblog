package references;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class SoftReferenceTest {

    static List<A> list = new ArrayList<>();

    public static void main(String[] args) {
        A a = new A("a");
        ReferenceQueue<A> referenceQueue = new ReferenceQueue<>();
        Reference<A> softReference = new SoftReference<>(a, referenceQueue);
        a = null;
        System.out.println("Reference.get() before gc: " + softReference.get());
        System.out.println("Referencequeue.poll() before gc: " + referenceQueue.poll());

        System.out.println("total memory: " + Runtime.getRuntime().totalMemory());

        for (int i = 0; i < 15000; i++) {
            A tmpA = new A(new String(i + " th string"));
            list.add(tmpA);
        }

        System.out.println("total memory: " + Runtime.getRuntime().totalMemory());

        System.gc();

        System.out.println("total memory: " + Runtime.getRuntime().totalMemory());

        System.out.println("Reference.get() after gc: " + softReference.get());
        System.out.println("Referencequeue.poll() after gc: " + referenceQueue.poll());


    }

    static class A {
        public A(String s) {
            this.s = s;
        }

        String s;
    }
}
