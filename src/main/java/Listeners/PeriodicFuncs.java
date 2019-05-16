package Listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class PeriodicFuncs implements ServletContextListener {
	private ScheduledExecutorService newProjectsScheduler = Executors.newSingleThreadScheduledExecutor();
	private ScheduledExecutorService deadlineCheckScheduler = Executors.newSingleThreadScheduledExecutor();

	@Override
	public void contextInitialized (ServletContextEvent event) {
		newProjectsScheduler.scheduleAtFixedRate(new PeriodProject(), 1, 1, TimeUnit.MINUTES);
		deadlineCheckScheduler.scheduleAtFixedRate(new ProjectDeadlineChecker(), 1, 1, TimeUnit.MINUTES);
	}

	@Override
	public void contextDestroyed (ServletContextEvent event) {
		newProjectsScheduler.shutdownNow();
		deadlineCheckScheduler.shutdownNow();
	}


}
