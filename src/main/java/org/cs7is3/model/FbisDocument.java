package org.cs7is3.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "DOC")
public record FbisDocument(
        @JacksonXmlProperty(localName = "DOCNO")
        String documentNumber,

        @JacksonXmlProperty(localName = "HT")
        String headerTag,

        @JacksonXmlProperty(localName = "HEADER")
        String header,

        @JacksonXmlProperty(localName = "TEXT")
        String text
) implements IndexableDocument {

    @Override
    public String content() {
        return text;
    }
}
