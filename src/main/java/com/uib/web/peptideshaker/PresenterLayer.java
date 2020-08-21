package com.uib.web.peptideshaker;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.PresenterContainer;
import com.uib.web.peptideshaker.presenter.PresenterManager;
import com.uib.web.peptideshaker.presenter.ViewableFrame;
import com.vaadin.ui.UI;
import pl.exsio.plupload.PluploadFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents presenter layer that responsible for front-end
 * visualisation and small computing processes that is done by the front end
 * components
 *
 * @author Yehia Farag
 */
public abstract class PresenterLayer {

    /**
     * PeptideShaker visualisation layer - Coordinator to organise the different
     * views (home, analysis, data, or results visualisation).
     */
    private final PresenterManager presentationManager;
    private final PresenterContainer presenterContainer;

    /**
     * Initialise the main presenter components.
     *
     * @param availableGalaxyServer galaxy server available online
     */
    public PresenterLayer(boolean availableGalaxyServer) {
        this.presenterContainer = new PresenterContainer(availableGalaxyServer) {
            private boolean connectedToGalaxy;

            @Override
            public void viewLayout(String viewId) {
                presentationManager.viewLayout(viewId);
            }

            @Override
            public void registerView(ViewableFrame view) {
                UI.getCurrent().access(() -> {
                    presentationManager.registerView(view);
                });

            }

            @Override
            public List<String> connectToGalaxyServer(String galaxyServerUrl, String userAPI, String userDataFolderUrl) {
                List<String> userData = PresenterLayer.this.connectToGalaxyServer(galaxyServerUrl, userAPI, userDataFolderUrl);
                connectedToGalaxy = userData != null;
                if (getSearchGUIPeptideShakerToolPresenter().getMainPresenterButton().isEnabled()) {
                    getSearchGUIPeptideShakerToolPresenter().getMainPresenterButton().setEnabled(connectedToGalaxy);
                    getSearchGUIPeptideShakerToolPresenter().getSmallPresenterControlButton().setEnabled(connectedToGalaxy);
                }

                return userData;
            }

            @Override
            public void viewToShareDataset(String galaxyServerUrl, String userDataFolderUrl) {
                PresenterLayer.this.viewToShareDataset(galaxyServerUrl, userDataFolderUrl);
            }

            @Override
            public List<String> getUserOverviewData() {
                return PresenterLayer.this.getUserOverviewData();
            }

            @Override
            public void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, IdentificationParameters searchParam, boolean quant) {
                PresenterLayer.this.execute_SearchGUI_PeptideShaker_WorkFlow(projectName, fastaFileId, searchParameterFileId, mgfIdsList, searchEnginesList, searchParam, quant);
            }

            @Override
            public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean isNew) {
                return PresenterLayer.this.saveSearchGUIParameters(searchParameters, isNew);

            }

            @Override
            public boolean uploadToGalaxy(PluploadFile[] toUploadFiles) {
                return PresenterLayer.this.uploadToGalaxy(toUploadFiles);

            }

            @Override
            public void deleteDataset(GalaxyFileObject fileObject) {
                PresenterLayer.this.deleteDataset(fileObject);
            }

            @Override
            public Set<String> getCsf_pr_Accession_List() {
                return PresenterLayer.this.getCsf_pr_Accession_List();
            }

            @Override
            public int insertDatsetLinkToShare(String dsDetails, String dsUniqueKey) {
                return PresenterLayer.this.insertDatsetLinkToShare(dsDetails, dsUniqueKey);
            }

        };
        presentationManager = new PresenterManager(presenterContainer.getSubViewButtonsActionContainer(), presenterContainer.getTopMiddleLayoutContainer(), presenterContainer.getPresenterButtonsContainerLayout(), presenterContainer.getSubPresenterButtonsContainer());
        presentationManager.registerView(presenterContainer.getSearchGUIPeptideShakerToolPresenter());
        presentationManager.registerView(presenterContainer.getWelcomePage());
        presentationManager.viewLayout(presenterContainer.getWelcomePage().getViewId());
        presentationManager.registerView(presenterContainer.getFileSystemPresenter());
        presentationManager.registerView(presenterContainer.getInteractivePSPRojectResultsPresenter());
        presentationManager.setSideButtonsVisible(true);

    }

    public void loginAsGuest() {
        presenterContainer.loginAsGuest();
    }

    public void retriveToShareDataset() {
        presenterContainer.retriveToShareDataset();

    }

    /**
     * Get presenter container
     *
     * @return main presenter controller
     */
    public PresenterContainer getPresenterContainer() {
        return presenterContainer;
    }

    /**
     * View selected dataset
     *
     * @param peptideShakerVisualizationDataset web PS visualisation dataset
     *                                          object
     */
    public void viewDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        presenterContainer.viewDataset(peptideShakerVisualizationDataset);
        if (peptideShakerVisualizationDataset.isToShareDataset()) {
            for (String btn : presentationManager.getPresenterBtnsMap().keySet()) {
                presentationManager.getPresenterBtnsMap().get(btn).setEnabled(false);
            }
        }
    }

    /**
     * Update system presenter with current data including currently running
     * processes
     *
     * @param tempHistoryFilesMap list of all files including under processing
     *                            files or datasets
     * @param historyFilesMap     list of already processed files and datasets
     * @param jobsInProgress      check if there is still jobs in progress
     */
    public void updatePresenter(Map<String, GalaxyFileObject> tempHistoryFilesMap, Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress) {
        presenterContainer.updateProjectOverviewPresenter(tempHistoryFilesMap, historyFilesMap, jobsInProgress);

//        presentationManager.viewLayout(presenterContainer.getFileSystemPresenter().getViewId());
    }

    /**
     * Connect the system to Galaxy Server
     *
     * @param galaxyServerUrl   the address of Galaxy Server
     * @param userAPI           Galaxy user API key
     * @param userDataFolderUrl main folder for storing users data
     * @return System connected to Galaxy server or not
     */
    public abstract List<String> connectToGalaxyServer(String galaxyServerUrl, String userAPI, String userDataFolderUrl);

    /**
     * Connect the system to Galaxy Server
     *
     * @param galaxyServerUrl   the address of Galaxy Server
     * @param userDataFolderUrl main folder for storing users data
     */
    public abstract void viewToShareDataset(String galaxyServerUrl, String userDataFolderUrl);

    /**
     * Get user statues for the left panel at the welcome page
     *
     * @return list of user information at galaxy server
     */
    public abstract List<String> getUserOverviewData();

    /**
     * Run Online Peptide-Shaker work-flow
     *
     * @param projectName           The project name
     * @param fastaFileId           FASTA file dataset id
     * @param searchParameterFileId .par file id
     * @param mgfIdsList            list of MGF file dataset ids
     * @param searchEnginesList     List of selected search engine names
     * @param searchParam           the identification search parameter file
     * @param quant                 is the data is including quant information or only
     *                              identification
     */
    public abstract void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, IdentificationParameters searchParam, boolean quant);

    /**
     * Save search settings file into galaxy
     *
     * @param searchParameters searchParameters .par file
     * @param isNew            is new search parameter file
     * @return updated search parameters file list
     */
    public abstract Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean isNew);

    /**
     * upload file into galaxy
     *
     * @param toUploadFiles files to be uploaded to galaxy
     * @return updated files map
     */
    public abstract boolean uploadToGalaxy(PluploadFile[] toUploadFiles);

    /**
     * Abstract method to allow customised delete action for files from Galaxy
     * Server
     *
     * @param fileObject the file to be removed from Galaxy Server
     */
    public abstract void deleteDataset(GalaxyFileObject fileObject);

    /**
     * Get list of accessions available on csf-pr in order to allow mapping data
     * to csf-pr
     *
     * @return set of Uni-prot protein accessions available on csf-pr
     */
    public abstract Set<String> getCsf_pr_Accession_List();

    /**
     * Store and retrieve dataset details index to share in link
     *
     * @param dsDetails   encoded dataset details to store in database
     * @param dsUniqueKey
     * @return dataset public key
     */
    public abstract int insertDatsetLinkToShare(String dsDetails, String dsUniqueKey);

}
