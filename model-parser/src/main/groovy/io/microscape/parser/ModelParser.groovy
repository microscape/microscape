package io.microscape.parser

import groovy.json.JsonSlurper
import io.microscape.parser.model.Documentation

import java.nio.file.Files
import java.nio.file.Path
import static java.util.stream.Collectors.toList;

class ModelParser {
    static List<Documentation> parseModel(Path directory) {
        def jsonSlurper = new JsonSlurper();

        return Files.list(directory).filter { it.toString().endsWith ".micromodel.json" }
                .map { it.toFile() }
                .map { new Documentation(jsonSlurper.parse(it)) }
                .collect(toList())
    }
}