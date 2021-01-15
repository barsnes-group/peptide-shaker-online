package com.uib.web.peptideshaker.uimanager;

import com.uib.web.peptideshaker.ui.abstracts.ViewableFrame;
import com.uib.web.peptideshaker.ui.views.FileSystemView;
import com.uib.web.peptideshaker.ui.views.ResultsView;
import com.uib.web.peptideshaker.ui.views.WelcomePageView;
import com.uib.web.peptideshaker.ui.views.WorkflowInvokingView;
import com.uib.web.peptideshaker.ui.views.subviews.DatasetProteinsSubView;
import com.uib.web.peptideshaker.ui.views.subviews.PeptidePsmsSubView;
import com.uib.web.peptideshaker.ui.views.subviews.PrideSubView;
import com.uib.web.peptideshaker.ui.views.subviews.ProteinPeptidesSubView;
import com.uib.web.peptideshaker.ui.views.subviews.UserUploadDataSubView;
import com.vaadin.ui.UI;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the main layout of the application and main view
 * controller manager
 *
 * @author Yehia Farag
 */
public class UIManager implements Serializable {

    private String selectedDatasetId;
    private int selectedProteinIndex=-1;
    private int selectedPeptideIndex;

    private String encodedProteinButtonImage;
    private String encodedPeptideButtonImage;
    private final Map<String, String> stylingMap = new HashMap<>();
    private final Map<String, String> subStylingMap = new HashMap<>();
    /**
     * Map of current registered views.
     */
    private final Map<String, ViewableFrame> visualizationMap = new LinkedHashMap<>();
    /**
     * Map of current sub views.
     */
    private final Map<String, Set<ViewableFrame>> subVisualizationMap = new LinkedHashMap<>();

    /**
     * Constructor to initialise UI Manager
     */
    public UIManager() {
        stylingMap.put(FileSystemView.class.getName(), "filesystemstyle");
        stylingMap.put(WorkflowInvokingView.class.getName(), "workflowstyle");
        stylingMap.put(ResultsView.class.getName(), "resultsviewstyle");
        stylingMap.put(WelcomePageView.class.getName(), "welcomepageviewstyle");

        subStylingMap.put(PrideSubView.class.getName(), "pridesubviewstyle");
        subStylingMap.put(UserUploadDataSubView.class.getName(), "useruploaddatasubviewstyle");
        subStylingMap.put(DatasetProteinsSubView.class.getName(), "datasetproteinssubviewstyle");
        subStylingMap.put(ProteinPeptidesSubView.class.getName(), "proteinpeptidessubviewstyle");
        subStylingMap.put(PeptidePsmsSubView.class.getName(), "peptidepsmssubviewstyle");

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
     * Register sub view into the view management system.
     *
     * @param view visualisation layout.
     */
    public void registerSubView(String mainViewId, ViewableFrame subView) {
        if (!subVisualizationMap.containsKey(mainViewId)) {
            subVisualizationMap.put(mainViewId, new LinkedHashSet<>());
        }
        subVisualizationMap.get(mainViewId).add(subView);
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

    /**
     * View only selected view and hide the rest of registered layout
     *
     * @param viewId main view id
     * @param subviewId selected sub-view id
     */
    public void viewSubLayout(String viewId, String subviewId) {
        Set<ViewableFrame> subViews = subVisualizationMap.get(viewId);
        for (ViewableFrame subView : subViews) {
            UI.getCurrent().removeStyleName(subStylingMap.get(subView.getViewId()));
            if (subView.getViewId().equals(subviewId)) {
                subView.maximizeView();
            } else {
                subView.minimizeView();
            }
        }
        UI.getCurrent().addStyleName(subStylingMap.get(subviewId));

    }

    public void updateAll() {

        visualizationMap.values().forEach((view) -> {
            view.update();
        });

    }

    public void updateResultsSubViews() {
        subVisualizationMap.get(ResultsView.class.getName()).forEach((subView) -> {
            subView.update();
        });

    }

    public int getSelectedProteinIndex() {
        return selectedProteinIndex;
    }

    public int getSelectedPeptideIndex() {
        return selectedPeptideIndex;
    }

    public String getSelectedDatasetId() {
        return selectedDatasetId;
    }

    public void setSelectedDatasetId(String selectedDatasetId) {
        selectedProteinIndex = -1;
        selectedPeptideIndex = -1;
        if (!selectedDatasetId.equalsIgnoreCase(this.selectedDatasetId)) {
            this.selectedDatasetId = selectedDatasetId;
            visualizationMap.get(ResultsView.class.getName()).update();
            this.updateResultsSubViews();
        } else {
            this.selectedDatasetId = selectedDatasetId;
        }
    }

    public void setSelectedProteinIndex(int proteinIndex) {
        this.selectedPeptideIndex = -1;
        this.selectedProteinIndex = proteinIndex;        
        this.updateResultsSubViews();
        visualizationMap.get(ResultsView.class.getName()).update();

    }

    public void setSelectedPeptideIndex(int selectedPeptideIndex) {
        this.selectedPeptideIndex = selectedPeptideIndex;
        this.updateResultsSubViews();
        visualizationMap.get(ResultsView.class.getName()).update();

    }

    public void setOngoingJob(boolean ongoing) {
        if (ongoing) {
            UI.getCurrent().addStyleName("ongoingjob");
        } else {
            UI.getCurrent().removeStyleName("ongoingjob");
        }
    }

    public String getEncodedProteinButtonImage() {
        return encodedProteinButtonImage;
    }

    public void setEncodedProteinButtonImage(String encodedProteinButtonImage) {
        this.encodedProteinButtonImage = encodedProteinButtonImage;
    }

    public String getEncodedPeptideButtonImage() {
        return encodedPeptideButtonImage;
    }

    public void setEncodedPeptideButtonImage(String encodedPeptideButtonImage) {
        this.encodedPeptideButtonImage = encodedPeptideButtonImage;
    }

}
