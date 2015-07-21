package com.ga2sa.scheduler;

import play.libs.Akka;
import akka.actor.ActorRef;
import akka.actor.Props;

public class Scheduler {
	
	private static ActorRef scheduler = Akka.system().actorOf(Props.create(SchedulerManager.class));
	
	public static ActorRef getInstance() {
		return scheduler;
	}
}
