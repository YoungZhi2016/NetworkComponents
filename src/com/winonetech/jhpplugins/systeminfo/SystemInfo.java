package com.winonetech.jhpplugins.systeminfo;

import com.winone.dhmp.rpc.service.terminalManage.TerminalServicePrx;
import com.winone.dhmp.rpc.service.terminalManage.TerminalSysInfo;
import com.winone.dhmp.rpc.service.terminalManage.TerminalSysmonit;
import com.winonetech.jhpplugins.utils.Configs;
import com.winonetech.jhpplugins.utils.Logs;
import com.winonetech.jhpplugins.utils.SystemInfoUtil;

public class SystemInfo {

	private TerminalServicePrx aTerminalServicePrx;

	public SystemInfo(TerminalServicePrx aTerminalServicePrx) {
		this.aTerminalServicePrx = aTerminalServicePrx;
	}

	public void sendInfo() {
		SystemInfoUtil aInfoUtil = SystemInfoUtil.getInstance();
		TerminalSysmonit aTerminalSysmonit = new TerminalSysmonit();
		aTerminalSysmonit.osname = aInfoUtil.getOsName();
		aTerminalSysmonit.osversion = aInfoUtil.getOsVersion();
		aTerminalSysmonit.osbit = aInfoUtil.getOsArch();

		aTerminalSysmonit.playerver = "";// 本地播放器版本

		double MB = 1024;
		aTerminalSysmonit.alcpu = (int) (aInfoUtil.getTotalPhysicalMemorySize() / MB);
		aTerminalSysmonit.avcpu = (int) (aInfoUtil.getFreePhysicalMemorySize() / MB);
		aTerminalSysmonit.alram = (int) (aInfoUtil.getJavaTotalMemory() / MB);
		aTerminalSysmonit.avram = (int) (aInfoUtil.getJavaFreeMemory() / MB);
		aTerminalSysmonit.aldk = (int) (aInfoUtil.getAllTotalSpace() / MB / MB);// G
		aTerminalSysmonit.avdk = (int) (aInfoUtil.getAllFreeSpace() / MB / MB);// G

		TerminalSysInfo aTerminalSysInfo = null;
		try {

			int monitorId = Integer.parseInt(Configs.getTerminalNo());// 本机终端id

			aTerminalSysInfo = new TerminalSysInfo(monitorId, aTerminalSysmonit, aInfoUtil.getIPAddress(),
					aInfoUtil.getMacAddress(), aInfoUtil.getScreenWidth(), aInfoUtil.getScreenHeight(),
					Integer.parseInt(Configs.getPort()));
		} catch (Exception e) {
			Logs.error("获取本机IP或者MAC地址失败");
		}
		aTerminalServicePrx.setSystemInfo(aTerminalSysInfo);
	}

}
