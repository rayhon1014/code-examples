package com.cf.util.async;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.asynchttpclient.*;

import com.sun.net.httpserver.HttpServer;

/**
 * a test driver to explore asyncHttpClient differences between netty and grizzly providers
 */

@SuppressWarnings("restriction")
public class AsyncHttpClientTest {

    AsyncHttpClient client;
    AsyncHttpClientConfig config;
    HttpServer httpServer;
    Request request;
    static StopWatch timer;
    private static CountDownLatch latch;
    private static final Logger logger = LoggerFactory.getLogger(AsyncHttpClientTest.class);

    private final static int serverResponseTimeoutMs = 1000;
    private final static int clientTimeoutMs = 400;
    private final static int auctionTimeMs = 500;
    private final static int keepAliveTimeMs = 5000;

    private static int firstEvent = 0;
    private static final int AuctionEvent = 1;
    private static final int TimeoutEvent = 2;

    private static int useNetty = 0;
    private static int useGrizzly = 1;

    @Before
    public void init() throws IOException {
        startServer();
    }

    @After
    public void shutdown() {
        logger.info("Shutting down AsyncHttpClient");
        if (client != null) {
            try {
                client.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        stopServer();
        timer.stop();
    }

    @Test
    public void nettyTimeoutTest() throws IOException, InterruptedException {
        firstEvent = 0;
        initClient(useNetty);
        sendRequest();
        assertEquals("Auction ended before timeout", TimeoutEvent, firstEvent);
    }

    @Test
    public void grizzlyTimeoutTest() throws IOException, InterruptedException {
        firstEvent = 0;
        initClient(useGrizzly);
        sendRequest();
        assertEquals("Auction ended before timeout", TimeoutEvent, firstEvent);
    }

    protected void initClient(int provider) {
        timer = new StopWatch();

        if (useNetty == provider) {
            config = createClientConfig();
            client = new DefaultAsyncHttpClient(config);
        } else if (useGrizzly == provider) {
        } else {
            assertTrue("unknown provder", false);
        }
    }


    private void sendRequest() throws IOException, InterruptedException {
        logger.info("sending request");
        createRequest();
        CountDownLatch latch = new CountDownLatch(1);
        client.executeRequest(request, new Callback());
        timer.start();
        doWait(latch);
        Thread.sleep(keepAliveTimeMs);  // stay alive so we can get the grizzly timeout, even though it is after the auction ends
    }

    private long doWait(final CountDownLatch latch) {
        StopWatch bidPhaseCompleteWatch = new StopWatch();
        bidPhaseCompleteWatch.start();
        try {
            if (!latch.await(auctionTimeMs, TimeUnit.MILLISECONDS)) {
                logger.warn("Auction ended due to max time [" + auctionTimeMs + "], not count down");
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted waiting for count down", e);
        } finally {
            bidPhaseCompleteWatch.stop();
        }
        firstEvent = AuctionEvent;
        logger.info("Done waiting for bid");
        return bidPhaseCompleteWatch.getTime();
    }

    protected static void setEvent(int event) {
        if (0 == firstEvent) {
            firstEvent = event;
        }
    }

    private static final String url = "http://localhost:18088/test/";
    private static final String body = "{\"body\":empty}";

    protected void createRequest() {
        this.request = new RequestBuilder("POST").setUrl(url).setBody(body).build();
    }

    protected AsyncHttpClientConfig createClientConfig() {
        DefaultAsyncHttpClientConfig.Builder builder = new DefaultAsyncHttpClientConfig.Builder()
                .setRequestTimeout(clientTimeoutMs)
                .setMaxConnections(200)
                .setConnectTimeout(100)
                .setCompressionEnforced(true)
                .setPooledConnectionIdleTimeout(100)
                .setMaxConnectionsPerHost(20);
        return builder.build();
    }


    public void startServer() throws IOException {
        String host = "localhost";
        int port = 18088;
        int connections = 2;


        logger.debug("Connecting to host [" + host + "] and port [" + port + "]");

        httpServer = HttpServer.create(new InetSocketAddress(port), connections);
        httpServer.createContext("/test", new DummyServer());

        Executor executor = Executors.newCachedThreadPool();
        httpServer.setExecutor(executor);

        logger.debug("starting bidder simulator http server");
        httpServer.start();
    }

    public void stopServer() {
        logger.debug("stopping bidder simulator http server");
        httpServer.stop(0);        //Stop immediately, without any delay
    }


    private static class Callback extends AsyncCompletionHandler<Response> {

        Callback() {
        }

        @Override
        public Response onCompleted(Response response) throws Exception {
            //timer.stop();
            //logger.info("completed: " + timer.getTime());
            logger.info("completed");
            return null;
        }


        @Override
        public synchronized void onThrowable(Throwable t) {
            timer.split();
            //logger.info("caught exception at :" + timer.getTime() + "> " + t.getMessage());
            logger.info("caught exception " + t.getMessage());
            timer.split();
            long time = timer.getSplitTime();
            timer.unsplit();
            logger.info("exception at : " + time);
            setEvent(TimeoutEvent);
            latch.countDown();

        }

    }

    public class DummyServer implements HttpHandler {


        @Override
        public void handle(HttpExchange he) throws IOException {
            logger.debug("server got request");
            try {
                Thread.sleep(serverResponseTimeoutMs);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            logger.debug("server returning");
            String response = "hello";
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.flush();
            os.close();
        }

    }
}

