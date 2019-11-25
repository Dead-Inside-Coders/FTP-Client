package com.ftpclient;

import com.formspackage.ClientForm;
import org.apache.commons.net.ftp.FTPSClient;

import java.io.IOException;

public class Connection {

    public FTPSClient ftpsClient = new FTPSClient();

    private String hostAddress;
    private String login;
    private String password;

    public Connection(String hostAddress, String login, String password) {
        this.hostAddress = hostAddress;
        this.login = login;
        this.password = password;
    }

    public boolean connect() throws IOException {

        ftpsClient.setAutodetectUTF8( true );
        ftpsClient.connect(hostAddress,21);
        ftpsClient.enterLocalPassiveMode();

        if (ftpsClient.login(login, password))
        {
            System.out.println("Соединение установлено!");
            ClientForm.dateStore.addEventToLogs("Соединение установлено!");
            return true;
        }
        else {
            ClientForm.dateStore.addEventToLogs("Неверные данные для входа!");
            throw new IOException("Неверные данные для входа!");
        }

    }

    public void disconnect() throws IOException {
        ftpsClient.logout();
        ftpsClient.disconnect();
        ClientForm.dateStore.addEventToLogs("Соединение разорвано!");
        System.out.println("Соединение разорвано!");
    }



}
