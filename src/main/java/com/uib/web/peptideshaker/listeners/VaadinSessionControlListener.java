
package com.uib.web.peptideshaker.listeners;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author Yehia Mokhtar Farag
 */
public class VaadinSessionControlListener {

    private static volatile int activeSessions = 0;

    public static class VaadinSessionInitListener implements SessionInitListener {

        @Override
        public void sessionInit(SessionInitEvent event) throws ServiceException {
            AppManagmentBean appManagmentBean = (AppManagmentBean) event.getSession().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
            if (appManagmentBean != null) {
                if (appManagmentBean.isAvailableGalaxy()) {
                    appManagmentBean.getUserHandler().cleanGalaxyHistory();
                }

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
            }
            incSessionCounter();
        }
    }

    public static class VaadinSessionDestroyListener implements SessionDestroyListener {

        @Override
        public void sessionDestroy(SessionDestroyEvent event) {
            AppManagmentBean appManagmentBean = (AppManagmentBean) event.getSession().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
            if (appManagmentBean != null) {
                if (appManagmentBean.isAvailableGalaxy()) {
                    appManagmentBean.getUserHandler().cleanGalaxyHistory();
                }

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
            }

            decSessionCounter();
        }
    }

    public static Integer getActiveSessions() {
        return activeSessions;
    }

    private synchronized static void decSessionCounter() {
        if (activeSessions > 0) {
            activeSessions--;
        }
    }

    private synchronized static void incSessionCounter() {
        activeSessions++;
    }

    private synchronized static void deletFile(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            Files.deleteIfExists(file.toPath());

        }

    }

    private synchronized static void deleteDirectory(File file) throws IOException {
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
