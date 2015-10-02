package beedu.chaosmico.sockets;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import static beedu.chaosmico.sockets.SocketFunctions.*;

public class ProxyRunnerThread extends Thread {
    private final static Logger LOGGER = Logger.getLogger(ProxyRunnerThread.class.getName());

    private static final int LINGER_TIME_MILLIS = 180;
    private static final int MAX_CONNS = 100;


    private String destinationHost;
    private int destinationPort;
    private int sourcePort;

    public ProxyRunnerThread(int sourcePort, String destinationHost, int destinationPort) {
        this.sourcePort = sourcePort;
        this.destinationHost = destinationHost;
        this.destinationPort = destinationPort;
    }

    private void debug(String msg) {
        LOGGER.log(Level.WARNING, "instance[" + super.hashCode() + "] " + msg);
    }

    @Override
    public void run() {
        debug("Creating socket for [:" + sourcePort + "]");
        ServerSocket serverSocket = newServerSocket(sourcePort, MAX_CONNS);
        try {
            process(serverSocket);
        } finally {
            debug("Closing server socket");
            closeSocket(serverSocket);
        }
    }

    private void process(ServerSocket serverSocket) {
        while (!interrupted()) {

            debug("Waiting for connections on [:" + serverSocket.getLocalPort() + "]");
            Socket sourceSocket = accept(serverSocket);
            debug("Got a connection");

            setSoLinger(sourceSocket, LINGER_TIME_MILLIS);
            Socket destSocket = newSocket(destinationHost, destinationPort);
            setSoLinger(destSocket, LINGER_TIME_MILLIS);

            InputStream sourceInput = getInputStreamFrom(sourceSocket);
            OutputStream sourceOutput = getOutputStreamFrom(sourceSocket);

            InputStream destInput = getInputStreamFrom(destSocket);
            OutputStream destOutput = getOutputStreamFrom(destSocket);

            BlockingQueue<Integer> src2dest = new LinkedBlockingQueue<>();
            BlockingQueue<Integer> dest2src = new LinkedBlockingQueue<>();

            runDaemon(new From(destInput, dest2src));
            runDaemon(new From(sourceInput, src2dest));

            runDaemon(new To(sourceOutput, dest2src));
            runDaemon(new To(destOutput, src2dest));
        }
    }
}


