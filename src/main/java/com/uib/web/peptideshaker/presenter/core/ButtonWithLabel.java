package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This class represent the top right small button
 *
 * @author Yehia Farag
 */
public class ButtonWithLabel extends HorizontalLayout {

    private Image btnThumbIconImage;
    private final Label iconLabel;
    private final int btnId;
    private final Label textLabel;

    public ButtonWithLabel(String text, int btnId) {
        ButtonWithLabel.this.setHeight(80, Unit.PIXELS);
        ButtonWithLabel.this.setWidthUndefined();
        ButtonWithLabel.this.setSpacing(true);
        ButtonWithLabel.this.addStyleName("btnwithtext");
        btnThumbIconImage = new Image() {
            @Override
            public void setSource(Resource source) {
                ButtonWithLabel.this.setVisible((source != null));
                if (source == null || btnThumbIconImage.getSource() == source) {
                    return;
                }
                btnThumbIconImage.setVisible(true);
                iconLabel.setVisible(false);
                super.setSource(source);
            }
        };

        btnThumbIconImage.setSizeFull();
        this.iconLabel = new Label();
        this.iconLabel.setSizeFull();
        this.iconLabel.setContentMode(ContentMode.HTML);
        this.iconLabel.setStyleName("iconimage");
        this.btnId = btnId;

        ButtonWithLabel.this.addComponent(btnThumbIconImage);
        ButtonWithLabel.this.setComponentAlignment(btnThumbIconImage, Alignment.MIDDLE_CENTER);
        ButtonWithLabel.this.addComponent(iconLabel);
        ButtonWithLabel.this.setComponentAlignment(iconLabel, Alignment.MIDDLE_CENTER);

        textLabel = new Label(text, ContentMode.HTML);
        textLabel.setStyleName(ValoTheme.LABEL_TINY);
        textLabel.addStyleName(ValoTheme.LABEL_BOLD);
        ButtonWithLabel.this.addComponent(textLabel);
        ButtonWithLabel.this.setComponentAlignment(textLabel, Alignment.MIDDLE_LEFT);
    }

    public void updateText(String text) {
        textLabel.setValue(text);

    }

    public Image getBtnThumbIconImage() {
        return btnThumbIconImage;
    }

    public int getBtnId() {
        return btnId;
    }

    @Override
    public void setData(Object data) {
        super.setData(data); //To change body of generated methods, choose Tools | Templates.
        this.iconLabel.setData(data);
        this.btnThumbIconImage.setData(data);
        this.textLabel.setData(data);

    }

    public void updateIconResource(Resource imageURL) {
        btnThumbIconImage.setSource(imageURL);
        iconLabel.setVisible(false);
    }

    public void updateIcon(String HTML) {
        this.setVisible((HTML != null));
        if (HTML == null) {
            return;
        }
        btnThumbIconImage.setVisible(false);
        iconLabel.setVisible(true);
        iconLabel.setValue(HTML);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            ButtonWithLabel.this.addStyleName("selectedbiglbtn");
        } else {
            ButtonWithLabel.this.removeStyleName("selectedbiglbtn");
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            this.removeStyleName("hide");
        } else {
            this.addStyleName("hide");
        }
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        super.addLayoutClickListener(listener); //To change body of generated methods, choose Tools | Templates.
    }

}
