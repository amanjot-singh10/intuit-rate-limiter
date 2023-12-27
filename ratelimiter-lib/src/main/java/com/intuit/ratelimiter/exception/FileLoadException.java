package com.intuit.ratelimiter.exception;

import java.io.IOException;

public class FileLoadException extends Exception{
    public FileLoadException(String s, IOException e) {
    }

    @Override
    public String getMessage() {
        String msg = "Redis Connection has not been established !!! ";
        return msg;
    }
}
