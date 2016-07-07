package com.cf.util.concurrency.wordcount.multi;

/**
 * Created by ray on 7/3/16.
 */
import com.cf.util.concurrency.wordcount.Page;
import com.cf.util.concurrency.wordcount.Words;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Map;

class Counter implements Runnable {

    private BlockingQueue<Page> queue;
    private Map<String, Integer> counts;
    private static ReentrantLock lock;

    public Counter(BlockingQueue<Page> queue,
                   Map<String, Integer> counts) {
        this.queue = queue;
        this.counts = counts;
        lock = new ReentrantLock();
    }

    public void run() {
        try {
            while(true) {
                Page page = queue.take();
                if (page.isPoisonPill())
                    break;
                Iterable<String> words = new Words(page.getText());
                for (String word: words)
                    countWord(word);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void countWord(String word) {
        lock.lock();
        try {
            Integer currentCount = counts.get(word);
            if (currentCount == null)
                counts.put(word, 1);
            else
                counts.put(word, currentCount + 1);
        } finally { lock.unlock(); }
    }
}
