package com.winonetech.jhpplugins.scoket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.fasterxml.jackson.databind.JsonNode;
import com.winonetech.jhpplugins.scoket.base.Communication;
import com.winonetech.jhpplugins.scoket.base.ComunicationClient;
import com.winonetech.jhpplugins.scoket.base.TimeThread;
import com.winonetech.jhpplugins.utils.Logs;
import com.winonetech.jhpplugins.utils.ParserUtils;

public class SocketServerThread extends Thread implements ComunicationClient {

	private long thisId;

	private Communication server;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private volatile boolean isMonite = false;
	private boolean isAccess;

	public SocketServerThread(Socket socket) {
		try {
			this.socket = socket;
			this.inputStream = socket.getInputStream();
			this.outputStream = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			int lenth = 0;
			byte[] buff = new byte[1024];
			String encoding = "UTF-8";

			while (isMonite) {
				if (socket.isConnected() && inputStream.available() > 0) {
					lenth = inputStream.read(buff);
					while (lenth != -1) {
						this.handlerMessage(new String(buff, 0, lenth, encoding));
						if (socket.isConnected() && inputStream.available() > 0) {
							lenth = inputStream.read(buff);
						} else {
							lenth = -1;
						}
					}
				}
			}
		} catch (IOException e) {
			Logs.error("inputStream错误 - " + e.getCause());
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				Logs.error("关闭资源失败: " + e.getMessage());
			}
		}
	}

	@Override
	public void handlerMessage(String result) {
		Logs.info("Receive the client data: " + result);

		JsonNode rJsonNode;
		try {
			rJsonNode = ParserUtils.getByJsonString(result);// result:{"cmd":"yx","value":"onli"}
		} catch (Exception e) {
			Logs.error("data is wrong: " + e.getMessage());
			server.handlerMessage(this, ParserUtils.getObjectNode().put("cmd", "error"));
			return;
		}
		if (rJsonNode == null || !rJsonNode.has("cmd")) {
			server.handlerMessage(this, ParserUtils.getObjectNode().put("cmd", "error"));
			return;
		}

		String cmd = rJsonNode.get("cmd").asText();
		switch (cmd.toUpperCase()) {
		case ClientInfo.YX:
			if (server != null) {
				thisId = ClientInfo.YX_ID;
				server.add(this);
				server.handlerMessage(this, rJsonNode);
			}
			break;
		case ClientInfo.ONLI:// 上线
		case ClientInfo.BUTTON_CANCEL:// 取消
		case ClientInfo.BUTTON_PUSH:// 推送
		case ClientInfo.SCREENSHOT:// 截屏
			if (server != null) {
				server.handlerMessage(this, rJsonNode);
			}
			break;
		default:
			if (server != null && !isAccess()) {
				server.remote(this);
			}
			break;
		}
	}

	@Override
	public void startMonite() {
		if (!isMonite) {
			isMonite = true;
			start();
		}

	}

	@Override
	public void stopMonite() {
		if (isMonite) {
			isMonite = false;
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}

				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				Logs.error("stopMonite: " + e.getCause());
			}
			synchronized (this) {
				this.notifyAll();
			}

		}
	}

	@Override
	public void sendMessage(String message) {
		if (outputStream != null && message != null && !message.isEmpty()) {
			try {
				outputStream.write(message.getBytes());
				outputStream.flush();
			} catch (IOException e) {

			}
		}
	}

	@Override
	public void setServer(Communication server) {
		this.server = server;
		new TimeThread(server, this, 1000 * 30);
	}

	@Override
	public boolean isAccess() {
		return isAccess;
	}

	@Override
	public void setAccess(boolean access) {
		isAccess = access;
	}

	@Override
	public boolean isMonite() {
		return isMonite;
	}

	@Override
	public long clientId() {
		return thisId;
	}

}
