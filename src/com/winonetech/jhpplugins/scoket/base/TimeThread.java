package com.winonetech.jhpplugins.scoket.base;

import com.winonetech.jhpplugins.utils.Logs;

public class TimeThread extends Thread {
	private Communication server;
	private ComunicationClient client;
	private int time;

	public TimeThread(Communication server, ComunicationClient client, int time) {
		this.server = server;
		this.client = client;
		this.time = time;
		start();
	}

	@Override
	public void run() {
		try {
			synchronized (client) {
				client.wait(time);
			}
		} catch (Exception e) {
			Logs.error("TimeThread: " + e.getMessage());
		}
		if (!client.isAccess() && client.isMonite()) {
			server.remote(client);
		}
		client = null;
		server = null;
	}

}
