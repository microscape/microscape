package io.microscape.generator;

import io.microscape.api.Documentation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.nio.file.Path;
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
        assertEquals(new Documentation(), actual);
    }

    @Test
    void scanJars_should_return_empty_Documentation_if_jar_is_empty() {
        //Given: Jar with no content
        Path jar = new File("./src/test/resources/empty.jar").toPath();

        //When:
        Documentation actual = ClassesScanner.scanJars(jar);

        //Then:
        assertEquals(new Documentation(), actual);
    }

    @ParameterizedTest
    @MethodSource("springBootJarsProvider")
    void scanJars_should_return_Documentation_with_service_id_for_Spring_Boot(String serviceId, String jarName) {
        //Given: Jar with SpringBoot content
        Path jar = new File("./src/test/resources/" + jarName +".jar").toPath();

        //When:
        Documentation actual = ClassesScanner.scanJars(jar);

        //Then:
        assertEquals(new Documentation(serviceId), actual);
    }

    private static Stream<Arguments> springBootJarsProvider() {
        return Stream.of(
                Arguments.of("yml", "spring-boot-yml"),
                Arguments.of("properties", "spring-boot-properties")
        );
    }
}