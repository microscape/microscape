package io.microscape.api;

import java.util.ArrayList;
import java.util.List;

public class Section {

    private final String headline;
    private final String text;
    private final List<Section> content = new ArrayList<>(10);

    public Section(String headline, String text) {
        this.headline = headline;
        this.text = text;
    }

    public String getHeadline() {
        return headline;
    }

    public String getText() {
        return text;
    }

    public List<Section> getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Section section = (Section) o;

        if (headline != null ? !headline.equals(section.headline) : section.headline != null) return false;
        if (text != null ? !text.equals(section.text) : section.text != null) return false;
        return content.equals(section.content);
    }

    @Override
    public int hashCode() {
        int result = headline != null ? headline.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + content.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Section{" +
                "headline='" + headline + '\'' +
                ", text='" + text + '\'' +
                ", content=" + content +
                '}';
    }
}
