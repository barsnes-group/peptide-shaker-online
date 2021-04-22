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
 * Main application configuration this class will be used to read JSON
 * configuration file
 *
 * @author Yehia Mokhtar Farag
 */
public class Config implements Serializable {

    /**
     * get path to functional folder
     *
     * @return url path
     *
     */
    public String getLocalFileSystemFolderPath() {
        return localFileSystemFolderPath;
    }

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
    private String csfprLink;
    private String working_folder_path;

    private int maximumAllowedUsers;
    private String galaxyServerUrl;
    private String userFolderUri;

    /**
     * Main working folder to offload files from galaxy server
     *
     * @return url path
     */
    public String getWorking_folder_path() {
        return working_folder_path;
    }

    /**
     * link to csf-pr web service
     *
     * @return
     */
    public String getCsfprLink() {
        return csfprLink;
    }

    /**
     * Main galaxy functioning history id
     *
     * @return history id
     */
    public String getMainGalaxyHistoryId() {
        return mainGalaxyHistoryId;
    }

    /**
     * set main galaxy functioning history id
     *
     * @param mainGalaxyHistoryId
     */
    public void setMainGalaxyHistoryId(String mainGalaxyHistoryId) {
        this.mainGalaxyHistoryId = mainGalaxyHistoryId;
    }

    /**
     * Get path to default search parameters file (in case of adding new search
     * files)
     *
     * @return url path
     */
    public String getDefaultSearchParamPath() {
        return defaultSearchParamPath;
    }

    /**
     * get the main path to the server
     *
     * @return url path
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * Main general (public) user API key on galaxy server
     *
     * @return
     */
    public String getTestUserAPIKey() {
        return testUserAPIKey;
    }

    /**
     * Get screen styling model
     *
     * @return device with small screen
     */
    public boolean isSmallDeviceStyle() {
        return smallDeviceStyle;
    }

    /**
     * is the device in portrait screen mode
     *
     * @return device orientation
     */
    public boolean isPortraitScreenMode() {
        return portraitScreenMode;
    }

    /**
     * set device orientation
     *
     * @param portraitScreenMode portrait /landscape
     */
    public void setPortraitScreenMode(boolean portraitScreenMode) {
        this.portraitScreenMode = portraitScreenMode;
    }

    /**
     * Cheick if the device is mobile device
     *
     * @return mobile device
     */
    public boolean isMobileDeviceStyle() {
        return mobileDeviceStyle;
    }

    /**
     * get url path to galaxy server
     *
     * @return url path
     */
    public String getGalaxyServerUrl() {
        return galaxyServerUrl;
    }

    /**
     * Get maximum number of login users
     *
     * @return the server capacity
     */
    public int getMaximumAllowedUsers() {
        return maximumAllowedUsers;
    }

    /**
     * get path for user functional folder
     *
     * @return url path
     */
    public String getUserFolderUri() {
        return userFolderUri;
    }

    /**
     * set galaxy user login API key
     *
     * @param userAPIKey
     */
    public void setUserFolderUri(String userAPIKey) {
        this.userFolderUri = localFileSystemFolderPath + "/" + userAPIKey;
        File f = new File(userFolderUri);
        f.mkdir();
    }
    private String localFileSystemFolderPath;

    public Config() {
        initConfig();
    }

    /**
     * @todo: replace context parameters by json configuration file
     */
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
            System.out.println("at error " + Config.class.getName() + "  line 205 " + ex);
            return;
        }

        ServletContext scx = VaadinServlet.getCurrent().getServletContext();
        testUserAPIKey = (scx.getInitParameter("testUserAPIKey"));
        galaxyServerUrl = (scx.getInitParameter("galaxyServerUrl"));
        csfprLink = (scx.getInitParameter("csfprservice"));
        dbURL = (scx.getInitParameter("url"));
        dbDriver = (scx.getInitParameter("driver"));
        dbUserName = (scx.getInitParameter("userName"));
        dbPassword = (scx.getInitParameter("password"));
        dbName = (scx.getInitParameter("dbName"));
        basePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        defaultSearchParamPath = basePath + "/VAADIN/SEARCHGUI_IdentificationParameters.json";
        quant_workflow_file_path = basePath + "/VAADIN/Galaxy-Workflow-Full-Pipeline-Workflow-Quant-Multiple-Input.ga";
        working_folder_path = basePath + "/VAADIN/";
        id_workflow_file_path = basePath + "/VAADIN/Galaxy-Workflow-Full-Pipeline-Workflow-Id-Multiple-Input.ga";
        quant_workflow_invoking_file_path = basePath + "/VAADIN/Multi-Quant-Invoking.json";
        id_workflow_invoking_file_path = basePath + "/VAADIN/Multi-Id-Invoking.json";

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
            UI.getCurrent().addStyleName("averagescreenstyle");
        } else if ((screenWidth < 1349 && screenWidth >= 1000) && (screenHeigh < 742 && screenHeigh >= 500)) {
            UI.getCurrent().addStyleName("averagescreenstyle");

        } else if (screenWidth < 1000 || screenHeigh <= 500) {
            UI.getCurrent().addStyleName("lowresolutionstyle");
            mobileDeviceStyle = true;
            UI.getCurrent().addStyleName("mobilestyle");
        }
    }

    /**
     * link to database
     *
     * @return url to database
     */
    public String getDbURL() {
        return dbURL;
    }

    /**
     * database driver
     *
     * @return string driver
     */
    public String getDbDriver() {
        return dbDriver;
    }

    /**
     * database username
     *
     * @return username (database)
     */
    public String getDbUserName() {
        return dbUserName;
    }

    /**
     * database password
     *
     * @return password
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     * get database name
     *
     * @return name
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * path to quant workflow file
     *
     * @return url path
     */
    public String getQuant_workflow_file_path() {
        return quant_workflow_file_path;
    }

    /**
     **path to id workflow file
     *
     * @return url path
     */
    public String getId_workflow_file_path() {
        return id_workflow_file_path;
    }

    /**
     **path to quant workflow invoking file
     *
     * @return url path
     */
    public String getQuant_workflow_invoking_file_path() {
        return quant_workflow_invoking_file_path;
    }

    /**
     **path to id workflow invoking file
     *
     * @return url path
     */
    public String getId_workflow_invoking_file_path() {
        return id_workflow_invoking_file_path;
    }

}
