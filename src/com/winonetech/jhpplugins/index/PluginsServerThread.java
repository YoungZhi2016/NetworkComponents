package com.winonetech.jhpplugins.index;

import com.winonetech.jhpplugins.connections.HeartbeatConnection;
import com.winonetech.jhpplugins.scoket.SocketService;
import com.winonetech.jhpplugins.systeminfo.SystemInfo;
import com.winonetech.jhpplugins.utils.ICEUtil;
import com.winonetech.jhpplugins.utils.Logs;

public class PluginsServerThread implements Runnable {

	/**
	 * 服务类型
	 */
	public static enum ServerType {
		HEARTSERVICE, // 心跳服务
		SOCKETSERVICE, // socket服务
		SYSTEMINFO// 系统信息发送
	}

	private ServerType type;

	public PluginsServerThread(ServerType type) {
		this.type = type;
	}

	@Override
	public void run() {
		switch (type) {
		case HEARTSERVICE:
			HeartbeatConnection.getInstance().startHeart();// 启动心跳服务
			break;
		case SOCKETSERVICE:
			try {
				SocketService.getInstance().startServer();// 启动socket服务
			} catch (Exception e) {
				Logs.error("SocketService启动失败 " + e.getMessage());
				Thread.currentThread().interrupt();
			}
			break;
		case SYSTEMINFO:
			SystemInfo aSystemInfo = new SystemInfo(ICEUtil.geTerminalServicePrx());
			aSystemInfo.sendInfo();
			break;
		}
	}

}
