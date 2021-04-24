package Utils;

import jdk.nashorn.internal.runtime.options.LoggingOption;
import org.apache.log4j.Logger;
import pojo.CaseInfo;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class HttpUtils {
    private static Logger logger= Logger.getLogger(HttpUtils.class);
    public static String call(CaseInfo caseInfo, Map<String, String> headers) {

         /*
        1、准备一个默认请求头 headers
        2、判断method调用get、post、patch
        3、如果post请求，再判断json、form
        4、如果是form，把json转换成map再转成Key=Value&Key=Value
        5、调用httpUtils.post
        6、post()-->读取headers并循环所有的key，添加到请求对象中request
         */
        String responseBody = "";
        try {
            //创建一个默认请求头对象，添加"X-Lemonban-Media-Type"
            //HashMap<String, String> headers = new HashMap<>();
            //headers.put("X-Lemonban-Media-Type","lemonban.v2");
            String params = caseInfo.getParams();
            String url = caseInfo.getUrl();
            String method = caseInfo.getMethod();
            //判断请求方式，如果是post
            if ("post".equalsIgnoreCase(method)) {
                String contentType = caseInfo.getContentType();
                //判断参数类型，如果是json
                if ("json".equalsIgnoreCase(contentType)) {
                    //headers.put("Content-Type", "application/json");
                    //判断参数类型，如果是form
                } else if ("form".equalsIgnoreCase(contentType)) {
                    //json参数转成key=value参数
                    //如果是form格式，则讲params重新复制，这样才能传递过去，否则还是原来的
                    params = jsonStr2KeyValueStr(params);
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                }
                responseBody = HttpUtils.post(url, params, headers);//post
//                System.out.println(headers);
                //判断请求方式，如果是get
            } else if ("get".equalsIgnoreCase(method)) {
                responseBody = HttpUtils.get(url, headers);//get不需要参数
                //判断请求方式，如果是patch
            } else if ("patch".equalsIgnoreCase(method)) {
                //headers.put("Content-Type", "application/json");
                responseBody = HttpUtils.patch(url, params, headers);//patch
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }


    public static String get(String url, Map<String, String> headers) throws Exception {
        HttpGet get = new HttpGet(url);
        //get.setHeader("X-Lemonban-Media-Type", "lemonban.v1");
        setHeaders(headers, get);
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(get);
        return printResponse(response);


    }

    /**
     * post 请求方式，可以有两中参数格式，json form
     *
     * @param url
     * @param params
     * @param headers
     * @throws Exception
     */
    public static String post(String url, String params, Map<String, String> headers) throws Exception {
        //创建请求对象、设置请求方法、设置接口url地址
        HttpPost httpPost = new HttpPost(url);
//        httpPost.setHeader("X-Lemonban-Media-Type", "lemonban.v1");
//        httpPost.setHeader("Content-Type", "application/json");
//        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        //设置请求头
        setHeaders(headers, httpPost);
        //设置请求体
        StringEntity body = new StringEntity(params, "utf-8");
        httpPost.setEntity(body);
        //请求是需要由客户端发出去的，创建一个客户端对象，这是一个可以发送请求的客户端
        HttpClient httpClient = HttpClients.createDefault();
        //execute是httpGet的父接口，所有的请求都实现了这个接口，多态的方法，接受httpUrlRequest所有子实现类
        //相当于执行，点击发送，发送了请求之后，会给我一个响应，response
        HttpResponse response = httpClient.execute(httpPost);
        return printResponse(response);
    }


    public static String patch(String url, String params, Map<String, String> headers) throws Exception {
        HttpPatch httpPatch = new HttpPatch(url);
        setHeaders(headers, httpPatch);

        StringEntity body = new StringEntity(params, "utf-8");
        httpPatch.setEntity(body);
        HttpClient client = HttpClients.createDefault();
        HttpResponse response = client.execute(httpPatch);
        return printResponse(response);
    }

    /**
     * @param response
     * @return
     * @throws Exception
     */
    public static String printResponse(HttpResponse response) throws IOException {
        //格式化响应对象 response = 状态码+响应头+响应体
        //状态码
        int statusCode = response.getStatusLine().getStatusCode();
//        System.out.println(statusCode);
        logger.info(statusCode);
        //响应头
        Header[] headers = response.getAllHeaders();
        logger.info(Arrays.toString(headers));
        //获取响应体
        HttpEntity entity = response.getEntity();
        //将得到的响应体进行格式化并且打印
        String body = EntityUtils.toString(entity);
        logger.info(body);
        return body;
    }

    /**
     * 设置请求头
     *
     * @param headers     包含了请求头的map集合
     * @param httpRequest 请求对象
     */
    public static void setHeaders(Map<String, String> headers, HttpRequest httpRequest) {
        //获取所有请求头name
        Set<String> headerNames = headers.keySet();
        //遍历所有请求头name
        for (String headerName : headerNames) {
            //获取请求头name对应的value
            String headerValue = headers.get(headerName);
            //设置请求头name，value
            httpRequest.setHeader(headerName, headerValue);
        }
    }

    /**
     * json字符串转换成key=value
     *
     * @param params
     * @return
     */
    public static String jsonStr2KeyValueStr(String params) {
        Map<String, String> map = JSONObject.parseObject(params, Map.class);
        Set<String> keySet = map.keySet();
        String formParams = "";
        for (String key : keySet) {
            String value = map.get(key);
            formParams += key + "=" + value + "&";
        }
        return formParams.substring(0, formParams.length() - 1);
    }

}
