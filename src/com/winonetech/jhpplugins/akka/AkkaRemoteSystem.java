package com.winonetech.jhpplugins.akka;

import java.io.File;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.winonetech.jhpplugins.utils.ConfigUtil;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;

public class AkkaRemoteSystem {

	public static final Config config = ConfigFactory
			.parseFile(new File(ConfigUtil.getRootPath() + "/config/akkaPlugins.conf"));

	public static final ActorSystem localSystem = ActorSystem.create("RemoteSystem", config);

	public static final ActorRef remoteActor = AkkaRemoteSystem.localSystem
			.actorOf(new RoundRobinPool(20).props(Props.create(RemoteActor.class)), "remote");

	public static final ActorRef senderActor = AkkaRemoteSystem.localSystem
			.actorOf(new RoundRobinPool(20).props(Props.create(SenderActor.class)), "sender");

	public static void init() {

	}

	public static ActorSelection getRemoteRef(String ip, int port) {
		StringBuilder comm = new StringBuilder("akka.tcp://RemoteSystem@");
		comm.append(ip);
		comm.append(":");
		comm.append(port);
		comm.append("/user/remote");
		return AkkaRemoteSystem.localSystem.actorSelection(comm.toString());
	}

}
