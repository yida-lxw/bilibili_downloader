package com.yida.bilibili.utils;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author yida
 * @package com.yida.bilibili.utils
 * @date 2022-09-10 19:08:
 * @description Type your description over here.
 */
public class ExCallback<R> implements Callback {
	public static final Logger logger = LogManager.getLogger(ExCallback.class);

	private ResponseCallbackHandler<R> responseCallbackHandler;

	private ExCallback() {}

	public ExCallback(ResponseCallbackHandler<R> responseCallbackHandler) {
		this.responseCallbackHandler = responseCallbackHandler;
	}

	@Override
	public void onFailure(@NotNull Call call, @NotNull IOException e) {
		logger.error("Failed to execute http request asynchronously:\n{}", e.getMessage());
	}

	@Override
	public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
		R content = responseCallbackHandler.handler(call, response);
		if(null != content) {
			logger.info("http response body:\n" + content);
		}
	}
}
