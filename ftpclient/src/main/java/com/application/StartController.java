package com.application;


import com.interfaces.FtpService;
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


    private FtpService ftpClient = MyFtpClient.getInstance();
    @FXML
    private void connect(ActionEvent event) throws Exception
    {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        String serverText = server.getText();
        String userText = user.getText();
        String passwordText = password.getText();

        if(!serverText.isEmpty() && !userText.isEmpty() && !passwordText.isEmpty())
        {
            if(ftpClient.connect(serverText,userText,passwordText))
            {
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
