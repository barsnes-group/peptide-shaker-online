package com.uib.web.peptideshaker.listeners;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import javax.servlet.ServletContext;

/**
 * This class responsible for cleaning folders after user session expire
 *
 * @author Yehia Farag
 */
public class VaadinSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent hse) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
        try {
            String userDataFolderUrl = hse.getSession().getAttribute("userDataFolderUrl") + "";
            File user_folder = new File(userDataFolderUrl);
            if (user_folder.exists()) {
                for (File tFile : user_folder.listFiles()) {
                    deletFile(tFile);
                }
            }
            Files.deleteIfExists(user_folder.toPath());
            System.out.println("at session is ready to distroy ..Good bye...folder (" + user_folder.getName() + " are cleaned (" + ") and folder exist (" + user_folder.exists() + ")");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        ScheduledFuture schedulerFuture = (ScheduledFuture) hse.getSession().getAttribute("schedulerfuture");
        if (schedulerFuture != null) {
            schedulerFuture.cancel(true);
        }
        ScheduledExecutorService scheduler = (ScheduledExecutorService) hse.getSession().getAttribute("scheduler");
        if (scheduler != null) {
            scheduler.shutdown();
            System.out.println("at scheduler shoutdown ");
        }
//        VaadinSession.getCurrent().close();
    }

    private void deletFile(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            Files.deleteIfExists(file.toPath());

        }

    }

    private void deleteDirectory(File file) throws IOException {
        for (File tFile : file.listFiles()) {
            if (tFile.isDirectory()) {
                deleteDirectory(tFile);
            } else {
                Files.deleteIfExists(tFile.toPath());
            }
        }
        Files.deleteIfExists(file.toPath());
    }

}
