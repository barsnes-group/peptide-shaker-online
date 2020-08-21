package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.util.Map;

/**
 * This class represent Spark-line label to be used inside tables the spark line
 * support staked barchart with 1,2 or 3 colours
 *
 * @author Yehia Farag
 */
public abstract class SparkLineLabel extends AbsoluteLayout implements Comparable<SparkLineLabel> {

    private final Label textLabel;

    /**
     * Initialise spark line component
     *
     * @param labelValue spark line label
     * @param values     spark line data
     * @param itemId     spark line id
     */
    public SparkLineLabel(String labelValue, Map<String, Number> values, Object itemId) {
        SparkLineLabel.this.setSizeFull();
        SparkLineLabel.this.setStyleName("sparkline");
        textLabel = new Label(labelValue, ContentMode.HTML);
        textLabel.setStyleName("sparklinelabel");
        SparkLineLabel.this.addComponent(textLabel, "left:5px;top:0px");
        HorizontalLayout sparkLineContainer = initSparkLine(values);
        SparkLineLabel.this.addComponent(sparkLineContainer, "left:45px;top:0px");
        SparkLineLabel.this.addLayoutClickListener((event) -> {
            selected(itemId);
        });

    }

    private HorizontalLayout initSparkLine(Map<String, Number> values) {
        HorizontalLayout container = new HorizontalLayout();
        container.setSizeFull();
        container.setStyleName("sparklinecontainer");
        float left = 1;
        for (String style : values.keySet()) {
            VerticalLayout dataLayout = new VerticalLayout();
            dataLayout.setStyleName(style);
            dataLayout.setSizeFull();
            container.addComponent(dataLayout);
            container.setExpandRatio(dataLayout, Math.max((float) values.get(style), 0f));
            left = left - (float) values.get(style);

        }
        if (left > 0) {
            VerticalLayout leftLayout = new VerticalLayout();
            leftLayout.setSizeFull();
            container.addComponent(leftLayout);
            container.setExpandRatio(leftLayout, left);
        }

        return container;
    }

    /**
     * On-Select spark line object
     *
     * @param itemId spark line item id
     */
    public abstract void selected(Object itemId);

    /**
     * compare to method to allow sorting the item based on the label value
     *
     * @param sparklineToCompare spark line item
     * @return comparison results
     */
    @Override
    public int compareTo(SparkLineLabel sparklineToCompare) {
        return Double.valueOf(textLabel.getValue()).compareTo(Double.valueOf(sparklineToCompare.textLabel.getValue()));
    }

    @Override
    public String toString() {
        return textLabel.getValue();
    }

}
