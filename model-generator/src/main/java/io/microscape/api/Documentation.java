package io.microscape.api;

import java.util.Collections;
import java.util.List;

public class Documentation {

    private final String id;
    private final List<Section> sections = Collections.emptyList();

    public Documentation() {
        this.id = "";
    }

    public Documentation(String id) {
        this.id = id == null ? "" : id;
    }

    public String getId() {
        return id;
    }

    public List<Section> getSections() {
        return sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Documentation that = (Documentation) o;

        return id.equals(that.id) && sections.equals(that.sections);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + sections.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Documentation{" +
                "id='" + id + '\'' +
                ", sections=" + sections +
                '}';
    }
}
