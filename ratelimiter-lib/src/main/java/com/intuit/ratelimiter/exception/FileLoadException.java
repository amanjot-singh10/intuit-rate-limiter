package com.intuit.ratelimiter.exception;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class FileLoadException extends Exception{
    public FileLoadException(String msg) {
        super(msg);
    }
}
