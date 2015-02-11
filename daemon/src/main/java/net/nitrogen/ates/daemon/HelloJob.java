package net.nitrogen.ates.daemon;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloJob implements Job {
	private Logger log = LoggerFactory.getLogger(HelloJob.class);

	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log.info("Hello World! - " + DateTime.now().toString("yyyy-MM-dd hh:mm:ss"));
	}
}
