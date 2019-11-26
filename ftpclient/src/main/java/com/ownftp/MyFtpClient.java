package com.ownftp;

import com.interfaces.FtpService;

import java.io.IOException;
import java.util.ArrayList;

public class MyFtpClient implements FtpService {

    @Override
    public boolean connect(String hostAddress, String login, String password) throws IOException {
        return false;
    }

    @Override
    public void disconnect() throws IOException {

    }

    @Override
    public ArrayList<String> listNameOfFiles() throws IOException {
        return null;
    }

    @Override
    public ArrayList<String> listNameOfFiles(String path) throws IOException {
        return null;
    }

    @Override
    public void downloadSingleFile(String remoteFilePath, String savePath) {

    }

    @Override
    public void uploadSingleFile(String uploadPath) throws IOException {

    }
}
