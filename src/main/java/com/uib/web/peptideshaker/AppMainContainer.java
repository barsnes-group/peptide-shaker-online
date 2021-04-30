package com.uib.web.peptideshaker;

import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.ui.UIContainer;
import com.uib.web.peptideshaker.ui.views.ResultsView;
import com.uib.web.peptideshaker.ui.views.WelcomePageView;
import com.uib.web.peptideshaker.ui.views.subviews.DatasetProteinsSubView;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import io.vertx.core.json.JsonObject;

/**
 * This class represents the main landing Online PeptideShaker application
 *
 * @author Yehia Mokhtar Farag
 */
public class AppMainContainer {

    /**
     * The Main data access layer that deal with the system files and data.
     */
    private final AppManagmentBean appManagmentBean;

    /**
     * Constructor to initialise the application.
     */
    public AppMainContainer() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);

        //check if it is shared link 
        String requestToShare = Page.getCurrent().getLocation().toString();
        appManagmentBean.setSharingDatasetMode(requestToShare.contains("?datasetid"));
        if (appManagmentBean.isSharingDatasetMode()) {
            appManagmentBean.getNotificationFacade().showGalaxyConnectingProcess("Retrieving  dataset");
        } else {
            appManagmentBean.getNotificationFacade().showGalaxyConnectingProcess(CONSTANT.PUBLIC_USER_CAPTION);
        }
        String userId = appManagmentBean.getGalaxyFacad().authenticate(appManagmentBean.getAppConfig().getTestUserAPIKey());
        appManagmentBean.getUserHandler().setUserLoggedIn(appManagmentBean.getAppConfig().getTestUserAPIKey(), userId);
        UIContainer uiContainer = new UIContainer();
        UI.getCurrent().setContent(uiContainer);
        if (appManagmentBean.isAvailableGalaxy() && !appManagmentBean.isSharingDatasetMode()) {
            appManagmentBean.getUI_Manager().registerView(uiContainer.getWorkflowInvokingView());
        }
        if (!appManagmentBean.isSharingDatasetMode()) {
            appManagmentBean.getUI_Manager().registerView(uiContainer.getWelcomePageView());
            appManagmentBean.getUI_Manager().registerView(uiContainer.getFileSystemView());
        }
        appManagmentBean.getUI_Manager().registerView(uiContainer.getResultsView());
        this.updateALLUI();

        if (appManagmentBean.isSharingDatasetMode()) {
            uiContainer.disableActionButtons();
            appManagmentBean.getNotificationFacade().showGalaxyConnectingProcess("Retrieving  dataset");
            JsonObject datasetAsJson = appManagmentBean.getSharedDatasetUtils().retrieveDataset(requestToShare.split("\\?datasetid")[1]);
            if (datasetAsJson == null) {
                Page.getCurrent().open(requestToShare.replace("?datasetid", "") + ".error", "_self");
                return;
            }
            userId = appManagmentBean.getGalaxyFacad().authenticate(datasetAsJson.getString("apiKey"));
            appManagmentBean.getUserHandler().setUserLoggedIn(datasetAsJson.getString("apiKey"), userId);
            appManagmentBean.getUI_Manager().setSelectedDatasetId(datasetAsJson.getString("ps"));
            appManagmentBean.getUI_Manager().viewLayout(ResultsView.class.getName());
            appManagmentBean.getUI_Manager().viewSubLayout(ResultsView.class.getName(), DatasetProteinsSubView.class.getName());


        } else {
            appManagmentBean.getUI_Manager().viewLayout(WelcomePageView.class.getName());
        }

        appManagmentBean.getNotificationFacade().hideGalaxyConnectingProcess();

    }

    private void updateALLUI() {
        appManagmentBean.getUI_Manager().updateAll();

    }

}
