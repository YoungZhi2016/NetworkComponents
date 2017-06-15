package com.winonetech.jhpplugins.utils;

/**
 * 下载任务参数
 * 
 * @author ywkj
 *
 */
public final class ActionConn {

	private String downWay;// 下载方式:FTP,HTTP_GET,HTTP_POST

	private int downFileType;// 下载文件类型

	private String url;// 下载地址

	private String localDir;// 本地目录

	private String fileName;// 文件名

	private boolean isCover;// 是否覆盖

	private ActionConn(Builder builder) {
		this.downWay = builder.downWay;
		this.downFileType = builder.downFileType;
		this.url = builder.url;
		this.localDir = builder.localDir;
		this.fileName = builder.fileName;
		this.isCover = builder.isCover;
	}

	public String getDownWay() {
		return downWay;
	}

	public int getDownFileType() {
		return downFileType;
	}

	public String getUrl() {
		return url;
	}

	public String getLocalDir() {
		return localDir;
	}

	public String getFileName() {
		return fileName;
	}

	public boolean isCover() {
		return isCover;
	}

	@Override
	public String toString() {
		return "downWay: " + downWay + ", downFileType " + downFileType + ", url " + url + ", localDir " + localDir
				+ ", fileName " + fileName + ", isCover " + isCover + "]";
	}

	/**
	 * 建造
	 */
	public static class Builder {
		private String downWay;
		private int downFileType;
		private String url;
		private String localDir;
		private String fileName;
		private boolean isCover = true;

		public Builder setDownWay(String downWay) {
			this.downWay = downWay;
			return this;
		}

		public Builder setDownFileType(int downFileType) {
			this.downFileType = downFileType;
			return this;
		}

		public Builder setUrl(String url) {
			this.url = url;
			return this;
		}

		public Builder setLocalDir(String localDir) {
			this.localDir = localDir;
			return this;
		}

		public Builder setFileName(String fileName) {
			this.fileName = fileName;
			return this;
		}

		public Builder setCover(boolean isCover) {
			this.isCover = isCover;
			return this;
		}

		public ActionConn builder() {
			return new ActionConn(this);
		}
	}
}
