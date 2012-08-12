package com.sociallangoliers.tasks;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Samir Faci
 * Date: 8/9/12
 * Time: 7:01 PM
 */
public class CleanUpScheduler {
	static Logger logger = Logger.getLogger(CleanUpScheduler.class);

	static {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	CleanUpScheduler() throws SchedulerException {
		SchedulerFactory sf=new StdSchedulerFactory();
		Scheduler sched=sf.getScheduler();
		sched.start();
		JobDetail jd=new JobDetail("cleanup",sched.DEFAULT_GROUP, CleanUp.class);
		SimpleTrigger st=new SimpleTrigger("mytrigger",sched.DEFAULT_GROUP,new Date(),
				null,SimpleTrigger.REPEAT_INDEFINITELY,60L*1000L);
		sched.scheduleJob(jd, st);
	}

	public static void main(String args[]) {

		try {
			new CleanUpScheduler();
		} catch (SchedulerException e) {
			logger.error(e);

		}

	}
}
