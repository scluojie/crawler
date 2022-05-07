package com.hxcy.crawler.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author kevin
 * @date 2022/5/5
 * @desc
 */
public class HttpClientUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    /**
     * @Description : http Get代理请求
     * @Author : ljw
     * @Date : 2022/5/5
     */
    public static String sendGetForProxy(String url) {
        String result = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            HttpGet httpGet=new HttpGet(url);
            // 创建httpClient实例
            httpClient= HttpClients.createDefault();
            //设置代理IP、端口 8123
            HttpHost proxy=new HttpHost("127.0.0.1",8123,"http");
            //也可以设置超时时间
            // RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).setConnectTimeout(3000).setSocketTimeout(3000).setConnectionRequestTimeout(3000).build();
            RequestConfig requestConfig=RequestConfig.custom().setProxy(proxy).build();
            httpGet.setConfig(requestConfig);
            //设置请求头消息
            httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
            // 执行http get请求  也可以使用psot
            response=httpClient.execute(httpGet);
            // 获取返回实体
            if (response != null){
                HttpEntity entity = response.getEntity();
                if (entity != null){
                    String msg = EntityUtils.toString(entity, "utf-8");
                    result = msg;
                }
            }
        }catch (Exception e){
            logger.error("http代理请求异常", e);
        }finally {
            try {
                //关闭response
                response.close();
                //关闭httpClient
                httpClient.close();
            } catch (IOException e) {
                logger.error("关闭资源异常", e);
            }
        }
        return result;
    }
}
