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

    /**
     *
     */
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

    /**
     * Notify users to use the mobile device in landscape mode.
     */
    public void showLandscapeModeNotification() {
        if (!UI.getCurrent().getWindows().contains(landscapeModeNotificationWindow)) {
            UI.getCurrent().addWindow(landscapeModeNotificationWindow);
        }
        landscapeModeNotificationWindow.setVisible(true);
    }

    /**
     * Hide landscape notification
     */
    public void hideLandscapeModeNotification() {
        landscapeModeNotificationWindow.setVisible(false);
    }

    /**
     * Show notification message
     *
     * @param message
     */
    public void showInfoNotification(String message) {
        Notification.show("Information", message, Notification.Type.TRAY_NOTIFICATION);
    }

    /**
     * Show error message
     *
     * @param message
     */
    public void showErrorNotification(String message) {
        Notification.show("Error", message, Notification.Type.TRAY_NOTIFICATION);
    }

    /**
     * break execution to show message (alert message)
     *
     * @param message
     */
    public void showAlertNotification(String message) {
        com.vaadin.ui.JavaScript.getCurrent().execute("alert('" + message + "')");
    }

    /**
     * Confirmation alert notification
     *
     * @param message
     * @param clickListener
     */
    public void confirmAlertNotification(String message, Button.ClickListener clickListener) {
        confirmationDialog.showConfirmationMessage(message, clickListener);
    }

    /**
     *Show galaxy connecting notification
     * @param caption message 
     */
    public void showGalaxyConnectingProcess(String caption) {
        if (GalaxyConnectingModal == null) {
            GalaxyConnectingModal = new GalaxyConnectingModal();
        }

        GalaxyConnectingModal.setLabelCaption(caption);
        GalaxyConnectingModal.setVisible(true);
    }

    /**
     *hide galaxy connecting notification.
     */
    public void hideGalaxyConnectingProcess() {
        GalaxyConnectingModal.setVisible(false);
    }

}
