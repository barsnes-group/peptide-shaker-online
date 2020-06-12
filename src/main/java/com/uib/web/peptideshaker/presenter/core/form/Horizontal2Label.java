package com.uib.web.peptideshaker.presenter.core.form;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This class represent form component (drop down list with caption on the left
 * side)
 *
 * @author Yehia Farag
 */
public class Horizontal2Label extends HorizontalLayout {

private final  Label valueLabel ;
    /**
     * Constructor to initialize the main attributes
     *
     * @param caption title
     * @param values the drop-down list values
     */
    public Horizontal2Label(String caption, Object defaultValue) {
        Horizontal2Label.this.setSpacing(true);
        Label cap = new Label(caption);
        cap.setContentMode(ContentMode.HTML);
        cap.addStyleName(ValoTheme.LABEL_TINY);
        cap.addStyleName(ValoTheme.LABEL_SMALL);
        cap.addStyleName(ValoTheme.LABEL_BOLD);
        cap.addStyleName("smallundecorated");
        Horizontal2Label.this.addComponent(cap); 
        this.valueLabel = new Label();      
        valueLabel.setContentMode(ContentMode.HTML);
        if(defaultValue==null)
        {
            Horizontal2Label.this.setEnabled(false);
            return;
            
        }

          
        valueLabel.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
        valueLabel.setWidth(100, Unit.PERCENTAGE);
        valueLabel.addStyleName(ValoTheme.LABEL_TINY);
        valueLabel.setValue(defaultValue+"");

        valueLabel.setWidth(100, Unit.PERCENTAGE);
        valueLabel.setHeight(25, Unit.PIXELS);
        valueLabel.addStyleName("smallundecorated");
        Horizontal2Label.this.addComponent(valueLabel);
    }
    public void updateValue(String value){
        this.valueLabel.setValue(value);
    }

    

}
