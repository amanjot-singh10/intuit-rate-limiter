package com.intuit.ratelimiter.exception;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class FileLoadException extends Exception{
    public FileLoadException(String s, IOException e) {
    }

    @Override
    public String getMessage() {
        String msg = "Redis Connection has not been established !!! ";
        return msg;
    }
}
