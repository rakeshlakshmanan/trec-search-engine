package org.cs7is3.topics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TopicParser {

    private static final Pattern TOPIC_PATTERN = Pattern.compile(
            "<top>\\s*" +
                    "<num> Number: (\\d+)\\s*" +
                    "<title>([^<]+)\\s*" +
                    "<desc> Description:\\s*([^<]+)\\s*" +
                    "<narr> Narrative:\\s*([^<]+)\\s*" +
                    "</top>",
            Pattern.MULTILINE);

    public static List<Topic> parse(Path topicsFilePath) throws IOException {
        String text = Files.readString(topicsFilePath);
        List<Topic> topics = new ArrayList<>();
        Matcher matcher = TOPIC_PATTERN.matcher(text);

        while (matcher.find()) {
            int number = Integer.parseInt(matcher.group(1).trim());
            String title = matcher.group(2).trim();
            String desc = matcher.group(3).trim();
            String narrative = matcher.group(4).trim();

            Topic topic = Topic.builder()
                    .number(number)
                    .title(title)
                    .description(desc)
                    .narrative(narrative)
                    .build();
            topics.add(topic);
        }
        return topics;
    }
}
