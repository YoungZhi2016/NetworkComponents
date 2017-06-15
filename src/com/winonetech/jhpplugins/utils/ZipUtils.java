package com.winonetech.jhpplugins.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 
 * @author Administrator zip工具类
 *
 */
public class ZipUtils {

	static int byteLength = 1024;

	public ZipUtils() {

	}

	/**
	 * zip解压缩
	 * 
	 * @param filePath
	 *            文件路径
	 * @param outputDirectory
	 *            解压目的路径
	 */
	public static void unZip(String filePath, String outputDirectory) {
		unZip(new File(filePath), outputDirectory);
	}

	@SuppressWarnings("unchecked")
	private static void unZip(File file, String outputDirectory) {
		ZipFile zipFile = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			Charset gbk = Charset.forName("gbk");
			zipFile = new ZipFile(file, gbk);
			Enumeration<ZipEntry> enumeration = (Enumeration<ZipEntry>) zipFile.entries();
			ZipEntry zipEntry;
			String unzipEntryPath;
			String unzipEntryDirPath;
			int index;
			File unzipEntryDir;
			byte[] data = new byte[byteLength];

			// 创建解压后的文件夹
			File unzipDir = new File(outputDirectory);
			if (!unzipDir.exists() || !unzipDir.isDirectory()) {
				unzipDir.mkdirs();
			}

			// 解压
			while (enumeration.hasMoreElements()) {
				zipEntry = (ZipEntry) enumeration.nextElement();
				unzipEntryPath = outputDirectory + File.separator + zipEntry.getName();
				index = unzipEntryPath.lastIndexOf(File.separator);
				if (zipEntry.isDirectory()) {
					// 若为文件夹，创建
					String name = zipEntry.getName().substring(0, zipEntry.getName().length() - 1);
					File f = new File(outputDirectory + File.separator + name);
					f.mkdirs();
				} else {
					// 获取解压文件上层目录
					if (index != -1) {
						unzipEntryDirPath = unzipEntryPath.substring(0, index);
					} else {
						unzipEntryDirPath = "";
					}
					// 创建解压文件上层目录
					unzipEntryDir = new File(unzipEntryDirPath);
					if (!unzipEntryDir.exists() || !unzipEntryDir.isDirectory()) {
						unzipEntryDir.mkdirs();
					}
					// 写出解压文件
					bos = new BufferedOutputStream(new FileOutputStream(unzipEntryPath));
					bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
					while (bis.read(data, 0, byteLength) != -1) {
						bos.write(data);
					}
					bos.flush();
				}
			}
		} catch (IOException e) {
			Logs.error("解压失败！");
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bis != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
