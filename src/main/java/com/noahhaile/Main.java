package com.noahhaile;

public class Main {
    public static void main(String[] args) {

        // Basic Setup
        ShellCommandWrapper shellWrapper = new ShellCommandWrapperImpl();
        shellWrapper.setWorkingDirectory("/");
        shellWrapper.setEnvironmentVariable("KEY", "VALUE");

        // You can run scripts without a file but separate with newlines.
//        shellWrapper.setExecutionScript(
//                "echo hello\n" + "echo world"
//                , TargetPlatform.ALL);

        // If working inside an IDE, store file in the root of the project
        shellWrapper.readExecutionScript("demo.bat", TargetPlatform.ALL);

        int exitCode = shellWrapper.execute();
        String output = shellWrapper.getOutput();
        System.out.println("Exit Code: " + exitCode);
        System.out.println("Output: " + output);
    }
}