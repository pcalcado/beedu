package beedu.chaosmico;


import beedu.chaosmico.sockets.ProxyRunnerThread;

import java.util.logging.Level;
import java.util.logging.Logger;

import static beedu.chaosmico.sockets.SocketFunctions.runDaemon;

public class ChaosMicoProxy {
    private final static Logger LOGGER = Logger.getLogger(ChaosMicoProxy.class.getName());

    private final String remoteHost;
    private final int remotePort;
    private final String localHost;
    private final int localPort;
    private ProxyRunnerThread runnerThread = null;

    public ChaosMicoProxy(String remoteHost, int remotePort, String localHost, int localPort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.localHost = localHost;
        this.localPort = localPort;
        this.runnerThread = new ProxyRunnerThread(localPort, remoteHost, remotePort);
    }

    public void stop() {
        LOGGER.log(Level.WARNING, "Stopping proxy [:" + localPort + "]->[" + remoteHost + ":" + remotePort + "]");
        runnerThread.interrupt();
    }

    public void run() {
        LOGGER.log(Level.WARNING, "Starting proxy [:" + localPort + "]->[" + remoteHost + ":" + remotePort + "]");
        runDaemon(runnerThread);
    }

}

