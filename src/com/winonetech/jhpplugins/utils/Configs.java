package com.winonetech.jhpplugins.utils;

/**
 * 
 * @author ywkj config
 *
 */
public class Configs {

	private static ConfigUtil configApplication = new ConfigUtil();
	private static ConfigUtil configPlayer = new ConfigUtil("akkaPlayer.conf");
	private static ConfigUtil configPlugins = new ConfigUtil("akkaPlugins.conf");

	/**
	 * @return port
	 */
	public static String getPort() {
		return configApplication.getString("SOCKET_PORT", "9999");
	}

	public static String getBasePath() {
		return configApplication.getString("BASE_PATH", ConfigUtil.getRootPath() + "/resources/");
	}

	/**
	 * @return screenshot file directory
	 */
	public static String getScreenShotPath() {
		return configApplication.getString("SCREENSHOT_FILE_DIR", ConfigUtil.getRootPath() + "/resources/screenShot");
	}

	/**
	 * @return Terminal No
	 */
	public static String getTerminalNo() {
		return configApplication.getString("TERMINAL_NO", null);
	}

	public static int getPluginsAkkaPort() {
		return configPlugins.getInt("akka.remote.netty.tcp.port", 6001);
	}

	public static String getPluginsAkkaIp() {
		return configPlugins.getString("akka.remote.netty.tcp.hostname", "127.0.0.1");
	}

	public static int getPlayerAkkaPort() {
		return configPlayer.getInt("akka.remote.netty.tcp.port", 6000);
	}

	public static String getPlayerAkkaIp() {
		return configPlayer.getString("akka.remote.netty.tcp.hostname", "127.0.0.1");
	}

	public static String get() {
		return configPlayer.getString("name", "127.0.asw0.1");
	}
}
