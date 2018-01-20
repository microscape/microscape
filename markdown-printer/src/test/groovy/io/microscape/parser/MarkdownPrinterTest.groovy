package io.microscape.parser

import io.microscape.api.Documentation
import io.microscape.api.Section
import io.microscape.printer.markdown.MarkdownPrinter
import spock.lang.*

import java.nio.file.Path
import java.nio.file.Paths

@Title("MarkdownPrinter Unittests")
@Subject(MarkdownPrinter)
class MarkdownPrinterTest extends Specification {

    @Shared
    String defaultPathBase

    def setupSpec() {
        defaultPathBase = new File("./src/test/resources").getCanonicalPath()
    }

    @Unroll
    def "printing the Model should result in correct Markdown"() {
        given: "a Model"
        Documentation documentation = model
        String expected = new File("$defaultPathBase/$markdown")
                .getText("utf-8")
                .replaceAll("\\r?\\n", "\n")

        when: "this model is printed"
        def actual = new MarkdownPrinter().print documentation
        actual = actual.replaceAll("\\r?\\n", "\n")

        then: "the printed markdown should be correct"
        actual == expected

        cleanup: "nothing to do"

        where: "possible testcases are"
        model                                                                                             || markdown
        [id: "simple", sections: []]                                                                      || "simple.md"
        [id: "sections", sections: [[headline: "h1", text: "t1"], [headline: "h2", text: "t2"]]]          || "sections.md"
        [id: "nested", sections: [[headline: "h3", text: "t3", content: [[headline: "h4", text: "t4"]]]]] || "nested.md"
        [id: "list", sections: [[headline: "h1", content: [
                [text: "t1"], [text: "t2"], [text: "t3"],
                [headline: "h2", content:
                        [[text: "t4"], [text: "t5"], [text: "t6"]]]]]]]                                   || "list.md"
    }
}
