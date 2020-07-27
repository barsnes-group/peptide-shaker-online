package com.uib.web.peptideshaker.presenter;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.DatasetVisulizationLevelContainer;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.ProteinVisulizationLevelContainer;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.PresenterSubViewSideBtn;
import com.uib.web.peptideshaker.presenter.core.ButtonWithLabel;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.PeptideVisulizationLevelContainer;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.UserUploadFilesContainer;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import pl.exsio.plupload.PluploadFile;

/**
 * This class represent PeptideShaker view presenter which is responsible for
 * viewing the peptide shaker results on web
 *
 * @author Yehia Farag
 */
public abstract class InteractivePSPRojectResultsPresenter extends VerticalLayout implements ViewableFrame, LayoutEvents.LayoutClickListener {

    /**
     * The small side button (normal size screen).
     */
    private final ButtonWithLabel mainPresenterBtn;
    /**
     * The small side button (normal size screen).
     */
    private final SmallSideBtn smallPresenterBtn;
    /**
     * The main left side buttons container in big screen mode.
     */
    private VerticalLayout viewControlButtonContainer;
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
     * The central selection manager .
     */
    private final SelectionManager Selection_Manager;
    /**
     * Reference index to the last selected sup-view.
     */
    private int lastSelectedBtn = 1;
    /**
     * The view is in maximised mode.
     */
    private boolean maximisedMode;
    private PresenterSubViewSideBtn datasetsOverviewBtn;
    private PresenterSubViewSideBtn uploadOwnDataBtn;
    private UserUploadFilesContainer userUploadDataLayoutContainer;

    /**
     * Constructor to initialise the main layout and attributes.
     *
     * @param sharedDataset view shared dataset using shared key
     */
    public InteractivePSPRojectResultsPresenter(boolean sharedDataset) {
        InteractivePSPRojectResultsPresenter.this.setSizeFull();
        InteractivePSPRojectResultsPresenter.this.setStyleName("activelayout");
        this.smallPresenterBtn = new SmallSideBtn(VaadinIcons.CLUSTER);
        smallPresenterBtn.updateIconSourceURL("img/venn_color.png");
        smallPresenterBtn.setDescription("Visualize selected projects / Upload your own project files");
        this.smallPresenterBtn.setData(InteractivePSPRojectResultsPresenter.this.getViewId());
        this.smallPresenterBtn.addStyleName("resultsmallbtn");
        this.mainPresenterBtn = new ButtonWithLabel("Visualize Data</br><font>Visualize/Upload project</font>", 1);
        this.mainPresenterBtn.setData(InteractivePSPRojectResultsPresenter.this.getViewId());
        this.mainPresenterBtn.updateIcon(VaadinIcons.CLUSTER.getHtml());
        this.mainPresenterBtn.updateIconResource(new ThemeResource("img/venn_color.png"));
        this.mainPresenterBtn.setDescription("Visualize selected projects / Upload your own project files");
        this.mainPresenterBtn.setEnabled(true);
        this.mainPresenterBtn.addStyleName("orangeiconcolor");
        this.mainPresenterBtn.addStyleName("resultsbtn");
        this.smallPresenterBtn.setEnabled(true);
        this.Selection_Manager = new SelectionManager();
        this.initLayout(sharedDataset);
        InteractivePSPRojectResultsPresenter.this.minimizeView();
    }

    /**
     * Initialise the container layout.
     */
    private void initLayout(boolean sharedDataset) {
        this.addStyleName("integratedframe");
        viewControlButtonContainer = new VerticalLayout();
        viewControlButtonContainer.setWidth(100, Unit.PERCENTAGE);
        viewControlButtonContainer.setHeightUndefined();
        viewControlButtonContainer.setSpacing(false);
        viewControlButtonContainer.setMargin(new MarginInfo(false, false, true, false));
        uploadOwnDataBtn = new PresenterSubViewSideBtn("upload-project", 1);
        viewControlButtonContainer.addComponent(uploadOwnDataBtn);
        viewControlButtonContainer.setComponentAlignment(uploadOwnDataBtn, Alignment.MIDDLE_CENTER);
        if (!sharedDataset) {
            uploadOwnDataBtn.addLayoutClickListener(InteractivePSPRojectResultsPresenter.this);
        }
        boolean mobileDeviceStyle = (Boolean) VaadinSession.getCurrent().getAttribute("mobilescreenstyle");
        uploadOwnDataBtn.setEnabled(!mobileDeviceStyle);
        uploadOwnDataBtn.setEnabled(!sharedDataset);
        userUploadDataLayoutContainer = new UserUploadFilesContainer(Selection_Manager, uploadOwnDataBtn) {
            @Override
            public boolean[] processVisualizationDataset(String projectName, Map<String, PluploadFile> uploadedFileMap) {
                final boolean[] checkFiles = new boolean[2];
                Thread t = new Thread(() -> {
                    uploadOwnDataBtn.updateIconByResource(new ThemeResource("img/loading.gif"));
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                Thread t2 = new Thread(() -> {
                    boolean[] reCheck = InteractivePSPRojectResultsPresenter.this.processVisualizationDataset(projectName, uploadedFileMap);
                    checkFiles[0] = reCheck[0];
                    checkFiles[1] = reCheck[1];
                });
                t2.start();
                while (t2.isAlive()) {

                }
                if (!checkFiles[0] || !checkFiles[1]) {
                    uploadOwnDataBtn.updateIconByHTMLCode(VaadinIcons.FILE_TEXT_O.getHtml() + "<div class='overlayicon'>" + VaadinIcons.ARROW_CIRCLE_UP_O.getHtml() + "</div>");//VaadinIcons.UPLOAD.getHtml()
                }
                return checkFiles;
            }

        };
        userUploadDataLayoutContainer.setSizeFull();

        Selection_Manager.addBtnLayout(uploadOwnDataBtn, userUploadDataLayoutContainer);
        datasetsOverviewBtn = new PresenterSubViewSideBtn("Dataset overview", 2) {
            @Override
            public void setId(String id) {
                Iterator<Component> itr = this.iterator();
                while (itr.hasNext()) {
                    itr.next().setId(id);
                }
                super.setId(id);
            }
        };
        datasetsOverviewBtn.addStyleName("dsoverviewbtn");
        datasetsOverviewBtn.setData("datasetoverview");
        datasetsOverviewBtn.addStyleName("inactive");
        datasetsOverviewBtn.setDescription("Dataset Overview");
        viewControlButtonContainer.addComponent(datasetsOverviewBtn);
        viewControlButtonContainer.setComponentAlignment(datasetsOverviewBtn, Alignment.MIDDLE_CENTER);
        datasetsOverviewBtn.addLayoutClickListener(InteractivePSPRojectResultsPresenter.this);
        datasetVisulizationLevelContainer = new DatasetVisulizationLevelContainer(Selection_Manager, datasetsOverviewBtn);
        datasetVisulizationLevelContainer.setSizeFull();
        Selection_Manager.addBtnLayout(datasetsOverviewBtn, datasetVisulizationLevelContainer);

        PresenterSubViewSideBtn proteinoverviewBtn = new PresenterSubViewSideBtn("Protein Overview", 3);
        proteinoverviewBtn.setDescription("Protein Overview");
        proteinoverviewBtn.updateIconByResource(null);
        proteinoverviewBtn.setData("proteinoverview");
        proteinoverviewBtn.addStyleName("proteinoverviewbtn");
        viewControlButtonContainer.addComponent(proteinoverviewBtn);
        viewControlButtonContainer.setComponentAlignment(proteinoverviewBtn, Alignment.MIDDLE_CENTER);
        proteinoverviewBtn.addLayoutClickListener(InteractivePSPRojectResultsPresenter.this);

        proteinsVisulizationLevelContainer = new ProteinVisulizationLevelContainer(Selection_Manager, proteinoverviewBtn);
        Selection_Manager.addBtnLayout(proteinoverviewBtn, proteinsVisulizationLevelContainer);

        PresenterSubViewSideBtn psmoverviewBtn = new PresenterSubViewSideBtn("PSM Overview", 4);
        psmoverviewBtn.updateIconByResource(null);
        psmoverviewBtn.setDescription("Peptide Spectrum Matches");
        psmoverviewBtn.setData("psmoverview");
        psmoverviewBtn.addStyleName("psmoverviewbtn");
        viewControlButtonContainer.addComponent(psmoverviewBtn);
        viewControlButtonContainer.setComponentAlignment(psmoverviewBtn, Alignment.MIDDLE_CENTER);
        psmoverviewBtn.addLayoutClickListener(InteractivePSPRojectResultsPresenter.this);

        peptideVisulizationLevelContainer = new PeptideVisulizationLevelContainer(Selection_Manager, psmoverviewBtn);
        Selection_Manager.addBtnLayout(psmoverviewBtn, peptideVisulizationLevelContainer);

        VerticalLayout toolViewFrame = new VerticalLayout();
        toolViewFrame.setSizeFull();
        toolViewFrame.setStyleName("viewframe");

        this.addComponent(toolViewFrame);
        this.setExpandRatio(toolViewFrame, 100);

        AbsoluteLayout toolViewFrameContent = new AbsoluteLayout();
        toolViewFrameContent.addStyleName("viewframecontent");
        toolViewFrameContent.setSizeFull();
        toolViewFrame.addComponent(toolViewFrameContent);
        toolViewFrameContent.addComponent(userUploadDataLayoutContainer);
        toolViewFrameContent.addComponent(datasetVisulizationLevelContainer);
        toolViewFrameContent.addComponent(proteinsVisulizationLevelContainer);
        toolViewFrameContent.addComponent(peptideVisulizationLevelContainer);

        LayoutEvents.LayoutClickListener notificationListener = (LayoutEvents.LayoutClickEvent event) -> {
            Component c = event.getClickedComponent();

            if (c != null && c.getId() != null && c.getId().contains("suspend")) {
                Notification.show("You need to select Project or upload your own files to visualize data", Notification.Type.WARNING_MESSAGE);
            }
        };
        viewControlButtonContainer.addLayoutClickListener(notificationListener);

        if (!mobileDeviceStyle) {
            uploadOwnDataBtn.setSelected(true);
        }

    }

    /**
     * Get the main frame layout
     *
     * @return PeptideShaker results view presenter layout
     */
    @Override
    public VerticalLayout getMainView() {
        return this;
    }

    /**
     * Get the small right side button component (represent view control button
     * in large screen mode)
     *
     * @return right view control button
     */
    @Override
    public SmallSideBtn getSmallPresenterControlButton() {
        return smallPresenterBtn;
    }

    /**
     * Get the current view ID
     *
     * @return view id
     */
    @Override
    public String getViewId() {
        return InteractivePSPRojectResultsPresenter.class.getName();
    }

    /**
     * Hide the main view for the current component.
     */
    @Override
    public void minimizeView() {
        mainPresenterBtn.setSelected(false);
        smallPresenterBtn.setSelected(false);
        this.addStyleName("hidepanel");
        this.viewControlButtonContainer.removeStyleName("visible");
        this.maximisedMode = false;

    }

    /**
     * Show the main view for the current component.
     */
    @Override
    public void maximizeView() {
        if (maximisedMode) {
            return;
        }
        UI.getCurrent().accessSynchronously(new Runnable() {
            @Override
            public void run() {
                smallPresenterBtn.setSelected(true);
                mainPresenterBtn.setSelected(true);
                datasetVisulizationLevelContainer.setMargin(new MarginInfo(false, false, false, false));
                maximisedMode = true;
                viewControlButtonContainer.addStyleName("visible");

                if (dataprocessFuture == null) {
                    datasetsOverviewBtn.setId("suspend");
                    datasetsOverviewBtn.setEnabled(false);
                    datasetsOverviewBtn.setDescription("Select project or upload your own project data");
                    datasetsOverviewBtn.addStyleName("inactive");
                    selectSubviewButton(uploadOwnDataBtn);
                    removeStyleName("hidepanel");
                    return;

                }
                datasetsOverviewBtn.setId(null);
                datasetsOverviewBtn.setEnabled(true);
                while (!dataprocessFuture.isDone()) {
                }
                removeStyleName("hidepanel");
                selectSubviewButton(datasetsOverviewBtn);
                datasetsOverviewBtn.removeStyleName("inactive");

                ScheduledExecutorService exe = Executors.newSingleThreadScheduledExecutor();
                Future f = exe.schedule(() -> {
                    UI.getCurrent().removeStyleName("busybrocess");
                    if (peptideShakerVisualizationDataset.isToShareDataset()) {
                        UI.getCurrent().push();
                    }
                }, 5, TimeUnit.SECONDS);
                exe.shutdown();
                while (!f.isDone()) {
                }

            }
        });

    }

    /**
     * Layout click method that is used to coordinate view inside the layout (in
     * case of multiple view under the same presenter).
     *
     * @param event left side button clicked action
     */
    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        PresenterSubViewSideBtn comp = (PresenterSubViewSideBtn) event.getComponent();
        Selection_Manager.selectBtn(comp);
        if (proteinsVisulizationLevelContainer != null) {
            if (comp.getBtnId() == 3 && lastSelectedBtn != 3) {
                proteinsVisulizationLevelContainer.activate3DProteinView();
            }
        }
        lastSelectedBtn = comp.getBtnId();

    }

    /**
     * Select sub visualisation for dataset presenter
     *
     * @param subPresenterBtn sub visualisation button (dataset, proteins, etc.
     */
    public void selectSubviewButton(PresenterSubViewSideBtn subPresenterBtn) {
        Selection_Manager.selectBtn(subPresenterBtn);
        if (proteinsVisulizationLevelContainer != null) {
            if (subPresenterBtn.getBtnId() == 3 && lastSelectedBtn != 3) {
                proteinsVisulizationLevelContainer.activate3DProteinView();
            }
        }
        lastSelectedBtn = subPresenterBtn.getBtnId();

    }

    /**
     * Get the left side container for left side big buttons (to be used in case
     * of large screen mode)
     *
     * @return left side buttons container
     */
    @Override
    public VerticalLayout getSubViewButtonsActionContainerLayout() {
        return viewControlButtonContainer;
    }

    /**
     * Get presenter button
     *
     * @return main presenter button
     */
    @Override
    public ButtonWithLabel getMainPresenterButton() {
        return mainPresenterBtn;
    }
    private PeptideShakerVisualizationDataset peptideShakerVisualizationDataset;

    /**
     * Activate PeptideShaker dataset visualisation upon user selection
     *
     * @param peptideShakerVisualizationDataset PeptideShaker visualisation
     * dataset
     */
    public void setSelectedDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        this.peptideShakerVisualizationDataset = peptideShakerVisualizationDataset;
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Runnable runnableTask = () -> {
            mainPresenterBtn.setEnabled(peptideShakerVisualizationDataset != null);
            smallPresenterBtn.setEnabled(peptideShakerVisualizationDataset != null);
            Selection_Manager.reset();
            Selection_Manager.selectBtn(0);
        };
        Runnable runnableTask1 = () -> {
            mainPresenterBtn.setEnabled(peptideShakerVisualizationDataset != null);
            smallPresenterBtn.setEnabled(peptideShakerVisualizationDataset != null);
            Selection_Manager.reset();
            Selection_Manager.selectBtn(0);
            datasetVisulizationLevelContainer.selectDataset(peptideShakerVisualizationDataset);
        };
        Runnable runnableTask2 = () -> {
            proteinsVisulizationLevelContainer.selectDataset(peptideShakerVisualizationDataset);
        };
        Runnable runnableTask3 = () -> {
            peptideVisulizationLevelContainer.selectDataset(peptideShakerVisualizationDataset);

        };
        dataprocessFuture = executorService.submit(runnableTask1);
        executorService.submit(runnableTask);
        executorService.submit(runnableTask2);
        executorService.submit(runnableTask3);
        executorService.shutdown();
        UI.getCurrent().addStyleName("busybrocess");
        while (!dataprocessFuture.isDone()) {

        }
        uploadOwnDataBtn.updateIconByHTMLCode(VaadinIcons.FILE_TEXT_O.getHtml() + "<div class='overlayicon'>" + VaadinIcons.ARROW_CIRCLE_UP_O.getHtml() + "</div>");
        maximisedMode = false;
        this.maximizeView();

    }

    private boolean allJobsAreDone = false;

    /**
     * Visualise dataset
     *
     * @param projectName
     * @param uploadedFileMap
     * @return array of validated files confirmation
     */
    public abstract boolean[] processVisualizationDataset(String projectName, Map<String, PluploadFile> uploadedFileMap);
}
