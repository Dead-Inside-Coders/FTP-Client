package com.ownftp;

import com.logging.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
    private Type type;

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

    public List<String> getFiles(String path){
        List<String> list=new ArrayList<>();

        if(this.type!=Type.A) setMode(Type.A);
        Socket sock=PASV();
        if(sock==null) return list;

        try {
            BufferedReader read = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            if(!command("MLSD "+path).startsWith("150")) return list;

            String file;

            String str = read.readLine();

            while(str!=null){
                file = parseLine(str);
                list.add(file);
                str=read.readLine();
            }

            read.close();
            sock.close();

            logger.addEventToLogs("Получен список файлов");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Type getType(){
        return this.type;
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

    private String parseLine(String line){
        return line.substring(line.lastIndexOf("; ")+2);
    }

    private String getVal(String line, String name){
        int indxF=line.indexOf(name);
        if(indxF==-1) return null;
        int indxL=line.indexOf(';', indxF);

        return line.substring(indxF+name.length()+1, indxL);
    }

    private Socket PASV(){
        Socket pasvSocket = null;
        String log;

        try {
            log=command("PASV");

            if(log.startsWith("227")){
                String[] tab=log.substring(log.indexOf("(")+1, log.indexOf(")")).split(",");
                String host=tab[0]+"."+tab[1]+"."+tab[2]+"."+tab[3];
                int port=(Integer.parseInt(tab[4])<<8)+Integer.parseInt(tab[5]);

                pasvSocket=new Socket(host, port);
                logger.addEventToLogs(log);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pasvSocket;
    }

    private boolean setMode(Type type){
        try {
            if(command("TYPE "+type).startsWith("200")){
                this.type=type;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String command(String cmd) throws IOException
    {
        while(this.in.ready()) logger.addEventToLogs("recv: "+this.in.readLine()); //secure clearing
        this.out.write(new String(cmd.getBytes(), StandardCharsets.UTF_8)+"\r\n");
        System.out.println(new String(cmd.getBytes(), StandardCharsets.UTF_8)+"\r\n");
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


    private enum Type {
        A, E, I, L
    }


}
