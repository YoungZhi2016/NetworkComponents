package com.winonetech.jhpplugins.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.winone.ftc.mtools.Log;

/**
 * 
 * @author ywkj 文件工具类
 *
 */
public class FileUtils {

	public final static int TYPE_FILE = 0xF01;// 文件
	public final static int TYPE_DIRECTTORY = 0xF02;// 目录

	public static final String ROOT_PATH = new File("").getAbsolutePath();// 根目录

	public static void createFile(File file, int type) throws IOException {
		Log.i(file.getPath());

		switch (type) {
		case TYPE_FILE:
			file.getParentFile().mkdirs();
			file.createNewFile();
			break;
		case TYPE_DIRECTTORY:
			file.mkdirs();
			break;
		}
	}

	public static void createFile(String filePath, int type) throws IOException {
		File file = new File(filePath);
		createFile(file, type);
	}

	/**
	 * write File UTF-8
	 */
	public static void writeFile(String filePath, String content) throws IOException {
		File file = new File(filePath);
		writeFile(file, content);
	}

	/**
	 * write File UTF-8
	 */
	public static void writeFile(File file, String content) throws IOException {
		file.getParentFile().mkdirs();// 创建文件夹
		file.createNewFile();// 创建新文件
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		osw.write(content);
		osw.close();
	}

	/**
	 * 
	 * @param fileName
	 *            文件名
	 * @param split
	 *            分隔符
	 * @param defaultSuffix
	 *            默认
	 * @return 后缀
	 */
	public static String getFileSuffix(String fileName, char split, String defaultSuffix) {
		if (fileName == null || fileName.isEmpty() || fileName.lastIndexOf(split) == -1) {
			return defaultSuffix;
		}
		return fileName.substring(fileName.lastIndexOf(split) + 1);
	}

	/**
	 * 
	 * @param url
	 *            file url
	 * @param split
	 *            s
	 * @return s
	 */
	public static String getFileNameByUrl(String url, String split) {
		return url.substring(url.lastIndexOf(split) + 1);
	}
}
