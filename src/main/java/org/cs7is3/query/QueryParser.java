package org.cs7is3.query;


import org.cs7is3.topics.Topic;
import org.cs7is3.topics.TopicParser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class QueryParser {
    public static List<Query> parseQuery(Path topicPath, QueryType queryType){
        List<Query> queries = new ArrayList<>();
        try {
            List<Topic> topics = TopicParser.parse(topicPath);

            if(queryType == QueryType.TITLE_AND_DESCRIPTION){
                for(Topic topic : topics){
                    Query query = Query.builder()
                            .id(String.valueOf(topic.number()))
                            .queryContent(topic.title() + " " + topic.description())
                            .build();
                    queries.add(query);
                }
            }
            else if(queryType == QueryType.TITLE_AND_DESCRIPTION_AND_NARRATIVE){
                for(Topic topic : topics){
                    Query query = Query.builder()
                            .id(String.valueOf(topic.number()))
                            .queryContent(topic.title() + " " + topic.description() + " " + topic.narrative())
                            .build();
                    queries.add(query);
                }
            }
            else {
                for(Topic topic : topics){
                    Query query = Query.builder()
                            .id(String.valueOf(topic.number()))
                            .queryContent(topic.title())
                            .build();
                    queries.add(query);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return queries;
    }
}
