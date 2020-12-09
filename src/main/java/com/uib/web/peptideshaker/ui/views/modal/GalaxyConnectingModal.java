package com.uib.web.peptideshaker.ui.views.modal;

import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Yehia Mokhtar Farag
 */
public class GalaxyConnectingModal extends Window {

    private final Label connectingLabel;

    public GalaxyConnectingModal() {
        super(null, new VerticalLayout());
        GalaxyConnectingModal.this.setSizeFull();
        GalaxyConnectingModal.this.setModal(true);
        GalaxyConnectingModal.this.setDraggable(false);
        GalaxyConnectingModal.this.setClosable(false);
        GalaxyConnectingModal.this.setStyleName("connectionwindow");
        GalaxyConnectingModal.this.setResizable(false);
        GalaxyConnectingModal.this.center();
        GalaxyConnectingModal.this.setWindowMode(WindowMode.NORMAL);
        if (UI.getCurrent() != null && !UI.getCurrent().getWindows().contains(GalaxyConnectingModal.this)) {
            UI.getCurrent().addWindow(GalaxyConnectingModal.this);
        }
        GalaxyConnectingModal.this.setVisible(false);

        VerticalLayout galaxyLoginLayout = (VerticalLayout) super.getContent();
        galaxyLoginLayout.setWidth(100, Unit.PERCENTAGE);
        galaxyLoginLayout.setSpacing(true);
        galaxyLoginLayout.setVisible(true);
        galaxyLoginLayout.setMargin(false);

        connectingLabel = new Label("<h1 class='animation'>Connecting to galaxy, Please wait...</h1>");
        connectingLabel.setVisible(true);
        connectingLabel.setCaptionAsHtml(true);
        connectingLabel.setContentMode(ContentMode.HTML);
        connectingLabel.setHeight(25, Sizeable.Unit.PIXELS);
        connectingLabel.setWidth(200, Sizeable.Unit.PIXELS);
        connectingLabel.setStyleName(ValoTheme.LABEL_SMALL);
        connectingLabel.addStyleName(ValoTheme.LABEL_BOLD);
        connectingLabel.addStyleName(ValoTheme.LABEL_TINY);
        connectingLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        galaxyLoginLayout.addComponent(connectingLabel);
        galaxyLoginLayout.setComponentAlignment(connectingLabel, Alignment.MIDDLE_CENTER);
    }

    public void setLabelCaption(String caption) {
        connectingLabel.setCaption( "<b style=\"color:#cd6e1d !important\">"+caption+"</b>");

    }

    @Override
    public void close() {
        this.setVisible(false);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            this.center();
        }
    }
}
