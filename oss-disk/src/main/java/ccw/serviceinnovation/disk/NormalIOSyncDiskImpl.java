package ccw.serviceinnovation.disk;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 普通IO
 */
public class NormalIOSyncDiskImpl implements SyncDisk {
    public NormalIOSyncDiskImpl() throws IOException {
        initialize();
    }

    @Override
    public void initialize() throws IOException {
        // Implement initialization logic here
    }

    @Override
    public void save(Path path, byte[] bytes, int start, int size) throws IOException {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(path.toFile(), true))) {
            os.write(bytes, start, size);
        }
    }

    @Override
    public byte[] read(Path path, int start, int size) throws IOException {
        byte[] data = new byte[size];
        try (InputStream is = new BufferedInputStream(Files.newInputStream(path.toFile().toPath()))) {
            is.skip(start);
            is.read(data);
        }
        return data;
    }

    @Override
    public void updateName(Path oldName, String newName) throws IOException {
        File oldFile = oldName.toFile();
        File newFile = new File(oldFile.getParent(), newName);
        if (!oldFile.renameTo(newFile)) {
            throw new IOException("Failed to rename file");
        }
    }

    @Override
    public void del(Path path) throws IOException {
        File file = path.toFile();
        if (!file.delete()) {
            throw new IOException("Failed to delete file");
        }
    }
}
