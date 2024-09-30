package com.noahhaile;

public class Main {
    public static void main(String[] args) {

        ShellCommandWrapper shellWrapper = new ShellCommandWrapperImpl();
        shellWrapper.setWorkingDirectory("/");
        shellWrapper.setEnvironmentVariable("KEY", "VALUE");

        shellWrapper.readExecutionScript("demo.bat", TargetPlatform.ALL);
        int exitCode = shellWrapper.execute();

        String output = shellWrapper.getOutput();
        System.out.println("Exit Code: " + exitCode);
        System.out.println("Output: " + output);
    }
}