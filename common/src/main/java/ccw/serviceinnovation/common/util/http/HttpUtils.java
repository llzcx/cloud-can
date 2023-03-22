package ccw.serviceinnovation.common.util.http;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 陈翔
 */
public class HttpUtils {
    public static String request(String url) throws Exception {
        return requestTo(url, "GET");
    }

    public static String requestTo(String url,String method) throws Exception {
        System.out.println("url:"+url);
        URL localURL = new URL(url);
        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
        httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestMethod(method);
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuilder resultBuffer = new StringBuilder();
        String tempLine = null;
        //响应失败
        if (httpURLConnection.getResponseCode() >= 300) {
            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }
        try {
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return resultBuffer.toString();
    }

    public static String getParamByUrl(String url, String name) {
        url += "&";
        String pattern = "(\\?|&){1}#{0,1}" + name + "=[a-zA-Z0-9]*(&{1})";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(url);
        if (matcher.find()) {
            return matcher.group(0).split("=")[1].replace("&", "");
        } else {
            return "";
        }
    }

    public static String getLastPathParams(String url){
        String name;
        int index1 = url.lastIndexOf("/");
        int index2 = url.lastIndexOf("?");
        System.out.println(index1 + " " + index2);
        if(index2==-1){
            name = url.substring(index1+1);
        }else{
            name = url.substring(index1+1,index2);
        }
        return name;
    }
    public static String[] getPathParams(String urlPath){
        String[] split = new String[0];
        try {
            URL url = new URL(urlPath);
            return url.getPath().substring(1).split("/");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return split;
    }
    public static String[] getPathParams(URI url){
        String[] split = new String[0];
        try {
            return url.getPath().substring(1).split("/");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return split;
    }
    public static void getSftpInfo() throws Exception {
        String ftpAddr = "http://blog.csdn.net:60000/jungsagacity/article/details/7645580";  //address中包含用户名和密码
        try {
            URL url = new URL(ftpAddr);
            String userInfo = url.getUserInfo();
            System.err.println("用户信息：" + userInfo);
            if (userInfo != null) {
                int index = userInfo.indexOf(":");
                String userName = userInfo.substring(0, index);
                System.err.println("用户名：" + userName);
                String password = userInfo.substring(index + 1);
                System.err.println("密码：" + password);
            }
            int port = url.getPort();
            System.err.println("端口号：" + port);
            String host = url.getHost();
            System.err.println("host：" + host);
            String path = url.getPath();
            System.err.println("远程路径：" + path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            System.out.println(Arrays.toString(getPathParams("http://127.0.0.1:8848/object/download/mybucket/myobject?serviceName=raft-rpc&ip=127.0.0.1&port=8031&metadata=group=group1,port=8021")));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

}
