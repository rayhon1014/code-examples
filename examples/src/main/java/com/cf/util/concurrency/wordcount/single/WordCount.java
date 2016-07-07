package com.cf.util.concurrency.wordcount.single;

/**
 * Created by ray on 7/3/16.
 */

import com.cf.util.concurrency.wordcount.Page;
import com.cf.util.concurrency.wordcount.Parser;
import com.cf.util.concurrency.wordcount.PoisonPill;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Single producer with single consumer and both run in its own thread
 * Result: Better the purely sequential.
 */
public class WordCount {

    public static void main(String[] args) throws Exception {
        /**
         * As well as blocking queues, java.util.concurrent provides ConcurrentLinkedQueue, an unbounded, wait-free,
         * and nonblocking queue. That sounds like a very desirable set of attributes, so why isn’t it a good choice for
         * this problem? The issue is that the producer and consumer may not (almost certainly will not) run at the same
         * speed. In particular, if the producer runs faster than the consumer, the queue will get larger and larger.
         * Given that the Wikipedia dump we’re parsing is around 40 GiB, that could easily result in the queue becoming
         * too large to fit in memory. Using a blocking queue, by contrast, will allow the producer to get ahead of
         * the consumer, but not too far
         */
        ArrayBlockingQueue<Page> queue = new ArrayBlockingQueue<Page>(100);
        HashMap<String, Integer> counts = new HashMap<String, Integer>();

        Thread counter = new Thread(new Counter(queue, counts));
        Thread parser = new Thread(new Parser(queue));
        long start = System.currentTimeMillis();

        counter.start();
        parser.start();
        parser.join();
        queue.put(new PoisonPill());
        counter.join();
        long end = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (end - start) + "ms");

        // for (Map.Entry<String, Integer> e: counts.entrySet()) {
        //   System.out.println(e);
        // }
    }
}