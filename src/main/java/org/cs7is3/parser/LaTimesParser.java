package org.cs7is3.parser;

import org.cs7is3.model.LaTimesDocument;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LaTimesParser {
    public static List<LaTimesDocument> parse(Path laTimes) {
        try (Stream<Path> files = Files.list(laTimes)) {
            return files
                    .filter(path -> !path.toString().contains("read"))
                    .map(LaTimesParser::readFileContent)
                    .flatMap(LaTimesParser::preprocess)
                    .map(xml -> XmlMapper.parse(xml, LaTimesDocument.class))
                    .toList();

        } catch (IOException e) {
            return List.of();
        }
    }

    private static Stream<String> preprocess(String fileContent) {
        Pattern pattern = Pattern.compile("<DOC>(.*?)</DOC>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fileContent);
        List<String> rawDocuments = new ArrayList<>();

        while (matcher.find()) {
            String cleaned = matcher.group(0)
                    .replaceAll("(?s)<!--.*?-->", "")
                    .replaceAll("[ \\t]+", " ")
                    .replaceAll("\\n{2,}", "\n")
                    .replaceAll(">\\s+<", "><")
                    .replaceAll("\n", " ")
                    .replaceAll("</?P>", "");

            rawDocuments.add(cleaned);
        }
        return rawDocuments.stream();
    }

    private static String readFileContent(Path path) {
        try {
            return Files.readString(path, StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
