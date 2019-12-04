package com.ownftp;

import com.interfaces.FtpService;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class MyFtpClient implements FtpService
{

    private static MyFtpClient instance;
    private MyFtpClient(){}
    public static MyFtpClient getInstance()
    {
        if(instance != null) return instance;
        else instance = new MyFtpClient();
        return instance;
    }

    private boolean connect;
    private final int PORT = 21;
    private CommandBuilder commandBuilder = CommandBuilder.getInstance();

    public boolean isConnect() { return connect; }

    @Override
    public boolean connect(String hostAddress, String login, String password) throws IOException {

        BufferedReader read = null;

        try {

            Socket socket = new Socket(hostAddress, PORT);

            read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            read.readLine();

            commandBuilder.setInputStream(socket.getInputStream());
            commandBuilder.setOutputStream(socket.getOutputStream());
             if(commandBuilder.connect(login, password))
             {
                 connect = true;
                 return true;
             }
             return false;

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
                TransferTask trf = new TransferTask(
                        commandBuilder.download(fileR),
                        new FileOutputStream(fileL));

                trf.startTransfer();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void uploadSingleFile(String uploadPath,String currentPath) throws IOException
    {
        if(!commandBuilder.isConnected()) return;

        String[] array = uploadPath.split("\\\\");

        String name = array[array.length-1];

        File file = new File(uploadPath);

        try {
            TransferTask trf=new TransferTask(
                    new FileInputStream(file),
                    commandBuilder.upload(currentPath + name)
                    );

            trf.startTransfer();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean deleteFile(String path)
    {
        return commandBuilder.deleteFile(path);
    }

    public boolean rename(String filePath ,String newName)
    {
        return commandBuilder.rename(filePath,newPathBuilder(filePath,newName));
    }

    private String newPathBuilder(String filePath,String newName)
    {
        if(filePath.split("/").length == 1) return newName;
        else
        {
            StringBuilder newPath = new StringBuilder(filePath);
            newPath.delete(filePath.lastIndexOf("/")+1,filePath.length());
            return newPath.append(newName).toString();
        }
    }
}
