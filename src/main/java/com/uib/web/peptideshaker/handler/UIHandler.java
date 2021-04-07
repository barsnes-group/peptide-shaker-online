package com.uib.web.peptideshaker.handler;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.ui.UIContainer;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * This class represents presenter layer that responsible for front-end
 * visualisation and small computing processes that is done by the front end
 * components
 *
 * @author Yehia Farag
 */
public class UIHandler {

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
        appManagmentBean.getUI_Manager().registerView(uiContainer.getWelcomePageView());
        appManagmentBean.getUI_Manager().registerView(uiContainer.getFileSystemView());
        appManagmentBean.getUI_Manager().registerView(uiContainer.getResultsView());

    }

  

    /**
     * Get presenter container
     *
     * @return main presenter controller
     */
    public UIContainer getUiContainer() {
        return uiContainer;
    }
}
