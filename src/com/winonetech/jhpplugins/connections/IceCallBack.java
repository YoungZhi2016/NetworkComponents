package com.winonetech.jhpplugins.connections;

import java.util.Date;

import Ice.Connection;

public class IceCallBack implements Ice.ConnectionCallback {

	@Override
	public void closed(Connection arg0) {
		// TODO Auto-generated method stub
		System.out.println(new Date());
		System.out.println("c"+arg0.toString());
	}

	@Override
	public void heartbeat(Connection arg0) {
		// TODO Auto-generated method stub
		System.out.println("hb");
	}

}
