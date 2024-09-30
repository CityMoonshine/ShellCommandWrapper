package com.noahhaile;

import java.util.Map;
import java.util.concurrent.Future;

public interface ShellCommandWrapper {
    ProcessBuilder processBuilder = new ProcessBuilder();
    Map<String, String> env = processBuilder.environment();

    boolean setWorkingDirectory(String path);
    void setEnvironmentVariable(String name, String value);
    void removeEnvironmentVariable(String name);
    String getEnvironmentVariable(String name);
    boolean setExecutionScript(String script, TargetPlatform platform);

    boolean readExecutionScript(String path, TargetPlatform platform);
    int execute();
    String getOutput();
}
