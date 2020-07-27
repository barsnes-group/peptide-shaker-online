package com.uib.web.peptideshaker.presenter;

import com.uib.web.peptideshaker.presenter.core.ButtonWithLabel;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class represents the welcome page for Online PeptideShaker
 *
 * @author Yehia Farag
 */
public abstract class WelcomePagePresenter extends VerticalLayout implements ViewableFrame {

    /**
     * The header layout panel.
     */
    private final HorizontalLayout mainHeaderPanel;
    /**
     * The header layout container layout.
     */
    private final HorizontalLayout headerPanelContentLayout;
    /**
     * The body layout panel.
     */
    private final VerticalLayout userConnectionPanel;
    /**
     * The connecting to galaxy progress label.
     */
    private final Label connectingLabel;
    /**
     * Galaxy login with API key button
     */
    private final ButtonWithLabel galaxyLoginConnectionBtnLabel;
    /**
     * Test user galaxy API key.
     */
    private final String testUserLogin = "test_User_Login";
    /**
     * Not valid API error message .
     */
    private final String apiErrorMessage = "Wrong API please try again";
    /**
     * Connection to galaxy statues label.
     */
    private final ButtonWithLabel galaxyLoginBtn;
    /**
     * Galaxy login controls layout.
     */
    private final VerticalLayout galaxyLoginLayout;
    /**
     * User API login field.
     */
    private final TextField userAPIFeald;
    /**
     * The side home button .
     */
    private final SmallSideBtn viewControlButton;
    /**
     * Busy connecting window
     */
    private final Window busyConnectinWindow;
    /**
     * Executor service to execute connection task to galaxy server.
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
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
    private final Window connectinoWindow;
    /**
     * Unique presenter id
     */
    private final String viewId = WelcomePagePresenter.class.getName();

    /**
     * Constructor to initialise the layout.
     *
     * @param availableGalaxy galaxy server is available
     */
    public WelcomePagePresenter(boolean availableGalaxy) {

        WelcomePagePresenter.this.setSizeFull();
        WelcomePagePresenter.this.addStyleName("welcomepagestyle");

        AbsoluteLayout container = new AbsoluteLayout();
        container.setSizeFull();
        container.setStyleName("welcomepagecontainer");
//             final FullScreenButton button = new FullScreenButton("All (on)");
//          button.addStyleName("fullscreenbtn");
//         container.addComponent(button);

        WelcomePagePresenter.this.addComponent(container);
        WelcomePagePresenter.this.setComponentAlignment(container, Alignment.TOP_CENTER);

        mainHeaderPanel = new HorizontalLayout();
        mainHeaderPanel.setHeight(50, Unit.PIXELS);
        mainHeaderPanel.setWidth(100, Unit.PERCENTAGE);
        container.addComponent(mainHeaderPanel, "left:10px;top:20px;");

        headerPanelContentLayout = initializeHeaderPanel();
        mainHeaderPanel.addComponent(headerPanelContentLayout);
        mainHeaderPanel.setMargin(new MarginInfo(false, false, false, false));

        galaxyLoginBtn = new ButtonWithLabel("Galaxy Login<br/><font>Login using API key</font>", 0);
        galaxyLoginBtn.updateIconResource(new ThemeResource("img/galaxylogocolor.png"));
        galaxyLoginBtn.addStyleName("galaxylabel");
        if (availableGalaxy) {
            galaxyLoginBtn.setDescription("Login to Galaxy - API key required");
        } else {
            galaxyLoginBtn.setDescription("Galaxy server is not available");
            VaadinSession.getCurrent().setAttribute("psVersion", "offline");
            VaadinSession.getCurrent().setAttribute("searchGUIversion", "offline");
        }
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
        /**
         * Pop-up window layout to connect to Galaxy Server.
         */
        userConnectionPanel = new VerticalLayout();
        userConnectionPanel.setWidth(500, Unit.PIXELS);
        userConnectionPanel.setHeightUndefined();
        userConnectionPanel.setMargin(new MarginInfo(true, true, true, true));
        userConnectionPanel.setSpacing(true);
        connectingLabel = new Label("<h1 class='animation'>Connecting to galaxy, Please wait...</h1>");
        connectingLabel.setVisible(false);
        connectingLabel.setCaptionAsHtml(true);
        connectingLabel.setContentMode(ContentMode.HTML);
        connectingLabel.setHeight(25, Sizeable.Unit.PIXELS);
        connectingLabel.setWidth(200, Sizeable.Unit.PIXELS);
        connectingLabel.setStyleName(ValoTheme.LABEL_SMALL);
        connectingLabel.addStyleName(ValoTheme.LABEL_BOLD);
        connectingLabel.addStyleName(ValoTheme.LABEL_TINY);
        connectingLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);

        userConnectionPanel.addComponent(connectingLabel);
        userConnectionPanel.setComponentAlignment(connectingLabel, Alignment.MIDDLE_CENTER);

        connectinoWindow = new Window(null, userConnectionPanel) {
            @Override
            public void close() {
                this.setVisible(false);
            }

            @Override
            public void setVisible(boolean visible) {
                super.setVisible(visible);
                if (visible) {
                    this.center();
                }
            }

        };
        connectinoWindow.setModal(true);
        connectinoWindow.setDraggable(false);
        connectinoWindow.setClosable(true);

        connectinoWindow.setResizable(false);
        connectinoWindow.setStyleName("connectionwindow");
        connectinoWindow.center();
        connectinoWindow.setWindowMode(WindowMode.NORMAL);
        if (!UI.getCurrent().getWindows().contains(connectinoWindow)) {
            UI.getCurrent().addWindow(connectinoWindow);
        }
        connectinoWindow.setVisible(false);

        presenterControlButtonsPanel.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            AbstractComponent comp = (AbstractComponent) event.getClickedComponent();
            if (presenteControlButtonsLayout.isEnabled() || comp == null || (comp.getData() != null && comp.getData().toString().equalsIgnoreCase("ignoreclick"))) {
                return;
            }

            if (galaxyLoginBtn.getData() == null) {
                connectinoWindow.setVisible(true);
            }
        });
        galaxyLoginBtn.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            if (galaxyLoginBtn.getData() == null) {
                connectinoWindow.removeStyleName("connectionwindow");
                connectinoWindow.setStyleName("windowcontainer");
                connectinoWindow.setVisible(true);
            } else {
                Notification.show("error in connection..", Notification.Type.ERROR_MESSAGE);
                VaadinSession.getCurrent().getSession().setMaxInactiveInterval(10);
                Page.getCurrent().reload();
            }
        });

        HorizontalLayout serviceButtonContainer = new HorizontalLayout();
        serviceButtonContainer.setWidth(100, Unit.PERCENTAGE);
        serviceButtonContainer.setHeight(40, Unit.PIXELS);
        serviceButtonContainer.setMargin(false);
        serviceButtonContainer.setSpacing(true);
        userConnectionPanel.addComponent(serviceButtonContainer);
        userConnectionPanel.setExpandRatio(serviceButtonContainer, 0.1f);
        userConnectionPanel.setComponentAlignment(serviceButtonContainer, Alignment.TOP_LEFT);

        galaxyLoginConnectionBtnLabel = new ButtonWithLabel("<font style='width: 100%;height: 100%;font-size: 14px;font-weight: 600;text-align: justify;line-height: 70px;'>Login to Galaxy Server using your API Key</font>", 1);
        galaxyLoginConnectionBtnLabel.updateIconResource(new ThemeResource("img/galaxylogocolor.png"));

        galaxyLoginConnectionBtnLabel.addStyleName("smaller");
        serviceButtonContainer.addComponent(galaxyLoginConnectionBtnLabel);

        galaxyLoginLayout = new VerticalLayout();
        galaxyLoginLayout.setSizeFull();
        galaxyLoginLayout.setSpacing(true);
        galaxyLoginLayout.setVisible(true);
        galaxyLoginLayout.setMargin(false);
        userConnectionPanel.addComponent(galaxyLoginLayout);
        userConnectionPanel.setExpandRatio(galaxyLoginLayout, 0.1f);
        userConnectionPanel.setComponentAlignment(galaxyLoginLayout, Alignment.TOP_LEFT);

        userAPIFeald = new TextField("User API");
        userAPIFeald.addFocusListener((FieldEvents.FocusEvent event) -> {
            if (userAPIFeald.getValue().equals(apiErrorMessage)) {
                userAPIFeald.clear();
                userAPIFeald.removeStyleName("redfont");
            }
        });
        userAPIFeald.setSizeFull();
        userAPIFeald.setStyleName(ValoTheme.TEXTFIELD_TINY);
        userAPIFeald.setRequired(true);
        userAPIFeald.setRequiredError("API key is required");
        userAPIFeald.setInputPrompt("Enter Galaxy API Key");
        galaxyLoginLayout.addComponent(userAPIFeald);

        HorizontalLayout galaxyServiceBtns = new HorizontalLayout();
        galaxyServiceBtns.setSizeFull();
        galaxyServiceBtns.setSpacing(true);
        galaxyLoginLayout.addComponent(galaxyServiceBtns);
        Link regLink = new Link("Register", new ExternalResource(VaadinSession.getCurrent().getAttribute("galaxyServerUrl") + "login"));
        regLink.setStyleName("newlink");
        regLink.setTargetName("_blank");
        galaxyServiceBtns.addComponent(regLink);
        galaxyServiceBtns.setExpandRatio(regLink, 0.1f);

        Link userAPI = new Link("Get User API", new ExternalResource(VaadinSession.getCurrent().getAttribute("galaxyServerUrl") + "user/api_key"));
        userAPI.setStyleName("newlink");
        userAPI.setTargetName("_blank");
        galaxyServiceBtns.addComponent(userAPI);
        galaxyServiceBtns.setExpandRatio(userAPI, 0.4f);

        Button loginButton = new Button("Login");
        loginButton.setStyleName(ValoTheme.BUTTON_TINY);
        galaxyServiceBtns.addComponent(loginButton);
        galaxyServiceBtns.setExpandRatio(loginButton, 0.4f);
        galaxyServiceBtns.setComponentAlignment(loginButton, Alignment.TOP_RIGHT);

        loginButton.addClickListener((Button.ClickEvent event) -> {
            userAPIFeald.commit();
            if (userAPIFeald.isValid() && !userAPIFeald.getValue().equalsIgnoreCase(testUserLogin)) {
                connectingLabel.setVisible(true);
                connectinoWindow.setClosable(false);
                galaxyLoginConnectionBtnLabel.setVisible(false);
                galaxyLoginLayout.setVisible(false);
                Runnable task = () -> {
                    connectinoWindow.removeStyleName("windowcontainer");
                    connectinoWindow.setStyleName("connectionwindow");
                    List<String> userOverviewData = connectToGalaxy(userAPIFeald.getValue(), viewId);
                    updateConnectionStatusToGalaxy(userOverviewData);
                };
                if (executorService.isShutdown()) {
                    executorService = Executors.newFixedThreadPool(2);
                }

                executorService.submit(task);
                executorService.shutdown();

            }
        });

        viewControlButton = new SmallSideBtn(VaadinIcons.HOME_O);
        viewControlButton.addStyleName("homepagepresenterbtn");
        viewControlButton.setData(WelcomePagePresenter.this.getViewId());

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

    }

    /**
     * Log in to galaxy as guest (test user API key).
     */
    public void loginAsGuest() {
        String caption = "<b style=\"color:#cd6e1d !important\">Guest User <i>(public data)</i></b>";

        String requestToShare = Page.getCurrent().getLocation().toString();
        if (requestToShare.contains("toShare_-_")) {
            caption = "<b style=\"color:#cd6e1d !important\">Retrieving Dataset Information</i></b>";

        }
        galaxyLoginConnectionBtnLabel.setVisible(false);
        connectinoWindow.setVisible(true);
        galaxyLoginLayout.setVisible(false);
        connectinoWindow.setClosable(false);
        connectingLabel.setCaption(caption);
        connectingLabel.setVisible(true);
        Runnable task = () -> {
            connectinoWindow.removeStyleName("windowcontainer");
            connectinoWindow.setStyleName("connectionwindow");
            List<String> userOverviewData = connectToGalaxy(testUserLogin, viewId);
            updateConnectionStatusToGalaxy(userOverviewData);
        };
        if (executorService.isShutdown()) {
            executorService = Executors.newSingleThreadExecutor();
        }
        executorService.submit(task);
        executorService.shutdown();
    }

    /**
     * update the layout based on connection to galaxy.
     *
     * @param userOverviewData list of user data gathered from user account on
     * Galaxy server
     */
    private void updateConnectionStatusToGalaxy(List<String> userOverviewData) {

        UI.getCurrent().accessSynchronously(new Runnable() {
            @Override
            public void run() {

                connectinoWindow.setClosable(true);
                galaxyLoginConnectionBtnLabel.setVisible(true);
                galaxyLoginLayout.setEnabled(true);
                galaxyLoginLayout.setVisible(true);
                galaxyLoginBtn.setData(null);
                connectingLabel.setCaption(null);

                if (userOverviewData != null && userOverviewData.get(0).contains("Guest User")) {
                    connectinoWindow.setVisible(false);
                    presenteControlButtonsLayout.setEnabled(true);
                } else if (userOverviewData != null && !userOverviewData.get(0).contains("Guest User")) {
                    connectinoWindow.setVisible(false);
                    galaxyLoginBtn.updateText("Galaxy Logout");
                    galaxyLoginBtn.setData("connected");
                    galaxyLoginLayout.setEnabled(false);
                    presenteControlButtonsLayout.setEnabled(true);

                } else {
                    userAPIFeald.setValue(apiErrorMessage);
                    userAPIFeald.addStyleName("redfont");
                    Notification.show("Public user is not available", Notification.Type.TRAY_NOTIFICATION);
                    connectinoWindow.setVisible(false);
                    presenteControlButtonsLayout.setEnabled(true);
                }
                updateUserOverviewPanel(userOverviewData);
                connectingLabel.setVisible(false);
                UI.getCurrent().push();
            }
        });
    }

    /**
     * update user overview panel.
     *
     * @param userOverviewData list of user data gathered from user account on
     * Galaxy server
     */
    public void updateUserOverviewPanel(List<String> userOverviewData) {
        if (userOverviewData != null && !userOverviewData.isEmpty()) {
            Label l1 = initLeftSideInfoLabel(VaadinIcons.USER.getHtml() + " <b class='rightsidediv' style='color:#cd6e1d !important'>" + userOverviewData.get(0) + "</b>", "");
            l1.addStyleName("headerlabel");
            userOverviewLayout.replaceComponent(userOverviewLayout.getComponent(1), l1);
            for (int i = 2; i < userOverviewLayout.getComponentCount(); i++) {
                Label l = (Label) userOverviewLayout.getComponent(i);
                updateLeftSideInfoLabel(l, userOverviewData.get(i - 1));
                if (i == 4) {
                    break;
                }
            }
        }
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

    /**
     * Connect to galaxy server.
     *
     * @param presenterId button used to login
     * @param userAPI user API key that is required to connect to galaxy
     * @return list of overview data for the user / null indicate failed to
     * connect to Galaxy Server
     */
    public abstract List<String> connectToGalaxy(String userAPI, String presenterId);

    /**
     * Create unified labels for the user overview panel
     *
     * @param title the label text title
     * @param value the label text value
     * @return the generated label
     *
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
     *
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

    /**
     * Set the presenter buttons (large button with label) container that have
     * button for each presenter in the system
     *
     * @param presenterBtnsContainer grid layout that contain all the large
     * presenter buttons
     */
    public void setPresenterControlButtonContainer(AbsoluteLayout presenterBtnsContainer) {
        presenterBtnsContainer.addComponent(galaxyLoginBtn, "left:50%;top:50%;");
        presenteControlButtonsLayout.addComponent(presenterBtnsContainer);
    }

}
