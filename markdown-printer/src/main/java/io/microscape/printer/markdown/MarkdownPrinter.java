package io.microscape.printer.markdown;

import io.microscape.api.Documentation;
import io.microscape.api.Printer;
import io.microscape.api.Section;

import java.util.stream.IntStream;

public class MarkdownPrinter implements Printer {

    @Override
    public String print(final Documentation documentation) {
        final StringBuilder sb = new StringBuilder(4000);

        sb.append("# ");
        sb.append(documentation.getId());
        newline(sb);

        documentation.getSections().forEach(it -> printSection(it, 0, sb));

        return sb.toString();
    }

    private void printSection(final Section section, final int level, final StringBuilder sb) {

        final String headlinePrefix = IntStream.range(0, level + 2).mapToObj(it -> "#").reduce("", (a,b) -> a + b) + " ";
        final boolean headlineEmtpy = section.getHeadline() == null || section.getHeadline().isEmpty();
        final String listPrefix = level > 0 && headlineEmtpy ? "* " : "";

        if (!headlineEmtpy) {
            sb.append(headlinePrefix);
            sb.append(section.getHeadline());
            newline(sb);
        }

        if (section.getText() != null && !section.getText().isEmpty()) {
            sb.append(listPrefix);
            sb.append(section.getText());
            newline(sb);
        }

        section.getContent().forEach(it -> printSection(it, level + 1, sb));
    }

    private void newline(StringBuilder sb) {
        sb.append('\n');
    }
}
