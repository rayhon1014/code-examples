package com.cf.util.concurrency;

/**
 * Created by ray on 7/2/16.
 */
public class Counting {

    class Counter {
        private int count = 0;
        //mutual exclusion with intrinsic object lock
        //for simple cases like this where only one variable is involved, the java.util.concurrent.atomic package
        //provides good alternatives to using a lock
        public synchronized void increment() { ++count; }

        //if this method is not synchronize, a thread calling getCount() may see a stale value
        public synchronized int getCount() { return count; }
    }

    public static void main(String[] args) throws InterruptedException {
        final Counter counter = new Counting().new Counter();
        class CountingThread extends Thread {
            public void run() {
                for(int x = 0; x < 10000; ++x)
                    counter.increment();
            }
        }
        CountingThread t1 = new CountingThread();
        CountingThread t2 = new CountingThread();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(counter.getCount());
    }
}