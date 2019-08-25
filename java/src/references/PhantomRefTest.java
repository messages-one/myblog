package references;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static references.StrongReference.deleteOldDumps;
import static references.StrongReference.runGC;


public class PhantomRefTest {

    private volatile static boolean finishing = false;
    private static final long WAIT_TIME_FOR_REMOVING_PHANTOM_REFERENCE_MS = 5000L;

    public static void main(String[] args) throws InterruptedException {
        deleteOldDumps();
        PhantomRefTest phantomRefTest = new PhantomRefTest();
        A a = phantomRefTest.new A("a");
        ReferenceQueue<A> referenceQueue = new ReferenceQueue<>();
        String someInfoToUseInCleanUp = "some info";
        Reference<A> reference = phantomRefTest.new MyFinalizer(a, someInfoToUseInCleanUp, referenceQueue);
        HeapDump.dumpHeap("java/heap-dumps/phantomRefBeforeGCEligible.hprof", false);
        a = null;
        System.out.println("Phantom reference always return null: " + reference.get());
        System.out.println("Phantom reference queue return null before GC: " + referenceQueue.poll());
        HeapDump.dumpHeap("java/heap-dumps/phantomRefBeforeGC.hprof", false);
        runGC();
        HeapDump.dumpHeap("java/heap-dumps/phantomRefAfterGC.hprof", false);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Caught shutdown hook, finishing phantom reference monitoring!");
            finishing = true;
            try {
                countDownLatch.await();
                runGC();
                HeapDump.dumpHeap("java/heap-dumps/phantomRefAfterCleanUp.hprof", false);
                //if you check internal referent here (by looking with debugger), it will be null as we cleared it.
                //If we would not call finalizer.clear() in monitoring thread here we will see the object value.
                //Note that after java 9 the referent cleared internally not waiting the phantomref object to be cleared.
                System.out.println("Getting internal referent by reflection as reference.get() always return null");
                Field referentField = Reference.class.getDeclaredField("referent");
                referentField.setAccessible(true);
                A referent = (A) referentField.get(reference);
                System.out.println("Check Referent: " + (referent == null ? null : referent.s));
            } catch (InterruptedException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            System.out.println("Finished all running threads. Exiting!");
        }));
        phantomRefTest.monitorPhantomReference(referenceQueue, reference, countDownLatch);

    }

    private void monitorPhantomReference(ReferenceQueue<A> referenceQueue, Reference<A> reference,
                                         CountDownLatch latch) {
        ExecutorService executorService = null;
        try {
            executorService = Executors.newSingleThreadExecutor();
            Runnable runnable = () -> {
                try {
                    //We can use either a loop with poll method and break if poll return not null and instead our ref
                    //or use blocking remove method most probably with a timeout to prevent blocking forever
                    //because in shut down thread we set finishing to true so that we want this thread to finish
                    //processing and return.
                    //Using poll method
//                    MyFinalizer finalizer;
//                    while ((finalizer = (MyFinalizer) referenceQueue.poll()) != reference && !finishing) {
//                        finalizer.cleanUp();
//                        //if you do not call this, it will be cleared after phantomref object itself cleared. It is cleared automatically after java9
//                        finalizer.clear();
//                        //can sleep for a while
//                    }
                    //Using remove method
                    while (!finishing) {
                        try {
                            System.out.println("Start reading phantom reference from queue!");
                            MyFinalizer finalizer = (MyFinalizer) referenceQueue.remove(WAIT_TIME_FOR_REMOVING_PHANTOM_REFERENCE_MS);
                            if (finalizer != null) {
                                System.out.println("We got the phantom referenced object in the queue");
                                System.out.println("Reference queue return the same ref object: " + (finalizer == reference));
                                System.out.println("Phantom reference always return null: " + finalizer.get());
                                finalizer.cleanUp();
                                //if you do not call this, it will be cleared after phantomref object itself cleared. It is cleared automatically after java9
                                //finalizer.clear();
                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                            continue;
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                    System.out.println("Exiting phantom reference monitoring thread!");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Counting down the latch in phantom reference monitoring thread!");
                    latch.countDown();
                }
            };
            executorService.submit(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (executorService != null) {
                executorService.shutdown();
            }
        }

    }

    private class A {
        public A(String s) {
            this.s = s;
        }

        String s;
    }

    /**
     * The object will be phantomly reachable only when you have no reference from your app.
     */
    private class MyFinalizer extends PhantomReference<A> {
        String someInfoToUseInCleanUp;

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
        public MyFinalizer(A referent, String someInfo, ReferenceQueue<? super A> q) {
            super(referent, q);
            this.someInfoToUseInCleanUp = someInfo;
        }

        public void cleanUp() {
            System.out.println("Clean up resources with info " + someInfoToUseInCleanUp);
        }
    }

}
