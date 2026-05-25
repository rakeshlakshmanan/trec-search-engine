package org.cs7is3.query;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Query {

    public static Builder builder() {
        return new AutoValue_Query.Builder();
    }

    public abstract String id();

    public abstract String queryContent();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder id(String id);

        public abstract Builder queryContent(String queryContent);

        public abstract String queryContent();

        public Query build(){
            String query = queryContent();
            queryContent(clean(query));
            return autoBuild();
        }
        public static String clean(String text) {
            return text.replaceAll("[^a-zA-Z0-9 ]", " ")
                    .replaceAll("\\s+", " ")
                    .toLowerCase()
                    .trim();
        }

        public abstract Query autoBuild();
    }
}