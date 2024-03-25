package ccw.serviceinnvation.sdk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Util {
    // 删除文件夹下所有文件
    public static void deleteAllFilesInFolder(String folderPath) throws IOException {
        Path start = Paths.get(folderPath);
        try (Stream<Path> paths = Files.walk(start)) {
            paths.filter(Files::isRegularFile)
                    .forEach(Util::deleteFile);
        }
        Files.deleteIfExists(start);
    }

    // 删除单个文件
    private static void deleteFile(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Unable to delete file: " + path + ", Reason: " + e.getMessage());
        }
    }
}
