package com.ga2sa.scheduler;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import models.Job;
import models.dao.JobDAO;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class SchedulerManager extends UntypedActor {
	
	@Override
	public void onReceive(Object obj) throws Exception {
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		if (obj instanceof String) {
			String command = (String) obj;
			switch (command) {
				case "start" 	: 
					Logger.debug("******* START SCHEDULER *******"); 
					this.start();
					break;
				case "update" 	: 
					Logger.debug("******* UPDATE LIST JOBS *******"); 
					this.update();
					break;
			}
		}
	}
	
	private void update() {
		
		Job job = JobDAO.getLastJob();
		
		Logger.debug("JOB   " + job.getName());
		
		Calendar currentDate = Calendar.getInstance();
		
		runJob(job, currentDate);
		
	}

	private void start() {
		
		List<Job> jobs = JobDAO.getJobsForScheduler();
		
		Calendar currentDate = Calendar.getInstance();
		
		jobs.forEach((job) -> runJob(job, currentDate));

	}
	
	private void runJob(Job job, Calendar currentDate) {
		
		Calendar scheduleDate = Calendar.getInstance();
		Calendar startDate = Calendar.getInstance();
		
		
		ActorRef backgroundJob = Akka.system().actorOf(Props.create(BackgroundJob.class));
		
		if (job.getNextStartTime() != null) {
			startDate.setTime(job.getNextStartTime());
			if (startDate.after(currentDate)) scheduleDate.setTime(job.getNextStartTime());
		} else {
			startDate.setTime(job.getStartTime());
			if (startDate.after(currentDate)) {
				Logger.debug("AFTER");
				scheduleDate.setTime(job.getStartTime());
			}
		}
		
		long offset = (scheduleDate.getTimeInMillis() - currentDate.getTimeInMillis()) / 1000;
		
		Logger.debug("OFFSET " + String.valueOf(offset) );

		if (job.isRepeated()) {
			
			Integer duration = (job.getRepeatPeriod().equals("week")) ? 7 : 1;
			Integer timeUnit = (job.getRepeatPeriod().equals("week") || job.getRepeatPeriod().equals("day") ) ? Calendar.DATE : Calendar.MONTH;
			
			Calendar period = Calendar.getInstance();
			period.setTime(scheduleDate.getTime());
			
			period.add(timeUnit, duration);
			
			// for testing
			//period.add(Calendar.MINUTE, 1);
			
			Logger.debug("NEXT START " + period.getTime().toString());
			
			job.setNextStartTime(new Timestamp(period.getTimeInMillis()));
			
			long offsetPeriod = (period.getTimeInMillis() - scheduleDate.getTimeInMillis()) / 1000;
			
			Akka.system().scheduler().schedule(FiniteDuration.create(offset, TimeUnit.SECONDS), FiniteDuration.create(offsetPeriod, TimeUnit.SECONDS), backgroundJob, job, Akka.system().dispatcher(), ActorRef.noSender());
		
		} else {
			
			Akka.system().scheduler().scheduleOnce(FiniteDuration.create(offset, TimeUnit.SECONDS), backgroundJob, job, Akka.system().dispatcher(), ActorRef.noSender());
		}
	}

}
