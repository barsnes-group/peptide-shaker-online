package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

/**
 * This class represent the top right small button
 *
 * @author Yehia Farag
 */
public class MobileSideBtn extends HorizontalLayout {

    private final Image iconImage;
    private final Label iconLabel;

    public MobileSideBtn(String text) {
        iconImage = new Image();
        iconImage.setSizeFull();
        MobileSideBtn.this.addComponent(iconImage);
        MobileSideBtn.this.setComponentAlignment(iconImage, Alignment.MIDDLE_CENTER);
        iconLabel = new Label();
        iconLabel.setSizeFull();
        iconLabel.setContentMode(ContentMode.HTML);
        MobileSideBtn.this.addComponent(iconLabel);
        MobileSideBtn.this.setComponentAlignment(iconLabel, Alignment.MIDDLE_CENTER);

        MobileSideBtn.this.setSizeFull();
        MobileSideBtn.this.setStyleName("bigmenubtn");
        MobileSideBtn.this.addStyleName("zeropadding");

    }

    public void updateIconResource(Resource imageURL) {
        this.setVisible((imageURL == null));
        if (imageURL == null) {
            return;
        }
        iconImage.setVisible(true);
        iconLabel.setVisible(false);
        iconImage.setSource(imageURL);

    }

    public void updateIconHTML(String HTML) {
        this.setVisible((HTML != null));
        if (HTML == null || iconLabel.getValue().equalsIgnoreCase(HTML)) {
            return;
        }
        iconImage.setVisible(false);
        iconLabel.setVisible(true);
        iconLabel.setValue(HTML);
        if (this.getStyleName().contains("reshake")) {
            this.removeStyleName("reshake");
            this.addStyleName("shake");
        } else {
            this.removeStyleName("shake");
            this.addStyleName("reshake");;
        }
    }

    public void setSelected(boolean selected) {
        if (selected) {
            MobileSideBtn.this.addStyleName("selectedbiglbtn");
        } else {
            MobileSideBtn.this.removeStyleName("selectedbiglbtn");
        }
    }

}
