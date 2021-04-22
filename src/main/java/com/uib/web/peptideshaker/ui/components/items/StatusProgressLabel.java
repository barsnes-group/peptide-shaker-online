package com.uib.web.peptideshaker.ui.components.items;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;

/**
 * This class represent status label
 *
 * @author Yehia Mokhtar Farag
 */
public class StatusProgressLabel extends Image {

    private final Resource notActive;
    private final Resource notValid;
    private final Resource processing;
    private int status;

    /**
     * Initialise progress label
     */
    public StatusProgressLabel() {
        this.processing = new ThemeResource("img/loading.gif");
        this.notValid = new ThemeResource("img/close-circle.png");
        this.notActive = new ThemeResource("img/check-button.png");
        StatusProgressLabel.this.setSource(notActive);
        StatusProgressLabel.this.setWidth(20, Unit.PIXELS);
        StatusProgressLabel.this.setHeight(20, Unit.PIXELS);
        StatusProgressLabel.this.addStyleName("inactive");
    }

    /**
     * Get current statues
     *
     * @return statues
     */
    public int getStatus() {
        return status;
    }

    /**
     * Set current statues
     *
     * @param status current statues
     */
    public void setStatus(String status) {
        StatusProgressLabel.this.removeStyleName("inactive");
        StatusProgressLabel.this.removeStyleName("active");
        status = status + "";
        StatusProgressLabel.this.setDescription((status + "").toUpperCase());
        if (status.equalsIgnoreCase("ok")) {
            StatusProgressLabel.this.setSource(notActive);
            StatusProgressLabel.this.addStyleName("active");
            this.status = 0;
        } else if (status.equalsIgnoreCase("running")) {
            StatusProgressLabel.this.setSource(processing);
            this.status = 1;
        } else {
            StatusProgressLabel.this.setSource(notValid);
            this.status = 2;
        }

    }

}
