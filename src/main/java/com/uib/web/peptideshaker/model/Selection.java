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

    public Selection(String selectionType, String filterId, Set<Comparable> selectionCategories,boolean addAction) {
        this.filterId = filterId;
        this.selectionCategories = selectionCategories;
        this.selectionType = selectionType;
        this.addAction = addAction;
    }

    public boolean isAddAction() {
        return addAction;
    }

    public void setSelectionIndexes(Set<Integer> selectionIndexes) {
        this.selectionIndexes = selectionIndexes;
    }

    public Set<Integer> getSelectionIndexes() {
        return selectionIndexes;
    }

    public String getFilterId() {
        return filterId;
    }

    public Set<Comparable> getSelectionCategories() {
        return selectionCategories;
    }

    public String getSelectionType() {
        return selectionType;
    }

}
