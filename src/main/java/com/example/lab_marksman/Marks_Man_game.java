package com.example.lab_marksman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Marks_Man_game extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Marks_Man_game.class.getResource("graphics_game.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 630);
        stage.setTitle("Marks_man_Game_Ermolaev_382003_3");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}