package com.uib.web.peptideshaker;

import com.uib.web.peptideshaker.listeners.VaadinSessionControlListener;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.*;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.UI;
import java.io.IOException;
import java.util.ConcurrentModificationException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialised using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialise non-component functionality.
 *
 * @author Yehia Mokhtar Farag
 */
@Push(transport = Transport.LONG_POLLING)
@Theme("webpeptideshakertheme")
@JavaScript({"../../VAADIN/js/venn.js", "../../VAADIN/js/myD3library.js", "../../VAADIN/js/myD3component-connector.js", "../../VAADIN/js/d3.v5.min.js", "../../VAADIN/litemol/js/LiteMol-plugin.js", "../../VAADIN/litemol/js/mylitemol-connector.js", "../../VAADIN/litemol/js/mylitemollibrary.js", "../../VAADIN/litemol/js/LiteMol-example.js?lmversion=1518789385303", "https://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js", "https://cdnjs.cloudflare.com/ajax/libs/jquery.touch/1.1.0/jquery.touch.min.js", "../../VAADIN/js/mylibrary.js", "../../VAADIN/js/mycomponent-connector.js", "https://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js", "https://cdnjs.cloudflare.com/ajax/libs/jquery.touch/1.1.0/jquery.touch.min.js", "../../VAADIN/js/mylibrary2.js", "../../VAADIN/js/mycomponent-connector2.js", "../../VAADIN/js/jquery.mousewheel.js"})
public class PeptidShakerEntryPoint extends UI {

    private AppMainContainer webPeptideShakerApp;
    private AppManagmentBean appManagmentBean;

    /**
     * The entry point for the application .
     *
     * @param vaadinRequest represents the incoming request
     */
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        AppManagmentBean oldManager = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        if (oldManager != null) {
            if (oldManager.getUserHandler() != null && oldManager.isAvailableGalaxy()) {
                oldManager.getUserHandler().clearHistory();
            }
            VaadinSession.getCurrent().close();
            Page.getCurrent().reload();
            return;
        }
        appManagmentBean = new AppManagmentBean();
        VaadinSession.getCurrent().setAttribute(CONSTANT.APP_MANAGMENT_BEAN, appManagmentBean);

        try {
            UI.getCurrent().getConnectorTracker().cleanConnectorMap();
            PeptidShakerEntryPoint.this.addStyleName("uicontainer");
            PeptidShakerEntryPoint.this.setSizeFull();
            if (VaadinSessionControlListener.getActiveSessions() < appManagmentBean.getAppConfig().getMaximumAllowedUsers()) {
                VaadinSession.getCurrent().getSession().setMaxInactiveInterval(60 * 15);
                webPeptideShakerApp = new AppMainContainer();
                /**
                 * On resize the browser re-arrange all the created pop-up
                 * windows to the page centre.
                 */
                Page.getCurrent().addBrowserWindowResizeListener((Page.BrowserWindowResizeEvent event) -> {
                    appManagmentBean.getAppConfig().setPortraitScreenMode(Page.getCurrent().getBrowserWindowWidth() < Page.getCurrent().getBrowserWindowHeight());
                    updateMainStyleMode(appManagmentBean.getAppConfig().isMobileDeviceStyle(), appManagmentBean.getAppConfig().isPortraitScreenMode());
                    UI.getCurrent().getWindows().forEach((w) -> {
                        w.center();
                    });
                });
                updateMainStyleMode(appManagmentBean.getAppConfig().isMobileDeviceStyle(), appManagmentBean.getAppConfig().isPortraitScreenMode());
                Page.getCurrent().setTitle("PeptideShaker Online");
            } else {
                appManagmentBean.getNotificationFacade().showAlertNotification("Sorry current users reach the maximum please try again later");
                VaadinSession.getCurrent().close();
                return;
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("at error " + PeptidShakerEntryPoint.class.getName() + " line: 85 " + e);
        }

        VaadinSession.getCurrent().setErrorHandler((com.vaadin.server.ErrorEvent event) -> {
           System.out.println("at error " + PeptidShakerEntryPoint.class.getName() + " line: 89 " + event.getThrowable().getCause());
        });

        //if there is capacity check the routes        
        String requestToShare = Page.getCurrent().getLocation().toString();
        if (requestToShare.endsWith(".error")) {
            appManagmentBean.getNotificationFacade().showErrorNotification("Not valid sharing link");
        }
    }

    /**
     * update main style for the application.
     *
     * @param mobileDeviceStyle the device is mobile phone
     * @param portrait the screen is in portrait mode
     */
    private void updateMainStyleMode(boolean mobileDeviceStyle, boolean portrait) {
        if (webPeptideShakerApp == null) {
            return;
        }
        if (portrait) {
            webPeptideShakerApp.getApplicationUserInterface().addStyleName("portraitmode");
            appManagmentBean.getNotificationFacade().hideLandscapeModeNotification();
        } else {
            webPeptideShakerApp.getApplicationUserInterface().removeStyleName("portraitmode");
        }
        if (mobileDeviceStyle && portrait) {
            appManagmentBean.getNotificationFacade().showLandscapeModeNotification();
        } else {
            appManagmentBean.getNotificationFacade().hideLandscapeModeNotification();
        }
    }

    /**
     * Main application Servlet.
     */
    @WebServlet(urlPatterns = "/*", name = "PeptidShakerServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = PeptidShakerEntryPoint.class, productionMode = true, resourceCacheTime = 0)
    public static class PeptidShakerServlet extends VaadinServlet {

        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) {
            try {
                super.service(request, response); //To change body of generated methods, choose Tools | Templates.
            } catch (ConcurrentModificationException | IOException | ServletException e) {
                System.out.println("at error " + PeptidShakerEntryPoint.class.getName() + " line: 144 " + e);
            }

        }

        @Override
        public void init(ServletConfig servletConfig) {

            try {
                super.init(servletConfig);
            } catch (ServletException e) {
                System.out.println("at error " + PeptidShakerEntryPoint.class.getName() + " line: 153 " + e);
            }
            /**
             * VaadinSessionListener
             */
            getService().addSessionInitListener(new VaadinSessionControlListener.VaadinSessionInitListener());
            getService().addSessionDestroyListener(new VaadinSessionControlListener.VaadinSessionDestroyListener());

        }

        @Override
        protected void servletInitialized() {
            try {
                super.servletInitialized();
            } catch (ServletException e) {
                System.out.println("at error " + PeptidShakerEntryPoint.class.getName() + " line: 171 " + e);
            }
//            });
        }
    }

}
