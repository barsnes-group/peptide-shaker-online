/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.listeners;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.JavaScript;
import javax.servlet.ServletContext;

/**
 *
 * @author y-mok
 */
public class VaadinSessionControlListener {

    private static volatile int activeSessions = 0;

    public static class VaadinSessionInitListener implements SessionInitListener {

        @Override
        public void sessionInit(SessionInitEvent event) throws ServiceException {
                incSessionCounter();
        }
    }

    public static class VaadinSessionDestroyListener implements SessionDestroyListener {

        @Override
        public void sessionDestroy(SessionDestroyEvent event) {
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
}
