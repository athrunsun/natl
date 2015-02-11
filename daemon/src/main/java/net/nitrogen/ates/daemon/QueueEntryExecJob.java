package net.nitrogen.ates.daemon;

import net.nitrogen.ates.core.exec.ExecManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QueueEntryExecJob implements Job {
	private ExecManager execManager = new ExecManager();

	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		this.execManager.fetchAndExecQueueEntry();
	}
}
