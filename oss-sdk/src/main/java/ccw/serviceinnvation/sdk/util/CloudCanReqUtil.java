package ccw.serviceinnvation.sdk.util;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CloudCanReqUtil {

    private final OkHttpClient client;

    public CloudCanReqUtil() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public Response getResponse(String url, String way, Headers headers, RequestBody requestBody) throws IOException {
        // Create HTTP request
        Request.Builder builder = new Request.Builder();
        if (headers != null) {
            builder.headers(headers);
        }
        if (url != null) {
            builder.url(url);
        }
        if ("get".equals(way)) {
            builder.get();
        } else if ("post".equals(way)) {
            builder.post(requestBody);
        } else if ("put".equals(way)) {
            builder.put(requestBody);
        } else if ("delete".equals(way)) {
            builder.delete(requestBody);
        } else if ("patch".equals(way)) {
            builder.patch(requestBody);
        }
        return client.newCall(builder.build()).execute();
    }

    private static void printResponse(Response response) throws IOException {
        System.out.println("Response Code: " + response.code());
        Headers headers = response.headers();
        for (String name : headers.names()) {
            System.out.println(name + ": " + headers.get(name));
        }
        System.out.println("Response Body: " + response.body().string());
    }
}
