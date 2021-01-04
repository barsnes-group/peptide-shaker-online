/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.ui.views.subviews;

import com.uib.web.peptideshaker.ui.abstracts.ViewableFrame;
import com.vaadin.ui.AbsoluteLayout;

/**
 *
 * @author Yehia Mokhtar Farag
 */
public class PeptidePsmsSubView extends AbsoluteLayout implements ViewableFrame{

    public PeptidePsmsSubView() {
        PeptidePsmsSubView.this.setSizeFull();
    }

    @Override
    public String getViewId() {
       return PeptidePsmsSubView.class.getName();
    }

     /**
     * Hide current presenter
     */
    @Override
    public void minimizeView() {
        this.addStyleName("hidepanel");
    }

    /**
     * View presenter
     */
    @Override
    public void maximizeView() {
        this.removeStyleName("hidepanel");
    }

    @Override
    public void update() {
        System.out.println("at update peptide-psm  overview ");
    }
    
}
