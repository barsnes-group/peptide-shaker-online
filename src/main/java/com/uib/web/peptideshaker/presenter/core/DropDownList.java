package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.data.Property;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import java.util.Map;

/**
 * This class represents drop-down list component
 *
 * @author Yehia Farag
 */
public class DropDownList extends AbsoluteLayout {

    /**
     * Drop down list component.
     */
    private final ComboBox list;
    private Property.ValueChangeListener listener;
    private Object lastSelectedId;
    private final VerticalLayout container ;

    /**
     * Constructor to initialise the main attributes.
     *
     * @param title the title of the list
     */
    public DropDownList(String title) {
        
       
        DropDownList.this.setSizeFull();
        DropDownList.this.setStyleName("dropdownlistframe");
 
        container = new VerticalLayout();
        container.setSizeFull();
        container.setStyleName("dropdownlistframe");
        DropDownList.this.addComponent(container);
        list = new ComboBox(title);
        list.setStyleName("dropdownlist");
        list.setInputPrompt("Please select from the list");
        list.setNullSelectionAllowed(false);
       container.addComponent(list);
        list.setTextInputAllowed(false);

    }
    public void addUploadBtn(PopupView uploader){
         DropDownList.this.removeAllComponents();
     DropDownList.this.addComponent(container,"right:36px;");
     DropDownList.this.addComponent(uploader,"right: 24px;top: 4px;");
    
    }

    /**
     * Set the list is required to have a value.
     *
     * @param required the selection is required
     * @param requiredMessage the error appear if no data selected
     */
    public void setRequired(boolean required, String requiredMessage) {
        list.setRequired(required);
        list.setRequiredError(requiredMessage);

    }

    public void setFocous() {
        this.addStyleName("focos");
    }

    /**
     * Update the drop down list
     *
     * @param idToCaptionMap list of ids and names
     */
    public void updateList(Map<String, String> idToCaptionMap) {
        if (listener != null) {
            list.removeValueChangeListener(listener);
        }
        list.removeAllItems();
        list.clear();
        for (String id : idToCaptionMap.keySet()) {
            list.addItem(id);
            list.setItemCaption(id, idToCaptionMap.get(id));
//            list.setValue(id);
        }
        if (listener != null) {
            list.addValueChangeListener(listener);
        }
        if (!list.getItemIds().isEmpty()) {
            this.removeStyleName("focos");
        }
    }

    public void setItemIcon(Object itemId, Resource icon) {
        list.setItemIcon(itemId, icon);
    }

    /**
     * Get selection value
     *
     * @return String id of the selected item
     */
    public String getSelectedValue() {
        list.removeStyleName("error");
        if (list.isValid()) {
            return list.getValue() + "";
        }
        list.addStyleName("error");
        return null;
    }

    public void addValueChangeListener(Property.ValueChangeListener listener) {
        this.listener = listener;
        this.list.addValueChangeListener(listener);
    }

    public void setSelected(Object objectId) {
        if (objectId == null || (objectId + "").equalsIgnoreCase("") || list == null || !list.getItemIds().contains(objectId)) {
            return;
        }
        try {
            list.select(objectId);
            list.setData(list.getValue());
            lastSelectedId = objectId;
        } catch (Exception e) {

        Page.getCurrent().reload();
        }

    }

    public void defultSelect() {
        list.setNullSelectionAllowed(lastSelectedId == null);
        list.select(lastSelectedId);
    }

    public Object getLastSelectedId() {
        return lastSelectedId;
    }

    public void addNewItemHandler(AbstractSelect.NewItemHandler newItemHandler, String message) {
        this.list.setDescription("Select or Enter New");
        this.list.setInputPrompt(message);
        this.list.setNewItemsAllowed(true);
        this.list.setNewItemHandler(newItemHandler);
    }

    public void addItem(String itemId) {
        if (listener != null) {
            list.removeValueChangeListener(listener);
        }
        this.list.addItem(itemId);
        this.list.select(itemId);
        if (listener != null) {
            list.addValueChangeListener(listener);
        }

    }

    public boolean isValid() {
        list.setRequired(true);
        boolean check = list.isValid();
        list.setRequired(!check);
        return check;
    }

    public boolean isModified() {
        return !list.getValue().toString().equalsIgnoreCase(list.getData() + "");
    }

}
