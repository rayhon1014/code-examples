package com.cf.util.concurrency;

/**
 * Created by ray on 7/2/16.
 */
public class Puzzle {
    static boolean answerReady = false;
    static int answer = 0;
    static Thread t1 = new Thread() {
        public void run() {
            answer = 42;
            answerReady = true;
        }
    };
    static Thread t2 = new Thread() {
        public void run() {
            if (answerReady)
                System.out.println("The meaning of life is: " + answer);
            else
                System.out.println("I don't know the answer");
        }
    };

    public static void main(String[] args) throws InterruptedException {

        //the result can be the following:
        //(1) The meaning of life is: 42
        //(2) I don't know the answer
        //(3) The meaning of life is: 0
        //
        //We know 1 and 2 are possible due to race condition but how could #3 be possible? From the code ordering,
        //answer should have set to 42 before answerReady set to true. However, code reordering can happen as following:
        //(1) The compiler is allowed to statically optimize your code by reordering things.
        //(2) The JVM is allowed to dynamically optimize your code by reordering things.
        //(3) The hardware you’re running on is allowed to optimize performance by reordering things.
        t1.start(); t2.start();
        t1.join(); t2.join();

    }
}
