package com.uib.web.peptideshaker.ui.components;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.jsclipboard.JSClipboardButton;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Utility to allow user to save links (dataset to share by clicking on links
 *
 * @author Yehia Mokhtar Farag
 */
public class ClipboardComponent extends AbsoluteLayout {

    private final TextArea area;
    private final JSClipboardButton clipboard;

    /**
     * Initialise clipboard utility
     *
     * @param linkAstText to copy
     */
    public ClipboardComponent(String linkAstText) {
        ClipboardComponent.this.setSizeFull();
        ClipboardComponent.this.addStyleName("clipboardcontainer");

        area = new TextArea();
        ClipboardComponent.this.addComponent(area);
        area.setValue(linkAstText);
        area.setId("tocopie");
        clipboard = new JSClipboardButton(area, VaadinIcons.LINK);
        clipboard.setClipboardText(linkAstText);
        clipboard.setDescription("Click to copy the link");
        ClipboardComponent.this.addComponent(clipboard);
        clipboard.addStyleName(ValoTheme.BUTTON_LINK);
        clipboard.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        clipboard.addSuccessListener(() -> {
            Notification.show("Copy to clipboard successful :-) ", linkAstText, Notification.Type.TRAY_NOTIFICATION);
        });
        clipboard.addErrorListener(() -> {
            Notification.show("Error", "Copy to clipboard unsuccessful", Notification.Type.ERROR_MESSAGE);
        });

    }

}
