package beedu.chaosmico.sockets;

import beedu.chaosmico.ChaosMicoException;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SocketFunctions {
    private final static Logger LOGGER = Logger.getLogger(SocketFunctions.class.getName());


    private static ChaosMicoException newChaosMicoException(String msg, Exception e) {
        ChaosMicoException exception = new ChaosMicoException(msg, e);
        LOGGER.log(Level.SEVERE, msg, e);
        return exception;
    }

    public static void runDaemon(Thread t) {
        t.setDaemon(true);
        t.start();
    }

    public static InputStream getInputStreamFrom(Socket socket) {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            throw newChaosMicoException("Error getting InputStream", e);
        }
    }

    public static OutputStream getOutputStreamFrom(Socket socket) {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            throw newChaosMicoException("Error getting OutputStream", e);
        }
    }

    public static void setSoLinger(Socket socket, int lingerTime) {
        try {
            socket.setSoLinger(true, lingerTime);
        } catch (SocketException e) {
            throw newChaosMicoException("Error setting SocketOptions#SO_LINGER ", e);
        }
    }

    public static Socket newSocket(String host, int port) {
        try {
            return new Socket(host, port);
        } catch (IOException e) {
            throw newChaosMicoException("Error opening socket to [" + host + ":" + port + "]", e);
        }
    }

    public static ServerSocket newServerSocket(int localPort, int backlog) {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(localPort), backlog);
            return serverSocket;
        } catch (IOException e) {
            throw newChaosMicoException("Error opening server socket", e);
        }
    }

    public static Socket accept(ServerSocket serverSocket) {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            throw newChaosMicoException("Error accepting connections", e);
        }
    }

    public static void closeSocket(Closeable socket) {
        if (socket == null) throw new ChaosMicoException("Trying to close a null socket");
        try {
            socket.close();
        } catch (IOException e) {
            throw new ChaosMicoException("Error closing socket", e);
        }
    }
}
