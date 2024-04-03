package ccw.serviceinnvation.example.bootdemo.minio;

public class StringUtils {
    public static boolean isBlank(String bucketName) {
        return bucketName == null || bucketName.trim().isEmpty();
    }

    public static boolean isNotBlank(String originalName) {
        return !isBlank(originalName);
    }
}
