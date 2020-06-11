package com.uib.web.peptideshaker.presenter.core.form;

import com.vaadin.data.Validator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * This class represent form component (drop down list with caption on the left
 * side)
 *
 * @author Yehia Farag
 */
public class HorizontalLabelTextFieldDropdownList extends HorizontalLayout {

    /**
     * First drop-down list.
     */
    private final TextField textField;
    /**
     * Second drop-down list.
     */
    private final ComboBox list2;
    private final String defaultValue;
    private final Label cap ;

    /**
     * Constructor to initialize the main attributes
     *
     * @param caption title
     * @param values the drop-down list values
     */
    public HorizontalLabelTextFieldDropdownList(String title, Object defaultValue, Set<String> values, Validator validator) {

        HorizontalLabelTextFieldDropdownList.this.setSizeFull();
        HorizontalLabelTextFieldDropdownList.this.setSpacing(true);
        cap = new Label(title);
        cap.addStyleName(ValoTheme.LABEL_TINY);
        cap.addStyleName(ValoTheme.LABEL_SMALL);
        cap.addStyleName("smallundecorated");
        HorizontalLabelTextFieldDropdownList.this.addComponent(cap);
        HorizontalLabelTextFieldDropdownList.this.setExpandRatio(cap, 45);

        if (defaultValue == null) {
            this.defaultValue = "0.0";
        } else {
            this.defaultValue = defaultValue.toString();
        }

        if (values == null) {
            values = new HashSet<>();
        }

        textField = new TextField();
        textField.setValidationVisible(true);
        textField.setConverter(Double.class);
        textField.setLocale(Locale.UK);

        textField.addValidator(validator);
        textField.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
        textField.setWidth(100, Unit.PERCENTAGE);
        textField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        textField.setNullRepresentation(this.defaultValue);
        
        textField.setWidth(100, Unit.PERCENTAGE);
        textField.setHeight(20, Unit.PIXELS);
        textField.setValue(this.defaultValue);
        textField.setData(this.defaultValue);
       
        HorizontalLabelTextFieldDropdownList.this.addComponent(textField);
        HorizontalLabelTextFieldDropdownList.this.setExpandRatio(textField, 27.5f);

        list2 = new ComboBox();
        list2.setTextInputAllowed(false);
        list2.setWidth(100, Unit.PERCENTAGE);
        list2.setHeight(20, Unit.PIXELS);
        list2.setStyleName(ValoTheme.COMBOBOX_SMALL);
        list2.addStyleName(ValoTheme.COMBOBOX_TINY);
        list2.addStyleName(ValoTheme.COMBOBOX_ALIGN_CENTER);
//        list.addStyleName("inline-label");
        list2.setNullSelectionAllowed(false);

        for (String str : values) {
            list2.addItem(str);
        }
        list2.setValue(values.toArray()[0]);
        list2.setData(list2.getValue());
        HorizontalLabelTextFieldDropdownList.this.addComponent(list2);
        HorizontalLabelTextFieldDropdownList.this.setExpandRatio(list2, 27.5f);
    }

    public String getFirstSelectedValue() {
        if (textField.getValue() == null) {
            return this.defaultValue;
        
        }
        return textField.getValue();

    }

    public String getSecondSelectedValue() {
        return list2.getValue().toString();

    }

    public void setTextValue(Object value){
        this.textField.setValue(value.toString());
        textField.setData(textField.getValue());
    }
    public void setSelected(Object objectId) {
        list2.select(objectId);
        list2.setData(objectId);

    }

    public boolean isValid() {
        boolean check1 = textField.isValid();

        list2.setRequired(true);
        boolean check2 = list2.isValid();
        list2.setRequired(!check2);
        return check1 && check2;
    }
     public boolean isModified() {
         return (!textField.getValue().equalsIgnoreCase(textField.getData()+"")) || (!list2.getValue().toString().equalsIgnoreCase(list2.getData()+""));
    
    }
     public String fullLabelValue(){
        return "<b>"+cap.getValue()+": </b> <font style='word-spacing: 1px;'>"+textField.getValue()+"_-_ "+list2.getValue()+"</font>";
    
    }

}
