package com.example.lab_marksman;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.input.KeyEvent;

public class GameController {
    @FXML public Circle target_1, target_2;
    @FXML public Button Start_button, Stop_button, Shoot_button;
    @FXML public Label points, shoots;
    @FXML public Polygon shooter;
    @FXML public Pane Panel_game;

    public GameLogic gameLogic;
    public Thread gameThread;
    public Line arrow;
    @FXML
    public Label gameStatus; // Убедитесь, что этот элемент добавлен в ваш FXML файл
    public Button Ready;
    private GameClient gameClient;
    public void updatePlayerColor(String color) {
        Platform.runLater(() -> {
            // Измените свойство 'fill' стрелка на новый цвет
            shooter.setFill(Color.web(color));
        });
    }
    public void initClient(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    public void updateGameStatus(String status) {
        Platform.runLater(() -> gameStatus.setText(status));
    }

    public void initialize() {
        arrow = new Line();
        arrow.setStrokeWidth(3);
        Panel_game.getChildren().add(arrow);
        arrow.setVisible(false);
        // Это гарантирует, что Panel_game может получать фокус для обработки событий клавиатуры
        Panel_game.setFocusTraversable(true);
    }

    @FXML
    public void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case W:
                moveShooterUp();
                break;
            case S:
                moveShooterDown();
                break;
            case SPACE:
                shoot();
                break;
        }
        event.consume(); // Предотвращаем распространение события дальше
    }

    private void moveShooterUp() {
        if (gameLogic.isRunning()) {
            // Логика для перемещения стрелка вверх
            double newY = shooter.getLayoutY() - 15; // Примерное значение для сдвига
            if (newY >= 0) shooter.setLayoutY(newY);
        }
    }

    private void moveShooterDown() {
        if (gameLogic.isRunning()) {
        // Логика для перемещения стрелка вниз
        double newY = shooter.getLayoutY() + 15; // Примерное значение для сдвига
        if (newY + shooter.getLayoutBounds().getHeight() <= Panel_game.getHeight()) shooter.setLayoutY(newY);
    }
    }

    public void shoot() {
        System.out.println("Thread name: " + Thread.currentThread().getName() + ", State: " + Thread.currentThread().getState());

        if (gameLogic != null) {
            if (gameLogic.isRunning()) {
                gameLogic.shootArrow(shooter.getLayoutBounds().getMinX() + shooter.getLayoutX(),
                        shooter.getLayoutBounds().getMinY() + shooter.getLayoutY());
            }
        }
        shooter.requestFocus();
    }
    @FXML
    private void handleReadyButtonAction() {

    }
    @FXML
    private void handleStartButtonAction() {
        updateGameStatus("ИГРА ЗАПУЩЕНА");
        System.out.println("UI Thread (Start Button) name: " + Thread.currentThread().getName() + ", State: " + Thread.currentThread().getState());
        if (gameLogic == null) {
            gameLogic = new GameLogic(this);
        }
        // Проверяем, запущен ли уже поток игры и "живой" ли он
        if (gameThread == null || !gameThread.isAlive()) {
            gameLogic.setRunning(true); // Убедитесь, что есть метод setRunning в GameLogic
            gameThread = new Thread(gameLogic);
            gameThread.setDaemon(true);
            gameThread.start();
        } else {
            // Если поток уже запущен и жив, вызываем метод для возобновления игры внутри потока
            gameLogic.resumeGame(); // Этот метод должен правильно управлять возобновлением игры
        }
        shooter.requestFocus();
    }

    public void moveTarget_1(Circle target) {
        double newY;
        if (gameLogic.movingDownTarget1) {
            newY = target.getLayoutY() + 2; // Движение вниз
            if (newY > Panel_game.getHeight() - target.getRadius()) {
                gameLogic.movingDownTarget1 = false; // Изменить направление на движение вверх
            }
        } else {
            newY = target.getLayoutY() - 2; // Движение вверх
            if (newY < 0 + target.getRadius()) {
                gameLogic.movingDownTarget1 = true; // Изменить направление на движение вниз
            }
        }
        target.setLayoutY(newY);
    }

    public void moveTarget_2(Circle target) {
        double newY;
        if (gameLogic.movingDownTarget2) {
            newY = target.getLayoutY() + 6; // Движение вниз быстрее
            if (newY > Panel_game.getHeight() - target.getRadius()) {
                gameLogic.movingDownTarget2 = false; // Изменить направление на движение вверх
            }
        } else {
            newY = target.getLayoutY() - 6; // Движение вверх быстрее
            if (newY < 0 + target.getRadius()) {
                gameLogic.movingDownTarget2 = true; // Изменить направление на движение вниз
            }
        }
        target.setLayoutY(newY);
    }

    @FXML
    private void handleStopButtonAction() {
        if (gameLogic != null) {
            gameLogic.pauseGame(); // Изменено на паузу игры
            updateGameStatus("ИГРА НА ПАУЗЕ");
        }
    }

    @FXML
    private void handleShootButtonAction() {

        if (gameLogic != null) {
            if (gameLogic.isRunning()) {
            gameLogic.shootArrow(shooter.getLayoutBounds().getMinX() + shooter.getLayoutX(),
                    shooter.getLayoutBounds().getMinY() + shooter.getLayoutY());
        }
        }
        shooter.requestFocus();
    }

    public void updatePoints(int newPoints) {
        Platform.runLater(() -> points.setText(String.valueOf(newPoints)));
    }

    public void updateShoots(int newShoots) {
        Platform.runLater(() -> shoots.setText(String.valueOf(newShoots)));
    }
}
