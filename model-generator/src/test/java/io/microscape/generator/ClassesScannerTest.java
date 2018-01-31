package io.microscape.generator;

import io.microscape.api.Documentation;
import io.microscape.api.Section;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassesScannerTest {

    @Test
    void scanJars_should_return_empty_Documentation_if_no_jar_is_given() {
        //Given:
        Path jar = null;

        //When:
        Documentation actual = ClassesScanner.scanJars(jar);

        //Then:
        assertEquals(Documentation.builder().build(), actual);
    }

    @Test
    void scanJars_should_return_empty_Documentation_if_jar_is_empty() {
        //Given: Jar with no content
        Path jar = new File("./src/test/resources/empty.jar").toPath();

        //When:
        Documentation actual = ClassesScanner.scanJars(jar);

        //Then:
        assertEquals(Documentation.builder().build(), actual);
    }

    @ParameterizedTest
    @MethodSource("springBootJarsProvider")
    void scanJars_should_return_Documentation_with_service_id_for_Spring_Boot(String serviceId, String jarName) {
        //Given: Jar with SpringBoot content
        Path jar = new File("./src/test/resources/" + jarName + ".jar").toPath();

        //When:
        Documentation actual = ClassesScanner.scanJars(jar);

        //Then:
        List<Section> consumer = Collections.singletonList(Section.builder().text("exchange-example").build());
        List<Section> sections = Collections.singletonList(Section.builder().headline("Exchange Consumer").content(consumer).build());
        Documentation expected = Documentation.builder().id(serviceId).sections(sections).build();
        assertEquals(expected, actual);
    }

    private static Stream<Arguments> springBootJarsProvider() {
        return Stream.of(
                Arguments.of("yml", "spring-boot-yml"),
                Arguments.of("properties", "spring-boot-properties")
        );
    }

    @Test
    void scanJars_should_find_FeignClient() {
        //Given: Jar with no content
        Path jar = new File("./src/test/resources/feign.jar").toPath();

        //When:
        Documentation actual = ClassesScanner.scanJars(jar);

        //Then:
        final Section feignClients = Section.builder()
                .headline("Feign Clients")
                .text(null)
                .content(
                        Collections.singletonList(Section.builder()
                                .headline(null)
                                .text("titles")
                                .build()))
                .build();
        final Documentation expected = Documentation.builder()
                .id("feignClients")
                .sections(Collections.singletonList(feignClients))
                .build();
        assertEquals(expected, actual);
    }

}