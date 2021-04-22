package com.uib.web.peptideshaker.model;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author Yehia Mokhtar Farag
 */
public class ModificationMatrixModel {

    /**
     * Rows data map.
     */
    private Map<String, Set<Integer>> rows;
    /**
     * The full calculated matrix.
     */
    private Map<Comparable, Set<Integer>> columns;

    /**
     *
     * get rows
     *
     * @return rows
     */
    public Map<String, Set<Integer>> getRows() {
        return rows;
    }

    /**
     * Set matrix rows
     *
     * @param rows
     */
    public void setRows(Map<String, Set<Integer>> rows) {
        this.rows = rows;
    }

    /**
     * The full calculated matrix.
     *
     * @return
     */
    public Map<Comparable, Set<Integer>> getColumns() {
        return columns;
    }

    /**
     * The full calculated matrix.
     *
     * @param columns
     */
    public void setColumns(Map<Comparable, Set<Integer>> columns) {
        this.columns = columns;
    }
}
