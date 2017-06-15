package com.winonetech.jhpplugins.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import Ice.ObjectPrx;

/**
 * Copyright 漏 2016棰栫綉绉戞妧銆怶INONE銆�. All rights reserved.
 * 
 * @ClassName: RpcClientUtil
 * @Description: TODO
 * @author: shanben-CN EMAIL锛歴hanben11@qq.com
 * @date: 2016骞�12鏈�1鏃� 涓婂崍11:48:50
 * @version: V1.0
 */
public class RpcClientUtil {
	/**
	 * volatile鐢ㄥ湪澶氱嚎绋嬶紝鍚屾鍙橀噺
	 * 鐢ㄦ潵纭繚灏嗗彉閲忕殑鏇存柊鎿嶄綔閫氱煡鍒板叾浠栫嚎绋�,淇濊瘉浜嗘柊鍊艰兘绔嬪嵆鍚屾鍒颁富鍐呭瓨,
	 * 浠ュ強姣忔浣跨敤鍓嶇珛鍗充粠涓诲唴瀛樺埛鏂�. 褰撴妸鍙橀噺澹版槑涓簐olatile绫诲瀷鍚�,
	 * 缂栬瘧鍣ㄤ笌杩愯鏃堕兘浼氭敞鎰忓埌杩欎釜鍙橀噺鏄叡浜殑
	 */
	private static volatile Ice.Communicator ic = null;
	@SuppressWarnings("rawtypes")
	private static Map<Class, ObjectPrx> cls2PrxMap = new HashMap<Class, ObjectPrx>();
	private static Map<String, String> prxStringMap = new HashMap<String, String>();
	/** 涓婃璁块棶鏃堕棿 */
	private static volatile long lastAccessTimestamp;
	/** 妫�娴嬬嚎绋� */
	private static volatile MonitorThread monitorThread;
	/** 绌洪棽瓒呮椂鏃堕棿 鍗曚綅绉� */
	;
	private static long idleTimeOutSeconds = 0;
	/** 杩滅▼鏈嶅姟閫氳浣嶇疆瀛楃涓� */
	private static String iceLocator = null;
	/**
	 * 寤舵椂鍔犺浇Communicator
	 * 
	 * @return Ice.Communicator
	 */
	static {
		prxStringMap.put("UserManageService", ":tcp -p 10001 -h 47.92.6.16:tcp -p 10002 -h 47.92.6.16");
		prxStringMap.put("TtlService", ":tcp -p 10011 -h 47.92.6.16:tcp -p 10012 -h 47.92.6.16");
		prxStringMap.put("SMSService", ":tcp -p 10021 -h 47.92.6.16:tcp -p 10022 -h 47.92.6.16");
		prxStringMap.put("JsbService", ":tcp -p 10031 -h 47.92.6.16:tcp -p 10032 -h 47.92.6.16");
		prxStringMap.put("HppService", ":tcp -p 10041 -h 47.92.6.16:tcp -p 10042 -h 47.92.6.16");
		prxStringMap.put("EmaService", ":tcp -p 10051 -h 47.92.6.16:tcp -p 10052 -h 47.92.6.16");
		prxStringMap.put("VosService", ":tcp -p 10061 -h 47.92.6.16:tcp -p 10062 -h 47.92.6.16");
		prxStringMap.put("ShareService", ":tcp -p 10071 -h 47.92.6.16:tcp -p 10072 -h 47.92.6.16");
	}

	public static Ice.Communicator getIceCommunicator() {
		if (ic == null) {
			synchronized (RpcClientUtil.class) {
				if (ic == null) {
					// ResourceBundle rb = ResourceBundle.getBundle("RpcClient",
					// Locale.ENGLISH);
					Properties rb = new Properties();
					InputStream in;
					try {
						in = new BufferedInputStream(
								new FileInputStream(FileUtils.ROOT_PATH + "/" + "config/RpcClient.properties"));
						rb.load(in);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (rb != null) {
						iceLocator = rb.getProperty("--Ice.Default.Locator");
						idleTimeOutSeconds = Integer.parseInt(rb.getProperty("idleTimeOutSeconds"));
						// System.out.println("Ice client`s locator is " +
						// iceLocator + " proxy cache time out seconds :" +
						// idleTimeOutSeconds);
						String[] initParams = new String[] { "--Ice.Default.Locator=" + iceLocator };
						ic = Ice.Util.initialize(initParams);
						// 鍒涘缓瀹堟姢绾跨▼
						createMonitorThread();
					}
				}
			}
		}
		lastAccessTimestamp = System.currentTimeMillis();
		return ic;
	}

	/**
	 * 鍒涘缓瀹堟姢绾跨▼
	 */
	private static void createMonitorThread() {
		monitorThread = new MonitorThread();
		monitorThread.setDaemon(true);
		monitorThread.start();
	}

	/**
	 * @Title: closeCommunicator
	 * @Description: TODO 鍏抽棴Ice.Communicator锛岄噴鏀捐祫婧�
	 * @param removeServiceCache
	 */
	public static void closeCommunicator(boolean removeServiceCache) {
		synchronized (RpcClientUtil.class) {
			if (ic != null) {
				safeShutdown();
				monitorThread.interrupt();
				if (removeServiceCache && !cls2PrxMap.isEmpty()) {
					try {
						cls2PrxMap.clear();
					} catch (Exception e) {
						// ignore
					}
				}
			}
		}
	}

	/**
	 * @Title: safeShutdown
	 * @Description: TODO 瀹夊叏鍏抽棴Ice.Communicator锛岄噴鏀捐祫婧�
	 */
	private static void safeShutdown() {
		try {
			ic.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ic.destroy();
			ic = null;
		}
	}

	/**
	 * 鐢ㄥ弽灏勬柟寮忓垱寤篛bject Proxy
	 * 
	 * @param communicator
	 * @param serviceCls
	 * @return ObjectPrx
	 */
	@SuppressWarnings("rawtypes")
	private static ObjectPrx createIceProxy(Ice.Communicator communicator, Class serviceCls) {
		ObjectPrx proxy = null;
		String clsName = serviceCls.getName();
		// System.out.println("clsName:"+clsName);
		String serviceName = serviceCls.getSimpleName();
		// System.out.println("serviceName:"+serviceName);
		int pos = serviceName.lastIndexOf("Prx");
		if (pos <= 0) {
			throw new java.lang.IllegalArgumentException("Invalid ObjectPrx class ,class name must end with Prx");
		}
		String realSvName = serviceName.substring(0, pos);
		try {
			// System.out.println("realSvName:"+realSvName);
			Ice.ObjectPrx base = communicator.stringToProxy(realSvName);
			// Ice.ObjectPrx base = communicator.stringToProxy(realSvName +
			// prxStringMap.get(realSvName));
			// System.out.println("base:"+base);
			proxy = (ObjectPrx) Class.forName(clsName + "Helper").newInstance();
			Method m1 = proxy.getClass().getDeclaredMethod("uncheckedCast", ObjectPrx.class);
			proxy = (ObjectPrx) m1.invoke(proxy, base);
			return proxy;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 鐢ㄤ簬瀹㈡埛绔疉PI鑾峰彇Ice鏈嶅姟瀹炰緥鐨勫満鏅�
	 * 
	 * @param serviceCls
	 * @return ObjectPrx
	 */
	@SuppressWarnings("rawtypes")
	public static ObjectPrx getServicePrx(Class serviceCls) {
		ObjectPrx proxy = cls2PrxMap.get(serviceCls);
		if (proxy != null) {
			lastAccessTimestamp = System.currentTimeMillis();
			return proxy;
		}
		proxy = createIceProxy(getIceCommunicator(), serviceCls);
		cls2PrxMap.put(serviceCls, proxy);
		lastAccessTimestamp = System.currentTimeMillis();
		return proxy;
	}

	/** 鍐呴儴闈欐�佸畧鎶ゆ娴嬬嚎绋嬬被 */
	static class MonitorThread extends Thread {
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					Thread.sleep(5000L);
					if (lastAccessTimestamp + idleTimeOutSeconds * 1000L < System.currentTimeMillis()) {
						closeCommunicator(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
