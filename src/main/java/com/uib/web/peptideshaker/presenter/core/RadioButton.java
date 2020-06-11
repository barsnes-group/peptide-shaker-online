package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represents layout for radio buttons, the layout made for Vaadin
 * table components to be updated on select row in table.
 *
 * @author Yehia Farag
 */
public abstract class RadioButton extends VerticalLayout implements LayoutEvents.LayoutClickListener {

    /**
     * Table item id.
     */
    private final Object itemId;

    /**
     * Constructor to initialise main attributes.
     *
     * @param itemId Table item id.
     */
    public RadioButton(Object itemId) {
        this.itemId = itemId;
        RadioButton.this.setWidth(15, Unit.PIXELS);
        RadioButton.this.setHeight(15, Unit.PIXELS);
        RadioButton.this.addLayoutClickListener(RadioButton.this);
        RadioButton.this.setStyleName("table_radio_btn");
    }

    /**
     * On select button (this layout).
     *
     * @param event click layout event
     */
    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        selectItem(itemId);

    }

    /**
     * On select item (this layout).
     *
     * @param itemId Table item id.
     */
    public abstract void selectItem(Object itemId);

}
