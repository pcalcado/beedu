package beedu.chaosmico.sockets;

import beedu.chaosmico.ChaosMicoException;

import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

class From extends Thread {
    private InputStream from;
    private BlockingQueue<Integer> queue;

    public From(InputStream from, BlockingQueue<Integer> queue) {
        this.from = from;
        this.queue = queue;
    }

    @Override
    public void run() {
        int b;
        try {
            while ((b = from.read()) != -1) {
                queue.put(b);
            }
            from.close();
        } catch (Exception e) {
            throw new ChaosMicoException("Exception reading bytes", e);
        }
    }
}
