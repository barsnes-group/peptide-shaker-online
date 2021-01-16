package com.uib.web.peptideshaker.ui;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.model.UploadedProjectUtility;
import com.uib.web.peptideshaker.ui.views.FileSystemView;
import com.uib.web.peptideshaker.ui.views.ResultsView;
import com.uib.web.peptideshaker.ui.views.WorkflowInvokingView;
import com.uib.web.peptideshaker.ui.abstracts.ViewableFrame;
import com.uib.web.peptideshaker.ui.components.ViewActionButtonsComponent;
import com.uib.web.peptideshaker.ui.views.WelcomePageView;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import pl.exsio.plupload.PluploadFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents container layout that contain all UI sub components
 *
 * @author Yehia Mokhtar Farag
 */
public class UIContainer extends AbsoluteLayout {

//    private final HorizontalLayout mainComponentContainer;
//    /**
//     * Left layout container is the main layout container that contain the main
//     * views.
//     */
//    private final AbsoluteLayout subViewButtonsActionContainer;
//    /**
//     * Left layout container is the main layout container that contain the main
//     * views.
//     */
//    private final AbsoluteLayout topMiddleLayoutContainer;
//    /**
//     * Left layout container is the main layout container that contain the main
//     * views.
//     */
//    private final VerticalLayout middleLayoutContainer;
//
//    private  Button signOutBtn;
    /**
     * upload project utility to allow users to upload their ready files to
     * visualise .
     */
    private UploadedProjectUtility uploadedProjectUtility;
    /**
     * Initialise welcome page .
     */
    private WelcomePageView welcomePageView;
    /**
     * Container to view selected PeptideShaker projects.
     */
    private ResultsView resultsView;
    /**
     * The SearchGUI & PeptideShaker tools view component (frame to start
     * analysis).
     */
    private WorkflowInvokingView workflowInvokingView;
    /**
     * Container to view the main available data-sets and files on galaxy server
     * or in other databases.
     */
    private FileSystemView fileSystemView;

    /**
     * Initialise container layout.
     *
     * @param availableGalaxyServer galaxy server is available online
     */
    public UIContainer() {
        UIContainer.this.setSizeFull();
//        UIContainer.this.setMargin(new MarginInfo(true, true, true, true));
        UIContainer.this.addStyleName("mainapplicationframe");
        UIContainer.this.addStyleName("frame");

//        mainComponentContainer = new HorizontalLayout();
//        UIContainer.this.mainComponentContainer.setSizeFull();
//        UIContainer.this.mainComponentContainer.setStyleName("mainlayout");
//        UIContainer.this.addComponent(mainComponentContainer);
//        UIContainer.this.setComponentAlignment(mainComponentContainer, Alignment.TOP_CENTER);
//        subViewButtonsActionContainer = new AbsoluteLayout();
//        subViewButtonsActionContainer.setSizeFull();
//        subViewButtonsActionContainer.setStyleName("subviewbuttonsactioncontainer");
//        mainComponentContainer.addComponent(subViewButtonsActionContainer);
//        mainComponentContainer.setExpandRatio(subViewButtonsActionContainer, 0);
//
//        middleLayoutContainer = new VerticalLayout();
//        middleLayoutContainer.setSizeFull();
//        middleLayoutContainer.setStyleName("middleviewcontainer");
//        mainComponentContainer.addComponent(middleLayoutContainer);
//        mainComponentContainer.setExpandRatio(middleLayoutContainer, 100);
//
//        topMiddleLayoutContainer = new AbsoluteLayout();
//        topMiddleLayoutContainer.setSizeFull();
//        middleLayoutContainer.addComponent(topMiddleLayoutContainer);
//        middleLayoutContainer.setExpandRatio(topMiddleLayoutContainer, 100);
//
//        
//        
//       
//      
//        
//
//       
//
//        signOutBtn = new Button("Signout");
//        signOutBtn.addClickListener((Button.ClickEvent event) -> {
////            VaadinSession.getCurrent().getSession().setMaxInactiveInterval(10);
////            Page.getCurrent().reload();
//        });
        ViewActionButtonsComponent actionButtonsComponent = new ViewActionButtonsComponent();
        UIContainer.this.addComponent(actionButtonsComponent);
        /**
         * landing page initialization.
         */
        welcomePageView = new WelcomePageView();
        UIContainer.this.addComponent(welcomePageView, "left:0px");

        
        workflowInvokingView = new WorkflowInvokingView();
        UIContainer.this.addComponent(workflowInvokingView, "left:0px");
        
        fileSystemView = new FileSystemView();
        UIContainer.this.addComponent(fileSystemView, "left:0px");
//
        resultsView = new ResultsView(false);
        UIContainer.this.addComponent(resultsView, "left:0px");


    }


    public void retriveToShareDataset() {
//        welcomePageView.retriveToShareDataset();

    }


    public ResultsView getResultsView() {
        return resultsView;
    }

    public WorkflowInvokingView getWorkflowInvokingView() {
        return workflowInvokingView;
    }

    public FileSystemView getFileSystemView() {
        return fileSystemView;
    }

    public WelcomePageView getWelcomePageView() {
        return welcomePageView;
    }

    public void viewDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {

    }

//    public void updateProjectOverviewPresenter(Map<String, GalaxyFileObject> tempHistoryFilesMap, Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress) {
////        fileSystemView.updateData(tempHistoryFilesMap, jobsInProgress,"");
//        workflowInvokingView.updateData(historyFilesMap);
//    }
//    public abstract void viewLayout(String viewId);

//    public abstract void registerView(ViewableFrame view);

    /**
     * Connect the system to Galaxy Server
     *
     * @param galaxyServerUrl the address of Galaxy Server
     * @param userDataFolderUrl main folder for storing users data
     */
//    public abstract void viewToShareDataset(String galaxyServerUrl, String userDataFolderUrl);

    /**
     * Run Online Peptide-Shaker work-flow
     *
     * @param projectName The project name
     * @param fastaFileId FASTA file dataset id
     * @param searchParameterFileId .par file id
     * @param mgfIdsList list of MGF file dataset ids
     * @param searchEnginesList List of selected search engine names
     * @param searchParam
     * @param quant
     */
//    public abstract void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, IdentificationParameters searchParam, boolean quant);

    /**
     * Save search settings file into galaxy
     *
     * @param searchParameters searchParameters .par file
     * @param isNew is new search parameter file
     * @return updated search parameters file list
     */
//    public abstract Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean isNew);

    /**
     * upload file into galaxy
     *
     * @param toUploadFiles files to be uploaded to galaxy
     * @return updated files map
     */
//    public abstract boolean uploadToGalaxy(PluploadFile[] toUploadFiles);

    /**
     * Abstract method to allow customised delete action for files from Galaxy
     * Server
     *
     * @param fileObject the file to be removed from Galaxy Server
     */
//    public abstract void deleteDataset(GalaxyFileObject fileObject);

//    public abstract Set<String> getCsf_pr_Accession_List();

    /**
     * Store and retrieve dataset details index to share in link
     *
     * @param dsDetails encoded dataset details to store in database
     * @param dsUniqueKey
     * @return dataset public key
     */
//    public abstract int insertDatsetLinkToShare(String dsDetails, String dsUniqueKey);

}
