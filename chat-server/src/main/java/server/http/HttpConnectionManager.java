package server.http;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: Dunfu Peng
 * @Date: 2019/4/5 13:08
 */

@Component("httpConnectionManager")
public class HttpConnectionManager {

    private PoolingHttpClientConnectionManager manager=null;

    /*
     被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，
     并且只会被服务器调用一次，类似于Servlet的init()方法。
     被@PostConstruct修饰的方法会在构造函数之后，init()方法之前运行。
     */
    @PostConstruct
    public void init(){
        LayeredConnectionSocketFactory sslsf=null;
        try {
            sslsf=new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Registry<ConnectionSocketFactory> socketFactoryRegistry= RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https",sslsf)
                .register("http",new PlainConnectionSocketFactory())
                .build();
        manager=new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        manager.setMaxTotal(200);
        manager.setDefaultMaxPerRoute(20);
    }

    public CloseableHttpClient getHttpClient(){
        CloseableHttpClient httpClient= HttpClients.custom()
                .setConnectionManager(manager)
                .build();
        return httpClient;
    }

    public InputStream openStream(String url){
        try {
            CloseableHttpClient client=getHttpClient();
            HttpGet httpGet=new HttpGet(url);
            CloseableHttpResponse response=client.execute(httpGet);
            return response.getEntity().getContent();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean copyStream(String url, OutputStream outputStream){
        try {
            CloseableHttpClient client=getHttpClient();
            HttpGet httpGet=new HttpGet(url);
            CloseableHttpResponse response=client.execute(httpGet);
            response.getEntity().writeTo(outputStream);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
