package com.uib.web.peptideshaker.presenter.core.filtercharts.filters;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.google.common.collect.Sets;
import com.uib.web.peptideshaker.presenter.core.FilterButton;
import com.uib.web.peptideshaker.presenter.core.filtercharts.RegistrableFilter;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.SelectionManager;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Slider;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.ui.RectangleInsets;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class represents range component with lower and upper range
 *
 * @author Yehia Farag
 */
public abstract class DivaRangeFilter extends AbsoluteLayout implements Property.ValueChangeListener, RegistrableFilter {

    private final String filterId;
    private final SelectionManager Selection_Manager;

    private final VerticalLayout chartContainer;
    private final Label chartImage;
    private final Slider upperRangeSlider;
    private final Slider lowerRangeSlider;
    private final AbsoluteLayout slidersContainer;
    private final TreeMap<Comparable, Set<Comparable>> activeData;
    private final Label chartTitle;
    private final JFreeChart mainChart;
    private final FilterButton resetFilterBtn;
    private final String title;
    private final String sign;
    private TreeMap<Comparable, Set<Comparable>> data;
    private int chartWidth;
    private int chartHeight;
    private double maxValue;
    private boolean suspendFilter;
    private Set<Comparable> lastselectedItems = new LinkedHashSet<>();
    private Set<Comparable> lastselectedCategories = new LinkedHashSet<>();

    public DivaRangeFilter(String title, String filterId, SelectionManager Selection_Manager) {

        DivaRangeFilter.this.setStyleName("rangefilter");
        this.filterId = filterId;
        if (title.contains("(")) {
            this.title = title.split("\\(")[0];
            this.sign = title.split("\\(")[1].replace(")", "");
        } else {
            this.title = title;
            this.sign = "";
        }

        this.Selection_Manager = Selection_Manager;
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

        this.Selection_Manager.RegistrDatasetsFilter(DivaRangeFilter.this);

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
            chartImage.setValue(saveToFile(mainChart, chartWidth, chartHeight));
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
                applyFilter(lowerRangeSlider.getMin(), lowerRangeSlider.getMax());

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

    public void initializeFilterData(TreeMap<Comparable, Set<Comparable>> data) {
        activeData.clear();
        if (data.isEmpty()) {
            upperRangeSlider.setEnabled(false);
            lowerRangeSlider.setEnabled(false);
            chartContainer.removeAllComponents();
            return;
        }

        upperRangeSlider.setEnabled(true);
        lowerRangeSlider.setEnabled(true);
        double min = 0.0;//Double.valueOf(data.firstKey() + "");
        if (sign.equalsIgnoreCase("")) {
            maxValue = Double.valueOf(data.lastKey() + "");
        } else {
            maxValue = 100.0;
        }
        upperRangeSlider.removeValueChangeListener(DivaRangeFilter.this);
        lowerRangeSlider.removeValueChangeListener(DivaRangeFilter.this);
        lowerRangeSlider.setMin(min);
        lowerRangeSlider.setMax(maxValue);
        lowerRangeSlider.setValue(min);
        upperRangeSlider.setMin(min);
        upperRangeSlider.setMax(maxValue);
        upperRangeSlider.setValue(maxValue);

        upperRangeSlider.addValueChangeListener(DivaRangeFilter.this);
        lowerRangeSlider.addValueChangeListener(DivaRangeFilter.this);
        chartTitle.setValue("<font>" + title + "</font> [" + (int) min + " " + "&#x2014;" + " " + (int) maxValue + "] " + sign);
        activeData.putAll(data);
        updateChartDataset();
        if (this.data == null) {
            this.data = data;
        }

    }

    private void updateChartDataset() {
        XYPlot plot = ((XYPlot) mainChart.getPlot());
        XYSeriesCollection dataset = (XYSeriesCollection) plot.getDataset();
        dataset.removeAllSeries();
        final XYSeries series1 = new XYSeries("rangeData");

        for (int index = (int) lowerRangeSlider.getMin(); index <= (int) lowerRangeSlider.getMax(); index++) {
            if (activeData.containsKey(index)) {
                series1.add(index, scaleValues(activeData.get(index).size(), 100, 10));
            } else {
                series1.add(index, 0);
            }

        }
        dataset.addSeries(series1);
        XYAreaRenderer renderer = (XYAreaRenderer) ((XYPlot) mainChart.getPlot()).getRenderer();
        renderer.setSeriesPaint(0, new Color(211, 211, 211), true);
        if (lowerRangeSlider.getMin() > 0) {
            ((NumberAxis) plot.getDomainAxis()).setAutoRangeMinimumSize(lowerRangeSlider.getMin());

        }
        ((NumberAxis) plot.getDomainAxis()).setFixedAutoRange(lowerRangeSlider.getMax() - lowerRangeSlider.getMin());

    }

    private JFreeChart initChart() {

        XYSeriesCollection dataset = new XYSeriesCollection();
        final NumberAxis domainAxis = new NumberAxis();
        domainAxis.setVisible(false);
        domainAxis.setAutoRange(true);
        domainAxis.setAutoRangeIncludesZero(false);

        final NumberAxis rangeAxis = new NumberAxis() {

        };

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
     * @param chart  JFree chart instance
     * @param width  Image width
     * @param height Image height.
     */
    private String saveToFile(final JFreeChart chart, int width, int height) {

        chart.getLegend().setVisible(false);
        chart.fireChartChanged();
        SVGGraphics2D g2 = new SVGGraphics2D(width, height);
        Rectangle r = new Rectangle(0, 0, width, height);
        chart.draw(g2, r);
        return g2.getSVGElement();
    }

    public void valueChange() {
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
        chartTitle.setValue("<font>" + title + "</font> [" + (int) min + " " + "&#x2014;" + " " + (int) max + "] " + sign);
        applyFilter(min, max);

    }

    private void applyFilter(double min, double max) {
        LinkedHashSet filter = new LinkedHashSet<>();
        if (min != lowerRangeSlider.getMin() || max != lowerRangeSlider.getMax()) {
            filter.addAll(Arrays.asList(new Comparable[]{min, max}));
        }
        Selection_Manager.setSelection("dataset_filter_selection", filter, null, filterId);
    }

    @Override
    public void updateFilterSelection(Set<Comparable> selectedItems, Set<Comparable> selectedCategories, boolean topFilter, boolean singleProteinsFilter, boolean selfAction) {
        if (suspendFilter || selectedItems == null || selectedCategories == null || (lastselectedItems.containsAll(selectedItems) && lastselectedCategories.containsAll(selectedCategories) && selectedCategories.size() == lastselectedCategories.size() && selectedItems.size() == lastselectedItems.size())) {
            return;
        }
        lastselectedItems = selectedItems;
        lastselectedCategories = selectedCategories;
        if (!selfAction) {
            if (selectedItems.isEmpty()) {
                return;
            }

            if (singleProteinsFilter && !selectedCategories.isEmpty()) {
                //reset filter value to oreginal 
                initializeFilterData(this.data);
            } else {
                TreeMap<Comparable, Set<Comparable>> tPieChartValues = new TreeMap<>();
                data.keySet().forEach((key) -> {
                    LinkedHashSet<Comparable> tSet = new LinkedHashSet<>(Sets.intersection(data.get(key), selectedItems));
                    if (!tSet.isEmpty()) {
                        tPieChartValues.put(key, new LinkedHashSet<>(Sets.intersection(data.get(key), selectedItems)));
                    }
                });
                initializeFilterData(tPieChartValues);
            }
        }
        if (selectedCategories.isEmpty()) {
            if (chartWidth <= 0 || chartHeight <= 0) {
                return;
            }
            XYSeriesCollection dataset = (XYSeriesCollection) ((XYPlot) mainChart.getPlot()).getDataset();
            if (dataset.getSeriesCount() == 2) {
                dataset.removeSeries(0);
            }
            redrawRangeOnChart(Math.min(lowerRangeSlider.getValue(), upperRangeSlider.getValue()), Math.max(lowerRangeSlider.getValue(), upperRangeSlider.getValue()), false);
            setMainAppliedFilter(false);
            return;
        }
        double min = Math.min(lowerRangeSlider.getValue(), upperRangeSlider.getValue());
        double max = Math.max(lowerRangeSlider.getValue(), upperRangeSlider.getValue());

        double tmin = (double) selectedCategories.toArray()[0];
        double tmax;
        if (selectedCategories.size() == 1) {
            tmax = min;
        } else {
            tmax = (double) selectedCategories.toArray()[1];
        }
        min = Math.max(min, tmin);
        max = Math.min(max, tmax);
        redrawRangeOnChart(min, max, true);
        setMainAppliedFilter(topFilter);
    }

    private void redrawRangeOnChart(double start, double end, boolean highlight) {
        if (start != -1 && end != -1) {
            upperRangeSlider.removeValueChangeListener(DivaRangeFilter.this);
            lowerRangeSlider.removeValueChangeListener(DivaRangeFilter.this);
            if (lowerRangeSlider.getStyleName().contains("lower")) {
                lowerRangeSlider.setValue(start);
                upperRangeSlider.setValue(end);
            } else {
                upperRangeSlider.setValue(start);
                lowerRangeSlider.setValue(end);
            }
            upperRangeSlider.addValueChangeListener(DivaRangeFilter.this);
            lowerRangeSlider.addValueChangeListener(DivaRangeFilter.this);

        }

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
        chartImage.setValue(saveToFile(mainChart, chartWidth, chartHeight));
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        valueChange();
    }

    @Override
    public String getFilterId() {
        return filterId;
    }

    @Override
    public void redrawChart() {
        applyFilter(lowerRangeSlider.getMin(), lowerRangeSlider.getMax());
    }

    /**
     * Converts the value from linear scale to log scale. The log scale numbers
     * are limited by the range of the type float. The linear scale numbers can
     * be any double value.
     *
     * @param linearValue the value to be converted to log scale
     * @param max         The upper limit number for the input numbers
     * @param lowerLimit  the lower limit for the input numbers
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
        this.suspendFilter = suspendFilter;
    }

    private void setMainAppliedFilter(boolean mainAppliedFilter) {
        resetFilterBtn.setVisible(mainAppliedFilter);
        if (mainAppliedFilter) {
            this.addStyleName("highlightfilter");
        } else {

            this.removeStyleName("highlightfilter");

            upperRangeSlider.removeValueChangeListener(DivaRangeFilter.this);
            lowerRangeSlider.removeValueChangeListener(DivaRangeFilter.this);
            lowerRangeSlider.setValue(lowerRangeSlider.getMin());
            upperRangeSlider.setValue(lowerRangeSlider.getMax());

            upperRangeSlider.addValueChangeListener(DivaRangeFilter.this);
            lowerRangeSlider.addValueChangeListener(DivaRangeFilter.this);
        }

    }

    public String externalTitle() {
        chartTitle.setVisible(false);
        return chartTitle.getValue();
    }

}
