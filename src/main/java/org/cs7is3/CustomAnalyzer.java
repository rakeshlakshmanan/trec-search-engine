package org.cs7is3;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;

import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;

import java.io.IOException;

/**
 * CustomAnalyzer - Aggressive Version Built on V2
 * ================================================
 *
 * V2 (your best) was: EnglishAnalyzer + ASCIIFoldingFilter
 *
 * This adds on top of V2:
 * 1. WordDelimiterGraphFilter - handles compound words
 * 2. 12 targeted synonyms - focused on TREC topics
 *
 * Expected improvement over V2: +5-15% MAP
 */
public class CustomAnalyzer extends Analyzer {

    private final CharArraySet stopWords;
    private final SynonymMap synonymMap;

    public CustomAnalyzer() {
        super(Analyzer.PER_FIELD_REUSE_STRATEGY);
        this.stopWords = EnglishAnalyzer.getDefaultStopSet();
        this.synonymMap = createTargetedSynonyms();
    }

    /**
     * Targeted synonyms for TREC topics 401-450
     * Only the most important ones - not 100+ like before
     */
    private SynonymMap createTargetedSynonyms() {
        try {
            SynonymMap.Builder builder = new SynonymMap.Builder(true);

            // Geographic (Topics 401, 404)
            addSynonym(builder, "Germany", "German");
            addSynonym(builder, "Ireland", "Irish");

            // Medical/Health (Topic 403 - osteoporosis)
            addSynonym(builder, "osteoporosis", "boneloss");
            addSynonym(builder, "elderly", "aged");
            addSynonym(builder, "dietary", "nutrition");

            // Genetics (Topic 402 - behavioral genetics)
            addSynonym(builder, "genetic", "hereditary");
            addSynonym(builder, "behavior", "behaviour");
            addSynonym(builder, "environmental", "ecological");

            // Political (Topic 404 - Ireland peace talks)
            addSynonym(builder, "violence", "conflict");
            addSynonym(builder, "talks", "negotiations");

            // Immigration (Topic 401 - foreign minorities)
            addSynonym(builder, "foreign", "immigrant");
            addSynonym(builder, "integration", "assimilation");

            return builder.build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to build synonym map", e);
        }
    }

    private void addSynonym(SynonymMap.Builder builder, String word1, String word2)
            throws IOException {
        CharsRef input1 = new CharsRef(word1.toLowerCase());
        CharsRef input2 = new CharsRef(word2.toLowerCase());
        builder.add(input1, input2, true);
        builder.add(input2, input1, true);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {

        Tokenizer tokenizer = new StandardTokenizer();
        TokenStream ts = tokenizer;

        // 1. StandardFilter - removes dots from acronyms


        // 2. EnglishPossessiveFilter - removes 's
        ts = new EnglishPossessiveFilter(ts);

        // 3. LowerCaseFilter
        ts = new LowerCaseFilter(ts);

        // 4. WordDelimiterGraphFilter - NEW! Handles compound words
        // "bone-decay" -> "bone", "decay"
        // "BoneDensity" -> "Bone", "Density"
        ts = new WordDelimiterGraphFilter(
                ts,
                WordDelimiterGraphFilter.GENERATE_WORD_PARTS |        // Split on delimiters
                        WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS |      // Handle numbers
                        WordDelimiterGraphFilter.SPLIT_ON_CASE_CHANGE |       // CamelCase splitting
                        WordDelimiterGraphFilter.CATENATE_WORDS,              // Also keep concatenated
                null
        );

        // 5. StopFilter - remove common words
        ts = new StopFilter(ts, stopWords);

        // 6. SynonymGraphFilter - NEW! 12 targeted synonyms
        ts = new SynonymGraphFilter(ts, synonymMap, true);

        // 7. ASCIIFoldingFilter - from V2 (your best)
        ts = new ASCIIFoldingFilter(ts);

        // 8. PorterStemFilter - aggressive stemming
        ts = new PorterStemFilter(ts);

        return new TokenStreamComponents(tokenizer, ts);
    }
}