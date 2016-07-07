package com.cf.util.benchmark;


public class ThreadResult {

    // internal vars --------------------------------------------------------------------------------------------------

    private final int targetRequests;
    private final int successfulRequests;
    private final long totalTime;

    // constructors ---------------------------------------------------------------------------------------------------

    public ThreadResult(int targetRequests, int successfulRequests, long totalTime) {
        this.targetRequests = targetRequests;
        this.successfulRequests = successfulRequests;
        this.totalTime = totalTime;
    }

    // getters & setters ----------------------------------------------------------------------------------------------

    public int getTargetRequests() {
        return targetRequests;
    }

    public int getSuccessfulRequests() {
        return successfulRequests;
    }

    public long getTotalTime() {
        return totalTime;
    }
}