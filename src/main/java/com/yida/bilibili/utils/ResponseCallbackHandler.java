package com.yida.bilibili.utils;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author yida
 * @package com.yida.bilibili.utils
 * @date 2022-09-10 18:23:
 * @description Type your description over here.
 */

@FunctionalInterface
public interface ResponseCallbackHandler<R> {
	R handler(Call call, Response response);
}
