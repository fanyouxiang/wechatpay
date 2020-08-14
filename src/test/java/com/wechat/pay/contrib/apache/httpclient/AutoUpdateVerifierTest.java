package com.wechat.pay.contrib.apache.httpclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AutoUpdateVerifierTest {

  private static String mchId = "1431712302"; // 商户号
  private static String mchSerialNo = "7DBA3C715736B1EF7F3EAB795610465CE43261A9"; // 商户证书序列号
  private static String apiV3Key = "he892hd982hdh932d2g7387r3mz91rr2"; // api密钥

  // 你的商户私钥
  public static String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
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

  //测试AutoUpdateCertificatesVerifier的verify方法参数
  private static String serialNumber = "";
  private static String message = "";
  private static String signature = "";
  private CloseableHttpClient httpClient;
  private AutoUpdateCertificatesVerifier verifier;

  @Before
  public void setup() throws IOException {
    PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
        new ByteArrayInputStream(privateKey.getBytes("utf-8")));

    //使用自动更新的签名验证器，不需要传入证书
    verifier = new AutoUpdateCertificatesVerifier(
        new WechatPay2Credentials(mchId, new PrivateKeySigner(mchSerialNo, merchantPrivateKey)),
        apiV3Key.getBytes("utf-8"));

    httpClient = WechatPayHttpClientBuilder.create()
        .withMerchant(mchId, mchSerialNo, merchantPrivateKey)
        .withValidator(new WechatPay2Validator(verifier))
        .build();
  }

  @After
  public void after() throws IOException {
    httpClient.close();
  }

  @Test
  public void autoUpdateVerifierTest() throws Exception {
    assertTrue(verifier.verify(serialNumber, message.getBytes("utf-8"), signature));
  }

  @Test
  public void getCertificateTest() throws Exception {
    URIBuilder uriBuilder = new URIBuilder("https://api.mch.weixin.qq.com/v3/certificates");
    HttpGet httpGet = new HttpGet(uriBuilder.build());
    httpGet.addHeader("Accept", "application/json");
    CloseableHttpResponse response1 = httpClient.execute(httpGet);
    assertEquals(200, response1.getStatusLine().getStatusCode());
    String content = "";
    try {
      HttpEntity entity1 = response1.getEntity();
      //判断响应实体是否为空
      if (entity1 != null) {
        BufferedReader in = new BufferedReader(new InputStreamReader(response1.getEntity()
                .getContent()));
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");
        while ((line = in.readLine()) != null) {
          sb.append(line + NL);
        }
        in.close();
        content = sb.toString();
        System.out.println("content:" + content);
        EntityUtils.consume(entity1);
      }
    } finally {
      response1.close();
    }
  }


  @Test
  public void uploadImageTest() throws Exception {
    String filePath = "/your/home/hellokitty.png";

    URI uri = new URI("https://api.mch.weixin.qq.com/v3/merchant/media/upload");

    File file = new File(filePath);
    try (FileInputStream s1 = new FileInputStream(file)) {
      String sha256 = DigestUtils.sha256Hex(s1);
      try (InputStream s2 = new FileInputStream(file)) {
        WechatPayUploadHttpPost request = new WechatPayUploadHttpPost.Builder(uri)
            .withImage(file.getName(), sha256, s2)
            .build();

        CloseableHttpResponse response1 = httpClient.execute(request);
        assertEquals(200, response1.getStatusLine().getStatusCode());
        try {
          HttpEntity entity1 = response1.getEntity();
          // do something useful with the response body
          // and ensure it is fully consumed
          String s = EntityUtils.toString(entity1);
          System.out.println(s);
        } finally {
          response1.close();
        }
      }
    }
  }


  /**
   * 反序列化证书并解密
   */
  private List<X509Certificate> deserializeToCerts(byte[] apiV3Key, String body)
          throws GeneralSecurityException, IOException {
    AesUtil decryptor = new AesUtil(apiV3Key);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode dataNode = mapper.readTree(body).get("data");
    List<X509Certificate> newCertList = new ArrayList<>();
    if (dataNode != null) {
      for (int i = 0, count = dataNode.size(); i < count; i++) {
        JsonNode encryptCertificateNode = dataNode.get(i).get("encrypt_certificate");
        //解密
        String cert = decryptor.decryptToString(
                encryptCertificateNode.get("associated_data").toString().replaceAll("\"", "")
                        .getBytes("utf-8"),
                encryptCertificateNode.get("nonce").toString().replaceAll("\"", "")
                        .getBytes("utf-8"),
                encryptCertificateNode.get("ciphertext").toString().replaceAll("\"", ""));

        CertificateFactory cf = CertificateFactory.getInstance("X509");
        X509Certificate x509Cert = (X509Certificate) cf.generateCertificate(
                new ByteArrayInputStream(cert.getBytes("utf-8"))
        );
        try {
          x509Cert.checkValidity();
        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
          continue;
        }
        newCertList.add(x509Cert);
      }
    }
    return newCertList;
  }

  //解密
  public String rsaDecryptOAEP(String ciphertext) throws BadPaddingException, IOException {
    PrivateKey key = PemUtil.loadPrivateKey(
            new ByteArrayInputStream(privateKey.getBytes("utf-8")));
    try {
      Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
      cipher.init(Cipher.DECRYPT_MODE, key);

      byte[] data = Base64.getDecoder().decode(ciphertext);
      return new String(cipher.doFinal(data), "utf-8");
    } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
      throw new RuntimeException("当前Java环境不支持RSA v1.5/OAEP", e);
    } catch (InvalidKeyException e) {
      throw new IllegalArgumentException("无效的私钥", e);
    } catch (BadPaddingException | IllegalBlockSizeException e) {
      throw new BadPaddingException("解密失败");
    }
  }

  //加密
  public String rsaEncryptOAEP(String message) throws Exception {
//    String content = getCertificateTest();
    String content ="";
            List<X509Certificate> newCertList = deserializeToCerts(apiV3Key.getBytes(),content);
    try {
      Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
      cipher.init(Cipher.ENCRYPT_MODE, newCertList.get(0).getPublicKey());

      byte[] data = message.getBytes("utf-8");
      byte[] cipherdata = cipher.doFinal(data);
      return Base64.getEncoder().encodeToString(cipherdata);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      throw new RuntimeException("当前Java环境不支持RSA v1.5/OAEP", e);
    } catch (InvalidKeyException e) {
      throw new IllegalArgumentException("无效的证书", e);
    } catch (IllegalBlockSizeException | BadPaddingException e) {
      throw new IllegalBlockSizeException("加密原串的长度不能超过214字节");
    }
  }

}
