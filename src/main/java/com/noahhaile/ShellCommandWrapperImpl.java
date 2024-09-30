package com.noahhaile;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ShellCommandWrapperImpl implements ShellCommandWrapper {

    private ProcessBuilder processBuilder;
    private Process process;
    private StringBuilder outputBuffer;

    public ShellCommandWrapperImpl() {
        this.processBuilder = new ProcessBuilder();
        this.outputBuffer = new StringBuilder();
    }

    @Override
    public boolean setWorkingDirectory(String path) {
        try {
            processBuilder.directory(new File(path));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setEnvironmentVariable(String name, String value) {
        Map<String, String> env = processBuilder.environment();
        env.put(name, value);
    }

    @Override
    public void removeEnvironmentVariable(String name) {
        Map<String, String> env = processBuilder.environment();
        env.remove(name);
    }

    @Override
    public String getEnvironmentVariable(String name) {
        return processBuilder.environment().get(name);
    }

    @Override
    public boolean setExecutionScript(String script, TargetPlatform platform) {
        if (!isPlatformCompatible(platform)) {
            System.err.println("The target platform does not match the current OS.");
            return false;
        }

        try {
            String[] command = getPlatformSpecificCommand(script, platform);
            processBuilder.command(command);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean readExecutionScript(String path, TargetPlatform platform) {
        File scriptFile = new File(path);
        if (!scriptFile.exists()) {
            System.err.println("Script file not found at path: " + path);
            return false;
        }

        if (!isPlatformCompatible(platform)) {
            System.err.println("The target platform does not match the current OS.");
            return false;
        }

        try {
            String scriptContent = readFileContent(scriptFile);
            var command = getPlatformSpecificCommand(scriptContent, platform);
            processBuilder.command(command);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int execute() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            process = processBuilder.start();

            // Capture output and error streams asynchronously
            Future<String> outputFuture = executorService.submit(() -> captureOutput(process.getInputStream()));
            Future<String> errorFuture = executorService.submit(() -> captureOutput(process.getErrorStream()));

            // Wait for process to complete and get exit code
            int exitCode = process.waitFor();

            // Append output and error to outputBuffer
            outputBuffer.append(outputFuture.get());
            outputBuffer.append(errorFuture.get());

            return exitCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            // Shutdown the executor service and clean up the process
            executorService.shutdown();
            cleanupProcess();
        }
    }

    @Override
    public String getOutput() {
        return outputBuffer.toString();
    }

    // --- Helper Methods ---

    private boolean isPlatformCompatible(TargetPlatform platform) {
        String osName = System.getProperty("os.name").toLowerCase();

        switch (platform) {
            case WINDOWS:
                return osName.contains("win");
            case UNIX:
                return osName.contains("nix") || osName.contains("nux") || osName.contains("aix");
            case MAC:
                return osName.contains("mac");
            case ALL:
                return true;
            default:
                return false;
        }
    }

    private TargetPlatform getCompatiblePlatform() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            return TargetPlatform.WINDOWS;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return TargetPlatform.UNIX;
        } else {
            return TargetPlatform.MAC;
        }
    }

    private String[] getPlatformSpecificCommand(String script, TargetPlatform platform) {
        switch (platform) {
            case WINDOWS:
                return new String[]{"cmd.exe", "/c", combineScript(script)};
            case UNIX:
            case MAC:
                return new String[]{"bash", "-c", combineScript(script)};
            case ALL:
                return getCompatiblePlatform() == TargetPlatform.WINDOWS
                        ? new String[]{"cmd.exe", "/c", combineScript(script)}
                        : new String[]{"bash", "-c", combineScript(script)};
            default:
                return new String[]{};
        }
    }

    private String combineScript(String script) {
        return script.replaceAll("[\\r\\n]+", " & ");
    }

    private String readFileContent(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private String captureOutput(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private void cleanupProcess() {
        if (process != null) {
            process.destroy(); // Ensure process is killed if still running
            try {
                process.waitFor(); // Wait for it to die, if necessary
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
