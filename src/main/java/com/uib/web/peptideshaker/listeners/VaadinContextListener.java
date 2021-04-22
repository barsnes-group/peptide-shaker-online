package com.uib.web.peptideshaker.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Context listener to perform cleaning on restarting the system
 *
 * @author Yehia Mokhtar Farag
 */
public class VaadinContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("at welcome back to life  :-) ");
        Path path = null;
        try {

            path = Files.createTempDirectory("t");
        } catch (IOException ex) {
            System.out.println("at error " + VaadinContextListener.class.getName() + "  line 25 " + ex);
        }
        if (path != null) {
            String tempFolderUrl = path.toFile().getParentFile().getAbsolutePath();
            File temp_folder = new File(tempFolderUrl);
            if (temp_folder.exists()) {
                for (File tFile : temp_folder.listFiles()) {
                    try {
                        deleteFile(tFile);
                    } catch (IOException ex) {
                        System.out.println("at error " + VaadinContextListener.class.getName() + "  line 35 " + ex);
                    }
                }
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    private void deleteFile(File file) throws IOException {
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
