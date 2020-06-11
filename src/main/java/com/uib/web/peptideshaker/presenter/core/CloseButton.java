package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represents close button layout.
 *
 * @author Yehia Farag
 */
public abstract class CloseButton extends VerticalLayout implements LayoutEvents.LayoutClickListener {

    /**
     * Constructor to initialize the layout.
     */
    public CloseButton() {
        CloseButton.this.setWidth(30,Unit.PIXELS);
        CloseButton.this.setHeight(30,Unit.PIXELS);
        CloseButton.this.setStyleName("v-window-closebox");
         CloseButton.this.addStyleName("closeboxbtn");
        CloseButton.this.addLayoutClickListener(CloseButton.this);
    }
    
}
