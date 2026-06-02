package listener;

import dao.NoteDAO;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class TrashCleanupListener implements ServletContextListener {
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable cleanupTask = new Runnable() {
            @Override
            public void run() {
                System.out.println("[Background Task] Running trash cleanup process...");
                new NoteDAO().cleanExpiredTrash();
            }
        };

        scheduler.scheduleAtFixedRate(cleanupTask, 0, 1, TimeUnit.DAYS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            scheduler.shutdownNow();
            System.out.println("[Background Task] Trash cleanup process stopped.");
        }
    }
}