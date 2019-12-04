package com.interfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface FtpService
{
    boolean isConnect();
    boolean connect(String hostAddress, String login, String password) throws IOException;
    void disconnect() throws IOException;
    List<String> listNameOfFiles() throws IOException;
    List<String> listNameOfFiles(String path) throws IOException;
    void downloadSingleFile(String remoteFilePath, String savePath);
    void uploadSingleFile(String uploadPath,String currentPath) throws IOException;
    boolean deleteFile(String path);
    boolean rename(String filePath ,String newName);
}
