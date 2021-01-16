package com.uib.web.peptideshaker.handler;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.ui.UIContainer;
import com.uib.web.peptideshaker.ui.views.FileSystemView;
import com.vaadin.server.VaadinSession;
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
public abstract class UIHandler {

    /**
     * PeptideShaker visualisation layer - Coordinator to organise the different
     * views (home, analysis, data, or results visualisation).
     */
    private final UIContainer uiContainer;
    private final AppManagmentBean appManagmentBean;

    /**
     * Initialise the main presenter components.
     *
     * @param availableGalaxyServer galaxy server available online
     */
    public UIHandler() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        this.uiContainer = new UIContainer();
        UI.getCurrent().setContent(uiContainer);
        if (appManagmentBean.isAvailableGalaxy()) {
            appManagmentBean.getUI_Manager().registerView(uiContainer.getWorkflowInvokingView());
        }
        appManagmentBean.getUI_Manager().registerView(uiContainer.getWelcomePageView());//     
        appManagmentBean.getUI_Manager().registerView(uiContainer.getFileSystemView());
        appManagmentBean.getUI_Manager().registerView(uiContainer.getResultsView());
//        appManagmentBean.getUI_Manager().viewLayout(FileSystemView.class.getName());

    }

    public void retriveToShareDataset() {
        uiContainer.retriveToShareDataset();

    }

    /**
     * Get presenter container
     *
     * @return main presenter controller
     */
    public UIContainer getUiContainer() {
        return uiContainer;
    }

    /**
     * View selected dataset
     *
     * @param peptideShakerVisualizationDataset web PS visualisation dataset
     * object
     */
    public void viewDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        uiContainer.viewDataset(peptideShakerVisualizationDataset);
        if (peptideShakerVisualizationDataset.isToShareDataset()) {
//            for (String btn : uiManager.getPresenterBtnsMap().keySet()) {
//                uiManager.getPresenterBtnsMap().get(btn).setEnabled(false);
//            }
        }
    }
//
//    /**
//     * Update system presenter with current data including currently running
//     * processes
//     *
//     * @param tempHistoryFilesMap list of all files including under processing
//     * files or datasets
//     * @param historyFilesMap list of already processed files and datasets
//     * @param jobsInProgress check if there is still jobs in progress
//     */
//    public void updatePresenter(Map<String, GalaxyFileObject> tempHistoryFilesMap, Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress) {
//        uiContainer.updateProjectOverviewPresenter(tempHistoryFilesMap, historyFilesMap, jobsInProgress);
//
////        presentationManager.viewLayout(uiContainer.getFileSystemPresenter().getViewId());
//    }

    /**
     * Connect the system to Galaxy Server
     *
     * @param galaxyServerUrl the address of Galaxy Server
     * @param userAPI Galaxy user API key
     * @param userDataFolderUrl main folder for storing users data
     * @return System connected to Galaxy server or not
     */
    public abstract List<String> connectToGalaxyServer(String galaxyServerUrl, String userAPI, String userDataFolderUrl);

    /**
     * Connect the system to Galaxy Server
     *
     * @param galaxyServerUrl the address of Galaxy Server
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
     * @param projectName The project name
     * @param fastaFileId FASTA file dataset id
     * @param searchParameterFileId .par file id
     * @param mgfIdsList list of MGF file dataset ids
     * @param searchEnginesList List of selected search engine names
     * @param searchParam the identification search parameter file
     * @param quant is the data is including quant information or only
     * identification
     */
    public abstract void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, IdentificationParameters searchParam, boolean quant);

    /**
     * Save search settings file into galaxy
     *
     * @param searchParameters searchParameters .par file
     * @param isNew is new search parameter file
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
     * @param dsDetails encoded dataset details to store in database
     * @param dsUniqueKey
     * @return dataset public key
     */
    public abstract int insertDatsetLinkToShare(String dsDetails, String dsUniqueKey);

}
