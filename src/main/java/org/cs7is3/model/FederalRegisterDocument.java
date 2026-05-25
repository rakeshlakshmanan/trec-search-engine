package org.cs7is3.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "DOC")
public record FederalRegisterDocument(
        @JacksonXmlProperty(localName = "DOCNO")
        String documentNumber,

        @JacksonXmlProperty(localName = "PARENT")
        String parent,

        @JacksonXmlProperty(localName = "TEXT")
        String text
) implements IndexableDocument {

    @Override
    public String header() {
        return "";
    }

    @Override
    public String content() {
        return text;
    }
}
