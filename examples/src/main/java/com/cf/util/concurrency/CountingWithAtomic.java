package com.cf.util.concurrency;

/**
 * Created by ray on 7/3/16.
 *
 *
 * Java allows us to mark a variable as volatile. Doing so guarantees that reads and writes to that variable will not
 * be reordered. We could fix Puzzle by making answerReady volatile.
 * Volatile is a very weak form of synchronization. It would not help us fix Counter, for example, because making count
 * volatile would not ensure that count++ is atomic.
 *
 * These days, with highly optimized JVMs that have very low-overhead locks, valid use cases for volatile variables
 * are rare. If you find yourself considering volatile, you should probably use one of the java.util.concurrent.atomic
 * classes instead.
 */
import java.util.concurrent.atomic.AtomicInteger;

public class CountingWithAtomic {
    public static void main(String[] args) throws InterruptedException {
        //no memory visibility problem
        //no deadlock issue as there is no lock there
        final AtomicInteger counter = new AtomicInteger();

        class CountingThread extends Thread {
            public void run() {
                for(int x = 0; x < 10000; ++x)
                    counter.incrementAndGet();
            }
        }

        CountingThread t1 = new CountingThread();
        CountingThread t2 = new CountingThread();

        t1.start(); t2.start();
        t1.join(); t2.join();

        System.out.println(counter.get());
    }
}
