package ccw.serviceinnovation.common.util.http;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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

    public static void main(String[] args) {
        try {
            String s = requestTo("http://127.0.0.1:8848/nacos/v1/ns/instance?serviceName=raft-rpc&ip=127.0.0.1&port=8031&groupName=group1&metadata=group=group1", "PUT");
            System.out.println(s);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

}
