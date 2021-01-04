package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview;

import com.uib.web.peptideshaker.uimanager.ResultsViewSelectionManager;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PSMObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.ui.components.items.SubViewSideButton;
import com.uib.web.peptideshaker.ui.abstracts.RegistrableFilter;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.PSMViewComponent;
import com.uib.web.peptideshaker.presenter.pscomponents.SpectrumInformation;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the layout that contains PeptideShaker datasets
 * overview
 *
 * @author Yehia Farag
 */
public class PeptideVisulizationLevelContainer extends HorizontalLayout implements RegistrableFilter {

    private final AbsoluteLayout container;
    private final Label headerLabel;
    private final ResultsViewSelectionManager Selection_Manager;
    private final SubViewSideButton psmViewBtn;
    private final PSMViewComponent psmViewComponent;
    private PeptideShakerVisualizationDataset peptideShakerVisualizationDataset;

    /**
     * Constructor to initialise the main layout and variables.
     *
     * @param Selection_Manager
     * @param psmViewBtn
     */
    public PeptideVisulizationLevelContainer(ResultsViewSelectionManager Selection_Manager, SubViewSideButton psmViewBtn) {

        PeptideVisulizationLevelContainer.this.setSizeFull();
        PeptideVisulizationLevelContainer.this.setSpacing(true);
        PeptideVisulizationLevelContainer.this.setMargin(false);
        PeptideVisulizationLevelContainer.this.setStyleName("psmView");
        PeptideVisulizationLevelContainer.this.addStyleName("transitionallayout");

        this.Selection_Manager = Selection_Manager;
        this.psmViewBtn = psmViewBtn;

        container = new AbsoluteLayout();
        container.setSizeFull();
        PeptideVisulizationLevelContainer.this.addComponent(container);

        HorizontalLayout topLabelContainer = new HorizontalLayout();
        topLabelContainer.setHeight(30, Unit.PIXELS);
        topLabelContainer.setWidth(100, Unit.PERCENTAGE);
        topLabelContainer.addStyleName("minhight30");
        container.addComponent(topLabelContainer);

        HorizontalLayout topLeftLabelContainer = new HorizontalLayout();
        topLeftLabelContainer.setWidth(100, Unit.PERCENTAGE);
        topLeftLabelContainer.setHeight(100, Unit.PERCENTAGE);
        topLabelContainer.addComponent(topLeftLabelContainer);
        headerLabel = new Label();
        headerLabel.setValue("Peptide Spectrum Matches");
        headerLabel.addStyleName("largetitle");
        headerLabel.setWidthUndefined();
        topLeftLabelContainer.setSpacing(true);
        topLeftLabelContainer.addComponent(headerLabel);

//        HorizontalLayout topControllerBtnContainer = new HorizontalLayout();
//        topControllerBtnContainer.setWidth(100, Unit.PIXELS);
//        topControllerBtnContainer.setHeight(100, Unit.PERCENTAGE);
//        topLeftLabelContainer.addComponent(topControllerBtnContainer);
//        topLeftLabelContainer.setComponentAlignment(topControllerBtnContainer, Alignment.TOP_RIGHT);
//        topControllerBtnContainer.setMargin(new MarginInfo(false, false, false, false));
//        topControllerBtnContainer.setSpacing(true);
//        topControllerBtnContainer.setStyleName("buttoncontainerstyle");
//        final Button viewTableBtn = new Button(VaadinIcons.TABLE);
//        topControllerBtnContainer.addComponent(viewTableBtn);
//        viewTableBtn.setStyleName(ValoTheme.BUTTON_TINY);
//        viewTableBtn.addStyleName("selectedBtn");
//        viewTableBtn.setSizeFull();
//        viewTableBtn.setData("table");
//
//        Button viewSpectraChartBtn = new Button(VaadinIcons.BAR_CHART_H);
//        topControllerBtnContainer.addComponent(viewSpectraChartBtn);
//        viewSpectraChartBtn.setStyleName(ValoTheme.BUTTON_TINY);
//        viewSpectraChartBtn.addStyleName("selectedBtn");
//        viewSpectraChartBtn.setSizeFull();
//        viewSpectraChartBtn.setData("plot");
        HorizontalLayout middleContainer = new HorizontalLayout();
        middleContainer.setSizeFull();
        middleContainer.setSpacing(true);
        container.addComponent(middleContainer, "left:0px ; top:30px");

        psmViewComponent = new PSMViewComponent() {
            @Override
            public Map<Object, SpectrumInformation> getSpectrumInformationMap(List<PSMObject> psms) {
                return peptideShakerVisualizationDataset.getSelectedSpectrumData(psms, Selection_Manager.getSelectedPeptide());
            }

        };
        psmViewComponent.setThumbImage(this.psmViewBtn.getBtnThumbIconImage());
        middleContainer.addComponent(psmViewComponent);
        Selection_Manager.RegistrProteinInformationComponent(PeptideVisulizationLevelContainer.this);

//        Button.ClickListener viewControlListener = (Button.ClickEvent event) -> {
//            Button actionBtn = event.getButton();
//            boolean isSmallScreenMods = (boolean) VaadinSession.getCurrent().getAttribute("smallscreenstyle");
//            
//            if (actionBtn.getStyleName().contains("selectedBtn")) {
//                actionBtn.removeStyleName("selectedBtn");
//                if (actionBtn.getData().toString().equalsIgnoreCase("plot")) {
//                    psmViewComponent.viewSpectraPlot(false);
//                    if (isSmallScreenMods) {
//                        psmViewComponent.viewPSMTable(true);
//                        viewTableBtn.addStyleName("selectedBtn");
//                    }
//                } else {
//                    psmViewComponent.viewPSMTable(false);
//                    if (isSmallScreenMods) {
//                        psmViewComponent.viewSpectraPlot(true);
//                        viewSpectraChartBtn.addStyleName("selectedBtn");
//                    }
//
//                }
//            } else {
//                actionBtn.addStyleName("selectedBtn");
//                if (actionBtn.getData().toString().equalsIgnoreCase("plot")) {
//                    psmViewComponent.viewSpectraPlot(true);
//                    if (isSmallScreenMods) {
//                        psmViewComponent.viewPSMTable(false);
//                        viewTableBtn.removeStyleName("selectedBtn");
//                    }
//
//                } else {
//                    psmViewComponent.viewPSMTable(true);
//                    if (isSmallScreenMods) {
//                        psmViewComponent.viewSpectraPlot(false);
//                        viewSpectraChartBtn.removeStyleName("selectedBtn");
//                    }
//                }
//            }
//
//        };
//        viewTableBtn.addClickListener(viewControlListener);
//        viewSpectraChartBtn.addClickListener(viewControlListener);
    }

    public void selectDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        this.peptideShakerVisualizationDataset = peptideShakerVisualizationDataset;
    }

    @Override
    public String getFilterId() {
        return "PSM";
    }

    @Override
    public void updateFilterSelection(Set<Comparable> selection, Set<Comparable> selectedCategories, boolean topFilter, boolean singleFilter, boolean selfAction) {

    }

    @Override
    public void selectionChange(String type) {
        if (peptideShakerVisualizationDataset == null) {
            return;
        }
        if (type.equalsIgnoreCase("peptide_selection") && !peptideShakerVisualizationDataset.isUploadedProject()) {

            if (Selection_Manager.getSelectedPeptide() != null) {               
                headerLabel.setValue("Peptide Spectrum Matches (" + Selection_Manager.getSelectedPeptide().getModifiedSequence() + ")");
                headerLabel.setDescription(Selection_Manager.getSelectedPeptide().getTooltip());
                String psmTooltip = "";
                for (String str : Selection_Manager.getSelectedPeptide().getTooltip().split("</br>")) {
                    if (str.contains("#PSMs:")) {
                        psmTooltip = Selection_Manager.getSelectedPeptide().getTooltip().replace(str + "</br>", "");
                        break;
                    }
                }

                this.psmViewComponent.updateView(peptideShakerVisualizationDataset.getPSM(Selection_Manager.getSelectedPeptide().getModifiedSequence()), psmTooltip, Selection_Manager.getSelectedPeptide().getModifiedSequence().length(), peptideShakerVisualizationDataset.isQuantDataset());
            } else {
                headerLabel.setValue("Peptide Spectrum Matches");
                this.psmViewBtn.updateIconByResource(null);
                headerLabel.setDescription("");
            }

        } else {
            headerLabel.setValue("Peptide Spectrum Matches");
            this.psmViewBtn.updateIconByResource(null);
            headerLabel.setDescription("");
        }
    }

    @Override
    public void redrawChart() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void suspendFilter(boolean suspend) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
//    public void addStyleName(String style) {
//        System.out.println("remove style name invoked "+style);
////          this.psmViewComponent.updateView(peptideShakerVisualizationDataset.getPSM(Selection_Manager.getSelectedPeptide().getModifiedSequence()), Selection_Manager.getSelectedPeptide().getTooltip(), Selection_Manager.getSelectedPeptide().getModifiedSequence().length());
//        super.removeStyleName(style); //To change body of generated methods, choose Tools | Templates.
//    }
}
