package com.uib.web.peptideshaker.presenter.core.form;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represent form component (drop down list with caption on the left
 * side)
 *
 * @author Yehia Farag
 */
public class HorizontalLabel2DropdownList extends HorizontalLayout {

    /**
     * First drop-down list.
     */
    private final ComboBox list1;
    /**
     * Second drop-down list.
     */
    private final ComboBox list2;
    private final Label cap;

    /**
     * Constructor to initialize the main attributes
     *
     * @param caption title
     * @param values the drop-down list values
     */
    public HorizontalLabel2DropdownList(String title, Set<String> values, Set<String> values2) {

        HorizontalLabel2DropdownList.this.setSizeFull();
         HorizontalLabel2DropdownList.this.setSpacing(true);
         cap = new Label(title);
        cap.addStyleName(ValoTheme.LABEL_TINY);
        cap.addStyleName(ValoTheme.LABEL_SMALL);
        cap.addStyleName("smallundecorated");
        HorizontalLabel2DropdownList.this.addComponent(cap);
        HorizontalLabel2DropdownList.this.setExpandRatio(cap, 47);

        if (values == null) {
            values = new HashSet<>();
        }
        if (values.isEmpty()) {
            values.add("N/A");
        }
        if (values2 == null) {
            values2 = new HashSet<>();
        }
        if (values2.isEmpty()) {
            values2.add("N/A");
        }
        list1 = new ComboBox();
        list1.setWidth(100, Unit.PERCENTAGE);
        list1.setHeight(20, Unit.PIXELS);
        list1.setStyleName(ValoTheme.COMBOBOX_SMALL);
        list1.addStyleName(ValoTheme.COMBOBOX_TINY);
        list1.addStyleName(ValoTheme.COMBOBOX_ALIGN_CENTER);
        list1.setTextInputAllowed(false);
//        list.addStyleName("inline-label");
        list1.setNullSelectionAllowed(false);
        for (String str : values) {
            list1.addItem(str);
        }
        list1.setValue(values.toArray()[0]);

        HorizontalLabel2DropdownList.this.addComponent(list1);
        HorizontalLabel2DropdownList.this.setExpandRatio(list1, 26.5f);

        list2 = new ComboBox();
        list2.setTextInputAllowed(false);
        list2.setWidth(100, Unit.PERCENTAGE);
        list2.setHeight(20, Unit.PIXELS);
        list2.setStyleName(ValoTheme.COMBOBOX_SMALL);
        list2.addStyleName(ValoTheme.COMBOBOX_TINY);
        list2.addStyleName(ValoTheme.COMBOBOX_ALIGN_CENTER);
//        list.addStyleName("inline-label");
        list2.setNullSelectionAllowed(false);

        for (String str : values2) {
            list2.addItem(str);
        }
        list2.setValue(values2.toArray()[0]);
        HorizontalLabel2DropdownList.this.addComponent(list2);
        HorizontalLabel2DropdownList.this.setExpandRatio(list2, 26.5f);
    }

    public String getFirstSelectedValue() {
        return list1.getValue().toString();

    }

    public String getSecondSelectedValue() {
        return list2.getValue().toString();

    }

    public void setSelectedI(Object objectId) {
        list1.select(objectId);
        list1.setData(objectId);

    }

    public void setSelectedII(Object objectId) {
        list2.select(objectId);
        list2.setData(objectId);
    }

    public boolean isValid() {
        list1.setRequired(true);
        boolean check1 = list1.isValid();
        list1.setRequired(!check1);

        list2.setRequired(true);
        boolean check2 = list2.isValid();
        list2.setRequired(!check2);
        return check1 && check2;
    }

    public boolean isModified() {

        return (!list1.getValue().toString().equalsIgnoreCase(list1.getData()+"")) || (!list2.getValue().toString().equalsIgnoreCase(list2.getData()+""));
    }
    public String fullLabelValue(){
        return "<b>"+cap.getValue()+": </b><font style='word-spacing: 1px;'>"+list1.getValue()+"_-_"+list2.getValue()+"</font>";
    
    }
}
