package com.uib.web.peptideshaker.presenter.core.filtercharts.components;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

import java.awt.*;

/**
 * This class represents legend item can be use with any of the charts
 *
 * @author Yehia Farag
 */
public class LegendItem extends Label {

    /**
     * Initialise legend item
     *
     * @param text  legend label and tool-tip
     * @param color legend colour
     */
    public LegendItem(String text, Color color) {
        LegendItem.this.setSizeFull();
        LegendItem.this.setValue("<font style='color:rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")'>" + FontAwesome.SQUARE.getHtml() + "</font> " + text.replace("Protein", ""));
        LegendItem.this.setStyleName("legenditem");
        LegendItem.this.setContentMode(ContentMode.HTML);
        LegendItem.this.setDescription(text);

    }

}
