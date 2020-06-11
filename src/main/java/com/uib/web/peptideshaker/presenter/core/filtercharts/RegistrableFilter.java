package com.uib.web.peptideshaker.presenter.core.filtercharts;

import com.vaadin.ui.Layout;
import java.util.Set;

/**
 * This interface include all the abstracted methods required to register the
 * filter into the selection manager
 *
 * @author Yehia Farag
 */
public interface RegistrableFilter extends Layout {

    /**
     * Get filter unique id
     *
     * @return id
     */
    public String getFilterId();

    /**
     * Update filter selection
     *
     * @param selection current selected data
     * @param selectedCategories current selected categories
     * @param topFilter top filter selected
     * @param singleFilter only one filter selected
     * @param selfAction action come from filter to ignore update
     */
    public void updateFilterSelection(Set<Comparable> selection, Set<Comparable> selectedCategories, boolean topFilter, boolean singleFilter, boolean selfAction);

    /**
     * Filter selection invoked
     *
     * @param type type of filter
     */
    public void selectionChange(String type);

    /**
     * redraw the filter chart is available
     */
    public void redrawChart();

    /**
     * Suspend filter
     *
     * @param suspend filter is inactive
     */
    public void suspendFilter(boolean suspend);

}
