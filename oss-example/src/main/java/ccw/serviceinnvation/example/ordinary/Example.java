package ccw.serviceinnvation.example.ordinary;

import ccw.serviceinnvation.sdk.CloudCan;
import ccw.serviceinnvation.sdk.CloudCanClientBuilder;
import ccw.serviceinnvation.sdk.exception.CloudCanDownLoadException;

import java.io.File;
import java.io.IOException;
public class Example {
    public static void main(String[] args) throws IOException, CloudCanDownLoadException {
        CloudCan cloudCan = new CloudCanClientBuilder()
                .build("localhost:8080","eyJ0eXBlIjoiSnd0IiwiYWxnIjoiSFMyNTYiLCJ0eXAiOiJKV1QifQ.eyJjdXJyZW50VGltZSI6MTcxMjE3MzE3MDMyNCwicGFzc3dvcmQiOiIxMjM0NTYiLCJpZCI6IjEiLCJleHAiOjE3MTIxNzMxNzAsInVzZXJuYW1lIjoicm9vdCJ9.bzmL0fcIvfJbAcryj2uxecrzMYBYXJa9WYGTz7u5ejM");
        String bucketName = "test";
        String objectName = "2.jpg";
//        cloudCan.createBucket(bucketName);
        cloudCan.putObject(bucketName,objectName,new File("D:\\test\\对象存储测试数据\\2.jpg"));
        cloudCan.getObject(bucketName,objectName, "D:\\oss\\test\\");
//        System.out.println("Before："+cloudCan.listObject(bucketName, 1, 10));
//        cloudCan.deletetAll(bucketName);
//        System.out.println("After："+cloudCan.listObject(bucketName, 1, 10));
    }
}