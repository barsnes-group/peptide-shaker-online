package com.uib.web.peptideshaker.ui.components.items;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represent form component (drop down list with caption on the left
 * side)
 *
 * @author Yehia Mokhtar Farag
 */
public class HorizontalLabelDropDownList extends HorizontalLayout {

    /**
     * Main drop-down list.
     */
    private final ComboBox list;
    private final Label cap;

    /**
     * Constructor to initialize the main attributes
     *
     * @param caption title
     * @param values  the drop-down list values
     */
    public HorizontalLabelDropDownList(String caption) {
        HorizontalLabelDropDownList.this.setSizeFull();
        HorizontalLabelDropDownList.this.setSpacing(true);
        cap = new Label(caption, ContentMode.HTML);
        cap.addStyleName(ValoTheme.LABEL_TINY);
        cap.addStyleName(ValoTheme.LABEL_SMALL);
        cap.addStyleName("smallundecorated");
        HorizontalLabelDropDownList.this.addComponent(cap);
        HorizontalLabelDropDownList.this.setExpandRatio(cap, 45);

        list = new ComboBox();
        list.setTextInputAllowed(false);
        list.setWidth(100, Unit.PERCENTAGE);
        list.setHeight(20, Unit.PIXELS);
        list.setStyleName(ValoTheme.COMBOBOX_SMALL);
        list.addStyleName(ValoTheme.COMBOBOX_TINY);
        list.addStyleName(ValoTheme.COMBOBOX_ALIGN_CENTER);
        list.setNullSelectionAllowed(false);
        list.setImmediate(true);

        HorizontalLabelDropDownList.this.addComponent(list);
        HorizontalLabelDropDownList.this.setExpandRatio(list, 55);
    }

    public void updateData(Set<String> values) {
        if (values == null) {
            values = new HashSet<>();
        }
        if (values.isEmpty()) {
            values.add("N/A");
        }
        for (String str : values) {
            list.addItem(str);
        }
        list.setValue(values.toArray()[0]);
        list.commit();
    }

    public String getSelectedValue() {
        return list.getValue().toString();

    }

    public void setItemCaption(Object itemId, String caption) {

        list.setItemCaption(itemId, caption);
    }

    public void addValueChangeListener(Property.ValueChangeListener listener) {
        this.list.addValueChangeListener(listener);
    }

    public void setSelected(Object objectId) {
        list.select(objectId);
        list.setData(objectId);

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

    public String fullLabelValue() {
        return "<b>" + cap.getValue() + ": </b>" + list.getValue();

    }

    public void updateExpandingRatio(float first, float secound) {
        HorizontalLabelDropDownList.this.setExpandRatio(cap, first);
        HorizontalLabelDropDownList.this.setExpandRatio(list, secound);
    }

}
