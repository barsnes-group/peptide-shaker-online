package com.uib.web.peptideshaker.facades;

import com.uib.web.peptideshaker.ui.views.modal.ConfirmationDialog;
import com.uib.web.peptideshaker.ui.views.modal.GalaxyConnectingModal;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.io.Serializable;

/**
 * This class responsible for user notifications
 *
 * @author Yehia Mokhtar Farag
 */
public class NotificationsFacade implements Serializable {

    private Label notification;
    private final Window landscapeModeNotificationWindow;
    private GalaxyConnectingModal GalaxyConnectingModal;
    private ConfirmationDialog confirmationDialog;

    public NotificationsFacade() {
        landscapeModeNotificationWindow = new Window();
        notification = new Label("Use the device in landscape mode <center><i>(recommended)</i></center>", ContentMode.HTML);
        notification.setStyleName("mobilealertnotification");
        landscapeModeNotificationWindow.setStyleName("mobilealertnotification");
        landscapeModeNotificationWindow.setClosable(true);
        landscapeModeNotificationWindow.setModal(false);
        landscapeModeNotificationWindow.setWindowMode(WindowMode.NORMAL);
        landscapeModeNotificationWindow.setDraggable(false);
        landscapeModeNotificationWindow.setResizable(false);
        landscapeModeNotificationWindow.setVisible(false);
        landscapeModeNotificationWindow.setContent(notification);
        this.confirmationDialog = new ConfirmationDialog();

    }

    public void showLandscapeModeNotification() {
        if (!UI.getCurrent().getWindows().contains(landscapeModeNotificationWindow)) {
            UI.getCurrent().addWindow(landscapeModeNotificationWindow);
        }
        landscapeModeNotificationWindow.setVisible(true);
    }

    public void hideLandscapeModeNotification() {
        landscapeModeNotificationWindow.setVisible(false);
    }

    public void showInfoNotification(String message) {
        Notification.show("Information", message, Notification.Type.TRAY_NOTIFICATION);
    }

    public void showErrorNotification(String message) {
        Notification.show("Error", message, Notification.Type.TRAY_NOTIFICATION);
    }

    public void showAlertNotification(String message) {
        com.vaadin.ui.JavaScript.getCurrent().execute("alert('" + message + "')");
    }

    public void confirmAlertNotification(String message, Button.ClickListener clickListener) {
        confirmationDialog.showConfirmationMessage(message, clickListener);
    }

    public void showGalaxyConnectingProcess(String caption) {
        if (GalaxyConnectingModal == null) {
            GalaxyConnectingModal = new GalaxyConnectingModal();
        }

        GalaxyConnectingModal.setLabelCaption(caption);
        GalaxyConnectingModal.setVisible(true);
    }

    public void hideGalaxyConnectingProcess() {
        GalaxyConnectingModal.setVisible(false);
    }

    public void showBusyProcess() {
    }

    public void hideBusyProcess() {
    }
}
