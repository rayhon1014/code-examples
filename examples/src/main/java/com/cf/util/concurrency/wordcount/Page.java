package com.cf.util.concurrency.wordcount;

/**
 * Created by ray on 7/3/16.
 */
public abstract class Page {
    public String getTitle() { throw new UnsupportedOperationException(); }
    public String getText() { throw new UnsupportedOperationException(); }
    public boolean isPoisonPill() { return false; }
}
