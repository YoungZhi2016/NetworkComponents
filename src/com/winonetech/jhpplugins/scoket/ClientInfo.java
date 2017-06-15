package com.winonetech.jhpplugins.scoket;

public interface ClientInfo {

	long YX_ID = 0xA01;
	String YX = "YX";

	/**
	 * 上线
	 */
	String ONLI = "ONLI";

	/**
	 * 广播
	 */
	String BROADCAST = "BROADCAST";

	/**
	 * 推送
	 */
	String BUTTON_PUSH = "APUSH";

	/**
	 * 取消
	 */
	String BUTTON_CANCEL = "ACAN";

	/**
	 * 截屏ScreenShot
	 */
	String SCREENSHOT = "SCRE";

	/**
	 * 错误
	 */
	String ERROR = "ERROR";

	/*--------------------------------------*/
	String KEY_CMD = "CMD";
	String KEY_VALUE = "VALUE";
	String KEY_CODE = "CODE";
	String KEY_PARAMETERS = "parameters";

	/**
	 * 返回结果
	 */
	String KEY_RESULT = "RESULT";

	/*--------------------------------------------*/
	String UNDEFINED = "undefined or unknown error";// 未知错误
}
