package org.cs7is3.cli;

import org.cs7is3.Searcher;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "search", description = "Searches topics in the given index.")
public class SearchCommand implements Callable<Integer> {

    @Option(names = "--index", required = true, description = "Path to the index")
    private String index;

    @Option(names = "--topics", required = true, description = "Path to the topics file")
    private String topics;

    @Option(names = "--output", required = true, description = "Output file for results")
    private String output;

    @Option(names = "--numDocs", defaultValue = "1000", description = "Number of documents to retrieve (default: ${DEFAULT-VALUE})")
    private int numDocs;

    @Override
    public Integer call() {
        Searcher searcher = new Searcher();
        try {
            searcher.searchTopics(
                    Path.of(index),
                    Path.of(topics),
                    Path.of(output),
                    numDocs
            );
            return 0;
        } catch (IOException e) {
            return 1;
        }
    }
}
