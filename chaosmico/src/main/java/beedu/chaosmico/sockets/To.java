package beedu.chaosmico.sockets;

import beedu.chaosmico.ChaosMicoException;

import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

class To extends Thread {
    private OutputStream to;
    private BlockingQueue<Integer> queue;

    public To(OutputStream to, BlockingQueue<Integer> queue) {
        this.to = to;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {

                Integer byteRead = queue.take();
                if (byteRead != -1) {
                    to.write(byteRead);
                } else {
                    to.close();
                    break;
                }
            }
            to.close();
        } catch (Exception e) {
            throw new ChaosMicoException("Exception writing bytes", e);
        }
    }
}
