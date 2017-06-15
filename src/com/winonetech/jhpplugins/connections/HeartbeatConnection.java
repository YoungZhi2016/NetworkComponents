package com.winonetech.jhpplugins.connections;

import java.time.LocalTime;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import com.winone.dhmp.rpc.service.terminalManage.TerminalServicePrx;
import com.winonetech.jhpplugins.utils.Logs;
import com.winonetech.jhpplugins.utils.RpcClientUtil;

/**
 * @author ywkj
 * 
 *         heart
 */
public class HeartbeatConnection {

	private LocalTime localTime;
	private ExecutorService executorService;

	private static class HeartbeatConnectionHolder {
		private static HeartbeatConnection connection = new HeartbeatConnection();
	}

	public static HeartbeatConnection getInstance() {
		return HeartbeatConnectionHolder.connection;
	}

	private HeartbeatConnection() {
		executorService = Executors.newCachedThreadPool();
	}

	/**
	 * 心跳开始
	 */
	public void startHeart() {
		add5Minutes(LocalTime.now());
		HeartTask heartTask = new HeartTask(geTerminalServicePrx());
		final TimerTask connectTask = new TimerTask() {
			@Override
			public void run() {
				if (LocalTime.now().isAfter(localTime)) {
					add5Minutes(LocalTime.now());
					heartTask.setServicePrx(geTerminalServicePrx());// 传新的连接
				}

				Future<Boolean> future = executorService.submit(heartTask);
				try {
					future.get(ConnectStrategy.PERIOD, ConnectStrategy.UNIT);// 30S超时
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					Logs.error("not received: " + e.getCause());
					future.cancel(true);
				}
			}
		};
		ConnectStrategy.SERVICE.scheduleAtFixedRate(connectTask, ConnectStrategy.INITIAL_DELAY, ConnectStrategy.PERIOD,
				ConnectStrategy.UNIT);
	}

	/**
	 * 加五分钟
	 */
	private void add5Minutes(LocalTime time) {
		this.localTime = time.plusMinutes(5);
	}

	/**
	 * 
	 * @return 获取 新的一个TerminalServicePrx
	 */
	public static TerminalServicePrx geTerminalServicePrx() {
		return (TerminalServicePrx) RpcClientUtil.getServicePrx(TerminalServicePrx.class);
	}

}
