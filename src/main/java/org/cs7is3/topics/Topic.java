package org.cs7is3.topics;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Topic {

    public static Builder builder() {
        return new AutoValue_Topic.Builder();
    }

    public abstract int number();

    public abstract String title();

    public abstract String description();

    public abstract String narrative();



    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder number(int id);

        public abstract Builder title(String title);

        public abstract Builder description(String author);

        public abstract Builder narrative(String bibliography);

        public abstract Topic build();
    }
}
