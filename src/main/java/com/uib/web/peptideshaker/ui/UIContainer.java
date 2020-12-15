package com.uib.web.peptideshaker.ui;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.model.UploadedProjectUtility;
import com.uib.web.peptideshaker.ui.views.FileSystemView;
import com.uib.web.peptideshaker.ui.views.ResultsView;
import com.uib.web.peptideshaker.ui.views.WorkflowView;
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
public abstract class UIContainer extends AbsoluteLayout {

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
    private ResultsView interactivePSPRojectResultsPresenter;
    /**
     * The SearchGUI & PeptideShaker tools view component (frame to start
     * analysis).
     */
    private WorkflowView searchGUIPeptideShakerToolPresenter;
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
    public UIContainer(boolean availableGalaxyServer) {
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
        welcomePageView = new WelcomePageView(availableGalaxyServer);
        UIContainer.this.addComponent(welcomePageView, "left:100px");

//        
//        
//        
//        
//        
//        
//        
//        searchGUIPeptideShakerToolPresenter = new WorkflowView() {
//            @Override
//            public void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> inputFilesIdsList, Set<String> searchEnginesList, IdentificationParameters searchParameters, boolean quant) {
//                UIContainer.this.execute_SearchGUI_PeptideShaker_WorkFlow(projectName, fastaFileId, searchParameterFileId, inputFilesIdsList, searchEnginesList, searchParameters, quant);
//                viewLayout(fileSystemView.getViewId());
//            }
//
//            @Override
//            public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean isNew) {
//                return UIContainer.this.saveSearchGUIParameters(searchParameters, isNew);
//            }
//
//            @Override
//            public void maximizeView() {
//                super.maximizeView();
//            }
//
//            @Override
//            public boolean uploadToGalaxy(PluploadFile[] toUploadFiles) {
//                boolean check = UIContainer.this.uploadToGalaxy(toUploadFiles);
//                /**
//                 * Invoke busy history only : active globe
//                 */
//                fileSystemView.updateData(null, check,"");
//                return check;
//
//            }
//
//        };
//
//        if (searchGUIPeptideShakerToolPresenter.getMainPresenterButton().isEnabled()) {
//            searchGUIPeptideShakerToolPresenter.getMainPresenterButton().setEnabled(availableGalaxyServer);
//            searchGUIPeptideShakerToolPresenter.getSmallPresenterControlButton().setEnabled(availableGalaxyServer);
//        }
//        if (!availableGalaxyServer) {
//            Notification.show("Galaxy server is not available", Notification.Type.TRAY_NOTIFICATION);
//            searchGUIPeptideShakerToolPresenter.getMainPresenterButton().setDescription("Galaxy server is not available");
//            searchGUIPeptideShakerToolPresenter.getSmallPresenterControlButton().setDescription("Galaxy server is not available");
//        }
//
        fileSystemView = new FileSystemView();
        UIContainer.this.addComponent(fileSystemView, "left:100px");
//
//        interactivePSPRojectResultsPresenter = new ResultsView(false) {
//            @Override
//            public boolean[] processVisualizationDataset(String projectName, Map<String, PluploadFile> uploadedFileMap) {
//                return uploadedProjectUtility.processVisualizationDataset(projectName, uploadedFileMap, getCsf_pr_Accession_List());
//            }
//
//        };
//
//        this.uploadedProjectUtility = new UploadedProjectUtility() {
//            @Override
//            public void viewUploadedProjectDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
//                String apiKey = VaadinSession.getCurrent().getAttribute("ApiKey") + "";
//                ((LinkedHashMap<String, GalaxyFileObject>) VaadinSession.getCurrent().getAttribute("uploaded_projects_" + apiKey)).put(peptideShakerVisualizationDataset.getProjectName(), peptideShakerVisualizationDataset);
//                Map<String, GalaxyFileObject> historyFilesMap = new LinkedHashMap<>();
//                historyFilesMap.put(peptideShakerVisualizationDataset.getProjectName(), peptideShakerVisualizationDataset);
//                historyFilesMap.putAll(fileSystemView.getHistoryFilesMap());
//                fileSystemView.updateData(historyFilesMap, fileSystemView.isJobInProgress(),peptideShakerVisualizationDataset.getGalaxyId());
//                interactivePSPRojectResultsPresenter.setSelectedDataset(peptideShakerVisualizationDataset);
////                viewLayout(interactivePSPRojectResultsPresenter.getViewId());
//            }
//
//        };

    }

    public void loginAsGuest() {
//        welcomePageView.loginAsGuest();
    }

    public void retriveToShareDataset() {
//        welcomePageView.retriveToShareDataset();

    }

//    public HorizontalLayout getMainComponentContainer() {
//        return mainComponentContainer;
//    }
//
//    public AbsoluteLayout getSubViewButtonsActionContainer() {
//        return subViewButtonsActionContainer;
//    }
//
//    public AbsoluteLayout getTopMiddleLayoutContainer() {
//        return topMiddleLayoutContainer;
//    }
//
//    public VerticalLayout getMiddleLayoutContainer() {
//        return middleLayoutContainer;
//    }
    public AbsoluteLayout getPresenterButtonsContainerLayout() {
        return new AbsoluteLayout();// presenterButtonsContainerLayout;
    }

//    public Button getSignOutBtn() {
//        return signOutBtn;
//    }
    public ResultsView getInteractivePSPRojectResultsPresenter() {
        return interactivePSPRojectResultsPresenter;
    }

    public WorkflowView getSearchGUIPeptideShakerToolPresenter() {
        return searchGUIPeptideShakerToolPresenter;
    }

    public FileSystemView getFileSystemView() {
        return fileSystemView;
    }

    public WelcomePageView getWelcomePageView() {
        return welcomePageView;
    }

    public void viewDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
      
    }

    public void updateProjectOverviewPresenter(Map<String, GalaxyFileObject> tempHistoryFilesMap, Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress) {
//        fileSystemView.updateData(tempHistoryFilesMap, jobsInProgress,"");
        searchGUIPeptideShakerToolPresenter.updateData(historyFilesMap);
    }

    public abstract void viewLayout(String viewId);

    public abstract void registerView(ViewableFrame view);

    /**
     * Connect the system to Galaxy Server
     *
     * @param galaxyServerUrl the address of Galaxy Server
     * @param userDataFolderUrl main folder for storing users data
     */
    public abstract void viewToShareDataset(String galaxyServerUrl, String userDataFolderUrl);

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
