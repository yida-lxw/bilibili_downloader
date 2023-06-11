package com.yida.bilibili;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.yida.bilibili.bean.VideoInfo;
import com.yida.bilibili.enums.VideoResolution;
import com.yida.bilibili.utils.ConstUtils;
import com.yida.bilibili.utils.GsonUtils;
import com.yida.bilibili.utils.OkHttpClientUtils;
import com.yida.bilibili.utils.RegexUtils;
import com.yida.bilibili.utils.ResponseCallbackHandler;
import okhttp3.Call;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author yida
 * @package com.yida.bilibili
 * @date 2022-09-10 22:00:
 * @description Type your description over here.
 */
public class BiliVideoDownload {
	public static final Logger logger = LogManager.getLogger(BiliVideoDownload.class);

	private static final String video_dir = "videos";
	private static final String audio_dir = "audios";
	private static final String video_suffix = ".flv";
	private static final String audio_suffix = ".mp3";

	private static final String video_baseurl_template = "https://www.bilibili.com/video/#bvid";

	private static final String video_baseurl_template_4_serial = "https://www.bilibili.com/video/#bvid?p=#p";

	private static final String videoinfo_flag = "window.__playinfo__=";
	private static final String preffix = "window.__INITIAL_STATE__=";
	private static final String suffix = ";(function()";
	private static final int preffix_len = preffix.length();
	private static final int suffix_len = suffix.length();

	private String videoUrl;

	private String videoName;

	//视频分辨率
	private VideoResolution resolution;

	//如果是视频选集，那么p表示当前是第几集,从1开始计算，如果不是视频选集，则p设置为默认值零即可
	private int p;

	//视频下载目录(即视频下载后保存在本地硬盘什么目录下)
	private String video_savepath;

	private VideoInfo videoInfo;

	private static Map<Integer, String> videoNameMap = new TreeMap<>();

	public BiliVideoDownload(String videoUrl, VideoResolution resolution, int p, String video_savepath) {
		this.videoUrl = videoUrl;
		this.resolution = resolution;
		this.p = p;
		this.video_savepath = video_savepath;
	}

	public BiliVideoDownload(String videoUrl, VideoResolution resolution, int p) {
		this(videoUrl, resolution, p, ConstUtils.DEFAULT_SAVEPATH);
	}

	public BiliVideoDownload(String videoUrl, VideoResolution resolution) {
		this(videoUrl, resolution, 1, ConstUtils.DEFAULT_SAVEPATH);
	}

	public BiliVideoDownload(String videoUrl) {
		//默认选择720P
		this(videoUrl, VideoResolution.R_720, 1, ConstUtils.DEFAULT_SAVEPATH);
	}

	/**
	 * @description 请求视频页的html内容并提取网页title以及视频播放JSON信息
	 * @author yida
	 * @date 2022-09-10 22:38:10
	 */
	public void singleDownload() {
		requestPageContent();
	}

	public void download() {
		List<String> downloadUrlList = prepareSerialDownload();
		if(null == downloadUrlList || downloadUrlList.size() <= 0) {
			return;
		}
		int size = downloadUrlList.size();
		if(size == 1) {
			singleDownload();
			return;
		}
		VideoResolution videoResolution = this.resolution;
		if(null == videoResolution) {
			videoResolution = VideoResolution.R_720;
		}

		//todo: 这里可以考虑改造成多线程下载,暂时没实现
		for (int i = 0; i < size; i++) {
			String downloadUrl = downloadUrlList.get(i);
			int p = i + 1;
			BiliVideoDownload biliVideoDownload = new BiliVideoDownload(downloadUrl, videoResolution, p, ConstUtils.DEFAULT_SAVEPATH);
			String serialVideoName = videoNameMap.get(p);
			biliVideoDownload.setVideoName(serialVideoName);
			biliVideoDownload.requestPageContent();
			if(i > 0 && i % 3 == 0) {
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			logger.info("begin to download video:[{}], resolution:[{}]", downloadUrl, videoResolution.getDesc());
		}
	}

	/**
	 * @description 提取视频选集中每个视频的初始下载链接
	 * @author yida
	 * @date 2022-09-11 14:12:20
	 *
	 */
	public List<String> prepareSerialDownload() {
		Response response = null;
		try {
			response = OkHttpClientUtils.syncGet(this.videoUrl);
			String content = response.body().string();
			int start_len = content.indexOf(preffix);
			int end_len = content.indexOf(suffix);
			String jsonData = content.substring(start_len + preffix_len, end_len);
			Map<String, Object> jsonMap = GsonUtils.toMap(jsonData);
			Map<String, Object> videoDataMap = (Map<String, Object>)jsonMap.get("videoData");
			List<Map<String, Object>> pageList = (List<Map<String, Object>>)videoDataMap.get("pages");
			String aid = jsonMap.get("aid").toString();
			String bvid = jsonMap.get("bvid").toString();
			bvid = bvid.replace("\"", "");
			int videos = Integer.valueOf(videoDataMap.get("videos").toString());
			//是否为视频选集类型(说明是个合集，存在多个视频，否则即表示单个视频)
			boolean isSerial = (videos > 1);
			System.out.println("aid:" + aid);
			System.out.println("bvid:" + bvid);
			System.out.println("videos:" + videos);
			List<String> downloadUrlList = new ArrayList<>();
			if(isSerial && null != pageList && pageList.size() > 0) {
				for (int i = 0; i < videos; i++) {
					Map<String, Object> videoMap = pageList.get(i);
					//String cid = videoMap.get("cid").toString();
					String part = videoMap.get("part").toString();
					part = part.replace("\"", "");
					String[] arr = part.split(" - ");
					part = (arr.length == 1)? part : arr[1];
					int p = i + 1;
					String pStr = (p >=0 && p < 10)? "0" + p : p + "";
					String videoPName = pStr + "." + part.replace(" ", ".");
					if(videoNameMap.size() < videos) {
						videoNameMap.put(p, videoPName);
					}
					String downloadUrl = video_baseurl_template_4_serial.replace("#bvid", bvid).replace("#p", p + "");
					downloadUrlList.add(downloadUrl);
				}
			} else {
				List<String> videoUrlList = parseSections(jsonMap);
				if(null == videoUrlList || videoUrlList.size() <= 0) {
					downloadUrlList.add(this.videoUrl);
				} else {
					downloadUrlList = videoUrlList;
				}
			}
			return downloadUrlList;
		} catch (Exception e) {
			logger.error("prepare serial download url occur error:{}", e.getMessage());
			return null;
		} finally {
		    response.close();
		}
	}

	/**
	 * @description 视频合集的另一种形式的解析
	 * @author yida
	 * @date 2022-09-11 15:28:21
	 *
	 * @return {@link List}
	 */
	private List<String> parseSections(Map<String, Object> jsonMap) {
		List<String> videoUrlList = new ArrayList<>();
		Map<String, Object> sectionsInfoMap = (Map<String, Object>)jsonMap.get("sectionsInfo");
		if(null == sectionsInfoMap || sectionsInfoMap.isEmpty()) {
			return videoUrlList;
		}
		List<Map<String, Object>> sectionsList = (List<Map<String, Object>>)sectionsInfoMap.get("sections");
		if(null == sectionsList || sectionsList.isEmpty()) {
			return videoUrlList;
		}
		Map<String, Object> sectionMap = sectionsList.get(0);
		List<Map<String, Object>> episodesList = (List<Map<String, Object>>)sectionMap.get("episodes");
		if(null == episodesList || episodesList.isEmpty()) {
			return videoUrlList;
		}
		for (Map<String, Object> episodeMap : episodesList) {
			String bvid = episodeMap.get("bvid").toString();
			bvid = bvid.replace("\"", "");
			String videoUrl = video_baseurl_template.replace("#bvid", bvid);
			videoUrlList.add(videoUrl);
		}
		return videoUrlList;
	}

	/**
	 * @description 请求视频页的html内容并提取网页title以及视频播放JSON信息
	 * @author yida
	 * @date 2022-09-10 22:38:10
	 */
	public void requestPageContent() {
		OkHttpClientUtils.get(this.videoUrl, null, new ResponseCallbackHandler<String>() {
			@Override
			public String handler(Call call, Response response) {
				try {
					String content = response.body().string();
					//System.out.println("orignal content:\n" + content);
					int start_len = content.indexOf(preffix);
					int end_len = content.indexOf(suffix);
					String jsonData = content.substring(start_len + preffix_len, end_len);
					Map<String, Object> jsonMap = GsonUtils.toMap(jsonData);
					Map<String, Object> videoDataMap = (Map<String, Object>)jsonMap.get("videoData");
					List<Map<String, Object>> pageList = (List<Map<String, Object>>)videoDataMap.get("pages");
					String aid = jsonMap.get("aid").toString();
					String bvid = jsonMap.get("bvid").toString();
					bvid = bvid.replace("\"", "");
					int videos = Integer.valueOf(videoDataMap.get("videos").toString());
					//是否为视频选集类型(说明是个合集，存在多个视频，否则即表示单个视频)
					boolean isSerial = (videos > 1);
					System.out.println("aid:" + aid);
					System.out.println("bvid:" + bvid);
					System.out.println("videos:" + videos);
					Document document = Jsoup.parse(content);
					Element titleEl = document.getElementsByTag("title").first();
					videoInfo = new VideoInfo();
					videoInfo.setBvid(bvid);
					videoInfo.setP(p);
					videoInfo.setVideoResolution(resolution);
					if(null == videoInfo.getVideoName()) {
						String title = titleEl.text().replace("_哔哩哔哩_bilibili", "");
						videoInfo.setVideoName(title);
					}
					String videoInfoStr = RegexUtils.getMatch(ConstUtils.videinfo_regeg, content);
					videoInfo.setVideoInfo(GsonUtils.parseJson(videoInfoStr));
					parseVideoInfo(videoInfo);
					return jsonData;

				} catch (IOException e) {
					logger.error("request page content occur error:{}", e.getMessage());
				} finally {
					response.close();
				}
				return null;
			}
		});
	}

	/**
	 * @description 解析视频信息
	 * @author yida
	 * @date 2022-09-10 22:37:10
	 *
	 */
	private void parseVideoInfo(VideoInfo videoInfo) {
		JsonObject videoInfoJsonObject = videoInfo.getVideoInfo();
		JsonArray videoInfoArr = videoInfoJsonObject.getAsJsonObject("data").getAsJsonObject("dash").getAsJsonArray("video");
		boolean pickUp = pickUpVideoWithResolution(videoInfoArr, videoInfo);
		if(!pickUp) {
			logger.warn("videoName:{},videoUrl:{},video_resolution:{} don't exists, please choose the other resolution videos.",
					videoInfo.getVideoName(), videoInfo.getVideoBaseUrl(), videoInfo.getVideoResolution().getDesc());
			return;
		}
		String bvid = videoInfo.getBvid();
		String videoeUrl = video_baseurl_template.replace("#bvid", bvid);
		Map<String, String> videoHeaderMap = new HashMap<>();
		videoHeaderMap.put("Referer", videoeUrl);
		videoHeaderMap.put("Range", "bytes=" + videoInfo.getVideoBaseRange());
		videoHeaderMap.put("User-Agent", OkHttpClientUtils.USER_AGENT);

		OkHttpClientUtils.get(videoInfo.getVideoBaseUrl(), videoHeaderMap, new ResponseCallbackHandler<String>() {
			@Override
			public String handler(Call call, Response response) {
				try {
					String videoSize = response.header("Content-Range").split("/")[1];
					videoInfo.setVideoSize(videoSize);
					// 获取音频基本信息
					JsonArray audioInfoArr = videoInfoJsonObject.getAsJsonObject("data").getAsJsonObject("dash").getAsJsonArray("audio");
					videoInfo.setAudioBaseUrl(audioInfoArr.get(0).getAsJsonObject().get("baseUrl").getAsString());
					videoInfo.setAudioBaseRange(audioInfoArr.get(0).getAsJsonObject().getAsJsonObject("SegmentBase").get("Initialization").getAsString());

					Map<String, String> audioHeaderMap = new HashMap<>();
					audioHeaderMap.put("Referer", videoeUrl);
					audioHeaderMap.put("Range", "bytes=" + videoInfo.getAudioBaseRange());
					audioHeaderMap.put("User-Agent", OkHttpClientUtils.USER_AGENT);
					OkHttpClientUtils.get(videoInfo.getAudioBaseUrl(), audioHeaderMap, new ResponseCallbackHandler<String>() {
						@Override
						public String handler(Call call, Response response) {
							try {
								String audioSize = response.header("Content-Range").split("/")[1];
								videoInfo.setAudioSize(audioSize);
								downloadVideoAndAudio(videoInfo);
							} catch (Exception e) {
								logger.error("request [{}] to parse audio info occur exception:{}", videoInfo.getAudioBaseUrl(), e.getMessage());
							} finally {
								response.close();
								return null;
							}
						}
					});
				} catch (Exception e) {
					logger.error("request [{}] to parse vide info occur exception:{}", videoInfo.getVideoBaseUrl(), e.getMessage());
				} finally {
				    response.close();
					return null;
				}
			}
		});
	}

	/**
	 * @description 根据用户确定的分辨率挑选视频
	 * @author yida
	 * @date 2022-09-11 09:05:07
	 * @param videoInfoArr
	 *
	 */
	private boolean pickUpVideoWithResolution(JsonArray videoInfoArr, VideoInfo videoInfo) {
		VideoResolution resolution = videoInfo.getVideoResolution();
		Iterator<JsonElement> iterator = videoInfoArr.iterator();
		//是否找到目标分辨率的视频
		boolean pickUp = false;
		while (iterator.hasNext()) {
			JsonElement jsonElement = iterator.next();
			JsonObject currentJsonObject = jsonElement.getAsJsonObject();
			int height = Integer.valueOf(currentJsonObject.get("height").getAsString());
			if(resolution.getHeight() != height) {
				continue;
			}
			videoInfo.setVideoBaseUrl(currentJsonObject.get("baseUrl").getAsString());
			videoInfo.setVideoBaseRange(currentJsonObject.getAsJsonObject("SegmentBase").get("Initialization").getAsString());
			pickUp = true;
			break;
		}
		return pickUp;
	}

	/**
	 * @description 合并音频和视频
	 * @author yida
	 * @date 2022-09-11 13:10:33
	 * @param videoFile
	 * @param audioFile
	 * @param videoInfo
	 */
	private void mergeFiles(File videoFile, File audioFile, VideoInfo videoInfo){
		String outFile = this.video_savepath + videoInfo.getVideoName() + video_suffix;
		List<String> commend = new ArrayList<>();
		commend.add(ConstUtils.ffmpeg_basepath);
		commend.add("-i");
		commend.add(videoFile.getAbsolutePath());
		commend.add("-i");
		commend.add(audioFile.getAbsolutePath());
		//commend.add("-c");
		//commend.add("copy");
		commend.add(outFile);

		ProcessBuilder builder = new ProcessBuilder();
		builder.command(commend);
		try {
			builder.inheritIO().start().waitFor();
			logger.info("--------------[{}]音视频合并完成--------------", videoInfo.getVideoName());
		} catch (InterruptedException | IOException e) {
			logger.info("[{}]音视频合并失败！", videoInfo.getVideoName());
			e.printStackTrace();
		}
	}

	/**
	 * @description 下载视频和音频文件
	 * @author yida
	 * @date 2022-09-11 09:32:59
	 * @param videoInfo
	 */
	private void downloadVideoAndAudio(VideoInfo videoInfo) {
		int p = videoInfo.getP();
		File videoFile = downloadVideo(videoInfo);
		File audioFile = downloadAudio(videoInfo);
		mergeFiles(videoFile, audioFile, videoInfo);
	}

	/**
	 * @description 下载视频
	 * @author yida
	 * @date 2022-09-11 12:55:43
	 * @param videoInfo
	 */
	private File downloadVideo(VideoInfo videoInfo) {
		return downloadFile(videoInfo.getVideoBaseUrl(), videoInfo.getVideoSize(), video_dir, videoInfo.getVideoName(), video_suffix);
	}

	/**
	 * @description 下载音频
	 * @author yida
	 * @date 2022-09-11 12:55:43
	 * @param videoInfo
	 */
	private File downloadAudio(VideoInfo videoInfo) {
		return downloadFile(videoInfo.getAudioBaseUrl(), videoInfo.getAudioSize(), audio_dir, videoInfo.getVideoName(), audio_suffix);
	}

	/**
	 * @description 下载文件
	 * @author yida
	 * @date 2022-09-11 12:55:43
	 */
	private File downloadFile(String downloadUrl, String fileSize, String baseDir, String fileName, String fileSuffix) {
		String audiosSavePath = prepareDir(baseDir);
		File audioFile = new File(audiosSavePath + fileName + fileSuffix);
		if (!audioFile.exists()) {
			Map<String, String> headerMap = new HashMap<>();
			headerMap.put("Referer", videoUrl);
			//todo: 这里最好是将所有字节数均匀分成N组采用多线程并行下载，然后把多个视频片段合并为整个完整视频，目前尚未实现(一次性下载所有字节会很慢)
			headerMap.put("Range", "bytes=0-" + fileSize);
			headerMap.put("User-Agent", OkHttpClientUtils.USER_AGENT);
			OkHttpClientUtils.download(downloadUrl, headerMap, audiosSavePath, fileName + fileSuffix);
			return audioFile;
		}
		return null;
	}


	private String prepareDir(String dir) {
		if(!this.video_savepath.endsWith(File.separator)) {
			this.video_savepath = this.video_savepath + File.separator;
		}
		String savePath = this.video_savepath + dir + File.separator;
		File fileDir = new File(savePath);
		if (!fileDir.exists()){
			fileDir.mkdirs();
		}
		return savePath;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getVideo_savepath() {
		return video_savepath;
	}

	public void setVideo_savepath(String video_savepath) {
		this.video_savepath = video_savepath;
	}

	public VideoInfo getVideoInfo() {
		return videoInfo;
	}

	public void setVideoInfo(VideoInfo videoInfo) {
		this.videoInfo = videoInfo;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}
}
