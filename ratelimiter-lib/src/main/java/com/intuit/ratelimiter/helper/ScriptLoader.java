package com.intuit.ratelimiter.helper;

import com.intuit.ratelimiter.exception.FileLoadException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

//TODO this class doesn't look like util class
@Slf4j
public class ScriptLoader {

    private final AtomicReference<String> storedScript;

    public ScriptLoader(String scriptPath) throws FileLoadException {
        this.storedScript = new AtomicReference<>(loadScript(scriptPath));;
    }

    private String loadScript(String scriptPath) throws FileLoadException {
        URL url = ScriptLoader.class.getClassLoader().getResource(scriptPath);
        String script = "";
        if (url == null) {
            throw new FileLoadException(String.format("Script %s not found", scriptPath));
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            script = reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            new FileLoadException("Unable to load Redis LUA script file");
        }
        return script;
    }

    public AtomicReference<String> getScript(){
        return storedScript;
    }
}