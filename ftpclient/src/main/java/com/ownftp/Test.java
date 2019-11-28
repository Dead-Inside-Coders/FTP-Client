package com.ownftp;

import com.interfaces.FtpService;
import com.logging.Logger;

import java.io.IOException;

public class Test {

   private static FtpService ftpService = new MyFtpClient();

    public static void main(String[] args) {
        try {
            System.out.println(ftpService.connect("localhost","ftp","ftp"));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            for (String file: ftpService.listNameOfFiles()) {
                System.out.println(file);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        for (String item:Logger.getInstance().getLogs()) {
            System.out.println(item);
        }

    }
}
