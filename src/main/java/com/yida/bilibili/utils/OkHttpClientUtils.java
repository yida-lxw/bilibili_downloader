package com.yida.bilibili.utils;

import com.yida.bilibili.enums.SyncOrAsync;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yida
 * @package com.yida.bilibili.utils
 * @date 2022-09-10 17:42:
 * @description type your description over here.
 */
public class OkHttpClientUtils {
	public static final Logger logger = LogManager.getLogger(OkHttpClientUtils.class);

	private static final long READ_TIMEOUT = 600000L;
	private static final long WRITE_TIMEOUT = 600000L;
	private static final long CONNECT_TIMEOUT = 3000L;

	private static final int MAX_IDLE_CONNECTION_COUNT = 100;
	private static final int KEEP_ALIVE = 60;

	private static final ConnectionPool connectionPool = new ConnectionPool(MAX_IDLE_CONNECTION_COUNT, KEEP_ALIVE, TimeUnit.SECONDS);

	public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36";

	private static class HolderClass{
		private static final OkHttpClient okHttpClient = buildOkHttpClient();
	}

	public static OkHttpClient getInstance(){
		return HolderClass.okHttpClient;
	}

	private static OkHttpClient buildOkHttpClient() {
		return new OkHttpClient.Builder()
				.followRedirects(true)
				.retryOnConnectionFailure(false)
				.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
				.writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
				.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
				.connectionPool(connectionPool)
				.build();
	}

	public static Response post(String url) {
		return post(url, null);
	}

	public static Response post(String url, Map<String, String> headerMap) {
		return post(url, headerMap, null);
	}

	public static Response post(String url, Map<String, String> headerMap, Map<String, String> paramMap) {
		return post(url, headerMap, paramMap, null);
	}

	public static Response post(String url, Map<String, String> headerMap, Map<String, String> paramMap, ResponseCallbackHandler<String> responseCallbackHandler) {
		SyncOrAsync syncOrAsync = (null == responseCallbackHandler)? SyncOrAsync.SYNC : SyncOrAsync.ASYNC;
		if(SyncOrAsync.ASYNC.equals(syncOrAsync)) {
			asyncPost(url, headerMap, paramMap, responseCallbackHandler);
			return null;
		}
		return syncPost(url, headerMap, paramMap);
	}

	public static Response get(String url) {
		return get(url, null);
	}

	public static Response get(String url, Map<String, String> headerMap) {
		return get(url, headerMap, null);
	}

	public static Response get(String url, Map<String, String> headerMap, ResponseCallbackHandler<String> responseCallbackHandler) {
		SyncOrAsync syncOrAsync = (null == responseCallbackHandler)? SyncOrAsync.SYNC : SyncOrAsync.ASYNC;
		if(SyncOrAsync.ASYNC.equals(syncOrAsync)) {
			asyncGet(url, headerMap, responseCallbackHandler);
			return null;
		}
		return syncGet(url, headerMap);
	}

	public static void asyncPost(String url) {
		asyncPost(url, null);
	}

	public static void asyncPost(String url, Map<String, String> headerMap) {
		asyncPost(url, headerMap, null);
	}

	public static void asyncPost(String url, Map<String, String> headerMap, Map<String, String> paramMap) {
		asyncPost(url, headerMap, paramMap, null);
	}

	public static void asyncPost(String url, Map<String, String> headerMap, Map<String, String> paramMap, ResponseCallbackHandler<String> responseCallbackHandler) {
		OkHttpClient okHttpClient = buildOkHttpClient();
		Request request = buildRequest(url, HttpMethod.POST, headerMap, paramMap);
		executeRequest(okHttpClient, request, SyncOrAsync.ASYNC, responseCallbackHandler);
	}

	public static void asyncGet(String url) {
		asyncGet(url, null);
	}

	public static void asyncGet(String url, Map<String, String> headerMap) {
		asyncGet(url, headerMap, null);
	}

	public static void asyncGet(String url, Map<String, String> headerMap, ResponseCallbackHandler<String> responseCallbackHandler) {
		OkHttpClient okHttpClient = buildOkHttpClient();
		Request request = buildRequest(url, HttpMethod.GET, headerMap);
		executeRequest(okHttpClient, request, SyncOrAsync.ASYNC, responseCallbackHandler);
	}

	public static Response syncPost(String url) {
		return syncPost(url, null);
	}

	public static Response syncPost(String url, Map<String, String> headerMap) {
		return syncPost(url, headerMap, null);
	}

	public static Response syncPost(String url, Map<String, String> headerMap, Map<String, String> paramMap) {
		OkHttpClient okHttpClient = buildOkHttpClient();
		Request request = buildRequest(url, HttpMethod.POST, headerMap, paramMap);
		return executeRequest(okHttpClient, request, SyncOrAsync.SYNC);
	}


	public static Response syncGet(String url) {
		return syncGet(url, null);
	}

	public static Response syncGet(String url, Map<String, String> headerMap) {
		OkHttpClient okHttpClient = buildOkHttpClient();
		Request request = buildRequest(url, HttpMethod.GET, headerMap);
		return executeRequest(okHttpClient, request, SyncOrAsync.SYNC);
	}

	private static Response executeRequest(OkHttpClient okHttpClient, Request request, SyncOrAsync syncOrAsync) {
		return executeRequest(okHttpClient, request, syncOrAsync, null);
	}

	private static Response executeRequest(OkHttpClient okHttpClient, Request request, SyncOrAsync syncOrAsync,
									   ResponseCallbackHandler<String> responseCallbackHandler) {
		//默认异步执行
		if(null == syncOrAsync) {
			syncOrAsync = SyncOrAsync.ASYNC;
		}
		Call call = okHttpClient.newCall(request);
		if(SyncOrAsync.ASYNC.equals(syncOrAsync)) {
			if(null == responseCallbackHandler) {
				responseCallbackHandler = new DefaultResponseCallbackHandler();
			}
			call.enqueue(new ExCallback(responseCallbackHandler));
			return null;
		}
		try {
			return call.execute();
		} catch (IOException e) {
			logger.error("An exception occurred in the synchronous execution of http request:[{}], exception:{}",
					request.url().url().toString(), e.getMessage());
			return null;
		}
	}

	/**
	 * @description 下载文件
	 * @author yida
	 * @date 2022-09-11 11:21:12
	 * @param url
	 * @param headerMap
	 * @param savePath
	 * @param fileName
	 *
	 *
	 */
	public static void download(String url, Map<String, String> headerMap, String savePath, String fileName) {
		Response response = syncGet(url, headerMap);
		try {
			byte[] bytes = response.body().bytes();
			Path folderPath = Paths.get(savePath);
			boolean exists = Files.exists(folderPath);
			if (!exists) {
				Files.createDirectories(folderPath);
			}
			if(!savePath.endsWith(File.separator)) {
				savePath = savePath + "/";
			}
			Path filePath = Paths.get(savePath + fileName);
			exists = Files.exists(filePath, LinkOption.NOFOLLOW_LINKS);
			if (!exists) {
				Files.write(filePath, bytes, StandardOpenOption.CREATE);
			}
		} catch (IOException e) {
			logger.error("downlod file:[{}] occur exception:{}", url, e.getMessage());
		}
	}

	public static Request buildRequest(String url, HttpMethod httpMethod, Map<String, String> headerMap) {
		return buildRequest(url, httpMethod, headerMap, null);
	}

	public static Request buildRequest(String url, HttpMethod httpMethod,
									   Map<String, String> headerMap, Map<String, String> paramMap) {
		if(HttpMethod.GET.equals(httpMethod)) {
			Request.Builder requestBuilder = new Request.Builder()
					.url(url).get();
			if(null != headerMap && headerMap.size() > 0) {
				populateHeader(requestBuilder, headerMap);
			}
			return requestBuilder.build();
		}
		//Post Request
		FormBody.Builder formBodyBuilder = new FormBody.Builder();
		if(null != paramMap && paramMap.size() > 0) {
			populateParam(formBodyBuilder, paramMap);
		}
		Request.Builder requestBuilder = new Request.Builder().url(url).post(formBodyBuilder.build());
		if(null != headerMap && headerMap.size() > 0) {
			populateHeader(requestBuilder, headerMap);
		}
		return requestBuilder.build();
	}

	private static void populateHeader(Request.Builder requestBuilder, Map<String, String> headerMap) {
		requestBuilder.headers(Headers.of(headerMap));
	}

	private static void populateParam(FormBody.Builder formBodyBuilder, Map<String, String> paramMap) {
		for(Map.Entry<String, String> entry : paramMap.entrySet()) {
		    String key = entry.getKey();
		    String val = entry.getValue();
			formBodyBuilder.add(key, val);
		}
	}
}
