package com.uib.web.peptideshaker;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.vaadin.ui.VerticalLayout;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;
import pl.exsio.plupload.PluploadFile;

/**
 * This class represents the main landing Online PeptideShaker application
 *
 * @author Yehia Farag
 */
public class WebPeptideShakerApp {

    /**
     * Get the main User Interface layer
     *
     * @return main user interface container
     */
    public VerticalLayout getApplicationUserInterface() {
        return Presenter_layer.getPresenterContainer();
    }

    private final PresenterLayer Presenter_layer;
    /**
     * The Main data access layer that deal with the system files and data.
     */
    private final ModelLayer Model_Layer;

    /**
     * Constructor to initialise the application.
     *
     * @param galaxyUrl web link to galaxy server
     */
    public WebPeptideShakerApp(String galaxyUrl) {
        /**
         * Check galaxy available online or run system in offline galaxy mode.
         */
        boolean availableGalaxyServer = checkConnectionToGalaxy(galaxyUrl);

        this.Model_Layer = new ModelLayer(availableGalaxyServer) {
            @Override
            public void viewDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
                Presenter_layer.viewDataset(peptideShakerVisualizationDataset);
            }

            @Override
            public void updateSystemData(Map<String, GalaxyFileObject> tempHistoryFilesMap, Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress) {
                Presenter_layer.updateSystemData(tempHistoryFilesMap, historyFilesMap, jobsInProgress);
            }
        };

        this.Presenter_layer = new PresenterLayer(availableGalaxyServer) {
            @Override
            public List<String> connectToGalaxyServer(String galaxyServerUrl, String userAPI, String userDataFolderUrl) {
                Model_Layer.connectToGalaxyServer(galaxyServerUrl, userAPI, userDataFolderUrl);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                }
                return Model_Layer.getUserOverViewList();
            }

            @Override
            public List<String> getUserOverviewData() {
                if (Model_Layer != null) {
                    return Model_Layer.getUserOverViewList();
                }
                return null;
            }

            @Override
            public void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> inputFilesIdsList, Set<String> searchEnginesList, IdentificationParameters searchParameters, boolean quant) {
                Model_Layer.execute_SearchGUI_PeptideShaker_WorkFlow(projectName, fastaFileId, searchParameterFileId, inputFilesIdsList, searchEnginesList, searchParameters, quant);

            }

            @Override
            public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean isNew) {
                return Model_Layer.saveSearchGUIParameters(searchParameters, isNew);
            }

            @Override
            public boolean uploadToGalaxy(PluploadFile[] toUploadFiles) {
                return Model_Layer.uploadDataFiles(toUploadFiles);
            }

            @Override
            public void deleteDataset(GalaxyFileObject fileObject) {
                Model_Layer.deleteDataset(fileObject);
            }

            @Override
            public Set<String> getCsf_pr_Accession_List() {
                return Model_Layer.getCsf_pr_Accession_List();
            }

            @Override
            public int insertDatsetLinkToShare(String dsDetails) {
                return Model_Layer.insertDatasetSharingLink(dsDetails);
            }
            

        };
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
            if (urlAddress.contains("https")) {
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int code = connection.getResponseCode();
                if (code == 404) {
                    return false;
                }
            } else {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int code = connection.getResponseCode();
                if (code == 404) {
                    return false;
                }
            }

        } catch (MalformedURLException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
