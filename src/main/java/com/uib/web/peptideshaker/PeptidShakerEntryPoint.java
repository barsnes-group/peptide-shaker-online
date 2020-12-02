package com.uib.web.peptideshaker;

import com.uib.web.peptideshaker.listeners.VaadinSessionControlListener;
import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.*;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

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
 * @author Yehia Farag
 */
@Push(PushMode.MANUAL)
@Theme("webpeptideshakertheme")
@JavaScript({"../../VAADIN/js/venn.js", "../../VAADIN/js/myD3library.js", "../../VAADIN/js/myD3component-connector.js", "../../VAADIN/js/d3.v5.min.js", "../../VAADIN/litemol/js/LiteMol-plugin.js", "../../VAADIN/litemol/js/mylitemol-connector.js", "../../VAADIN/litemol/js/mylitemollibrary.js", "../../VAADIN/litemol/js/LiteMol-example.js?lmversion=1518789385303", "https://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js", "https://cdnjs.cloudflare.com/ajax/libs/jquery.touch/1.1.0/jquery.touch.min.js", "../../VAADIN/js/mylibrary.js", "../../VAADIN/js/mycomponent-connector.js", "https://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js", "https://cdnjs.cloudflare.com/ajax/libs/jquery.touch/1.1.0/jquery.touch.min.js", "../../VAADIN/js/mylibrary2.js", "../../VAADIN/js/mycomponent-connector2.js", "../../VAADIN/js/jquery.mousewheel.js"})
public class PeptidShakerEntryPoint extends UI {

    private Label notification;
    private Window notificationWindow;
    private WebPeptideShakerApp webPeptideShakerApp;

    /**
     * The entry point for the application .
     *
     * @param vaadinRequest represents the incoming request
     */
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        ScheduledFuture schedulerFuture = (ScheduledFuture) VaadinSession.getCurrent().getAttribute("schedulerfuture");
        if (schedulerFuture != null) {
            schedulerFuture.cancel(true);
        }
        ScheduledExecutorService scheduler = (ScheduledExecutorService) VaadinSession.getCurrent().getAttribute("scheduler");
        if (scheduler != null) {
            scheduler.shutdown();
        }

        Config config = new Config();
        notificationWindow = new Window();
        try {
            PeptidShakerEntryPoint.this.addStyleName("uicontainer");
            PeptidShakerEntryPoint.this.setSizeFull();
            notification = new Label("Use the device in landscape mode <center><i>(recommended)</i></center>", ContentMode.HTML);
            notification.setStyleName("mobilealertnotification");
            notificationWindow.setStyleName("mobilealertnotification");
            notificationWindow.setClosable(true);
            notificationWindow.setModal(false);
            notificationWindow.setWindowMode(WindowMode.NORMAL);
            notificationWindow.setDraggable(false);
            notificationWindow.setResizable(false);
            if (!UI.getCurrent().getWindows().contains(notificationWindow)) {
                UI.getCurrent().addWindow(notificationWindow);
            }
            notificationWindow.setVisible(false);
            notificationWindow.setContent(notification);

            if (VaadinSessionControlListener.getActiveSessions() < config.getMaximumAllowedUsers()) {
                VaadinSession.getCurrent().getSession().setMaxInactiveInterval(60 * 1);
                webPeptideShakerApp = new WebPeptideShakerApp(config.getGalaxyServerUrl());
                PeptidShakerEntryPoint.this.setContent(webPeptideShakerApp.getApplicationUserInterface());
                /**
                 * On resize the browser re-arrange all the created pop-up
                 * windows to the page center.
                 */
                Page.getCurrent().addBrowserWindowResizeListener((Page.BrowserWindowResizeEvent event) -> {
                    config.setPortraitScreenMode(Page.getCurrent().getBrowserWindowWidth() < Page.getCurrent().getBrowserWindowHeight());
                    updateMainStyleMode(config.isMobileDeviceStyle(), config.isPortraitScreenMode());
                    UI.getCurrent().getWindows().forEach((w) -> {
                        w.center();
                    });
                });
                updateMainStyleMode(config.isMobileDeviceStyle(), config.isPortraitScreenMode());
                Page.getCurrent().setTitle("PeptideShaker Online");
            } else {
                com.vaadin.ui.JavaScript.getCurrent().execute("alert('Sorry current users reach the maximum please try again later')");
                VaadinSession.getCurrent().getSession().invalidate();
                return;
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            System.err.println("Error UI Class : " + e);
        }
        try {
            UI.getCurrent().getConnectorTracker().cleanConnectorMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        VaadinSession.getCurrent().setErrorHandler((com.vaadin.server.ErrorEvent event) -> {
            System.out.println("Error handler: " + event.getThrowable().getCause());

        });
        String requestToShare = Page.getCurrent().getLocation().toString();

        if (requestToShare.contains(".error")) {
            webPeptideShakerApp.loginAsGuest();
            Notification.show("Error", "Not valid sharing link", Notification.Type.TRAY_NOTIFICATION);

        } else if (!requestToShare.contains("toShare_-_")) {
            webPeptideShakerApp.loginAsGuest();
        } else if (requestToShare.contains("toShare_-_")) {
            webPeptideShakerApp.retriveToShareDataset();
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
            notificationWindow.setVisible(false);
        } else {
            webPeptideShakerApp.getApplicationUserInterface().removeStyleName("portraitmode");
        }
        if (mobileDeviceStyle && portrait) {
            notificationWindow.setVisible(true);
        } else {
            notificationWindow.setVisible(false);
        }
    }

    /**
     * Main application Servlet.
     */
    @WebServlet(urlPatterns = "/*", name = "PeptidShakerUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = PeptidShakerEntryPoint.class, productionMode = true, resourceCacheTime = 0)
//, resourceCacheTime = 1
    public static class PeptidShakerUIServlet extends VaadinServlet {

        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) {
            try {
                super.service(request, response); //To change body of generated methods, choose Tools | Templates.
            } catch (ConcurrentModificationException | IOException | ServletException e) {
                System.out.println("exception 3 "+e);
            }

        }

        @Override
        public void init(ServletConfig servletConfig) {

            try {
                super.init(servletConfig);
            } catch (ServletException e) {
                System.out.println("exception 1");
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
                System.out.println("exception 2");
            }

//           
//            getService().addSessionInitListener((SessionInitEvent event) -> {
//                event.getSession().addBootstrapListener(new BootstrapListener() {
//
//                    @Override
//                    public void modifyBootstrapPage(BootstrapPageResponse response) {
//                        response.getDocument().head().prependElement("meta").attr("name", "'viewport'").attr("content", "'initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,width=device-width,user-scalable=no'").attr("Set-Cookie", "'cross-site-cookie=name; SameSite=None; Secure'");
//                        response.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9,application/javascript");
//                    }
//
//                    @Override
//                    public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
//                    }
//                });
//            });
        }
    }

}
