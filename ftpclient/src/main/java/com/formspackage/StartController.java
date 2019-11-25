package com.formspackage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.ftpclient.Connection;

public class StartController
{
    @FXML
    private TextField server;
    @FXML
    private TextField user;
    @FXML
    private PasswordField password;
    @FXML
    private Button connectionbutton;


    @FXML
    private void connect(ActionEvent event)throws Exception
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        String serverText = server.getText();
        String userText = user.getText();
        String passwordText = password.getText();
        if(!serverText.equals("") && !userText.equals("") && !passwordText.equals(""))
        {
            if(new Connection(serverText,userText,passwordText).connect())
            {

                ClientForm.dateStore.setServerdate(serverText);
                ClientForm.dateStore.setUserdate(userText);
                ClientForm.dateStore.setPasswordate(passwordText);
                Parent root = FXMLLoader.load(getClass().getResource("StartConfig.fxml"));
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
