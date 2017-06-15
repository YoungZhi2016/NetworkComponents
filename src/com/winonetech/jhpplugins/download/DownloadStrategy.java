package com.winonetech.jhpplugins.download;

import com.winonetech.jhpplugins.utils.Configs;

/**
 * 
 * @author ywkj
 *
 *         下载参数
 */
public interface DownloadStrategy {

	/**
	 * base directory
	 */
	String ROOT_DIRECTORY = Configs.getBasePath();

	/**
	 * download directory
	 */
	String DOWNLOAD = "download";

	/**
	 * template directory
	 */
	String TEMPLATE_DIR = "template/";

	/**
	 * schedule directory
	 */
	String SCHEDULE_DIR = "schedule";

	/**
	 * effect scheduling file
	 */
	String SCHEDULE_EFFECT = "effect_schedule.json";

	/**
	 * old scheduling file
	 */
	String SCHEDULE_OLD = "old_schedule.json";

	/**
	 * Latest scheduling file
	 * 
	 * OVERDUE
	 */
	String SCHEDULE_TEMP = "temp_schedule.json";

	/**
	 * image directory
	 */
	String IMAGE_DIR = DOWNLOAD + "/image";

	/**
	 * PDF directory
	 */
	String PDF_DIR = DOWNLOAD + "/pdf";

	/**
	 * video directory
	 */
	String VIDEO_DIR = DOWNLOAD + "/video";

	/**
	 * TXT directory
	 */
	String TEXT_DIR = DOWNLOAD + "/txt";

	/**
	 * Audio directory
	 */
	String AUDIO_DIR = DOWNLOAD + "/audio";

	/**
	 * The Shared directory
	 */
	String SHARE_DIR = DOWNLOAD + "/Share";

	/**
	 * TXT suffix
	 */
	String TXT_SUFFIX = "txt";

	/**
	 * audio suffix
	 */
	String AUDIO_SUFFIX = "mp3,wma,wav,asf,aac";

	/**
	 * image suffix
	 */
	String IMAGE_SUFFIX = "jpeg,jpg,png,bmp,gif,tiff,psd,svg";

	/**
	 * PDF suffix
	 */
	String PDF_SUFFIX = "pdf";

	/**
	 * video suffix
	 */
	String VIDEO_SUFFIX = "avi,wmv,mpeg,mp4,mov,mkv,flv,f4v,m4v,rmvb,rm,3gp,dat,ts,mts,vob";

	/**
	 * unknown
	 */
	String UNKNOWN_SUFFIX = "unknown";

	/*---------------------------------------------------JSON KEY------------------------------------------------------------------------------*/
	/**
	 * JSON key : release
	 */
	String KEY_SCHEDULE_RELEASE = "release";

	/**
	 * JSON key : version
	 */
	String KEY_SCHEDULE_VERSION = "version";

	/**
	 * JSON key : schedule
	 */
	String KEY_SCHEDULE_SCHEDULE = "schedule";

	/**
	 * JSON key : materialSch
	 */
	String KEY_SCHEDULE_MATERIALSCH = "materialSch";

	/**
	 * JSON key : url
	 */
	String KEY_SCHEDULE_MATCONT = "matcont";

	/**
	 * id
	 */
	String KEY_SCHEDULE_LAYID = "layid";

	/**
	 * laycont
	 */
	String KEY_SCHEDULE_LAYCONT = "laycont";
}
