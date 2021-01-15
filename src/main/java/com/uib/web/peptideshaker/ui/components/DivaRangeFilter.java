package com.uib.web.peptideshaker.ui.components;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.FilterUpdatingEvent;
import com.uib.web.peptideshaker.model.Selection;
import com.uib.web.peptideshaker.ui.components.items.FilterButton;
import com.uib.web.peptideshaker.ui.abstracts.RegistrableFilter;
import com.uib.web.peptideshaker.uimanager.SelectionManager;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Slider;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.awt.Rectangle;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.ui.RectangleInsets;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class represents range component with lower and upper range
 *
 * @author Yehia Mokhtar Farag
 */
public abstract class DivaRangeFilter extends AbsoluteLayout implements Property.ValueChangeListener, RegistrableFilter {

    private final String filterId;
    private final SelectionManager selectionManager;
    private final VerticalLayout chartContainer;
    private final Label chartImage;
    private final Slider upperRangeSlider;
    private final Slider lowerRangeSlider;
    private final AbsoluteLayout slidersContainer;
    private final TreeMap<Comparable, Set<Integer>> activeData;
    private final Label chartTitle;
    private final JFreeChart mainChart;
    private final FilterButton resetFilterBtn;
    private final String title;
    private final String sign;
    private int chartWidth;
    private int chartHeight;
    private double maxValue;

    public DivaRangeFilter(String title, String filterId, SelectionManager selectionManager) {

        DivaRangeFilter.this.setStyleName("rangefilter");
        this.filterId = filterId;
        if (title.contains("(")) {
            this.title = title.split("\\(")[0];
            this.sign = title.split("\\(")[1].replace(")", "");
        } else {
            this.title = title;
            this.sign = "";
        }

        this.selectionManager = selectionManager;
        this.activeData = new TreeMap<>();
        DivaRangeFilter.this.setSizeFull();

        AbsoluteLayout frame = new AbsoluteLayout();
        frame.setSizeFull();
        frame.setStyleName("innerborderframe");
        frame.addStyleName("thumbfilterframe");
        frame.addStyleName("reorderlayout");
        DivaRangeFilter.this.addComponent(frame);

        chartTitle = new Label("<font>" + title + "</font>", ContentMode.HTML);
        chartTitle.setWidth(100, Unit.PERCENTAGE);
        chartTitle.setHeight(20, Unit.PIXELS);
        chartTitle.setStyleName(ValoTheme.LABEL_BOLD);
        chartTitle.addStyleName("resizeabletext");
        chartTitle.addStyleName("toppanel");
        frame.addComponent(chartTitle, "left:10px;top:10px;");

        this.selectionManager.RegistrDatasetsFilter(DivaRangeFilter.this);

        chartContainer = new VerticalLayout();
        chartContainer.setStyleName("chartcontainer");
        chartContainer.setWidth(100, Unit.PERCENTAGE);
        chartContainer.setHeight(100, Unit.PERCENTAGE);
        chartContainer.setMargin(new MarginInfo(false, false, false, false));
        chartImage = new Label("", ContentMode.HTML);
        chartImage.setSizeFull();
        chartImage.addStyleName("labeasimg");
        chartContainer.addComponent(chartImage);
        SizeReporter reporter = new SizeReporter(chartContainer);
        mainChart = initChart();
        reporter.addResizeListener((ComponentResizeEvent event) -> {
            int tChartWidth = event.getWidth();
            int tChartHeight = event.getHeight();
            if (tChartWidth <= 0 || tChartHeight <= 0) {
                return;
            }
            if ((tChartWidth == chartWidth || Math.abs(tChartWidth - chartWidth) < 10) && (chartHeight == tChartHeight || Math.abs(tChartHeight - chartHeight) < 10)) {
                return;
            }
            chartWidth = tChartWidth;
            chartHeight = tChartHeight;
            if (!activeData.isEmpty()) {
                chartImage.setValue(saveToFile(mainChart, chartWidth, chartHeight));
            }
        });

        frame.addComponent(chartContainer, "top: 30px;left: 21px;right: 9px;bottom: 18px;");
        slidersContainer = new AbsoluteLayout();
        slidersContainer.setStyleName("maxhight20");
        slidersContainer.addStyleName("visibleoverflow");
        slidersContainer.setWidth(100, Unit.PERCENTAGE);
        slidersContainer.setHeight(20, Unit.PIXELS);
        frame.addComponent(slidersContainer, "left:21px;bottom:-4px; ;right:7px");

        lowerRangeSlider = new Slider();
        lowerRangeSlider.setWidth(100, Unit.PERCENTAGE);
        lowerRangeSlider.setStyleName("rangeslider");
        lowerRangeSlider.addStyleName("lower");
        lowerRangeSlider.addValueChangeListener(DivaRangeFilter.this);
        slidersContainer.addComponent(lowerRangeSlider, "left:0px; top:50%;");

        upperRangeSlider = new Slider();
        upperRangeSlider.setStyleName("rangeslider");
        upperRangeSlider.addStyleName("upper");
        upperRangeSlider.setWidth(100, Unit.PERCENTAGE);
        upperRangeSlider.addValueChangeListener(DivaRangeFilter.this);
        slidersContainer.addComponent(upperRangeSlider, "left:0px; top:50%;");
        resetFilterBtn = new FilterButton() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                reset();

            }
        };
        resetFilterBtn.setWidth(15, Unit.PIXELS);
        resetFilterBtn.setHeight(15, Unit.PIXELS);
        resetFilterBtn.setVisible(false);
        resetFilterBtn.addStyleName("btninframe");
        DivaRangeFilter.this.addComponent(resetFilterBtn, "top:0px;right:0px;");

        Label label = new Label("<center>#Proteins</center>", ContentMode.HTML);
        label.setStyleName("verticallabel");
        label.setWidth(20, Unit.PIXELS);
        label.setHeight(100, Unit.PERCENTAGE);
        frame.addComponent(label, "top: 22px;left: 1px;bottom: 18px;");

    }

    private void updateChartDataset() {
        XYPlot plot = ((XYPlot) mainChart.getPlot());
        XYSeriesCollection dataset = (XYSeriesCollection) plot.getDataset();
//        dataset.removeAllSeries();
        final XYSeries series1 = new XYSeries("rangeData");
        for (int index = (int) lowerRangeSlider.getMin(); index <= (int) lowerRangeSlider.getMax(); index++) {
            if (activeData.containsKey(index)) {
                series1.add(index, scaleValues(activeData.get(index).size(), 100, 10));
            } else {
                series1.add(index, 0);
            }
        }
        dataset.addSeries(series1);
        plot.setDataset(dataset);
        XYAreaRenderer renderer = (XYAreaRenderer) ((XYPlot) mainChart.getPlot()).getRenderer();
        renderer.setSeriesPaint(0, new Color(211, 211, 211), true);
        if (lowerRangeSlider.getMin() > 0) {
            ((NumberAxis) plot.getDomainAxis()).setAutoRangeMinimumSize(lowerRangeSlider.getMin());
        }
        ((NumberAxis) plot.getDomainAxis()).setFixedAutoRange(lowerRangeSlider.getMax() - lowerRangeSlider.getMin());
        plot.setRenderer(renderer);

    }

    private JFreeChart initChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        final NumberAxis domainAxis = new NumberAxis();
        domainAxis.setVisible(false);
        domainAxis.setAutoRange(true);
        domainAxis.setAutoRangeIncludesZero(false);
        final NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setVisible(false);
        XYAreaRenderer renderer = new XYAreaRenderer();
        renderer.setSeriesPaint(0, new Color(211, 211, 211), true);
        XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);
        plot.setOutlineVisible(false);
        plot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.getDomainAxis().setLowerMargin(0.0D);
        plot.getDomainAxis().setUpperMargin(0.0D);
        plot.setBackgroundPaint(Color.WHITE);
        final JFreeChart chart = new JFreeChart(plot);
        chart.setPadding(new RectangleInsets(0.0, 0.0, 0.0, 0.0));
        plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
        chart.setBorderPaint(null);
        chart.setBackgroundPaint(null);
        return chart;

    }

    /**
     * Convert JFree chart into image and encode it as base64 string to be used
     * as image link.
     *
     * @param chart JFree chart instance
     * @param width Image width
     * @param height Image height.
     */
    private String saveToFile(final JFreeChart chart, int width, int height) {
        if (activeData.isEmpty()) {
            return "";
        }
        chart.getLegend().setVisible(false);
        chart.fireChartChanged();
        SVGGraphics2D g2 = new SVGGraphics2D(width, height);
        Rectangle r = new Rectangle(0, 0, width, height);
        chart.draw(g2, r);
        return g2.getSVGElement();
    }

    @Override
    public void updateFilterSelection(Set<Comparable> selectedItems, Set<Comparable> selectedCategories, boolean topFilter, boolean singleProteinsFilter, boolean selfAction) {

    }

    private void redrawRangeOnChart(double start, double end, boolean highlight) {

        XYSeriesCollection dataset = (XYSeriesCollection) ((XYPlot) mainChart.getPlot()).getDataset();
        if (dataset.getSeriesCount() == 2) {
            dataset.removeSeries(0);
        }

        final XYSeries series1 = new XYSeries("highlightedData");
        final XYSeries series2 = dataset.getSeries("rangeData");

        for (int index = 0; index < series2.getItemCount(); index++) {
            double value = series2.getDataItem(index).getXValue();
            if (value >= start && value <= end) {
                series1.add(value, series2.getDataItem(index).getYValue());
            }

        }

        dataset.removeAllSeries();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        XYAreaRenderer renderer = (XYAreaRenderer) ((XYPlot) mainChart.getPlot()).getRenderer();
        renderer.setSeriesPaint(1, new Color(211, 211, 211), true);
        if (highlight) {
            renderer.setSeriesPaint(0, new Color(186, 213, 242), true);
        } else {
            renderer.setSeriesPaint(0, new Color(211, 211, 211), true);
        }
        if (chartWidth <= 0 || chartHeight <= 0) {
            return;
        }
        chartImage.setVisible(true);
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        upperRangeSlider.removeStyleName("lower");
        upperRangeSlider.removeStyleName("upper");
        lowerRangeSlider.removeStyleName("lower");
        lowerRangeSlider.removeStyleName("upper");
        AbsoluteLayout.ComponentPosition lowerPos = slidersContainer.getPosition(lowerRangeSlider);
        AbsoluteLayout.ComponentPosition upperPos = slidersContainer.getPosition(upperRangeSlider);
        slidersContainer.removeAllComponents();
        double min;
        double max;
        if (lowerRangeSlider.getValue() <= upperRangeSlider.getValue() && slidersContainer.getComponentCount() == 0) {
            slidersContainer.addComponent(lowerRangeSlider, lowerPos.getCSSString());
            slidersContainer.addComponent(upperRangeSlider, upperPos.getCSSString());
            upperRangeSlider.addStyleName("upper");
            lowerRangeSlider.addStyleName("lower");
            min = lowerRangeSlider.getValue();
            max = upperRangeSlider.getValue();
        } else {
            slidersContainer.addComponent(upperRangeSlider, upperPos.getCSSString());
            slidersContainer.addComponent(lowerRangeSlider, lowerPos.getCSSString());
            lowerRangeSlider.addStyleName("upper");
            upperRangeSlider.addStyleName("lower");
            min = upperRangeSlider.getValue();
            max = lowerRangeSlider.getValue();

        }
        selectCategory(activeData.subMap((int) min, (int) max + 1).keySet());

    }

    @Override
    public String getFilterId() {
        return filterId;
    }

    @Override
    public void redrawChart() {
//        applyFilter(lowerRangeSlider.getMin(), lowerRangeSlider.getMax());
    }

    /**
     * Converts the value from linear scale to log scale. The log scale numbers
     * are limited by the range of the type float. The linear scale numbers can
     * be any double value.
     *
     * @param linearValue the value to be converted to log scale
     * @param max The upper limit number for the input numbers
     * @param lowerLimit the lower limit for the input numbers
     * @return the value in log scale
     */
    private double scaleValues(double linearValue, double max, double lowerLimit) {
        double logMax = (Math.log(max) / Math.log(2));
        double logValue = (Math.log(linearValue) / Math.log(2));
        logValue = ((max / logMax) * logValue) + lowerLimit;//(max/Math.log(max))*Math.log(linearValue)+10;
        return logValue;
    }

    @Override
    public void suspendFilter(boolean suspendFilter) {
            Label noquant = new Label("<center> No data available </center>", ContentMode.HTML);
            noquant.setSizeFull();
            noquant.setStyleName("noquantlabel");
            this.removeAllComponents();
            this.addComponent(noquant);
        
    }

    private void setMainAppliedFilter(boolean mainAppliedFilter) {
        resetFilterBtn.setVisible(mainAppliedFilter);
        if (mainAppliedFilter) {
            this.addStyleName("highlightfilter");
        } else {
            this.removeStyleName("highlightfilter");
        }

    }

    @Override
    public void updateSelection(FilterUpdatingEvent event) {
        System.out.println("at event "+event.getSelectionMap().size()+ "  "+filterId);
        activeData.clear();
        try {

            upperRangeSlider.setEnabled(!event.getSelectionMap().isEmpty());
            lowerRangeSlider.setEnabled(!event.getSelectionMap().isEmpty());
            if (event.getSelectionMap().isEmpty()) {
                chartContainer.removeAllComponents();
                return;
            }
            event.getSelectionMap().keySet().forEach((compKey) -> {
                if ((int) compKey != -1 && !event.getSelectionMap().get(compKey).isEmpty()) {
                    if (!activeData.containsKey(compKey)) {
                        activeData.put(compKey, new HashSet<>());
                    }
                    activeData.get(compKey).addAll(event.getSelectionMap().get(compKey));
                }
            });
            if (event.getSeletionCategories() != null && event.getSeletionCategories().containsAll(activeData.keySet())) {
                reset();
                return;
            }
            double min = 0.0;//Double.valueOf(data.firstKey() + "");
            if (sign.equalsIgnoreCase("")) {
                maxValue = Double.valueOf(activeData.lastKey().toString());
            } else {
                maxValue = 100.0;
            }
            upperRangeSlider.removeValueChangeListener(DivaRangeFilter.this);
            lowerRangeSlider.removeValueChangeListener(DivaRangeFilter.this);
            lowerRangeSlider.setMin(min);
            lowerRangeSlider.setMax(maxValue);

            upperRangeSlider.setMin(min);
            upperRangeSlider.setMax(maxValue);

            chartTitle.setValue("<font>" + title + "</font> [" + (int) min + " " + "&#x2014;" + " " + (int) maxValue + "] " + sign);
            UI.getCurrent().accessSynchronously(() -> {
                updateChartDataset();
                if ((event.getSeletionCategories() != null && !event.getSeletionCategories().isEmpty())) {
                    TreeSet<Comparable> sortedSet = new TreeSet<>(event.getSeletionCategories());
                    double minValue = Double.parseDouble(sortedSet.first().toString());
                    double maxValue = Double.parseDouble(sortedSet.last().toString());
                    upperRangeSlider.setValue(maxValue);
                    lowerRangeSlider.setValue(minValue);
                    redrawRangeOnChart(minValue, maxValue, true);
                    setMainAppliedFilter(true);
                } else {
                    upperRangeSlider.setValue(maxValue);
                    lowerRangeSlider.setValue(min);
                    setMainAppliedFilter(false);
                }

                upperRangeSlider.addValueChangeListener(DivaRangeFilter.this);
                lowerRangeSlider.addValueChangeListener(DivaRangeFilter.this);
//                updateChartDataset();
                chartImage.setValue(saveToFile(mainChart, chartWidth, chartHeight));
                UI.getCurrent().push();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectCategory(Set<Comparable> categories) {
        Selection selection = new Selection(CONSTANT.DATASET_SELECTION, filterId, categories, true);
        selectionManager.setSelection(selection);
    }

    private void reset() {
        Selection selection = new Selection(CONSTANT.DATASET_SELECTION, filterId, null, false);
        selectionManager.setSelection(selection);
    }

    public void forceRest() {
        if (this.getStyleName().contains("highlightfilter")) {
            reset();
        }
    }
}
