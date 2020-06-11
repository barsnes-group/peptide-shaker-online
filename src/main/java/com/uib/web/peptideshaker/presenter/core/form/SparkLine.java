package com.uib.web.peptideshaker.presenter.core.form;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.awt.Color;
import java.text.DecimalFormat;

/**
 * This class represent sparkLine
 *
 * @author Yehia Farag
 */
public class SparkLine extends HorizontalLayout {

    private final DecimalFormat df = new DecimalFormat("0.00E00");// new DecimalFormat("#.##");
    private ColorLabel spark;
    private Label textLabel;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (spark == null) {
            return;
        }
        if (selected) {
            textLabel.addStyleName("selected");
//            spark.updateColor(new Color(25, 125, 225));
        } else {
            textLabel.removeStyleName("selected");
//            spark.updateColor(Color.lightGray);
        }

    }
    private boolean selected;

    /**
     * Constructor to initialize the spark-line class
     */
    public SparkLine(double value, double min, double max) {

        SparkLine.this.setWidth(100, Unit.PERCENTAGE);
        SparkLine.this.setHeight(10, Unit.PIXELS);
        Label label = new Label(df.format(value));
        label.setStyleName("smalltable");
        SparkLine.this.setSpacing(true);
        SparkLine.this.addComponent(label);
        SparkLine.this.setComponentAlignment(label, Alignment.TOP_LEFT);
        Color selectedColor;

        double factor = Math.max(max, Math.abs(min));
        factor = Math.log10((Double) factor);
        if (value > 0) {
            value = Math.log10((Double) value);

            value = value * 100 / factor;
            selectedColor = Color.RED;
        } else if (value < 0) {
            value = Math.log10((Double) value * -1.0);
            value = value * 100 / factor;
            selectedColor = Color.BLUE;
        } else {
            return;
        }

        spark = new ColorLabel(selectedColor);
        spark.setHeight(60, Unit.PERCENTAGE);
        spark.setWidth(Math.min(Math.max(Math.round(value), 5), 100), Unit.PERCENTAGE);
        SparkLine.this.addComponent(spark);
        SparkLine.this.setComponentAlignment(spark, Alignment.TOP_LEFT);

    }

    /**
     * Constructor to initialise the spark-line class
     */
    public SparkLine(String text, double value, double min, double max, Color color) {

        SparkLine.this.setWidth(100, Unit.PERCENTAGE);
        SparkLine.this.setHeight(20, Unit.PIXELS);
        SparkLine.this.setStyleName("margintop-5");
        textLabel = new Label(text);
        textLabel.setWidth(100, Unit.PERCENTAGE);
        textLabel.setHeight(100, Unit.PERCENTAGE);
        textLabel.setStyleName("smalltable");
        textLabel.addStyleName("sparklabel");
//        SparkLine.this.setSpacing(true);

        double factor = Math.max(max, Math.abs(min));
        factor = Math.log10((Double) factor);
        if (value > 0) {
            value = Math.log10((Double) value);
            value = value * 100 / factor;
//            selectedColor = Color.LIGHT_GRAY;
        } else if (value < 0) {
            value = Math.log10((Double) value * -1.0);
            value = value * 100 / factor;
//            selectedColor = Color.BLUE;
        } else if (max != -100000) {
            Label empty = new Label();
            SparkLine.this.addComponent(empty);
             SparkLine.this.setExpandRatio(empty, 20);
            SparkLine.this.addComponent(textLabel);
            SparkLine.this.setComponentAlignment(textLabel, Alignment.MIDDLE_LEFT);
            SparkLine.this.setExpandRatio(textLabel, 80);
            return;
        }

        spark = new ColorLabel(color);
        spark.setHeight(5, Unit.PIXELS);
//        spark.addStyleName("margintop5");
        spark.setWidth(Math.min(Math.max(Math.round(value), 5), 100), Unit.PERCENTAGE);
        spark.addStyleName("maxwidth150");

//        Label colorLegend = new Label("<div style='margin-left: 0px;width: 10px;height: 10px;margin-top: 0px;border: 1px solid lightgray;background: rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");'></div>",ContentMode.HTML);
        if (max == -100000) {
            SparkLine.this.setWidthUndefined();
//            colorLegend.setWidth(13,Unit.PIXELS);
//            colorLegend.setHeight(13,Unit.PIXELS);
            SparkLine.this.setSpacing(true);
//            SparkLine.this.addComponent(colorLegend);
//            SparkLine.this.setComponentAlignment(colorLegend, Alignment.MIDDLE_LEFT);
            SparkLine.this.addComponent(textLabel);
            SparkLine.this.setComponentAlignment(textLabel, Alignment.MIDDLE_LEFT);
            SparkLine.this.setExpandRatio(textLabel, 90);
//            SparkLine.this.setExpandRatio(colorLegend, 10);
        } else {
            VerticalLayout spacer = new VerticalLayout();
            spacer.setSizeFull();
            SparkLine.this.addComponent(spacer);
            SparkLine.this.setComponentAlignment(spacer, Alignment.MIDDLE_RIGHT);
            SparkLine.this.setExpandRatio(spacer, 0.2f);

            SparkLine.this.addComponent(spark);
            SparkLine.this.setComponentAlignment(spark, Alignment.MIDDLE_RIGHT);
            SparkLine.this.setExpandRatio(spark, 1.8f);
            SparkLine.this.addComponent(textLabel);
            SparkLine.this.setComponentAlignment(textLabel, Alignment.MIDDLE_LEFT);
            SparkLine.this.setExpandRatio(textLabel, 8);
//            SparkLine.this.addComponent(colorLegend);
//            SparkLine.this.setComponentAlignment(colorLegend, Alignment.MIDDLE_LEFT);
//            SparkLine.this.setExpandRatio(colorLegend, 0.1f);
        }

    }

}
