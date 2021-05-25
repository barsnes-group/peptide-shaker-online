package com.uib.web.peptideshaker;

import com.uib.web.peptideshaker.facades.GalaxyFacade;
import com.uib.web.peptideshaker.facades.NotificationsFacade;
import com.uib.web.peptideshaker.handler.UserHandler;
import com.uib.web.peptideshaker.handler.WorkFlowHandler;
import com.uib.web.peptideshaker.utils.ModificationMatrixUtilis;
import com.uib.web.peptideshaker.uimanager.SelectionManager;
import com.uib.web.peptideshaker.uimanager.UIManager;
import com.uib.web.peptideshaker.utils.CoreUtils;
import com.uib.web.peptideshaker.utils.DatabaseUtils;
import com.uib.web.peptideshaker.utils.DatasetUtils;
import com.uib.web.peptideshaker.utils.FilesUtils;
import com.uib.web.peptideshaker.utils.HttpClientUtils;
import com.uib.web.peptideshaker.utils.PDBUtils;
import com.uib.web.peptideshaker.utils.PRIDEUtils;
import com.uib.web.peptideshaker.utils.SharedDatasetUtils;
import com.uib.web.peptideshaker.utils.URLUtils;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * This class is used to encapsulate application main handler and facades to
 * avoid using static classes
 *
 * @author Yehia Mokhtar Farag
 */
public class AppManagmentBean implements Serializable {

    private ModificationMatrixUtilis modificationMatrixUtilis;
    private UIManager UI_Manager;
    private SelectionManager selectionManager;
    private PRIDEUtils PRIDEUtils;
    private Config appConfig;
    private GalaxyFacade galaxyFacad;
    private NotificationsFacade notificationFacade;
    private UserHandler userHandler;
    private HttpClientUtils httpClientUtil;
    private DatabaseUtils databaseUtils;
    private URLUtils URLUtils;
    private WorkFlowHandler workFlowHandler;
    private FilesUtils filesUtils;
    private DatasetUtils datasetUtils;
    private CoreUtils coreUtils;
    private ScheduledExecutorService scheduler;
    private Set<ScheduledFuture> scheduledFutureSet;
    private boolean availableGalaxy = false;
    private boolean sharingDatasetMode;
    private SharedDatasetUtils sharedDatasetUtils;

    public SharedDatasetUtils getSharedDatasetUtils() {
        if (sharedDatasetUtils == null) {
            sharedDatasetUtils = new SharedDatasetUtils();
        }
        return sharedDatasetUtils;
    }

    public boolean isSharingDatasetMode() {
        return sharingDatasetMode;
    }

    public void setSharingDatasetMode(boolean sharingDatasetMode) {
        this.sharingDatasetMode = sharingDatasetMode;
    }
    private PDBUtils pdbUtils;

    /**
     * get DivaMatrixLayoutChartFilter utilities
     *
     * @return ModificationMatrixUtilis object
     */
    public ModificationMatrixUtilis getModificationMatrixUtilis() {
        if (modificationMatrixUtilis == null) {
            modificationMatrixUtilis = new ModificationMatrixUtilis();
        }
        return modificationMatrixUtilis;
    }

    /**
     * get presenter selection manager
     *
     * @return SelectionManager object
     */
    public SelectionManager getSelectionManager() {
        if (selectionManager == null) {
            selectionManager = new SelectionManager();
        }
        return selectionManager;
    }

    /**
     * Get visualization dataset utilities
     *
     * @return DatasetUtils object
     */
    public DatasetUtils getDatasetUtils() {
        if (datasetUtils == null) {
            datasetUtils = new DatasetUtils();
        }
        return datasetUtils;
    }

    /**
     * get utilities responsible for handling files on the system
     *
     * @return FilesUtils object
     */
    public FilesUtils getFilesUtils() {
        if (filesUtils == null) {
            filesUtils = new FilesUtils();
        }
        return filesUtils;
    }

    /**
     * get galaxy work-flow handler that is responsible for handling all work
     * flows (search, analyse and uploads data)
     *
     * @return WorkFlowHandler object
     */
    public WorkFlowHandler getWorkFlowHandler() {
        if (workFlowHandler == null) {
            workFlowHandler = new WorkFlowHandler();
        }
        return workFlowHandler;
    }

    /**
     * Get database utilities
     *
     * @return DatabaseUtils object
     */
    public DatabaseUtils getDatabaseUtils() {
        if (databaseUtils == null) {
            databaseUtils = new DatabaseUtils();
        }
        return databaseUtils;
    }

    /**
     * Give access the the notification facade that is responsible for all
     * system notifications and alerts
     *
     * @return NotificationsFacade object
     */
    public NotificationsFacade getNotificationFacade() {
        if (notificationFacade == null) {
            notificationFacade = new NotificationsFacade();
        }
        return notificationFacade;
    }

    /**
     * get utilities responsible for handling "http" calls from the web-server
     * to other web services
     *
     * @return HttpClientUtils object
     */
    public HttpClientUtils getHttpClientUtil() {
        if (httpClientUtil == null) {
            httpClientUtil = new HttpClientUtils();
        }
        return httpClientUtil;
    }

    /**
     * Managing all interaction with galaxy server
     *
     * @return GalaxyFacade object
     */
    public GalaxyFacade getGalaxyFacad() {
        if (galaxyFacad == null) {
            galaxyFacad = new GalaxyFacade();
        }
        return galaxyFacad;
    }

    /**
     * User handler responsible for handling current login user data and
     * information
     *
     * @return UserHandlerobject
     */
    public UserHandler getUserHandler() {
        if (userHandler == null) {
            userHandler = new UserHandler();
        }
        return userHandler;
    }

    /**
     * get the main application configuration
     *
     * @return Config object
     */
    public Config getAppConfig() {
        if (appConfig == null) {
            appConfig = new Config();
        }
        return appConfig;
    }

    /**
     * Give access to the main layout of the application and main view
     * controller manager
     *
     * @return UIManager object
     */
    public UIManager getUI_Manager() {
        if (UI_Manager == null) {
            UI_Manager = new UIManager();
        }
        return UI_Manager;
    }

    /**
     * give access to the url utilities include encoding and decoding
     *
     * @return URLUtils object
     */
    public URLUtils getURLUtils() {
        if (URLUtils == null) {
            URLUtils = new URLUtils();
        }
        return URLUtils;
    }

    /**
     * give access for general and common utilities that can be shared between
     * different classes
     *
     * @return CoreUtils object
     */
    public CoreUtils getCoreUtils() {
        if (coreUtils == null) {
            coreUtils = new CoreUtils();
        }
        return coreUtils;
    }

    /**
     * give access to the main Scheduler service for the system
     *
     * @return ScheduledExecutorService object
     */
    public ScheduledExecutorService getScheduler() {
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        return scheduler;
    }

    /**
     * Allow to add ScheduledFuture objects to store temporarly and allow the
     * system to clean and destroy them after the session expired
     *
     * @param future object of ScheduledFuture type
     */
    public void addScheduledFuture(ScheduledFuture future) {
        if (scheduledFutureSet == null) {
            scheduledFutureSet = new HashSet<>();
        }
        scheduledFutureSet.add(future);
    }

    /**
     * Reset the system upon login or logout or when session is expired.
     */
    public void reset() {
        UI_Manager = null;
        appConfig = null;
        galaxyFacad = null;
        notificationFacade = null;
        userHandler = null;
        httpClientUtil = null;
        databaseUtils = null;
        URLUtils = null;
        workFlowHandler = null;
        filesUtils = null;
        datasetUtils = null;
        coreUtils = null;
        if (scheduledFutureSet != null && !scheduledFutureSet.isEmpty()) {
            scheduledFutureSet.forEach((future) -> {
                future.cancel(true);
            });
        }
        if (scheduler != null) {
            scheduler.shutdownNow();
        }

        scheduler = null;
        scheduledFutureSet = null;

    }

    /**
     * Is galaxy server available online
     *
     * @return boolean object galaxy server is available
     */
    public boolean isAvailableGalaxy() {
        return availableGalaxy;
    }

    /**
     * set galaxy server available online
     *
     * @param availableGalaxy boolean object
     */
    public void setAvailableGalaxy(boolean availableGalaxy) {
        this.availableGalaxy = availableGalaxy;
    }

    /**
     * Get all invoked ScheduledFuture objects
     *
     * @return scheduled Future Set collection
     */
    public Set<ScheduledFuture> getScheduledFutureSet() {
        return scheduledFutureSet;
    }

    /**
     * PDB utilities provide utilities for protein 3d visualization
     *
     * @return PDBUtils object
     */
    public PDBUtils getPdbUtils() {
        if (pdbUtils == null) {
            pdbUtils = new PDBUtils();
        }
        return pdbUtils;
    }

    /**
     * PRIDEUtils utilities provide utilities for PRIDE database
     *
     * @return PRIDEUtils object
     */
    public PRIDEUtils getPRIDEUtils() {
        if (PRIDEUtils == null) {
            PRIDEUtils = new PRIDEUtils();
        }
        return PRIDEUtils;
    }
}
