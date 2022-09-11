package com.yida.bilibili.test;

import com.yida.bilibili.utils.OkHttpClientUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yida
 * @package com.yida.bilibili.test
 * @date 2022-09-11 11:23:
 * @description Type your description over here.
 */
public class DownloadTest {
	public static void main(String[] args) {
		String savePath = "/Users/yida/Downloads/bili_videos/";
		String url = "https://upos-sz-estgoss02.bilivideo.com/upgcxcode/64/77/761987764/761987764-1-64.flv?e=ig8euxZM2rNcNbRznwdVhwdlhWh3hwdVhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=\\u0026uipk=5\\u0026nbs=1\\u0026deadline=1662875999\\u0026gen=playurlv2\\u0026os=upos\\u0026oi=1007291627\\u0026trid=52a44715aee54a65b4344d4dd9d81a6bu\\u0026mid=25415264\\u0026platform=pc\\u0026upsig=2c396af314918dab7e4c81fd680001d5\\u0026uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform\\u0026bvc=vod\\u0026nettype=0\\u0026orderid=0,3\\u0026agrr=1\\u0026bw=139345\\u0026logo=80000000";
		String fileName = "来自李丽珍的美颜暴击，你心动了吗.mp4";
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Content-Type", "application/json;charset=UTF-8");
		headerMap.put("Referer", "https://www.bilibili.com");
		headerMap.put("Sec-Fetch-Mode", "no-cors");
		headerMap.put("User-Agent", OkHttpClientUtils.USER_AGENT);

		OkHttpClientUtils.download(url, headerMap, savePath, fileName);
	}
}
