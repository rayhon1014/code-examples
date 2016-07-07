package com.cf.util.benchmark;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;


import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class JettyBenchmark extends AbstractBenchmark {
    // internal vars --------------------------------------------------------------------------------------------------

    private HttpClient client;

    // constructors ---------------------------------------------------------------------------------------------------

    public JettyBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String uri) {
        super(threads, requestsPerThreadPerBatch, batches, uri);
    }
    // AbstractBenchmark ----------------------------------------------------------------------------------------------

    @Override
    protected void setup() {
        super.setup();
        this.client = new HttpClient();
        this.client.setRequestBufferSize(8 * 1024);
        this.client.setResponseBufferSize(8 * 1024);
        this.client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        this.client.setMaxConnectionsPerAddress(10);
    }

    @Override
    protected void tearDown() {
        super.tearDown();

        try {
            this.client.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void warmup() {
        try {
            this.client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < this.warmupRequests; i++) {

            ContentExchange exchange = new ContentExchange();

            exchange.setURL(this.url);

            try {
                this.client.send(exchange);
                try {
                    exchange.waitForDone();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException ex) {
            }
        }
    }

    @Override
    protected BatchResult runBatch() {
        final CountDownLatch latch = new CountDownLatch(this.threads);
        final Vector<ThreadResult> threadResults = new Vector<ThreadResult>(this.threads);

        long batchStart = System.nanoTime();
        for (int i = 0; i < this.threads; i++) {
            this.executor.submit(new Runnable() {

                @Override
                public void run() {
                    final AtomicInteger successful = new AtomicInteger();
                    long start = System.nanoTime();
                    for (int i = 0; i < requestsPerThreadPerBatch; i++) {
                        ContentExchange exchange = new ContentExchange();

                        exchange.setURL(url);

                        try {
                            client.send(exchange);
                            try {
                                exchange.waitForDone();
                                if (exchange.getResponseStatus() == 200) {
                                    successful.incrementAndGet();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                    }
                    long totalTime = System.nanoTime() - start;
                    threadResults.add(new ThreadResult(requestsPerThreadPerBatch, successful.get(), totalTime));
                    latch.countDown();
                }
            });
        }


        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
        long batchTotalTime = System.nanoTime() - batchStart;

        return new BatchResult(threadResults, batchTotalTime);
    }
}