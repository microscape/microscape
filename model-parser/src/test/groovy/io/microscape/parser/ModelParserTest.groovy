package io.microscape.parser

import io.microscape.api.Documentation
import io.microscape.api.Section
import spock.lang.*

import java.nio.file.Path
import java.nio.file.Paths

@Title("ModelParser Unittests")
@Subject(ModelParser)
class ModelParserTest extends Specification {

    @Shared
    String defaultPathBase

    def setupSpec() {
        defaultPathBase = new File("./src/test/resources").getCanonicalPath()
    }

    @Unroll
    def "parsing the Model from folder #directory should result in a correct Model #model"() {
        given: "a directory with a model.json"
        Path folder = Paths.get(defaultPathBase, directory)
        Documentation expected = model

        when: "this model is parsed"
        def actualDocumentation = ModelParser.parseModel folder

        then: "the parsed model should be correct"
        actualDocumentation[0] == expected

        cleanup: "nothing to do"

        where: "possible testcases are"
        directory  || model
        "simple"   || [id: "simple", sections: []]
        "sections" || [id: "sections", sections: [[headline: "h1", text: "t1"], [headline: "h2", text: "t2"]]]
        "nested"   || [id: "nested", sections: [[headline: "h3", text: "t3", content: [[headline: "h4", text: "t4"]]]]]
    }

    def "sections is a List of Section"() {
        given: "a directory with a model.json"
        Path folder = Paths.get(defaultPathBase, "simple")

        when: "this model is parsed"
        def actualDocumentation = ModelParser.parseModel folder

        then: "the parsed model contains Section"
        actualDocumentation.find().sections?.stream().allMatch { it instanceof Section }
    }

    def "content is a List of Section"() {
        given: "a directory with a model.json"
        Path folder = Paths.get(defaultPathBase, "nested")

        when: "this model is parsed"
        def actualDocumentation = ModelParser.parseModel folder

        then: "the parsed model contains Section"
        actualDocumentation.find().sections.find().content?.stream().allMatch { it instanceof Section }
    }
}
