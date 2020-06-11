package com.uib.web.peptideshaker.presenter.core.form;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import java.awt.Color;

/**
 * This class represents color label
 *
 * @author Yehia Farag
 */
public class ColorLabel extends Label {

    private final String rgbColor;

    /**
     * Constructor to initialize the label color
     */
    public ColorLabel(Color color) {
        ColorLabel.this.setContentMode(ContentMode.HTML);
        ColorLabel.this.setWidth(100, Unit.PERCENTAGE);
        ColorLabel.this.setHeight(15, Unit.PIXELS);
        rgbColor = "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
        ColorLabel.this.setValue("<div style='background:rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");width: 100% !important;height: 100%!important;'></div>");
    }

    /**
     * Constructor to initialize the label color
     */
    public ColorLabel(int r, int g, int b) {
        ColorLabel.this.setContentMode(ContentMode.HTML);
        ColorLabel.this.setWidth(100, Unit.PERCENTAGE);
        ColorLabel.this.setHeight(15, Unit.PIXELS);
        rgbColor = "rgb(" + r + "," + g + "," + b + ")";
        ColorLabel.this.setValue("<div style='background:rgb(" + r + "," + g + "," + b + "); min-width: 20px;min-height:15px !important;height: 100%!important;'></div>");
    }

    public void updateColor(Color newColor) {
        ColorLabel.this.setValue("<div style='background:rgb(" + newColor.getRed() + "," + newColor.getGreen() + "," + newColor.getBlue() + ");width: 100% !important;height: 100%!important;'></div>");

    }

    public String getRGBColorAsString() {
        return rgbColor;
    }

}
