package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This class represents Label with link that support action
 *
 * @author Yehia Farag
 */
public abstract class ActionLabel extends HorizontalLayout implements LayoutEvents.LayoutClickListener {

    private Label label;

    /**
     * Initialise label
     *
     * @param text label text
     * @param url  web link to go on click
     */
    public ActionLabel(String text, Resource url) {
        ActionLabel.this.setSizeUndefined();
        Link linkLabel = new Link();
        linkLabel.setResource(url);
        linkLabel.setCaption(text);
        ActionLabel.this.setStyleName("actionlabel");
        ActionLabel.this.setDescription(text);
        linkLabel.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        linkLabel.setDescription(text);
        linkLabel.setTargetName("_blank");
        linkLabel.setStyleName(ValoTheme.LINK_SMALL);
        ActionLabel.this.addComponent(linkLabel);
        ActionLabel.this.setComponentAlignment(linkLabel, Alignment.TOP_CENTER);

    }

    /**
     * Initialise label
     *
     * @param icon resource icon
     * @param text label text
     */
    public ActionLabel(Resource icon, String text) {
        ActionLabel.this.setSizeUndefined();
        label = new Label();
        ActionLabel.this.setStyleName("actionlabel");
        ActionLabel.this.setDescription(text);
        label.setIcon(icon);
        label.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        label.setDescription(text);
        ActionLabel.this.addComponent(label);
        ActionLabel.this.setComponentAlignment(label, Alignment.TOP_CENTER);
        ActionLabel.this.addLayoutClickListener(ActionLabel.this);

    }

    /**
     * Initialise label
     *
     * @param icon        resource icon
     * @param name        label text
     * @param description label tool-tip
     */
    public ActionLabel(Resource icon, String name, String description) {
        ActionLabel.this.setSizeFull();
        ActionLabel.this.setDescription(description);
        HorizontalLayout wrapper = new HorizontalLayout();
        wrapper.setWidthUndefined();
        wrapper.setHeight(100, Unit.PERCENTAGE);
        wrapper.setStyleName("actionlabel");
        wrapper.setSpacing(true);
        ActionLabel.this.addComponent(wrapper);
        Label iconLabel = new Label();
        iconLabel.setSizeFull();
        iconLabel.setIcon(icon);
        wrapper.addComponent(iconLabel);
        wrapper.setComponentAlignment(iconLabel, Alignment.MIDDLE_LEFT);
        label = new Label(name, ContentMode.HTML);
        label.setSizeFull();
        wrapper.addComponent(label);
        ActionLabel.this.addLayoutClickListener(ActionLabel.this);

    }

    public void setdownloadLink(String url, String fileName) {
        label.setContentMode(ContentMode.HTML);
        label.setValue("<a href='" + url + "' download='" + fileName + "' target='_blank' > to hide text </a>");
        label.setStyleName(ValoTheme.BUTTON_SMALL);
//        label.setIcon(null);

    }

    /**
     * Get label value
     *
     * @return label value
     */
    public String getLabelValue() {
        return label.getValue();
    }

    /**
     * Update label title
     *
     * @param updatedTitle new label value
     */
    public void updateLabelTitle(String updatedTitle) {
        label.setValue(updatedTitle);
    }

}
