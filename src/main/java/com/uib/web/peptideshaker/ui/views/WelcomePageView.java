package com.uib.web.peptideshaker.ui.views;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.ui.interfaces.ViewableFrame;
import com.uib.web.peptideshaker.ui.components.items.ButtonWithLabel;
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

/**
 * This class represents the welcome page for Online PeptideShaker
 *
 * @author Yehia Mokhtar Farag
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
     * Connection to galaxy statues label.
     */
    private final GalaxyLoginPopup galaxyLoginBtn;
    /**
     * Busy connecting window.
     */
    private final Window busyConnectinWindow;
    /**
     * Container for different presenters buttons (data overview, tools ,
     * results, and galaxy connection).
     */
    private final VerticalLayout viewsControlButtonsLayout;
    /**
     * Layout for labels that have user information gathered from user account
     * on galaxy server.
     */
    private final VerticalLayout userOverviewLayout;
    /**
     * Presenter buttons container layout container is layout container that
     * contain the small presenter control buttons.
     */
    private final AbsoluteLayout viewBtnsContainer;
    private final AppManagmentBean appManagmentBean;

    /**
     * Constructor to initialise the layout.
     *
     */
    public WelcomePageView() {
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
        galaxyLoginBtn.setEnabled(appManagmentBean.isAvailableGalaxy());

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
        overviewLabel.setHeight(10, Unit.PIXELS);
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

        Label searchGUI = initLeftSideInfoLabel("<img src='VAADIN/themes/webpeptideshakertheme/img/sgiconHRNSgray21.png' alt style='width: auto;height:15px;margin-left:-2px;    margin-right: 4px;'>" + " SearchGUI ", "<i class='nrightsidediv'>" + CONSTANT.SEARCHGUI_TOOL_VERSION.replace("+galaxy0", "") + "</i>");
        searchGUI.setValue(searchGUI.getValue().replace("<div style='white-space: nowrap;width: 65px;height: 20px;", "<div class='psversstyle' style='white-space: nowrap;width: 65px;height: 20px;"));
        userOverviewLayout.addComponent(searchGUI);

        Label peptideShaker = initLeftSideInfoLabel("<img src='VAADIN/themes/webpeptideshakertheme/img/psiconHRNSgray21.png' alt style='width: auto;height:15px;margin-left:-2px;    margin-right: 4px;'>" + " PeptideShaker ", "<i class='nrightsidediv'>" + CONSTANT.PEPTIDESHAKER_TOOL_VERSION.replace("+galaxy0", "")+ "</i>");
        peptideShaker.setValue(peptideShaker.getValue().replace("<div style='white-space: nowrap;width: 65px;height: 20px;", "<div class='psversstyle' style='white-space: nowrap;width: 65px;height: 20px;"));
        userOverviewLayout.addComponent(peptideShaker);

        Label moff = initLeftSideInfoLabel("<img src='VAADIN/themes/webpeptideshakertheme/img/mofficon21.png' alt style='width: auto;height:15px;margin-left:-2px;    margin-right: 4px;'>" + " moff ", "<i class='nrightsidediv'>" + CONSTANT.MOFF_TOOL_Version + "</i>");
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
        welcomeText.setValue("<font style='font-weight: bold; font-size:23px'>Welcome to PeptideShaker Online </font>");
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
        viewsControlButtonsLayout = new VerticalLayout();
        viewsControlButtonsLayout.setHeight(180, Unit.PIXELS);
        viewsControlButtonsLayout.setWidth(100, Unit.PERCENTAGE);
        viewsControlButtonsLayout.setSpacing(true);
        presenterControlButtonsPanel.addComponent(viewsControlButtonsLayout);
        presenterControlButtonsPanel.setExpandRatio(viewsControlButtonsLayout, 0.6f);
        viewsControlButtonsLayout.setEnabled(true);
        viewsControlButtonsLayout.addStyleName("disableasenable");

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
        if (appManagmentBean.getAppConfig().isMobileDeviceStyle()) {
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

        LayoutEvents.LayoutClickListener listener = (LayoutEvents.LayoutClickEvent event) -> {
            String viewId = ((ButtonWithLabel) event.getComponent()).getData() + "";
            appManagmentBean.getUI_Manager().viewLayout(viewId);
        };
        /**
         * The workflow view button.
         */
        ButtonWithLabel workflowViewButton = new ButtonWithLabel("Analyze Data</br><font>Search and process data</font>", 1);//spectra2.png
        workflowViewButton.setData(WorkflowInvokingView.class.getName());
        workflowViewButton.updateIconResource(new ThemeResource("img/searchguiblue.png"));//img/workflow3.png
        workflowViewButton.addStyleName("searchguiicon");
        workflowViewButton.setEnabled(appManagmentBean.isAvailableGalaxy());
        workflowViewButton.addLayoutClickListener(listener);
        viewBtnsContainer.addComponent(workflowViewButton, "left:0%;top:0%;");
        /**
         * The File System view button.
         */
        ButtonWithLabel fileSystemViewButton = new ButtonWithLabel("Projects Overview</br><font>Available projects and data files</font>", 1);
        fileSystemViewButton.setData(FileSystemView.class.getName());
        fileSystemViewButton.setDescription("Projects and available data files");
        fileSystemViewButton.updateIconResource(new ThemeResource("img/globeearthanimation.png"));
        fileSystemViewButton.addStyleName("glubimg");
        fileSystemViewButton.addLayoutClickListener(listener);
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
        resultsViewBtn.setData(ResultsView.class.getName());
        viewBtnsContainer.addComponent(resultsViewBtn, "left:50%;top:0%;");
        resultsViewBtn.addLayoutClickListener(listener);
        viewsControlButtonsLayout.addComponent(viewBtnsContainer);
        if (appManagmentBean.isAvailableGalaxy()) {
            galaxyLoginBtn.setDescription("Login to Galaxy - API key required");
        } else {
            galaxyLoginBtn.setDescription("Galaxy server is not available");
        }
        WelcomePageView.this.minimizeView();
    }

    /**
     * update user overview panel.
     *
     * @param userOverviewData list of user data gathered from user account on
     * Galaxy server
     */
    private void updateUserOverviewPanel() {
        Map<String, String> userOverviewData = appManagmentBean.getUserHandler().getUserInformation(); //List<String> userOverviewData
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

        Link headerLogoLabel = new Link("PeptideShaker <i>Online</i>", new ExternalResource(""));
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
        return WelcomePageView.class.getName();
    }

    /**
     * Hide current presenter
     */
    @Override
    public void minimizeView() {
        this.addStyleName("hidepanel");
    }

    /**
     * View presenter
     */
    @Override
    public void maximizeView() {
        this.removeStyleName("hidepanel");
    }

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
        label.setHeight(40, Unit.PIXELS);
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

    @Override
    public void update() {
        this.updateUserOverviewPanel();
    }

}
