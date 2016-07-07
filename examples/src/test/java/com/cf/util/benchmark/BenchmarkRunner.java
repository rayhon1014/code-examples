package com.cf.util.benchmark;


public class BenchmarkRunner {

    private static String TARGET_URL = "http://localhost:8080/index.html";

    // public static methods ------------------------------------------------------------------------------------------

    public static void runApacheBenchmark() {
        AbstractBenchmark benchmark = new ApacheBenchmark(100, 50, 10, TARGET_URL);
        BenchmarkResult result = benchmark.doBenchmark();
        System.err.println( result );
    }

    public static void runAhcBenchmark() {
        AbstractBenchmark benchmark = new AhcBenchmark(100, 50, 10, TARGET_URL);
        BenchmarkResult result = benchmark.doBenchmark();
        System.err.println( result );
    }


    public static void runJettyBenchmark()
    {
        JettyBenchmark benchmark = new JettyBenchmark( 100, 50, 10, TARGET_URL );
        BenchmarkResult result = benchmark.doBenchmark();
        System.err.println( result );
    }


    // main -----------------------------------------------------------------------------------------------------------

    public static void main( String[] args )
            throws Exception
    {
        if (args.length > 0 && args[0] != null)
            TARGET_URL = args[0];

        runApacheBenchmark();
        runAhcBenchmark();
        runJettyBenchmark();

    }
}
