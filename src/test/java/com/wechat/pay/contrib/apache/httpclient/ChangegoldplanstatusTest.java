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
import org.junit.Test;

import java.io.*;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fanyouxiang
 * @version 1.0.0
 * @name ChangegoldplanstatusTest
 * @date 2020/8/14 10:54
 * @describe
 */
public class ChangegoldplanstatusTest {

    @Test
    public void test() throws IOException {
        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/goldplan/merchants/changegoldplanstatus");
        String body = "{\"sub_mchid\":\"1234567890\",\"operation_type\":\"OPEN\"}";
        StringEntity reqEntity = new StringEntity(
                body, ContentType.create("application/json", "utf-8"));
        httpPost.setEntity(reqEntity);
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Wechatpay-Serial", "74EC5F8D4DA629AF9BF8E4517CA41BC4966996E1");
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

        String mchtId = "1431712302"; //商户号
        String mchSerialNo = "7DBA3C715736B1EF7F3EAB795610465CE43261A9"; // 商户证书序列号
        String apiV3Key = "he892hd982hdh932d2g7387r3mz91rr2"; // api密钥
        // 你的商户私钥
        String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDcbxsqWyoTzsfG\n" +
                "euqnRqnIfSZUYLJ4RCUvBT4VBtqxFllCx1y5yVjngqUekqE2NBO9uqfiFT1ZPG2I\n" +
                "KCZUXIbP/FC2icxsBWryet9WPojdLUSdz6CmmInGMZxBB+ix/ilURW2qfNaSLWWp\n" +
                "eI2Nafd8MeDKlwun+F534WTc7U4GhxB4HyASDlHkih3BETOQkkFAavapL2wy+/Z9\n" +
                "A9cMHwVTJgUcwC59Q4QgiN3fWssFSjpFv+7jFAeNthaT//uYy2iQ3/xNiSqO29ia\n" +
                "2TcrmJq943Y3I4HMzheKp4TDeo7SepqaDftG9LvvtNwzUNm9zm6TUWSOpLZgF8gr\n" +
                "Dgplnoj7AgMBAAECggEAK7ZMqMWTPoJO02kkhy9YE83df/X87iR1SzG68eh+DYfl\n" +
                "A30NQjpPSvl5TOq+1UTWdVVCXW7gz4VQIyPzFd+TTEM4rjn9M56Paq47A5s7AMEt\n" +
                "Ti4SKXcuOwMzfSHYXxOLlo5+BMTkt/AQrN6fE1/kF9i8PvBc+14hPZyQp87XHivk\n" +
                "Or6obarz8a6bFNRkH3ttzwYuJrUJTOxJB4WqbQN0roAmtQ/5eXmbpNEmnbdWRMGr\n" +
                "0rGhL498mmAcyPpWYBe8uJfC7n18OzfRYQZE+vyw8+/DbkH5+cYzbSsef0jSyGbZ\n" +
                "bwXOMmvtaFpwX4W2kqDOraREkbb4wzzcX181gEWpQQKBgQDw9mFEjGUao/iMyn0E\n" +
                "s0tfxby8YrJKr+t9GkXN7qdRsYud0+ZpUN2D0BFN22PKpiMsSIy7GPcGI1SCkF19\n" +
                "wajnFL0QxdSHdtVgk1d+XMeH85dY6Hb0woeaSustH+02/VOVNas1u+zp44KCGw34\n" +
                "ofBiTWcVlbmawugUpLC0MgTkAwKBgQDqMMOMA1lJmT1mWNg96poPrsNmYdicJ6H3\n" +
                "ndz2i73LY2sur9wVg6QRhbc9JaZlYhGwsL3hlZ+kUuL5Pgm5SBVAoaHxygqgPlFj\n" +
                "w0dL4/qtcw8P32PR6RmNpoLUZoAk1EDocCQbWD7mh0ZuEdZ/PZpq2xeSORC468pN\n" +
                "PxCTHoABqQKBgELPXVq/Po0rZ4/J2O4DtGNilRj6mIqiyj1OmooloJjDDH3/McSD\n" +
                "mlegyht3N+0JMXlmyDZcDqnSA+2Lj5B9sJIZqu5Km8nPhgwX7ktn+B7WyGDrW4gz\n" +
                "o5uBKEHDt2bmyFT3o3frmh2jah0e2TnV1Ku84FcYw3SIlAadPy1HLRn3AoGAQ7zP\n" +
                "EtR1IwCb7dqM0XvmC9s00YxDpwcfpguXOgi5YPbkraBYPDDTZ+4RTjJxqqlvDHhS\n" +
                "s8kfX9xJomX7UggzfXpC6TjTUf40dHKrUJwkib7aAHFQ4gpWRc5/0QfE0OJD0/d7\n" +
                "uh0QNIW0LWojbLJY08eDTdwLpjEtVqKDHMqZo+ECgYAau4uDTLQdsuFX8i3jsin8\n" +
                "5azLoUo1XQfITWJt0EmjV/aPt6WIum+C9hAC702g63iUP9rHGTUJNrsR+dgVQnTr\n" +
                "zFx0pz1vCsel4DGOEfCRZh0vDzBiBc2Y/A0L8saNe+048i0pz41iBr9P9vr/GCkQ\n" +
                "zcEAAPbPuYW47WqKk/UnMg==\n" +
                "-----END PRIVATE KEY-----";
        // 你的商户平台证书
        String certificate = "-----BEGIN CERTIFICATE-----\n" +
                "MIID/DCCAuSgAwIBAgIUfbo8cVc2se9/Pqt5VhBGXOQyYakwDQYJKoZIhvcNAQEL\n" +
                "BQAwXjELMAkGA1UEBhMCQ04xEzARBgNVBAoTClRlbnBheS5jb20xHTAbBgNVBAsT\n" +
                "FFRlbnBheS5jb20gQ0EgQ2VudGVyMRswGQYDVQQDExJUZW5wYXkuY29tIFJvb3Qg\n" +
                "Q0EwHhcNMTkwOTAyMDk0MjA3WhcNMjQwODMxMDk0MjA3WjCBjTETMBEGA1UEAwwK\n" +
                "MTQzMTcxMjMwMjEbMBkGA1UECgwS5b6u5L+h5ZWG5oi357O757ufMTkwNwYDVQQL\n" +
                "DDDmsZ/oi4/luLjnhp/lhpzmnZHllYbkuJrpk7booYzogqHku73mnInpmZDlhazl\n" +
                "j7gxCzAJBgNVBAYMAkNOMREwDwYDVQQHDAhTaGVuWmhlbjCCASIwDQYJKoZIhvcN\n" +
                "AQEBBQADggEPADCCAQoCggEBANxvGypbKhPOx8Z66qdGqch9JlRgsnhEJS8FPhUG\n" +
                "2rEWWULHXLnJWOeCpR6SoTY0E726p+IVPVk8bYgoJlRchs/8ULaJzGwFavJ631Y+\n" +
                "iN0tRJ3PoKaYicYxnEEH6LH+KVRFbap81pItZal4jY1p93wx4MqXC6f4XnfhZNzt\n" +
                "TgaHEHgfIBIOUeSKHcERM5CSQUBq9qkvbDL79n0D1wwfBVMmBRzALn1DhCCI3d9a\n" +
                "ywVKOkW/7uMUB422FpP/+5jLaJDf/E2JKo7b2JrZNyuYmr3jdjcjgczOF4qnhMN6\n" +
                "jtJ6mpoN+0b0u++03DNQ2b3ObpNRZI6ktmAXyCsOCmWeiPsCAwEAAaOBgTB/MAkG\n" +
                "A1UdEwQCMAAwCwYDVR0PBAQDAgTwMGUGA1UdHwReMFwwWqBYoFaGVGh0dHA6Ly9l\n" +
                "dmNhLml0cnVzLmNvbS5jbi9wdWJsaWMvaXRydXNjcmw/Q0E9MUJENDIyMEU1MERC\n" +
                "QzA0QjA2QUQzOTc1NDk4NDZDMDFDM0U4RUJEMjANBgkqhkiG9w0BAQsFAAOCAQEA\n" +
                "ZjuWhCG8wod/Zr8oh0xj1T7sQiMwtbh4XCznQ0T9gHIaI5ZWGllMk3fCoCc1Zt0q\n" +
                "OkRi9Bwv2DjVxWSxehCWA9qb4p/oQUsKqTzWStwJhnz64E3UpKYliVv19JCca6U0\n" +
                "tXx3KBWcJwxjmqDM/ptmQNec2sHJuRgZhzHnJ6a9OrEFuXtPVdo2X8TawionIEYF\n" +
                "eBkMF/SQGo9FBjyGY31vUYmJilbMCcAyiPfr8xYPXkzhT7L6cMOzF5iH/3OSjyhy\n" +
                "T7CY8YftjyThDm87DzRAwlvfFknYGavWcvjn8TVsn2dwjINYnkY6av1LmuBcWQJe\n" +
                "2nBoXH6M4zqOasZ+fLKF4A==\n" +
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
