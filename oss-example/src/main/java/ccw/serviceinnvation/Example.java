package ccw.serviceinnvation;

import ccw.serviceinnovation.hash.checksum.EtagHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.Checksum;

public class Example {
    public static void main(String[] args) throws IOException {
        OssRestfulClient ossRestfulClient = new OssRestfulClient("localhost", 8080, "root", "123456");
        EtagHandler etagHandler = ossRestfulClient.etagHandler;
        ossRestfulClient.login();
        String bucketName = "test";
        String objectName = "2.jpg";
        Path path = Paths.get("D:\\IDEProJect\\springcloudalibaba\\oss-example\\src\\main\\java\\ccw\\serviceinnvation\\2.jpg");
//        ossRestfulClient.createBucket(bucketName);
        ossRestfulClient.upload(bucketName,path.toString());
        Checksum deserialize = etagHandler.deserialize("0");
        byte[] bytes = Files.readAllBytes(path);
        deserialize.update(bytes,0,bytes.length);
        System.out.println(deserialize.getValue());
        ossRestfulClient.download(bucketName,objectName, "D:\\oss\\test\\");

    }
}