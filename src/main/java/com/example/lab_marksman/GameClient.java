package com.example.lab_marksman;

import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final String serverAddress;
    private final int serverPort;
    private final String nickname;
    private final GameController gameController;

    public GameClient(String serverAddress, int serverPort, String nickname, GameController gameController) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.nickname = nickname;
        this.gameController = gameController;
    }

    public void start() {
        try {
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Отправляем на сервер никнейм
            sendCommand("NICKNAME:" + nickname);

            // Поток для чтения сообщений от сервера
            new Thread(() -> {
                try {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        if (fromServer.startsWith("COLOR:")) {
                            String color = fromServer.substring(6);
                            Platform.runLater(() -> gameController.updatePlayerColor(color));
                        } else {
                            System.out.println("Server: " + fromServer);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Connection lost.");
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            System.err.println("Could not connect to the server.");
            e.printStackTrace();
        }
    }

    public void sendCommand(String command) {
        out.println(command);
    }

    // ... дополнительные методы, если они нужны ...

    public void stop() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
