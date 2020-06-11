package com.uib.web.peptideshaker.listeners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * This class responsible for cleaning folders after user session expire
 *
 * @author Yehia Farag
 */
public class VaadinSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent hse) {
        System.out.println("at *****welcome user*******");

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
        try { 
        String userDataFolderUrl = hse.getSession().getAttribute("userDataFolderUrl").toString();
        File user_folder = new File(userDataFolderUrl);
       if (user_folder.exists()) {
            for (File tFile : user_folder.listFiles()) {               
                    deletFile(tFile);     
            }
        }
         Files.deleteIfExists(user_folder.toPath());
        System.out.println("at session is ready to distroy ..Good bye...folder (" + user_folder.getName() + " are cleaned ("  + ") and folder exist (" + user_folder.exists() + ")");
        } catch (IOException ex) {
            ex.printStackTrace();
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
