package com.ownftp;

import com.interfaces.FtpService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class MyFtpClient implements FtpService {

    private Socket socket;
    private final int PORT = 21;
    CommandBuilder commandBuilder = CommandBuilder.getInstance();

    @Override
    public boolean connect(String hostAddress, String login, String password) throws IOException {
        BufferedReader read = null;
        try {
            Socket socket=new Socket(hostAddress, PORT);

            read =new BufferedReader(new InputStreamReader(socket.getInputStream()));
            read.readLine();

            commandBuilder.setInputStream(socket.getInputStream());
            commandBuilder.setOutputStream(socket.getOutputStream());

            return commandBuilder.connect(login, password);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (read != null) {
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
