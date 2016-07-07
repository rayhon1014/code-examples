package com.cf.util.concurrency;

/**
 * Created by ray on 7/2/16.
 */
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

class Downloader extends Thread {

    private InputStream in;
    private OutputStream out;
    private ArrayList<ProgressListener> listeners;
    private CopyOnWriteArrayList<ProgressListener> listeners_v2;

    public Downloader(URL url, String outputFilename) throws IOException {
        in = url.openConnection().getInputStream();
        out = new FileOutputStream(outputFilename);
        listeners = new ArrayList<ProgressListener>();
        listeners_v2 = new CopyOnWriteArrayList<ProgressListener>();
    }

    public synchronized void addListener(ProgressListener listener) {
        listeners_v2.add(listener);
        System.out.println("listener is added to download");
    }

    public synchronized void removeListener(ProgressListener listener) {
        listeners_v2.remove(listener);
    }

    /**
     * remove the synchronized keyword on the updateProgress method. That way, we are not holding the lock on
     * the Downloader. Then, we make a defensive copy of the listener list to avoid ConcurrentModificationException
     * @param n
     */
    private void updateProgress(int n) {
//        ArrayList<ProgressListener> listenersCopy;
//        synchronized(this) {
//            listenersCopy = (ArrayList<ProgressListener>)listeners.clone();
//        }
        for (ProgressListener listener: listeners_v2) {
            System.out.println("update progress for listener ["+listener+"]");
            listener.onProgress(n);
        }
    }

    public void run() {
        int n = 0, total = 0;
        byte[] buffer = new byte[1024];

        try {
            while((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
                total += n;
                updateProgress(total);
            }
            out.flush();
        } catch (IOException e) { }
    }

    public static void main(String[] args) throws Exception
    {
        URL from = new URL("http://www.google.com");
        final Downloader downloader = new Downloader(from, "download.out");
        final ProgressListener l = new ProgressListener() {
            public void onProgress(int n) {
               try {
                    System.out.println(n);
                    System.out.flush();
                   /**
                    * To make a deadlock happen, I had to create a new thread before calling the
                    * Downloader’s removeListener() method. That’s because the lock that Java creates
                    * for each object is a ReentrantLock. But if you don't synchronized the updateProgress, no lock
                    * need to obtain and remove from Array will happen and then CurrentModificationException will be
                    * ended up.
                    */
                    AlienThread alien = new AlienThread(downloader, this);
                    alien.start(); alien.join();

               }
               catch (InterruptedException ex) {
               }
            }
            public void onComplete(boolean success) {}
        };
        downloader.start();
        System.out.println("Listener instance:"+l);
        downloader.addListener(l);
        downloader.join();
    }

    public static class AlienThread extends Thread {
        private Downloader downloader;
        private ProgressListener l;

        AlienThread(Downloader d, ProgressListener l) {
            this.downloader = d;
            this.l = l;
        }
        public void run() {
            System.out.println("attempt to remove listener from download");
            downloader.removeListener(l);
            System.out.println("removed listener from download");
        }
    }

}
