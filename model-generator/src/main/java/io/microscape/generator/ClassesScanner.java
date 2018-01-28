package io.microscape.generator;

import io.microscape.api.Documentation;
import io.microscape.api.Section;
import io.microscape.feign.FeignVisitor;
import io.microscape.generator.yml.YamlConfig;
import org.objectweb.asm.ClassReader;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ClassesScanner {

    private static final Logger LOGGER = Logger.getLogger(ClassesScanner.class.getName());

    private static final String EMPTY_SERVICE_ID = "";

    static Documentation scanJars(final Path pathToJar) {

        try (final JarFile jarFile = new JarFile(pathToJar.toFile(), false, JarFile.OPEN_READ)) {
            final String serviceId = findServiceId(jarFile);
            final Documentation documentation = new Documentation(serviceId);

            final List<Section> feignClients = jarFile.stream().filter(it -> it.getName().endsWith(".class"))
                    .flatMap(it -> findFeignClientsAndReturnServiceNames(jarFile, it))
                    .map(it -> new Section(null, it))
                    .collect(toList());

            if (!feignClients.isEmpty()) {
                final Section feignClientSection = new Section("Feign Clients", null);
                feignClientSection.getContent().addAll(feignClients);
                documentation.getSections().add(feignClientSection);
            }

            return documentation;
        } catch (Exception e) {
            return new Documentation(EMPTY_SERVICE_ID);
        }
    }

    private static Stream<? extends String> findFeignClientsAndReturnServiceNames(JarFile jarFile, JarEntry it) {
        try (final InputStream in = jarFile.getInputStream(it)) {
            final ClassReader classReader = new ClassReader(in);
            final FeignVisitor feignVisitor = new FeignVisitor();
            classReader.accept(feignVisitor, ClassReader.SKIP_DEBUG);
            return feignVisitor.getFoundClients().stream();
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return Stream.empty();
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
