package com.uib.web.peptideshaker.ui.views.modal;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.ui.components.items.ButtonWithLabel;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This class represents galaxy server login panel that allow users to login to
 * their accounts using API key
 *
 * @author Yehia Mokhtar Farag
 */
public class GalaxyLoginPopup extends ButtonWithLabel implements LayoutEvents.LayoutClickListener {

    /**
     * The body layout panel.
     */
    private final VerticalLayout galaxyLoginPanelContainer;
    /**
     * Not valid API error message .
     */
    private final String apiErrorMessage = "Wrong API please try again";
    private final String panelButtonText = "<font style='width: 100%;height: 100%;font-size: 14px;font-weight: 600;text-align: justify;line-height: 70px;'>Login to Galaxy Server using your API Key</font>";
    private final String logoutButtonText = "Galaxy Logout<br/><font>Logout user account</font>";
    private final String loginButtonText = "Galaxy Login<br/><font>Login using API key</font>";
    private final Window galaxyLoginWindow;
    private final AppManagmentBean appManagmentBean;

    public GalaxyLoginPopup() {
        super("Galaxy Login<br/><font>Login using API key</font>", 0);
        super.setData("login");
        GalaxyLoginPopup.this.updateIconResource(new ThemeResource("img/galaxylogocolor.png"));
        GalaxyLoginPopup.this.addStyleName("galaxylabel");
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        /**
         * Pop-up window layout to connect to Galaxy Server.
         */
        galaxyLoginPanelContainer = new VerticalLayout();
        galaxyLoginPanelContainer.setWidth(500, Unit.PIXELS);
        galaxyLoginPanelContainer.setHeightUndefined();
        galaxyLoginPanelContainer.setMargin(new MarginInfo(true, true, true, true));
        galaxyLoginPanelContainer.setSpacing(true);
        galaxyLoginWindow = new Window(null, galaxyLoginPanelContainer) {
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
        galaxyLoginWindow.setModal(true);
        galaxyLoginWindow.setDraggable(false);
        galaxyLoginWindow.setClosable(true);
        galaxyLoginWindow.setStyleName("windowcontainer");
        galaxyLoginWindow.setResizable(false);
        galaxyLoginWindow.center();
        galaxyLoginWindow.setWindowMode(WindowMode.NORMAL);
        if (!UI.getCurrent().getWindows().contains(galaxyLoginWindow)) {
            UI.getCurrent().addWindow(galaxyLoginWindow);
        }
        galaxyLoginWindow.setVisible(false);
        VerticalLayout galaxyLoginLayout = new VerticalLayout();
        galaxyLoginLayout.setSizeFull();
        galaxyLoginLayout.setSpacing(true);
        galaxyLoginLayout.setVisible(true);
        galaxyLoginLayout.setMargin(false);
        galaxyLoginPanelContainer.addComponent(galaxyLoginLayout);
        galaxyLoginPanelContainer.setExpandRatio(galaxyLoginLayout, 0.1f);
        galaxyLoginPanelContainer.setComponentAlignment(galaxyLoginLayout, Alignment.TOP_LEFT);

        HorizontalLayout serviceButtonContainer = new HorizontalLayout();
        serviceButtonContainer.setWidth(100, Unit.PERCENTAGE);
        serviceButtonContainer.setHeight(40, Unit.PIXELS);
        serviceButtonContainer.setMargin(false);
        serviceButtonContainer.setSpacing(true);
        galaxyLoginLayout.addComponent(serviceButtonContainer);
        galaxyLoginLayout.setExpandRatio(serviceButtonContainer, 0.1f);
        galaxyLoginLayout.setComponentAlignment(serviceButtonContainer, Alignment.TOP_LEFT);

        ButtonWithLabel galaxyLoginConnectionBtnLabel = new ButtonWithLabel(panelButtonText, 1);
        galaxyLoginConnectionBtnLabel.updateIconResource(new ThemeResource("img/galaxylogocolor.png"));
        galaxyLoginConnectionBtnLabel.addStyleName("smaller");
        serviceButtonContainer.addComponent(galaxyLoginConnectionBtnLabel);

        TextField userAPIFeald = new TextField("User API");
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

        Link regLink = new Link("Register", new ExternalResource(VaadinSession.getCurrent().getAttribute("galaxyServerUrl") + "/login"));
        regLink.setStyleName("newlink");
        regLink.setTargetName("_blank");
        galaxyServiceBtns.addComponent(regLink);
        galaxyServiceBtns.setExpandRatio(regLink, 0.1f);

        Link userAPI = new Link("Get User API", new ExternalResource(VaadinSession.getCurrent().getAttribute("galaxyServerUrl") + "/user/api_key"));
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
            if (userAPIFeald.isValid()) {
                String userId = appManagmentBean.getGalaxyFacad().authenticate(userAPIFeald.getValue());
                if (userId != null) {
                    appManagmentBean.getNotificationFacade().showGalaxyConnectingProcess("Retrive user data");
                    appManagmentBean.getUserHandler().setUserLoggedIn(userAPIFeald.getValue(), userId);
                    appManagmentBean.getUI_Manager().updateAll();
                    appManagmentBean.getUI_Manager().updateAll();
                    appManagmentBean.getNotificationFacade().hideGalaxyConnectingProcess();
                    galaxyLoginWindow.setVisible(false);
                    super.updateText(logoutButtonText);
                    super.setData("logout");

                } else {
                    userAPIFeald.setValue(apiErrorMessage);
                    userAPIFeald.addStyleName("redfont");
                }

            }
        });
        GalaxyLoginPopup.this.addLayoutClickListener(GalaxyLoginPopup.this);
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        if (super.getData().toString().equals("logout")) {
            Page.getCurrent().reload();
        } else {
            galaxyLoginWindow.setVisible(true);
        }
    }

}
