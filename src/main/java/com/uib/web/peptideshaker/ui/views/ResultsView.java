package com.uib.web.peptideshaker.ui.views;

import com.uib.web.peptideshaker.ui.components.UserUploadFilesComponent;
import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.uimanager.ResultsViewSelectionManager_old;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.ui.abstracts.ViewableFrame;
import com.uib.web.peptideshaker.ui.components.items.SubViewSideButton;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.*;
import com.uib.web.peptideshaker.ui.views.subviews.DatasetProteinsSubView;
import com.uib.web.peptideshaker.ui.views.subviews.PeptidePsmsSubView;
import com.uib.web.peptideshaker.ui.views.subviews.PrideSubView;
import com.uib.web.peptideshaker.ui.views.subviews.ProteinPeptidesSubView;
import com.uib.web.peptideshaker.ui.views.subviews.UserUploadDataSubView;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import pl.exsio.plupload.PluploadFile;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * This class represent PeptideShaker view presenter which is responsible for
 * viewing the peptide shaker results on web
 *
 * @author Yehia Farag
 */
public class ResultsView extends AbsoluteLayout implements ViewableFrame {

    /**
     * The small side button (normal size screen).
     */
//    private final ButtonWithLabel mainPresenterBtn;
    /**
     * The small side button (normal size screen).
     */
//    private final SmallSideBtn smallPresenterBtn;
    /**
     * The central selection manager .
     */
    private ResultsViewSelectionManager_old Selection_Manager;
    /**
     * The main left side buttons container in big screen mode.
     */
    private VerticalLayout leftSideButtonsContainer;
    private SubViewSideButton proteinPeptidesOverviewBtn;
    private SubViewSideButton peptidePsmoverviewBtn;

    private PeptidePsmsSubView peptidePsmsSubView;
    private ProteinPeptidesSubView proteinPeptidesSubView;
    private DatasetProteinsSubView datasetProteinsSubView;

    private Future dataprocessFuture;
    /**
     * The first presenter layout (Dataset-protein level visualisation) .
     */
    private DatasetVisulizationLevelContainer datasetVisulizationLevelContainer;
    /**
     * The second presenter layout (Protein-peptides level visualisation) .
     */
    private ProteinVisulizationLevelContainer proteinsVisulizationLevelContainer;
    /**
     * The third presenter layout (Peptide-PSM level visualisation) .
     */
    private PeptideVisulizationLevelContainer peptideVisulizationLevelContainer;
    /**
     * Reference index to the last selected sup-view.
     */
    private int lastSelectedBtn = 1;
    /**
     * The view is in maximised mode.
     */
    private boolean maximisedMode;
    private SubViewSideButton datasetProteinsOverviewBtn;
    private SubViewSideButton uploadOwnDataBtn;
    private UserUploadFilesComponent userUploadDataLayoutContainer;
    private PeptideShakerVisualizationDataset peptideShakerVisualizationDataset;
    private boolean allJobsAreDone = false;

    private final AppManagmentBean appManagmentBean;

    /**
     * Constructor to initialise the main layout and attributes.
     *
     * @param sharedDataset view shared dataset using shared key
     */
    public ResultsView(boolean sharedDataset) {
        ResultsView.this.setSizeFull();
        ResultsView.this.setStyleName("activelayout");
        ResultsView.this.addStyleName("hidelowerpanel");
        this.appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
//        this.Selection_Manager = new ResultsViewSelectionManager_old();
        this.initLayout(sharedDataset);
//        ResultsView.this.minimizeView();
    }

    /**
     * Initialise the container layout.
     */
    private void initLayout(boolean sharedDataset) {
//        this.addStyleName("integratedframe");
        leftSideButtonsContainer = new VerticalLayout();
        leftSideButtonsContainer.setWidth(100, Unit.PIXELS);
        leftSideButtonsContainer.setHeightUndefined();
        leftSideButtonsContainer.setSpacing(true);
        this.addComponent(leftSideButtonsContainer, "top:120px;left:4px");

        /**
         *******left side buttons*******
         */
        LayoutEvents.LayoutClickListener listener = (LayoutEvents.LayoutClickEvent event) -> {
            String subViewId = ((SubViewSideButton) event.getComponent()).getData() + "";
            appManagmentBean.getUI_Manager().viewSubLayout(ResultsView.class.getName(), subViewId);
        };

        int buttonIndex = 1;
        SubViewSideButton prideDataButton = new SubViewSideButton("pride_data", buttonIndex++);
        prideDataButton.setDescription("Load datasets from PRIDE");
        prideDataButton.updateIconByResource(new ThemeResource("img/PRIDE_logo.png"));
        prideDataButton.addStyleName("prideimg");
        prideDataButton.setData(PrideSubView.class.getName());
        prideDataButton.addLayoutClickListener(listener);
        leftSideButtonsContainer.addComponent(prideDataButton);
        leftSideButtonsContainer.setComponentAlignment(prideDataButton, Alignment.TOP_CENTER);

        uploadOwnDataBtn = new SubViewSideButton("upload-project", buttonIndex++);
        uploadOwnDataBtn.setDescription("Upload project files");
        leftSideButtonsContainer.addComponent(uploadOwnDataBtn);
        leftSideButtonsContainer.setComponentAlignment(uploadOwnDataBtn, Alignment.TOP_CENTER);
        uploadOwnDataBtn.addStyleName("uploadbigbtn");
        uploadOwnDataBtn.setData(UserUploadDataSubView.class.getName());
        uploadOwnDataBtn.addLayoutClickListener(listener);
        uploadOwnDataBtn.updateIconByHTMLCode(VaadinIcons.FILE_TEXT_O.getHtml() + "<div class='overlayicon'>" + VaadinIcons.ARROW_CIRCLE_UP_O.getHtml() + "</div>");//VaadinIcons.UPLOAD.getHtml()
//               
//        if (!sharedDataset) {
//            uploadOwnDataBtn.addLayoutClickListener(ResultsView.this);
//        }
//        boolean mobileDeviceStyle = (Boolean) VaadinSession.getCurrent().getAttribute("mobilescreenstyle");
//        uploadOwnDataBtn.setEnabled(!mobileDeviceStyle);
//        uploadOwnDataBtn.setEnabled(!sharedDataset);
//        userUploadDataLayoutContainer = new UserUploadFilesComponent(Selection_Manager, uploadOwnDataBtn) {
//            @Override
//            public boolean[] processVisualizationDataset(String projectName, Map<String, PluploadFile> uploadedFileMap) {
//                final boolean[] checkFiles = new boolean[2];
//                Thread t = new Thread(() -> {
//                    uploadOwnDataBtn.updateIconByResource(new ThemeResource("img/loading.gif"));
//                });
//                t.start();
//                try {
//                    t.join();
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }
//                Thread t2 = new Thread(() -> {
//                    boolean[] reCheck = ResultsView.this.processVisualizationDataset(projectName, uploadedFileMap);
//                    checkFiles[0] = reCheck[0];
//                    checkFiles[1] = reCheck[1];
//                });
//                t2.start();
//                while (t2.isAlive()) {
//
//                }
//                if (!checkFiles[0] || !checkFiles[1]) {
//                    uploadOwnDataBtn.updateIconByHTMLCode(VaadinIcons.FILE_TEXT_O.getHtml() + "<div class='overlayicon'>" + VaadinIcons.ARROW_CIRCLE_UP_O.getHtml() + "</div>");//VaadinIcons.UPLOAD.getHtml()
//                }
//                return checkFiles;
//            }
//
//        };
//        userUploadDataLayoutContainer.setSizeFull();
//
//        Selection_Manager.addBtnLayout(uploadOwnDataBtn, userUploadDataLayoutContainer);
        datasetProteinsOverviewBtn = new SubViewSideButton("Dataset overview", buttonIndex++) {
            @Override
            public void setId(String id) {
                Iterator<Component> itr = this.iterator();
                while (itr.hasNext()) {
                    itr.next().setId(id);
                }
                super.setId(id);
            }
        };
        datasetProteinsOverviewBtn.addStyleName("dsoverviewbtn");
        datasetProteinsOverviewBtn.setData(DatasetProteinsSubView.class.getName());
        datasetProteinsOverviewBtn.addStyleName("inactive");
        datasetProteinsOverviewBtn.setDescription("Dataset Overview");
//        datasetProteinsOverviewBtn.setDescription("Selected dataset overview and the proteins list");
        datasetProteinsOverviewBtn.updateIconByHTMLCode(VaadinIcons.CLUSTER.getHtml());
        datasetProteinsOverviewBtn.updateIconByResource(new ThemeResource("img/venn_color.png"));//img/vizicon.png
        leftSideButtonsContainer.addComponent(datasetProteinsOverviewBtn);
        datasetProteinsOverviewBtn.addLayoutClickListener(listener);
        leftSideButtonsContainer.setComponentAlignment(datasetProteinsOverviewBtn, Alignment.TOP_CENTER);
//        datasetsOverviewBtn.addLayoutClickListener(ResultsView.this);
//        datasetVisulizationLevelContainer = new DatasetVisulizationLevelContainer(Selection_Manager, datasetsOverviewBtn);
//        datasetVisulizationLevelContainer.setSizeFull();
//        Selection_Manager.addBtnLayout(datasetsOverviewBtn, datasetVisulizationLevelContainer);

        proteinPeptidesOverviewBtn = new SubViewSideButton("Protein Overview", buttonIndex++);
        proteinPeptidesOverviewBtn.setDescription("Protein Overview");
        proteinPeptidesOverviewBtn.updateIconByResource(null);
        proteinPeptidesOverviewBtn.setData(ProteinPeptidesSubView.class.getName());
        proteinPeptidesOverviewBtn.addStyleName("proteinoverviewbtn");
        proteinPeptidesOverviewBtn.addLayoutClickListener(listener);
        leftSideButtonsContainer.addComponent(proteinPeptidesOverviewBtn);
        leftSideButtonsContainer.setComponentAlignment(proteinPeptidesOverviewBtn, Alignment.TOP_CENTER);
//        proteinoverviewBtn.addLayoutClickListener(ResultsView.this);

//        proteinsVisulizationLevelContainer = new ProteinVisulizationLevelContainer(Selection_Manager, proteinoverviewBtn);
//        Selection_Manager.addBtnLayout(proteinoverviewBtn, proteinsVisulizationLevelContainer);
        peptidePsmoverviewBtn = new SubViewSideButton("PSM Overview", buttonIndex++);
        peptidePsmoverviewBtn.updateIconByResource(null);
        peptidePsmoverviewBtn.setDescription("Peptide Spectrum Matches");
        peptidePsmoverviewBtn.setData(PeptidePsmsSubView.class.getName());
        peptidePsmoverviewBtn.addStyleName("psmoverviewbtn");
        peptidePsmoverviewBtn.addLayoutClickListener(listener);
        leftSideButtonsContainer.addComponent(peptidePsmoverviewBtn);
        leftSideButtonsContainer.setComponentAlignment(peptidePsmoverviewBtn, Alignment.TOP_CENTER);

//        peptideVisulizationLevelContainer = new PeptideVisulizationLevelContainer(Selection_Manager, psmoverviewBtn);
//        Selection_Manager.addBtnLayout(psmoverviewBtn, peptideVisulizationLevelContainer);
        /**
         ***** end left side buttons / start sub view ******
         */
        AbsoluteLayout subviewContainerFrame = new AbsoluteLayout();
        subviewContainerFrame.setSizeFull();
        subviewContainerFrame.setStyleName("integratedframe");
        this.addComponent(subviewContainerFrame, "left:100px");

        AbsoluteLayout subviewContainerContent = new AbsoluteLayout();
        subviewContainerContent.addStyleName("viewframecontent");
        subviewContainerContent.setSizeFull();
        subviewContainerFrame.addComponent(subviewContainerContent, "left:10px;right:10px;top:10px;bottom:10px;");

        PrideSubView prideSubView = new PrideSubView();
        subviewContainerContent.addComponent(prideSubView);
        appManagmentBean.getUI_Manager().registerSubView(this.getViewId(), prideSubView);

        UserUploadDataSubView userUploadDataSubView = new UserUploadDataSubView();
        subviewContainerContent.addComponent(userUploadDataSubView);
        appManagmentBean.getUI_Manager().registerSubView(this.getViewId(), userUploadDataSubView);

        datasetProteinsSubView = new DatasetProteinsSubView();
        subviewContainerContent.addComponent(datasetProteinsSubView);
        appManagmentBean.getUI_Manager().registerSubView(this.getViewId(), datasetProteinsSubView);

        proteinPeptidesSubView = new ProteinPeptidesSubView();
        subviewContainerContent.addComponent(proteinPeptidesSubView);
        appManagmentBean.getUI_Manager().registerSubView(this.getViewId(), proteinPeptidesSubView);

        peptidePsmsSubView = new PeptidePsmsSubView();
        subviewContainerContent.addComponent(peptidePsmsSubView);
        appManagmentBean.getUI_Manager().registerSubView(this.getViewId(), peptidePsmsSubView);

        appManagmentBean.getUI_Manager().viewSubLayout(this.getViewId(), userUploadDataSubView.getViewId());
//        subviewContainerLayout.addComponent(userUploadDataLayoutContainer);
//        subviewContainerLayout.addComponent(datasetVisulizationLevelContainer);
//        subviewContainerLayout.addComponent(proteinsVisulizationLevelContainer);
//        subviewContainerLayout.addComponent(peptideVisulizationLevelContainer);
//        LayoutEvents.LayoutClickListener notificationListener = (LayoutEvents.LayoutClickEvent event) -> {
//            Component c = event.getClickedComponent();
//
//            if (c != null && c.getId() != null && c.getId().contains("suspend")) {
//                Notification.show("You need to select Project or upload your own files to visualize data", Notification.Type.TRAY_NOTIFICATION);
//            }
//        };
//        leftSideButtonsContainer.addLayoutClickListener(notificationListener);
//
//        if (!mobileDeviceStyle) {
//            uploadOwnDataBtn.setSelected(true);
//        }
    }

    /**
     * Get the main frame layout
     *
     * @return PeptideShaker results view presenter layout
     */
//    @Override
//    public VerticalLayout getMainView() {
//        return this;
//    }
    /**
     * Get the small right side button component (represent view control button
     * in large screen mode)
     *
     * @return right view control button
     */
//    @Override
//    public SmallSideBtn getSmallPresenterControlButton() {
//        return smallPresenterBtn;
//    }
    /**
     * Get the current view ID
     *
     * @return view id
     */
    @Override
    public String getViewId() {
        return ResultsView.class.getName();
    }

    /**
     * Hide the main view for the current component.
     */
    @Override
    public void minimizeView() {
//        mainPresenterBtn.setSelected(false);
//        smallPresenterBtn.setSelected(false);
        this.addStyleName("hidepanel");
        this.leftSideButtonsContainer.removeStyleName("visible");
//        this.maximisedMode = false;

    }

    /**
     * Show the main view for the current component.
     */
    @Override
    public void maximizeView() {
        this.leftSideButtonsContainer.addStyleName("visible");
        this.removeStyleName("hidepanel");

//        if (maximisedMode) {
//            return;
//        }
//        UI.getCurrent().accessSynchronously(new Runnable() {
//            @Override
//            public void run() {
////                smallPresenterBtn.setSelected(true);
////                mainPresenterBtn.setSelected(true);
//                datasetVisulizationLevelContainer.setMargin(new MarginInfo(false, false, false, false));
//                maximisedMode = true;
//                viewControlButtonContainer.addStyleName("visible");
//                if (dataprocessFuture == null && peptideShakerVisualizationDataset==null) {
//                    datasetsOverviewBtn.setId("suspend");
//                    datasetsOverviewBtn.setEnabled(false);
//                    datasetsOverviewBtn.setDescription("Select project or upload your own project data");
//                    datasetsOverviewBtn.addStyleName("inactive");
//                    selectSubviewButton(uploadOwnDataBtn);
//                    removeStyleName("hidepanel");
//                    return;
//
//                }
//                datasetsOverviewBtn.setId(null);  
//                datasetsOverviewBtn.setEnabled(true);
//                while (dataprocessFuture != null && !dataprocessFuture.isDone()) {
//                }
//                removeStyleName("hidepanel");
//                selectSubviewButton(datasetsOverviewBtn);
//                datasetsOverviewBtn.removeStyleName("inactive");
//
//                ScheduledExecutorService exe = Executors.newSingleThreadScheduledExecutor();
//                Future f = exe.schedule(() -> {
//                    UI.getCurrent().removeStyleName("busybrocess");
//                    if (peptideShakerVisualizationDataset.isToShareDataset()) {
//                        UI.getCurrent().push();
//                    }
//                }, 5, TimeUnit.SECONDS);
//                exe.shutdown();
//                while (!f.isDone()) {
//                }
//
//            }
//        });
    }

    /**
     * Layout click method that is used to coordinate view inside the layout (in
     * case of multiple view under the same presenter).
     *
     * @param event left side button clicked action
     */
//    @Override
//    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
//        SubViewSideButton comp = (SubViewSideButton) event.getComponent();
//        Selection_Manager.selectBtn(comp);
//        if (proteinsVisulizationLevelContainer != null) {
//            if (comp.getBtnId() == 3 && lastSelectedBtn != 3) {
//                proteinsVisulizationLevelContainer.activate3DProteinView();
//            }
//        }
//        lastSelectedBtn = comp.getBtnId();
//
//    }
    /**
     * Select sub visualisation for dataset presenter
     *
     * @param subPresenterBtn sub visualisation button (dataset, proteins, etc.
     */
//    public void selectSubviewButton(SubViewSideButton subPresenterBtn) {
//        Selection_Manager.selectBtn(subPresenterBtn);
//        if (proteinsVisulizationLevelContainer != null) {
//            if (subPresenterBtn.getBtnId() == 3 && lastSelectedBtn != 3) {
//                proteinsVisulizationLevelContainer.activate3DProteinView();
//            }
//        }
//        lastSelectedBtn = subPresenterBtn.getBtnId();
//
//    }
    /**
     * Get the left side container for left side big buttons (to be used in case
     * of large screen mode)
     *
     * @return left side buttons container
     */
//    @Override
//    public VerticalLayout getSubViewButtonsActionContainerLayout() {
//        return viewControlButtonContainer;
//    }
    /**
     * Get presenter button
     *
     * @return main presenter button
     */
//    @Override
//    public ButtonWithLabel getMainPresenterButton() {
//        return mainPresenterBtn;
//    }
    /**
     * Activate PeptideShaker dataset visualisation upon user selection
     *
     * @param peptideShakerVisualizationDataset PeptideShaker visualisation
     * dataset
     */
    public void setSelectedDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
//        this.peptideShakerVisualizationDataset = peptideShakerVisualizationDataset;
//        ExecutorService executorService = Executors.newFixedThreadPool(4);
//
//        Runnable runnableTask1 = () -> {
////            mainPresenterBtn.setEnabled(peptideShakerVisualizationDataset != null);
////            smallPresenterBtn.setEnabled(peptideShakerVisualizationDataset != null);
//            Selection_Manager.reset();
//            Selection_Manager.selectBtn(0);
//            datasetVisulizationLevelContainer.selectDataset(peptideShakerVisualizationDataset);
//        };
//        Runnable runnableTask2 = () -> {
//            proteinsVisulizationLevelContainer.selectDataset(peptideShakerVisualizationDataset);
//        };
//        Runnable runnableTask3 = () -> {
//            if (!peptideShakerVisualizationDataset.isUploadedProject()) {
//                peptideVisulizationLevelContainer.selectDataset(peptideShakerVisualizationDataset);
//            }
//
//        };
//        dataprocessFuture = executorService.submit(runnableTask1);
//        executorService.submit(runnableTask2);
//        executorService.submit(runnableTask3);
//        executorService.shutdown();
//        UI.getCurrent().addStyleName("busybrocess");
//        while (!dataprocessFuture.isDone()) {
//        }
//        uploadOwnDataBtn.updateIconByHTMLCode(VaadinIcons.FILE_TEXT_O.getHtml() + "<div class='overlayicon'>" + VaadinIcons.ARROW_CIRCLE_UP_O.getHtml() + "</div>");
//        if (!peptideShakerVisualizationDataset.isUploadedProject() || !maximisedMode) {
//            maximisedMode = false;
//            this.maximizeView();
//        } else {
//            UI.getCurrent().removeStyleName("busybrocess");
//            datasetProteinsOverviewBtn.removeStyleName("inactive");
//            datasetProteinsOverviewBtn.setId(null);
//            datasetProteinsOverviewBtn.setEnabled(true);
//        }

    }

    @Override
    public void update() {
        try {

            if (appManagmentBean.getUI_Manager().getSelectedDatasetId() != null) {
                datasetProteinsOverviewBtn.removeStyleName("inactive");        
                if (appManagmentBean.getUI_Manager().getSelectedProteinIndex() != -1) {
                    proteinPeptidesOverviewBtn.setVisible(true);
                    proteinPeptidesOverviewBtn.updateIconByResource(new ExternalResource(appManagmentBean.getUI_Manager().getEncodedProteinButtonImage()));
                } else {
                    proteinPeptidesOverviewBtn.setVisible(false);
                }
                if (appManagmentBean.getUI_Manager().getSelectedPeptideIndex() != -1) {
                    peptidePsmoverviewBtn.setVisible(true);
                    peptidePsmoverviewBtn.updateIconByResource(new ExternalResource(appManagmentBean.getUI_Manager().getEncodedPeptideButtonImage()));
                } else {
                    peptidePsmoverviewBtn.setVisible(false);
                }
            } else {
                datasetProteinsOverviewBtn.addStyleName("inactive");
                peptidePsmoverviewBtn.setVisible(false);
                proteinPeptidesOverviewBtn.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
