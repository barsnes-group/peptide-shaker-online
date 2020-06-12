package com.uib.web.peptideshaker.model.core;

import java.util.Set;

/**
 * This class represents controller class that keep the filtered and not
 * filtered proteins set
 *
 * @author Yehia Farag
 */
public class FilteredProteins {

    /**
     * Set of full protein accessions.
     */
    private Set<Comparable> fullProteinList;
    /**
     * Set of filtered protein accessions.
     */
    private Set<Comparable> filteredProteinList;

    /**
     * Get Set of full protein accessions.
     *
     * @return Set of protein accessions.
     */
    public Set<Comparable> getFullProteinList() {
        return fullProteinList;
    }

    /**
     * Set Set of full protein accessions.
     *
     * @param fullProteinList Set of protein accessions
     */
    public void setFullProteinList(Set<Comparable> fullProteinList) {
        this.fullProteinList = fullProteinList;
    }

    /**
     * Get Set of filtered protein accessions.
     *
     * @return Set of protein accessions
     */
    public Set<Comparable> getFilteredProteinList() {
        return filteredProteinList;
    }

    /**
     * Set Set of filtered protein accessions.
     *
     * @param filteredProteinList Set of protein accessions
     */
    public void setFilteredProteinList(Set<Comparable> filteredProteinList) {
        this.filteredProteinList = filteredProteinList;
    }

}
