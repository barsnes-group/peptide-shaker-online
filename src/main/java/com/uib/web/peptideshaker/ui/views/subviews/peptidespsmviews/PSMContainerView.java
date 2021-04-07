package com.uib.web.peptideshaker.ui.views.subviews.peptidespsmviews;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.PSMObject;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.PeptideObject;
import com.uib.web.peptideshaker.model.VisualizationDatasetModel;
import com.uib.web.peptideshaker.ui.components.items.ColorLabelWithPopupTooltip;
import com.uib.web.peptideshaker.ui.components.items.SparkLineLabel;
import com.uib.web.peptideshaker.ui.components.items.ValidationLabel;
import com.uib.web.peptideshaker.ui.views.subviews.peptidespsmviews.components.SecondarySpectraChartsGenerator;
import com.uib.web.peptideshaker.ui.views.subviews.peptidespsmviews.components.SpectrumInformation;
import com.uib.web.peptideshaker.ui.views.subviews.peptidespsmviews.components.SpectrumPlot;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Yehia Farag
 */
public class PSMContainerView extends VerticalLayout {

    private final Table psmOverviewTable;
    private final AbsoluteLayout psmTableWrapper;
    private final Property.ValueChangeListener psmlistener;
    private final VerticalLayout chartContainer;
    private final SpectrumPlot spectrumPlot;
    private final DecimalFormat df1 = new DecimalFormat("#.##");
    private final Map<Integer, Component> filterComponentsMap;
    private final LayoutEvents.LayoutClickListener listener;
    private int index = 0;
    private int intensityColumnWidth = 120;
    private int currentFilterView = 0;
    private int fragWidth;
    private Map<Object, SpectrumInformation> spectrumInformationMap;
    private final AppManagmentBean appManagmentBean;
    private  String psmTooltip ;
    private  String mainPeptideTooltip;

    public PSMContainerView() {
        this.appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        PSMContainerView.this.setSizeFull();
        PSMContainerView.this.setSpacing(true);

        final AbsoluteLayout splitpanel = new AbsoluteLayout();
        splitpanel.setSizeFull();

        PSMContainerView.this.addComponent(splitpanel);

        psmTableWrapper = new AbsoluteLayout();
        psmTableWrapper.setSizeFull();
        this.psmTableWrapper.addStyleName("psmtablecontainerstyle");
        psmOverviewTable = new Table() {
            DecimalFormat df = new DecimalFormat("0.00E00");// new DecimalFormat("#.##");
            DecimalFormat df1 = new DecimalFormat("#.##");

            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                Object v = property.getValue();
                if (v instanceof Double) {
                    if ((double) v == 1) {
                        return "1.00";
                    }
                    if ((double) v > 100) {
                        return df.format(v);
                    } else {
                        return df1.format(v);
                    }
                }
                return super.formatPropertyValue(rowId, colId, property);
            }
        };
        psmTableWrapper.addComponent(psmOverviewTable, "left:0px;bottom:0px");
        psmOverviewTable.setStyleName(ValoTheme.TABLE_COMPACT);
        psmOverviewTable.addStyleName(ValoTheme.TABLE_SMALL);
        psmOverviewTable.addStyleName("framedpanel");
        psmOverviewTable.addStyleName("inframetable");
        psmOverviewTable.addStyleName("psmtable");
        psmOverviewTable.addStyleName(ValoTheme.TABLE_NO_STRIPES);
        psmOverviewTable.setSelectable(true);
        psmOverviewTable.setNullSelectionAllowed(false);
        psmOverviewTable.setMultiSelect(false);
        psmOverviewTable.setWidth(100, Unit.PERCENTAGE);
        psmOverviewTable.setHeight(100, Unit.PERCENTAGE);
        psmOverviewTable.addContainerProperty("index", Integer.class, null, "", null, Table.Align.RIGHT);
//        psmOverviewTable.addContainerProperty("id", ColorLabel.class, null, "ID", null, Table.Align.CENTER);
        psmOverviewTable.addContainerProperty("sequenceFrag", VerticalLayout.class, null, generateCaptionWithTooltio("Fragmentation", "Sequence Fragmentation"), null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("massErrorPlot", VerticalLayout.class, null, generateCaptionWithTooltio("Mass Error Plot", "Mass Error Plot"), null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("Intensity", ColorLabelWithPopupTooltip.class, null, "Intensity", null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("charge", SparkLineLabel.class, null, generateCaptionWithTooltio("Charge", "Charge"), null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("mzError", SparkLineLabel.class, null, generateCaptionWithTooltio("Mass Error", "Mass Error"), null, Table.Align.LEFT);

        psmOverviewTable.addContainerProperty("confidence", SparkLineLabel.class, null, generateCaptionWithTooltio("Confidence", "Confidence level"), null, Table.Align.LEFT);
        psmOverviewTable.addContainerProperty("validation", ValidationLabel.class, null, generateCaptionWithTooltio("", "Protein validation"), null, Table.Align.CENTER);
        psmOverviewTable.setColumnWidth("index", 37);
//        psmOverviewTable.setColumnWidth("id", 37);
        psmOverviewTable.setColumnWidth("validation", 37);
        psmOverviewTable.setColumnWidth("confidence", 120);
        psmOverviewTable.setColumnWidth("Intensity", Math.max(120, intensityColumnWidth));
        psmOverviewTable.setColumnWidth("charge", 100);
        psmOverviewTable.setColumnWidth("mzError", 100);
        psmOverviewTable.setColumnWidth("massErrorPlot", 275);
        psmOverviewTable.setSortContainerPropertyId("charge");

        splitpanel.setStyleName("nonscrollsplitpanel");
        chartContainer = new VerticalLayout();
        chartContainer.setSizeFull();
        this.chartContainer.addStyleName("inframetable");
        this.chartContainer.addStyleName("psmchartcontainerstyle");
        splitpanel.addComponent(chartContainer, "left:0px;top:0px;bottom:40%;");
        splitpanel.addComponent(psmTableWrapper, "left:0px;top:60%; bottom:-1px");

        psmlistener = (Property.ValueChangeEvent event) -> {
            SpectrumInformation spectrumInformation = spectrumInformationMap.get(psmOverviewTable.getValue());
            this.getSpectrumPlot().selectedSpectrum(spectrumInformation.getSpectrum(), spectrumInformation.getSequenceProvider(), spectrumInformation.getCharge(), spectrumInformation.getFragmentIonAccuracy(), spectrumInformation.getIdentificationParameters(), spectrumInformation.getSpectrumMatch());
        };
        psmOverviewTable.addValueChangeListener(psmlistener);

        this.spectrumPlot = new SpectrumPlot();
        this.spectrumPlot.setSizeFull();
        this.chartContainer.addComponent(spectrumPlot);

        this.listener = (LayoutEvents.LayoutClickEvent event) -> {
            psmOverviewTable.setValue(((VerticalLayout) (event.getComponent())).getData());
        };

        Table.ColumnResizeListener columnResizeListener = ((Table.ColumnResizeEvent event) -> {
            psmOverviewTable.setColumnWidth(event.getPropertyId(), event.getPreviousWidth());
        });

        psmOverviewTable.addColumnResizeListener(columnResizeListener);
        SizeReporter tableResporter = new SizeReporter(psmOverviewTable);
        tableResporter.addResizeListener((ComponentResizeEvent event) -> {

            int height = event.getHeight();
            int actH = 37 + (74 * psmOverviewTable.getItemIds().size());
            int resizeFragWidth = Math.max(this.fragWidth, event.getWidth() - (649) - 9 - 20 - intensityColumnWidth);

            psmOverviewTable.removeColumnResizeListener(columnResizeListener);
            psmOverviewTable.setColumnWidth("index", 37);
            psmOverviewTable.setColumnWidth("sequenceFrag", resizeFragWidth);
            psmOverviewTable.setColumnWidth("validation", 37);
            psmOverviewTable.setColumnWidth("confidence", 100);
            psmOverviewTable.setColumnWidth("charge", 100);
            psmOverviewTable.setColumnWidth("mzError", 100);
            psmOverviewTable.setColumnWidth("massErrorPlot", 275);
            psmOverviewTable.setColumnWidth("Intensity", intensityColumnWidth);
            psmOverviewTable.addColumnResizeListener(columnResizeListener);
        });
        this.filterComponentsMap = new HashMap<>();
        this.filterComponentsMap.put(1, this.chartContainer);
        this.filterComponentsMap.put(2, this.psmTableWrapper);

        HorizontalLayout paggingBtnsContainer = new HorizontalLayout();
        paggingBtnsContainer.setWidth(100, Unit.PERCENTAGE);
        paggingBtnsContainer.setHeight(20, Unit.PIXELS);
        paggingBtnsContainer.addStyleName("paggingbtnscontainer");
        splitpanel.addComponent(paggingBtnsContainer, "left:0px;bottom:50px");
        HorizontalLayout btnContainer = new HorizontalLayout();
        btnContainer.setHeight(100, Unit.PERCENTAGE);
        btnContainer.setWidthUndefined();
        btnContainer.setSpacing(true);
        paggingBtnsContainer.addComponent(btnContainer);
        paggingBtnsContainer.setComponentAlignment(btnContainer, Alignment.TOP_CENTER);

        Button beforeBtn = new Button(VaadinIcons.CARET_LEFT);
        beforeBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        btnContainer.addComponent(beforeBtn);

        final Label filterViewIndex = new Label(" (1/2) ", ContentMode.HTML);
        btnContainer.addComponent(filterViewIndex);

        beforeBtn.addClickListener((Button.ClickEvent event) -> {
            filterViewIndex.setValue(" (" + this.showBefore() + "/2) ");
        });
        Button nextBtn = new Button(VaadinIcons.CARET_RIGHT);
        nextBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        btnContainer.addComponent(nextBtn);
        nextBtn.addClickListener((Button.ClickEvent event) -> {
            filterViewIndex.setValue(" (" + this.showNext() + "/2) ");
        });
        filterViewIndex.setValue(" (" + PSMContainerView.this.showNext() + "/2) ");

//       
//        resizeControlBtn.resize(2);
    }

    public SpectrumPlot getSpectrumPlot() {
        return spectrumPlot;
    }

    public void viewSpectraPlot(boolean view) {
        chartContainer.setVisible(view);
        if (!view) {
            psmTableWrapper.addStyleName("nomargintop");
        } else {
            psmTableWrapper.removeStyleName("nomargintop");
        }
    }

    public void viewPSMTable(boolean view) {
        psmTableWrapper.setVisible(view);
        psmTableWrapper.setSizeFull();
        psmOverviewTable.setSizeFull();

    }

    public void updateView() {
        this.psmOverviewTable.removeValueChangeListener(psmlistener);
        this.psmOverviewTable.removeAllItems();
        VisualizationDatasetModel selectedDataset = appManagmentBean.getUserHandler().getDataset(appManagmentBean.getUI_Manager().getSelectedDatasetId());
        PeptideObject selectedPeptide = selectedDataset.getPeptidesMap().get(appManagmentBean.getUI_Manager().getSelectedPeptideIndex());
        mainPeptideTooltip = "";
        for (String str : selectedPeptide.getTooltip().split("</br>")) {
            if (str.contains("#PSMs:")) {
                mainPeptideTooltip = selectedPeptide.getTooltip().replace(str + "</br>", "");
                break;
            }
        }
        Set<String> psmKeys = selectedDataset.getPeptidesPsmMap().get(selectedPeptide.getModifiedSequence());
        List<PSMObject> psms = new ArrayList<>();
        psmKeys.forEach((str) -> {
            psms.add(selectedDataset.getPsmsMap().get(str));
        });
        index = 1;
        fragWidth = (-1 * Integer.MAX_VALUE);
        if (selectedDataset.getDatasetType().equals(CONSTANT.ID_DATASET)) {
            psmOverviewTable.setColumnWidth("sequenceFrag", psmOverviewTable.getColumnWidth("sequenceFrag") + 120);
            psmOverviewTable.setColumnCollapsingAllowed(true);
            psmOverviewTable.setColumnCollapsible("Intensity", true);
            psmOverviewTable.setColumnCollapsed("Intensity", true);
            intensityColumnWidth = 0;
        }

        spectrumInformationMap = appManagmentBean.getDatasetUtils().getSelectedSpectrumData(psms,selectedPeptide,selectedDataset,appManagmentBean.getUserHandler().getUserAPIKey());

        if (spectrumInformationMap == null) {
            return;
        }
        psms.stream().map((psm) -> {
            return psm;
        }).forEachOrdered((psm) -> {
            fragWidth = Math.max(fragWidth, psm.getSequence().length() * 17 + 100);
             psmTooltip = "";
            for (String str : mainPeptideTooltip.split("</br>")) {
                if (str.contains("Intensity:") && psm.getIntensity() == -10000.0) {
                    psmTooltip = mainPeptideTooltip.replace(str, "");
                } else if (str.contains("Intensity:")) {
                    psmTooltip = mainPeptideTooltip.replace(str, "Intensity: " + psm.getIntensity());
                    break;
                }
            }
            SecondarySpectraChartsGenerator chartGenerator = new SecondarySpectraChartsGenerator(psm.getModifiedSequence(), psmTooltip, psm.getIndex(), spectrumInformationMap.get(psm.getIndex()));
            chartGenerator.getSequenceFragmentationChart().addLayoutClickListener(listener);
            chartGenerator.getMassErrorPlot().addLayoutClickListener(listener);
            int charge = Integer.parseInt(psm.getIdentificationCharge().replace("+", ""));
            Map<String, Number> values = new LinkedHashMap<>();
            values.put("greenlayout", (float) charge / (float) spectrumInformationMap.get(psm.getIndex()).getMaxCharge());
            SparkLineLabel chargeLabel = new SparkLineLabel(charge + "", values, psm.getIndex()) {
                @Override
                public void selected(Object itemId) {
                    psmOverviewTable.setValue(itemId);
                }
            };

            double mzError = Math.abs(psm.getPrecursorMZError_PPM());
            Map<String, Number> mzErrorValues = new LinkedHashMap<>();
            mzErrorValues.put("greenlayout", (float) mzError / ((float) spectrumInformationMap.get(psm.getIndex()).getMzError() * 2.0f));
            SparkLineLabel mzErrorLabel = new SparkLineLabel(df1.format(mzError) + "", mzErrorValues, psm.getIndex()) {
                @Override
                public void selected(Object itemId) {
                    psmOverviewTable.setValue(itemId);
                }
            };

            Map<String, Number> confidentValues = new LinkedHashMap<>();
            confidentValues.put("greenlayout", (float) psm.getConfidence() / 100f);
            SparkLineLabel confidentLabel = new SparkLineLabel(df1.format(psm.getConfidence()), confidentValues, psm.getIndex()) {
                @Override
                public void selected(Object itemId) {
                    psmOverviewTable.setValue(itemId);
                }
            };
            ValidationLabel validation = new ValidationLabel(psm.getValidation());
            this.psmOverviewTable.addItem(new Object[]{index++, chartGenerator.getSequenceFragmentationChart(), chartGenerator.getMassErrorPlot(), new ColorLabelWithPopupTooltip(psm.getIntensity(), psm.getIntensityColor(), psm.getIntensityPercentage()), chargeLabel, mzErrorLabel, confidentLabel, validation}, psm.getIndex());

        });
        this.psmOverviewTable.setSortContainerPropertyId("confidence");
        this.psmOverviewTable.setSortAscending(false);
        this.psmOverviewTable.sort();
        index = 1;
        psmOverviewTable.getItemIds().forEach((id) -> {
            this.psmOverviewTable.getItem(id).getItemProperty("index").setValue(index++);
        });
        this.psmOverviewTable.addValueChangeListener(psmlistener);
        spectrumPlot.setDisableSizeReporter(false);
        psmOverviewTable.setValue(psmOverviewTable.firstItemId());
        psmOverviewTable.commit();

    }      
  

    private String generateCaptionWithTooltio(String caption, String tooltip) {
        return "<div class='tooltip'>" + caption + "<span class='tooltiptext'>" + tooltip + "</span></div>";
    }

    public int showNext() {
        filterComponentsMap.values().stream().map((view) -> {
            view.addStyleName("hidedsfilter");
            return view;
        }).forEachOrdered((view) -> {
            view.removeStyleName("viewdsfilter");
        });
        currentFilterView++;
        if (currentFilterView > 2) {
            currentFilterView = 1;
        }

        filterComponentsMap.get(currentFilterView).addStyleName("viewdsfilter");
        filterComponentsMap.get(currentFilterView).removeStyleName("hidedsfilter");

        return currentFilterView;
    }

    public int showBefore() {
        filterComponentsMap.values().stream().map((view) -> {
            view.addStyleName("hidedsfilter");
            return view;
        }).forEachOrdered((view) -> {
            view.removeStyleName("viewdsfilter");
        });
        currentFilterView--;
        if (currentFilterView < 1) {
            currentFilterView = 2;
        }
        filterComponentsMap.get(currentFilterView).addStyleName("viewdsfilter");
        filterComponentsMap.get(currentFilterView).removeStyleName("hidedsfilter");

        return currentFilterView;
    }
}
