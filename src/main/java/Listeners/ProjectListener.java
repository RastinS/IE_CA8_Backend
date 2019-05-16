package Listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class ProjectListener implements ServletContextListener {
	private ScheduledExecutorService scheduler;

	@Override
	public void contextInitialized (ServletContextEvent event) {
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new PeriodProject(), 1, 1, TimeUnit.MINUTES);
	}

	@Override
	public void contextDestroyed (ServletContextEvent event) {
		scheduler.shutdownNow();
	}


}
