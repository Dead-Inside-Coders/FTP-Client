package com.ownftp;

import com.logging.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FTPLogic
{
    private static FTPLogic instance;
    private FTPLogic(){}
    public static FTPLogic getInstance()
    {
        if(instance == null)
        {
            instance = new FTPLogic();
        }
        return instance;
    }

    private boolean connected;
    private BufferedReader in;
    private BufferedWriter out;
    private Logger logger = Logger.getInstance();


    public boolean isConnected() { return connected; }
    public void disconnect(){ this.connected=false; }


    protected synchronized String command(String cmd) throws IOException
    {
        while(this.in.ready()) logger.addEventToLogs("recv: "+this.in.readLine()); //secure clearing
        this.out.write(new String(cmd.getBytes(), StandardCharsets.UTF_8)+"\r\n");
        this.out.flush();
        logger.addEventToLogs("send: "+cmd);

        String str;
        try
        {
            str=this.in.readLine();
            if((str==null && isConnected()) || (str != null && str.startsWith("530") && isConnected()))
            {
                this.connected=false;
                logger.addEventToLogs("Disconnected");
            }

            logger.addEventToLogs("recv: "+str);

        }
        catch (IOException e)
        {
            e.printStackTrace();
            if(isConnected())
            {
                this.connected=false;
                logger.addEventToLogs("Disconnected");
            }
            throw e;
        }
        // while(this.in.ready()) logger.addEventToLogs("recv: "+this.in.readLine()); //secure clearing
        return str==null ? "" : str;
    }
}
 