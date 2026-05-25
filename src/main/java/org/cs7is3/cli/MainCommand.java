package org.cs7is3.cli;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "Best CLI",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "CLI with index and search commands",
        subcommands = {
                IndexCommand.class,
                SearchCommand.class
        }
)
public class MainCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        System.out.println("Please specify a subcommand (index or search)");
        return 0;
    }
}
