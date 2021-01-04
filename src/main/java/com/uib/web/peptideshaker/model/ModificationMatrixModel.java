
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
    private  Map<String, Integer> rows;
    /**
     * The full calculated matrix.
     */
    private  Map<String, Set<Integer>> columns;

    public Map<String, Integer> getRows() {
        return rows;
    }

    public void setRows(Map<String, Integer> rows) {
        this.rows = rows;
    }

    public Map<String, Set<Integer>> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, Set<Integer>> columns) {
        this.columns = columns;
    }
}
