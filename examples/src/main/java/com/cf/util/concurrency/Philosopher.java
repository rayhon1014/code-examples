package com.cf.util.concurrency;

/**
 * Created by ray on 7/2/16.
 */

import java.util.Random;

class Philosopher extends Thread {
    private Chopstick first, second;
    private Random random;
    private int thinkCount;

    /**
     * as long as you can get the lock with a global increasing order, you can avoid the deadlock
     * @param left
     * @param right
     */
    public Philosopher(Chopstick left, Chopstick right) {
        //to avoid deadlock
        if(left.getId() < right.getId()) {
            first = left; second = right;
        } else {
            first = right; second = left;
        }
        random = new Random();
    }

    class Chopstick{
        private long id;

        public Chopstick()
        {
            id = System.currentTimeMillis();
        }
        public long getId() {
            return id;
        }
    }

    public void run() {
        try {
            while(true) {
                ++thinkCount;
                if (thinkCount % 10 == 0)
                    System.out.println("Philosopher " + this + " has thought " + thinkCount + " times");
                Thread.sleep(random.nextInt(1000));     // Think for a while
                synchronized(first) {                   // Grab first chopstick
                    synchronized(second) {                // Grab second chopstick
                        Thread.sleep(random.nextInt(1000)); // Eat for a while
                    }
                }
            }
        } catch(InterruptedException e) {}
    }
}
