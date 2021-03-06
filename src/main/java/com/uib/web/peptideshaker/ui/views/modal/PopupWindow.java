package com.uib.web.peptideshaker.ui.views.modal;

import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;

/**
 * This Class represents pop-up window that support modal mode
 *
 * @author Yehia Mokhtar Farag
 */
public abstract class PopupWindow extends VerticalLayout implements LayoutEvents.LayoutClickListener {

    private final Window window;
    private final Label titleLabel;
    private Layout windoContent;

    /**
     * Constructor to initialize the main layout.
     *
     * @param title header for the popup window
     */
    public PopupWindow(String title) {
        titleLabel = new Label(title, ContentMode.HTML);
        titleLabel.setStyleName("windowtitle");
        PopupWindow.this.addComponent(titleLabel);
        PopupWindow.this.addLayoutClickListener(PopupWindow.this);
        window = new Window() {
            @Override
            public void close() {
                this.setVisible(false);
                onClosePopup();
            }

            @Override
            public void setVisible(boolean visible) {
                if (windoContent != null) {
                    super.setVisible(visible);
                } else {
                    super.setVisible(false);
                    onClosePopup();
                }
            }
        };
        window.center();
        window.setWindowMode(WindowMode.NORMAL);
        window.setStyleName("popupwindow");
        window.setClosable(false);
        window.setModal(true);
        window.setResizable(false);
        window.setDraggable(false);
        window.setVisible(false);

        UI.getCurrent().access(() -> {
            UI.getCurrent().addWindow(window);
        });


    }

    public void setClosable(boolean closable) {
        window.setClosable(closable);
    }

    public void setLabelValue(String value) {
        titleLabel.setValue(value);
    }

    public Layout getContent() {
        return windoContent;
    }

    public void setContent(Layout popup) {
        windoContent = popup;
        window.setContent(popup);
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        window.setVisible(true);
    }

    public void setPopupVisible(boolean visible) {
        window.setVisible(visible);
    }

    public void setWindowSize(int width, int height) {
        window.setWidth(width, Unit.PERCENTAGE);
        window.setHeight(height, Unit.PERCENTAGE);
    }

    public void addWindowStyle(String style) {
        window.addStyleName(style);
    }

    public abstract void onClosePopup();

}
