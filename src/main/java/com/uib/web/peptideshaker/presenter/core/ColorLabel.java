package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represent coloured based Inference label
 *
 * @author Yehia Farag
 */
public class ColorLabel extends VerticalLayout implements Comparable<ColorLabel> {

    private final String[] colorStyles = new String[]{"whitecolor", "greenincolor", "yellowincolor", "orangeincolor", "redincolor"};
    private final Integer colorIndex;
    private final String description;

    public ColorLabel(int colorIndex, String description) {
        this.colorIndex = colorIndex;
        ColorLabel.this.setSizeFull();
        ColorLabel.this.setStyleName("colorlabelfortablecell");
        ColorLabel.this.addStyleName(colorStyles[colorIndex]);
        ColorLabel.this.setDescription(description);
        this.description = description;
    }

    public ColorLabel(int colorIndex, String description, Object data, LayoutEvents.LayoutClickListener listener) {
        this.colorIndex = colorIndex;
        ColorLabel.this.setSizeFull();
        ColorLabel.this.setStyleName("colorlabelfortablecell");
        ColorLabel.this.addStyleName(colorStyles[colorIndex]);
        ColorLabel.this.setDescription(description);
        ColorLabel.this.setData(data);
        ColorLabel.this.addLayoutClickListener(listener);
           this.description = description;
    }

    @Override
    public int compareTo(ColorLabel t) {
        return this.colorIndex.compareTo(t.colorIndex);
    }

    @Override
    public String toString() {
        return this.description;
    }

}
