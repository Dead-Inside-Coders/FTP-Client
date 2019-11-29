package com.logging;

import java.util.ArrayList;
import java.util.List;

public class Logger
{

    private static Logger instance = null;

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    private Logger() {
    }

    private List<String> logs = new ArrayList<>();

    public List<String> getLogs() { return logs; }

    public void addEventToLogs(String event) { logs.add(event); }

    public void clearLogs(){logs.clear();}
}
