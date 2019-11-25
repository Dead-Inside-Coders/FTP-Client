package com.formspackage;

import com.datepackage.DateStore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class ClientForm extends Application {

    public static DateStore dateStore = new DateStore();
    public static Stage primaryStage;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("StartConfig.fxml"));
        primaryStage.setTitle("Вход на сервер");
        primaryStage.setScene(new Scene(root));
        runStage(primaryStage);
    }

    public static void runStage(Stage stage) throws IOException
    {
        primaryStage = stage;
        primaryStage.show();
    }
}

