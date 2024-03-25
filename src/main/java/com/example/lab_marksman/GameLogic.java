package com.example.lab_marksman;

import javafx.application.Platform;
import javafx.scene.shape.Circle;

public class GameLogic implements Runnable {
    private int points;
    private int shoots;
    private final GameController controller;
    private volatile boolean running = true;
    public boolean movingDownTarget1 = true;
    public boolean movingDownTarget2 = true;
    private volatile boolean arrowInFlight = false;
    public GameLogic(GameController controller) {
        this.controller = controller;
    }
    // Метод для проверки, работает ли поток
    public boolean isRunning() {
        return running;
    }
    public void shootArrow(double startX, double startY) {
        if (!arrowInFlight) {
            arrowInFlight = true;
            shoots++;
            Platform.runLater(() -> {
                controller.arrow.setVisible(true);
                controller.arrow.setStartX(startX);
                controller.arrow.setStartY(startY + 14);
                controller.arrow.setEndX(startX + 20);
                controller.arrow.setEndY(startY + 14);
                controller.updateShoots(shoots);
            });

        }
    }
    private void checkCollision(double arrowX, double arrowY) {
        Platform.runLater(() -> {
            if (targetHit(arrowX, arrowY, controller.target_1)) {
                points +=1;
                arrowInFlight = false;
            } else if (targetHit(arrowX, arrowY, controller.target_2)) {
                points +=2;
                arrowInFlight = false;
            }
        });
        Platform.runLater(() -> {
            controller.updatePoints(points);
            if (!arrowInFlight) {
                controller.arrow.setVisible(false);
            }
        });
    }
    private boolean targetHit(double arrowX, double arrowY, Circle target) {
        double distance = Math.sqrt(Math.pow(target.getLayoutX() - arrowX, 2) + Math.pow(target.getLayoutY() - arrowY, 2));
        return distance <= target.getRadius(); // Простая проверка на пересечение
    }
    public void setRunning(boolean running) {
        this.running = running;
    }
    private void updateArrowPosition() {
        double currentEndX = controller.arrow.getEndX() + 15; // Скорость стрелы
        if (currentEndX > controller.Panel_game.getWidth()) {
            arrowInFlight = false;
            Platform.runLater(() -> controller.arrow.setVisible(false));
        } else {
            // Здесь нет необходимости повторно проверять попадание, это делается в run()
            Platform.runLater(() -> {
                controller.arrow.setEndX(currentEndX);
                controller.arrow.setStartX(controller.arrow.getStartX() + 15);
            });
        }
    }
    @Override
    public void run() {
        System.out.println("Thread name: " + Thread.currentThread().getName() + ", State: " + Thread.currentThread().getState());

        while (true) {
            if (running){
                try {
                    if (points > 6) {
                        Platform.runLater(() -> {
                            controller.updateGameStatus("Вы выиграли!");
                        });
                        break; // Выходим из цикла
                    }
                 else {
                    if (arrowInFlight) {
                        updateArrowPosition();
                        checkCollision(controller.arrow.getStartX(), controller.arrow.getStartY());
                    }

                    Platform.runLater(() -> {
                        controller.moveTarget_1(controller.target_1);
                        controller.moveTarget_2(controller.target_2);
                    });

                    Thread.sleep(20);
                }
            }
                catch (InterruptedException e) {
                        running = false;
                }
            }
            else{
                // Даем немного времени на паузу, чтобы избежать чрезмерной загрузки процессора
                try {
                    Thread.sleep(100); // Значение времени может быть скорректировано
                } catch (InterruptedException e) {
                    // Обработка прерывания потока, если требуется
                    System.out.println("GameLogic thread was interrupted");
                    break; // Выход из внешнего цикла и завершение потока
                }
            }
        }
    }
    public void pauseGame() {
        running = false;
        // Не сбрасываем состояния, чтобы позволить возобновление
    }

    public void resumeGame() {
        // Метод resumeGame должен корректно сигнализировать о возобновлении игрового процесса
        // Это может включать в себя установку флагов или инициализацию состояний
        setRunning(true);
        // Возможно, потребуется установить дополнительные условия для возобновления игры в методе run()
    }


}
