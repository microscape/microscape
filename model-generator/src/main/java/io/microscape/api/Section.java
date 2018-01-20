package io.microscape.api;

import java.util.Collections;
import java.util.List;

class Section {

    private final String headline;
    private final String text;
    private final List<Section> content = Collections.emptyList();

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
}
