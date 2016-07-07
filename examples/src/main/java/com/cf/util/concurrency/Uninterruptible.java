package com.cf.util.concurrency;

/**
 * Created by ray on 7/3/16.
 *
 * this demonstrates deadlock on the intrinsic lock and there is no way to kill a deadlock thread.
 * You can only release the lock via thread run() return (possibly as a result of an InterruptedException).
 * So if your thread is deadlocked on an intrinsic lock, you are simply out of luck. You can't interrupt
 * it, and the only way that thread is ever going to exit is if you kill JVM it is running in.
 *
 */
public class Uninterruptible {

    public static void main(String[] args) throws InterruptedException {

        final Object o1 = new Object();
        final Object o2 = new Object();

        Thread t1 = new Thread() {
            public void run() {
                try {
                    synchronized (o1) {
                        Thread.sleep(1000);
                        synchronized (o2) {
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println("t1 interrupted");
                }
            }
        };

        Thread t2 = new Thread() {
            public void run() {
                try {
                    synchronized (o2) {
                        Thread.sleep(1000);
                        synchronized (o1) {
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println("t2 interrupted");
                }
            }
        };

        t1.start();
        t2.start();
        Thread.sleep(2000);
        t1.interrupt();//you cannot interrupt it
        t2.interrupt();
        t1.join();
        t2.join();
    }
}
