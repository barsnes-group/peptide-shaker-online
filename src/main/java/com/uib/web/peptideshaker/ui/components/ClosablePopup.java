
package com.uib.web.peptideshaker.ui.components;

import com.uib.web.peptideshaker.ui.components.items.CloseButton;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.*;

/**
 * This class represents popup layout with close button
 *
 * @author Yehia Mokhtar Farag
 */
public class ClosablePopup extends VerticalLayout {

    private final PopupView popupView;

    /**
     *
     * @param title
     * @param content
     * @param caption
     */
    public ClosablePopup(String title, Component content, String caption) {

        AbsoluteLayout container = new AbsoluteLayout();
        container.setWidth(200, Unit.PIXELS);
        container.setHeight(90, Unit.PIXELS);
        container.setStyleName("popupclosablecontent");
        CloseButton btn = new CloseButton() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                popupView.setPopupVisible(false);
            }
        };
        container.addComponent(btn, "right:-17px;top:-14px");
        Label l = new Label(title);
        container.addComponent(l, "left:5px;top:5px");

        container.addComponent(content, "left:5px;bottom:5px;top:32px;right:5px;");
        popupView = new PopupView(caption, container);
        popupView.setCaptionAsHtml(true);
        popupView.setHideOnMouseOut(false);
        popupView.setStyleName("popupwithicon");
        ClosablePopup.this.addComponent(popupView);
    }

}
