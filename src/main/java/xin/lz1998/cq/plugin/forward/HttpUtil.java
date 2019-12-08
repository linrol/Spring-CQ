package xin.lz1998.cq.plugin.forward;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
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
	
	private final static String CONTENT_CHARSET = "UTF-8";

	private final static int SO_TIMEOUT = 30000;
	private final static int REDIRECT_CODE = 302;
	
	private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	

	/**
	 * Get请求，返回JSONObject类型数据
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static JSONObject sendGet(String url) throws Exception{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try{
			url = url.replaceFirst("^http://", "");
			URIBuilder uriBuilder = new URIBuilder().setScheme("http").setHost(url);
			HttpGet httpGet = new HttpGet(uriBuilder.build());
			HttpResponse response = httpClient.execute(httpGet);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				String result = EntityUtils.toString(response.getEntity(),CONTENT_CHARSET);
				if (StringUtils.isEmpty(result)) {
					throw new NullPointerException("requset is null url:" + url);
				}
				JSONObject jsonObj = JSON.parseObject(result);
				if (jsonObj == null) {
					throw new NullPointerException("jsonobject is null:" + result);
				}
				return jsonObj;
			}else{
				logger.error("调用URL地址通讯失败,失败状态：{}" , response.getStatusLine().getStatusCode());
				throw new Exception("调用URL地址通讯失败,失败状态：" + response.getStatusLine().getStatusCode());
			}
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}finally{
			if(null != httpClient){
				httpClient.close();
			}
		}
	}
	
	/**
	 * GET方式调用HTTPS
	 * @param url 网址
	 * @param data 参数
	 * @throws Exception
	 */
	public static JSONObject sendHttpsGet(String url) throws Exception{
		CloseableHttpClient sslClient = createSSLClient();
		try{
			url = url.replaceFirst("^https://", "");
			URIBuilder uriBuilder = new URIBuilder().setScheme("https").setHost(url);
			HttpGet httpGet = new HttpGet(uriBuilder.build());
			HttpResponse response = sslClient.execute(httpGet);
			String result = "";
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				result = EntityUtils.toString(response.getEntity(),CONTENT_CHARSET);
			} else if(REDIRECT_CODE == response.getStatusLine().getStatusCode()){
				Header[] hs = response.getHeaders("Location");
				if(hs.length > 0){
					return sendGet(hs[0].getValue());
				}
				result = EntityUtils.toString(response.getEntity(), CONTENT_CHARSET);
			}else{
				result = EntityUtils.toString(response.getEntity(), CONTENT_CHARSET);
				throw new Exception("调用URL地址通讯失败,失败状态：" + response.getStatusLine().getStatusCode() + ",失败原因:" + result);
			}
			if (StringUtils.isEmpty(result)) {
				throw new NullPointerException("requset is null url:" + url);
			}
			JSONObject jsonObj = JSON.parseObject(result);
			if (jsonObj == null) {
				throw new NullPointerException("jsonobject is null:" + result);
			}
			return jsonObj;
		}finally {
			sslClient.close();
		}
	}
	
	public static CloseableHttpClient createSSLClient(){
		ConnectionConfig connConfig = ConnectionConfig.custom().build();
		SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(SO_TIMEOUT).build();
		try {
			RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
			ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
			registryBuilder.register("http", plainSF);
			//指定信任密钥存储对象和连接套接字工厂
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(trustStore, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext,NoopHostnameVerifier.INSTANCE);
			registryBuilder.register("https", sslSF);
			Registry<ConnectionSocketFactory> registry = registryBuilder.build();
			//设置连接管理器
			PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
			connManager.setDefaultConnectionConfig(connConfig);
			connManager.setDefaultSocketConfig(socketConfig);
			//构建客户端
			return HttpClientBuilder.create().setConnectionManager(connManager).build();
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("创建SSLClient失败,错误信息:" + e.getMessage());
		}
	}
}