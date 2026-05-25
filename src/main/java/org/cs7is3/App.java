package org.cs7is3;

import org.cs7is3.cli.MainCommand;
import picocli.CommandLine;

import java.io.IOException;


public class App {
    public static void main(String[] args) throws IOException {

        int exitCode = new CommandLine(new MainCommand()).execute(args);
        System.exit(exitCode);
    }
}
