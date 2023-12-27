package com.intuit.ratelimiter.utils;

import com.intuit.ratelimiter.exception.FileLoadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ScriptLoader {

    private final AtomicReference<String> storedScript;

    public ScriptLoader(String scriptPath) {
        this.storedScript = new AtomicReference<>(loadScript(scriptPath));;
    }

    private String loadScript(String scriptPath) {
        URL url = ScriptLoader.class.getClassLoader().getResource(scriptPath);
        String script = "";
        if (url == null) {
            throw new IllegalArgumentException("script sliding-window-ratelimit.lua not found");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            script = reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            new FileLoadException("Unable to load Redis LUA script file", e);
        }
        return script;
    }

    public AtomicReference<String> getScript(){
        return storedScript;
    }
}
