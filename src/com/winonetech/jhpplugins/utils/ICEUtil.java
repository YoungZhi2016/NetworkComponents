package com.winonetech.jhpplugins.utils;

import com.winone.dhmp.rpc.service.terminalManage.TerminalServicePrx;

public final class ICEUtil {

	/**
	 * 
	 * @return 获取 新的一个TerminalServicePrx
	 */
	public static TerminalServicePrx geTerminalServicePrx() {
		return (TerminalServicePrx) RpcClientUtil.getServicePrx(TerminalServicePrx.class);
	}
}
