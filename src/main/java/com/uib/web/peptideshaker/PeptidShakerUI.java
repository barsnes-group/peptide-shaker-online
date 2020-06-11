package com.uib.web.peptideshaker;

import com.vaadin.annotations.JavaScript;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.Extension;
import com.vaadin.server.Page;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

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
@JavaScript({"../../VAADIN/js/venn.js", "../../VAADIN/js/myD3library.js", "../../VAADIN/js/myD3component-connector.js", "../../VAADIN/js/d3.v5.min.js", "../../VAADIN/js/LiteMol-plugin.js", "../../VAADIN/js/mylitemol-connector.js", "../../VAADIN/js/mylitemollibrary.js", "https://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js", "https://cdnjs.cloudflare.com/ajax/libs/jquery.touch/1.1.0/jquery.touch.min.js", "../../VAADIN/js/mylibrary.js", "../../VAADIN/js/mycomponent-connector.js", "https://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js", "https://cdnjs.cloudflare.com/ajax/libs/jquery.touch/1.1.0/jquery.touch.min.js", "../../VAADIN/js/mylibrary2.js", "../../VAADIN/js/mycomponent-connector2.js", "../../VAADIN/js/jquery.mousewheel.js"})

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
     
          
        String brwserApp = Page.getCurrent().getWebBrowser().getBrowserApplication();
        int screenWidth = Page.getCurrent().getBrowserWindowWidth();
        int screenHeigh = Page.getCurrent().getBrowserWindowHeight();
//        int screenWidth2 = Page.getCurrent().getWebBrowser().getScreenWidth();
//        int screenHeigh2 = Page.getCurrent().getWebBrowser().getScreenHeight();
//        System.out.println("*************************************************************************");
//        System.out.println("at app " + brwserApp);
//        System.out.println("at  w: " + screenWidth + " |  h: " + screenHeigh);
//        System.out.println("at w2: " + screenWidth2 + " | h2: " + screenHeigh2);
//        System.out.println("*************************************************************************");
        portraitScreenMode = screenWidth < screenHeigh;
        /**
         * case of average screen 1000*500
         */
        if (brwserApp.contains("Mobile")) {
            mobileDeviceStyle = true;
            PeptidShakerUI.this.addStyleName("mobilestyle");
        } else if ((screenWidth < 1349 && screenWidth >= 1000) && (screenHeigh < 742 && screenHeigh >= 500)) {
            PeptidShakerUI.this.addStyleName("averagescreenstyle");

        } //        else if ((screenWidth < 1000) && (screenHeigh < 742 )) {
        //            smallDeviceStyle = true;
        //            PeptidShakerUI.this.addStyleName("averagescreenstyle");
        //            PeptidShakerUI.this.addStyleName("smallscreenstyle");
        //        } 
        //       else if ((screenHeigh < 1349 && screenHeigh > 800) && (screenWidth <= 800 && screenWidth > 600)) {
        //            smallDeviceStyle = true;
        //            PeptidShakerUI.this.addStyleName("smallscreenstyle");
        //        }else if (screenHeigh <= 800 || screenWidth <= 600) {
        //            PeptidShakerUI.this.addStyleName("lowresolutionstyle");
        //            mobileDeviceStyle = true;
        //            PeptidShakerUI.this.addStyleName("mobilestyle");
        //        } 
        else if (screenWidth < 1000 || screenHeigh <= 500) {
//            PeptidShakerUI.this.addStyleName("averagescreenstyle");
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
            UI.getCurrent().addWindow(notificationWindow);
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
            String dbPassword = (scx.getInitParameter("password"));//"d#%[Q=`+<8U,)Pxw";//
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
            Page.getCurrent().setTitle("PeptideShaker");
        } catch (Exception e) {
            e.printStackTrace();

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

    @Override
    public void addExtension(Extension extension) {
        super.addExtension(extension);

    }

    /**
     * Main application Servlet.
     */
    @WebServlet(urlPatterns = "/*", name = "PeptidShakerUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = PeptidShakerUI.class, productionMode = true, resourceCacheTime = 0)//, resourceCacheTime = 1
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
//                        System.out.println("at headerosss "+response.getRequest().geta.keySet());
                    }

                    @Override
                    public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
                    }
                });
            });
        }
    }

}
