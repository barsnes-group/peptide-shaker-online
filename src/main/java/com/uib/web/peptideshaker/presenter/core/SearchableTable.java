package com.uib.web.peptideshaker.presenter.core;

import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelTextField;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.text.DecimalFormat;
import java.util.*;

/**
 * This class represents table that support search and selection
 *
 * @author Yehia Farag
 */
public abstract class SearchableTable extends AbsoluteLayout implements Property.ValueChangeListener {

    private final String tableMainTitle;
    private final Map<Integer, String> tableSearchingResults;
    private final Map<String, String> tableSearchingMap;
    private final Table mainTable;
    private final HorizontalLayout serachComponent;
    private final Map<Comparable, Object[]> tableData;
    private Label searchResultsLabel;
    private boolean resetSearching = false;
    private Button searchBtn;
    private boolean suspendColumnResizeListener;
    private boolean disableHtmlColumnHeader;

    /**
     * Initialise table component with search in table support
     *
     * @param title                   table title
     * @param defaultSearchingMessage default message in blank search field
     * @param tableHeaders            array of table headers
     */
    public SearchableTable(String title, String defaultSearchingMessage, TableColumnHeader[] tableHeaders) {
        SearchableTable.this.setSizeFull();
        this.tableMainTitle = title;
        this.tableSearchingResults = new TreeMap<>();
        this.tableSearchingMap = new LinkedHashMap<>();
        this.mainTable = initTable(tableHeaders);
        SearchableTable.this.addComponent(mainTable, "top: 35px;left: 0px;");
        serachComponent = initSearchComponentLayout(defaultSearchingMessage);
        SearchableTable.this.addComponent(serachComponent, "top: 10px;right: 0px;");
        this.tableData = new LinkedHashMap<>();
    }

    private HorizontalLayout initSearchComponentLayout(String defaultSearchingMessage) {

        HorizontalLayout searchContainer = new HorizontalLayout();
        searchContainer.setSpacing(false);
        searchContainer.setStyleName("searchtablecontainer");
        searchContainer.setHeight(20, Unit.PIXELS);
        Button exportBtn = new Button(VaadinIcons.FILE_TABLE);
        exportBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        exportBtn.addStyleName("exportbtn");
        exportBtn.setDescription("Export protein table");
        exportBtn.setWidth(20, Unit.PIXELS);
        exportBtn.setHeight(20, Unit.PIXELS);
        searchContainer.addComponent(exportBtn);
        searchContainer.setComponentAlignment(exportBtn, Alignment.TOP_RIGHT);
        exportBtn.addClickListener((Button.ClickEvent event) -> {
            disableHtmlColumnHeader = true;
            ExcelExport csvExport = new ExcelExport(mainTable, "Dataset Protein table");
            csvExport.setReportTitle("Web PeptideShaker / Dataset Protein table");
            csvExport.setExportFileName("Web PeptideShaker - Dataset Protein table" + ".xls");
            csvExport.getColumnHeaderStyle().setWrapText(false);
            csvExport.setMimeType(ExcelExport.EXCEL_MIME_TYPE);
            csvExport.setDisplayTotals(false);
//                csvExport.setExcelFormatOfProperty("Index", "0");

            csvExport.export();
            disableHtmlColumnHeader = false;
        });

        HorizontalLabelTextField searchField = new HorizontalLabelTextField("", defaultSearchingMessage, null);
        searchContainer.addComponent(searchField);

        searchBtn = new Button(VaadinIcons.SEARCH);
        searchBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        searchBtn.setWidth(20, Unit.PIXELS);
        searchBtn.setHeight(20, Unit.PIXELS);
        searchContainer.addComponent(searchBtn);
        searchBtn.setDescription("Search");
        searchContainer.setComponentAlignment(searchBtn, Alignment.TOP_RIGHT);
        searchBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        final Button nextBtn = new Button(VaadinIcons.STEP_FORWARD) {
            @Override
            public void setEnabled(boolean enabled) {
                if (enabled) {
                    searchBtn.removeClickShortcut();
                    this.setClickShortcut(ShortcutAction.KeyCode.ENTER);
                } else {
                    this.removeClickShortcut();
                    searchBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
                }
                super.setEnabled(enabled);
            }

        };
        nextBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        nextBtn.setWidth(20, Unit.PIXELS);
        nextBtn.setHeight(20, Unit.PIXELS);
        searchContainer.addComponent(nextBtn);
        searchContainer.setComponentAlignment(nextBtn, Alignment.TOP_RIGHT);
        nextBtn.setDescription("Select next result");

        searchResultsLabel = new Label("0 of 0");
        searchContainer.addComponent(searchResultsLabel);
        searchContainer.setComponentAlignment(searchResultsLabel, Alignment.TOP_RIGHT);

        searchBtn.addClickListener((Button.ClickEvent event) -> {
            searchforKeyword(searchField.getSelectedValue().trim());
            nextBtn.setEnabled(!tableSearchingResults.isEmpty());
            resetSearching = true;
            nextBtn.click();
            nextBtn.setEnabled(tableSearchingResults.size() > 1);
        });

        nextBtn.addClickListener(new Button.ClickListener() {
            private Iterator<Integer> itr;

            @Override
            public void buttonClick(Button.ClickEvent event) {

                if (itr == null || !itr.hasNext() || resetSearching) {
                    itr = tableSearchingResults.keySet().iterator();
                }
                if (itr.hasNext()) {
                    resetSearching = false;
                    int key = itr.next();
                    searchResultsLabel.setValue(key + " of " + tableSearchingResults.size());
                    Object itemId = tableSearchingMap.get(tableSearchingResults.get(key));
                    mainTable.setValue(itemId);
                    mainTable.setCurrentPageFirstItemIndex(((int) mainTable.getItem(itemId).getItemProperty("index").getValue()) - 1);
                    mainTable.commit();

                }
            }
        });
        searchField.addTextChangeListener((FieldEvents.TextChangeEvent event) -> {
            String txt = event.getText().trim();
            searchforKeyword(txt);
            nextBtn.setEnabled(!tableSearchingResults.isEmpty());
            resetSearching = true;
            nextBtn.click();
            nextBtn.setEnabled(tableSearchingResults.size() > 1);
        });

        return searchContainer;
    }

    private void searchforKeyword(String keyWord) {
        tableSearchingResults.clear();
        searchResultsLabel.setValue(0 + " of " + 0);
        if (keyWord == null || keyWord.equalsIgnoreCase("")) {
            mainTable.setValue(null);
            return;

        }
        keyWord = keyWord.toLowerCase();
        int index = 1;
        tableSearchingResults.clear();
        for (String key : tableSearchingMap.keySet()) {
            if (key.contains(keyWord) && mainTable.containsId(tableSearchingMap.get(key))) {
                tableSearchingResults.put(index++, key);
            }
        }
        if (tableSearchingResults.isEmpty()) {
            Notification.show("No results available", Notification.Type.TRAY_NOTIFICATION);
        }

    }

    public boolean isDisableHtmlColumnHeader() {
        return disableHtmlColumnHeader;
    }

    public void setDisableHtmlColumnHeader(boolean disableHtmlColumnHeader) {
        this.disableHtmlColumnHeader = disableHtmlColumnHeader;
    }

    /**
     * Initialise the proteins table.
     *
     * @param tableHeaders headers of the table
     * @return initialised table
     */
    private Table initTable(TableColumnHeader[] tableHeaders) {
        Table table = new Table() {
            DecimalFormat df = new DecimalFormat("0.00E00");// new DecimalFormat("#.##");
            DecimalFormat df1 = new DecimalFormat("#.##");

            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                Object v = property.getValue();
                if (v instanceof Double) {
                    if ((double) v > 100) {
                        return df.format(v);
                    } else {
                        return df1.format(v);
                    }
                }
                return super.formatPropertyValue(rowId, colId, property);
            }

            @Override
            public String getColumnHeader(Object propertyId) {
                if (disableHtmlColumnHeader) {
                    String header = super.getColumnHeader(propertyId).split("<span class='tooltiptext'>")[0].trim().replace("<div class='tooltip'>", "");
                    if (header.equals("")) {
                        header = "Validation";
                    }
                    return header;
                }
                return super.getColumnHeader(propertyId); //To change body of generated methods, choose Tools | Templates.
            }

        };

        table.setCaption("<b>" + tableMainTitle + "</b>");
        table.setCaptionAsHtml(true);
        table.addStyleName("framedpanel");
        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
        table.addStyleName(ValoTheme.TABLE_SMALL);
        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.setHeight(100, Unit.PERCENTAGE);
        table.setWidth(100, Unit.PERCENTAGE);
        table.setCacheRate(1);
        table.setMultiSelect(false);
        table.setMultiSelectMode(MultiSelectMode.DEFAULT);
        table.setSelectable(true);
        table.setSortEnabled(false);
        table.setColumnReorderingAllowed(false);
        table.setNullSelectionAllowed(false);

        table.setColumnCollapsingAllowed(true);
        table.addColumnResizeListener((Table.ColumnResizeEvent event) -> {
            if (suspendColumnResizeListener) {
                return;
            }
            table.setColumnWidth(event.getPropertyId(), event.getPreviousWidth());
        });
        table.setImmediate(true);
        for (TableColumnHeader header : tableHeaders) {
            table.addContainerProperty(header.getPropertyId(), header.getType(), header.getDefaultValue(), header.getColumnHeader(), header.getColumnIcon(), header.getColumnAlignment());
        }
        table.addHeaderClickListener((Table.HeaderClickEvent event) -> {
            sortTable(event.getPropertyId());
        });

        return table;
    }

    /**
     * Disable table resize option
     */
    public void suspendColumnResizeListener() {
        suspendColumnResizeListener = true;
    }

    /**
     * Enable table resize option
     */
    public void activateColumnResizeListener() {
        suspendColumnResizeListener = false;
    }

    /**
     * Sort table based on specific header
     *
     * @param headerId header id
     */
    public void sortTable(Object headerId) {
        mainTable.setSortEnabled(true);
        if ((mainTable.getSortContainerPropertyId() != null) && headerId.toString().equalsIgnoreCase(mainTable.getSortContainerPropertyId().toString())) {
            mainTable.setSortAscending(!mainTable.isSortAscending());
        } else {
            mainTable.setSortAscending(true);
            mainTable.setSortContainerPropertyId(headerId);
        }
        mainTable.sort();
        int index = 1;
        for (Object key : mainTable.getItemIds()) {
            mainTable.getItem(key).getItemProperty("index").setValue(index++);
        }
        mainTable.setSortEnabled(false);
    }

    /**
     * Get table data items
     *
     * @return map of data items mapped to its id
     */
    public Map<Comparable, Object[]> getTableData() {
        return tableData;
    }

    /**
     * Get table object
     *
     * @return table component
     */
    public Table getMainTable() {
        return mainTable;
    }

    /**
     * Rest table information and data
     */
    public void resetTable() {
        this.tableSearchingMap.clear();
        this.tableData.clear();
        mainTable.removeValueChangeListener(SearchableTable.this);
        mainTable.removeAllItems();
    }

    /**
     * Add item (row) to the table
     *
     * @param dataKey          row id
     * @param value            row data as array
     * @param searchingKeyword keyword to search/select added row
     */
    public void addTableItem(Comparable dataKey, Object[] value, String searchingKeyword) {
        try {
            this.mainTable.addItem(value, dataKey);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            this.mainTable.addItem(value, dataKey);
        }
        this.tableSearchingMap.put(searchingKeyword.toLowerCase().replace(",", "_"), dataKey.toString());

        this.tableData.put(dataKey.toString().trim(), value);

    }

    /**
     * Suspend value change listener
     */
    public void deActivateValueChangeListener() {
        mainTable.removeValueChangeListener(SearchableTable.this);
    }

    /**
     * Activate value change listener
     */
    public void activateValueChangeListener() {
        mainTable.addValueChangeListener(SearchableTable.this);
    }

    /**
     * Update table label (title) with current available data in the table
     */
    public void updateLabel() {
        mainTable.setCaption("<b>" + tableMainTitle + " (" + mainTable.getItemIds().size() + "/" + tableData.size() + ")</b>");
        if (mainTable.getItemIds().size() == 1) {
            mainTable.select(mainTable.getCurrentPageFirstItemId());
        } else {
            mainTable.select(null);
            itemSelected(null);
        }
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        Object objcetId = event.getProperty().getValue();//"P01889";
        itemSelected(objcetId);
    }

    /**
     * Action on select item
     *
     * @param itemId row item id
     */
    public abstract void itemSelected(Object itemId);

    /**
     * Filter table (view only selected items )
     *
     * @param objectIds set of items(row ids)
     */
    public void filterTable(Set<Comparable> objectIds) {
        if ((objectIds == null || objectIds.isEmpty()) && this.mainTable.getItemIds().size() == tableData.size()) {
            return;
        }
        if (objectIds != null && objectIds.size() == mainTable.getItemIds().size()) {
            return;
        }
        mainTable.removeValueChangeListener(SearchableTable.this);
        mainTable.removeAllItems();
        if (objectIds != null && !objectIds.isEmpty()) {
            objectIds.forEach((data) -> {
                if (tableData.containsKey(data)) {
                    this.mainTable.addItem(tableData.get(data), data);
                }
            });
        } else if ((objectIds == null || objectIds.isEmpty()) && this.mainTable.getItemIds().size() != tableData.size()) {
            tableData.keySet().forEach((data) -> {
                this.mainTable.addItem(tableData.get(data), data);
            });

        }
        mainTable.setCaption("<b>" + tableMainTitle + " (" + mainTable.getItemIds().size() + "/" + tableData.size() + ")</b>");
        mainTable.addValueChangeListener(SearchableTable.this);
        if (mainTable.getItemIds().size() == 1) {
            mainTable.select(mainTable.getCurrentPageFirstItemId());
        } else {
            mainTable.select(null);
            itemSelected(null);
        }
        searchBtn.click();

    }

    /**
     * Add button to the table layout
     *
     * @param serviceBtnView button component
     */
    public void addServiceButton(Component serviceBtnView) {
        serachComponent.addComponent(serviceBtnView, 0);
    }

}
