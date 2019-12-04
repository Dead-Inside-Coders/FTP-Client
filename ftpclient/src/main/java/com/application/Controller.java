package com.application;

import com.interfaces.FtpService;
import com.logging.Logger;
import com.ownftp.MyFtpClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.Optional;

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
    // Кнопка удаления файлов с сервера
    @FXML
    private Button delButton;
    // Кнопкал для переименования файла на сервере
    @FXML
    private Button renameButton;
    // Папка, куда скачиваются файлы с FTP
    private String savePath;
    //Список файлов, которые добавляются в ListView
    private ObservableList<String> listOfItems;
    // Модель для выбранных файлов
    private MultipleSelectionModel<String> listFilesSelectionModel;
    // Объект класса операций с файлами
    private FtpService ftpService;
    // Error Alert
    private Alert errorAlert;
    // Information Alert
    private Alert successAlert;
    //
    private String currentPath;


    public Controller() {
        init();
    }

    private void init()
    {
        ftpService =  MyFtpClient.getInstance();
        errorAlert = new Alert(Alert.AlertType.ERROR);
        successAlert = new Alert(Alert.AlertType.INFORMATION);
        listOfItems = FXCollections.observableArrayList();

        if(!ftpService.isConnect())
        {
            errorAlert.setTitle("Ошибка");
            errorAlert.setContentText("Подключение не удалось");
            errorAlert.showAndWait();
        }
    }

    private void fillListView() {

        try
        {
            // bug fix
            listOfItems.clear();
            listFiles.setItems(null);
            listOfItems.addAll(ftpService.listNameOfFiles());
            currentPath ="/";
        }
        catch (Exception ex) {
            errorAlert.setTitle("Ошибка");
            errorAlert.setContentText("Не удалось получить список файлов");
            errorAlert.showAndWait();
            return;
        }

        listFiles.setItems(listOfItems);

        if (!listOfItems.isEmpty())
        {
            downloadButton.setVisible(true);
            uploadButton.setVisible(true);
            delButton.setVisible(true);
            renameButton.setVisible(true);
        } 
    }

    private void fillListView(String path) {

        try
        {
            listOfItems.clear();
            listFiles.setItems(null);
            listOfItems.addAll(ftpService.listNameOfFiles(path));
        } catch (Exception ex)
        {
            errorAlert.setTitle("Ошибка");
            errorAlert.setContentText("Не удалось получить список файлов");
            errorAlert.showAndWait();
            return;
        }

        listFiles.setItems(listOfItems);

    }

    private void openSelectedDirectory()
    {
        // получаем модель выбора элементов
        listFilesSelectionModel = listFiles.getSelectionModel();
        // устанавливаем слушатель для отслеживания изменений
//        listFilesSelectionModel.selectedItemProperty()
//              .addListener((changed, oldValue, newValue) -> fillListView(newValue));
    }

    @FXML
    public void showFiles()
    {
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
            errorAlert.setTitle("Ошибка");
            errorAlert.setContentText("Выберите скачиваемый файл");
            errorAlert.showAndWait();
            return;
        }

        String[] array = selectedItem.split("/");
        String Path = savePath + "\\"+array[array.length-1];
        File file = new File(Path);

        if(file.createNewFile()) // if possible create a such file
        {
            ftpService.downloadSingleFile(currentPath+selectedItem, Path);
            successAlert.setTitle("Инфо");
            successAlert.setContentText("Файл успешно скачан"); 
            successAlert.showAndWait();
        }
        else
        {
            errorAlert.setTitle("Ошибка скачивания");
            errorAlert.setContentText("Такой файл уже существет");
            errorAlert.showAndWait();
        }
    }

    @FXML
    public void uploadFileToServer()
    {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(ClientForm.primaryStage);

        try
        {
            ftpService.uploadSingleFile(file.getPath(),currentPath);
            successAlert.setTitle("Инфо");
            successAlert.setContentText("Файл успешно загружен");
            successAlert.showAndWait();
            fillListView(currentPath);
        }
        catch (IOException | NullPointerException ex)
        {
            errorAlert.setTitle("Ошибка");
            errorAlert.setContentText("Не удалось загрузить файл на сервер");
            errorAlert.showAndWait();
        }
    }

    private void chooseSavePath()
    {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      directoryChooser.setTitle("Выберите директорию для сохранения файла");
      savePath = directoryChooser.showDialog(ClientForm.primaryStage).getPath();
    }

    @FXML
    public void getLogs() throws IOException
    {
        Stage logeStag = new Stage();
        logeStag.setScene(new Scene(FXMLLoader.load(getClass().getResource("LogsForm.fxml"))));
        logeStag.setTitle("Логи");
        logeStag.show();
    }

    public void disconnecting(ActionEvent actionEvent) throws IOException
    {
        ftpService.disconnect();
        Logger.getInstance().clearLogs();
        Parent root = FXMLLoader.load(getClass().getResource("StartConfig.fxml"));
        ClientForm.primaryStage.close();
        ClientForm.primaryStage.setScene(new Scene(root));
        ClientForm.primaryStage.setTitle("Вход на сервер");
        ClientForm.primaryStage.show();
    }

    public void deleteFile(ActionEvent actionEvent)
    {
        String selectedItem = listFilesSelectionModel.getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Подтверждение");
        alert.setContentText("Вы уверены, что хотите удалить файл: " + selectedItem);
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK)
        {
            if(ftpService.deleteFile(currentPath+selectedItem))
            {
                successAlert.setContentText("Файл успешено удален");
                successAlert.showAndWait();
                fillListView(currentPath);
            }
            else
            {
                errorAlert.setContentText("При удалении файла произошла ошибка");
                errorAlert.showAndWait();
            }
        }
    }

    public void renameFile(ActionEvent actionEvent)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rename");
        dialog.setHeaderText("Enter new name:");
        dialog.setContentText("New name:");

        Optional<String> result = dialog.showAndWait();
        String selectedItem = listFilesSelectionModel.getSelectedItem();
        int endIndex = selectedItem.contains(".") ? selectedItem.lastIndexOf(".") : selectedItem.length();

        if(result.isPresent())
        {
            if(ftpService.rename(currentPath+selectedItem,result.get()+new StringBuilder(selectedItem).delete(0,endIndex).toString()))
            {
                fillListView(currentPath);
                successAlert.setContentText("Файл успешено переименован");
                successAlert.showAndWait();
            }
            else
            {
                errorAlert.setContentText("При переименовании файла произошла ошибка");
                errorAlert.showAndWait();
            }
        }
    }

    public void chooseItem(MouseEvent mouseEvent) throws IOException
    {
        if(mouseEvent.getButton() == MouseButton.PRIMARY)
        {
                String item = listFilesSelectionModel.getSelectedItem();

                System.out.println("Мы выбрали айтем " + item);
            if (mouseEvent.getClickCount()==2)
            {
                if (item!= null)//isDirectory костыль fucking
                {
                    currentPath +=item+"/";
                    fillListView(currentPath);
                    System.out.println("Мы зашли в деректорю файла " + item);
                }
            }
        }
    }

    private boolean isDirectory(String item) throws IOException
    {
        return !ftpService.listNameOfFiles(item).isEmpty();//Не работает,вернее работатет но не так как надо
    }

}
