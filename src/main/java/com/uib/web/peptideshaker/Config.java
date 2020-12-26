package com.uib.web.peptideshaker;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import java.io.File;
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
public class Config implements Serializable {

    private boolean mobileDeviceStyle;
    private boolean smallDeviceStyle;
    private boolean portraitScreenMode;
    private String testUserAPIKey;

    private String dbURL;
    private String dbDriver;
    private String dbUserName;
    private String dbPassword;
    private String dbName;
    private String basePath;
    private String defaultSearchParamPath;
    private String mainGalaxyHistoryId;
    private String quant_workflow_file_path;
    private String id_workflow_file_path;
    
    private String quant_workflow_invoking_file_path;
    private String id_workflow_invoking_file_path;

    public String getMainGalaxyHistoryId() {
        return mainGalaxyHistoryId;
    }

    public void setMainGalaxyHistoryId(String mainGalaxyHistoryId) {
        this.mainGalaxyHistoryId = mainGalaxyHistoryId;
    }

    public String getDefaultSearchParamPath() {
        return defaultSearchParamPath;
    }

    public String getBasePath() {
        return basePath;
    }

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
    private String userFolderUri;

    public String getUserFolderUri() {
        return userFolderUri;
    }

    public void setUserFolderUri(String userAPIKey) {
        this.userFolderUri = localFileSystemFolderPath + "/" + userAPIKey;
        File f = new File(userFolderUri);
        f.mkdir();
    }
    private String localFileSystemFolderPath;

    public Config() {
        initConfig();
    }

    private void initConfig() {
        /**
         * Initialise the context parameters and store them in VaadinSession.
         */

        Path path;
        try {
            path = Files.createTempDirectory("userTempFolder");
            path.toFile().deleteOnExit();
            localFileSystemFolderPath = path.toFile().getAbsolutePath();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        ServletContext scx = VaadinServlet.getCurrent().getServletContext();
        testUserAPIKey = (scx.getInitParameter("testUserAPIKey"));
        galaxyServerUrl = (scx.getInitParameter("galaxyServerUrl"));
        String csfprLink = (scx.getInitParameter("csfprservice"));
        dbURL = (scx.getInitParameter("url"));
        dbDriver = (scx.getInitParameter("driver"));
        dbUserName = (scx.getInitParameter("userName"));
        dbPassword = (scx.getInitParameter("password"));
        dbName = (scx.getInitParameter("dbName"));
        basePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        defaultSearchParamPath = basePath + "/VAADIN/SEARCHGUI_IdentificationParameters.json";
        quant_workflow_file_path=basePath +"/VAADIN/Galaxy-Workflow-Full-Pipeline-Workflow-Quant-Multiple-Input.ga";
        id_workflow_file_path=basePath +"/VAADIN/Galaxy-Workflow-Full-Pipeline-Workflow-Id-Multiple-Input.ga";
        quant_workflow_invoking_file_path= basePath +"/VAADIN/Multi-Quant-Invoking.json";
        id_workflow_invoking_file_path= basePath +"/VAADIN/Multi-Id-Invoking.json";
        String appName = (scx.getInitParameter("appName"));
        VaadinSession.getCurrent().setAttribute("appName", appName);
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

    public String getDbURL() {
        return dbURL;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbName() {
        return dbName;
    }

    public String getQuant_workflow_file_path() {
        return quant_workflow_file_path;
    }

    public String getId_workflow_file_path() {
        return id_workflow_file_path;
    }

    public String getQuant_workflow_invoking_file_path() {
        return quant_workflow_invoking_file_path;
    }

    public String getId_workflow_invoking_file_path() {
        return id_workflow_invoking_file_path;
    }

}
