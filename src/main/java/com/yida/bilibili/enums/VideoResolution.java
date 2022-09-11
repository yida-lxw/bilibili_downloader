package com.yida.bilibili.enums;

/**
 * @author yida
 * @package com.yida.bilibili.enums
 * @date 2022-09-11 09:09:
 * @description 视频分辨率的枚举
 */
public enum VideoResolution {
	R_360(0, 360, "360P"),
	R_480(1, 480, "480P"),
	R_720(2, 720, "720P"),
	R_1080(3, 1080, "1080P");

	//0=360P, 1=480P, 2=720P, 3=1080P
	private int index;
	private int height;
	private String desc;

	VideoResolution(int index, int height, String desc) {
		this.index = index;
		this.height = height;
		this.desc = desc;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public static final VideoResolution R360 = R_360;
	public static final VideoResolution R480 = R_480;
	public static final VideoResolution R720 = R_720;
	public static final VideoResolution R1080 = R_1080;
}
