package com.cf.util.concurrency.wordcount.multi1;

/**
 * Created by ray on 7/3/16.
 */
import com.cf.util.concurrency.wordcount.Page;
import com.cf.util.concurrency.wordcount.Parser;
import com.cf.util.concurrency.wordcount.PoisonPill;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Multiple consumers with concurrent hash map
 * Result: Multiple consumer is faster than single consumer but not like 4x faster if you have 4 cores.
 */
public class WordCount {

    private static final int NUM_COUNTERS = 4;

    public static void main(String[] args) throws Exception {
        ArrayBlockingQueue<Page> queue = new ArrayBlockingQueue<Page>(100);
        ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<String, Integer>();
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < NUM_COUNTERS; ++i)
            executor.execute(new Counter(queue, counts));
        Thread parser = new Thread(new Parser(queue));
        long start = System.currentTimeMillis();
        parser.start();
        parser.join();
        for (int i = 0; i < NUM_COUNTERS; ++i)
            queue.put(new PoisonPill());
        executor.shutdown();
        executor.awaitTermination(10L, TimeUnit.MINUTES);
        long end = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (end - start) + "ms");

        // for (Map.Entry<String, Integer> e: counts.entrySet()) {
        //   System.out.println(e);
        // }
    }
}