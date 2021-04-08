package com.uib.web.peptideshaker.ui.components.items;

import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represent coloured based Inference label
 *
 * @author Yehia Mokhtar Farag
 */
public class ProteinInferenceColorLabel extends VerticalLayout implements Comparable<ProteinInferenceColorLabel> {

    private final String[] colorStyles = new String[]{"whitecolor", "greenincolor", "yellowincolor", "orangeincolor", "redincolor"};
    private final Integer colorIndex;
    private final String description;

    public ProteinInferenceColorLabel(int colorIndex, String description) {
        this.colorIndex = colorIndex;
        ProteinInferenceColorLabel.this.setSizeFull();
        ProteinInferenceColorLabel.this.setStyleName("colorlabelfortablecell");
        ProteinInferenceColorLabel.this.addStyleName(colorStyles[colorIndex]);
        ProteinInferenceColorLabel.this.setDescription(description);
        this.description = description;
    }

    public ProteinInferenceColorLabel(int colorIndex, String description, Object data, LayoutEvents.LayoutClickListener listener) {
        this.colorIndex = colorIndex;
        ProteinInferenceColorLabel.this.setSizeFull();
        ProteinInferenceColorLabel.this.setStyleName("colorlabelfortablecell");
        ProteinInferenceColorLabel.this.addStyleName(colorStyles[colorIndex]);
        ProteinInferenceColorLabel.this.setDescription(description);
        ProteinInferenceColorLabel.this.setData(data);
        ProteinInferenceColorLabel.this.addLayoutClickListener(listener);
        this.description = description;
    }

    @Override
    public int compareTo(ProteinInferenceColorLabel t) {
        return this.colorIndex.compareTo(t.colorIndex);
    }

    @Override
    public String toString() {
        return this.description;
    }

}
