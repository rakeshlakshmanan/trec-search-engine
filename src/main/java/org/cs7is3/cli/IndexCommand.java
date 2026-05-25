package org.cs7is3.cli;

import org.cs7is3.Indexer;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(
        name = "index",
        description = "Indexes documents"
)
public class IndexCommand implements Callable<Integer> {
    @Option(names = "--docs", required = true, description = "Path or name of the documents to index")
    private String docs;

    @Option(names = "--index", required = true, description = "Path to store the index")
    private String index;


    @Override
    public Integer call() {
        Indexer indexer = new Indexer();
        try {
            indexer.buildIndex(
                    Path.of(docs),
                    Path.of(index)
            );
            return 0;
        } catch (IOException e) {
            return 1;
        }
    }
}
