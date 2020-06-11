package com.uib.web.peptideshaker.presenter.core.filtercharts.components.coverage;

import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.VerticalLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents peptide component layout that is used in the protein
 * sequence coverage.
 *
 * @author Yehia Farag
 */
public class StackedBarPeptideComponent extends VerticalLayout implements Comparable<StackedBarPeptideComponent>, LayoutEvents.LayoutClickListener {

    /**
     * Parameters map.
     */
    private final Map<String, Object> parametersMap = new HashMap<>();
    /**
     * Default CSS style.
     */
    private String defaultStyleShowAllMode;
    /**
     * The pValue is significant.
     */
    private boolean significant;
    /**
     * The peptide location level.
     */
    private int level = 0;
    /**
     * The peptide start location on x access.
     */
    private int x0;
    /**
     * The peptide width.
     */
    private final Integer widthArea;
    /**
     * The peptide PTM layout container.
     */
    private final VerticalLayout ptmLayout = new VerticalLayout();
    /**
     * The peptide has PTMs.
     */
    private boolean ptmAvailable = false;

    /**
     * The main pop-up window that contain peptide information form.
     */
//    private PopupWindowFrame popupWindow;
    /**
     * Get the peptide location level
     *
     * @return peptide level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Set peptide location level
     *
     * @param level the location level.
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Set Default CSS style name.
     *
     * @param defaultStyleShowAllMode Default CSS style name.
     */
    public void setDefaultStyleShowAllMode(String defaultStyleShowAllMode) {
        this.defaultStyleShowAllMode = defaultStyleShowAllMode.replace("selected", "").trim();
        this.setStyleName(this.defaultStyleShowAllMode);
    }

    /**
     * Get the peptide start location on x access.
     *
     * @return location on x access
     */
    public int getX0() {
        return x0;
    }

    /**
     * Get peptide width.
     *
     * @return widthArea Peptide width.
     */
    public int getWidthArea() {
        return widthArea;
    }

    /**
     * Set the peptide start location on x access.
     *
     * @param x0 x access location.
     */
    public void setX0(int x0) {
        this.x0 = x0;
    }

    /**
     * Constructor to initialize the main attributes.
     *
     * @param x0 The peptide start location on x access.
     * @param widthArea The peptide width.
     * @param peptideModification The peptide modification PTMs.
     * @param quantPeptide Quant peptide object that has all peptide
     * information.
     * @param proteinName The parent protein name.
     */
    public StackedBarPeptideComponent(int x0, int widthArea, String peptideModification, QuantPeptide quantPeptide, String proteinName) {
        this.setHeight(15, Unit.PIXELS);
        this.setWidth((widthArea), Unit.PIXELS);
        this.x0 = x0;
        this.widthArea = widthArea;
        StackedBarPeptideComponent.this.setParam("width", widthArea);
        if (peptideModification != null && !peptideModification.trim().equalsIgnoreCase("")) {
            ptmAvailable = true;
            ptmLayout.setStyleName("ptmglycosylation");
            ptmLayout.setWidth(10, Unit.PIXELS);
            ptmLayout.setHeight(10, Unit.PIXELS);
            ptmLayout.setDescription(peptideModification);
            ptmLayout.setVisible(false);
        }
        if (quantPeptide != null) {

            VerticalLayout popupBody = new VerticalLayout();
            popupBody.setWidth(99, Unit.PERCENTAGE);
            popupBody.setHeight(365, Unit.PIXELS);
            String title = "Peptide Information (" + proteinName.trim() + ")";

//            popupWindow = new PopupWindowFrame(title, popupBody);
//
//            popupWindow.setFrameHeight(450);
//            popupWindow.setFrameWidth(1500);
//
//            PeptidesInformationOverviewLayout peptideInfo = new PeptidesInformationOverviewLayout(quantPeptide);
//            popupBody.addComponent(peptideInfo);
//            popupBody.setComponentAlignment(peptideInfo, Alignment.TOP_CENTER);
            this.addLayoutClickListener(StackedBarPeptideComponent.this);
        }

    }

    /**
     * Check if peptide has PTMs.
     *
     * @return ptmAvailable peptide has PTMs.
     */
    public boolean isPtmAvailable() {
        return ptmAvailable;
    }

    /**
     * Get PTM layout container.
     *
     * @return ptmLayout container.
     */
    public VerticalLayout getPtmLayout() {
        return ptmLayout;
    }

    /**
     * Add parameter.
     *
     * @param key parameter key
     * @param value parameter value.
     */
    public void setParam(String key, Object value) {
        parametersMap.put(key, value);
    }

    /**
     * Get parameter
     *
     * @param key parameter key.
     * @return parameter value.
     */
    public Object getParam(String key) {
        return parametersMap.get(key);
    }

    @Override
    public int compareTo(StackedBarPeptideComponent o) {
        return widthArea.compareTo(o.widthArea);
    }

    /**
     * Check if the pValue is significant.
     *
     * @return significant pValue.
     */
    public boolean isSignificant() {
        return significant;
    }

    /**
     * Set pValue is significant.
     *
     * @param significant significant pValue.
     */
    public void setSignificant(boolean significant) {
        this.significant = significant;
    }

    /**
     * View peptide information pop-up form
     *
     * @param event user click action.
     */
    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
//        popupWindow.view();
    }

}
