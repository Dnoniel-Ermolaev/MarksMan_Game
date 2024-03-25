package com.example.lab_marksman;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {
    private final int port;
    private final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private final ExecutorService pool = Executors.newFixedThreadPool(4);

    public GameServer(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Game Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized boolean addClient(String nickname, ClientHandler clientHandler) {
        if (clients.size() >= 4 || clients.containsKey(nickname)) {
            clientHandler.sendMessage("ERROR: Nickname is already taken or max players reached.");
            return false;
        }
        String color = assignColorToPlayer();
        clients.put(nickname, clientHandler);
        clientHandler.sendMessage("COLOR:" + color);
        System.out.println("Player " + nickname + " joined the game with color " + color);
        broadcastNewPlayer(nickname, color);
        return true;
    }

    public synchronized void removeClient(String nickname) {
        clients.remove(nickname);
        System.out.println("Player " + nickname + " left the game.");
    }
    public synchronized void broadcastNewPlayer(String nickname, String color) {
        String message = "NEW_PLAYER:" + nickname + ":" + color;
        broadcastMessage(message);
    }

    private void broadcastMessage(String message) {
        clients.values().forEach(client -> client.sendMessage(message));
    }

    private String assignColorToPlayer() {
        Random rand = new Random();
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        return String.format("#%02x%02x%02x", r, g, b);
    }

    public static void main(String[] args) {
        GameServer server = new GameServer(5555);
        server.start();
    }
}
