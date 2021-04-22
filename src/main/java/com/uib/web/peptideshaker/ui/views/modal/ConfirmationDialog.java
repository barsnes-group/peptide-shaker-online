package com.uib.web.peptideshaker.ui.views.modal;

import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This class work as confirmation dialog
 *
 * @author Yehia Mokhtar Farag
 */
public class ConfirmationDialog implements Button.ClickListener {

    @Override
    public void buttonClick(Button.ClickEvent event) {
        containerWindow.setPopupVisible(false);
    }

    private final PopupWindow containerWindow;
    private final Label header;
    private final Button okButton;
    private final Button canceButton;
    private Button.ClickListener buttonsClickListener;

    public ConfirmationDialog() {
        AbsoluteLayout layout = new AbsoluteLayout();
        layout.setWidth(310, Sizeable.Unit.PIXELS);
        layout.setHeight(100, Sizeable.Unit.PIXELS);
        containerWindow = new PopupWindow("Confirm") {
            @Override
            public void onClosePopup() {
                if (buttonsClickListener != null) {
                    okButton.removeClickListener(buttonsClickListener);
                }
            }
        };
        layout.addStyleName("confirmdialog");
        containerWindow.setContent(layout);
        containerWindow.setClosable(true);
        header = new Label();
        header.setContentMode(ContentMode.HTML);
        layout.addComponent(header, "top:10px;right:40px;left:10px;");
        okButton = new Button("OK");
        okButton.setWidth(76, Sizeable.Unit.PIXELS);
        okButton.setHeight(20, Sizeable.Unit.PIXELS);
        okButton.setStyleName(ValoTheme.BUTTON_TINY);

        canceButton = new Button("Cancel");
        canceButton.setStyleName(ValoTheme.BUTTON_TINY);
        canceButton.setWidth(76, Sizeable.Unit.PIXELS);
        canceButton.setHeight(20, Sizeable.Unit.PIXELS);

        okButton.addClickListener(ConfirmationDialog.this);
        canceButton.addClickListener(ConfirmationDialog.this);
        layout.addComponent(okButton, "bottom:5px;right:96px");
        layout.addComponent(canceButton, "bottom:5px;right:10px");

    }

    public void showConfirmationMessage(String message, Button.ClickListener buttonsClickListener) {
        header.setValue(message);
        containerWindow.setPopupVisible(true);
        okButton.addClickListener(buttonsClickListener);
        this.buttonsClickListener = buttonsClickListener;
    }

}
