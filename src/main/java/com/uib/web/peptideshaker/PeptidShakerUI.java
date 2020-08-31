package com.uib.web.peptideshaker;

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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

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
@Theme("webpeptideshakertheme")
@JavaScript({"../../VAADIN/js/venn.js", "../../VAADIN/js/myD3library.js", "../../VAADIN/js/myD3component-connector.js", "../../VAADIN/js/d3.v5.min.js", "../../VAADIN/litemol/js/LiteMol-plugin.js", "../../VAADIN/litemol/js/mylitemol-connector.js", "../../VAADIN/litemol/js/mylitemollibrary.js","../../VAADIN/litemol/js/LiteMol-example.js?lmversion=1518789385303", "https://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js", "https://cdnjs.cloudflare.com/ajax/libs/jquery.touch/1.1.0/jquery.touch.min.js", "../../VAADIN/js/mylibrary.js", "../../VAADIN/js/mycomponent-connector.js", "https://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js", "https://cdnjs.cloudflare.com/ajax/libs/jquery.touch/1.1.0/jquery.touch.min.js", "../../VAADIN/js/mylibrary2.js", "../../VAADIN/js/mycomponent-connector2.js", "../../VAADIN/js/jquery.mousewheel.js"})
@Push(PushMode.MANUAL)
public class PeptidShakerUI extends UI {

    private Label notification;
    private boolean mobileDeviceStyle;
    private boolean smallDeviceStyle;
    private boolean portraitScreenMode;
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
        String brwserApp = Page.getCurrent().getWebBrowser().getBrowserApplication();
        int screenWidth = Page.getCurrent().getBrowserWindowWidth();
        int screenHeigh = Page.getCurrent().getBrowserWindowHeight();
        portraitScreenMode = screenWidth < screenHeigh;
        /**
         * case of average screen 1000*500
         */
        if (brwserApp.contains("Mobile")) {
            mobileDeviceStyle = true;
            PeptidShakerUI.this.addStyleName("mobilestyle");
        } else if ((screenWidth < 1349 && screenWidth >= 1000) && (screenHeigh < 742 && screenHeigh >= 500)) {
            PeptidShakerUI.this.addStyleName("averagescreenstyle");

        } else if (screenWidth < 1000 || screenHeigh <= 500) {
            PeptidShakerUI.this.addStyleName("lowresolutionstyle");
            mobileDeviceStyle = true;
            PeptidShakerUI.this.addStyleName("mobilestyle");
        }
        String localFileSystemFolderPath;
        String tempFolder;
        notificationWindow = new Window();
        try {
            PeptidShakerUI.this.addStyleName("uicontainer");
            PeptidShakerUI.this.setSizeFull();
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

            Path path;
            try {
                path = Files.createTempDirectory("userTempFolder");
                path.toFile().deleteOnExit();
                localFileSystemFolderPath = path.toFile().getAbsolutePath();
                tempFolder = path.toFile().getParentFile().getAbsolutePath();
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            /**
             * Initialise the context parameters and store them in
             * VaadinSession.
             */
            ServletContext scx = VaadinServlet.getCurrent().getServletContext();
            scx.setAttribute("tempFolder", tempFolder);
            VaadinSession.getCurrent().setAttribute("userDataFolderUrl", localFileSystemFolderPath);
            VaadinSession.getCurrent().getSession().setAttribute("userDataFolderUrl", localFileSystemFolderPath);
            VaadinSession.getCurrent().setAttribute("ctxPath", vaadinRequest.getContextPath());
            String testUserAPIKey = (scx.getInitParameter("testUserAPIKey"));
            VaadinSession.getCurrent().setAttribute("testUserAPIKey", testUserAPIKey);
            String galaxyServerUrl = (scx.getInitParameter("galaxyServerUrl"));
            VaadinSession.getCurrent().setAttribute("galaxyServerUrl", galaxyServerUrl);
            String csfprLink = (scx.getInitParameter("csfprservice"));
            String dbURL = (scx.getInitParameter("url"));
            String dbDriver = (scx.getInitParameter("driver"));
            String dbUserName = (scx.getInitParameter("userName"));
            String dbPassword = "d#%[Q=`+<8U,)Pxw";//(scx.getInitParameter("password"));//d#%[Q=`+<8U,)Pxw
            String dbName = (scx.getInitParameter("dbName"));
            String appName = (scx.getInitParameter("appName"));
            VaadinSession.getCurrent().setAttribute("dbName", dbName);
            VaadinSession.getCurrent().setAttribute("appName", appName);
            VaadinSession.getCurrent().setAttribute("dbURL", dbURL);
            VaadinSession.getCurrent().setAttribute("dbDriver", dbDriver);
            VaadinSession.getCurrent().setAttribute("dbUserName", dbUserName);
            VaadinSession.getCurrent().setAttribute("dbPassword", dbPassword);
            VaadinSession.getCurrent().setAttribute("csfprLink", csfprLink);
            String psVersion = (scx.getInitParameter("psvirsion"));
            String searchGUIversion = (scx.getInitParameter("searchguivirsion"));
            String moffVersion = (scx.getInitParameter("moffvirsion"));
            VaadinSession.getCurrent().setAttribute("psVersion", psVersion);
            VaadinSession.getCurrent().setAttribute("searchGUIversion", searchGUIversion);
            VaadinSession.getCurrent().setAttribute("moffvirsion", moffVersion);
            VaadinSession.getCurrent().setAttribute("mobilescreenstyle", (mobileDeviceStyle));
            VaadinSession.getCurrent().setAttribute("smallscreenstyle", smallDeviceStyle);

            webPeptideShakerApp = new WebPeptideShakerApp(galaxyServerUrl);
            PeptidShakerUI.this.setContent(webPeptideShakerApp.getApplicationUserInterface());
            /**
             * On resize the browser re-arrange all the created pop-up windows
             * to the page center.
             */
            Page.getCurrent().addBrowserWindowResizeListener((Page.BrowserWindowResizeEvent event) -> {
                portraitScreenMode = (Page.getCurrent().getBrowserWindowWidth() < Page.getCurrent().getBrowserWindowHeight());
                updateMainStyleMode(mobileDeviceStyle, portraitScreenMode);
                UI.getCurrent().getWindows().forEach((w) -> {
                    w.center();
                });
            });
            updateMainStyleMode(mobileDeviceStyle, portraitScreenMode);
            Page.getCurrent().setTitle("PeptideShaker Online");
        } catch (IllegalArgumentException | NullPointerException e) {
            System.err.println("Error in UI Class : " + e);
            e.printStackTrace();
        }
        try {
            UI.getCurrent().getConnectorTracker().cleanConnectorMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        VaadinSession.getCurrent().setErrorHandler(new ErrorHandler() {
                                                       @Override
                                                       public void error(com.vaadin.server.ErrorEvent event) {
                                                           System.out.println("at ----------- error handler is working ------------------- ");
                                                           event.getThrowable().printStackTrace();
//                Page.getCurrent().reload();
                                                       }
                                                   }
        );
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
     * @param portrait          the screen is in portrait mode
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
    @VaadinServletConfiguration(ui = PeptidShakerUI.class, productionMode = true, resourceCacheTime = 0)
//, resourceCacheTime = 1
    public static class PeptidShakerUIServlet extends VaadinServlet {

        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();

            getService().addSessionInitListener((SessionInitEvent event) -> {
                event.getSession().addBootstrapListener(new BootstrapListener() {

                    @Override
                    public void modifyBootstrapPage(BootstrapPageResponse response) {
                        response.getDocument().head().prependElement("meta").attr("name", "'viewport'").attr("content", "'initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,width=device-width,user-scalable=no'").attr("Set-Cookie", "'cross-site-cookie=name; SameSite=None; Secure'");
                        response.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9,application/javascript");
                    }

                    @Override
                    public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
                    }
                });
            });
        }
    }

}
