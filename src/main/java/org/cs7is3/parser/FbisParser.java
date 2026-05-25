package org.cs7is3.parser;

import org.apache.commons.text.StringEscapeUtils;
import org.cs7is3.model.FbisDocument;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FbisParser {

    public static List<FbisDocument> parse(Path fbisPath) {
        try (Stream<Path> files = Files.list(fbisPath)){
            return files
                    .filter(path -> !path.toString().contains("read"))
                    .map(FbisParser::readFileContent)
                    .flatMap(FbisParser::preprocess)
                    .map(xml -> XmlMapper.parse(xml, FbisDocument.class))
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
                    // Normalize all whitespace
                    .replaceAll("\\s+", " ")
                    // Remove spaces between tags
                    .replaceAll(">\\s+<", "><")
                    // Wrap inner text with CDATA
                    .replaceAll(">\\s*([^<>]+?)\\s*<", "><![CDATA[$1]]><")
                    // Add quotes around unquoted attributes
                    .replaceAll("\\s(\\w+)=([^\\s\"'>]+)", " $1=\"$2\"")
                    // Rename numeric tags like <123> → <num123>
                    .replaceAll("<(\\d+)([^>]*)>", "<num$1$2>")
                    .replaceAll("</(\\d+)>", "</num$1>")
                    .trim();


            String withoutEscapedHtml = StringEscapeUtils.unescapeHtml4(cleaned);
            rawDocuments.add(withoutEscapedHtml);
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
