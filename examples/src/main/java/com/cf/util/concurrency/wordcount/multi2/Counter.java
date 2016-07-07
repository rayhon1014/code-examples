package com.cf.util.concurrency.wordcount.multi2;

/**
 * Created by ray on 7/3/16.
 */

import com.cf.util.concurrency.wordcount.Page;
import com.cf.util.concurrency.wordcount.Words;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

class Counter implements Runnable {

    private BlockingQueue<Page> queue;
    private ConcurrentMap<String, Integer> counts;
    private HashMap<String, Integer> localCounts;

    public Counter(BlockingQueue<Page> queue,
                   ConcurrentMap<String, Integer> counts) {
        this.queue = queue;
        this.counts = counts;
        localCounts = new HashMap<String, Integer>();
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
            mergeCounts();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void countWord(String word) {
        Integer currentCount = localCounts.get(word);
        if (currentCount == null)
            localCounts.put(word, 1);
        else
            localCounts.put(word, currentCount + 1);
    }

    private void mergeCounts() {
        for (Map.Entry<String, Integer> e: localCounts.entrySet()) {
            String word = e.getKey();
            Integer count = e.getValue();
            while (true) {
                Integer currentCount = counts.get(word);
                if (currentCount == null) {
                    if (counts.putIfAbsent(word, count) == null)
                        break;
                } else if (counts.replace(word, currentCount, currentCount + count)) {
                    break;
                }
            }
        }
    }
}
