/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.listeners;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;

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
            AppManagmentBean appManagmentBean = (AppManagmentBean) event.getSession().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
            if (appManagmentBean != null) {
                if (appManagmentBean.isAvailableGalaxy()) {
                    appManagmentBean.getUserHandler().clearHistory();
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
}
