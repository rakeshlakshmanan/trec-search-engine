package org.cs7is3;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.cs7is3.topics.Topic;
import org.cs7is3.topics.TopicParser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Searcher {
    private static final String RUN_TAG = "baseline";
    private static final Analyzer analyzer = new CustomAnalyzer();
    private static final Similarity similarity = new BM25Similarity(0.7f, 0.75f);

    public void searchTopics(
            Path indexPath,
            Path topicsPath,
            Path outputRun,
            int numDocs
    ) throws java.io.IOException {
        List<Topic> topics = TopicParser.parse(topicsPath);

        Directory dir = FSDirectory.open(indexPath);
        DirectoryReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(similarity);

        int feedbackDocuments = 8;
        int feedbackTerms = 32;
        float alpha = 0.5f;
        float beta = 3.2f;

        String[] searchableFields = {"header", "content"};
        Map<String, Float> boosts = Map.of("header", 1.0f, "content", 7.0f);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(searchableFields, analyzer, boosts);

        try (BufferedWriter writer = Files.newBufferedWriter(outputRun, StandardCharsets.UTF_8)) {
            for (Topic topic : topics) {
                String queryText = "(%s)^3.5 (%s)^1.5".formatted(topic.title(), topic.description());
                Query originalQuery;
                try {
                    originalQuery = parser.parse(queryText);
                } catch (ParseException e) {
                    System.err.printf("Error parsing query for topic (%d)%n", topic.number());
                    continue;
                }

                TopDocs firstPass = searcher.search(originalQuery, feedbackDocuments);

                Query expandedQuery = expandQuery(
                        searcher,
                        originalQuery,
                        firstPass,
                        analyzer,
                        feedbackTerms,
                        alpha,
                        beta
                );

                TopDocs finalResults = searcher.search(expandedQuery, numDocs);

                int rank = 1;
                for (var scoreDoc : finalResults.scoreDocs) {
                    Document hitDoc = searcher.doc(scoreDoc.doc);
                    String docno = hitDoc.get("documentNumber");
                    if (docno == null) docno = "UNKNOWN";

                    writer.write(String.format(
                            "%d Q0 %s %d %f %s%n",
                            topic.number(),
                            docno,
                            rank,
                            scoreDoc.score,
                            RUN_TAG
                    ));
                    rank++;
                }
            }
        }
        reader.close();
        dir.close();
        System.out.printf("Search complete. Results written to: %s%n", outputRun);
    }

    private Query expandQuery(
            IndexSearcher searcher,
            Query originalQuery,
            TopDocs feedbackDocs,
            Analyzer analyzer,
            int numTerms,
            float alpha,
            float beta
    ) throws IOException {
        Map<String, Integer> termFreqs = new HashMap<>();
        for (ScoreDoc sd : feedbackDocs.scoreDocs) {
            Document doc = searcher.doc(sd.doc);
            IndexableField content = doc.getField("content");
            if (content == null || content.stringValue() == null) continue;

            try (TokenStream tokenStream = analyzer.tokenStream("content", content.stringValue())) {
                CharTermAttribute termAttr = tokenStream.addAttribute(CharTermAttribute.class);
                tokenStream.reset();
                while (tokenStream.incrementToken()) {
                    String term = termAttr.toString();
                    if (term.length() > 3) {
                        termFreqs.merge(term, 1, Integer::sum);
                    }
                }
                tokenStream.end();
            }
        }

        List<String> topTerms = termFreqs.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(numTerms)
                .map(Map.Entry::getKey)
                .toList();

        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(new BoostQuery(originalQuery, alpha), BooleanClause.Occur.SHOULD);

        for (String term : topTerms) {
            try {
                Query termQuery = new TermQuery(new Term("content", term));
                builder.add(new BoostQuery(termQuery, beta), BooleanClause.Occur.SHOULD);
            } catch (Exception e) {
                System.out.println("Invalid Term");
            }
        }

        return builder.build();
    }

}
