package com.reachauto.hkr.si.utils;

import com.reachauto.hkr.exception.HkrServerException;
import com.reachauto.hkr.si.config.WechatConfigure;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class WechatHttpClientUtil {

    public static final String WECHAT_ERROR_MSG = "微信APPTSL客户端启动失败!!!{}";

    private static CloseableHttpClient wechatHttpClient = null;

    /**
     * 启动时初始化HTTP连接池
     */
    static {
        /**
         * 微信HTTP连接池
         */
        KeyStore wechatKeyStore = getKeyStore(WechatConfigure.CERT_PATH, WechatConfigure.CERT_PASS);
        wechatHttpClient = getHttpClient(wechatKeyStore, WechatConfigure.CERT_PASS);
    }

    /**
     * 加载证书并组装连接池
     * @param keyStore
     * @param certPwd
     * @return
     */
    private static CloseableHttpClient getHttpClient(KeyStore keyStore, String certPwd) {

        CloseableHttpClient httpClient = null;
        try {
            // Trust own CA and all self-signed certs
            SSLContext sslcontext = SSLContexts
                    .custom()
                    .loadKeyMaterial(keyStore, certPwd.toCharArray())
                    .build();

            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1"},
                    null,
                    new DefaultHostnameVerifier());

            List<Header> headers = new ArrayList<>();
            headers.add(new BasicHeader("Content-Type", "text/xml"));

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslsf)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();

            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            cm.setMaxTotal(200);
            cm.setDefaultMaxPerRoute(20);

            RequestConfig requestConfig = RequestConfig
                    .custom()
                    .setConnectTimeout(3000)
                    .setConnectionRequestTimeout(1000)
                    .setSocketTimeout(3000)
                    .build();

            httpClient = HttpClients
                    .custom()
                    .setDefaultRequestConfig(requestConfig)
                    .setDefaultHeaders(headers)
                    .setConnectionManager(cm)
                    .build();

        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | UnrecoverableKeyException e) {
            log.error("", e);
            throw new HkrServerException();
        }
        return httpClient;
    }

    protected static KeyStore getKeyStore(String certPath, String pwd) {

        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            //加载本地的证书进行https加密传输
            Resource fileRource = new ClassPathResource(certPath);
            keyStore.load(
                    fileRource.getInputStream(),
                    pwd.toCharArray()
            );
            return keyStore;
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
            log.error(WECHAT_ERROR_MSG, e);
            throw new HkrServerException();
        }
    }

    /**
     * POST请求微信接口
     * @param path
     * @param xmlParam
     * @return
     */
    public static String wechatPost(String path, String xmlParam) throws IOException{
        return post(wechatHttpClient, path, xmlParam);
    }

    private static String post(HttpClient httpClient, String path, String xmlParam) throws IOException{

        HttpPost httpPost = new HttpPost(path);
        StringEntity postEntity = new StringEntity(xmlParam, "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.addHeader("User-Agent", "wxpay sdk java v1.0 ");
        httpPost.setEntity(postEntity);
        // 请求接口
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity, "UTF-8");
    }
}
