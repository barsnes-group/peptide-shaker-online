package com.uib.web.peptideshaker;

import com.uib.web.peptideshaker.handler.UIHandler;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.ui.UIContainer;
import com.vaadin.server.VaadinSession;
import pl.exsio.plupload.PluploadFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class represents the main landing Online PeptideShaker application
 *
 * @author Yehia Mokhtar Farag
 */
public class AppController {

    private UIHandler uiHandler;
    /**
     * The Main data access layer that deal with the system files and data.
     */
    private ModelLayer Model_Layer;
    private Future uiExecutorFuture;
    private boolean availableGalaxyServer;
    private final AppManagmentBean appManagmentBean;

    /**
     * Constructor to initialise the application.
     */
    public AppController() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        /**
         * Check galaxy available online using public user API key or run system
         * in offline galaxy mode.
         */
        appManagmentBean.getNotificationFacade().showGalaxyConnectingProcess(CONSTANT.PUBLIC_USER_CAPTION);
        String userId = appManagmentBean.getGalaxyFacad().authenticate(appManagmentBean.getAppConfig().getTestUserAPIKey());
        availableGalaxyServer = (userId != null);
        if (availableGalaxyServer) {
            appManagmentBean.getUserHandler().setUserLoggedIn(appManagmentBean.getAppConfig().getTestUserAPIKey(), userId);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(2);
//        modelFuture = executorService.submit(() -> {
//            Model_Layer = new ModelLayer(authUserLogin) {
//                @Override
//                public void viewDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
//                    try {
//                        while (!presenterFuture.isDone()) {
//                        }
//                        Presenter_layer.viewDataset(peptideShakerVisualizationDataset);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                
//                @Override
//                public void updatePresenter(Map<String, GalaxyFileObject> tempHistoryFilesMap, Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress) {
//                    while (!presenterFuture.isDone()) {
//                    }
//                    Presenter_layer.updatePresenter(tempHistoryFilesMap, historyFilesMap, jobsInProgress);
//                }
//            };
//        });
        uiExecutorFuture = executorService.submit(() -> {
            uiHandler = new UIHandler(availableGalaxyServer) {
                @Override
                public List<String> connectToGalaxyServer(String galaxyServerUrl, String userAPI, String userDataFolderUrl) {
//                    while (!modelFuture.isDone()) {
//                    }

//                    boolean connected = Model_Layer.connectToGalaxyServer(galaxyServerUrl, userAPI, userDataFolderUrl);
//                    if (!connected) {
//                        return null;
//                    }
//                    return Model_Layer.getUserOverViewList();
                    return new ArrayList<>();
                }

                @Override
                public void viewToShareDataset(String galaxyServerUrl, String userDataFolderUrl) {
//                    while (!modelFuture.isDone()) {
//                    }
//                    Model_Layer.viewToShareDataset(galaxyServerUrl, userDataFolderUrl);
                }

                @Override
                public List<String> getUserOverviewData() {

//                    while (!modelFuture.isDone()) {
//                    }
//                    if (Model_Layer != null) {
//                        return Model_Layer.getUserOverViewList();
//                    }
                    return null;
                }

                @Override
                public void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> inputFilesIdsList, Set<String> searchEnginesList, IdentificationParameters searchParameters, boolean quant) {
//                    while (!modelFuture.isDone()) {
//                    }
//                    Model_Layer.execute_SearchGUI_PeptideShaker_WorkFlow(projectName, fastaFileId, searchParameterFileId, inputFilesIdsList, searchEnginesList, searchParameters, quant);

                }

                @Override
                public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean isNew) {
//                    while (!modelFuture.isDone()) {
//                    }
//                    return Model_Layer.saveSearchGUIParameters(searchParameters, isNew);
                    return null;
                }

                @Override
                public boolean uploadToGalaxy(PluploadFile[] toUploadFiles) {
//                    while (!modelFuture.isDone()) {
//                    }

                    /**
                     * upload file to galaxy server
                     */
                    boolean success = false;// Model_Layer.uploadDataFiles(toUploadFiles);
                    return success;
                }

                @Override
                public void deleteDataset(GalaxyFileObject fileObject) {
//                    while (!modelFuture.isDone()) {
//                    }
//                    Model_Layer.deleteDataset(fileObject);
                }

                @Override
                public Set<String> getCsf_pr_Accession_List() {
//                    while (!modelFuture.isDone()) {
//                    }
//                    return Model_Layer.getCsf_pr_Accession_List();
                    return null;
                }

                @Override
                public int insertDatsetLinkToShare(String dsDetails, String dsUniqueKey) {
//                    while (!modelFuture.isDone()) {
//                    }
//                    return Model_Layer.insertDatasetSharingLink(dsDetails, dsUniqueKey);
                    return 1;
                }

            };
        });
        this.updateALLUI();
        appManagmentBean.getNotificationFacade().hideGalaxyConnectingProcess();
    }

    private void updateALLUI() {
        while (!uiExecutorFuture.isDone()) {
        }
        appManagmentBean.getUI_Manager().updateAll();
        
    }

    /**
     * Get the main User Interface layer
     *
     * @return main user interface container
     */
    public UIContainer getApplicationUserInterface() {
        while (!uiExecutorFuture.isDone()) {
        }
        return uiHandler.getUiContainer();
    }

   

    public void retriveToShareDataset() {
        while (!uiExecutorFuture.isDone()) {
        }
        if (availableGalaxyServer) {
            uiHandler.retriveToShareDataset();
        }

    }

}
