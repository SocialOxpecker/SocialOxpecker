package org.techieworld.tasks;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * User: Samir Faci
 * Date: 8/9/12
 * Time: 6:59 PM
 */
public class CleanUp implements Job {
	Logger logger = Logger.getLogger(CleanUp.class);

	public CleanUp() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("Hello World Quartz Scheduler: " + new Date());

	}
}
