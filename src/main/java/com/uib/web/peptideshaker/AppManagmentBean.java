package com.uib.web.peptideshaker;

import com.uib.web.peptideshaker.facades.GalaxyFacade;
import com.uib.web.peptideshaker.facades.NotificationsFacade;
import com.uib.web.peptideshaker.handler.UserHandler;
import com.uib.web.peptideshaker.handler.WorkFlowHandler;
import com.uib.web.peptideshaker.model.core.ModificationMatrixUtilis;
import com.uib.web.peptideshaker.uimanager.ResultsViewSelectionManager;
import com.uib.web.peptideshaker.uimanager.UIManager;
import com.uib.web.peptideshaker.utils.CoreUtils;
import com.uib.web.peptideshaker.utils.DatabaseUtils;
import com.uib.web.peptideshaker.utils.DatasetUtils;
import com.uib.web.peptideshaker.utils.FilesUtils;
import com.uib.web.peptideshaker.utils.HttpClientUtils;
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

    public ModificationMatrixUtilis getModificationMatrixUtilis() {
        if (modificationMatrixUtilis == null) {
            modificationMatrixUtilis = new ModificationMatrixUtilis();
        }
        return modificationMatrixUtilis;
    }

    private UIManager UI_Manager;
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
    private ResultsViewSelectionManager resultsViewSelectionManager;

    public ResultsViewSelectionManager getResultsViewSelectionManager() {
        if (resultsViewSelectionManager == null) {
            resultsViewSelectionManager = new ResultsViewSelectionManager();
        }
        return resultsViewSelectionManager;
    }

    public DatasetUtils getDatasetUtils() {
        if (datasetUtils == null) {
            datasetUtils = new DatasetUtils();
        }
        return datasetUtils;
    }

    public FilesUtils getFilesUtils() {
        if (filesUtils == null) {
            filesUtils = new FilesUtils();
        }
        return filesUtils;
    }

    public WorkFlowHandler getWorkFlowHandler() {
        if (workFlowHandler == null) {
            workFlowHandler = new WorkFlowHandler();
        }
        return workFlowHandler;
    }

    public DatabaseUtils getDatabaseUtils() {
        if (databaseUtils == null) {
            databaseUtils = new DatabaseUtils();
        }
        return databaseUtils;
    }

    public NotificationsFacade getNotificationFacade() {
        if (notificationFacade == null) {
            notificationFacade = new NotificationsFacade();
        }
        return notificationFacade;
    }

    public HttpClientUtils getHttpClientUtil() {
        if (httpClientUtil == null) {
            httpClientUtil = new HttpClientUtils();
        }
        return httpClientUtil;
    }

    public GalaxyFacade getGalaxyFacad() {
        if (galaxyFacad == null) {
            galaxyFacad = new GalaxyFacade();
        }
        return galaxyFacad;
    }

    public UserHandler getUserHandler() {
        if (userHandler == null) {
            userHandler = new UserHandler();
        }
        return userHandler;
    }

    public AppManagmentBean() {

    }

    public Config getAppConfig() {
        if (appConfig == null) {
            appConfig = new Config();
        }
        return appConfig;
    }

    public UIManager getUI_Manager() {
        if (UI_Manager == null) {
            UI_Manager = new UIManager();
        }
        return UI_Manager;
    }

    public URLUtils getURLUtils() {
        if (URLUtils == null) {
            URLUtils = new URLUtils();
        }
        return URLUtils;
    }

    public CoreUtils getCoreUtils() {
        if (coreUtils == null) {
            coreUtils = new CoreUtils();
        }
        return coreUtils;
    }

    public ScheduledExecutorService getScheduler() {
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        return scheduler;
    }

    public void addScheduledFuture(ScheduledFuture future) {
        if (scheduledFutureSet == null) {
            scheduledFutureSet = new HashSet<>();
        }
        scheduledFutureSet.add(future);
    }

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

}
