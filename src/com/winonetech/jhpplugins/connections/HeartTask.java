package com.winonetech.jhpplugins.connections;

import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.winone.dhmp.rpc.service.terminalManage.TerminalServicePrx;
import com.winonetech.jhpplugins.download.DownloadServer;
import com.winonetech.jhpplugins.utils.Configs;
import com.winonetech.jhpplugins.utils.Logs;
import com.winonetech.jhpplugins.utils.ParserUtils;

/**
 * 心跳任务
 * 
 * @author ywkj
 *
 */
public class HeartTask implements Callable<Boolean> {

	private TerminalServicePrx servicePrx;

	private static String parameters;// 心跳命令

	static {
		ArrayNode arr = ParserUtils.getArrayNode();
		ObjectNode jsonNode = ParserUtils.getObjectNode();
		jsonNode.put("cmd", Commands.HRBT).put("value", Integer.parseInt(Configs.getTerminalNo()));

		arr.add(jsonNode);
		parameters = arr.toString();
	}

	public HeartTask(TerminalServicePrx servicePrx) {
		this.servicePrx = servicePrx;
	}

	/**
	 * 
	 * @param comm
	 *            心跳jsonString
	 */
	private void heartBeat(String resultJsonString) {
		if (resultJsonString == null || resultJsonString.isEmpty()) {
			return;
		}
		ArrayNode jsonNodes = (ArrayNode) ParserUtils.getJsonNedeByJsonString(resultJsonString);
		Logs.info("received: " + jsonNodes.toString());

		jsonNodes.forEach(jsonNode -> {
			if (jsonNode.has("cmd")) {
				String commandType = jsonNode.findValue("cmd").asText();
				switch (commandType) {
				case Commands.NEW_SCHEDULING:// 接收新排期命令
					if (jsonNode.has("value")) {
						DownloadServer.getInstance().scheduleNode(jsonNode.get("value"));
					}
					break;
				case Commands.GET_FILE:
					Logs.info("下载文件命令");
					break;
				case Commands.PUT_FILE:
					Logs.info("上传文件命令");
					break;
				default:
					Logs.info("----未定义命令--->" + commandType);
					break;
				}
			}
		});
	}

	@Override
	public Boolean call() throws Exception {
		Logs.info("heartbeat: " + parameters);
		heartBeat(servicePrx.commandHandle(parameters));
		return true;
	}

	public void setServicePrx(TerminalServicePrx servicePrx) {
		this.servicePrx = servicePrx;
	}

}
