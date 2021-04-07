package com.uib.web.peptideshaker;

import com.uib.web.peptideshaker.handler.UIHandler;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.ui.UIContainer;
import com.uib.web.peptideshaker.ui.views.WelcomePageView;
import com.vaadin.server.VaadinSession;

/**
 * This class represents the main landing Online PeptideShaker application
 *
 * @author Yehia Mokhtar Farag
 */
public class AppMainContainer {

    private final UIHandler uiHandler;
    /**
     * The Main data access layer that deal with the system files and data.
     */
    private final AppManagmentBean appManagmentBean;

    /**
     * Constructor to initialise the application.
     */
    public AppMainContainer() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        /**
         * Check galaxy available online using public user API key or run system
         * in offline galaxy mode.
         */
        appManagmentBean.getNotificationFacade().showGalaxyConnectingProcess(CONSTANT.PUBLIC_USER_CAPTION);
        String userId = appManagmentBean.getGalaxyFacad().authenticate(appManagmentBean.getAppConfig().getTestUserAPIKey());
        appManagmentBean.getUserHandler().setUserLoggedIn(appManagmentBean.getAppConfig().getTestUserAPIKey(), userId);

        uiHandler = new UIHandler();
        this.updateALLUI();
        appManagmentBean.getUI_Manager().viewLayout(WelcomePageView.class.getName());
        appManagmentBean.getNotificationFacade().hideGalaxyConnectingProcess();

    }

    private void updateALLUI() {
        appManagmentBean.getUI_Manager().updateAll();

    }

    /**
     * Get the main User Interface layer
     *
     * @return main user interface container
     */
    public UIContainer getApplicationUserInterface() {
        return uiHandler.getUiContainer();
    }

    

}
