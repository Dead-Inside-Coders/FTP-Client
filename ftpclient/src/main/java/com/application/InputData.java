package com.application;

public class InputData
{
    private String serverdate;
    private String userdate;
    private String passwordate;

    private static InputData instance = null;

    public static InputData getInstance() {
        if (instance == null) {
            instance = new InputData();
        }
        return instance;
    }

    private InputData() { }

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



}
