package ccw.serviceinnovation.common.util.file;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;


import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;
import org.apache.commons.compress.archivers.zip.ScatterZipOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;


public class ScatterSample {
    private String rootPath;


    ParallelScatterZipCreator scatterZipCreator = new ParallelScatterZipCreator();
    // ParallelScatterZipCreator api says:
    // 注意这个类不保证写入到输出文件的顺序。需要保持特定顺序的（manifests，文件夹）必须使用这个类的客户类进行处理
    // 通常的做法是 在调用这个类的writeTo方法前把这些东西写入到ZipArchiveOutputStream
    ScatterZipOutputStream dirs = ScatterZipOutputStream
            .fileBased(File.createTempFile("whatever-preffix", ".whatever"));


    public ScatterSample(String rootPath) throws IOException {
        this.rootPath = rootPath;
    }


    public void addEntry(final ZipArchiveEntry zipArchiveEntry, final InputStreamSupplier streamSupplier)
            throws IOException {
        if (zipArchiveEntry.isDirectory() && !zipArchiveEntry.isUnixSymlink()) {
            dirs.addArchiveEntry(ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipArchiveEntry, streamSupplier));
        } else {
            scatterZipCreator.addArchiveEntry(zipArchiveEntry, streamSupplier);
        }
    }


    public void writeTo(final ZipArchiveOutputStream zipArchiveOutputStream)
            throws IOException, ExecutionException, InterruptedException {
        dirs.writeTo(zipArchiveOutputStream);
        dirs.close();
        scatterZipCreator.writeTo(zipArchiveOutputStream);
    }


    public String getRootPath() {
        return rootPath;
    }


    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }


}