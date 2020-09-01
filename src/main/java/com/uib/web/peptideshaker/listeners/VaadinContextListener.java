package com.uib.web.peptideshaker.listeners;

import com.vaadin.server.VaadinServlet;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.ServletContext;

/**
 * Context listener to perform cleaning on restarting the system
 *
 * @author Yehia Farag
 */
public class VaadinContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
         ServletContext scx =sce.getServletContext();
            if (scx.getAttribute("maxusernumb") == null) {
                scx.setAttribute("maxusernumb", scx.getInitParameter("maxusernumb"));
                scx.setAttribute("currentuser", 0);
            }
            System.out.println("at welcome back to life :-) "+scx.getInitParameter("maxusernumb"));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        String tempFolderUrl = sce.getServletContext().getAttribute("tempFolder") + "";
        File temp_folder = new File(tempFolderUrl);
        if (temp_folder.exists()) {
            for (File tFile : temp_folder.listFiles()) {
                try {
                    deletFile(tFile);


                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
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
