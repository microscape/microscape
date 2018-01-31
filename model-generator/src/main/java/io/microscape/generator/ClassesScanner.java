package io.microscape.generator;

import io.microscape.api.Documentation;
import io.microscape.api.Section;
import io.microscape.feign.FeignVisitor;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class ClassesScanner {

    private static final Logger LOGGER = Logger.getLogger(ClassesScanner.class.getName());

    private static final String EMPTY_SERVICE_ID = StringUtils.EMPTY;

    static Documentation scanJars(final Path pathToJar) {

        try (final JarFile jarFile = new JarFile(pathToJar.toFile(), false, JarFile.OPEN_READ)) {
            final String serviceId = findApplicationName(jarFile);
            final Documentation documentation = Documentation.builder().id(serviceId).build();

            final List<Section> feignClients = jarFile.stream().filter(it -> it.getName().endsWith(".class"))
                    .flatMap(it -> findFeignClientsAndReturnServiceNames(jarFile, it))
                    .map(it -> Section.builder().headline(null).text(it).build())
                    .collect(toList());

            if (!feignClients.isEmpty()) {
                final Section feignClientSection = Section.builder().headline("Feign Clients").text(null).build();
                feignClientSection.getContent().addAll(feignClients);
                documentation.getSections().add(feignClientSection);
            }
            List<Section> exchanges = findExchangeComsumerAndReturnExchangeNames(jarFile)
                    .stream()
                    .map(ex -> Section.builder().headline(null).text(ex).build())
                    .collect(toList());
            if (!exchanges.isEmpty()) {
                final Section exchangeSection = Section.builder().headline("Exchange Consumer").text(null).build();
                exchangeSection.getContent().addAll(exchanges);
                documentation.getSections().add(exchangeSection);
            }

            return documentation;
        } catch (Exception e) {
            return Documentation.builder().id(EMPTY_SERVICE_ID).build();
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

    private static Set<String> findExchangeComsumerAndReturnExchangeNames(JarFile jarFile) {
        Optional<JarEntry> applicationYaml = jarFile.stream().filter(it -> "BOOT-INF/classes/application.yml".equals(it.getName())).findFirst();
        if (applicationYaml.isPresent()) {
            return ClassesScanner.mapExchangeNamesFromYaml(jarFile, applicationYaml.get());
        }
        Optional<JarEntry> applicationProperties = jarFile.stream().filter(it -> "BOOT-INF/classes/application.properties".equals(it.getName())).findFirst();
        if (applicationProperties.isPresent()) {
            return ClassesScanner.mapExchangeNamesFromProperties(jarFile, applicationProperties.get());
        }
        return Collections.emptySet();
    }

    private static Set<String> mapExchangeNamesFromProperties(JarFile jarFile, JarEntry jarEntry) {
        try (
                final InputStream in = jarFile.getInputStream(jarEntry);
                final InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)
        ) {
            final Properties properties = new Properties();
            properties.load(reader);
            return properties.keySet()
                    .stream()
                    .filter(key -> ((String) key).startsWith("cloud.stream.bindings."))
                    .map(key -> (StringUtils.substringBetween((String) key, "cloud.stream.bindings.", "."))).collect(toSet());
        } catch (Exception e) {
            return null;
        }
    }

    private static Set<String> mapExchangeNamesFromYaml(JarFile jarFile, JarEntry jarEntry) {
        try (
                final InputStream in = jarFile.getInputStream(jarEntry);
                final InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)
        ) {
            final Yaml yaml = new Yaml();
            Iterable<Object> yamlConfig = yaml.loadAll(reader);
            return readExchangeNames(yamlConfig);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            return Collections.emptySet();
        }
    }

    private static Set<String> readExchangeNames(Iterable<Object> yamlConfig) {
        Optional<Object> spring = StreamSupport.stream(yamlConfig.spliterator(), false)
                .filter(it -> ((Map) it).get("spring") != null).map(it -> ((Map) it).get("spring")).findFirst();
        if (spring.isPresent()) {
            Map springMap = (Map) spring.get();
            Map cloud = (Map) springMap.get("cloud");
            if (cloud != null) {
                Map stream = (Map) cloud.get("stream");
                if (stream != null) {
                    Map bindings = (Map) stream.get("bindings");
                    return bindings.keySet();
                }
            }
        }


        return Collections.emptySet();
    }

    private static String findApplicationName(final JarFile jarFile) {
        final Optional<String> yml = jarFile.stream().filter(it -> "BOOT-INF/classes/application.yml".equals(it.getName()))
                .map(it -> ClassesScanner.mapApplicationNameFromYaml(jarFile, it)).findFirst();

        final Optional<String> properties = jarFile.stream().filter(it -> "BOOT-INF/classes/application.properties".equals(it.getName()))
                .map(it -> ClassesScanner.mapApplicationNameFromProperties(jarFile, it)).findFirst();

        return yml.orElse(properties.orElse(EMPTY_SERVICE_ID));
    }

    private static String mapApplicationNameFromYaml(final JarFile jarFile, final JarEntry jarEntry) {
        try (
                final InputStream in = jarFile.getInputStream(jarEntry);
                final InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)
        ) {
            final Yaml yaml = new Yaml();
            Iterable<Object> yamlConfig = yaml.loadAll(reader);
            return readApplicationName(yamlConfig);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            return null;
        }
    }

    private static String readApplicationName(Iterable<Object> yamlConfig) {
        Optional<Object> spring = StreamSupport.stream(yamlConfig.spliterator(), false)
                .filter(it -> ((Map) it).get("spring") != null).map(it -> ((Map) it).get("spring")).findFirst();
        if (spring.isPresent()) {
            Map springMap = (Map) spring.get();
            Map application = (Map) springMap.get("application");
            if (application != null) {
                return (String) application.get("name");
            }
        }


        return "";//config.getSpring().getApplication().getName();
    }

    private static String mapApplicationNameFromProperties(JarFile jarFile, JarEntry jarEntry) {
        try (
                final InputStream in = jarFile.getInputStream(jarEntry);
                final InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)
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
