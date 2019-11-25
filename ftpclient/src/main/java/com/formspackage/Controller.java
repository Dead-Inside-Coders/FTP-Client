package com.formspackage;

import com.ftpclient.Actions;
import com.ftpclient.Connection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    // ListView для вывода файлов
    @FXML
    private ListView<String> listFiles = new ListView<>();
    // Кнопка скачивания файлов на сервер
    @FXML
    private Button downloadButton;
    // Кнопка загрузки файлов на сервер
    @FXML
    private Button uploadButton;
    // Переменная состояния подключения
    private boolean isConnected;
    // Папка, куда скачиваются файлы с FTP
    private String savePath;
    //Список файлов, которые добавляются в ListView
    private ObservableList<String> listOfItems;
    // Модель для выбранных файлов
    private MultipleSelectionModel<String> listFilesSelectionModel;
    // Объект класса операций с файлами
    private Actions actions;
    // Error Alert
    private Alert alert;
    // Information Alert
    private Alert successAlert;

    public Controller() {
        init();
    }

    private void init()
    {
        // Объект подключения
        Connection connection = new Connection(ClientForm.dateStore.getServerdate(), ClientForm.dateStore.getUserdate(),
                ClientForm.dateStore.getPasswordate());

        alert = new Alert(Alert.AlertType.ERROR);
        successAlert = new Alert(Alert.AlertType.INFORMATION);

        listOfItems = FXCollections.observableArrayList();

        actions = new Actions(connection.ftpsClient);

        try {
            isConnected = connection.connect();
        }
        catch (IOException ex) {
            alert.setTitle("Ошибка");
            alert.setContentText("Подключение не удалось");
            alert.showAndWait();
        }
    }

    private void fillListView() {

        try {
            // bug fix
            listOfItems.clear();
            listOfItems.clear();
             
            listOfItems.addAll(actions.listNameOfFiles());
        } catch (Exception ex) {
            alert.setTitle("Ошибка");
            alert.setContentText("Не удалось получить список файлов");
            alert.showAndWait();
            return;
        }

        listFiles.setItems(listOfItems);

        if (!listOfItems.isEmpty()) {
            downloadButton.setVisible(true);
            uploadButton.setVisible(true);
        } 
    }

    private void fillListView(String path) {

        try {
            listOfItems.clear();
            listOfItems.addAll(actions.listNameOfFiles(path));
        } catch (Exception ex) {
            alert.setTitle("Ошибка");
            alert.setContentText("Не удалось получить список файлов");
            alert.showAndWait();
            return;
        }

        listFiles.setItems(listOfItems);

    }

    private void openSelectedDirectory() {
        // получаем модель выбора элементов
        listFilesSelectionModel = listFiles.getSelectionModel();
        // устанавливаем слушатель для отслеживания изменений
        listFilesSelectionModel.selectedItemProperty()
                .addListener((changed, oldValue, newValue) -> fillListView(newValue));
    }

    @FXML
    public void showFiles() {
        fillListView();
        openSelectedDirectory();
    }

    @FXML
    public void downloadSelectedFile() throws IOException {

        String selectedItem; 

        try {
            selectedItem = listFilesSelectionModel.getSelectedItem();
            chooseSavePath();
        }
        catch (NullPointerException ex) {
            alert.setTitle("Ошибка");
            alert.setContentText("Выберите скачиваемый файл");
            alert.showAndWait();
            return;
        }

        String[] array = selectedItem.split("/");
        String Path = savePath + "\\"+array[array.length-1];
        File file = new File(Path);

        if(file.createNewFile()) // if possible create a such file
        {
            actions.downloadSingleFile(selectedItem, Path);
            successAlert.setTitle("Инфо");
            successAlert.setContentText("Файл успешно скачан"); 
            successAlert.showAndWait();
        }
        else
        {
            alert.setTitle("Ошибка скачивания");
            alert.setContentText("Такой файл уже существет");
            alert.showAndWait();
        }
    }

    @FXML
    public void uploadFileToServer()
    {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(ClientForm.primaryStage);

        try
        {
            actions.uploadSingleFile(file.getPath());
            successAlert.setTitle("Инфо");
            successAlert.setContentText("Файл успешно загружен");
            successAlert.showAndWait();
        }
        catch (IOException | NullPointerException ex)
        {
            alert.setTitle("Ошибка");
            alert.setContentText("Не удалось загрузить файл на сервер");
            alert.showAndWait();
        }
    }

    private void chooseSavePath()
    {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      directoryChooser.setTitle("Выберите директорию для сохранения файла");
      savePath = directoryChooser.showDialog(ClientForm.primaryStage).getPath();
    }

    @FXML
    public void getLogs()
    {
        successAlert.setTitle("Логи");
        successAlert.setContentText(ClientForm.dateStore.getLogs().toString());
        successAlert.showAndWait();
    }
}
