/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.ui.views.subviews;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.ui.abstracts.ViewableFrame;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbsoluteLayout;

/**
 *
 * @author Yehia Mokhtar Farag
 */
public class PeptidePsmsSubView extends AbsoluteLayout implements ViewableFrame{
 private final AppManagmentBean appManagmentBean;
    public PeptidePsmsSubView() {
        this.appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
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
//        if (appManagmentBean.getUI_Manager().getSelectedPeptideIndex()== -1) {
//            this.minimizeView();
//            appManagmentBean.getUI_Manager().setEncodedPeptideButtonImage("null");
//        } else {
//            System.out.println("at update protein peptide overview " + appManagmentBean.getUI_Manager().getSelectedPeptideIndex());
//            appManagmentBean.getUI_Manager().setEncodedPeptideButtonImage("Kokowawa protein image");
//        }

    }
    
}
