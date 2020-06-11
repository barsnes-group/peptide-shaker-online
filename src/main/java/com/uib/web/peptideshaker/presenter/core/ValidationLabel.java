package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Valid or not valid label
 *
 * @author Yehia Farag
 */
public class ValidationLabel extends VerticalLayout implements Comparable<ValidationLabel> {

    private final Label icon;
    private final Integer sortIndex;
    private final String validation;

    /**
     * Initialise label
     *
     * @param validation valid label
     */
    public ValidationLabel(String validation) {
        ValidationLabel.this.setSizeFull();
        this.validation = validation;
        this.icon = new Label();
        this.icon.setWidth(16, Unit.PIXELS);
        this.icon.setHeight(16, Unit.PIXELS);
        this.icon.setContentMode(ContentMode.HTML);
        ValidationLabel.this.addComponent(icon);
        ValidationLabel.this.setComponentAlignment(icon, Alignment.TOP_CENTER);
        if (validation.trim().equalsIgnoreCase("confident")) {
            ValidationLabel.this.setStyleName("validlabel");
            this.icon.setValue("<center style='padding-top: 5px;'>" + VaadinIcons.CHECK_CIRCLE.getHtml() + "</center>");
            sortIndex = 0;

        } else if (validation.trim().equalsIgnoreCase("doubtful")) {
            ValidationLabel.this.setStyleName("doubtfullabel");
            this.icon.setValue("<center style='padding-top: 5px;'>" + VaadinIcons.WARNING.getHtml() + "</center>");
            sortIndex = 1;

        } else {
            ValidationLabel.this.setStyleName("notvalidlabel");
            this.icon.setValue("<center style='padding-top: 5px;'>" + VaadinIcons.CLOSE_CIRCLE.getHtml() + "</center>");
            sortIndex = 2;
        }
        ValidationLabel.this.setDescription(validation);

    }

    /**
     * Override compare to method to allow sort on the item
     *
     * @param validationLabelToCompare label to compare with
     * @return comparison results
     */
    @Override
    public int compareTo(ValidationLabel validationLabelToCompare) {
        return validationLabelToCompare.sortIndex.compareTo(sortIndex);
    }

    @Override
    public String toString() {
        return this.validation;
    }
}
