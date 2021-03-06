
package com.uib.web.peptideshaker.ui.components.items;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represents bar with label on top of it
 *
 * @author Yehia Mokhtar Farag
 */
public class HBarWithLabel extends AbsoluteLayout {
    private final int barValue;

    public HBarWithLabel(String labelText, int barValue) {
        HBarWithLabel.this.setStyleName("barwlabel");
        this.barValue = barValue;
        Label l = new Label("<font>" + labelText + "</font>", ContentMode.HTML);
        HBarWithLabel.this.addComponent(l, "top:10px;left:0px");
        l.setHeight(12, Unit.PIXELS);
        l.setWidth(100, Unit.PERCENTAGE);
        VerticalLayout bar = new VerticalLayout();
        bar.setStyleName("barlayout");
        bar.addStyleName("selectablenode");
        bar.addStyleName("bordermarker");
        bar.setSizeFull();
        HBarWithLabel.this.addComponent(bar, "top:30px;left:0px");

    }

    public int getBarValue() {
        return barValue;
    }

}
