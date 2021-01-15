
package com.uib.web.peptideshaker.model;

import java.util.Map;
import java.util.Set;

/**
 *This class represents event to update filers
 * @author Yehia Mokhtar Farag
 */
public class FilterUpdatingEvent {
    
    private Map<Comparable, Set<Integer>> selectionMap;
    private Set<Comparable> seletionCategories;

    public Map<Comparable, Set<Integer>> getSelectionMap() {
        return selectionMap;
    }

    public void setSelectionMap(Map<Comparable, Set<Integer>> selectionMap) {
        this.selectionMap = selectionMap;
    }

    public Set<Comparable> getSeletionCategories() {
        return seletionCategories;
    }

    public void setSeletionCategories(Set<Comparable> seletionCategories) {
        this.seletionCategories = seletionCategories;
    }
    
}
