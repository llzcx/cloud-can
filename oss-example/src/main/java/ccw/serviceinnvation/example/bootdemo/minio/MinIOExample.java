package ccw.serviceinnvation.example.bootdemo.minio;


import io.minio.MinioClient;

public class MinIOExample {
    public static void main(String[] args) {
        StorageProperty storageProperty = new StorageProperty();
        storageProperty.setUrl("http://localhost:9000");
        storageProperty.setBucketName("test");
        storageProperty.setAccessKey("vYi3OcXk18vwI9oJ8ei9");
        storageProperty.setSecretKey("xlWAlPZ7G5xHS9npYD8ryETTweXPlpETwtFVlJeg");
        MinioClientConfig minioClientConfig = new MinioClientConfig();
        minioClientConfig.storageProperty = storageProperty;
        minioClientConfig.init();

        MinioClient minioClient = MinioClientConfig.getMinioClient();
        MinioUtil minioUtil = new MinioUtil();
    }
}
