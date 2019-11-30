package com.apacheftp;

import com.interfaces.FtpService;
import com.logging.Logger;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FtpClient implements FtpService {

    private FTPSClient ftpsClient;
    private Logger logger = Logger.getInstance();

    private boolean connect;

    public FtpClient() {
        ftpsClient = new FTPSClient();
    }

    @Override
    public boolean isConnect()
    {
        return connect;
    }

    public boolean connect(String hostAddress, String login, String password) throws IOException {

        ftpsClient.setAutodetectUTF8( true );
        ftpsClient.connect(hostAddress,21);
        ftpsClient.enterLocalPassiveMode();

        if (ftpsClient.login(login, password))
        {
            System.out.println("Соединение установлено!");
            logger.addEventToLogs("Соединение установлено!");
            connect = true;
            return true;
        }
        else {
            logger.addEventToLogs("Неверные данные для входа!");
            throw new IOException("Неверные данные для входа!");
        }
    }

    public void disconnect() throws IOException {
        ftpsClient.logout();
        ftpsClient.disconnect();
        logger.addEventToLogs("Соединение разорвано!");
        System.out.println("Соединение разорвано!");
    }

    public List<String> listNameOfFiles() throws IOException  {

        logger.addEventToLogs("Получен список файлов");
        return new ArrayList<>(Arrays.asList(ftpsClient.listNames()));
    }

    public List<String> listNameOfFiles(String path) throws IOException  {
        logger.addEventToLogs("Получен список файлов");
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

            logger.addEventToLogs("Файл: "+remoteFilePath + " Успешно скачан в папку: "+savePath);
        }
        catch (IOException ex)
        {
            logger.addEventToLogs("Ошибка при скачивании файла: "+remoteFilePath);
            logger.addEventToLogs(ex.getMessage());
            ex.printStackTrace(); }

    }

    public void uploadSingleFile(String uploadPath) throws IOException {

        // create input stream from file and use it in a storeFile method

        String[] array = uploadPath.split("\\\\");

        File firstLocalFile = new File(uploadPath);

        String firstRemoteFile = array[array.length-1];

        InputStream inputStream = new FileInputStream(firstLocalFile);

        logger.addEventToLogs("Файл начал скачиваться");

        if (ftpsClient.storeFile(firstRemoteFile, inputStream))
            logger.addEventToLogs("Файл "+firstRemoteFile+" успешно скачан");

        inputStream.close();
    }

    @Override
    public boolean deleteFile(String path) throws IOException {
        return false;
    }
}
