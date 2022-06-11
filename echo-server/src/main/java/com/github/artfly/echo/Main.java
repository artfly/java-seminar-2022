package com.github.artfly.echo;

import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        nonblocking();
    }

    /*
    1. slow os call (create thread) - new client waits for a long time in accept
    2. context switches between threads - slow client input handling, additional work
     */
//    private static void blocking() throws IOException {
//        try (ServerSocket socket = new ServerSocket()) {
//            socket.bind(new InetSocketAddress("localhost", 8080));
//            LOG.debug("Server started...");
//            while (true) {
//                Socket client = socket.accept();
//                LOG.info("New client! (port {})", client.getPort());
//                Thread thread = new Thread(new ClientHandler(client));
//                thread.start();
//            }
//        }
//    }
    
    private enum Event {
        SERVER,
        CLIENT
    }

    
    /*
    1. client connected (0 ms)
    2. new client connected (100 ms)
    3. first client writes data (200 ms)
    ----------
    slow os call - slow accept
    context switches - slow input handling, extra work
    */
    private static final ByteBuffer CLIENT_BUFFER = ByteBuffer.allocate(1024);
    private static final byte[] BUFFER_BYTES = new byte[1024];
    
    private static void nonblocking() throws IOException {
        try (Selector selector = Selector.open(); ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.bind(new InetSocketAddress("localhost", 8080));
            serverChannel.configureBlocking(false);

            serverChannel.register(selector, SelectionKey.OP_ACCEPT, Event.SERVER);
            
            while (true) {
                selector.select();
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    Event event = (Event) key.attachment();
                    switch (event) {
                        case SERVER -> {
                            if (!key.isAcceptable()) {
                                throw new IllegalStateException("Expected accept event from server, got " + key.readyOps());
                            }
                            SocketChannel clientChannel = serverChannel.accept();
                            clientChannel.configureBlocking(false);
                            clientChannel.register(selector, SelectionKey.OP_READ, Event.CLIENT);
                        }
                        case CLIENT -> {
                            if (!key.isReadable()) {
                                throw new IllegalStateException("Expected read event from client, got " + key.readyOps());
                            }
                            try (SocketChannel clientChannel = (SocketChannel) key.channel()) {
                                int read = clientChannel.read(CLIENT_BUFFER);
                                CLIENT_BUFFER.position(0);
                                CLIENT_BUFFER.get(BUFFER_BYTES, 0, read);
                                CLIENT_BUFFER.position(0);
                                LOG.info(new String(BUFFER_BYTES, 0, read, StandardCharsets.UTF_8));
                            }
                        }
                    }

                    keyIterator.remove();
                }
            }
        }
    }

//    private static void sequential() throws IOException {
//        try (ServerSocket socket = new ServerSocket()) {
//            socket.bind(new InetSocketAddress("localhost", 8080));
//            LOG.info("Server started...");
//            while (true) {
//                try (Socket client = socket.accept()) {
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
//                    System.err.println(reader.readLine());
//                }
//            }
//        }
//    }

    private static class ClientHandler implements Runnable {

        private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);

        private final Socket client;

        private ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                LOG.info(reader.readLine());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
