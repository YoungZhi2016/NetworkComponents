package com.winonetech.jhpplugins.scoket;

import java.awt.AWTException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.winonetech.jhpplugins.akka.AkkaRemoteSystem;
import com.winonetech.jhpplugins.function.ScreenShot;
import com.winonetech.jhpplugins.scoket.base.Communication;
import com.winonetech.jhpplugins.scoket.base.ComunicationClient;
import com.winonetech.jhpplugins.utils.Configs;
import com.winonetech.jhpplugins.utils.Logs;
import com.winonetech.jhpplugins.utils.ParserUtils;

public class SocketService implements Communication {

	private List<ComunicationClient> clients = new ArrayList<>();
	private ReentrantLock lock = new ReentrantLock();

	private ServerSocket serverSocket;

	private static final class SocketServiceHolder {
		private static SocketService service = new SocketService();
	}

	private SocketService() {
	}

	public static SocketService getInstance() {
		return SocketServiceHolder.service;
	}

	/**
	 * 启动socket 服务
	 * 
	 * @throws Exception
	 */
	public void startServer() throws Exception {
		if (serverSocket == null) {
			try {
				serverSocket = new ServerSocket(SocketInfo.SOCKET_PORT);
				Logs.info("service socket is opened,port: " + SocketInfo.SOCKET_PORT);
			} catch (IOException e) {
				Logs.error("socket启动失败: " + e.getCause());
				serverSocket = null;
			}
		}
		if (serverSocket == null) {
			return;
		}

		ComunicationClient client;
		Socket socket;
		while (true) {
			socket = serverSocket.accept();// 9999
			client = new SocketServerThread(socket);
			client.setServer(this);
			client.startMonite();
		}
	}

	@Override
	public void add(ComunicationClient client) {
		if (client == null) {
			return;
		}
		try {
			lock.lock();
			Iterator<ComunicationClient> iterator = clients.iterator();
			ComunicationClient tempClient;
			while (iterator.hasNext()) {
				tempClient = iterator.next();
				if (tempClient.clientId() == ClientInfo.YX_ID) {
					iterator.remove();// 删除已存在的YX_ID
					tempClient.setAccess(false);
					tempClient.stopMonite();
				}
			}
			if (clients.add(client)) {
				client.setAccess(true);
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void remote(ComunicationClient client) {
		try {
			lock.lock();
			Iterator<ComunicationClient> iterator = clients.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().equals(client)) {
					iterator.remove();
				}
			}
			client.setAccess(false);
			client.stopMonite();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void handlerMessage(ComunicationClient client, JsonNode message) {
		try {
			lock.lock();
			String cmd = message.get("cmd").asText().toUpperCase();
			ObjectNode node = ParserUtils.getObjectNode();
			if (client.isAccess()) {// 长连接
				switch (cmd) {
				case ClientInfo.YX:
					client.sendMessage(node.put(ClientInfo.KEY_RESULT, true).put("code", "welcome").toString());
					break;
				case ClientInfo.ERROR:
				default:// 未知
					client.sendMessage(ParserUtils.getObjectNode().put(ClientInfo.KEY_RESULT, false)
							.put(ClientInfo.KEY_VALUE, ClientInfo.UNDEFINED).toString());
				}
			} else {// 短连接
				switch (cmd) {
				case ClientInfo.BUTTON_PUSH:// 推送{"cmd":"APUSH","value":[1]}
					client.sendMessage(ParserUtils.getObjectNode().put(ClientInfo.KEY_RESULT, true).toString());
					tellPalyer(ParserUtils.getObjectNode().put("code", ClientInfo.BUTTON_PUSH).set("parameters",
							ParserUtils.getArrayNode().add(message.get("value").asText())));
					break;
				case ClientInfo.BUTTON_CANCEL:// 取消
					client.sendMessage(ParserUtils.getObjectNode().put(ClientInfo.KEY_RESULT, true).toString());
					tellPalyer(ParserUtils.getObjectNode().put("code", ClientInfo.BUTTON_CANCEL).set("parameters",
							ParserUtils.getArrayNode().add(message.get("value").asText())));
					break;
				case ClientInfo.ONLI:// 上线
					client.sendMessage(node.put(ClientInfo.KEY_RESULT, true).put("message", "welcome").toString());
					break;
				case ClientInfo.BROADCAST:// 广播
					Iterator<ComunicationClient> iterator = clients.iterator();
					while (iterator.hasNext()) {
						iterator.next().sendMessage("hello everybody");
					}
					break;

				case ClientInfo.SCREENSHOT:// 截屏
					ObjectNode aObjectNode = ParserUtils.getObjectNode();
					try {
						String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_hh:mm:ss"));
						ScreenShot.getInstance().snapShot("scr" + time, "png");
						client.sendMessage(aObjectNode.put(ClientInfo.KEY_RESULT, true)
								.put(ClientInfo.KEY_VALUE, "截屏成功").toString());
					} catch (AWTException | IOException e) {
						Logs.error("截屏失败!" + e.getMessage());
						client.sendMessage(aObjectNode.put(ClientInfo.KEY_RESULT, false)
								.put(ClientInfo.KEY_VALUE, "截屏失败！").toString());
					}
					break;
				case ClientInfo.ERROR:
				default:// 未知
					client.sendMessage(ParserUtils.getObjectNode().put(ClientInfo.KEY_RESULT, false)
							.put(ClientInfo.KEY_VALUE, ClientInfo.UNDEFINED).toString());
				}
			}

		} finally {
			lock.unlock();
		}
	}

	/**
	 * @param message
	 *            tell yx
	 */
	public void tellYX(String message) {
		tellPalyer(ParserUtils.getObjectNode().put("code", message));
	}

	private void tellPalyer(JsonNode message) {
		AkkaRemoteSystem.getRemoteRef(Configs.getPlayerAkkaIp(), Configs.getPlayerAkkaPort()).tell(message.toString(),
				AkkaRemoteSystem.senderActor);
	}

}
