package com.ftpclient;

import com.formspackage.ClientForm;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Actions {

    private FTPSClient ftpsClient;

    public Actions(FTPSClient ftpsClient) {
        this.ftpsClient = ftpsClient;
    }

    public ArrayList<String> listNameOfFiles() throws IOException  {

        ClientForm.dateStore.addEventToLogs("Получен список файлов");
        return new ArrayList<>(Arrays.asList(ftpsClient.listNames()));
    }

    public ArrayList<String> listNameOfFiles(String path) throws IOException  {
        ClientForm.dateStore.addEventToLogs("Получен список файлов");
        return new ArrayList<>(Arrays.asList(ftpsClient.listNames(path)));
    }

    public void downloadSingleFile(String remoteFilePath, String savePath) {

        try {
             //write output stream from input stream
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(savePath)));
            ftpsClient.setFileType(FTP.BINARY_FILE_TYPE);
            InputStream inputStream = ftpsClient.retrieveFileStream(remoteFilePath);

            byte[] bytesArray = new byte[4096];

            int bytesRead = -1;

            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                outputStream.write(bytesArray, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            boolean success = ftpsClient.completePendingCommand();
            if (!success) throw new IOException(); 

            ClientForm.dateStore.addEventToLogs("Файл: "+remoteFilePath + " Успешно скачан в папку: "+savePath);
        }
        catch (IOException ex)
        {
            ClientForm.dateStore.addEventToLogs("Ошибка при скачивании файла: "+remoteFilePath);
            ClientForm.dateStore.addEventToLogs(ex.getMessage());
            ex.printStackTrace(); }

    }

    public void uploadSingleFile(String uploadPath) throws IOException {

        // create input stream from file and use it in a storeFile method

        String[] array = uploadPath.split("\\\\");

        File firstLocalFile = new File(uploadPath);

        String firstRemoteFile = array[array.length-1];

        InputStream inputStream = new FileInputStream(firstLocalFile);

        ClientForm.dateStore.addEventToLogs("Файл начал скачиваться");

        if (ftpsClient.storeFile(firstRemoteFile, inputStream))
            ClientForm.dateStore.addEventToLogs("Файл "+firstRemoteFile+" успешно скачан");

        inputStream.close();
    }


}
