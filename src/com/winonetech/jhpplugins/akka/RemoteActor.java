package com.winonetech.jhpplugins.akka;

import akka.actor.UntypedActor;

public class RemoteActor extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Throwable {
		System.out.println("RemoteActor onReceive: " + message);

		getSender().tell("收到: ", getSelf());
	}

}
