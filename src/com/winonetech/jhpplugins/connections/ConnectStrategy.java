package com.winonetech.jhpplugins.connections;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author ywkj 连接策略
 *
 */
public interface ConnectStrategy {

	/**
	 * 创建一个定长线程池，支持定时及周期性任务执行
	 */
	ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(1);

	/**
	 * 周期时间
	 */
	long PERIOD = 30;

	/**
	 * 延迟加载
	 */
	long INITIAL_DELAY = 1;

	/**
	 * 时间单位
	 */
	TimeUnit UNIT = TimeUnit.SECONDS;

}
