package com.uib.web.peptideshaker.ui.components.items;

import com.uib.web.peptideshaker.model.CONSTANT;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;

/**
 * This class represent status label
 *
 * @author Yehia Mokhtar Farag
 */
public class StatusLabel extends Image {

    private final Resource ok;
    private final Resource error;
    private final Resource processing;
    private String status;

    /**
     * Initialise statues label
     */
    public StatusLabel() {
        this.ok = new ThemeResource("img/check-circle.png");
        this.error = new ThemeResource("img/close-circle.png");
        this.processing = new ThemeResource("img/indeterminateprogress.gif");
        StatusLabel.this.setSource(processing);
        StatusLabel.this.setWidth(16, Unit.PIXELS);
        StatusLabel.this.setHeight(16, Unit.PIXELS);
    }

    /**
     * Get label statues
     *
     * @return label statues
     */
    public String getStatus() {
        return status;
    }

    /**
     * Update label statues
     *
     * @param status label statues
     */
    public void setStatus(String status) {
        this.status = status;
        StatusLabel.this.setWidth(16, Unit.PIXELS);
        StatusLabel.this.setDescription((status + "").toUpperCase());
        if (status.equals(CONSTANT.OK_STATUS)) {
            StatusLabel.this.setSource(ok);
        } else if (status.equals(CONSTANT.RUNNING_STATUS)) {
            StatusLabel.this.setSource(processing);
            StatusLabel.this.setWidth(80, Unit.PIXELS);
        } else {
            StatusLabel.this.setSource(error);

        }

    }

}
