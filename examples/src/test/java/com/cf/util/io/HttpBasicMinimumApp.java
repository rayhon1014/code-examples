package com.cf.util.io;

/**
 * Created by ray on 5/10/16.
 */

        import io.parallec.core.ParallecResponseHandler;
        import io.parallec.core.ParallelClient;
        import io.parallec.core.ResponseOnSingleTask;

        import java.util.Map;

/**
 * The Class HttpBasicMinimumApp.
 * With basic response handling.
 * Does not use response context
 */
public class HttpBasicMinimumApp {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        ParallelClient pc = new ParallelClient();
        pc.prepareHttpGet("")
                .setConcurrency(1000)
                .setTargetHostsFromString(
                        "www.parallec.io www.jeffpei.com www.restcommander.com")
                .execute(new ParallecResponseHandler() {
                    public void onCompleted(ResponseOnSingleTask res,
                                            Map<String, Object> responseContext) {
                        System.out.println(res.getResponseContent());
                    }
                });
        pc.releaseExternalResources();
    }
}
