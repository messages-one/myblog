package falsesharing;

import sun.misc.Contended;

/**
 * This class is used to test the false sharing concept. It uses 2 threads changing 2 independent int variables.
 * By changing the variables to volatile and non-volatile, marking and unmarking the variables as @Contented,
 * and adding 7 long and 1 int variables to span whole cache line for a volatile value which is 64 bytes.
 * At the bottom of the class you can find various results of the tests with this class.
 * Contended annotation does not work on user classpath by default and only work for classes on bootclasspath.
 * So we need to add -XX:-RestrictContended VM argument on JVM startup.
 * @author Ali Gelenler
 */

public class TestPaddingAndContended {

    private static final long NUM_OF_ITERATION = 100000000L;

    private static final TestPaddingAndContended INSTANCE = new TestPaddingAndContended();

    private TestPaddingAndContended() {

    }

    @Contended
    private volatile int myVolatileValue1; // 4 bytes

    // fields used for padding to prevent false sharing - uncomment to test padding after removing @Contented above
//	private volatile int dummyInt1; // 4 bytes
//	private volatile long dummyLong1; // 8 bytes
//	private volatile long dummyLong2;
//	private volatile long dummyLong3;
//	private volatile long dummyLong4;
//	private volatile long dummyLong5;
//	private volatile long dummyLong6;
//	private volatile long dummyLong7;

    @Contended
    private volatile int myVolatileValue2; // 4 bytes

    public static void main(String[] args) {
        Thread thread1 = new Thread(new Thread1Runnable());
        Thread thread2 = new Thread(new Thread2Runnable());
        thread1.start();
        thread2.start();
    }

    public static class Thread1Runnable implements Runnable {

        public void run() {
            long start = System.nanoTime();
            long i = NUM_OF_ITERATION;
            while (--i != 0) {
                INSTANCE.myVolatileValue1 = (int) i;
            }
            System.out.println("End of thread 1, last value of myVolatileValue1 is " + INSTANCE.myVolatileValue1 + " it took " + (System.nanoTime() - start) + " nanoseconds");
        }
    }

    public static class Thread2Runnable implements Runnable {

        public void run() {
            long start = System.nanoTime();
            long i = NUM_OF_ITERATION;
            while (--i != 0) {
                INSTANCE.myVolatileValue2 = (int) i;
            }
            System.out.println("End of thread 2, last value of myVolatileValue2 is " + INSTANCE.myVolatileValue2 + " it took " + (System.nanoTime() - start) + " nanoseconds");
        }
    }
}

/**
 2 non-shared volatile without padding, suppose to be on the same cache line
 First Attempt:
 End of thread 1, last value of myVolatileValue is 1 it took 3133182407 nanoseconds
 End of thread 2, last value of myValue is 1 it took 3167983652 nanoseconds
 Second Attempt:
 End of thread 2, last value of myValue is 1 it took 3383002049 nanoseconds
 End of thread 1, last value of myVolatileValue is 1 it took 3418189666 nanoseconds
 Third Attempt:
 End of thread 2, last value of myValue is 1 it took 3480275412 nanoseconds
 End of thread 1, last value of myVolatileValue is 1 it took 3492587998 nanoseconds
 Avg. running time: 3.34 sec
 -----------------------------------------------------------------------------------
 2 non-shared volatile with padding, suppose to be on different cache line
 First Attempt:
 End of thread 2, last value of myValue is 1 it took 3000347735 nanoseconds
 End of thread 1, last value of myVolatileValue is 1 it took 3032000048 nanoseconds
 Second Attempt:
 End of thread 1, last value of myVolatileValue is 1 it took 3104793729 nanoseconds
 End of thread 2, last value of myValue is 1 it took 3143762961 nanoseconds
 Third Attempt:
 End of thread 1, last value of myVolatileValue is 1 it took 3091972217 nanoseconds
 End of thread 2, last value of myValue is 1 it took 3106549306 nanoseconds
 Avg. running time: 3.07 sec
 -----------------------------------------------------------------------------------
 2 non-shared volatile with @Contended, suppose to be on different cache line
 First Attempt:
 End of thread 1, last value of myVolatileValue1 is 1 it took 1406085998 nanoseconds
 End of thread 2, last value of myVolatileValue2 is 1 it took 1684038215 nanoseconds
 Second Attempt:
 End of thread 1, last value of myVolatileValue1 is 1 it took 1329552500 nanoseconds
 End of thread 2, last value of myVolatileValue2 is 1 it took 1374891105 nanoseconds
 Third Attempt:
 End of thread 2, last value of myVolatileValue2 is 1 it took 1093849103 nanoseconds
 End of thread 1, last value of myVolatileValue1 is 1 it took 1489967853 nanoseconds
 Avg: 1.39
 -----------------------------------------------------------------------------------
 2 non-shared non-volatile without padding, suppose to be on the same cache line
 First Attempt:
 End of thread 2, last value of myValue is 1 it took 112383522 nanoseconds
 End of thread 1, last value of myVolatileValue is 1 it took 114422239 nanoseconds
 Second Attempt:
 End of thread 1, last value of myVolatileValue is 1 it took 115202830 nanoseconds
 End of thread 2, last value of myValue is 1 it took 115687606 nanoseconds
 Third Attempt:
 End of thread 2, last value of myValue is 1 it took 105320160 nanoseconds
 End of thread 1, last value of myVolatileValue is 1 it took 106646504 nanoseconds
 Avg. running time: 1.11 sec
 -----------------------------------------------------------------------------------
 2 non-shared non-volatile with padding, suppose to be on different cache line
 First Attempt:
 End of thread 1, last value of myVolatileValue is 1 it took 113062087 nanoseconds
 End of thread 2, last value of myValue is 1 it took 113854150 nanoseconds
 Second Attempt:
 End of thread 2, last value of myValue is 1 it took 126021245 nanoseconds
 End of thread 1, last value of myVolatileValue is 1 it took 126020036 nanoseconds
 Third Attempt:
 End of thread 2, last value of myValue is 1 it took 109028728 nanoseconds
 End of thread 1, last value of myVolatileValue is 1 it took 109101776 nanoseconds
 Avg. running time: 1.16 sec
 */
