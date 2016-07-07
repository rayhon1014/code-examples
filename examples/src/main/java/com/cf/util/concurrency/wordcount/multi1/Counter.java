package com.cf.util.concurrency.wordcount.multi1;

/**
 * Created by ray on 7/3/16.
 */
import com.cf.util.concurrency.wordcount.Page;
import com.cf.util.concurrency.wordcount.Words;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

class Counter implements Runnable {

    private BlockingQueue<Page> queue;
    private ConcurrentMap<String, Integer> counts;

    public Counter(BlockingQueue<Page> queue,
                   ConcurrentMap<String, Integer> counts) {
        this.queue = queue;
        this.counts = counts;
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
        while (true) {
            Integer currentCount = counts.get(word);
            if (currentCount == null) {
                // ConcurrentHashMap not only provides atomic read-modify-write methods so we don't need to use
                // ReentrantLock to simulate the atomicity of read-modify-write but it’s been designed to support high
                // levels of concurrent access (via a technique called lock striping). If you don't do it and implement
                // you own version using ReentrantLock, you will actually see it slower due to contention issue.
                if (counts.putIfAbsent(word, 1) == null)
                    break;
            } else if (counts.replace(word, currentCount, currentCount + 1)) {
                break;
            }
        }
    }
}