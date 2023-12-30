package com.intuit.ratelimiter.exception;

import lombok.extern.slf4j.Slf4j;

//TODO make exceptions better
@Slf4j
public class ScriptFoundException extends Exception {
    public ScriptFoundException(String s) {
        super(s);
    }
}
