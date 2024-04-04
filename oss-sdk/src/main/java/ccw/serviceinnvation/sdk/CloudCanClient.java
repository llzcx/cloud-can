package ccw.serviceinnvation.sdk;

import ccw.serviceinnovation.hash.directcalculator.EtagDirectCalculator;
import ccw.serviceinnovation.hash.directcalculator.MD5EtagDirectCalculatorAdapter;
import ccw.serviceinnvation.sdk.exception.CloudCanDownLoadException;
import ccw.serviceinnvation.sdk.util.CloudCanReqUtil;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static ccw.serviceinnvation.sdk.util.HTTPConst.JSON_TYPE;
import static ccw.serviceinnvation.sdk.util.HTTPConst.STREAM;
import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;

public class CloudCanClient implements CloudCan {
    private final String url;
    private final String userName;
    private final String password;
    private Headers headers;
    public final EtagDirectCalculator etagDirectCalculator;
    private final CloudCanReqUtil cloudCanReqUtil;

    public CloudCanClient(String endpoint, String username, String password)  {
        this.url = "http://" + endpoint + "/";
        this.userName = username;
        this.password = password;
        cloudCanReqUtil = new CloudCanReqUtil();
        etagDirectCalculator = new MD5EtagDirectCalculatorAdapter();
        try{
            enableLogin();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public CloudCanClient(String endpoint,String token)  {
        this.url = "http://" + endpoint + "/";
        this.userName = null;
        this.password = null;
        cloudCanReqUtil = new CloudCanReqUtil();
        etagDirectCalculator = new MD5EtagDirectCalculatorAdapter();
        try{
            this.headers = new Headers.Builder().add("Authorization", token).build();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void login() throws IOException {
        String url = this.url + "/user/login";
        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("username", userName);
        loginParams.put("password", password);

        RequestBody requestBody = RequestBody.create(JSON_TYPE, toJSONString(loginParams));
        JSONObject response;
        response = sync(url, "post", null, requestBody);
        if (response != null) {
            String token = response.getString("token");
            System.out.println("令牌:\n" + token);
            this.headers = new Headers.Builder().add("Authorization", token).build();
        }
    }

    private JSONObject sync(String url, String way, Headers headers, RequestBody requestBody, int times) throws IOException {
        Response response = cloudCanReqUtil.getResponse(url, way, headers, requestBody);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected response code: " + response);
        }
        JSONObject res = parseObject(response.body().string());
        Integer code = res.getInteger("code");
        String msg = res.getString("msg");
        if (code == 200) {
            System.out.println("请求成功 " + url);
            try {
                return parseObject(res.getString("data"));
            } catch (JSONException e) {
                return res;
            }
        } else if ((code == 3014 || code == 3015) && times == 1) {
            enableLogin();
            return sync(url, way, this.headers, requestBody, times + 1);
        } else {
            throw new RuntimeException("error code:" + code + ";" + msg);
        }
    }

    public JSONObject sync(String url, String way, Headers headers, RequestBody requestBody) throws IOException {
        return sync(url, way, headers, requestBody, 1);
    }

    private void enableLogin() throws IOException {
        login();
    }

    @Override
    public void putObject(String bucketName, String objName, File file) throws IOException {
        Path pat = Paths.get(file.getAbsolutePath());
        byte[] bytes = Files.readAllBytes(pat);
        putObject(bucketName,objName,bytes);
    }

    @Override
    public void putObject(String bucketName, String objName, InputStream inputStream) throws IOException {
        String url = this.url + "/ossObject/upload";
        byte[] byteArray = toByteArray(inputStream);
        putObject(bucketName,objName,byteArray);
    }
    private static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384]; // 使用一个适当大小的缓冲区
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    @Override
    public void putObject(String bucketName, String objName, byte[] bytes) throws IOException {
        String url = this.url + "/ossObject/upload";
        String etag = etagDirectCalculator.get(bytes);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("bucketName", bucketName)
                .addFormDataPart("objectName", objName)
                .addFormDataPart("etag", etag)
                .addFormDataPart("file", objName,
                        RequestBody.create(STREAM, bytes))
                .build();
        JSONObject sync = sync(url, "put", headers, requestBody);
    }

    @Override
    public void createBucket(String bucketName) throws IOException {
        String url = this.url + "/bucket/createBucket";
        Map<String, Object> addBucketParams = new HashMap<>();
        addBucketParams.put("bucketName", bucketName);
        addBucketParams.put("bucketAcl", 5); // 桶权限
        addBucketParams.put("secret", 0); // 无加密
        RequestBody requestBody = RequestBody.create(JSON_TYPE, toJSONString(addBucketParams));
        JSONObject response = sync(url, "post", headers, requestBody);
        System.out.println("桶创建成功");
    }

    @Override
    public void getObject(String bucketName, String objName, OutputStream outputStream) throws IOException, CloudCanDownLoadException {
        String url = this.url + "/ossObject/download?objectName=" + objName + "&bucketName=" + bucketName;
        Response response = cloudCanReqUtil.getResponse(url, "get", headers, null);
        if(response.code() != 200) throw new CloudCanDownLoadException();
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

    @Override
    public void getObject(String bucketName, String objName, String path) throws IOException, CloudCanDownLoadException {
        Path parent = Paths.get(path);
        if (!Files.exists(parent)) Files.createDirectories(parent);
        Path fileName = Paths.get(parent.toString(), objName);
        if (Files.exists(fileName)) Files.delete(fileName);
        Files.createFile(fileName);
        OutputStream outputStream = Files.newOutputStream(fileName);
        getObject(bucketName, objName, outputStream);
    }

    @Override
    public void deletetAll(String bucketName) throws IOException {
        String url = this.url + "/ossObject/deleteAll?"+"bucketName=" + bucketName;
        cloudCanReqUtil.getResponse(url, "delete", headers, null);
    }

    @Override
    public JSONObject listObject(String bucketName,Integer pageNum,Integer pageSIze) throws IOException {
        String url = this.url + "/ossObject/listObjects?"+"bucketName=" + bucketName + "&pagenum="+pageNum+"&size="+pageSIze;;
        return sync(url, "get", headers, null);

    }


}
