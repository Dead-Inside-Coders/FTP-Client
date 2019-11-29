package com.ownftp;

import com.logging.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TransferTask {
    private BufferedInputStream in;
    private BufferedOutputStream out;
    Logger logger = Logger.getInstance();

    public TransferTask(InputStream in, OutputStream out)
    {
        this.in = new BufferedInputStream(in);
        this.out = new BufferedOutputStream(out);
    }

    public void startTransfer()
    {
        byte[] buff = new byte[4096];
        int read;

        try {
            read = this.in.read(buff);

            while (read != -1) {
                logger.addEventToLogs("Передача файла...");
                this.out.write(buff, 0, read);
                read = this.in.read(buff);
            }

            this.in.close();
            this.out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.addEventToLogs("Передача успешно завершена");
    }
}