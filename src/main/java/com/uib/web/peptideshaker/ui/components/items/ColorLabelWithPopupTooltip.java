
package com.uib.web.peptideshaker.ui.components.items;

import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;

import java.text.DecimalFormat;

/**
 * This class represent spark-line with colour and pop-up tool-tip
 *
 * @author Yehia Mokhtar Farag
 */
public class ColorLabelWithPopupTooltip extends AbsoluteLayout implements Comparable<ColorLabelWithPopupTooltip> {

    private final Label colorLabel;
    private final Label description;
    private final DecimalFormat df1 = new DecimalFormat("0.00E00");// new DecimalFormat("#.##");
    private double value;
    private Label caption;
    private AbsoluteLayout sparkContainer;

    public ColorLabelWithPopupTooltip(double value, String color) {
        this.value = value;
        ColorLabelWithPopupTooltip.this.setWidth(20, Unit.PIXELS);
        ColorLabelWithPopupTooltip.this.setHeight(12, Unit.PIXELS);
        ColorLabelWithPopupTooltip.this.setStyleName("colorrangelabel");
        colorLabel = new Label("<div style='background:" + color + ";'>", ContentMode.HTML);
        colorLabel.setSizeFull();
        ColorLabelWithPopupTooltip.this.addComponent(colorLabel);

        description = new Label("Intensity: " + value);
        description.setWidthUndefined();
        description.setStyleName("popuptooltip");
        description.setHeight(30, Unit.PIXELS);
        PopupView tooltip = new PopupView("", description) {
            @Override
            public void setVisible(boolean visible) {
                super.setVisible(visible);
                super.setPopupVisible(visible);
            }
        };
        ColorLabelWithPopupTooltip.this.addComponent(tooltip, "left:50%; top:50%");
        tooltip.setVisible(false);
        tooltip.setHideOnMouseOut(false);

        ColorLabelWithPopupTooltip.this.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            tooltip.setVisible(true);
        });

    }

    public ColorLabelWithPopupTooltip(double value, String color, double percentage) {
        this.value = value;
        ColorLabelWithPopupTooltip.this.setSizeFull();
        ColorLabelWithPopupTooltip.this.setStyleName("sparklinecolorrange");

        caption = new Label(df1.format(value));
        caption.setWidth(50, Unit.PIXELS);
        caption.setHeight(20, Unit.PIXELS);
        ColorLabelWithPopupTooltip.this.addComponent(caption);

        sparkContainer = new AbsoluteLayout();
        sparkContainer.setWidth(100, Unit.PERCENTAGE);
        sparkContainer.setHeight(20, Unit.PIXELS);
        ColorLabelWithPopupTooltip.this.addComponent(sparkContainer, "left:55px;");

        colorLabel = new Label("<div style='background:" + color + ";'>", ContentMode.HTML);
        colorLabel.setSizeFull();
        double right = scaleValues(100.0 - percentage, 100, 20);
        sparkContainer.addComponent(colorLabel, "left:0px; right:" + (right) + "%;");

        description = new Label("Intensity: " + value);
        description.setWidthUndefined();
        description.setStyleName("popuptooltip");
        description.setHeight(30, Unit.PIXELS);
        PopupView tooltip = new PopupView("", description) {
            @Override
            public void setVisible(boolean visible) {
                super.setVisible(visible);
                super.setPopupVisible(visible);
            }
        };
        ColorLabelWithPopupTooltip.this.addComponent(tooltip, "left:50%; top:50%");
        tooltip.setVisible(false);
        tooltip.setHideOnMouseOut(false);

        ColorLabelWithPopupTooltip.this.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            tooltip.setVisible(true);
        });

    }

    public void updateValues(double value, String color) {
        this.value = value;
        description.setValue("Intensity: " + value);
        colorLabel.setValue("<div style='background:" + color + ";'>");

    }

    @Override
    public int compareTo(ColorLabelWithPopupTooltip t) {
        return Double.valueOf(this.value).compareTo(t.value);
    }

    public void updateValues(double value, String color, double percentage) {
        this.value = value;
        caption.setValue(df1.format(value));
        description.setValue("Intensity: " + df1.format(value));
        colorLabel.setValue("<div style='background:" + color + ";'>");
        double right = scaleValues(100.0 - percentage, 100, 20);
        sparkContainer.getPosition(colorLabel).setCSSString("left:0px; right:" + (right) + "%;");

    }

    /**
     * Converts the value from linear scale to log scale. The log scale numbers
     * are limited by the range of the type float. The linear scale numbers can
     * be any double value.
     *
     * @param linearValue the value to be converted to log scale
     * @param max         The upper limit number for the input numbers
     * @param lowerLimit  the lower limit for the input numbers
     * @return the value in log scale
     */
    private double scaleValues(double linearValue, double max, double lowerLimit) {

        if (linearValue <= 0) {
            return 0;
        }

        if (linearValue > 85) {
            return 85;
        }
        return linearValue;
//        double logMax = (Math.log(max) / Math.log(2));
//        double logValue = (Math.log(linearValue + 1) / Math.log(2));
//        logValue = (logValue * 2 / logMax) + lowerLimit;
//        return Math.min(logValue, max);
    }

    @Override
    public String toString() {
        return this.value + "";
    }

}
