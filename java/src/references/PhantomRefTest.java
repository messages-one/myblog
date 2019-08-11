package references;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PhantomRefTest {

    private volatile static boolean finishing = false;

    public static void main(String[] args) throws InterruptedException {
        PhantomRefTest phantomRefTest = new PhantomRefTest();
        A a = phantomRefTest.new A("a");
        ReferenceQueue<A> referenceQueue = new ReferenceQueue<>();
        Reference<A> reference = phantomRefTest.new AFinalizer(a, referenceQueue);
        a = null;
        System.out.println("Phantom reference always return null: " + reference.get());
        System.out.println("Phantom reference queue return null before GC: " + referenceQueue.poll());
        System.gc();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        phantomRefTest.monitorPhantomReference(referenceQueue, reference, countDownLatch);
        countDownLatch.await();
        finishing = true;
        System.out.println(reference);//if you check internal referent here, it will be null as we cleared it.
        //Note that after java 9 the referent cleared internally not waiting the phantomref object to be cleared.
    }

    private void monitorPhantomReference(ReferenceQueue<A> referenceQueue, Reference<A> reference, CountDownLatch latch) {
        //ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Runnable runnable = () -> {
            try {
                while (!finishing) {
                    //We can use either a loop with poll method and break if poll return not null and our ref or use blocking remove method
                    AFinalizer finalizer;
                    finalizer = (AFinalizer) referenceQueue.remove();//remove(10000L)
                    //              while ((finalizer = referenceQueue.poll()) != reference) {
                    //                      //can break with a common variable
                    //              }
                    System.out.println("We got the phantom referent object in the queue");
                    System.out.println("Reference queue return the same ref object: " + (finalizer == reference));
                    System.out.println("Phantom reference always return null: " + finalizer.get());
                    finalizer.cleanUp();
                    finalizer.clear();//if you do not call this, it will be cleared after phantomref object itself cleared. It is cleared after java9 automatically
                    latch.countDown();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        //scheduledExecutorService.schedule(runnable, 5, TimeUnit.SECONDS); with this you can use finishing variable to shutdown the executor service
        executorService.submit(runnable);
        executorService.shutdown();
    }

    class A {
        public A(String s) {
            this.s = s;
        }

        String s;
    }

    /**
     * Cannot resurrect the referent here. If you try to assign to a property, it will be still referenced by your app
     * so not be added to reference queue. The object will be phantomly reachable only when you have no reference from your app.
     */
    public class AFinalizer extends PhantomReference<A> {

        /**
         * Creates a new phantom reference that refers to the given object and
         * is registered with the given queue.
         *
         * <p> It is possible to create a phantom reference with a <tt>null</tt>
         * queue, but such a reference is completely useless: Its <tt>get</tt>
         * method will always return null and, since it does not have a queue, it
         * will never be enqueued.
         *
         * @param referent the object the new phantom reference will refer to
         * @param q        the queue with which the reference is to be registered,
         */
        public AFinalizer(A referent, ReferenceQueue<? super A> q) {
            super(referent, q);
        }

        public void cleanUp() {
            System.out.println("Clean up resources!");
        }
    }


}
