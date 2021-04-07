package com.uib.web.peptideshaker.listeners;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
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
        System.out.println("at session created ");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
        if (VaadinSession.getCurrent() != null) {
            AppManagmentBean appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
            appManagmentBean.getUserHandler().cleanGalaxyHistory();   
            File temp_folder = new File(appManagmentBean.getAppConfig().getLocalFileSystemFolderPath());
            if (temp_folder.exists()) {
                for (File tFile : temp_folder.listFiles()) {
                    try {
                        deletFile(tFile);
                    } catch (IOException ex) {
                        System.out.println("at error " + VaadinContextListener.class.getName() + "  line 35 " + ex);
                    }
                }
            }  
            appManagmentBean.reset();
            VaadinSession.getCurrent().close(); 
        }
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
