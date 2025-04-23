package com.example.apktool.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ShellExecutor {

    public static final int DEFAULT_TIMEOUT = 600; // 10 minutes

    /**
     * Execute a shell command with default timeout
     *
     * @param command Command to execute
     * @return Output of the command
     */
    public List<String> execute(String command) throws IOException, InterruptedException {
        return execute(command, DEFAULT_TIMEOUT);
    }

    /**
     * Execute a shell command with specified timeout
     *
     * @param command Command to execute
     * @param timeoutSeconds Timeout in seconds
     * @return Output of the command
     */
    public List<String> execute(String command, int timeoutSeconds) throws IOException, InterruptedException {
        log.info("Executing command: {}", command);
        Process process = Runtime.getRuntime().exec(command);
        List<String> output = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
                log.debug("Command output: {}", line);
            }
        }
        
        boolean completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        if (!completed) {
            process.destroyForcibly();
            throw new RuntimeException("Command execution timed out after " + timeoutSeconds + " seconds");
        }
        
        return output;
    }
}
