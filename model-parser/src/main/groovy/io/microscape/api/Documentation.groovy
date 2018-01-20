package io.microscape.api

import groovy.transform.EqualsAndHashCode

import static java.util.stream.Collectors.toList

@EqualsAndHashCode
class Documentation {

    String id
    List<Section> sections = []

    Documentation(Map<String, Object> map) {
        this.id = map["id"]
        this.sections = map["sections"]?.stream()?.map{ new Section(it) }?.collect(toList()) ?: []
    }
}
