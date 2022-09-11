package com.yida.bilibili.test;

import com.yida.bilibili.BiliVideoDownload;
import com.yida.bilibili.enums.VideoResolution;
import com.yida.bilibili.utils.ConstUtils;
import com.yida.bilibili.utils.GsonUtils;
import com.yida.bilibili.utils.OkHttpClientUtils;
import com.yida.bilibili.utils.ResponseCallbackHandler;
import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yida
 * @package com.yida.bilibili.test
 * @date 2022-09-10 16:55:
 * @description Type your description over here.
 */
public class HttpTest {
	private static final String videoinfo_flag = "window.__playinfo__=";
	private static final String preffix = "window.__INITIAL_STATE__=";
	private static final String suffix = ";(function()";
	private static final int preffix_len = preffix.length();
	private static final int suffix_len = suffix.length();

	/**
	 * @description 支持下载视频选集(即一个视频列表里包含多个视频)
	 * @author yida
	 * @date 2022-09-11 14:43:44
	 * @param args
	 */
	public static void main(String[] args) {
		//视频下载后保存在本地硬盘什么路径下
		String savePath = ConstUtils.DEFAULT_SAVEPATH;
		//需要下载的B站视频链接
		String url = "https://www.bilibili.com/video/BV1bS4y1n7Wa";

		//下载什么分辨率的视频(默认选择的是 720P)
		VideoResolution videoResolution = VideoResolution.R_720;
		BiliVideoDownload biliVideoDownload = new BiliVideoDownload(url, videoResolution, 1, savePath);
		biliVideoDownload.download();

	}
}
