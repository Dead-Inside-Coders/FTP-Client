package com.application;

import com.apacheftp.FtpClient;
import com.interfaces.FtpService;
import com.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

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
    private FtpService ftpService;
    // Error Alert
    private Alert alert;
    // Information Alert
    private Alert successAlert;

    private InputData inputData;

    public Controller() {
        init();
    }

    private void init()
    {
        inputData = InputData.getInstance();

        ftpService = new FtpClient();

        alert = new Alert(Alert.AlertType.ERROR);
        successAlert = new Alert(Alert.AlertType.INFORMATION);

        listOfItems = FXCollections.observableArrayList();

        try {
            isConnected = ftpService.connect(inputData.getServerdate(), inputData.getUserdate(),
                    inputData.getPasswordate());;
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
             
            listOfItems.addAll(ftpService.listNameOfFiles());
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
            listOfItems.addAll(ftpService.listNameOfFiles(path));
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
            ftpService.downloadSingleFile(selectedItem, Path);
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
            ftpService.uploadSingleFile(file.getPath());
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
        successAlert.setContentText(Logger.getInstance().getLogs().toString());
        successAlert.showAndWait();
    }
}