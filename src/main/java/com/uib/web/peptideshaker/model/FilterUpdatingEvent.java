
package com.uib.web.peptideshaker.model;

import java.util.Map;
import java.util.Set;

/**
 *This class represents event to update filers
 * @author Yehia Mokhtar Farag
 */
public class FilterUpdatingEvent {
    
    private Map<Comparable, Set<Integer>> selectionMap;
    private Set<Comparable> selectionCategories;

    /**
     *
     * @return
     */
    public Map<Comparable, Set<Integer>> getSelectionMap() {
        return selectionMap;
    }

    /**
     *
     * @param selectionMap
     */
    public void setSelectionMap(Map<Comparable, Set<Integer>> selectionMap) {
        this.selectionMap = selectionMap;
    }

    /**
     *
     * @return
     */
    public Set<Comparable> getSelectionCategories() {
        return selectionCategories;
    }

    /**
     *
     * @param selectionCategories
     */
    public void setSelectionCategories(Set<Comparable> selectionCategories) {
        this.selectionCategories = selectionCategories;
    }
    
}
