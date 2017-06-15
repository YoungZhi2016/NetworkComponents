package com.winonetech.jhpplugins.connections;

/**
 * 
 * @author ywkj
 */
public interface Commands {

	/**
	 * 心跳命令
	 */
	String HRBT = "HRBT";

	/**
	 * 排期更新命令
	 */
	String NEW_SCHEDULING = "UPSC";

	/**
	 * 下载文件命令
	 */
	String GET_FILE = "DL";

	/**
	 * 上传文件命令
	 */
	String PUT_FILE = "PL";
}
