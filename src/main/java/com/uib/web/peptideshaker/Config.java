package com.uib.web.peptideshaker;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.UI;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.IOUtils;

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
    private JsonObject configJson;
    private String analyze_Data_text;
    private String projects_Overview_text;
    private String upload_Visualize_Projects_text;
    private String datset_Visualization_text;
    private String protein_Visualization_text;
    private String peptide_Visualization_text;
    private boolean enableUpload;
    private boolean limitedsearchengine;

    public boolean isEnableUpload() {
        return enableUpload;
    }

    public boolean isEnableDelete() {
        return enableDelete;
    }
    private boolean enableDelete;

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

          VaadinSession.getCurrent().setAttribute("mobilescreenstyle", (mobileDeviceStyle));

        String brwserApp = Page.getCurrent().getWebBrowser().getBrowserApplication();
        int screenWidth = Page.getCurrent().getBrowserWindowWidth();
        int screenHeigh = Page.getCurrent().getBrowserWindowHeight();
        portraitScreenMode = screenWidth < screenHeigh;
        Thread t = new Thread(() -> {
            JavaScript.getCurrent().addFunction("com.example.foo.myfunc", (elemental.json.JsonArray arguments) -> {
                double ratio = arguments.getNumber(0);

                if (brwserApp.contains("Mobile")) {
                    mobileDeviceStyle = true;
                    UI.getCurrent().addStyleName("mobilestyle");
                    UI.getCurrent().addStyleName("averagescreenstyle");
                } else if (((screenWidth < 1349 && screenWidth >= 1000) && (screenHeigh < 742 && screenHeigh >= 500)) || ratio > 1.5) {
                    UI.getCurrent().addStyleName("averagescreenstyle");

                } else if (screenWidth < 1000 || screenHeigh <= 500) {
                    UI.getCurrent().addStyleName("lowresolutionstyle");
                    mobileDeviceStyle = true;
                    UI.getCurrent().addStyleName("mobilestyle");
                }
            });
            Page.getCurrent().getJavaScript().execute("com.example.foo.myfunc(window.devicePixelRatio)");

        });
        t.start();
        
        Path path;
        try {
            path = Files.createTempDirectory("userTempFolder");
            path.toFile().deleteOnExit();
            localFileSystemFolderPath = path.toFile().getAbsolutePath();
        } catch (IOException ex) {
            System.out.println("at error " + Config.class.getName() + "  line 205 " + ex);
            return;
        }
        basePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        String configJsonFilePath = basePath + "/VAADIN/config.json";
        try {
            configJson = new JsonObject(IOUtils.toString(new FileReader(configJsonFilePath)));
            analyze_Data_text = configJson.getJsonObject("infotext").getString("Analyze_Data");
            projects_Overview_text = configJson.getJsonObject("infotext").getString("Projects_Overview");
            upload_Visualize_Projects_text = configJson.getJsonObject("infotext").getString("Upload_Visualize_Projects");
            datset_Visualization_text = configJson.getJsonObject("infotext").getString("Datset_Visualization");
            protein_Visualization_text = configJson.getJsonObject("infotext").getString("Protein_Visualization");
            peptide_Visualization_text = configJson.getJsonObject("infotext").getString("Peptide_Visualization");
            enableUpload = configJson.getBoolean("enableupload");
            enableDelete = configJson.getBoolean("enabledelete");
            limitedsearchengine = configJson.getBoolean("limitedsearchengine");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("at error at " + Config.class.getName() + " line 223" + ex);
        }
//        ServletContext scx = VaadinServlet.getCurrent().getServletContext();
        testUserAPIKey = configJson.getString("testUserAPIKey");//scx.getInitParameter("testUserAPIKey"));
        galaxyServerUrl = configJson.getString("galaxyServerUrl");//(scx.getInitParameter("galaxyServerUrl"));
        csfprLink = configJson.getString("csfprservice");//(scx.getInitParameter("csfprservice"));
        dbURL = configJson.getString("url");//(scx.getInitParameter("url"));
        dbDriver = configJson.getString("driver");//(scx.getInitParameter("driver"));
        dbUserName = configJson.getString("userName");//(scx.getInitParameter("userName"));
        dbPassword = configJson.getString("password");//(scx.getInitParameter("password"));
        dbName = configJson.getString("dbName");//(scx.getInitParameter("dbName"));
        maximumAllowedUsers = configJson.getInteger("maxusernumb");// Integer.parseUnsignedInt(scx.getInitParameter("maxusernumb") + "");
        defaultSearchParamPath = basePath + "/VAADIN/SEARCHGUI_IdentificationParameters.json";
        quant_workflow_file_path = basePath + "/VAADIN/Galaxy-Workflow-Full-Pipeline-Workflow-Quant-Multiple-Input.ga";
        working_folder_path = basePath + "/VAADIN/";
        id_workflow_file_path = basePath + "/VAADIN/Galaxy-Workflow-Full-Pipeline-Workflow-Id-Multiple-Input.ga";
        quant_workflow_invoking_file_path = basePath + "/VAADIN/Multi-Quant-Invoking.json";
        id_workflow_invoking_file_path = basePath + "/VAADIN/Multi-Id-Invoking.json";

      

//        /**
//         * case of average screen 1000*500
//         */
//        if (brwserApp.contains("Mobile")) {
//            mobileDeviceStyle = true;
//            UI.getCurrent().addStyleName("mobilestyle");
//            UI.getCurrent().addStyleName("averagescreenstyle");
//        } else if ((screenWidth < 1349 && screenWidth >= 1000) && (screenHeigh < 742 && screenHeigh >= 500)) {
//            UI.getCurrent().addStyleName("averagescreenstyle");
//
//        } else if (screenWidth < 1000 || screenHeigh <= 500) {
//            UI.getCurrent().addStyleName("lowresolutionstyle");
//            mobileDeviceStyle = true;
//            UI.getCurrent().addStyleName("mobilestyle");
//        }
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

    public JsonObject getConfigJson() {
        return configJson;
    }

    public String getAnalyze_Data_text() {
        return analyze_Data_text;
    }

    public String getProjects_Overview_text() {
        return projects_Overview_text;
    }

    public String getUpload_Visualize_Projects_text() {
        return upload_Visualize_Projects_text;
    }

    public String getDatset_Visualization_text() {
        return datset_Visualization_text;
    }

    public String getProtein_Visualization_text() {
        return protein_Visualization_text;
    }

    public String getPeptide_Visualization_text() {
        return peptide_Visualization_text;
    }

    public boolean isLimitedsearchengine() {
        return limitedsearchengine;
    }

}
