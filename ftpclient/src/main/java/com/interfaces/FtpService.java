package com.interfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface FtpService {
    public boolean connect(String hostAddress, String login, String password) throws IOException;
    public void disconnect() throws IOException;
    public List<String> listNameOfFiles() throws IOException;
    public List<String> listNameOfFiles(String path) throws IOException;
    public void downloadSingleFile(String remoteFilePath, String savePath);
    public void uploadSingleFile(String uploadPath) throws IOException;
}
