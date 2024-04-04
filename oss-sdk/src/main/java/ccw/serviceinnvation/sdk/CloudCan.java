package ccw.serviceinnvation.sdk;

import ccw.serviceinnvation.sdk.exception.CloudCanDownLoadException;
import com.alibaba.fastjson.JSONObject;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface CloudCan {
    void putObject(String bucketName, String objName, File file) throws IOException;

    void putObject(String bucketName, String objName, InputStream inputStream) throws IOException;
    void putObject(String bucketName, String objName, byte[] bytes) throws IOException;
    void createBucket(String bucketName) throws IOException;

    void getObject(String bucketName, String objName, OutputStream outputStream) throws IOException, CloudCanDownLoadException;

    void getObject(String bucketName, String objName, String path) throws IOException, CloudCanDownLoadException;

    void deletetAll(String bucketName) throws IOException;

    JSONObject listObject(String bucketName, Integer pageNum, Integer pageSIze) throws IOException;
}
