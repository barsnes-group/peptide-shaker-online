
package com.uib.web.peptideshaker.listeners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *Context listener to perform cleaning on restarting the system
 * @author Yehia Farag
 */
public class VaadinContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("at welcome back to life :-) ");
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
