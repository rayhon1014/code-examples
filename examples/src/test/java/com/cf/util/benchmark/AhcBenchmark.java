package com.cf.util.benchmark;

import org.asynchttpclient.*;

import java.util.Vector;
import java.util.concurrent.Future;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="http://bruno.factor45.org/">Bruno de Carvalho</a>
 */
public class AhcBenchmark extends AbstractBenchmark {

    // internal vars --------------------------------------------------------------------------------------------------

    private AsyncHttpClient client;

    // constructors ---------------------------------------------------------------------------------------------------

    public AhcBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String uri) {
        super(threads, requestsPerThreadPerBatch, batches, uri);
    }
    // AbstractBenchmark ----------------------------------------------------------------------------------------------

    @Override
    protected void setup() {
        super.setup();

        AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().setMaxConnectionsPerHost(10).setConnectTimeout(0).build();
        this.client = new DefaultAsyncHttpClient(config);
    }

    @Override
    protected void tearDown(){
        try {
            super.tearDown();
            this.client.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void warmup() {
        List<Future<Response>> futures = new ArrayList<Future<Response>>(this.warmupRequests);
        for (int i = 0; i < this.warmupRequests; i++) {
            futures.add(this.client.prepareGet(this.url).execute());
        }

        for (Future<Response> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected BatchResult runBatch() {
        final CountDownLatch latch = new CountDownLatch(this.threads);
        final Vector<ThreadResult> threadResults = new Vector<ThreadResult>(this.threads);

        long batchStart = System.nanoTime();
        for (int i = 0; i < this.threads; i++) {
             this.executor.submit(new Runnable()
             {
                 @Override
                 public void run() {
                     final AtomicInteger successful = new AtomicInteger();
                     long start = System.nanoTime();
                     for (int i = 0; i < requestsPerThreadPerBatch; i++) {
                         try {
                             Response response = client.prepareGet(url).execute().get();

                             if ((response.getStatusCode() >= 200) && (response.getStatusCode() <= 299)) {
                                 successful.incrementAndGet();
                             }
                         } catch (InterruptedException e) {
                             e.printStackTrace();
                         } catch (ExecutionException e) {
                             e.printStackTrace();
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