package com.uib.web.peptideshaker.ui.views.subviews;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.PSMObject;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.PeptideObject;
import com.uib.web.peptideshaker.ui.components.items.HelpPopupButton;
import com.uib.web.peptideshaker.ui.views.subviews.peptidespsmviews.PSMContainerView;
import com.uib.web.peptideshaker.ui.views.subviews.peptidespsmviews.components.SpectrumInformation;
import com.uib.web.peptideshaker.ui.interfaces.ViewableFrame;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import java.util.List;
import java.util.Map;

/**
 * This class represents the layout that contains selected peptide psm subview
 * container
 *
 * @author Yehia Mokhtar Farag
 */
public class PeptidePsmsSubView extends AbsoluteLayout implements ViewableFrame {

    private final AppManagmentBean appManagmentBean;
    private final Label headerLabel;
    private final AbsoluteLayout container;
    private final PSMContainerView psmViewComponent;

    public PeptidePsmsSubView() {
        this.appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        PeptidePsmsSubView.this.setSizeFull();
        PeptidePsmsSubView.this.setStyleName("transitionallayout");

        container = new AbsoluteLayout();
        container.setSizeFull();
        PeptidePsmsSubView.this.addComponent(container);

        HorizontalLayout topLabelContainer = new HorizontalLayout();
        topLabelContainer.setHeight(30, Unit.PIXELS);
        topLabelContainer.setWidth(100, Unit.PERCENTAGE);
        topLabelContainer.addStyleName("minhight30");
        container.addComponent(topLabelContainer);
        HorizontalLayout topLeftLabelContainer = new HorizontalLayout();
        topLeftLabelContainer.setWidthUndefined();
        topLeftLabelContainer.setHeight(100, Unit.PERCENTAGE);
        topLabelContainer.addComponent(topLeftLabelContainer);
        headerLabel = new Label();
        headerLabel.setValue("Peptide Spectrum Matches");
        headerLabel.addStyleName("largetitle");
        headerLabel.setWidthUndefined();
        topLeftLabelContainer.setSpacing(true);
        topLeftLabelContainer.addComponent(headerLabel);
         HelpPopupButton helpBtn = new HelpPopupButton("<h1>Peptide level</h1>visulization of peptide details include available peptide-to-spectrum matches and spectrum visulizaion chart.", "", 350  , 90);

         topLeftLabelContainer.addComponent(helpBtn);

        HorizontalLayout middleContainer = new HorizontalLayout();
        middleContainer.setSizeFull();
        middleContainer.setSpacing(true);
        container.addComponent(middleContainer, "left:0px ; top:30px");
        psmViewComponent = new PSMContainerView();
        middleContainer.addComponent(psmViewComponent);

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
        if (appManagmentBean.getUI_Manager().getSelectedPeptideIndex() == -1) {
            this.minimizeView();
            appManagmentBean.getUI_Manager().setEncodedPeptideButtonImage("null");
            headerLabel.setValue("Peptide Spectrum Matches");
            headerLabel.setDescription("");
        } else {
            
            PeptideObject selectedPeptide = appManagmentBean.getUserHandler().getDataset(appManagmentBean.getUI_Manager().getSelectedDatasetId()).getPeptidesMap().get(appManagmentBean.getUI_Manager().getSelectedPeptideIndex());
            headerLabel.setValue("Peptide Spectrum Matches (" + selectedPeptide.getModifiedSequence() + ")");
            headerLabel.setDescription(selectedPeptide.getTooltip());
            this.psmViewComponent.updateView();
           

        }

    }

}
