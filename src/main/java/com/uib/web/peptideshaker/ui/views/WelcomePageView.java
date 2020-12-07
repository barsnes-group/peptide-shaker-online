package com.uib.web.peptideshaker.ui.views;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.ui.abstracts.ViewableFrame;
import com.uib.web.peptideshaker.presenter.core.ButtonWithLabel;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.uib.web.peptideshaker.ui.views.modal.GalaxyLoginPopup;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.*;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class represents the welcome page for Online PeptideShaker
 *
 * @author Yehia Farag
 */
public class WelcomePageView extends VerticalLayout implements ViewableFrame {

    /**
     * The header layout panel.
     */
    private final HorizontalLayout mainHeaderPanel;
    /**
     * The header layout container layout.
     */
    private final HorizontalLayout headerPanelContentLayout;

    /**
     * The connecting to galaxy progress label.
     */
//    private final Label connectingLabel;
    /**
     * Galaxy login with API key button
     */
//    private final ButtonWithLabel galaxyLoginConnectionBtnLabel;
    /**
     * Test user galaxy API key.
     */
    private final String testUserLogin = "test_User_Login";

    /**
     * Connection to galaxy statues label.
     */
    private final GalaxyLoginPopup galaxyLoginBtn;
//    /**
//     * Galaxy login controls layout.
//     */
//    private final VerticalLayout galaxyLoginLayout;
//    /**
//     * User API login field.
//     */
//    private final TextField userAPIFeald;
    /**
     * The side home button .
     */
    private final SmallSideBtn viewControlButton;
    /**
     * Busy connecting window
     */
    private final Window busyConnectinWindow;
    /**
     * Container for different presenters buttons (data overview, tools ,
     * results, and galaxy connection)
     */
    private final VerticalLayout presenteControlButtonsLayout;
    /**
     * Layout for labels that have user information gathered from user accountr
     * on galaxy server
     */
    private final VerticalLayout userOverviewLayout;
    /**
     * Connection to galaxy progress window
     */
//    private final Window connectinoWindow;
    /**
     * Unique presenter id
     */
    private final String viewId = WelcomePageView.class.getName();
    /**
     * Executor service to execute connection task to galaxy server.
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    /**
     * Presenter buttons container layout container is layout container that
     * contain the small presenter control buttons.
     */
    private final AbsoluteLayout viewBtnsContainer;
    private final AppManagmentBean appManagmentBean;

    /**
     * Constructor to initialise the layout.
     *
     * @param availableGalaxy galaxy server is available
     */
    public WelcomePageView(boolean availableGalaxy) {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        WelcomePageView.this.setSizeFull();
        WelcomePageView.this.addStyleName("welcomepagestyle");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setSizeFull();
        container.setStyleName("welcomepagecontainer");
        WelcomePageView.this.addComponent(container);
        WelcomePageView.this.setComponentAlignment(container, Alignment.TOP_CENTER);

        mainHeaderPanel = new HorizontalLayout();
        mainHeaderPanel.setHeight(50, Unit.PIXELS);
        mainHeaderPanel.setWidth(100, Unit.PERCENTAGE);
        container.addComponent(mainHeaderPanel, "left:10px;top:20px;");

        headerPanelContentLayout = initializeHeaderPanel();
        mainHeaderPanel.addComponent(headerPanelContentLayout);
        mainHeaderPanel.setMargin(new MarginInfo(false, false, false, false));

        galaxyLoginBtn = new GalaxyLoginPopup();
        galaxyLoginBtn.setEnabled(availableGalaxy);

        HorizontalLayout mainMiddlePanel = new HorizontalLayout();
        mainMiddlePanel.addStyleName("welcomepagebody");
        mainMiddlePanel.setSizeFull();
        mainMiddlePanel.setSpacing(true);
        container.addComponent(mainMiddlePanel, "left:10px;top:100px;");
        /**
         * The left panel (user data overview/connect-disconnect)
         */
        VerticalLayout userOverviewPanel = new VerticalLayout();
        userOverviewPanel.setWidth(100, Unit.PERCENTAGE);
        userOverviewPanel.setHeightUndefined();
        userOverviewPanel.setSpacing(true);
        userOverviewPanel.setStyleName("useroverviewpanelstyle");
        mainMiddlePanel.addComponent(userOverviewPanel);
        mainMiddlePanel.setExpandRatio(userOverviewPanel, 20);
        /**
         * *user overview layout*
         */
        userOverviewLayout = new VerticalLayout();
        userOverviewLayout.setSizeFull();
        userOverviewPanel.addComponent(userOverviewLayout);
        userOverviewPanel.setExpandRatio(userOverviewLayout, 0.7f);
        userOverviewPanel.setComponentAlignment(userOverviewLayout, Alignment.TOP_LEFT);
        Label overviewLabel = initLeftSideInfoLabel("<b></b>", "");
        overviewLabel.addStyleName("hidetextonmobilemode");
        overviewLabel.addStyleName(ValoTheme.LABEL_H2);
        userOverviewLayout.addComponent(overviewLabel);

        Label userLabel = initLeftSideInfoLabel(VaadinIcons.USER.getHtml() + " User ", "<i class='rightsidediv'>Offline</i>");
        userLabel.addStyleName("headerlabel");
        userOverviewLayout.addComponent(userLabel);

        Label dsNumberLabel = initLeftSideInfoLabel("<img src='VAADIN/themes/webpeptideshakertheme/img/venn_icon.png' alt style='width: auto;height:15px;margin-left:-2px;    margin-right: 4px;'>" + " Projects ", "<i>Not Available</i>");
        userOverviewLayout.addComponent(dsNumberLabel);

        Label filesNumberLabel = initLeftSideInfoLabel(VaadinIcons.FILE_TEXT_O.getHtml() + " Files ", "<i>Not Available</i>");
        userOverviewLayout.addComponent(filesNumberLabel);

        Label usedMemory = initLeftSideInfoLabel(VaadinIcons.CLOUD_O.getHtml() + " Storage ", "<i>Not Available</i>");
        userOverviewLayout.addComponent(usedMemory);

        Label searchGUI = initLeftSideInfoLabel("<img src='VAADIN/themes/webpeptideshakertheme/img/sgiconHRNSgray21.png' alt style='width: auto;height:15px;margin-left:-2px;    margin-right: 4px;'>" + " SearchGUI ", "<i class='nrightsidediv'>" + VaadinSession.getCurrent().getAttribute("searchGUIversion") + "</i>");
        searchGUI.setValue(searchGUI.getValue().replace("<div style='white-space: nowrap;width: 65px;height: 20px;", "<div class='psversstyle' style='white-space: nowrap;width: 65px;height: 20px;"));
        userOverviewLayout.addComponent(searchGUI);

        Label peptideShaker = initLeftSideInfoLabel("<img src='VAADIN/themes/webpeptideshakertheme/img/psiconHRNSgray21.png' alt style='width: auto;height:15px;margin-left:-2px;    margin-right: 4px;'>" + " PeptideShaker ", "<i class='nrightsidediv'>" + VaadinSession.getCurrent().getAttribute("psVersion") + "</i>");
        peptideShaker.setValue(peptideShaker.getValue().replace("<div style='white-space: nowrap;width: 65px;height: 20px;", "<div class='psversstyle' style='white-space: nowrap;width: 65px;height: 20px;"));
        userOverviewLayout.addComponent(peptideShaker);

        Label moff = initLeftSideInfoLabel("<img src='VAADIN/themes/webpeptideshakertheme/img/mofficon21.png' alt style='width: auto;height:15px;margin-left:-2px;    margin-right: 4px;'>" + " Moff ", "<i class='nrightsidediv'>" + VaadinSession.getCurrent().getAttribute("moffvirsion") + "</i>");
        moff.setValue(moff.getValue().replace("<div style='white-space: nowrap;width: 65px;height: 20px;", "<div class='psversstyle' style='white-space: nowrap;width: 65px;height: 20px;"));
        userOverviewLayout.addComponent(moff);

        /**
         * The right panel (welcome message / presenter control buttons)
         */
        VerticalLayout presenterControlButtonsPanel = new VerticalLayout();
        presenterControlButtonsPanel.setWidth(100, Unit.PERCENTAGE);
        presenterControlButtonsPanel.setHeight(360, Unit.PIXELS);
        presenterControlButtonsPanel.setSpacing(true);
        mainMiddlePanel.addComponent(presenterControlButtonsPanel);
        mainMiddlePanel.setExpandRatio(presenterControlButtonsPanel, 80);
        presenterControlButtonsPanel.setMargin(new MarginInfo(false, false, false, true));
        presenterControlButtonsPanel.addStyleName("actionbuttoncontainer");

        VerticalLayout welcomeTextContainerLayout = new VerticalLayout();
        welcomeTextContainerLayout.setSizeFull();
        welcomeTextContainerLayout.setData("ignoreclick");
        welcomeTextContainerLayout.addStyleName("mainbodystyle");
        welcomeTextContainerLayout.addStyleName("connectionpanelstyle");
        welcomeTextContainerLayout.setSpacing(true);
        welcomeTextContainerLayout.setWidth(100, Unit.PERCENTAGE);
        welcomeTextContainerLayout.setHeight(100, Unit.PIXELS);
        presenterControlButtonsPanel.addComponent(welcomeTextContainerLayout);
        presenterControlButtonsPanel.setComponentAlignment(welcomeTextContainerLayout, Alignment.TOP_LEFT);
        presenterControlButtonsPanel.setExpandRatio(welcomeTextContainerLayout, 0.4f);

        Label welcomeText = new Label();
        welcomeText.setSizeFull();
        welcomeText.setContentMode(ContentMode.HTML);
        welcomeText.setStyleName(ValoTheme.LABEL_NO_MARGIN);
        welcomeText.addStyleName("hideonmobilemode");
        welcomeText.setValue("<font style='font-weight: bold; font-size:23px'>Welcome to PeptideShaker </font>");
        welcomeText.setData("ignoreclick");
        welcomeTextContainerLayout.addComponent(welcomeText);
        welcomeTextContainerLayout.setExpandRatio(welcomeText, 0.05f);
        welcomeTextContainerLayout.setComponentAlignment(welcomeText, Alignment.TOP_LEFT);

        final Label subWelcomeText = new Label();
        subWelcomeText.setSizeFull();
        subWelcomeText.setData("ignoreclick");
        subWelcomeText.setContentMode(ContentMode.HTML);
        subWelcomeText.setStyleName(ValoTheme.LABEL_NO_MARGIN);
        subWelcomeText.setValue("<font>Interactive visual analysis of proteomics data on the web!</font>");

        welcomeTextContainerLayout.addComponent(subWelcomeText);
        welcomeTextContainerLayout.setExpandRatio(subWelcomeText, 0.05f);
        welcomeTextContainerLayout.setComponentAlignment(subWelcomeText, Alignment.TOP_LEFT);
        /**
         * *Presenter control buttons container layout
         */
        presenteControlButtonsLayout = new VerticalLayout();
        presenteControlButtonsLayout.setHeight(180, Unit.PIXELS);
        presenteControlButtonsLayout.setWidth(100, Unit.PERCENTAGE);
        presenteControlButtonsLayout.setSpacing(true);
        presenterControlButtonsPanel.addComponent(presenteControlButtonsLayout);
        presenterControlButtonsPanel.setExpandRatio(presenteControlButtonsLayout, 0.6f);
        presenteControlButtonsLayout.setEnabled(true);
        presenteControlButtonsLayout.addStyleName("disableasenable");

        presenterControlButtonsPanel.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            AbstractComponent comp = (AbstractComponent) event.getClickedComponent();
            if (presenteControlButtonsLayout.isEnabled() || comp == null || (comp.getData() != null && comp.getData().toString().equalsIgnoreCase("ignoreclick"))) {
                return;
            }

//            if (galaxyLoginBtn.getData() == null) {
//                connectinoWindow.setVisible(true);
//            }
        });
//        galaxyLoginBtn.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
//            if (galaxyLoginBtn.getData() == null) {
////                connectinoWindow.removeStyleName("connectionwindow");
////                connectinoWindow.setStyleName("windowcontainer");
////                connectinoWindow.setVisible(true);
//            } else {
//                Notification.show("error in connection..", Notification.Type.ERROR_MESSAGE);
//                VaadinSession.getCurrent().getSession().setMaxInactiveInterval(10);
//                Page.getCurrent().reload();
//            }
//        });

//       
        viewControlButton = new SmallSideBtn(VaadinIcons.HOME_O);
        viewControlButton.addStyleName("homepagepresenterbtn");
        viewControlButton.setData(WelcomePageView.this.getViewId());

        this.viewControlButton.setDescription("Home page");

        VerticalLayout mainBottomPanel = new VerticalLayout();
        mainBottomPanel.setStyleName("bluelayout");
        mainBottomPanel.setHeight(65, Unit.PIXELS);
        mainBottomPanel.setWidth(100, Unit.PERCENTAGE);
        container.addComponent(mainBottomPanel, "left:0px;bottom:10px");

        HorizontalLayout sponserContainer = new HorizontalLayout();
        sponserContainer.setHeight(100, Unit.PERCENTAGE);
        sponserContainer.setSpacing(true);
        sponserContainer.setWidthUndefined();
        mainBottomPanel.addComponent(sponserContainer);
        mainBottomPanel.setComponentAlignment(sponserContainer, Alignment.TOP_CENTER);
        sponserContainer.addStyleName("hidetextonmobilemode");

        Label developmentText = new Label("<font>The web version of PeptideShaker is being developed by <a href='http://www.cbu.uib.no/barsnes/' target='_blank'>Barsnes Group</a> at the Computational Biology Unit (CBU) at the University of Bergen, Norway, in close collaboration with the Proteomics Unit at the University of Bergen (PROBE), Bergen, Norway.</font>", ContentMode.HTML);
        developmentText.setStyleName("refrencetext");
        sponserContainer.addComponent(developmentText);

        Link probeLink = new Link("<img src='VAADIN/themes/webpeptideshakertheme/img/probe-updated.png' alt style='height: 65px;margin-top: -20px;cursor:pointer !important;'>", new ExternalResource("https://www.uib.no/rg/probe"));
        probeLink.setCaptionAsHtml(true);
        probeLink.setHeight(100, Unit.PERCENTAGE);
        probeLink.setWidth(180, Unit.PIXELS);
        probeLink.setTargetName("_blank");
        sponserContainer.addComponent(probeLink);
        Link uibLink = new Link("<img src='VAADIN/themes/webpeptideshakertheme/img/uib-logo.svg' alt style='height: 85px;margin-top: -30px;margin-left: -20px;cursor:pointer !important;'>", new ExternalResource("https://www.uib.no/"));
        uibLink.setCaptionAsHtml(true);
        uibLink.setHeight(100, Unit.PERCENTAGE);
        uibLink.setWidth(65, Unit.PIXELS);
        uibLink.setTargetName("_blank");
        sponserContainer.addComponent(uibLink);
        Link cbuLink = new Link("<img src='VAADIN/themes/webpeptideshakertheme/img/cbu_logo_lightBlue_transparent.png' alt style='height: 55px;margin-top: -10px;cursor:pointer !important;'>", new ExternalResource("http://www.cbu.uib.no/"));
        cbuLink.setCaptionAsHtml(true);
        cbuLink.setHeight(100, Unit.PERCENTAGE);
        cbuLink.setWidth(112, Unit.PIXELS);
        cbuLink.setTargetName("_blank");
        sponserContainer.addComponent(cbuLink);

        VerticalLayout windowContent = new VerticalLayout();
        windowContent.setWidth(500, Unit.PIXELS);
        this.busyConnectinWindow = new Window(null, windowContent);
        this.busyConnectinWindow.setSizeFull();
        this.busyConnectinWindow.setStyleName("busyconnectingwindow");
        this.busyConnectinWindow.setModal(false);
        this.busyConnectinWindow.setDraggable(false);
        this.busyConnectinWindow.setClosable(false);
        this.busyConnectinWindow.setResizable(false);
        busyConnectinWindow.center();
        this.busyConnectinWindow.setWindowMode(WindowMode.NORMAL);
        busyConnectinWindow.addStyleName("hidewindow");
        if ((boolean) VaadinSession.getCurrent().getAttribute("mobilescreenstyle")) {
            userOverviewPanel.setIcon(VaadinIcons.ANGLE_RIGHT);
            userOverviewPanel.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
                if (userOverviewPanel.getStyleName().contains("hidecontent")) {
                    userOverviewPanel.removeStyleName("hidecontent");
                } else {
                    userOverviewPanel.addStyleName("hidecontent");
                }
            });
            userOverviewPanel.addStyleName("hidecontent");

        }
        this.viewBtnsContainer = new AbsoluteLayout();
        viewBtnsContainer.setSizeFull();
        viewBtnsContainer.addComponent(galaxyLoginBtn, "left:50%;top:50%;");

        /**
         * The workflow view button.
         */
        ButtonWithLabel workflowViewBtn = new ButtonWithLabel("Analyze Data</br><font>Search and process data</font>", 1);//spectra2.png
        workflowViewBtn.setData(SearchGUIPSWorkflowView.class.getName());
        workflowViewBtn.updateIconResource(new ThemeResource("img/searchguiblue.png"));//img/workflow3.png
        workflowViewBtn.addStyleName("searchguiicon");
        viewBtnsContainer.addComponent(workflowViewBtn, "left:0%;top:0%;");
        /**
         * The File System view button.
         */
        ButtonWithLabel fileSystemViewButton = new ButtonWithLabel("Projects Overview</br><font>Available projects and data files</font>", 1);
        fileSystemViewButton.setData(FileSystemView.class.getName());
        fileSystemViewButton.setDescription("Projects and available data files");
        fileSystemViewButton.updateIconResource(new ThemeResource("img/globeearthanimation.png"));
        fileSystemViewButton.addStyleName("glubimg");
        viewBtnsContainer.addComponent(fileSystemViewButton, "left:0%;top:50%;");
        /**
         * The Results view button.
         */
        ButtonWithLabel resultsViewBtn = new ButtonWithLabel("Visualize Data</br><font>Visualize/Upload project</font>", 1);
        resultsViewBtn.updateIcon(VaadinIcons.CLUSTER.getHtml());
        resultsViewBtn.updateIconResource(new ThemeResource("img/venn_color.png"));
        resultsViewBtn.setDescription("Visualize selected projects / Upload your own project files");
        resultsViewBtn.setEnabled(true);
        resultsViewBtn.addStyleName("orangeiconcolor");
        resultsViewBtn.addStyleName("resultsbtn");
        viewBtnsContainer.addComponent(resultsViewBtn, "left:50%;top:0%;");
        presenteControlButtonsLayout.addComponent(viewBtnsContainer);
        if (availableGalaxy) {
            galaxyLoginBtn.setDescription("Login to Galaxy - API key required");
            updateUserOverviewPanel();
        } else {
            galaxyLoginBtn.setDescription("Galaxy server is not available");
        }
    }

    /**
     * Log in to galaxy as guest (test user API key).
     */
    public void loginAsGuest() {
//        String caption = "<b style=\"color:#cd6e1d !important\">Guest User <i>(public data)</i></b>";
//
//        galaxyLoginConnectionBtnLabel.setVisible(false);
//        connectinoWindow.setVisible(true);
//        galaxyLoginLayout.setVisible(false);
//        connectinoWindow.setClosable(false);
//        connectingLabel.setCaption(caption);
//        connectingLabel.setVisible(true);
//        Runnable task = () -> {
//            connectinoWindow.removeStyleName("windowcontainer");
//            connectinoWindow.setStyleName("connectionwindow");
//            List<String> userOverviewData = connectToGalaxy(testUserLogin, viewId);
//            updateConnectionStatusToGalaxy(userOverviewData);
//        };
//        if (executorService.isShutdown()) {
//            executorService = Executors.newSingleThreadExecutor();
//        }
//        executorService.submit(task);
//        executorService.shutdown();
    }

    public void retriveToShareDataset() {
//        String caption = "<b style=\"color:#cd6e1d !important\">Retrieving Dataset Information</i></b>";
//        galaxyLoginConnectionBtnLabel.setVisible(false);
//        connectinoWindow.setVisible(true);
//        galaxyLoginLayout.setVisible(false);
//        connectinoWindow.setClosable(false);
//        connectingLabel.setCaption(caption);
//        connectingLabel.setVisible(true);
//        Runnable task = () -> {
//            connectinoWindow.removeStyleName("windowcontainer");
//            connectinoWindow.setStyleName("connectionwindow");
//            viewToShareDataset();
//
//        };
//        if (executorService.isShutdown()) {
//            executorService = Executors.newSingleThreadExecutor();
//        }
//        executorService.submit(task);
//        ScheduledExecutorService scd = Executors.newSingleThreadScheduledExecutor();
//        scd.schedule(() -> {
//            connectinoWindow.setVisible(false);
//        }, 5, TimeUnit.SECONDS);
//        scd.shutdown();
//
//        executorService.shutdown();

    }

    /**
     * update user overview panel.
     *
     * @param userOverviewData list of user data gathered from user account on
     * Galaxy server
     */
    private void updateUserOverviewPanel() {
        Map<String, String> userOverviewData = appManagmentBean.getUserLoginHandler().getUserInformation(); //List<String> userOverviewData
        Label user_label = initLeftSideInfoLabel(VaadinIcons.USER.getHtml() + " <b class='rightsidediv' style='color:#cd6e1d !important'>" + userOverviewData.get(CONSTANT.USERNAME) + "</b>", "");
        user_label.addStyleName("headerlabel");
        userOverviewLayout.replaceComponent(userOverviewLayout.getComponent(1), user_label);
        Label projects_label = (Label) userOverviewLayout.getComponent(2);
        updateLeftSideInfoLabel(projects_label, userOverviewData.get(CONSTANT.PS_DATASET_NUMBER));
        Label files_label = (Label) userOverviewLayout.getComponent(3);
        updateLeftSideInfoLabel(files_label, userOverviewData.get(CONSTANT.FILES_NUMBER));
        Label storage_label = (Label) userOverviewLayout.getComponent(4);
        updateLeftSideInfoLabel(storage_label, userOverviewData.get(CONSTANT.STORAGE));
    }

    /**
     * Initialise the header layout.
     */
    private HorizontalLayout initializeHeaderPanel() {

        HorizontalLayout headerLayoutContainer = new HorizontalLayout();
        headerLayoutContainer.setSpacing(true);
        headerLayoutContainer.addStyleName("logocontainer");
        Image peptideShakerLogoIcon = new Image();
        peptideShakerLogoIcon.setSource(new ThemeResource("img/peptideshakericon.png"));
        peptideShakerLogoIcon.setHeight(100, Unit.PIXELS);
        headerLayoutContainer.addComponent(peptideShakerLogoIcon);
        headerLayoutContainer.setComponentAlignment(peptideShakerLogoIcon, Alignment.MIDDLE_LEFT);

        Link headerLogoLabel = new Link("PeptideShaker", new ExternalResource(""));
        headerLayoutContainer.addComponent(headerLogoLabel);
        headerLogoLabel.setCaptionAsHtml(true);
        headerLayoutContainer.setComponentAlignment(headerLogoLabel, Alignment.MIDDLE_LEFT);
        headerLogoLabel.setStyleName("headerlogo");
        headerLayoutContainer.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            Page.getCurrent().open("", "", false);
        });
        return headerLayoutContainer;
    }

    /**
     * Get the unique presenter ID for the welcome page
     *
     * @return presenter ID
     */
    @Override
    public String getViewId() {
        return viewId;
    }

    /**
     * Hide current presenter
     */
    @Override
    public void minimizeView() {
        viewControlButton.setSelected(false);
        this.addStyleName("hidepanel");
    }

    /**
     * View presenter
     */
    @Override
    public void maximizeView() {
        viewControlButton.setSelected(true);
        this.removeStyleName("hidepanel");
    }

    /**
     * Get presenter side menu button
     *
     * @return small side button for the presenter
     */
    @Override
    public SmallSideBtn getSmallPresenterControlButton() {
        return viewControlButton;
    }

    /**
     * Get the main frame that contains the presenter view
     *
     * @return
     */
    @Override
    public VerticalLayout getMainView() {
        return this;
    }

    /**
     * Get the left side (sub presenter action buttons) in welcome page the side
     * view layout is not exist
     *
     * @return empty layout
     */
    @Override
    public VerticalLayout getSubViewButtonsActionContainerLayout() {
        return new VerticalLayout();
    }

//    /**
//     * Connect to galaxy server.
//     *
//     * @param presenterId button used to login
//     * @param userAPI     user API key that is required to connect to galaxy
//     * @return list of overview data for the user / null indicate failed to
//     * connect to Galaxy Server
//     */
//    public abstract List<String> connectToGalaxy(String userAPI, String presenterId);
//
//    /**
//     * View dataset that is shared by link.
//     */
//    public abstract void viewToShareDataset();
    /**
     * Create unified labels for the user overview panel
     *
     * @param title the label text title
     * @param value the label text value
     * @return the generated label
     */
    private Label initLeftSideInfoLabel(String title, String value) {
        Label label = new Label("<div class='lefttitle' style='width:120px;font-size: 14px;'>" + title + "</div><div style='white-space: nowrap;width: 65px;height: 20px;font-size: 12px;color:#cd6e1d;position: relative;top: -20px;left: 120px;text-overflow: ellipsis;overflow: hidden;'>  " + value + " </div>");
        label.setContentMode(ContentMode.HTML);
        label.setHeight(55, Unit.PIXELS);
        label.setWidth(180, Unit.PIXELS);
        label.setStyleName(ValoTheme.LABEL_SMALL);
        label.addStyleName("leftsidelabel");
        return label;

    }

    /**
     * Update the generated labels for the user overview panel
     *
     * @param label the generated label to be updated
     * @param value the new value for the label
     */
    private void updateLeftSideInfoLabel(Label label, String value) {
        String org = label.getValue().split("</div>")[0];
        label.setValue(org + "</div><div class='nrightsidediv' style='white-space: nowrap;width: 65px;height: 20px;font-size: 12px;color:#cd6e1d;position: relative;top: -20px;left: 120px;text-overflow: ellipsis;overflow: hidden;'>  " + value + " </div>");

    }

    /**
     * Get the presenter button (large button with label) for the welcome page
     * (not exist for welcome page presenter)
     *
     * @return null no large button for welcome page presenter
     */
    @Override
    public ButtonWithLabel getMainPresenterButton() {
        return null;
    }

}
