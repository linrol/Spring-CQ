package xin.lz1998.cq.plugin.forward;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * HTTP请求工具类
 * 
 * @author luolin
 *
 */
public class HttpUtil {

	private static final int TIMEOUT_IN_MILLIONS = 5000;
	
	private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	/**
	 * 同步的Get请求
	 * @param urlStr 请求的URL地址
	 * @param entityClass 返回的实体类型
	 * @return 返回任意类型，依靠传入的实体原型
	 * @throws Exception
	 */
	public static Object doGetSync(String urlStr, Class<?> entityClass) throws Exception{
		String result = doGet(urlStr);
		if (StringUtils.isEmpty(result)) {
			throw new Exception("request is null url:" + urlStr);
		}
		Object httpObj = JSON.parseObject(result, entityClass);
		if(httpObj == null){
			throw new Exception(entityClass + "object is null");
		}
		return httpObj;
	}

	/**
	 * Get请求，返回JSONObject类型数据
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static JSONObject doGetReturnJson(String urlStr){
		String result = doGet(urlStr);
		if (StringUtils.isEmpty(result)) {
			throw new NullPointerException("requset is null url:" + urlStr);
		}
		JSONObject jsonObj = JSON.parseObject(result);
		if (jsonObj == null) {
			throw new NullPointerException("jsonobject is null:" + result);
		}
		return jsonObj;
	}
	/**
	 * Get请求，获得返回数据
	 * 
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String urlStr) {
		long time = System.currentTimeMillis();
		URL url = null;
		HttpURLConnection conn = null;
		String result = "";
		BufferedReader in = null;
		try {
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == 200) {
				in = new BufferedReader(new InputStreamReader(conn.getInputStream(), Consts.UTF_8));
				String line;
				while ((line = in.readLine()) != null) {
					result += line;
				}
				logger.info("get request time:" + (System.currentTimeMillis() - time));
				logger.info("get request result:" + (result.length() > 500 ? result.substring(0, 500):result) + "....");
				return result;
			} else {
				logger.info("get response code error:" + conn.getResponseCode());
				throw new RuntimeException("get response code error:" + conn.getResponseCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("get request error" + e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			conn.disconnect();
		}
		return null;
	}
	
	/**
	 * Get请求，获得返回数据
	 * 添加请求头
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String urlStr,Map<String, String> headers) {
		long time = System.currentTimeMillis();
		URL url = null;
		HttpURLConnection conn = null;
		String result = "";
		BufferedReader in = null;
		try {
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			for (Map.Entry<String, String> e : headers.entrySet()) {
	        	conn.setRequestProperty(e.getKey(), e.getValue());
	        }
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == 200) {
				in = new BufferedReader(new InputStreamReader(conn.getInputStream(), Consts.UTF_8));
				String line;
				while ((line = in.readLine()) != null) {
					result += line;
				}
				logger.info("get request time:" + (System.currentTimeMillis() - time));
				logger.info("get request result:" + (result.length() > 500 ? result.substring(0, 500):result) + "....");
				return result;
			} else {
				logger.info("get response code error:" + conn.getResponseCode());
				throw new RuntimeException("get response code error:" + conn.getResponseCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("get request error:" + e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			conn.disconnect();
		}
		return null;
	}

	/**
	 * 同步的Post请求
	 * @param urlStr 请求的URL地址
	 * @param entityClass 返回的实体类型
	 * @return 返回任意类型，依靠传入的实体原型
	 * @throws Exception
	 */
	public static Object doPostSync(String urlStr, String param, Class<?> entityClass) throws Exception{
		String result = doPost(urlStr, param);
		if (StringUtils.isEmpty(result)) {
			throw new Exception("post(sync) null url:" + urlStr);
		}
		Object httpObj = JSON.parseObject(result, entityClass);
		if(httpObj == null){
			throw new Exception(entityClass + "parse object error:" + result);
		}
		return httpObj;
	}
	
	/**
	 * Post请求，返回JSONObject类型数据
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static JSONObject doPostReturnJson(String urlStr,String param) throws Exception{
		String result = doPost(urlStr,param);
		if (StringUtils.isEmpty(result)) {
			throw new Exception("post null url:" + urlStr);
		}
		JSONObject jsonObj = JSON.parseObject(result);
		if (jsonObj == null) {
			throw new Exception("parse object error:" + result);
		}
		return jsonObj;
	}
	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 * @throws Exception
	 */
	public static String doPost(String url, String param) {
		long time = System.currentTimeMillis();
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "text/html");
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("contentType", "utf-8");
			conn.setRequestProperty("charset", "utf-8");
			conn.setUseCaches(false);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			if (param != null && !param.trim().equals("")) {
				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),Consts.UTF_8));
				// 发送请求参数
				out.print(param);
				// flush输出流的缓冲
				out.flush();
			}
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(),Consts.UTF_8));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			logger.info("post request time:" + (System.currentTimeMillis() - time));
			logger.info("post request result:" + (result.length() > 500 ? result.substring(0, 500):result) + "....");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("post request error" + e);
		}finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 微信提现请求
	 * @param entity
	 * @param defaultCharset
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String doPostWithDraw(String url, String data, String partner, String keyStoreFile) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		InputStream instream = HttpUtil.class.getResourceAsStream(keyStoreFile);
		try {
			keyStore.load(instream, partner.toCharArray());// 这里写密码..默认是你的MCHID
		} finally {
			instream.close();
		}
		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, partner.toCharArray()).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		try {
			HttpPost httpost = new HttpPost(url); // 设置响应头信息
			httpost.addHeader("Connection", "keep-alive");
			httpost.addHeader("Accept", "*/*");
			httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			httpost.addHeader("Host", "api.mch.weixin.qq.com");
			httpost.addHeader("X-Requested-With", "XMLHttpRequest");
			httpost.addHeader("Cache-Control", "max-age=0");
			httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
			httpost.setEntity(new StringEntity(data, "UTF-8"));
			CloseableHttpResponse response = httpclient.execute(httpost);
			try {
				HttpEntity entity = response.getEntity();
				String jsonStr = toStringInfo(response.getEntity(), "UTF-8");
				// 微信返回的报文时GBK，直接使用httpcore解析乱码
				// String jsonStr =
				// EntityUtils.toString(response.getEntity(),"UTF-8");
				EntityUtils.consume(entity);
				return jsonStr;
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}

	private static String toStringInfo(HttpEntity entity, String defaultCharset) throws Exception, IOException {
		final InputStream instream = entity.getContent();
		if (instream == null) {
			return null;
		}
		try {
			Args.check(entity.getContentLength() <= Integer.MAX_VALUE, "HTTP entity too large to be buffered in memory");
			int i = (int) entity.getContentLength();
			if (i < 0) {
				i = 4096;
			}
			Charset charset = null;

			if (charset == null) {
				charset = Charset.forName(defaultCharset);
			}
			if (charset == null) {
				charset = HTTP.DEF_CONTENT_CHARSET;
			}
			final Reader reader = new InputStreamReader(instream, charset);
			final CharArrayBuffer buffer = new CharArrayBuffer(i);
			final char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
			return buffer.toString();
		} finally {
			instream.close();
		}
	}
	
	/**
	 * 
	 * HTTP协议POST请求方法
	 */
	public static String httpMethodPost(String url,
                                        TreeMap<String, String> paramsMap, String gb) {
		if (null == gb || "".equals(gb)) {
			gb = "utf-8";
		}
		String params = null;
		if (null != paramsMap) {
			params = getParamStr(paramsMap);
		}
		StringBuffer sb = new StringBuffer();
		URL urls;
		HttpURLConnection uc = null;
		BufferedReader in = null;
		try {
			urls = new URL(url);
			uc = (HttpURLConnection) urls.openConnection();
			uc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			uc.setDoOutput(true);
			uc.setDoInput(true);
			uc.setRequestMethod("POST");
			uc.setUseCaches(false);
			uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			uc.connect();
			DataOutputStream out = new DataOutputStream(uc.getOutputStream());
			out.write(params.getBytes(gb));
			out.flush();
			out.close();
			in = new BufferedReader(new InputStreamReader(uc.getInputStream(), gb));
			String readLine = "";
			while ((readLine = in.readLine()) != null) {
				sb.append(readLine).append("\n");
			}
			if (in != null) {
				in.close();
			}
			if (uc != null) {
				uc.disconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (uc != null) {
				uc.disconnect();
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * HTTP协议POST请求添加参数的封装方法
	 */
	public static String getParamStr(TreeMap<String, String> paramsMap) {
		StringBuilder param = new StringBuilder();
		for (Iterator<Map.Entry<String, String>> it = paramsMap.entrySet()
				.iterator(); it.hasNext();) {
			Map.Entry<String, String> e = it.next();
			param.append("&").append(e.getKey()).append("=")
					.append(e.getValue());
		}
		return param.toString().substring(1);
	}
}