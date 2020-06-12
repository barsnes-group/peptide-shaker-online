package com.uib.web.peptideshaker.presenter.core.form;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This class represent form component (drop down list with caption on the left
 * side)
 *
 * @author Yehia Farag
 */
public class HorizontalLabelTextField extends HorizontalLayout {

    /**
     * Main drop-down list.
     */
    private final TextField textField;
    private String defaultValue;
    private final Label captionLabel;
    public String appliedValue;

    /**
     * Constructor to initialize the main attributes
     *
     * @param caption title
     * @param values the drop-down list values
     */
    public HorizontalLabelTextField(String caption, Object defaultValue, Validator validator) {
        HorizontalLabelTextField.this.setSizeFull();
        HorizontalLabelTextField.this.setSpacing(true);
        captionLabel = new Label(caption);
        captionLabel.setContentMode(ContentMode.HTML);
        captionLabel.addStyleName(ValoTheme.LABEL_TINY);
        captionLabel.addStyleName(ValoTheme.LABEL_SMALL);
        captionLabel.addStyleName("smallundecorated");
        HorizontalLabelTextField.this.addComponent(captionLabel);
        HorizontalLabelTextField.this.setExpandRatio(captionLabel, 45);

        if (defaultValue == null) {
            this.defaultValue = "0";
        } else {
            this.defaultValue = defaultValue.toString();
        }

        textField = new TextField() {

            public String getValue() {
                if (validator instanceof DoubleRangeValidator) {
                    return appliedValue;
                }
                return super.getValue();
            }
        };
        textField.setValidationVisible(true);

        if (validator != null) {
            if (validator instanceof IntegerRangeValidator) {
                textField.setConverter(Integer.class);
            } else if (validator instanceof DoubleRangeValidator) {
                textField.setConverter(Double.class);
                 textField.addTextChangeListener((FieldEvents.TextChangeEvent event) -> {
                     appliedValue=event.getText();
                });

            }
            textField.addValidator(validator);
        }
        textField.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
        textField.setWidth(100, Unit.PERCENTAGE);
        textField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        textField.setNullRepresentation(this.defaultValue);
        textField.setInputPrompt(this.defaultValue);
//        textField.setValue(this.defaultValue);

        textField.setWidth(100, Unit.PERCENTAGE);
        textField.setHeight(20, Unit.PIXELS);
        HorizontalLabelTextField.this.addComponent(textField);
        HorizontalLabelTextField.this.setExpandRatio(textField, 55);

    }

    public void updateExpandingRatio(float first, float secound) {
        HorizontalLabelTextField.this.setExpandRatio(captionLabel, first);
        HorizontalLabelTextField.this.setExpandRatio(textField, secound);
    }

    public void addTextChangeListener(FieldEvents.TextChangeListener listener) {
        textField.addTextChangeListener(listener);
        textField.setTextChangeTimeout(2000);

    }

    public void setRequired(boolean required) {
        this.required = required;
        textField.setRequired(required);
        textField.setRequiredError("Can not be empty");
    }

    private boolean required = true;

    public boolean isValid() {
        textField.setRequired(required);
        boolean check = textField.isValid();
        textField.setRequired(!check);
        return check;

    }

    public boolean isModified() {
        return !textField.getValue().equalsIgnoreCase(textField.getData() + "");
    }

    public void setSelectedValue(Object value) {
        if (value == null || value.toString().equalsIgnoreCase("")) {
            textField.setRequired(false);
            textField.clear();
            required = false;
            return;
        }
        if (textField.getValidators() != null && !textField.getValidators().isEmpty()) {
            Validator v = textField.getValidators().iterator().next();
            if (v instanceof IntegerRangeValidator) {
                textField.setValue(((int) value) + "");
            } else if (v instanceof DoubleRangeValidator) {
                appliedValue = value + "";
                textField.setValue(((double) value) + "");

            } else {
                textField.setValue(value + "");
            }

        } else {
            textField.setValue(value + "");
        }

        textField.setData(value);

    }

    public String getSelectedValue() {
        if (textField.getValue() == null) {
            return defaultValue.replace(" ", "_");

        }
        return textField.getValue().replace(" ", "_");

    }

    public String fullLabelValue() {
        return "<b>" + captionLabel.getValue() + ": </b>" + textField.getValue();

    }

}
