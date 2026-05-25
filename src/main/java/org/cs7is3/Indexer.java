package org.cs7is3;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.cs7is3.model.IndexableDocument;
import org.cs7is3.parser.FbisParser;
import org.cs7is3.parser.FederalRegisterParser;
import org.cs7is3.parser.FinancialTimesParser;
import org.cs7is3.parser.LaTimesParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Indexer {
    private final static Analyzer ANALYZER = new CustomAnalyzer();
    private final static Similarity SIMILARITY = new BM25Similarity(0.7f, 0.75f);

    public void buildIndex(
            Path docsPath,
            Path indexPath
    ) throws IOException {
        System.out.println("Parsing Documents");
        List<IndexableDocument> documents = readDocuments(docsPath);
        System.out.println("Parsing completed");

        try (Directory indexDirectory = FSDirectory.open(indexPath)) {
            IndexWriter indexWriter = createIndexWriter(indexDirectory);

            System.out.println("Starting Indexing");
            documents.stream()
                    .map(IndexableDocument::toLuceneDocument)
                    .forEach(doc -> {
                        try {
                            indexWriter.addDocument(doc);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

            indexWriter.commit();
            indexWriter.close();
            System.out.println("Indexing completed");
        }
    }

    private static List<IndexableDocument> readDocuments(Path docsPath) throws IOException {
        try (Stream<Path> paths = Files.walk(docsPath, 1)) {
            return paths.filter(Files::isDirectory)
                    .flatMap(Indexer::parseFiles)
                    .toList();
        }
    }

    private static Stream<IndexableDocument> parseFiles(Path contentPath) {
        String pathName = contentPath.getFileName().toString();
        return switch (pathName) {
            case "fbis" -> FbisParser.parse(contentPath).stream().map(IndexableDocument.class::cast);
            case "fr94" -> FederalRegisterParser.parse(contentPath).stream().map(IndexableDocument.class::cast);
            case "ft" -> FinancialTimesParser.parse(contentPath).stream().map(IndexableDocument.class::cast);
            case "latimes" -> LaTimesParser.parse(contentPath).stream().map(IndexableDocument.class::cast);
            default -> Stream.empty();
        };
    }

    private static IndexWriter createIndexWriter(Directory indexDirectory) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(ANALYZER);
        indexWriterConfig.setSimilarity(SIMILARITY);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        return new IndexWriter(indexDirectory, indexWriterConfig);
    }
}
