# ShellCommandWrapper
ShellCommandWrapper is a Java-based utility designed to execute shell commands across different platforms (Windows, Linux, Mac) in a seamless and platform-agnostic way. Easy-to-use, dead simple interface for setting up working directories, environment variables, and executing shell scripts while capturing command outputs.

``` java
ShellCommandWrapper shellWrapper = new ShellCommandWrapperImpl();
shellWrapper.setWorkingDirectory("/");
shellWrapper.setEnvironmentVariable("KEY", "VALUE");

// You can run scripts without a file but separate with newlines.
    // shellWrapper.setExecutionScript(
    // "echo hello\n" + "echo world"
    //  , TargetPlatform.ALL);

// If working inside an IDE, store file in the root of the project
shellWrapper.readExecutionScript("demo.bat", TargetPlatform.WINDOWS);

int exitCode = shellWrapper.execute();
String output = shellWrapper.getOutput();
System.out.println("Exit Code: " + exitCode);
System.out.println("Output: " + output);
```

This is all you need to get started.