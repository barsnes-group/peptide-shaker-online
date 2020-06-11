package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;

/**
 * This class represent status label
 *
 * @author Yehia Farag
 */
public class StatusLabel extends Image {

    private final Resource ok;
    private final Resource notValid;
    private final Resource processing;
    private int status;

    /**
     * Initialise statues label
     */
    public StatusLabel() {
        this.ok = new ThemeResource("img/check-circle.png");
        this.notValid = new ThemeResource("img/close-circle.png");
        this.processing = new ThemeResource("img/indeterminateprogress.gif");
        StatusLabel.this.setSource(processing);
        StatusLabel.this.setWidth(16, Unit.PIXELS);
        StatusLabel.this.setHeight(16, Unit.PIXELS);
    }

    /**
     * Update label statues
     *
     * @param status label statues
     */
    public void setStatus(String status) {
        status = status + "";
        StatusLabel.this.setWidth(16, Unit.PIXELS);
        StatusLabel.this.setDescription((status + "").toUpperCase());
        if (status.equalsIgnoreCase("ok")) {
            StatusLabel.this.setSource(ok);
            this.status = 0;
        } else if (status.equalsIgnoreCase("new") || status.equalsIgnoreCase("running") || status.equalsIgnoreCase("queued")) {
            StatusLabel.this.setSource(processing);
            StatusLabel.this.setWidth(100, Unit.PIXELS);
            this.status = 1;
        } else {
            StatusLabel.this.setSource(notValid);
            this.status = 2;
        }

    }

    /**
     * Get label statues
     *
     * @return label statues
     */
    public int getStatus() {
        return status;
    }

}
