package org.cs7is3.parser;

import org.cs7is3.model.FinancialTimesDocument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FinancialTimesParser {
    public static List<FinancialTimesDocument> parse(Path financialTimesPath) {
        try (Stream<Path> files = Files.list(financialTimesPath)) {
            return files
                    .filter(Files::isDirectory)
                    .flatMap(FinancialTimesParser::readContenFromFiles)
                    .flatMap(FinancialTimesParser::preprocess)
                    .map(xml -> XmlMapper.parse(xml, FinancialTimesDocument.class))
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
                    .replaceAll(
                            "(?<=<DATE>)(\\d{6}).*?(?=</DATE>)",
                            "$1"
                    );

            rawDocuments.add(cleaned);
        }
        return rawDocuments.stream();
    }

    private static Stream<String> readContenFromFiles(Path directoryPath) {
        try {
            return Files.list(directoryPath)
                    .map(path -> {
                        try {
                            return Files.readString(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
