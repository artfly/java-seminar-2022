package com.github.artfly.echo;

import org.slf4j.*;
import org.slf4j.Logger;

import java.io.*;
import java.net.*;
import java.util.*;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

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
    
    static class Client implements Runnable {
        
        private final int id;

        Client(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("localhost", 8080));
//                Thread.sleep((1_000 - id) * 1000L);
                OutputStream os = socket.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                LOG.debug("Client " + id + " sends message...");
                writer.write("Client id: " + id);
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
}
