package com.winonetech.jhpplugins.utils;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import java.awt.Toolkit;

import com.sun.management.OperatingSystemMXBean;

import sun.management.ManagementFactoryHelper;

/**
 * system info
 */
public class SystemInfoUtil {

	private OperatingSystemMXBean aOperatingSystemMXBean;// CPU

	private static int KB = 1024;

	private static class InfoHolder {
		private static SystemInfoUtil sInfoUtil = new SystemInfoUtil();
	}

	private SystemInfoUtil() {
		aOperatingSystemMXBean = (OperatingSystemMXBean) ManagementFactoryHelper.getOperatingSystemMXBean();
	}

	public static SystemInfoUtil getInstance() {
		return InfoHolder.sInfoUtil;
	}

	/**
	 * IP
	 * 
	 * @throws UnknownHostException
	 */
	public String getIPAddress() throws UnknownHostException {
		InetAddress address = InetAddress.getLocalHost();
		return address.getHostAddress();
	}

	/**
	 * @return MAC
	 */
	public String getMacAddress() throws SocketException, UnknownHostException {
		InetAddress address = InetAddress.getLocalHost();
		NetworkInterface ni = NetworkInterface.getByInetAddress(address);
		ni.getInetAddresses().nextElement().getAddress();
		byte[] mac = ni.getHardwareAddress();
		String sMAC = "";
		@SuppressWarnings("resource")
		Formatter formatter = new Formatter();
		for (int i = 0; i < mac.length; i++) {
			sMAC = formatter.format(Locale.getDefault(), "%02X%s", mac[i], (i < mac.length - 1) ? "-" : "").toString();
		}
		return sMAC;
	}

	/**
	 * @return 广播地址
	 */
	public List<String> getBroadcastAddress() throws UnknownHostException, SocketException {
		List<String> list = new ArrayList<>();
		NetworkInterface networkInterface = NetworkInterface.getByInetAddress(Inet4Address.getLocalHost());
		for (InterfaceAddress inteAddress : networkInterface.getInterfaceAddresses()) {
			if (inteAddress.getBroadcast() != null) {
				list.add(inteAddress.getBroadcast().getHostAddress());
			}
		}
		return list;
	}

	/**
	 * 
	 * @return 操作系统的构架
	 */
	public String getOsArch() {
		return aOperatingSystemMXBean.getArch();
	}

	/**
	 * 
	 * @return 操作系统的版本
	 */
	public String getOsVersion() {
		return aOperatingSystemMXBean.getVersion();
	}

	/**
	 * 
	 * @return 操作系统的名称
	 */
	public String getOsName() {
		return aOperatingSystemMXBean.getName();
	}

	/**
	 * @return 空闲内存 KB
	 */
	public long getJavaFreeMemory() {
		return Runtime.getRuntime().freeMemory() / KB;
	}

	/**
	 * @return 内存总量KB
	 */
	public long getJavaTotalMemory() {
		return Runtime.getRuntime().totalMemory() / KB;
	}

	/**
	 * @return 最大允许使用的内存KB
	 */
	public long getJavaMaxMemory() {
		return Runtime.getRuntime().maxMemory() / KB;
	}

	/**
	 * @return 总的物理内存KB
	 */
	public long getTotalPhysicalMemorySize() {
		return aOperatingSystemMXBean.getTotalPhysicalMemorySize() / KB;
	}

	/**
	 * @return 剩余的物理内存KB
	 */
	public long getFreePhysicalMemorySize() {
		return aOperatingSystemMXBean.getFreePhysicalMemorySize() / KB;
	}

	/**
	 * @return 所有磁盘总大小KB
	 */
	public long getAllTotalSpace() {
		long totalSpace = 0;
		File[] roots = File.listRoots();
		for (File file : roots) {
			totalSpace += file.getTotalSpace();
		}
		return totalSpace / KB;
	}

	/**
	 * @return 所有磁盘可用大小KB
	 */
	public long getAllFreeSpace() {
		long freeSpace = 0;
		File[] roots = File.listRoots();
		for (File file : roots) {
			freeSpace += file.getFreeSpace();
		}
		return freeSpace / KB;
	}

	/**
	 * 屏幕分辨率:宽
	 */
	public int getScreenWidth() {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}

	/**
	 * 屏幕分辨率:高
	 */
	public int getScreenHeight() {
		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}
}
