package com.uib.web.peptideshaker.uimanager;

import com.uib.web.peptideshaker.ui.abstracts.ViewableFrame;
import com.uib.web.peptideshaker.ui.views.FileSystemView;
import com.uib.web.peptideshaker.ui.views.ResultsView;
import com.uib.web.peptideshaker.ui.views.WelcomePageView;
import com.uib.web.peptideshaker.ui.views.WorkflowInvokingView;
import com.vaadin.ui.UI;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents the main layout of the application and main view
 * controller manager
 *
 * @author Yehia Farag
 */
public class UIManager implements Serializable{

    private String viewedDatasetId;
    private final Map<String, String> stylingMap = new HashMap<>();
    /**
     * Map of current registered views.
     */
    private final Map<String, ViewableFrame> visualizationMap = new LinkedHashMap<>();

    /**
     * Constructor to initialise UI Manager
     */
    public UIManager() {
        stylingMap.put(FileSystemView.class.getName(), "filesystemstyle");
        stylingMap.put(WorkflowInvokingView.class.getName(), "workflowstyle");
        stylingMap.put(ResultsView.class.getName(), "resultsviewstyle");
        stylingMap.put(WelcomePageView.class.getName(), "welcomepageviewstyle");
    }

    /**
     * Register view into the view management system.
     *
     * @param view visualisation layout.
     */
    public void registerView(ViewableFrame view) {
        visualizationMap.put(view.getViewId(), view);
    }

    /**
     * View only selected view and hide the rest of registered layout
     *
     * @param viewId selected view id
     */
    public void viewLayout(String viewId) {

        visualizationMap.values().forEach((view) -> {
            UI.getCurrent().removeStyleName(stylingMap.get(view.getViewId()));
            if (viewId.equalsIgnoreCase(view.getViewId())) {
                view.maximizeView();
            } else {
                view.minimizeView();
            }
        });
        UI.getCurrent().addStyleName(stylingMap.get(viewId));
    }

    public void updateAll() {
       
            visualizationMap.values().forEach((view) -> {
                view.update();
            });

    }

    public String getViewedDatasetId() {
        return viewedDatasetId;
    }

    public void setViewedDatasetId(String viewedDatasetId) {
        this.viewedDatasetId = viewedDatasetId;
    }

    public void setOngoingJob(boolean ongoing) {
        if (ongoing) {
            UI.getCurrent().addStyleName("ongoingjob");
        } else {
            UI.getCurrent().removeStyleName("ongoingjob");
        }
    }

}
