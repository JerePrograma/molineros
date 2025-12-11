package ar.com.ospim.webservice;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;

import ar.com.ospim.httpclient.ssl.StrictSSLProtocolSocketFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class WSClient {

	private static Log logger = LogFactoryUtil.getLog(WSClient.class);

	@SuppressWarnings("deprecation")
	private static HttpClient getHttpsClient(String host) {
		Protocol myhttps = new Protocol("https",
				new StrictSSLProtocolSocketFactory(false), 443);
		HttpClient httpclient = new HttpClient();
		httpclient.getHostConfiguration().setHost(host, 443, myhttps);
		return httpclient;
	}

	private static HttpClient getHttpClient(String host) {
		HttpClient httpclient = new HttpClient();
		httpclient.getHostConfiguration().setHost(host);
		return httpclient;
	}

	private static WSResult get(String url, HttpClient httpclient)
			throws IOException, HttpException {
		String responseBodyAsString;
		GetMethod httpget = new GetMethod(url);
		int statusCode = 200;
		try {
			httpclient.executeMethod(httpget);
			responseBodyAsString = httpget.getResponseBodyAsString();
			statusCode = httpget.getStatusLine().getStatusCode();
		} finally {
			httpget.releaseConnection();
		}
		return new WSResult(statusCode, responseBodyAsString);
	}

	private static WSResult put(String url, String content,
			HttpClient httpclient) throws IOException, HttpException {
		String responseBodyAsString;
		PutMethod httpput = new PutMethod(url);
		int statusCode = 200;
		try {
			httpput.setRequestEntity(new StringRequestEntity(content,
					"application/j", "UTF-8"));
			httpclient.executeMethod(httpput);
			responseBodyAsString = httpput.getResponseBodyAsString();
			statusCode = httpput.getStatusLine().getStatusCode();
		} finally {
			httpput.releaseConnection();
		}
		return new WSResult(statusCode, responseBodyAsString);
	}

	private static WSResult post(String url, String content,
			HttpClient httpclient) throws IOException, HttpException {
		String responseBodyAsString;
		PostMethod httpost = new PostMethod(url);
		int statusCode = 200;
		try {
			httpost.setRequestEntity(new StringRequestEntity(content,
					"application/j", "UTF-8"));
			httpclient.executeMethod(httpost);
			responseBodyAsString = httpost.getResponseBodyAsString();
			statusCode = httpost.getStatusLine().getStatusCode();
		} finally {
			httpost.releaseConnection();
		}
		return new WSResult(statusCode, responseBodyAsString);
	}

	private static WSResult delete(String url, HttpClient httpclient)
			throws IOException, HttpException {
		String responseBodyAsString;
		DeleteMethod httpdelete = new DeleteMethod(url);
		int statusCode = 200;
		try {
			httpclient.executeMethod(httpdelete);
			responseBodyAsString = httpdelete.getResponseBodyAsString();
			statusCode = httpdelete.getStatusLine().getStatusCode();
		} finally {
			httpdelete.releaseConnection();
		}
		return new WSResult(statusCode, responseBodyAsString);
	}

	private static <T> T getUniqueResult(String host, String url,
			Class<T> clazz, HttpClient httpsClient) throws Exception {
		try {
			WSResult response = get(url, httpsClient);
			Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL)
					.setDateFormat("dd/MM/yyyy").create();
			T ret = gson.fromJson(response.getResult(), clazz);
			return ret;
		} catch (Exception e) {
			logger.error("Error al llamar a ws host: " + host + " url:" + url,
					e);
			throw e;
		}
	}

	private static <T> List<T> getListResult(String host, String url,
			TypeToken<Collection<T>> typeToken, HttpClient httpsClient)
			throws Exception {
		try {
			WSResult response = get(url, httpsClient);
			Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL)
					.setDateFormat("dd/MM/yyyy").create();
			// Gson gson = new Gson();
			List<T> ret = gson.fromJson(response.getResult(),
					typeToken.getType());
			return ret;
		} catch (Exception e) {
			logger.error("Error al llamar a ws host: " + host + " url:" + url,
					e);
			throw e;
		}
	}

	public static <T> T getHttpsUniqueResult(String host, String url,
			Class<T> clazz) throws Exception {
		return getUniqueResult(host, url, clazz, getHttpsClient(host));
	}

	public static <T> List<T> getHttpsListResult(String host, String url,
			TypeToken<Collection<T>> typeToken) throws Exception {
		return getListResult(host, url, typeToken, getHttpsClient(host));
	}

	public static <T> T getHttpUniqueResult(String host, String url,
			Class<T> clazz) throws Exception {
		return getUniqueResult(host, url, clazz, getHttpClient(host));
	}

	public static <T> List<T> getHttpListResult(String host, String url,
			TypeToken<Collection<T>> typeToken) throws Exception {
		return getListResult(host, url, typeToken, getHttpClient(host));
	}

	public static WSResult putHttps(String host, String url, String jSONContent)
			throws Exception {
		return put(url, jSONContent, getHttpsClient(host));
	}

	public static WSResult putHttp(String host, String url, String jSONContent)
			throws Exception {
		return put(url, jSONContent, getHttpClient(host));
	}

	public static WSResult postHttps(String host, String url, String jSONContent)
			throws Exception {
		return post(url, jSONContent, getHttpsClient(host));
	}

	public static WSResult postHttp(String host, String url, String jSONContent)
			throws Exception {
		return post(url, jSONContent, getHttpClient(host));
	}

	public static WSResult deleteHttps(String host, String url)
			throws Exception {
		return delete(url, getHttpsClient(host));
	}

	public static WSResult deleteHttp(String host, String url) throws Exception {
		return delete(url, getHttpClient(host));
	}

	// public static void main(String... strings) throws Exception {
	//
	// List<Informe> httpsListResult = WSClient.getHttpListResult("localhost",
	// "/web/ws/ejemplo/asdasdasd",
	// new TypeToken<Collection<Informe>>() {
	// });
	//
	// // Informe informe = WSClient.getHttpsUniqueResult("localhost",
	// // "/web/ws/ejemplo/asdasdasd", Informe.class);
	//
	// WSResult putHttp = WSClient.putHttp("localhost",
	// "/web/ws/ejemplo/asdasdasd",
	// "{\"askldjas\":34,\"asd\":\"213\"}");
	//
	// System.out.println("asd");
	// }

	public static class WSResult {
		private int code;
		private String result;

		public WSResult(int code, String result) {
			this.code = code;
			this.result = result;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}
	}
}
