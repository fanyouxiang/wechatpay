package com.wechat.pay.contrib.apache.httpclient;

import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.*;
import java.security.PrivateKey;

/**
 * @author fanyouxiang
 * @version 1.0.0
 * @name ChangegoldplanstatusTest
 * @date 2020/8/14 10:54
 * @describe
 */
public class Test {

    @org.junit.Test
    public void test() throws IOException {
        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/goldplan/merchants/changegoldplanstatus");
        String body = "{\"sub_mchid\":\"1234567890\",\"operation_type\":\"OPEN\"}";
        StringEntity reqEntity = new StringEntity(
                body, ContentType.create("application/json", "utf-8"));
        httpPost.setEntity(reqEntity);
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Wechatpay-Serial", "***********");
        //构建请求对象
        CloseableHttpClient httpClient = buildClient();
                CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity httpEntity = response.getEntity();
        if (httpEntity != null) {
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity()
                    .getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            System.out.println(sb);
        }
    }

    /**
     * 创建连接
     * @return
     */
    private  CloseableHttpClient buildClient() throws UnsupportedEncodingException {

        String mchtId = "**********"; //商户号
        String mchSerialNo = "**********"; // 商户证书序列号
        String apiV3Key = "**********"; // api密钥
        // 你的商户私钥
        String privateKey = "-----BEGIN PRIVATE KEY-----\n" +

                "-----END PRIVATE KEY-----";
        // 你的商户平台证书
        String certificate = "-----BEGIN CERTIFICATE-----\n" +

                "-----END CERTIFICATE-----";

        //商户私钥
        PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
                new ByteArrayInputStream(privateKey.getBytes("utf-8")));
        //使用自动更新的签名验证器，不需要传入证书
        AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
                new WechatPay2Credentials(mchtId, new PrivateKeySigner(mchSerialNo, merchantPrivateKey)),
                apiV3Key.getBytes("utf-8"));
        //创建httpclient对象
        return WechatPayHttpClientBuilder.create()
                .withMerchant(mchtId, mchSerialNo, merchantPrivateKey)
                .withValidator(new WechatPay2Validator(verifier))
                .build();
    }

}
