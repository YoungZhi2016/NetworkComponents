package com.winonetech.jhpplugins.index;

import com.winonetech.jhpplugins.akka.AkkaRemoteSystem;

public class OpenPlugin {

	public static void main(String[] args) {
		AkkaRemoteSystem.init();
		IndexPlugins.getInstance().startServer();
	}
}
