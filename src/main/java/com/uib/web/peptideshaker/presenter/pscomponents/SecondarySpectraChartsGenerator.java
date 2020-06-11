package com.uib.web.peptideshaker.presenter.pscomponents;

import com.compomics.util.experiment.biology.proteins.Peptide;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.spectrum_annotation.AnnotationParameters;
import com.compomics.util.experiment.identification.spectrum_annotation.SpecificAnnotationParameters;
import com.compomics.util.experiment.identification.spectrum_annotation.spectrum_annotators.PeptideSpectrumAnnotator;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.io.biology.protein.SequenceProvider;
import com.compomics.util.gui.spectrum.MassErrorPlot;
import com.compomics.util.gui.spectrum.SequenceFragmentationPanel;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.advanced.SequenceMatchingParameters;
import com.compomics.util.parameters.identification.search.ModificationParameters;
import com.compomics.util.parameters.identification.search.SearchParameters;
import com.itextpdf.text.pdf.codec.Base64;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.encoders.ImageEncoder;
import org.jfree.chart.encoders.ImageEncoderFactory;
import org.jfree.chart.encoders.ImageFormat;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.graphics2d.svg.SVGGraphics2D;

/**
 *
 * @author Yehia Farag this class represents spectra chart in relation with
 * peptide sequence
 */
public class SecondarySpectraChartsGenerator {

    private final Image sequenceFragmentationChartComponent;
    private final Label massErrorPlotComponent;
//    private final Label sequenceLabel;
    private final Object objectId;
    private int imgW = -1;
    private int imgH = -1;
    private String base64;
    private MassErrorPlot errorPlot;
    private final VerticalLayout sequenceFragmentationChart;
    private final VerticalLayout massErrorPlot;

    public Object getObjectId() {
        return objectId;
    }

    public VerticalLayout getSequenceFragmentationChart() {
        return sequenceFragmentationChart;
    }

    public VerticalLayout getMassErrorPlot() {
        return massErrorPlot;
    }

    public SecondarySpectraChartsGenerator(String sequence, String tooltip, Object objectId, SpectrumInformation spectrumInformation) {
        this.sequenceFragmentationChart = new VerticalLayout();
        this.massErrorPlot = new VerticalLayout();
        this.sequenceFragmentationChart.setHeight(70, Unit.PIXELS);
        this.sequenceFragmentationChart.setWidth(100, Unit.PERCENTAGE);
        this.massErrorPlot.setHeight(70, Unit.PIXELS);
        this.massErrorPlot.setWidth(100, Unit.PERCENTAGE);
        SecondarySpectraChartsGenerator.this.sequenceFragmentationChart.setStyleName("sequencefragmentationchart");
        SecondarySpectraChartsGenerator.this.sequenceFragmentationChart.setData(objectId);
        SecondarySpectraChartsGenerator.this.massErrorPlot.setStyleName("sequencefragmentationchart");
        SecondarySpectraChartsGenerator.this.massErrorPlot.setData(objectId);
        this.sequenceFragmentationChartComponent = new Image();
        this.massErrorPlotComponent = new Label();
        this.massErrorPlotComponent.setContentMode(ContentMode.HTML);
        this.massErrorPlotComponent.setHeight(100, Unit.PERCENTAGE);
        this.massErrorPlotComponent.setWidth(100, Unit.PERCENTAGE);
        this.sequenceFragmentationChart.addComponent(this.sequenceFragmentationChartComponent);
        this.massErrorPlot.addComponent(this.massErrorPlotComponent);
        sequenceFragmentationChart.setDescription(tooltip);
        this.objectId = objectId;
        // create the sequence fragment ion view
        double accuracy = spectrumInformation.getFragmentIonAccuracy();

        // get the spectrum annotations
        PeptideAssumption peptideAssumption = spectrumInformation.getSpectrumMatch().getBestPeptideAssumption();
        Peptide currentPeptide = peptideAssumption.getPeptide();
        PeptideSpectrumAnnotator spectrumAnnotator = new PeptideSpectrumAnnotator();
        AnnotationParameters annotationParameters = spectrumInformation.getIdentificationParameters().getAnnotationParameters();
        annotationParameters.setIntensityLimit(0.75);
        annotationParameters.setFragmentIonAccuracy(accuracy);

        SequenceProvider sequenceProvider = spectrumInformation.getSequenceProvider();//peptideShakerGUI.getSequenceProvider();
        IdentificationParameters identificationParameters = spectrumInformation.getIdentificationParameters();
        ModificationParameters modificationParameters = identificationParameters.getSearchParameters().getModificationParameters();
        identificationParameters.setAnnotationParameters(annotationParameters);
        SequenceMatchingParameters modificationSequenceMatchingParameters = identificationParameters.getModificationLocalizationParameters().getSequenceMatchingParameters();

        SpecificAnnotationParameters specificAnnotationParameters = annotationParameters.getSpecificAnnotationParameters(spectrumInformation.getSpectrum().getSpectrumKey(), peptideAssumption, modificationParameters, sequenceProvider, modificationSequenceMatchingParameters, spectrumAnnotator);

        IonMatch[] annotations = spectrumAnnotator.getSpectrumAnnotation(annotationParameters, specificAnnotationParameters, spectrumInformation.getSpectrum(), currentPeptide,
                modificationParameters, sequenceProvider, modificationSequenceMatchingParameters);
//        PeptideAssumption peptideAssumption = spectrumInformation.getSpectrumMatch().getBestPeptideAssumption();
//        Peptide currentPeptide = peptideAssumption.getPeptide();
//        AnnotationParameters annotationParameters = spectrumInformation.getIdentificationParameters().getAnnotationParameters();

//        PeptideSpectrumAnnotator spectrumAnnotator = new PeptideSpectrumAnnotator();
//        
//        SpecificAnnotationParameters specificAnnotationParameters = new SpecificAnnotationParameters(spectrumInformation.getSpectrum().getSpectrumKey(), peptideAssumption);
//
//        spectrumInformation.getIdentificationParameters().setAnnotationParameters(annotationParameters);
//        specificAnnotationParameters = annotationParameters.getSpecificAnnotationParameters(spectrumInformation.getSpectrum().getSpectrumKey(), peptideAssumption, modificationParameters, sequenceProvider, SequenceMatchingParameters.defaultStringMatching, spectrumAnnotator) //        specificAnnotationParameters = annotationParameters.getSpecificAnnotationParameters(spectrumInformation.getSpectrum().getSpectrumKey(), specificAnnotationParameters.getSpectrumIdentificationAssumption(), spectrumInformation.getIdentificationParameters().getSequenceMatchingParameters(),spectrumAnnotator);
//
//        SequenceProvider sequenceProvider = null;// spectrumInformation.getIdentificationParameters().gets
//        SequenceMatchingParameters modificationSequenceMatchingParameters = spectrumInformation.getIdentificationParameters().getModificationLocalizationParameters().getSequenceMatchingParameters();
//
//        IonMatch[] annotations = spectrumAnnotator.getSpectrumAnnotation(annotationParameters, specificAnnotationParameters, spectrumInformation.getSpectrum(), currentPeptide,
//                spectrumInformation.getIdentificationParameters().getSearchParameters().getModificationParameters(), sequenceProvider, modificationSequenceMatchingParameters);
        Integer forwardIon = spectrumInformation.getIdentificationParameters().getSearchParameters().getForwardIons().get(0);
        Integer rewindIon = spectrumInformation.getIdentificationParameters().getSearchParameters().getRewindIons().get(0);//

        HashSet<String> modifications = new HashSet<>();
        for (ModificationMatch mm : currentPeptide.getVariableModifications()) {
            modifications.add(mm.getModification());
        }
        String taggedPeptideSequence = currentPeptide.getTaggedModifiedSequence(modificationParameters, sequenceProvider, modificationSequenceMatchingParameters, false, false, true, modifications);

//        String taggedPeptideSequence = currentPeptide.getTaggedModifiedSequence(spectrumInformation.getIdentificationParameters().getSearchParameters().getModificationParameters(), sequenceProvider, modificationSequenceMatchingParameters, false, false, false,);
        SequenceFragmentationPanel sequenceFragmentationPanel = new SequenceFragmentationPanel(
                taggedPeptideSequence,
                annotations, true, identificationParameters.getSearchParameters().getModificationParameters(), forwardIon, rewindIon);
        sequenceFragmentationPanel.setMinimumSize(new Dimension(sequenceFragmentationPanel.getPreferredSize().width, sequenceFragmentationPanel.getHeight()));

//
//
//SequenceFragmentationPanel sequenceFragmentationPanel = new SequenceFragmentationPanel(sequence, annotations, false, false, spectrumInformation.getIdentificationParameters().getSearchParameters().getModificationParameters(), forwardIon, rewindIon);
        sequenceFragmentationPanel.setOpaque(true);
        sequenceFragmentationPanel.setBackground(Color.WHITE);
        sequenceFragmentationPanel.setSize(1000, 68);
        sequenceFragmentationChartComponent.setSource(new ExternalResource(drawImage(sequenceFragmentationPanel)));
        //            errorPlot = new MassErrorPlot(annotations, spectrumInformation.getSpectrum(), accuracy, spectrumInformation.getIdentificationParameters().getSearchParameters().getFragmentAccuracyType() == SearchParameters.MassAccuracyType.PPM);

        // create the miniature mass error plot
        errorPlot = new MassErrorPlot(annotations, spectrumInformation.getSpectrum(),
                specificAnnotationParameters.getFragmentIonAccuracy(),
                spectrumInformation.getIdentificationParameters().getSearchParameters().getFragmentAccuracyType() == SearchParameters.MassAccuracyType.PPM);
        errorPlot.setSize(300, 68);
        if (errorPlot.getChartPanel() != null) {
            errorPlot.getChartPanel().setSize(300, 68);
            errorPlot.updateUI();
            XYPlot plot = (XYPlot) errorPlot.getChartPanel().getChart().getPlot();
            plot.getDomainAxis().setVisible(false);
            plot.getRangeAxis().setVisible(false);
            plot.setRangeGridlinesVisible(false);
            plot.setRangeZeroBaselineVisible(true);
            plot.setRangeZeroBaselinePaint(Color.LIGHT_GRAY);
            plot.setRangeZeroBaselineStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f}, 0.0f));
            plot.getDomainAxis().setUpperBound(plot.getDomainAxis().getUpperBound() + 30);
//            plot.getRangeAxis().setUpperBound(plot.getRangeAxis().getUpperBound()+30);

            DefaultXYItemRenderer renderer = (DefaultXYItemRenderer) plot.getRenderer();
            for (int i = 0; i < plot.getSeriesCount(); i++) {
                renderer.setSeriesShape(i, new Ellipse2D.Double(-2.0, -2.0, 4.0, 4.0));
            }
            plot.setRenderer(renderer);
            massErrorPlotComponent.setValue(drawImage(errorPlot.getChartPanel().getChart(), 270, 68));
        } else {
            System.out.println("at error plot panel was null");
        }

    }

    public void reset() {
//        this.plotImage.setVisible(false);
//        this.sequenceLabel.setVisible(true);

    }
    int count = 0;

    private String drawImage(JPanel panel) {
        panel.revalidate();
        panel.repaint();
        if (panel.getWidth() <= 0) {
            panel.setSize(100, panel.getHeight());
        }
        if (panel.getHeight() <= 0) {
            panel.setSize(panel.getWidth(), 100);
        }
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        imgH = panel.getHeight();
        imgW = panel.getWidth();
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        //draw sequence line
        panel.paint(g2d);
        byte[] imageData = null;
        try {
            ImageEncoder in = ImageEncoderFactory.newInstance(ImageFormat.PNG, 1);
            imageData = in.encode(image);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        base64 = Base64.encodeBytes(imageData);
        base64 = "data:image/png;base64," + base64;
        //total chain coverage     
        return base64;
    }

    private String drawImage(JFreeChart chart, int imgW, int imgH) {

//         chart.getLegend().setVisible(false);
//        chart.fireChartChanged();
        SVGGraphics2D g2 = new SVGGraphics2D(imgW, imgH);
        Rectangle r = new Rectangle(0, 0, imgW, imgH);
        chart.draw(g2, r);
        return g2.getSVGElement();

//        BufferedImage image = chart.createBufferedImage(imgW, imgH);
//
//        byte[] imageData = null;
//        try {
//            ImageEncoder in = ImageEncoderFactory.newInstance(ImageFormat.PNG, 1);
//            imageData = in.encode(image);
//        } catch (IOException e) {
//            System.out.println(e.getLocalizedMessage());
//        }
//        base64 = Base64.encodeBytes(imageData);
//        base64 = "data:image/png;base64," + base64;
//        //total chain coverage     
//        return base64;
    }

}
