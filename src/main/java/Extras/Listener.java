package Extras;

import DataManagers.DataManager;
import Listeners.PeriodProject;
import Listeners.ProjectDeadlineChecker;
import Listeners.UpdateValidBidders;
import Services.JWTService;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener()
public class Listener implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener, ServletRequestListener {
    private ScheduledExecutorService newProjectsScheduler;
    private ScheduledExecutorService deadlineCheckScheduler;
    private ScheduledExecutorService validBidderUpdateScheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Starting up!");
        newProjectsScheduler = Executors.newSingleThreadScheduledExecutor();
        deadlineCheckScheduler = Executors.newSingleThreadScheduledExecutor();
        validBidderUpdateScheduler = Executors.newSingleThreadScheduledExecutor();
        newProjectsScheduler.scheduleAtFixedRate(new PeriodProject(), 1, 1, TimeUnit.MINUTES);
        deadlineCheckScheduler.scheduleAtFixedRate(new ProjectDeadlineChecker(), 1, 1, TimeUnit.MINUTES);
        validBidderUpdateScheduler.scheduleAtFixedRate(new UpdateValidBidders(), 1, 1, TimeUnit.MINUTES);
        try {
            DataManager.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        HttpServletRequest req = (HttpServletRequest) event.getServletRequest();
        String     username    = JWTService.decodeUsernameJWT(req.getHeader("user-token"));
        req.setAttribute("username", username);
    }

    @Override
    public void contextDestroyed (ServletContextEvent event) {
        newProjectsScheduler.shutdownNow();
        deadlineCheckScheduler.shutdownNow();
        validBidderUpdateScheduler.shutdownNow();
    }

}
