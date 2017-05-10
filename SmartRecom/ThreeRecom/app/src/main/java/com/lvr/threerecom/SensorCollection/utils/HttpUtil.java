package com.lvr.threerecom.sensorcollection.utils;

import android.util.Log;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


public class HttpUtil {
    private final static Logger log = Logger.getLogger(HttpUtil.class);

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     * @throws Exception
     */
    public static String sendGet(String url, String param) throws Exception {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            // System.out.println("发送GET请求出现异常！" + e);
            // e.printStackTrace();
            log.error("发送GET请求出现异常！", e);
            throw e; // 向上抛出异常
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                // e2.printStackTrace();
                log.error("使用finally块来关闭输入流！", e2);
                throw e2; // 向上抛出异常
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     * @throws Exception
     */
    public static String sendPost(String url, String param) throws Exception {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            // System.out.println("发送 POST 请求出现异常！"+e);
            // e.printStackTrace();
            log.error("发送 POST 请求出现异常！", e);
            throw e; // 向上抛出异常
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ex.printStackTrace();
                log.error("使用finally块来关闭输入流！", ex);
                throw ex; // 向上抛出异常
            }
        }
        return result;
    }


    public static String get(String url, Map<String, String> parameters) throws Exception {
        return get(url, parameters, 50000);
    }

    /**
     * httpclient get方式发送请求,默认超时时间5000毫秒
     *
     * @param timeout timeout 超时时间 （单位毫秒）
     */
    public static String get(String url, Map<String, String> parameters, int timeout) throws Exception {
        HttpClient client = HttpClientFactory.getInstance().createHttpClient();
        GetMethod method = null;
        try {
            StringBuilder paramBd = new StringBuilder(url.trim()).append("?");
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                paramBd.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            }
            url = paramBd.toString();
            url = url.substring(0, url.length() - 1);

            method = new GetMethod(url);

            decorateMethod(method, timeout);
            int statusCode = client.executeMethod(method);
            if (statusCode != 200) {
                throw new Exception("return statusCode is : " + statusCode);
            }
            return method.getResponseBodyAsString();
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            throw e;
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
            client.getHttpConnectionManager().closeIdleConnections(0);
        }
    }

    /**
     * httpclient Post方式发送请求,默认超时时间5000毫秒
     *
     * @param url   请求地址
     * @param bodys 请求body的Map
     */
    public static String post(String url, Map<String, String> bodys) {
        return post(url, bodys, 5000);
    }

    /**
     * httpclient Post方式发送请求,默认超时时间5000毫秒
     *
     * @param url     请求地址
     * @param bodys   请求body的Map
     * @param timeout timeout 超时时间 （单位毫秒）
     */
    public static String post(String url, Map<String, String> bodys, int timeout) {
        PostMethod method = new PostMethod(url);
        HttpClient client = HttpClientFactory.getInstance().createHttpClient();
        try {
            for (Map.Entry<String, String> entry : bodys.entrySet()) {
                method.addParameter(entry.getKey(), entry.getValue());
            }
            decorateMethod(method, timeout);
            int statusCode = client.executeMethod(method);
            if (statusCode != 200) {
                Log.e(Constant.tag, String.valueOf(statusCode));
            }
            String result = method.getResponseBodyAsString();
            return result;
        } catch (Exception e) {
            Log.e(Constant.tag, e.toString());
            e.printStackTrace();
        } finally {
            //每次连接网络后都要记得释放资源
            method.releaseConnection();
            client.getHttpConnectionManager().closeIdleConnections(0);
        }
        return null;
    }

    private static void decorateMethod(HttpMethod method, int timeout) {
        method.setRequestHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        method.setRequestHeader("connection", "Keep-Alive");
        method.setRequestHeader("user-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        HttpMethodParams param = method.getParams();
        param.setContentCharset("UTF-8");
        param.setParameter("http.socket.timeout", timeout);
    }

    public static class HttpClientFactory {
        private static final int CONNECTION_TIMEOUT_MILLIS = 100000;
        private static final int SO_TIMEOUT_MILLIS = 100000;
        private static final int DEFAULT_MAX_CONNECTIONS_PER_HOST = 50000;
        private static final int MAX_TOTAL_CONNECTIONS = 50000;

        private MultiThreadedHttpConnectionManager connectionManager;

        private static HttpClientFactory httpClientFactory = new HttpClientFactory();

        public static HttpClientFactory getInstance() {
            return httpClientFactory;
        }

        private HttpClientFactory() {
            connectionManager = new MultiThreadedHttpConnectionManager();
            HttpConnectionManagerParams connectionManagerParams = connectionManager.getParams();
            connectionManagerParams.setConnectionTimeout(CONNECTION_TIMEOUT_MILLIS);
            connectionManagerParams.setSoTimeout(SO_TIMEOUT_MILLIS);
            connectionManagerParams.setDefaultMaxConnectionsPerHost(DEFAULT_MAX_CONNECTIONS_PER_HOST);
            connectionManagerParams.setMaxTotalConnections(MAX_TOTAL_CONNECTIONS);
        }

        public HttpClient createHttpClient() {
            return new HttpClient(connectionManager);
        }
    }
}