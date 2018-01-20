package io.microscape.generator;

import io.microscape.api.Documentation;
import io.microscape.generator.yml.YamlConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassesScanner {

    private static final String EMPTY_SERVICE_ID = "";

    static Documentation scanJars(final Path pathToJar) {

        try (final JarFile jarFile = new JarFile(pathToJar.toFile(), false, JarFile.OPEN_READ)) {
            final String serviceId = findServiceId(jarFile);
            return new Documentation(serviceId);
        } catch (Exception e) {
            return new Documentation(EMPTY_SERVICE_ID);
        }
    }

    private static String findServiceId(final JarFile jarFile) {
        final Optional<String> yml = jarFile.stream().filter(it -> "BOOT-INF/classes/application.yml".equals(it.getName()))
                .map(it -> ClassesScanner.mapServiceId(jarFile, it)).findFirst();

        final Optional<String> properties = jarFile.stream().filter(it -> "BOOT-INF/classes/application.properties".equals(it.getName()))
                .map(it -> ClassesScanner.mapServiceIdFromProperties(jarFile, it)).findFirst();

        return yml.orElse(properties.orElse(EMPTY_SERVICE_ID));
    }

    private static String mapServiceId(final JarFile jarFile, final JarEntry jarEntry) {
        try (
                final InputStream in = jarFile.getInputStream(jarEntry);
                final InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        ) {
            final Yaml yaml = new Yaml();
            final YamlConfig config = yaml.loadAs(reader, YamlConfig.class);
            return config.getSpring().getApplication().getName();
        } catch (Exception e) {
            return null;
        }
    }

    private static String mapServiceIdFromProperties(JarFile jarFile, JarEntry jarEntry) {
        try (
                final InputStream in = jarFile.getInputStream(jarEntry);
                final InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        ) {
            final Properties properties = new Properties();
            properties.load(reader);
            return properties.getProperty("spring.application.name");
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * No Instances.
     */
    private ClassesScanner() {
    }
}
