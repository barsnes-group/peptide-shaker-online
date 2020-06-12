
package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;

/**
 *This class represents pop-up label in case of large text 
 * @author Yehia Farag
 */
public class PopupLabel extends VerticalLayout{
    
    public PopupLabel(String text) {
        PopupLabel.this.setSizeFull();
        Label fullTextLabel = new Label(text);
        PopupView popupLabel = new PopupView(text, fullTextLabel);
         PopupLabel.this.addComponent(popupLabel);
         popupLabel.setStyleName("popuptext");
         popupLabel.setHideOnMouseOut(true);
        
    }
    
    
    
}
