package com.winonetech.jhpplugins.utils;

/**
 * @author ywkj
 * 
 *         类型
 */
public interface TypeUtil {

	/**
	 * 下载方式
	 */
	String WAY_FTP = "FTP";
	String WAY_HTTP_POST = "POST";
	String WAY_HTTP_GET = "GET";
	String WAY_UNKNOWN = "UNKNOWN";

	/**
	 * 文件类型
	 */
	int SCHEDULE_FILE = 0xB01; // 排期文件
	int MATERIAL_FILE = 0xB02;// 素材文件爱你
	int TEMPLATE_FILE = 0xB03;// 模版文件
	int NORMAL = 0xB04;// 普通文件
}
