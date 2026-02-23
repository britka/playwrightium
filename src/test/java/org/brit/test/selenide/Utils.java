package org.brit.test.selenide;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.apache.commons.io.IOUtils;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.io.FileUtils.writeStringToFile;

public class Utils {
    private static final ObjectMapper mapper = new ObjectMapper().setVisibility(FIELD, ANY);

    public static String resource(String fileName) throws IOException {
        URL resource = requireNonNull(Utils.class.getResource(fileName), () -> "Test resource not found: " + fileName);
        return IOUtils.toString(resource, UTF_8);
    }

    public static File generateTextFile() throws IOException {
        File file = File.createTempFile("playwrightium-", "-test.txt");
        String paragraph = new Faker().lorem().paragraph(6);
        writeStringToFile(file, paragraph, UTF_8);
        return file;
    }

    public static <T> T js(String jsCode, Class<T> clazz, Object... args) {
        Object viewport = executeJavaScript(jsCode, args);
        return mapper.convertValue(viewport, clazz);
    }

    @Nullable
    public static File getLastRecordedFile() throws IOException {
        Path recordsFolder = Path.of("build/video");

        if (Files.isDirectory(recordsFolder)) {
            try (Stream<Path> files = Files.list(recordsFolder)) {
                return files
                        .filter(p -> !Files.isDirectory(p))
                        .max(comparing(p -> p.toFile().lastModified()))
                        .map(Path::toFile)
                        .orElse(null);
            }
        }
        return null;
    }
}
