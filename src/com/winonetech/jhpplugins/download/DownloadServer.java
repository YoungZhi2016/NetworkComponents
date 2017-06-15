package com.winonetech.jhpplugins.download;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.winone.ftc.mcore.imps.ManagerImp;
import com.winone.ftc.mentity.mbean.State;
import com.winone.ftc.mentity.mbean.Task;
import com.winone.ftc.mentity.mbean.Task.onResult;
import com.winone.ftc.mentity.mbean.TaskFactory;
import com.winonetech.jhpplugins.scoket.SocketService;
import com.winonetech.jhpplugins.utils.ActionConn;
import com.winonetech.jhpplugins.utils.FileUtils;
import com.winonetech.jhpplugins.utils.Logs;
import com.winonetech.jhpplugins.utils.ParserUtils;
import com.winonetech.jhpplugins.utils.TypeUtil;

public final class DownloadServer {

	private static class BuilderHolper {
		private static DownloadServer downloadServer = new DownloadServer();
	}

	private DownloadServer() {
	}

	public static DownloadServer getInstance() {
		return BuilderHolper.downloadServer;
	}

	/**
	 * @param scheduleString
	 *            排期JsonString
	 */
	public void schedule(String scheduleString) {
		scheduleNode(ParserUtils.getJsonNedeByJsonString(scheduleString));
	}

	/**
	 * @param scheduleNode
	 *            排期JsonNode
	 */
	public void scheduleNode(JsonNode scheduleNode) {
		// 异步
		CompletableFuture.supplyAsync(() -> {
			String version = scheduleNode.get(DownloadStrategy.KEY_SCHEDULE_VERSION).asText();
			Logs.info("version: " + version);

			String effectScheduleFilePath = getScheduleFilePath(DownloadStrategy.SCHEDULE_EFFECT);
			try {
				if (validate(effectScheduleFilePath, version, scheduleNode.toString())) {
					noticeSchedule("UPSC");
					downScheduleMaterials(scheduleNode);
				}
			} catch (IOException e) {
				Logs.error("排期解析失败:" + e.getMessage());
			}
			return 0;
		});
	}

	/**
	 * @param action
	 *            down
	 */
	private void down(ActionConn action) {
		switch (action.getDownWay()) {
		case TypeUtil.WAY_FTP:// FTP
			ftpDownloadTask(action.getUrl(), action.getLocalDir(), action.getFileName(), action.isCover(),
					action.getDownFileType());
			break;
		case TypeUtil.WAY_HTTP_GET:// HTTP GET
			httpDownloadTask(action.getUrl(), TypeUtil.WAY_HTTP_GET, action.getLocalDir(), action.getFileName(),
					action.isCover(), action.getDownFileType());
			break;
		case TypeUtil.WAY_HTTP_POST:// HTTP POST
			httpDownloadTask(action.getUrl(), TypeUtil.WAY_HTTP_POST, action.getLocalDir(), action.getFileName(),
					action.isCover(), action.getDownFileType());
			break;
		case TypeUtil.WAY_UNKNOWN:
		default:
		}

	}

	/**
	 * @param scheduleJsonNode
	 *            下载排期中的素材
	 */
	private void downScheduleMaterials(JsonNode scheduleJsonNode) {
		if (!scheduleJsonNode.has(DownloadStrategy.KEY_SCHEDULE_SCHEDULE))// schedule
			return;

		ArrayNode scheduleArr = (ArrayNode) scheduleJsonNode.get(DownloadStrategy.KEY_SCHEDULE_SCHEDULE);

		HashMap<Integer, String> templates = new HashMap<>();

		Set<String> urlSets = new HashSet<>();
		ArrayNode materialSchArr;
		for (JsonNode schedule : scheduleArr) {
			if (schedule.has(DownloadStrategy.KEY_SCHEDULE_LAYCONT)
					&& schedule.has(DownloadStrategy.KEY_SCHEDULE_LAYID)) {
				templates.put(schedule.get(DownloadStrategy.KEY_SCHEDULE_LAYID).asInt(),
						schedule.get(DownloadStrategy.KEY_SCHEDULE_LAYCONT).asText());
			}

			if (!schedule.has(DownloadStrategy.KEY_SCHEDULE_MATERIALSCH))
				continue;
			materialSchArr = (ArrayNode) schedule.get(DownloadStrategy.KEY_SCHEDULE_MATERIALSCH);
			for (JsonNode materialSch : materialSchArr) {
				if (materialSch.has(DownloadStrategy.KEY_SCHEDULE_MATCONT))
					urlSets.add(materialSch.get(DownloadStrategy.KEY_SCHEDULE_MATCONT).asText());
			}
		}

		saveLay(templates);// 保存模版

		urlSets.forEach(url -> {
			ActionConn actionConn = getActionByUrl(url, TypeUtil.MATERIAL_FILE);
			Logs.info("下载url->: " + actionConn.getUrl());
			down(actionConn);
		});
	}

	/**
	 * @param templates
	 * 
	 * @throws IOException
	 */
	private void saveLay(HashMap<Integer, String> templates) {
		CompletableFuture.supplyAsync(() -> {
			Iterator<Entry<Integer, String>> iterator = templates.entrySet().iterator();
			Entry<Integer, String> entry;
			String tempDir = DownloadStrategy.ROOT_DIRECTORY + DownloadStrategy.TEMPLATE_DIR;

			String suffix = ".html";
			try {
				while (iterator.hasNext()) {
					entry = iterator.next();
					FileUtils.writeFile(tempDir + entry.getKey() + suffix, entry.getValue());
				}
			} catch (IOException e) {
				Logs.error("保存模版文件失败:" + e.getMessage());
			}
			return 0;
		});
	}

	/**
	 * HTTP方式下载
	 * 
	 * @param url
	 * 
	 * @param httpType
	 *            GET or PUT
	 * 
	 * @param localDir
	 *            本地目录
	 * @param fileName
	 *            文件名
	 * @param isCover
	 *            是否覆盖
	 * @param downFileType
	 *            下载文件类型
	 */
	@SuppressWarnings("serial")
	private void httpDownloadTask(String url, String httpType, String localDir, String fileName, boolean isCover,
			int downFileType) {
		Task task = TaskFactory.httpTaskDown(url, httpType, localDir, fileName, isCover);

		task.setOnResult(new onResult() {
			@Override
			public void onSuccess(State arg0) {
				switch (downFileType) {
				case TypeUtil.SCHEDULE_FILE:// 排期文件
					String scheduleFilePath = localDir + File.separator + fileName;
					scheduleNode(ParserUtils.getJsonNodeByFilePath(scheduleFilePath));
					break;
				case TypeUtil.MATERIAL_FILE:
					tell("http方式下载素材文件成功");
					break;
				case TypeUtil.NORMAL:
				default:
					tell("普通文件下载成功");
					break;
				}

			}

			@Override
			public void onLoading(State arg0) {
			}

			@Override
			public void onFailt(State arg0) {
				Logs.info("---下载失败---");
			}
		});
		ManagerImp.get().load(task);
	}

	/**
	 * FTP方式下载
	 * 
	 * @param url
	 *            下载地址
	 * @param localDir
	 *            本地目录
	 * @param fileName
	 *            文件名
	 * @param isCover
	 *            是否覆盖
	 * @param type
	 *            文件类型
	 */
	@SuppressWarnings("serial")
	private void ftpDownloadTask(String url, String localDir, String fileName, boolean isCover, int type) {
		CompletableFuture.supplyAsync(() -> {
			Task task = TaskFactory.ftpTaskDown(url, localDir, fileName, true);
			task.setOnResult(new onResult() {
				@Override
				public void onSuccess(State arg0) {
					switch (type) {
					case TypeUtil.SCHEDULE_FILE:// 排期文件类型
						String scheduleFilePath = localDir + File.separator + fileName;
						scheduleNode(ParserUtils.getJsonNodeByFilePath(scheduleFilePath));
						break;
					case TypeUtil.MATERIAL_FILE:

						tell("ftp方式下载素材文件成功");
						break;
					case TypeUtil.NORMAL:
					default:
						tell("ftp普通文件下载成功");
						break;
					}
				}

				@Override
				public void onLoading(State arg0) {

				}

				@Override
				public void onFailt(State arg0) {
					Logs.error(url + "---下载失败---" + arg0.toString());
				}
			});
			ManagerImp.get().load(task);

			return 0;
		});
	}

	/**
	 * 排期版本验证
	 * 
	 * @param effectFilePath
	 *            有效排期文件
	 * @param version
	 *            本次推送排期的版本
	 * @return
	 * @throws IOException
	 */
	private boolean validate(String effectFilePath, String version, String scheduleContent) throws IOException {
		File effectFile = new File(effectFilePath);

		if (!effectFile.exists() || !effectFile.isFile()) {
			FileUtils.writeFile(effectFile, scheduleContent);
		}
		JsonNode jsonNode = ParserUtils.getJsonNodeByFile(effectFile);

		if (jsonNode.has(DownloadStrategy.KEY_SCHEDULE_VERSION)
				&& (version.equals(jsonNode.get(DownloadStrategy.KEY_SCHEDULE_VERSION).asText()))) {// �汾��һ�¾Ͳ�����
			return false;
		}

		Logs.info("收到新排期--->");
		FileUtils.writeFile(getScheduleFilePath(DownloadStrategy.SCHEDULE_TEMP), scheduleContent);

		// 失效排期文件Path
		Path path = Paths.get(effectFile.getParent(), DownloadStrategy.SCHEDULE_OLD);
		// 将有效排期内容复制到失效排期文件中
		Files.copy(effectFile.toPath(), path, StandardCopyOption.REPLACE_EXISTING);

		// 最新排期文件Path
		path = Paths.get(effectFile.getParent(), DownloadStrategy.SCHEDULE_TEMP);
		// 最新排期文件内容复制到有效排期文件中
		Files.copy(path, effectFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return true;
	}

	/**
	 * 通知:UPSC
	 */
	private void noticeSchedule(String scheduleComman) {
		int size = ManagerImp.get().getCurrenTaskQueue().size();
		Logs.info("当前下载任务: " + size);
		if (size == 0) {// 所有下载任务已完成
			SocketService.getInstance().tellYX(scheduleComman);
		}
	}

	/**
	 * tell
	 * 
	 * @param filePath
	 */
	private void tell(String filePath) {
		Logs.info("tell: " + filePath);
	}

	/**
	 * 下载任务详情
	 * 
	 * @param downUrl
	 * @return
	 */
	private ActionConn getActionByUrl(String downUrl, int downFileType) {
		return new ActionConn.Builder().setUrl(downUrl)//
				.setDownWay(getWayByUrl(downUrl))//
				.setLocalDir(getLocalDir(downUrl, downFileType))//
				.setFileName(getFileNameByUrl(downUrl, downFileType, "/"))//
				.setDownFileType(downFileType)//
				.setCover(true).builder();
	}

	/**
	 * 获取下载方式
	 * 
	 * @param url
	 *            ftp://ftpuser:ftpuser@192.168.6.16:21/aaa/http.json
	 *            http://sw.bos.baidu.com/sw-search-sp/software/b2b8f30bb45be/cloudmusicsetup_2.1.2.186197_baidupc.exe
	 * @return
	 */
	private String getWayByUrl(String url) {
		String uString = url.toUpperCase();// 转大写
		if (uString.startsWith(TypeUtil.WAY_FTP))
			return TypeUtil.WAY_FTP;
		else if (uString.startsWith("HTTP")) {
			return TypeUtil.WAY_HTTP_GET;// ---------------->GET
		}
		return TypeUtil.WAY_UNKNOWN;
	}

	/**
	 * 获取本地保存目录
	 * 
	 * @param url
	 * @return
	 */
	private String getLocalDir(String url, int downFileType) {
		if (url == null || url.isEmpty())
			return DownloadStrategy.SHARE_DIR;
		String localDir;
		switch (downFileType) {
		case TypeUtil.TEMPLATE_FILE://
			localDir = DownloadStrategy.TEMPLATE_DIR;
			break;
		case TypeUtil.SCHEDULE_FILE://
			localDir = DownloadStrategy.SCHEDULE_DIR;
			break;
		case TypeUtil.MATERIAL_FILE://
			String suffix = FileUtils.getFileSuffix(url, '.', DownloadStrategy.UNKNOWN_SUFFIX).toLowerCase();// �ļ���׺(Сд)
			if (DownloadStrategy.IMAGE_SUFFIX.contains(suffix)) {
				localDir = DownloadStrategy.IMAGE_DIR;
			} else if (DownloadStrategy.VIDEO_SUFFIX.contains(suffix)) {
				localDir = DownloadStrategy.VIDEO_DIR;
			} else if (DownloadStrategy.TXT_SUFFIX.contains(suffix)) {
				localDir = DownloadStrategy.TEXT_DIR;
			} else if (DownloadStrategy.PDF_SUFFIX.contains(suffix)) {// PDF
				localDir = DownloadStrategy.PDF_DIR;
			} else {
				localDir = DownloadStrategy.SHARE_DIR;
			}
			break;
		default:
			localDir = DownloadStrategy.SHARE_DIR;
		}
		return DownloadStrategy.ROOT_DIRECTORY + localDir;
	}

	/**
	 * 获取文件名
	 * 
	 * @param url
	 * @param downFileType
	 * @param split
	 * @return
	 */
	private String getFileNameByUrl(String url, int downFileType, String split) {
		return downFileType == TypeUtil.SCHEDULE_FILE ? DownloadStrategy.SCHEDULE_TEMP
				: FileUtils.getFileNameByUrl(url, split);
	}

	/**
	 * @return 获取排期文件地址
	 */
	private String getScheduleFilePath(String fileName) {
		return DownloadStrategy.ROOT_DIRECTORY + DownloadStrategy.SCHEDULE_DIR + File.separator + fileName;
	}
}