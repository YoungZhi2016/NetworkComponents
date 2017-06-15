package com.winonetech.jhpplugins.index;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.winonetech.jhpplugins.index.PluginsServerThread.ServerType;

public final class IndexPlugins {

	private ExecutorService service;

	private static class IndexPluginsHolder {
		private static IndexPlugins indexPlugins = new IndexPlugins();
	}

	public static IndexPlugins getInstance() {
		return IndexPluginsHolder.indexPlugins;
	}

	private IndexPlugins() {
		service = Executors.newCachedThreadPool();
	}

	/**
	 * 开启插件服务
	 */
	public void startServer() {
		PluginsServerThread socketService = new PluginsServerThread(ServerType.SOCKETSERVICE);
		PluginsServerThread heartService = new PluginsServerThread(ServerType.HEARTSERVICE);
		PluginsServerThread systemInfoService = new PluginsServerThread(ServerType.SYSTEMINFO);
		service.execute(socketService);
		service.execute(heartService);
		service.execute(systemInfoService);
	}
}
