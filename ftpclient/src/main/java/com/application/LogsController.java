package com.application;

import com.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.IOException;


public class LogsController
{
    @FXML
    private ListView<String> logsList;

    @FXML
    private Button clearLogs;


    private Logger logger = Logger.getInstance();
    public void initialize()
    {
        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(logger.getLogs());
        logsList.setItems(observableList);
    }

    @FXML
    void toClear(ActionEvent event)
    {
        logger.clearLogs();
        logsList.setItems(null);
    }
}
