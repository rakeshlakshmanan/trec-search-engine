package org.cs7is3.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class XmlMapper {

    private final static com.fasterxml.jackson.dataformat.xml.XmlMapper XML_MAPPER;

    static {
        XML_MAPPER = new com.fasterxml.jackson.dataformat.xml.XmlMapper();

        XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        XML_MAPPER.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        XML_MAPPER.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
        XML_MAPPER.configure(FromXmlParser.Feature.EMPTY_ELEMENT_AS_NULL, true);
        XML_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        XML_MAPPER.registerModule(new JavaTimeModule());
        XML_MAPPER.registerModule(new Jdk8Module());
    }

    private XmlMapper() {

    }

    public static <T> T parse(String xml, Class<T> clazz) {
        try {
            return XML_MAPPER.readValue(xml, clazz);
        } catch (JsonProcessingException e) {
            System.out.printf("Could not deserialize xml object: %s%n", xml);
            throw new RuntimeException(e);
        }
    }
}
