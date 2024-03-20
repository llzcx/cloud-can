package ccw.serviceinnvation;



import ccw.serviceinnovation.hash.checksum.Crc32EtagHandlerAdapter;
import ccw.serviceinnovation.hash.checksum.EtagHandler;
import ccw.serviceinnovation.hash.directcalculator.EtagDirectCalculator;
import ccw.serviceinnovation.hash.directcalculator.MD5EtagDirectCalculatorAdapter;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;

public class OssRestfulClient {
    private final String url;

    private final String userName;

    private final String password;

    private Headers headers;

    public final EtagHandler etagHandler = new Crc32EtagHandlerAdapter();

    public final EtagDirectCalculator etagDirectCalculator = new MD5EtagDirectCalculatorAdapter();

    private final OkHttpClient client;

    private MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    private MediaType STREAM = MediaType.parse("application/octet-stream");

    public OssRestfulClient(String host, Integer port, String userName, String password) {
        this.url = "http://" + host + ":" + port + "/";
        this.userName = userName;
        this.password = password;
        client = new OkHttpClient();
    }

    private Response getResponse(String url,String way ,Headers headers, RequestBody requestBody) throws IOException {
        // Create HTTP request
        Request.Builder builder = new Request.Builder();
        if(headers!=null){
            builder.headers(headers);
        }
        if(url!=null){
            builder.url(url);
        }
        if("get".equals(way)){
            builder.get();
        }else if("post".equals(way)){
            builder.post(requestBody);
        }else if("put".equals(way)){
            builder.put(requestBody);
        }else if("delete".equals(way)){
            builder.delete(requestBody);
        }else if("patch".equals(way)){
            builder.patch(requestBody);
        }
        Response response = client.newCall(builder.build()).execute();
        return response;
    }

    private JSONObject sync(String url,String way ,Headers headers, RequestBody requestBody) throws IOException {
        Response response = getResponse(url, way, headers, requestBody);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected response code: " + response);
        }
        JSONObject res = parseObject(response.body().string());
        Integer code = res.getInteger("code");
        String msg = res.getString("msg");
        if(code==200){
            System.out.println("请求成功 "+url);
            try {
                return parseObject(res.getString("data"));
            }catch (JSONException e){
                return res;
            }
        }else{
            throw new RuntimeException("error code:"+code+";"+msg);
        }
    }

    public void login() throws IOException {
        String url = this.url + "/user/login";
        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("username", userName);
        loginParams.put("password", password);

        RequestBody requestBody = RequestBody.create(JSON_TYPE, toJSONString(loginParams));
        JSONObject response = sync(url, "post",null, requestBody);

        if (response != null) {
            String token = response.getString("token");
            System.out.println("令牌:"+token);
            this.headers = new Headers.Builder().add("Authorization",token).build();
        }

    }

    public void upload(String bucketName, String path) throws IOException {
        Path pat = Paths.get(path);
        byte[] bytes = Files.readAllBytes(pat);
        String url = this.url + "/ossObject/upload";
        String objectName = pat.getFileName().toString();
        String etag = etagDirectCalculator.get(pat);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("bucketName", bucketName)
                .addFormDataPart("objectName", objectName)
                .addFormDataPart("etag", etag)
                .addFormDataPart("file", objectName,
                        RequestBody.create(STREAM, bytes))
                .build();
        JSONObject sync = sync(url, "put",headers, requestBody);
        System.out.println(sync);
    }

    public List<String> bucketList(){

        return null;
    }

    public void createBucket(String bucketName) throws IOException {
        String url = this.url+"/bucket/createBucket";
        Map<String, Object> addBucketParams = new HashMap<>();
        addBucketParams.put("bucketName", bucketName);
        addBucketParams.put("bucketAcl", 5); // 桶权限
        addBucketParams.put("secret", 0); // 无加密
        RequestBody requestBody = RequestBody.create(JSON_TYPE, toJSONString(addBucketParams));
        JSONObject response = sync(url, "post",headers, requestBody);
        System.out.println("桶创建成功");
    }

    private static void printResponse(Response response) throws IOException {
        System.out.println("Response Code: " + response.code());
        Headers headers = response.headers();
        for (String name : headers.names()) {
            System.out.println(name + ": " + headers.get(name));
        }
        System.out.println("Response Body: " + response.body().string());
    }

    public void download(String bucketName, String objectName, String path) throws IOException {
        String url = this.url +"/ossObject/download?objectName=" +objectName + "&bucketName=" + bucketName;
        Response response = getResponse(url, "get", headers, null);
        Path parent = Paths.get(path);
        if(!Files.exists(parent)){
            Files.createDirectories(parent);
        }
        Path fileName = Paths.get(parent.toString(),objectName);
        if(Files.exists(fileName)){
            Files.delete(fileName);
        }
        Files.createFile(fileName);
        OutputStream outputStream = Files.newOutputStream(fileName);
        ResponseBody body = response.body();
        if (body != null) {
            try (InputStream inputStream = body.byteStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } else {
            throw new IOException("Response body is null");
        }
    }



}
