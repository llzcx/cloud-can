package ccw.serviceinnovation.oss.manager.test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

public class HelpTest {
    public static void main(String[] args) throws IOException {
        Path path1 = Paths.get("D:\\oss\\n1");
        Path path2 = Paths.get("D:\\oss\\n2");
        Path path3 = Paths.get("D:\\oss\\n3");
        Path test = Paths.get("D:\\oss\\testdata");

        long size = calculateTotalSize(path1, path2, path3);
        long testSize = calculateTotalSize(test);
        deleteFolders(path1,path2,path3);

        System.out.println("Rate is:"+size*1.0/testSize);
    }
    public static long calculateTotalSize(Path... paths) throws IOException {
        long totalSize = 0;
        for (Path path : paths) {
            totalSize += Files.walk(path)
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    })
                    .sum();
        }
        return totalSize;
    }

    public static void deleteFolders(Path... paths) throws IOException {
        for (Path path : paths) {
            Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
}
