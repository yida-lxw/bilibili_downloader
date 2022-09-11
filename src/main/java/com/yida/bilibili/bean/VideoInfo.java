package com.yida.bilibili.bean;

import com.google.gson.JsonObject;
import com.yida.bilibili.enums.VideoResolution;

/**
 * @author yida
 * @package com.yida.bilibili.bean
 * @date 2022-09-10 21:42:
 * @description Type your description over here.
 */
public class VideoInfo {
	private String bvid;
	private String videoName;
	/**视频选集中的第几集(从1开始计算)*/
	private int p;
	private JsonObject videoInfo;
	private String videoBaseUrl;
	private String audioBaseUrl;
	private String videoBaseRange;
	private String audioBaseRange;
	private String videoSize;
	private String audioSize;
	private VideoResolution videoResolution;

	public String getBvid() {
		return bvid;
	}

	public void setBvid(String bvid) {
		this.bvid = bvid;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public int getP() {
		return p;
	}

	public void setP(int p) {
		this.p = p;
	}

	public JsonObject getVideoInfo() {
		return videoInfo;
	}

	public void setVideoInfo(JsonObject videoInfo) {
		this.videoInfo = videoInfo;
	}

	public String getVideoBaseUrl() {
		return videoBaseUrl;
	}

	public void setVideoBaseUrl(String videoBaseUrl) {
		this.videoBaseUrl = videoBaseUrl;
	}

	public String getAudioBaseUrl() {
		return audioBaseUrl;
	}

	public void setAudioBaseUrl(String audioBaseUrl) {
		this.audioBaseUrl = audioBaseUrl;
	}

	public String getVideoBaseRange() {
		return videoBaseRange;
	}

	public void setVideoBaseRange(String videoBaseRange) {
		this.videoBaseRange = videoBaseRange;
	}

	public String getAudioBaseRange() {
		return audioBaseRange;
	}

	public void setAudioBaseRange(String audioBaseRange) {
		this.audioBaseRange = audioBaseRange;
	}

	public String getVideoSize() {
		return videoSize;
	}

	public void setVideoSize(String videoSize) {
		this.videoSize = videoSize;
	}

	public String getAudioSize() {
		return audioSize;
	}

	public void setAudioSize(String audioSize) {
		this.audioSize = audioSize;
	}

	public VideoResolution getVideoResolution() {
		return videoResolution;
	}

	public void setVideoResolution(VideoResolution videoResolution) {
		this.videoResolution = videoResolution;
	}
}
