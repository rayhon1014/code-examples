package com.cf.util.http;

import com.cf.util.http.model.HttpResponse;
import com.cf.util.task.Task;
import com.cf.util.task.TaskManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/test-beans.xml"
})
public class DynamicHttpTemplateTest {
    @Autowired
    private HttpService dynamicHttpTemplate;

    @Test
    public void testGetContent() throws Exception {
        List<String> l = new ArrayList<>();
        l.add(phantomjsBatchTest(20,  10));
        l.add(phantomjsBatchTest(20, 20));
        l.add(phantomjsBatchTest(100, 10));
        l.add(phantomjsBatchTest(100, 20));
        l.add(phantomjsBatchTest(100, 40));
        l.add(phantomjsBatchTest(100, 60));
        l.add(phantomjsBatchTest(100, 100));
        l.add(phantomjsBatchTest(200, 20));
        l.add(phantomjsBatchTest(200, 60));
        l.add(phantomjsBatchTest(200, 100));
        l.add(phantomjsBatchTest(200, 200));

        System.out.println("-----------------------");
        System.out.println("Total Tasks | Thread Num | Fail Tasks | Fail % | Cost (second) | Cost per Task");
        for (String result: l) {
            System.out.println(result);
        }
        System.out.println("-----------------------");

    }

    private String phantomjsBatchTest(int totalTasks, int threadNum) {
        TaskManager<Boolean> taskManager = new TaskManager<Boolean>();
        List<Task<Boolean>> searchTasks = new ArrayList<Task<Boolean>>();

        long start = System.currentTimeMillis();
        for (int i=1;i<=totalTasks;i++) {
            final int num = i;
            searchTasks.add(new Task<Boolean>() {

                @Override
                public String getTaskName() {
                    return "Check Task";
                }

                @Override
                public void onComplete(Boolean result) {

                }

                @Override
                public Boolean call() throws Exception {
//                    String url = "http://www.shopstyle.com/browse?fts=shoes&cb=" + System.currentTimeMillis();
                    String url = "http://localhost/test/"+num+".html";
                    HttpResponse httpResponse = dynamicHttpTemplate.getContent(url);
                    System.out.println(httpResponse.getContent());
                    int matchCount = StringUtils.countMatches(httpResponse.getContent(), "class=\"product-cell\"");

                    return (matchCount == 40);
                }
            });

        }

        final List<Boolean> results = taskManager.parallel(searchTasks, threadNum);
        long duration = (System.currentTimeMillis() - start) / 1000; // seconds

        int failNum = 0;
        for (boolean success: results) {
            if (!success) {
                failNum++;
            }
        }

        return totalTasks+ "|" + threadNum + "|" +failNum+ "|" +(failNum*100/totalTasks)+ "|" +duration+ "|" +String.format("%.2f", (double)duration/totalTasks);
    }

    @Test
    public void testSendRequest() {
//        String url = "http://www.hm.com/us/search/#!/products?q=shoes";
//        String url = "http://www.gap.com/browse/search.do?searchText=shoes";
        for (int i=1; i<=5; i++) {
            String url = "http://localhost/test/"+i+".html";
            long start = System.currentTimeMillis();
            String content = ((DynamicHttpTemplate)dynamicHttpTemplate).sendRequest(url, true, "GET", null, null).getContent();
            System.out.println(content);
            long end = System.currentTimeMillis();
//            System.out.println("perf_info => Get HTML content size ["+content.length()+"], cost ["+(end - start)/1000+"] seconds.");
        }

    }

    @Test
    public void testSendRequestParallel() throws Exception {
//        phantomjsBatchTest(5, 5);

        // warm up
//        for (int i=0;i<5;i++) {
//            String url = "http://www.hm.com/us/search/#!/products?q=shoes";
//            String content = ((DynamicHttpTemplate)dynamicHttpTemplate).sendRequest(url, true, "GET", null, null);
//        }

        long start = System.currentTimeMillis();
        String url = "http://tools.pingdom.com/fpt/#!/qokdl/1661hk.com";
        String content = ((DynamicHttpTemplate)dynamicHttpTemplate).sendRequest(url, true, "GET", null, null).getContent();

        long end = System.currentTimeMillis();
        System.out.println("perf_info => Get HTML content size ["+content.length()+"], cost ["+(end - start)/1000+"] seconds.");

        String outFile = "/tmp/phantomjs.out/"+System.currentTimeMillis()+".html";
        FileUtils.writeStringToFile(new File(outFile), content);
        Runtime.getRuntime().exec("/usr/bin/open " + outFile);
    }
}