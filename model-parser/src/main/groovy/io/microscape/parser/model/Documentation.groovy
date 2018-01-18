package io.microscape.parser.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

import static java.util.stream.Collectors.toList

@EqualsAndHashCode
class Documentation {

    String id;
    List<Section> sections = [];

    Documentation(Map<String, Object> map) {
        this.id = map["id"]
        this.sections = map["sections"]?.stream()?.map{ new Section(it) }?.collect(toList())
    }
}
