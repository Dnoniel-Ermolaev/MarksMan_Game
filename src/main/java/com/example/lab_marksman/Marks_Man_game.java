package com.example.lab_marksman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import java.util.Optional;

public class Marks_Man_game extends Application {

    private GameClient gameClient;
    private GameController gameController;

    @Override
    public void start(Stage stage) {
        TextInputDialog dialog = new TextInputDialog("Player");
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Please enter your nickname:");
        dialog.setContentText("Nickname:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nickname -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("graphics_game.fxml")); // Replace with your FXML file path
                Scene scene = new Scene(fxmlLoader.load());
                stage.setScene(scene);

                gameController = fxmlLoader.getController();

                // Initializing GameClient with the GameController reference
                gameClient = new GameClient("localhost", 5555, nickname, gameController);
                gameClient.start();

                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
