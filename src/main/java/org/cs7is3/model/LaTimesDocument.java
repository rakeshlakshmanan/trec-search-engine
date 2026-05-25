package org.cs7is3.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "DOC")
public record LaTimesDocument(
        @JacksonXmlProperty(localName = "DOCNO")
        String documentNumber,

        @JacksonXmlProperty(localName = "DOCID")
        String documentId,

        @JacksonXmlProperty(localName = "DATE")
        String date,

        @JacksonXmlProperty(localName = "SECTION")
        Optional<String> section,

        @JacksonXmlProperty(localName = "LENGTH")
        Optional<String> length,

        @JacksonXmlProperty(localName = "HEADLINE")
        Optional<String> headline,

        @JacksonXmlProperty(localName = "BYLINE")
        Optional<String> byline,

        @JacksonXmlProperty(localName = "TEXT")
        Optional<String> text,

        @JacksonXmlProperty(localName = "GRAPHIC")
        Optional<String> graphic,

        @JacksonXmlProperty(localName = "TYPE")
        Optional<String> type

) implements IndexableDocument {

    @Override
    public String documentNumber() {
        return documentNumber.trim();
    }

    @Override
    public String header() {
        return headline.map(headline -> "%s %s".formatted(headline, byline))
                .orElse("");
    }

    @Override
    public String content() {
        return text.orElse("");
    }
}
