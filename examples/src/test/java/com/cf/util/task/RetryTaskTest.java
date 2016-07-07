package com.cf.util.task;

import org.junit.Assert;
import org.junit.Test;

public class RetryTaskTest {

    @Test
    public void testRetry() throws Exception {
        TestTask testTask = new TestTask();
        testTask.reset();

        RetryTask retryTask = new RetryTask(testTask, -1);
        try {
            retryTask.call();
        } catch (Exception ex) {
            Assert.assertEquals("Intended Excception", ex.getMessage());
            Assert.assertEquals(1, testTask.getExecuteCount());
        }

        testTask.reset();
        retryTask = new RetryTask(testTask, 0);
        try {
            retryTask.call();
        } catch (Exception ex) {
            Assert.assertEquals("Intended Excception", ex.getMessage());
            Assert.assertEquals(1, testTask.getExecuteCount());
        }

        testTask.reset();
        retryTask = new RetryTask(testTask, 1);
        try {
            retryTask.call();
        } catch (Exception ex) {
            Assert.assertEquals("Intended Excception", ex.getMessage());
            Assert.assertEquals(2, testTask.getExecuteCount());
        }
    }

    private static class TestTask implements Task {
        private int executeCount = 0;

        public int getExecuteCount() {
            return executeCount;
        }

        public void reset() {
            executeCount = 0;
        }

        @Override
        public String getTaskName() {
            return null;
        }

        @Override
        public void onComplete(Object result) {

        }

        @Override
        public Object call() throws Exception {
            executeCount++;
            throw new Exception("Intended Excception");
        }
    }

}