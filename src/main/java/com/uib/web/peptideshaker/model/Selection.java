package com.uib.web.peptideshaker.model;

import java.util.Set;

/**
 * Selection for selection manager
 *
 * @author Yehia Mokhtar Farag
 */
public class Selection {

    private Set<Integer> selectionIndexes;
    private final String filterId;
    private final Set<Comparable> selectionCategories;
    private final String selectionType;
    private final boolean addAction;

    /**
     *
     * @param selectionType
     * @param filterId
     * @param selectionCategories
     * @param addAction
     */
    public Selection(String selectionType, String filterId, Set<Comparable> selectionCategories, boolean addAction) {
        this.filterId = filterId;
        this.selectionCategories = selectionCategories;
        this.selectionType = selectionType;
        this.addAction = addAction;
    }

    /**
     * Is add filter selection
     *
     * @return
     */
    public boolean isAddAction() {
        return addAction;
    }

    /**
     *
     * @param selectionIndexes
     */
    public void setSelectionIndexes(Set<Integer> selectionIndexes) {
        this.selectionIndexes = selectionIndexes;
    }

    /**
     *
     * @return
     */
    public Set<Integer> getSelectionIndexes() {
        return selectionIndexes;
    }

    /**
     * Get filter id used for the event
     *
     * @return
     */
    public String getFilterId() {
        return filterId;
    }

    /**
     * get the selection categories
     *
     * @return
     */
    public Set<Comparable> getSelectionCategories() {
        return selectionCategories;
    }

    /**
     * Get the selection type
     *
     * @return
     */
    public String getSelectionType() {
        return selectionType;
    }

}
