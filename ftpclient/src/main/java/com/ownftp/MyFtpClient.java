package com.ownftp;

import com.interfaces.FtpService;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyFtpClient implements FtpService {

    private Socket sock;
    private final int PORT = 21;
    CommandBuilder commandBuilder = CommandBuilder.getInstance();

    @Override
    public boolean connect(String hostAddress, String login, String password) throws IOException {

        BufferedReader read = null;

        try {

            Socket socket = new Socket(hostAddress, PORT);


            read =new BufferedReader(new InputStreamReader(socket.getInputStream()));
            read.readLine();

            commandBuilder.setInputStream(socket.getInputStream());
            commandBuilder.setOutputStream(socket.getOutputStream());
            return commandBuilder.connect(login, password);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (read != null)
            {
                try {
                 //   read.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void disconnect() throws IOException {
        commandBuilder.disconnect();
    }

    @Override
    public List<String> listNameOfFiles() throws IOException
    {
        return commandBuilder.isConnected() ? commandBuilder.getFiles("/") : null;
    }

    @Override
    public List<String> listNameOfFiles(String path) throws IOException {
        return commandBuilder.isConnected() ? commandBuilder.getFiles(path) : null;
    }

    @Override
    public void downloadSingleFile(String remoteFilePath, String savePath) {
            if(!commandBuilder.isConnected()) return;

            String fileR = remoteFilePath;
            if(fileR==null) return;
            File fileL=new File(savePath);

            try {
                TransferTask trf=new TransferTask(
                        commandBuilder.download(fileR),
                        new FileOutputStream(fileL));

                trf.startTransfer();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void uploadSingleFile(String uploadPath) throws IOException {

    }
}
