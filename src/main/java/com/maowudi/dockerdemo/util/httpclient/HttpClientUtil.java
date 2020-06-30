package com.maowudi.dockerdemo.util.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

public class HttpClientUtil {



    public static void httpPost(String url) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity!=null) {
                System.out.println(response.getStatusLine());
                System.out.println(EntityUtils.toString(entity));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(response!=null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void upload(String url, File file, String filename) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000).build();
            httppost.setConfig(requestConfig);
            FileBody bin = new FileBody(file);
            StringBody comment = new StringBody(filename, ContentType.TEXT_PLAIN);
            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", bin).addPart("filename", comment).build();
            httppost.setEntity(reqEntity);
            System.out.println("executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    String responseEntityStr = EntityUtils.toString(response.getEntity());
                    System.out.println(responseEntityStr);
                }
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        File file = new File("C:\\Users\\HD\\IdeaProjects\\docker\\src\\main\\docker\\dockerfile.tar.gz");
        HttpClientUtil.upload("http://127.0.0.1:80/uploadDockerfileGz?tag=remotedeploytest",file,"dockerfile.tar.gz");
    }
}
