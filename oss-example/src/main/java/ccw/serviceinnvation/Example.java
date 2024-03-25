package ccw.serviceinnvation;
import ccw.serviceinnvation.sdk.CloudCan;
import ccw.serviceinnvation.sdk.CloudCanClientBuilder;
import ccw.serviceinnvation.sdk.exception.CloudCanDownLoadException;

import java.io.File;
import java.io.IOException;
public class Example {
    public static void main(String[] args) throws IOException {
        CloudCan cloudCan = new CloudCanClientBuilder()
                .build("localhost:8080","root","123456");
        String bucketName = "test";
        String objectName = "2.jpg";
//        ossRestfulClient.createBucket(bucketName);
        cloudCan.putObject(bucketName,objectName,new File("D:\\test\\对象存储测试数据\\2.jpg"));
//        cloudCan.getObject(bucketName,objectName, "D:\\oss\\test\\");
    }
}