package com.winonetech.jhpplugins.akka;

import akka.actor.UntypedActor;

public class SenderActor extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Throwable {
		System.out.println("SenderActor onReceive: " + message);
	}

}
