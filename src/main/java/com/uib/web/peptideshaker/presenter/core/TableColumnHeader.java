package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.server.Resource;
import com.vaadin.ui.Table;

/**
 * This class include information required to initialize table headers
 *
 * @author Yehia Farag
 */
public class TableColumnHeader {

    private final Object propertyId;
    private final Class<?> type;
    private final Object defaultValue;
    private final String columnHeader;
    private final Resource columnIcon;
    private final Table.Align columnAlignment;

    /**
     * Initialise table column header
     *
     * @param propertyId column id
     * @param type column type
     * @param defaultValue null representer
     * @param columnHeader title of the column
     * @param columnIcon column icon
     * @param columnAlignment title alignment
     */
    public TableColumnHeader(Object propertyId, Class<?> type, Object defaultValue, String columnHeader, Resource columnIcon, Table.Align columnAlignment) {
        this.propertyId = propertyId;
        this.type = type;
        this.defaultValue = defaultValue;
        this.columnHeader = columnHeader;
        this.columnIcon = columnIcon;
        this.columnAlignment = columnAlignment;
    }

    /**
     * Get column id
     *
     * @return id
     */
    public Object getPropertyId() {
        return propertyId;
    }

    /**
     * get column type
     *
     * @return type
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * Get null representer
     *
     * @return default value
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Get the column header title
     *
     * @return header title
     */
    public String getColumnHeader() {
        return columnHeader;
    }

    /**
     * Get column icon
     *
     * @return icon resource
     */
    public Resource getColumnIcon() {
        return columnIcon;
    }

    /**
     * Get column title alignment
     *
     * @return column title alignment
     */
    public Table.Align getColumnAlignment() {
        return columnAlignment;
    }

    public String toString() {
        return columnHeader;
    }
}
