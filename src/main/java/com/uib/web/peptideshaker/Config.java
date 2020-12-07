package com.uib.web.peptideshaker;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.ServletContext;

/**
 * Main application configuration
 *
 * @author Yehia Mokhtar Farag
 */
public class Config implements Serializable{

    private boolean mobileDeviceStyle;
    private boolean smallDeviceStyle;
    private boolean portraitScreenMode;
    private String testUserAPIKey;

    public String getTestUserAPIKey() {
        return testUserAPIKey;
    }

    public boolean isSmallDeviceStyle() {
        return smallDeviceStyle;
    }

    public boolean isPortraitScreenMode() {
        return portraitScreenMode;
    }

    public void setPortraitScreenMode(boolean portraitScreenMode) {
        this.portraitScreenMode = portraitScreenMode;
    }
    private int maximumAllowedUsers;

    public boolean isMobileDeviceStyle() {
        return mobileDeviceStyle;
    }
    private String galaxyServerUrl;

    public String getGalaxyServerUrl() {
        return galaxyServerUrl;
    }

    public int getMaximumAllowedUsers() {
        return maximumAllowedUsers;
    }

    public Config() {
        initConfig();
    }

    private void initConfig() {
        /**
         * Initialise the context parameters and store them in VaadinSession.
         */

        Path path;
        String localFileSystemFolderPath;
        try {
            path = Files.createTempDirectory("userTempFolder");
            path.toFile().deleteOnExit();
            localFileSystemFolderPath = path.toFile().getAbsolutePath();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        ServletContext scx = VaadinServlet.getCurrent().getServletContext();
        VaadinSession.getCurrent().setAttribute("userDataFolderUrl", localFileSystemFolderPath);
        VaadinSession.getCurrent().getSession().setAttribute("userDataFolderUrl", localFileSystemFolderPath);

        VaadinSession.getCurrent().setAttribute("ctxPath", scx.getContextPath());
        testUserAPIKey =(scx.getInitParameter("testUserAPIKey"));
        VaadinSession.getCurrent().setAttribute("testUserAPIKey", testUserAPIKey);
        galaxyServerUrl = (scx.getInitParameter("galaxyServerUrl"));
        VaadinSession.getCurrent().setAttribute("galaxyServerUrl", galaxyServerUrl);
        String csfprLink = (scx.getInitParameter("csfprservice"));
        String dbURL = (scx.getInitParameter("url"));
        String dbDriver = (scx.getInitParameter("driver"));
        String dbUserName = (scx.getInitParameter("userName"));
        String dbPassword = (scx.getInitParameter("password"));
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
        maximumAllowedUsers = Integer.parseUnsignedInt(scx.getInitParameter("maxusernumb") + "");

        VaadinSession.getCurrent().setAttribute("mobilescreenstyle", (mobileDeviceStyle));
        VaadinSession.getCurrent().setAttribute("smallscreenstyle", smallDeviceStyle);

        String brwserApp = Page.getCurrent().getWebBrowser().getBrowserApplication();
        int screenWidth = Page.getCurrent().getBrowserWindowWidth();
        int screenHeigh = Page.getCurrent().getBrowserWindowHeight();
        portraitScreenMode = screenWidth < screenHeigh;
        /**
         * case of average screen 1000*500
         */
        if (brwserApp.contains("Mobile")) {
            mobileDeviceStyle = true;
            UI.getCurrent().addStyleName("mobilestyle");
        } else if ((screenWidth < 1349 && screenWidth >= 1000) && (screenHeigh < 742 && screenHeigh >= 500)) {
            UI.getCurrent().addStyleName("averagescreenstyle");

        } else if (screenWidth < 1000 || screenHeigh <= 500) {
            UI.getCurrent().addStyleName("lowresolutionstyle");
            mobileDeviceStyle = true;
            UI.getCurrent().addStyleName("mobilestyle");
        }
    }

}
