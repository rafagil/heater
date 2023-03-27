package app.osmosi.heater.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

public class FileUtils {
    public static Stream<String> read(File file) throws IOException {
        return Files.lines(file.toPath(), StandardCharsets.UTF_8);
    }

    public static void write(File file, String contents) throws IOException {
        Files.writeString(file.toPath(), contents);
    }

    public static Stream<String> read(String path) throws IOException {
        File file = new File(path);
        return read(file);
    }

    public static void write(String path, String contents) throws IOException {
        File file = new File(path);
        write(file, contents);
    }
}
