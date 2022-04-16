package com.github.artfly.echo;

import org.slf4j.*;

import java.io.*;
import java.net.*;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        blocking();
    }

    private static void blocking() throws IOException {
        try (ServerSocket socket = new ServerSocket()) {
            socket.bind(new InetSocketAddress("localhost", 8080));
            LOG.debug("Server started...");
            while (true) {
                Socket client = socket.accept();
                LOG.debug("New client!");
                Thread thread = new Thread(new ClientHandler(client));
                thread.start();
            }
        }
    }

    private static void sequential() throws IOException {
        try (ServerSocket socket = new ServerSocket()) {
            socket.bind(new InetSocketAddress("localhost", 8080));
            LOG.debug("Server started...");
            while (true) {
                try (Socket client = socket.accept()) {
                    LOG.debug("New client!");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    System.err.println(reader.readLine());
                }
            }
        }
    }
    
    private static class ClientHandler implements Runnable {
        
        private final Socket client;

        private ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                LOG.info(reader.readLine());
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
