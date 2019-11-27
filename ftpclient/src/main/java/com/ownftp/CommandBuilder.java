package com.ownftp;

import com.logging.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CommandBuilder
{
    private static CommandBuilder instance;
    private CommandBuilder(){}
    public static CommandBuilder getInstance()
    {
        if(instance == null)
        {
            instance = new CommandBuilder();
        }
        return instance;
    }

    private boolean connected;
    private BufferedReader in;
    private BufferedWriter out;
    private Logger logger = Logger.getInstance();

    public boolean isConnected() { return connected; }
    public void disconnect(){ this.connected=false; }

    public boolean connect(String id, String passwd){
        try {
            if(!command("USER "+id).startsWith("331 ")) return false;
            if(!command("PASS "+passwd).startsWith("230 ")) return false;

            if(isConnected()){
                this.connected=false;
                logger.addEventToLogs("Disconnected");
            }

            this.connected=true;
            logger.addEventToLogs("Connected");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private String command(String cmd) throws IOException
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


    public void setInputStream(InputStream in){
        try {
            if(this.in!=null) this.in.close();
            this.in=new BufferedReader(new InputStreamReader(in, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.err.println("WTF?");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOutputStream(OutputStream out){
        try {
            if(this.out!=null) this.out.close();
            this.out=new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.err.println("WTF?");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
 