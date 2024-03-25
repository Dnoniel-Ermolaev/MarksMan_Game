package com.example.lab_marksman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private GameServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String nickname;

    public ClientHandler(Socket socket, GameServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String initialMessage = in.readLine();
            if (initialMessage.startsWith("NICKNAME:")) {
                nickname = initialMessage.substring(9);
                if (!server.addClient(nickname, this)) {
                    out.println("ERROR: Nickname is taken or max players reached.");
                    return;
                }
            }

            String input;
            while ((input = in.readLine()) != null) {
                System.out.println(nickname + ": " + input);
                // ... Обработка сообщений от клиента ...
            }
        } catch (IOException e) {
            System.err.println("Client handler exception for " + nickname + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            server.removeClient(nickname);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
