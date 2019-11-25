package com.datepackage;

import java.util.ArrayList;

public class DateStore
{
    //Надо было все статическим делать ((( блин ((
    private String serverdate;
    private String userdate;
    private String passwordate;
    private ArrayList<String> logs = new ArrayList<>();

    public String getPasswordate() {
        return passwordate;
    }

    public String getUserdate() {
        return userdate;
    }

    public void setPasswordate(String passwordate) {
        this.passwordate = passwordate;
    }

    public void setUserdate(String userdate) {
        this.userdate = userdate;
    }

    public void setServerdate(String serverdate) {
        this.serverdate = serverdate;
    }

    public String getServerdate() {
        return serverdate;
    }

    public ArrayList<String> getLogs() { return logs; }
    public void addEventToLogs(String event) { logs.add(event); }
    public void clearLogs(){logs.clear();}

}
