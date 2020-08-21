package com.uib.web.peptideshaker;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.vaadin.ui.VerticalLayout;
import pl.exsio.plupload.PluploadFile;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class represents the main landing Online PeptideShaker application
 *
 * @author Yehia Farag
 */
public class WebPeptideShakerApp {

    private PresenterLayer Presenter_layer;
    /**
     * The Main data access layer that deal with the system files and data.
     */
    private ModelLayer Model_Layer;
    private Future presenterFuture;
    private Future modelFuture;
    private boolean availableGalaxyServer;
    /**
     * Constructor to initialise the application.
     *
     * @param galaxyUrl web link to galaxy server
     */
    public WebPeptideShakerApp(String galaxyUrl) {
        /**
         * Check galaxy available online or run system in offline galaxy mode.
         */
        availableGalaxyServer = checkConnectionToGalaxy(galaxyUrl);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        modelFuture = executorService.submit(new Runnable() {
            @Override
            public void run() {
                Model_Layer = new ModelLayer(availableGalaxyServer) {
                    @Override
                    public void viewDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
                        try {
                            while (!presenterFuture.isDone()) {
                            }
                            Presenter_layer.viewDataset(peptideShakerVisualizationDataset);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void updatePresenter(Map<String, GalaxyFileObject> tempHistoryFilesMap, Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress) {
                        while (!presenterFuture.isDone()) {
                        }
                        Presenter_layer.updatePresenter(tempHistoryFilesMap, historyFilesMap, jobsInProgress);
                    }
                };
            }
        });

        presenterFuture = executorService.submit(new Runnable() {
            @Override
            public void run() {
                Presenter_layer = new PresenterLayer(availableGalaxyServer) {
                    @Override
                    public List<String> connectToGalaxyServer(String galaxyServerUrl, String userAPI, String userDataFolderUrl) {
                        while (!modelFuture.isDone()) {
                        }

                        boolean connected = Model_Layer.connectToGalaxyServer(galaxyServerUrl, userAPI, userDataFolderUrl);
                        if (!connected) {
                            return null;
                        }
                        return Model_Layer.getUserOverViewList();
                    }

                    @Override
                    public void viewToShareDataset(String galaxyServerUrl, String userDataFolderUrl) {
                        while (!modelFuture.isDone()) {
                        }
                        Model_Layer.viewToShareDataset(galaxyServerUrl, userDataFolderUrl);
                    }

                    @Override
                    public List<String> getUserOverviewData() {

                        while (!modelFuture.isDone()) {
                        }
                        if (Model_Layer != null) {
                            return Model_Layer.getUserOverViewList();
                        }
                        return null;
                    }

                    @Override
                    public void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> inputFilesIdsList, Set<String> searchEnginesList, IdentificationParameters searchParameters, boolean quant) {
                        while (!modelFuture.isDone()) {
                        }
                        Model_Layer.execute_SearchGUI_PeptideShaker_WorkFlow(projectName, fastaFileId, searchParameterFileId, inputFilesIdsList, searchEnginesList, searchParameters, quant);

                    }

                    @Override
                    public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean isNew) {
                        while (!modelFuture.isDone()) {
                        }
                        return Model_Layer.saveSearchGUIParameters(searchParameters, isNew);
                    }

                    @Override
                    public boolean uploadToGalaxy(PluploadFile[] toUploadFiles) {
                        while (!modelFuture.isDone()) {
                        }

                        /**
                         * upload file to galaxy server
                         */
                        boolean success = Model_Layer.uploadDataFiles(toUploadFiles);
                        return success;
                    }

                    @Override
                    public void deleteDataset(GalaxyFileObject fileObject) {
                        while (!modelFuture.isDone()) {
                        }
                        Model_Layer.deleteDataset(fileObject);
                    }

                    @Override
                    public Set<String> getCsf_pr_Accession_List() {
                        while (!modelFuture.isDone()) {
                        }
                        return Model_Layer.getCsf_pr_Accession_List();
                    }

                    @Override
                    public int insertDatsetLinkToShare(String dsDetails, String dsUniqueKey) {
                        while (!modelFuture.isDone()) {
                        }
                        return Model_Layer.insertDatasetSharingLink(dsDetails, dsUniqueKey);
                    }

                };

            }


        });
    }

    /**
     * Get the main User Interface layer
     *
     * @return main user interface container
     */
    public VerticalLayout getApplicationUserInterface() {
        while (!presenterFuture.isDone()) {
        }
        return Presenter_layer.getPresenterContainer();
    }

    public void loginAsGuest() {
        while (!presenterFuture.isDone()) {
        }
        if (availableGalaxyServer) {
            Presenter_layer.loginAsGuest();
        }

    }

    public void retriveToShareDataset() {
        while (!presenterFuture.isDone()) {
        }
        if (availableGalaxyServer) {
            Presenter_layer.retriveToShareDataset();
        }

    }

    /**
     * Check Galaxy server is available.
     *
     * @param urlAddress Galaxy server web URL
     * @return is galaxy server available online
     */
    private boolean checkConnectionToGalaxy(String urlAddress) {
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection connection;
            if (urlAddress.contains("https")) {
                connection = (HttpsURLConnection) url.openConnection();
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }
            connection.setRequestMethod("GET");
            connection.connect();
            int code = connection.getResponseCode();
            if (code == 404) {
                return false;
            }

        } catch (IOException e) {
            System.err.println("Error at chck galaxy connection " + e);
            return false;
        }
        return true;
    }

}
