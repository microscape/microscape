package io.microscape.parser.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

import static java.util.stream.Collectors.toList

@EqualsAndHashCode
class Section {

    String headline;
    String text;
    List<Section> content = [];

    Section(Map<String, Object> map) {
        this.headline = map["headline"]
        this.text = map["text"]
        this.content = map["content"]?.stream()?.map{ new Section(it) }?.collect(toList())
    }
}
