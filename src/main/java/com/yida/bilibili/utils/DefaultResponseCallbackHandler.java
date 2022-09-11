package com.yida.bilibili.utils;

import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author yida
 * @package com.yida.bilibili.utils
 * @date 2022-09-10 18:25:
 * @description Type your description over here.
 */
public class DefaultResponseCallbackHandler implements ResponseCallbackHandler<java.lang.String> {
	public static final Logger logger = LogManager.getLogger(DefaultResponseCallbackHandler.class);

	@Override
	public String handler(Call call, Response response) {
		try {
			return response.body().string();
		} catch (IOException e) {
			logger.error("extract response body string occur error:{}", e.getMessage());
		}
		return null;
	}
}
