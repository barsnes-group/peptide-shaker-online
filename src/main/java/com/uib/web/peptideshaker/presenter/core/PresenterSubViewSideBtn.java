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
public class PresenterSubViewSideBtn extends HorizontalLayout {

    private Image btnThumbIconImage;
    private final Label iconLabel;
    private final int btnId;

    /**
     * Initialise side button view
     *
     * @param text buttons text
     * @param btnId buttons id
     */
    public PresenterSubViewSideBtn(String text, int btnId) {
        btnThumbIconImage = new Image() {
            @Override
            public void setSource(Resource source) {
                PresenterSubViewSideBtn.this.setVisible((source != null));
                if (source == null || btnThumbIconImage.getSource() == source) {
                    return;
                }
                btnThumbIconImage.setVisible(true);
                iconLabel.setVisible(false);
                super.setSource(source);
            }

        };
        btnThumbIconImage.setWidth(100, Unit.PERCENTAGE);
        btnThumbIconImage.setHeight(60, Unit.PERCENTAGE);
        this.iconLabel = new Label();
        this.iconLabel.setSizeFull();
        this.iconLabel.setContentMode(ContentMode.HTML);
        this.btnId = btnId;
        PresenterSubViewSideBtn.this.setHeight(100, Unit.PIXELS);
        PresenterSubViewSideBtn.this.setWidth(100, Unit.PIXELS);
        PresenterSubViewSideBtn.this.addComponent(btnThumbIconImage);
        PresenterSubViewSideBtn.this.setComponentAlignment(btnThumbIconImage, Alignment.MIDDLE_CENTER);
        PresenterSubViewSideBtn.this.setExpandRatio(btnThumbIconImage, 1);
        PresenterSubViewSideBtn.this.addComponent(iconLabel);
        PresenterSubViewSideBtn.this.setComponentAlignment(iconLabel, Alignment.MIDDLE_CENTER);
        PresenterSubViewSideBtn.this.setExpandRatio(iconLabel, 1);
        PresenterSubViewSideBtn.this.setSizeFull();
        PresenterSubViewSideBtn.this.setStyleName("bigmenubtn");

    }

    /**
     * Get the button icon as image object
     *
     * @return image
     */
    public Image getBtnThumbIconImage() {
        return btnThumbIconImage;
    }

    /**
     * Get button unique id
     *
     * @return id
     */
    public int getBtnId() {
        return btnId;
    }

    /**
     * Update button icon resource
     *
     * @param imageResource image resource
     */
    public void updateIconByResource(Resource imageResource) {
        iconLabel.setVisible(false);
        btnThumbIconImage.setVisible(true);
        btnThumbIconImage.setSource(imageResource);
    }

    /**
     * Update using HTML code
     *
     * @param HTML icon as html
     */
    public void updateIconByHTMLCode(String HTML) {
        this.setVisible((HTML != null));
        if (HTML == null) {
            return;
        }
        btnThumbIconImage.setVisible(false);
        iconLabel.setVisible(true);
        iconLabel.setValue(HTML);
        if (this.getStyleName().contains("reshake")) {
            this.removeStyleName("reshake");
            this.addStyleName("shake");
        } else {
            this.removeStyleName("shake");
            this.addStyleName("reshake");
        }
    }

    /**
     * Set button selected
     *
     * @param selected button is selected
     */
    public void setSelected(boolean selected) {
        if (selected) {
            PresenterSubViewSideBtn.this.addStyleName("selectedbiglbtn");
        } else {
            PresenterSubViewSideBtn.this.removeStyleName("selectedbiglbtn");
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            this.removeStyleName("hide");
        } else {
            this.addStyleName("hide");
        }
    }

}
