package com.application;

import com.apacheftp.FtpClient;
import com.ownftp.MyFtpClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class StartController
{
    @FXML
    private TextField server;
    @FXML
    private TextField user;
    @FXML
    private PasswordField password;

    private InputData inputData;

    @FXML
    private void connect(ActionEvent event) throws Exception
    {
        inputData = InputData.getInstance();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        String serverText = server.getText();
        String userText = user.getText();
        String passwordText = password.getText();

        if(!serverText.equals("") && !userText.equals("") && !passwordText.equals(""))
        {
            if(new MyFtpClient().connect(serverText,userText,passwordText))
            {
                inputData.setServerdate(serverText);
                inputData.setUserdate(userText);
                inputData.setPasswordate(passwordText);
                Parent root = FXMLLoader.load(getClass().getResource("Config.fxml"));
                ClientForm.primaryStage.close();
                ClientForm.primaryStage.setScene(new Scene(root));
                ClientForm.primaryStage.setTitle("Работа с FTP");
                ClientForm.primaryStage.show();
            }
            else
            {
                alert.setTitle("Ошибка подключения");
                alert.setContentText("Проверьте правильность ввода данных");
                alert.showAndWait();
            }
        }
        else
        {
            alert.setTitle("Ошибка");
            alert.setContentText("Вы должны заполнить все поля");
            alert.showAndWait();
        }


    }

}
