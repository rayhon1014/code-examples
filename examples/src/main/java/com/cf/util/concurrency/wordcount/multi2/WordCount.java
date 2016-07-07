package com.cf.util.concurrency.wordcount.multi2;

/**
 * Created by ray on 7/3/16.
 */
import com.cf.util.concurrency.wordcount.Page;
import com.cf.util.concurrency.wordcount.Parser;
import com.cf.util.concurrency.wordcount.PoisonPill;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Multiple consumers with local hash map and merge on concurrent hash map in batch (minimize contention)
 * Result: Multiple consumer is faster than single consumer and scale with # of cores in system
 * if your system has 4 cores, you can move the consumer count to 8.  This is possible because each of the cores in
 * MacBook supports two “hyperthreads”—availableProcessors() actually returns eight, even though there are only four
 * physical cores.
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
