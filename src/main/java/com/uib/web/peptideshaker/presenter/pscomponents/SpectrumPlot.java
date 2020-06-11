package com.uib.web.peptideshaker.presenter.pscomponents;

import com.compomics.util.experiment.biology.aminoacids.AminoAcid;
import com.compomics.util.experiment.biology.aminoacids.sequence.AminoAcidPattern;
import com.compomics.util.experiment.biology.ions.Charge;
import com.compomics.util.experiment.biology.ions.Ion.IonType;
import com.compomics.util.experiment.biology.ions.IonFactory;
import com.compomics.util.experiment.biology.ions.NeutralLoss;
import com.compomics.util.experiment.biology.ions.impl.PeptideFragmentIon;
import com.compomics.util.experiment.biology.modifications.Modification;
import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.compomics.util.experiment.biology.modifications.ModificationType;
import com.compomics.util.experiment.biology.proteins.Peptide;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.spectrum_annotation.AnnotationParameters;
import com.compomics.util.experiment.identification.spectrum_annotation.SpecificAnnotationParameters;
import com.compomics.util.experiment.identification.spectrum_annotation.SpectrumAnnotator;
import com.compomics.util.experiment.identification.spectrum_annotation.spectrum_annotators.PeptideSpectrumAnnotator;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.io.biology.protein.SequenceProvider;
import com.compomics.util.experiment.mass_spectrometry.spectra.Precursor;
import com.compomics.util.experiment.mass_spectrometry.spectra.Spectrum;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.advanced.SequenceMatchingParameters;
import com.compomics.util.parameters.identification.search.ModificationParameters;
import com.compomics.util.parameters.identification.search.SearchParameters;
import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.ComponentResizeListener;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.itextpdf.text.pdf.codec.Base64;
import com.vaadin.data.Property;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Slider;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.jfree.chart.encoders.ImageEncoder;
import org.jfree.chart.encoders.ImageEncoderFactory;
import org.jfree.chart.encoders.ImageFormat;
import org.vaadin.simplefiledownloader.SimpleFileDownloader;
import selectioncanvas.SelectioncanvasComponent;

/**
 * This class represents Spectrum Plot extracted from PeptideShaker and
 * converted using DiVA concept
 *
 * @author Yehia Farag
 */
public class SpectrumPlot extends AbsoluteLayout {

    private final Image plot;
    private Image plotThumbImage;
    private SelectioncanvasComponent selectionCanvas;
    private final Slider levelSlider;
    private final Slider annotationAccuracySlider;
    private WebSpectrumPanel spectrumPanel;
    private final SizeReporter mainSizeReporter;
//    private final String[] ions = {"a", "b", "c", "x", "y", "z"};
    private final MenuBar.Command annotationsItemsCommand;
    private final MenuItem ionsItem;
    private final MenuItem otherItem;
    private final MenuItem lossItem;
    private final MenuItem chargeItem;
    private final LinkedHashMap<String, Integer> ions;
    private final LinkedHashMap<String, IonType> others;
    private final MenuItem resetAnnoItem;
    private final MenuItem deNovoItem;
    private final MenuItem settingsItem;
    private boolean disableSizeReporter = false;
    private final ComponentResizeListener compResizeListener;
    private int updatedComponentWidth;
    private int updatedComponentHeight;
    private SequenceProvider sequenceProvider;
    /**
     * Time to wait
     */
    private final int DELAY = 1000;
    /**
     * Waiting timer
     */
    private javax.swing.Timer waitingTimer;

    public boolean isDisableSizeReporter() {
        return disableSizeReporter;
    }

    public void setDisableSizeReporter(boolean disableSizeReporter) {
        this.disableSizeReporter = disableSizeReporter;
    }

    public void setPlotThumbImage(Image plotThumbImage) {
        this.plotThumbImage = plotThumbImage;
        this.plotThumbImage.addStyleName("nopadding");
    }

    public SpectrumPlot() {
//        SpectrumPlot.this.setStyleName("splotframe");
        SpectrumPlot.this.setSizeFull();
        SpectrumPlot.this.setStyleName("spectrumplotstyle");
        ions = new LinkedHashMap<>();
        ions.put("a", PeptideFragmentIon.A_ION);
        ions.put("b", PeptideFragmentIon.B_ION);
        ions.put("c", PeptideFragmentIon.C_ION);
        ions.put("x", PeptideFragmentIon.X_ION);
        ions.put("y", PeptideFragmentIon.Y_ION);
        ions.put("z", PeptideFragmentIon.Z_ION);

        others = new LinkedHashMap<>();
        others.put("Precursor", IonType.PRECURSOR_ION);
        others.put("Immonium", IonType.IMMONIUM_ION);
        others.put("Related", IonType.RELATED_ION);
        others.put("Reporter", IonType.REPORTER_ION);
//                   
//        SpectrumPlot.this.setWidth(1000, Unit.PIXELS);
//        SpectrumPlot.this.setHeight(500, Unit.PIXELS);
//        SpectrumPlot.this.setStyleName(ValoTheme.LAYOUT_WELL);
        plot = new Image() {
            @Override
            public void setSource(Resource source) {
                if (this.getStyleName().contains("imgII")) {
                    this.removeStyleName("imgII");
                    this.addStyleName("imgI");
                } else {
                    this.removeStyleName("imgI");
                    this.addStyleName("imgII");

                }
                super.setSource(source); //To change body of generated methods, choose Tools | Templates.
            }

        };
        plot.setSizeFull();
        plot.addStyleName("psmplotstyle");
        SpectrumPlot.this.addComponent(plot, "left:0px;top:0px;bottom:40px;");
//        selectionCanvas = initSelectionCanvas(1000,500);
//        
//
//        SpectrumPlot.this.addComponent(selectionCanvas, "left:0px;bottom:40px");

        annotationAccuracySlider = new Slider();
        annotationAccuracySlider.setMax(100);
        annotationAccuracySlider.setMin(0);
        annotationAccuracySlider.setStyleName("borderslider");
        annotationAccuracySlider.setHeight(100, Unit.PIXELS);
        annotationAccuracySlider.setCaptionAsHtml(true);
        annotationAccuracySlider.setCaption("Accuracy</br>0.02 Da");
        annotationAccuracySlider.setDescription("Annotation accuracy : 0.02 Da");
//        annotationAccuracySlider.setResolution(3);
        annotationAccuracySlider.setOrientation(SliderOrientation.VERTICAL);

        levelSlider = new Slider();
        levelSlider.setMax(100);
        levelSlider.setMin(0);
        levelSlider.setStyleName("borderslider");
        levelSlider.setHeight(100, Unit.PIXELS);
        levelSlider.setCaptionAsHtml(true);
        levelSlider.setCaption("Level");
        levelSlider.setOrientation(SliderOrientation.VERTICAL);
        HorizontalLayout controlsLayout = new HorizontalLayout();
        controlsLayout.setWidthUndefined();//100, Unit.PERCENTAGE);
        controlsLayout.setHeight(40, Unit.PIXELS);
        controlsLayout.addStyleName("psmcontrolstyle");
        SpectrumPlot.this.addComponent(controlsLayout, "left:0px;bottom:0px");
        MenuBar menue = new MenuBar();
        menue.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
        menue.addStyleName(ValoTheme.MENUBAR_SMALL);
        menue.setSizeFull();
        menue.setAutoOpen(false);
        menue.setHtmlContentAllowed(true);
        controlsLayout.addComponent(menue);

        //initialise ions tab
        ionsItem = menue.addItem("<div class='tinydot'></div>" + VaadinIcons.DOT_CIRCLE.getHtml() + "Ions", null, null);
        otherItem = menue.addItem("<div class='otinydot'></div>" + VaadinIcons.DOT_CIRCLE.getHtml() + "Other", null, null);

        lossItem = menue.addItem(VaadinIcons.MINUS_CIRCLE_O.getHtml() + "Loss", null, null);//"<div class='lossin'>"+VaadinIcons.MINUS_CIRCLE_O.getHtml()+"</div><div class='lossout'>"+VaadinIcons.BAR_CHART.getHtml() +"</div>

        chargeItem = menue.addItem("Charge", VaadinIcons.PLUS, null);
        resetAnnoItem = menue.addItem("Reset Annotations", VaadinIcons.RETWEET, (MenuItem selectedItem) -> {
            selectedItem.setEnabled(false);
            resetAnnotations();
            updateAnnotationPreferences();
        });
        annotationsItemsCommand = (MenuItem selectedItem) -> {
            defaultAnnotationInUse = false;
            if (selectedItem.getText().equalsIgnoreCase("Adapt")) {
                MenuItem H2OItem = lossItem.getChildren().get(0);
                H2OItem.setEnabled(!selectedItem.isChecked());
                MenuItem NH3Item = lossItem.getChildren().get(1);
                NH3Item.setEnabled(!selectedItem.isChecked());
            }
            updateAnnotationPreferences();
            resetAnnoItem.setEnabled(true);
            updateImage(spectrumPanel);
        };
        initIonsItem(ionsItem, annotationsItemsCommand);
        initOtherItem(otherItem, annotationsItemsCommand);

        MenuItem H2OItem = lossItem.addItem("H2O", annotationsItemsCommand);
        H2OItem.setEnabled(false);
        H2OItem.setCheckable(true);
        H2OItem.setChecked(true);
        MenuItem NH3Item = lossItem.addItem("NH3", annotationsItemsCommand);
        NH3Item.setEnabled(false);
        NH3Item.setCheckable(true);
        NH3Item.setChecked(true);
        MenuItem adaptItem = lossItem.addItem("Adapt", annotationsItemsCommand);
        adaptItem.setCheckable(true);
        adaptItem.setChecked(true);
        lossItem.addSeparatorBefore(adaptItem);

        deNovoItem = menue.addItem("De Novo", VaadinIcons.BAR_CHART_H, null);
        MenuBar.Command deNovoItemItemsCommand = (MenuItem selectedItem) -> {
            if (selectedItem.getText().contains("Single Charge")) {
                deNovoItem.getChildren().get(4).setChecked(false);
            } else if (selectedItem.getText().contains("Double Charge")) {
                deNovoItem.getChildren().get(3).setChecked(false);
            }
            updateAnnotationPreferences();
            updateImage(spectrumPanel);
        };
        MenuItem bions = deNovoItem.addItem("b-ions", deNovoItemItemsCommand);
        bions.setCheckable(true);
        bions.setChecked(false);
        MenuItem yions = deNovoItem.addItem("y-ions", deNovoItemItemsCommand);
        yions.setCheckable(true);
        yions.setChecked(false);
        deNovoItem.addSeparator();

        MenuItem singleChargeItem = deNovoItem.addItem("Single Charge", deNovoItemItemsCommand);
        singleChargeItem.setCheckable(true);
        singleChargeItem.setChecked(true);
        MenuItem doubleChargeItem = deNovoItem.addItem("Double Charge", deNovoItemItemsCommand);
        doubleChargeItem.setCheckable(true);
        doubleChargeItem.setChecked(false);

        settingsItem = menue.addItem("Settings", VaadinIcons.COGS, null);
        MenuItem showAllPeaksItem = settingsItem.addItem("Show All Peaks", deNovoItemItemsCommand);
        showAllPeaksItem.setCheckable(true);
        showAllPeaksItem.setChecked(false);
        MenuItem highResolutionItem = settingsItem.addItem("High Resolution", deNovoItemItemsCommand);
        highResolutionItem.setCheckable(true);
        highResolutionItem.setChecked(true);
        settingsItem.addSeparator();
        MenuItem automaticAnnotationItem = settingsItem.addItem("Automatic Annotation", deNovoItemItemsCommand);
        automaticAnnotationItem.setCheckable(true);
        automaticAnnotationItem.setChecked(true);

        SimpleFileDownloader downloader = new SimpleFileDownloader();
        addExtension(downloader);

        

         MenuBar.Command exportCommand = (MenuItem selectedItem) -> {
             StreamResource graphStreamResource = generateImg(1500, 500);
            downloader.setFileDownloadResource(graphStreamResource);
            downloader.download();
        };
        MenuItem exportItem = menue.addItem("Export", VaadinIcons.CAMERA, exportCommand);
//        MenuItem helpItem = menue.addItem("Help", VaadinIcons.QUESTION_CIRCLE_O, null);

        mainSizeReporter = new SizeReporter(SpectrumPlot.this);
        compResizeListener = (ComponentResizeEvent event) -> {

            if (isDisableSizeReporter()) {
                return;
            }
            updatedComponentWidth = event.getWidth();
            updatedComponentHeight = event.getHeight();
            if (this.waitingTimer == null) {
                /* Start waiting for DELAY to elapse. */
                this.waitingTimer = new Timer(DELAY, (ActionEvent ae) -> {
                    reDraw();
                });
                this.waitingTimer.setRepeats(false);
                this.waitingTimer.start();
            } else {
                /* Event came too soon, swallow it by resetting the timer.. */
                this.waitingTimer.restart();
            }

        };
        mainSizeReporter.addResizeListener(compResizeListener);

        MenuBar.Command showPeakDetailsCommand = (MenuItem selectedItem) -> {
            spectrumPanel.showPeakDetails((selectedItem.getText().equals("Show Peak Details")));
            if (selectedItem.getText().equals("Show Peak Details")) {
                selectedItem.setText("Hide Peak Details");
            } else {
                selectedItem.setText("Show Peak Details");
            }
            updateImage(spectrumPanel);
        };
        menue.addItem("Show Peak Details", VaadinIcons.TAGS, showPeakDetailsCommand);

        resetAnnoItem.setEnabled(false);
        disableSizeReporter = false;

        HorizontalLayout spectrumSliderContainer = new HorizontalLayout();
        spectrumSliderContainer.setHeight(200, Unit.PIXELS);
        spectrumSliderContainer.setWidth(112, Unit.PIXELS);
        spectrumSliderContainer.setSpacing(true);
        spectrumSliderContainer.addComponent(annotationAccuracySlider);
        spectrumSliderContainer.setComponentAlignment(annotationAccuracySlider, Alignment.TOP_CENTER);
        spectrumSliderContainer.addComponent(levelSlider);
        spectrumSliderContainer.setComponentAlignment(levelSlider, Alignment.TOP_CENTER);
        PopupView spectrumSlidersPopup = new PopupView(VaadinIcons.SLIDERS.getHtml() + " Spectrum Sliders", spectrumSliderContainer);
//        spectrumSlidersPopup.setIcon(VaadinIcons.SLIDERS);
        spectrumSlidersPopup.setCaptionAsHtml(true);
        spectrumSlidersPopup.setStyleName("popupasmenueitem");
        spectrumSlidersPopup.setHideOnMouseOut(false);
        spectrumSlidersPopup.setDescription("Show annotation accuracy  and  intensity level sliders");
        controlsLayout.addComponent(spectrumSlidersPopup);

    }

    private void updateImage(JPanel jpanel) {
        String resUrl = drawImage(jpanel, false);
        plot.setSource(new ExternalResource(resUrl));
    }
 private StreamResource generateImg(int width, int height) {       

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D g2 = image.createGraphics();
        g2.setPaint(Color.BLACK);
        g2.fillRect(0, 0, width, height);
        g2.setPaint(Color.WHITE);
        g2.fillRect(2, 2, width - 4, height - 4);
        g2.setPaint(Color.GRAY);
       int dw =  spectrumPanel.getSize().width;
        int dh =  spectrumPanel.getSize().width;
        spectrumPanel.setSize(width, height);       
       spectrumPanel.paint(g2);

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bos);
            final byte[] touseImageData = bos.toByteArray();
            StreamResource graphAsImageResource = new StreamResource(() -> {
                return new ByteArrayInputStream(touseImageData);
            }, "spectrum.png");
             spectrumPanel.setSize(dw, dh);  
            return graphAsImageResource;
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
         spectrumPanel.setSize(dw, dh);  
        return null;

    }

    private void resetAnnotations() {
        ionsItem.getChildren().forEach((mi) -> {
            mi.setChecked(false);
        });
        identificationParameters.getSearchParameters().getForwardIons().forEach((i) -> {
            ionsItem.getChildren().get(i).setChecked(true);
        });
        identificationParameters.getSearchParameters().getRewindIons().forEach((i) -> {
            ionsItem.getChildren().get(i + 1).setChecked(true);
        });
        otherItem.getChildren().forEach((mi) -> {
            mi.setChecked(true);
        });
        MenuItem H2OItem = lossItem.getChildren().get(0);
        H2OItem.setChecked(true);
        H2OItem.setEnabled(false);
        MenuItem NH3Item = lossItem.getChildren().get(1);
        NH3Item.setChecked(true);
        NH3Item.setEnabled(false);

        MenuItem adaptItem = lossItem.getChildren().get(3);
        adaptItem.setChecked(true);
        adaptItem.setEnabled(true);

        chargeItem.getChildren().forEach((mi) -> {
            mi.setChecked(true);
        });

    }

    private SelectioncanvasComponent initSelectionCanvas(int w, int h) {
        SelectioncanvasComponent sc = new SelectioncanvasComponent() {
            @Override
            public void dragSelectionIsPerformed(double startX, double startY, double endX, double endY) {
                if ((endX - startX) < 5) {
                    return;
                }
                if (spectrumPanel != null) {
                    spectrumPanel.zoom((int) startX, 50, (int) endX, (int) 50);
                    updateImage(spectrumPanel);
                }
            }

            @Override
            public void rightSelectionIsPerformed(double startX, double startY) {
                if (spectrumPanel != null) {
                    spectrumPanel.reset();
                    updateImage(spectrumPanel);
                }
            }

            @Override
            public void leftSelectionIsPerformed(double startX, double startY) {
                if (spectrumPanel != null) {
                }
            }

        };
        sc.setSize(w, h, false);
        return sc;
    }

    private void reDraw() {
        int w = /*mainSizeReporter.getWidth()*/ updatedComponentWidth;
        int h = /*mainSizeReporter.getHeight()*/ updatedComponentHeight - 50;
        if (w <= 0 || h <= 0) {
            return;
        }

        if (selectionCanvas != null) {
            SpectrumPlot.this.removeComponent(selectionCanvas);
        }
        selectionCanvas = initSelectionCanvas(w, h);
        selectionCanvas.addStyleName("psmplotstyle");
        SpectrumPlot.this.addComponent(selectionCanvas, "left:0px;top:0px; bottom:40px");
        selectionCanvas.setSize(w, h, false);
        if (spectrumPanel != null) {
            spectrumPanel.setSize(w, h);
            plot.setWidth(w, Unit.PIXELS);
            plot.setHeight(h, Unit.PIXELS);

            updateImage(spectrumPanel);
            double tH = ((double) h * 0.8 * 0.5);
            levelSlider.setHeight((int) tH, Unit.PIXELS);
            annotationAccuracySlider.setHeight((int) tH, Unit.PIXELS);
        }

    }

    /**
     * Save the current annotation preferences selected in the annotation menus
     * in the specific annotation preferences.
     */
    public void updateAnnotationPreferences() {
        levelSlider.setCaption("Intensity <br/>" + ((int) ((double) levelSlider.getValue())) + " %");
        levelSlider.setDescription("Intensity level : " + ((int) ((double) levelSlider.getValue())) + " %");
        double accuracy = (annotationAccuracySlider.getValue() / 100.0) * fragmentIonAccuracy;
        annotationAccuracySlider.setCaption("Accuracy<br/>" + String.format("%.2f", accuracy) + " Da");
        annotationAccuracySlider.setDescription("Annotation accuracy : " + accuracy + " Da");
        PeptideAssumption peptideAssumption = spectrumMatch.getBestPeptideAssumption();
        currentPeptide = peptideAssumption.getPeptide();
        AnnotationParameters annotationParameters = identificationParameters.getAnnotationParameters();
        annotationParameters.setIntensityLimit(levelSlider.getValue() / 100.0);
        annotationParameters.setFragmentIonAccuracy(accuracy);
        specificAnnotationParameters = new SpecificAnnotationParameters();
        ModificationParameters modificationParameters = identificationParameters.getSearchParameters().getModificationParameters();
        SequenceMatchingParameters modificationSequenceMatchingParameters = identificationParameters.getModificationLocalizationParameters().getSequenceMatchingParameters();
        identificationParameters.setAnnotationParameters(annotationParameters);
        
        specificAnnotationParameters = annotationParameters.getSpecificAnnotationParameters(
                        spectrumMatch.getSpectrumFile(),
                        spectrumMatch.getSpectrumTitle(),
                        peptideAssumption,
                        modificationParameters,
                        sequenceProvider,
                        modificationSequenceMatchingParameters,
                        spectrumAnnotator
                );

        if (!defaultAnnotationInUse) {
            specificAnnotationParameters.getIonTypes().get(IonType.PEPTIDE_FRAGMENT_ION).clear();
            specificAnnotationParameters.getIonTypes().get(IonType.TAG_FRAGMENT_ION).clear();
            specificAnnotationParameters.clearIonTypes();
            ionsItem.getChildren().forEach((mi) -> {
                if (mi.isChecked()) {
                    specificAnnotationParameters.addIonType(IonType.PEPTIDE_FRAGMENT_ION, ions.get(mi.getText()));
                    specificAnnotationParameters.addIonType(IonType.TAG_FRAGMENT_ION, ions.get(mi.getText()));
                }
            });
            otherItem.getChildren().forEach((mi) -> {
                if (mi.isChecked()) {
                    if (mi.getText().equalsIgnoreCase("Reporter")) {
                        ArrayList<Integer> reporterIons = new ArrayList<>(IonFactory.getReporterIons(getIdentificationParameters().getSearchParameters().getModificationParameters()));
                        reporterIons.forEach((subtype) -> {
                            specificAnnotationParameters.addIonType(IonType.REPORTER_ION, subtype);
                        });
                    } else {
                        specificAnnotationParameters.addIonType(others.get(mi.getText()));
                    }
                }
            });

            MenuItem adaptItem = lossItem.getChildren().get(3);
            if (!adaptItem.isChecked()) {
                specificAnnotationParameters.setNeutralLossesAuto(false);
                specificAnnotationParameters.clearNeutralLosses();
                lossItem.getChildren().forEach((mi) -> {
                    if (mi.isChecked()) {
                        if (mi.getText().equalsIgnoreCase("NH3")) {
                            specificAnnotationParameters.addNeutralLoss(NeutralLoss.NH3);
                        } else if (mi.getText().equalsIgnoreCase("H2O")) {
                            specificAnnotationParameters.addNeutralLoss(NeutralLoss.H2O);
                        }
                    }
                });

            }

            specificAnnotationParameters.clearCharges();
            chargeItem.getChildren().stream().filter((charge) -> (charge.isChecked())).forEachOrdered((charge) -> {
                specificAnnotationParameters.addSelectedCharge(Integer.parseInt(charge.getText().replace("+", "").trim()));
            });
        }
        // The following preferences are kept for all spectra
        annotationParameters.setShowForwardIonDeNovoTags(deNovoItem.getChildren().get(0).isChecked());
        annotationParameters.setShowRewindIonDeNovoTags(deNovoItem.getChildren().get(1).isChecked());
        if (deNovoItem.getChildren().get(3).isChecked()) {
            annotationParameters.setDeNovoCharge(1);
        } else {
            annotationParameters.setDeNovoCharge(2);
        }
        SpectrumAnnotator.TiesResolution tiesResolution = settingsItem.getChildren().get(1).isChecked() ? SpectrumAnnotator.TiesResolution.mostAccurateMz : SpectrumAnnotator.TiesResolution.mostIntense;
        annotationParameters.setTiesResolution(tiesResolution); //@TODO: replace by a drop down menu
        annotationParameters.setShowAllPeaks(settingsItem.getChildren().get(0).isChecked());//@TODO:implement control btns
        annotationParameters.setAutomaticAnnotation(settingsItem.getChildren().get(3).isChecked());
        spectrumPanel.removeAllReferenceAreasXAxis();
        spectrumPanel.removeAllReferenceAreasYAxis();
        spectrumPanel.setDeltaMassWindow(accuracy);
        IonMatch[] annotations = spectrumAnnotator.getSpectrumAnnotation(
                annotationParameters, 
                specificAnnotationParameters, 
                spectrumMatch.getSpectrumFile(),
                spectrumMatch.getSpectrumTitle(),
                currentSpectrum, 
                currentPeptide,
                modificationParameters, 
                sequenceProvider,
                modificationSequenceMatchingParameters
        );
        spectrumPanel.setAnnotations(SpectrumAnnotator.getSpectrumAnnotation(annotations));
        spectrumPanel.showAnnotatedPeaksOnly(!annotationParameters.showAllPeaks());
        spectrumPanel.setYAxisZoomExcludesBackgroundPeaks(annotationParameters.yAxisZoomExcludesBackgroundPeaks());//
        Integer forwardIon = identificationParameters.getSearchParameters().getForwardIons().get(0);
        Integer rewindIon = identificationParameters.getSearchParameters().getRewindIons().get(0);//
        spectrumPanel.addAutomaticDeNovoSequencing(currentPeptide, annotations,
                forwardIon, rewindIon, annotationParameters.getDeNovoCharge(),
                annotationParameters.showForwardIonDeNovoTags(),
                annotationParameters.showRewindIonDeNovoTags(), false,
                modificationParameters, sequenceProvider, modificationSequenceMatchingParameters);
        spectrumPanel.setAnnotateHighestPeak(annotationParameters.getTiesResolution() == SpectrumAnnotator.TiesResolution.mostIntense); //@TODO: implement ties resolution in the spectrum panel
        spectrumPanel.setAnnotations(SpectrumAnnotator.getSpectrumAnnotation(annotations), annotationParameters.getTiesResolution() == SpectrumAnnotator.TiesResolution.mostIntense); //@TODO: the selection of the peak to annotate should be done outside the spectrum panel
    }

    private void initIonsItem(MenuItem parent, MenuBar.Command mainCommand) {
        ions.keySet().stream().map((str) -> parent.addItem(str, null, mainCommand)).forEachOrdered((ion) -> {
            ion.setCheckable(true);
        });
        parent.addSeparatorBefore(parent.getChildren().get(3));
    }

    private void initOtherItem(MenuItem parent, MenuBar.Command mainCommand) {
        others.keySet().stream().map((str) -> parent.addItem(str, null, mainCommand)).forEachOrdered((subItem) -> {
            subItem.setCheckable(true);
        });
    }
    private IdentificationParameters identificationParameters;
    private SpecificAnnotationParameters specificAnnotationParameters;
    private boolean defaultAnnotationInUse;
    private Spectrum currentSpectrum;
    private Peptide currentPeptide;
    private double fragmentIonAccuracy;
    private SpectrumMatch spectrumMatch;
    private Thread selectedSpectrumThread;

    public void selectedSpectrum(Spectrum currentSpectrum, SequenceProvider sequanceProvider, String charge, double fragmentIonAccuracy, IdentificationParameters identificationParameters, SpectrumMatch spectrumMatch) {
        mainSizeReporter.removeResizeListener(compResizeListener);
        this.identificationParameters = identificationParameters;
        this.currentSpectrum = currentSpectrum;
        this.fragmentIonAccuracy = fragmentIonAccuracy;
        this.spectrumMatch = spectrumMatch;
        this.sequenceProvider = sequanceProvider;
        if (spectrumPanel != null) {
            spectrumPanel.reset();
            if (selectedSpectrumThread.isAlive()) {
                selectedSpectrumThread.interrupt();
            }
        }
        selectedSpectrumThread = new Thread(() -> {
            Precursor precursor = currentSpectrum.getPrecursor();
            spectrumPanel = new WebSpectrumPanel(
                    currentSpectrum.mz, currentSpectrum.intensity,
                    precursor.mz, Charge.toString(spectrumMatch.getBestPeptideAssumption().getIdentificationCharge()),
                    "", 40, false, false, false, 2, false);

//                    
//                    new WebSpectrumPanel(currentSpectrum.getOrderedMzValues(), currentSpectrum.getIntensityValuesNormalizedAsArray(), 500, "2",
//                    "", 40, false, false, false, 2, false);
            spectrumPanel.setBorder(null);
            int w = mainSizeReporter.getWidth() - 52;
            int h = mainSizeReporter.getHeight() - 92;
            if (w <= 0 || h <= 0) {
                w = Math.max(h, w);
                h = Math.max(h, w);
            }
            spectrumPanel.setSize(w, h);
            plot.setWidth(w, Unit.PIXELS);
            plot.setHeight(h, Unit.PIXELS);
            spectrumPanel.setDataPointAndLineColor(Color.RED, 0);
            spectrumPanel.setSpectrumPeakColor(Color.RED);
            spectrumPanel.setSpectrumProfileModeLineColor(Color.RED);
            spectrumPanel.rescale(0.0, spectrumPanel.getMaxXAxisValue());
            this.defaultAnnotationInUse = true;
            chargeItem.removeChildren();
            int precursorCharge = 1;
            int currentCharge = spectrumMatch.getBestPeptideAssumption().getIdentificationCharge();
            if (currentCharge > precursorCharge) {
                precursorCharge = currentCharge;
            }
            if (precursorCharge == 1) {
                precursorCharge = 2;
            }
            for (Integer tcharge = 1; tcharge < precursorCharge; tcharge++) {
                MenuItem item = chargeItem.addItem(tcharge + "+", annotationsItemsCommand);
                item.setCheckable(true);
                item.setChecked(true);
            }
            resetAnnotations();
            Property.ValueChangeListener listener = (Property.ValueChangeEvent event) -> {
                updateAnnotationPreferences();
                updateImage(spectrumPanel);
            };
            annotationAccuracySlider.setValue(100.0);
            levelSlider.setValue(75.0);
            levelSlider.addValueChangeListener(listener);
            annotationAccuracySlider.addValueChangeListener(listener);
            updateAnnotationPreferences();
            if (plotThumbImage != null) {
                spectrumPanel.setMiniature(true);
                spectrumPanel.setSize(100, 100);
                spectrumPanel.setPeakWidth(2f);
                spectrumPanel.setBackgroundPeakWidth(0.5f);
                spectrumPanel.setMaxPadding(10);
                plotThumbImage.setSource(new ExternalResource(drawImage(spectrumPanel, true)));
                spectrumPanel.setMiniature(false);
                spectrumPanel.setPeakWidth(1f);
                spectrumPanel.setBackgroundPeakWidth(1f);
                spectrumPanel.setMaxPadding(50);
            }
            reDraw();
            mainSizeReporter.addResizeListener(compResizeListener);
        });
        selectedSpectrumThread.start();
        try {
            selectedSpectrumThread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public IdentificationParameters getIdentificationParameters() {
        return identificationParameters;
    }

    private String drawImage(JPanel panel, boolean thumb) {
        if (panel.getWidth() <= 0) {
            panel.setSize(100, panel.getHeight());
        }
        if (panel.getHeight() <= 0) {
            panel.setSize(panel.getWidth(), 100);
        }
//        panel.repaint();
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
        Graphics2D g2d = image.createGraphics();
        if (thumb) {
            panel.setSize(100, 60);
            panel.setOpaque(false);
//            panel.repaint();
            g2d.translate(0, 20);
        }
//        //draw sequence line
        g2d.setColor(Color.LIGHT_GRAY);
        panel.updateUI();
        panel.paint(g2d);
        ImageEncoder in = ImageEncoderFactory.newInstance(ImageFormat.PNG, 1);
        byte[] imageData = null;
        try {
            imageData = in.encode(image);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String base64 = Base64.encodeBytes(imageData);
        base64 = "data:image/png;base64," + base64;
        return base64;
    }

    /**
     * Get the current delta masses for use when annotating the spectra.
     *
     * @param identificationParameters
     * @return the current delta masses
     */
    public HashMap<Double, String> getCurrentMassDeltas(IdentificationParameters identificationParameters) {

        HashMap<Double, String> knownMassDeltas = new HashMap<>();

        // add the monoisotopic amino acids masses
        knownMassDeltas.put(AminoAcid.A.getMonoisotopicMass(), "A");
        knownMassDeltas.put(AminoAcid.R.getMonoisotopicMass(), "R");
        knownMassDeltas.put(AminoAcid.N.getMonoisotopicMass(), "N");
        knownMassDeltas.put(AminoAcid.D.getMonoisotopicMass(), "D");
        knownMassDeltas.put(AminoAcid.C.getMonoisotopicMass(), "C");
        knownMassDeltas.put(AminoAcid.Q.getMonoisotopicMass(), "Q");
        knownMassDeltas.put(AminoAcid.E.getMonoisotopicMass(), "E");
        knownMassDeltas.put(AminoAcid.G.getMonoisotopicMass(), "G");
        knownMassDeltas.put(AminoAcid.H.getMonoisotopicMass(), "H");
        knownMassDeltas.put(AminoAcid.I.getMonoisotopicMass(), "I/L");
        knownMassDeltas.put(AminoAcid.K.getMonoisotopicMass(), "K");
        knownMassDeltas.put(AminoAcid.M.getMonoisotopicMass(), "M");
        knownMassDeltas.put(AminoAcid.F.getMonoisotopicMass(), "F");
        knownMassDeltas.put(AminoAcid.P.getMonoisotopicMass(), "P");
        knownMassDeltas.put(AminoAcid.S.getMonoisotopicMass(), "S");
        knownMassDeltas.put(AminoAcid.T.getMonoisotopicMass(), "T");
        knownMassDeltas.put(AminoAcid.W.getMonoisotopicMass(), "W");
        knownMassDeltas.put(AminoAcid.Y.getMonoisotopicMass(), "Y");
        knownMassDeltas.put(AminoAcid.V.getMonoisotopicMass(), "V");
        knownMassDeltas.put(AminoAcid.U.getMonoisotopicMass(), "U");
        knownMassDeltas.put(AminoAcid.O.getMonoisotopicMass(), "O");

        // add default neutral losses
//        knownMassDeltas.put(NeutralLoss.H2O.mass, "H2O");
//        knownMassDeltas.put(NeutralLoss.NH3.mass, "NH3");
//        knownMassDeltas.put(NeutralLoss.CH4OS.mass, "CH4OS");
//        knownMassDeltas.put(NeutralLoss.H3PO4.mass, "H3PO4");
//        knownMassDeltas.put(NeutralLoss.HPO3.mass, "HPO3");
//        knownMassDeltas.put(4d, "18O"); // @TODO: should this be added to neutral losses??
//        knownMassDeltas.put(44d, "PEG"); // @TODO: should this be added to neutral losses??
        // add the modifications
        SearchParameters searchParameters = identificationParameters.getSearchParameters();
        ModificationParameters modificationProfile = searchParameters.getModificationParameters();
        ArrayList<String> modificationList = modificationProfile.getAllModifications();
        Collections.sort(modificationList);

        // iterate the modifications list and add the non-terminal modifications
        modificationList.forEach((modification) -> {
            Modification ptm = ModificationFactory.getInstance().getModification(modification);

            if (ptm != null) {

                String shortName = ptm.getShortName();
                double mass = ptm.getMass();

                if (ptm.getModificationType() == ModificationType.modaa) {
                    AminoAcidPattern ptmPattern = ptm.getPattern();
                    ptmPattern.getAminoAcidsAtTarget().stream().filter((aa) -> (!knownMassDeltas.containsValue(aa + "<" + shortName + ">"))).forEachOrdered((aa) -> {
                        AminoAcid aminoAcid = AminoAcid.getAminoAcid(aa);
                        knownMassDeltas.put(mass + aminoAcid.getMonoisotopicMass(),
                                aa + "<" + shortName + ">");
                    });
                }
            } else {
                System.out.println("Error: PTM not found: " + modification);
            }
        });

        return knownMassDeltas;
    }

    /**
     * The spectrum annotator.
     */
    private final PeptideSpectrumAnnotator spectrumAnnotator = new PeptideSpectrumAnnotator();

}
