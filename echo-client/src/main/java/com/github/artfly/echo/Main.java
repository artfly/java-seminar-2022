package com.github.artfly.echo;

import org.slf4j.*;
import org.slf4j.Logger;

import java.io.*;
import java.net.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 1_000; i++) {
            Thread thread = new Thread(new Client(i));
            thread.start();
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }

    private static class Client implements Runnable {

        private static final Logger LOG = LoggerFactory.getLogger(Client.class);

        private final int id;

        Client(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try (Socket socket = new Socket()) {
                LOG.debug("Client {} connects to server...", id);
                socket.connect(new InetSocketAddress("localhost", 8080));
                Writer writer = new OutputStreamWriter(socket.getOutputStream());
                LOG.debug("Client {} sends message...", id);
                writer.write("Client id: " + id);
                writer.flush();
                LOG.info("Client {} sent message (port {})", id, socket.getLocalPort());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

}
