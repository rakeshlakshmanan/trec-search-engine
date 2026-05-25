package org.cs7is3.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.LocalDate;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "DOC")
public record FinancialTimesDocument(
        @JacksonXmlProperty(localName = "DOCNO")
        String documentNumber,

        @JacksonXmlProperty(localName = "PROFILE")
        String profile,

        @JacksonXmlProperty(localName = "DATE")
        @JsonFormat(pattern = "yyMMdd")
        LocalDate date,

        @JacksonXmlProperty(localName = "HEADLINE")
        Optional<String> headline,

        @JacksonXmlProperty(localName = "TEXT")
        Optional<String> text,

        @JacksonXmlProperty(localName = "PUB")
        String publisher,

        @JacksonXmlProperty(localName = "PAGE")
        String page
) implements IndexableDocument {

    @Override
    public String header() {
        return headline.map(headline -> "%s %s".formatted(headline, publisher)).orElse("");
    }

    @Override
    public String content() {
        return text.orElse("");
    }
}