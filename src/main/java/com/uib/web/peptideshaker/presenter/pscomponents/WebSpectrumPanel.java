/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.pscomponents;

import com.compomics.util.gui.interfaces.SpectrumAnnotation;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.compomics.util.interfaces.SpectrumFile;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author y-mok
 */
public class WebSpectrumPanel extends SpectrumPanel {

    public WebSpectrumPanel(SpectrumFile aSpecFile) {
        super(aSpecFile);
    }

    public WebSpectrumPanel(SpectrumFile aSpecFile, boolean aEnableInteraction) {
        super(aSpecFile, aEnableInteraction);
    }

    public WebSpectrumPanel(SpectrumFile aSpecFile, DrawingStyle aDrawStyle, boolean aEnableInteraction) {
        super(aSpecFile, aDrawStyle, aEnableInteraction);
    }

    public WebSpectrumPanel(SpectrumFile aSpecFile, DrawingStyle aDrawStyle, boolean aEnableInteraction, Color aSpectrumFilenameColor) {
        super(aSpecFile, aDrawStyle, aEnableInteraction, aSpectrumFilenameColor);
    }

    public WebSpectrumPanel(SpectrumFile aSpecFile, DrawingStyle aDrawStyle, boolean aEnableInteraction, Color aSpectrumFilenameColor, int aMaxPadding, boolean aShowFileName) {
        super(aSpecFile, aDrawStyle, aEnableInteraction, aSpectrumFilenameColor, aMaxPadding, aShowFileName);
    }

    public WebSpectrumPanel(SpectrumFile aSpecFile, DrawingStyle aDrawStyle, boolean aEnableInteraction, Color aSpectrumFilenameColor, int aMaxPadding, boolean aShowFileName, boolean aShowPrecursorDetails, boolean aShowResolution) {
        super(aSpecFile, aDrawStyle, aEnableInteraction, aSpectrumFilenameColor, aMaxPadding, aShowFileName, aShowPrecursorDetails, aShowResolution);
    }

    public WebSpectrumPanel(SpectrumFile aSpecFile, DrawingStyle aDrawStyle, boolean aEnableInteraction, Color aSpectrumFilenameColor, int aMaxPadding, boolean aShowFileName, boolean aShowPrecursorDetails, boolean aShowResolution, int aMSLevel) {
        super(aSpecFile, aDrawStyle, aEnableInteraction, aSpectrumFilenameColor, aMaxPadding, aShowFileName, aShowPrecursorDetails, aShowResolution, aMSLevel);
    }

    public WebSpectrumPanel(SpectrumFile aSpecFile, DrawingStyle aDrawStyle, boolean aEnableInteraction, Color aSpectrumFilenameColor, int aMaxPadding, boolean aShowFileName, boolean aShowPrecursorDetails, boolean aShowResolution, int aMSLevel, boolean aProfileMode) {
        super(aSpecFile, aDrawStyle, aEnableInteraction, aSpectrumFilenameColor, aMaxPadding, aShowFileName, aShowPrecursorDetails, aShowResolution, aMSLevel, aProfileMode);
    }

    public WebSpectrumPanel(double[] aXAxisData, double[] aYAxisData, double aPrecursorMZ, String aPrecursorCharge, String aFileName) {
        super(aXAxisData, aYAxisData, aPrecursorMZ, aPrecursorCharge, aFileName);
    }

    public WebSpectrumPanel(double[] aXAxisData, double[] aYAxisData, double aPrecursorMZ, String aPrecursorCharge, String aFileName, boolean aShowFileName) {
        super(aXAxisData, aYAxisData, aPrecursorMZ, aPrecursorCharge, aFileName, aShowFileName);
    }

    public WebSpectrumPanel(double[] aXAxisData, double[] aYAxisData, double aPrecursorMZ, String aPrecursorCharge, String aFileName, int aMaxPadding, boolean aShowFileName) {
        super(aXAxisData, aYAxisData, aPrecursorMZ, aPrecursorCharge, aFileName, aMaxPadding, aShowFileName);
    }

    public WebSpectrumPanel(double[] aXAxisData, double[] aYAxisData, double aPrecursorMZ, String aPrecursorCharge, String aFileName, int aMaxPadding, boolean aShowFileName, boolean aShowPrecursorDetails, boolean aShowResolution) {
        super(aXAxisData, aYAxisData, aPrecursorMZ, aPrecursorCharge, aFileName, aMaxPadding, aShowFileName, aShowPrecursorDetails, aShowResolution);
    }

    public WebSpectrumPanel(double[] aXAxisData, double[] aYAxisData, double aPrecursorMZ, String aPrecursorCharge, String aFileName, int aMaxPadding, boolean aShowFileName, boolean aShowPrecursorDetails, boolean aShowResolution, int aMSLevel) {
        super(aXAxisData, aYAxisData, aPrecursorMZ, aPrecursorCharge, aFileName, aMaxPadding, aShowFileName, aShowPrecursorDetails, aShowResolution, aMSLevel);
    }

    public WebSpectrumPanel(double[] aXAxisData, double[] aYAxisData, double aPrecursorMZ, String aPrecursorCharge, String aFileName, int aMaxPadding, boolean aShowFileName, boolean aShowPrecursorDetails, boolean aShowResolution, int aMSLevel, boolean aProfileMode) {
        super(aXAxisData, aYAxisData, aPrecursorMZ, aPrecursorCharge, aFileName, aMaxPadding, aShowFileName, aShowPrecursorDetails, aShowResolution, aMSLevel, aProfileMode);
    }

    public void zoom(int iStartXLoc, int iStartYLoc, int iEndXLoc, int iEndYLoc) {
        int min = Math.min(iEndXLoc, iStartXLoc);
        int max = Math.max(iEndXLoc, iStartXLoc);
        double start = iXAxisMin + ((min - iXPadding) * iXScaleUnit);
        double end = iXAxisMin + ((max - iXPadding) * iXScaleUnit);
//        if (iDragged) {
//            iDragged = false;
        // Rescale.
        if ((max - min) > iMinDrag) {
            rescale(start, end);
        }
        iDragXLoc = 0;
        repaint();
//        }

    }

    public void reset() {
        if (iXAxisStartAtZero) {
            rescale(0.0, getMaxXAxisValue());
        } else {

            double tempMinXValue = getMinXAxisValue();

            // if isotopic distribution add a little padding on the left side
            // to make sure that the first peak is not too close to the y-axis
            if (currentGraphicsPanelType.equals(GraphicsPanelType.isotopicDistributionProfile)
                    || currentGraphicsPanelType.equals(GraphicsPanelType.isotopicDistributionCentroid)) {
                tempMinXValue -= 1;

                if (tempMinXValue < 0) {
                    tempMinXValue = 0;
                }
            }

            rescale(tempMinXValue, getMaxXAxisValue());
        }
        iDragged = false;
        repaint();
    }

    public void showPeakDetails(boolean show) {
        showAllPeakDetails = show;
        repaint();

    }
    private boolean showAllPeakDetails;

    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClickedAction(MouseEvent e) {
        mouseMoved(e.getX(), e.getY());
        if (e.getButton() == MouseEvent.BUTTON1) {
            iStartXLoc = e.getX();
            iStartYLoc = e.getY();
        }
        // see if we're above or below the x-axis
        int xAxisYLocation = (getHeight() - currentPadding) / 2;
        boolean aboveXAxis = e.getY() < xAxisYLocation;

        if (dataSetCounterMirroredSpectra == 0) {
            aboveXAxis = true;
        }
        if (aboveXAxis) { // @TODO: merge the above/below code
            if (iXAxisData != null) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getModifiersEx() == (MouseEvent.CTRL_DOWN_MASK | MouseEvent.ALT_DOWN_MASK)) {

                    iStoredSequence = new Vector(15, 5);
                    iStoredSequenceDatasetIndices = new Vector(15, 5);
                    repaint();
                } else if (e.getButton() == MouseEvent.BUTTON1 && e.getModifiersEx() == MouseEvent.CTRL_DOWN_MASK) {
                    iClicked = false;
                    iClickedList = new Vector(15, 5);
                    iClickedListDatasetIndices = new Vector(15, 5);
                    repaint();
                } else if (e.getButton() == MouseEvent.BUTTON1 && e.getModifiersEx() == MouseEvent.SHIFT_DOWN_MASK) {
                    // If the clicked point is the last one in the list of previously clicked points,
                    // remove it from the list!
                    if (iClickedList != null && iClickedList.size() > 0 && iHighLightIndex == iClickedIndex) {
                        // Retrieve the previously clicked index from the list and set the currently clicked
                        // one to that value.
                        iClickedIndex = ((Integer) iClickedList.get(iClickedList.size() - 1)).intValue();
                        iClickedDataSetIndex = ((Integer) iClickedListDatasetIndices.get(iClickedListDatasetIndices.size() - 1)).intValue();
                        // Remove the previously clicked index from the list.
                        iClickedList.remove(iClickedList.size() - 1);
                        iClickedListDatasetIndices.remove(iClickedListDatasetIndices.size() - 1);
                        // Repaint.
                        repaint();
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1 && e.getModifiersEx() == MouseEvent.ALT_DOWN_MASK) {
                    // See if there is a clicked list and if it contains any values.
                    if (iClickedList != null && iClickedList.size() > 0) {
                        // Copy the current clickedlist into the stored sequence.
                        iStoredSequence = (Vector) iClickedList.clone();
                        iStoredSequence.add(Integer.valueOf(iClickedIndex));
                        iStoredSequenceDatasetIndices = (Vector) iClickedListDatasetIndices.clone();
                        iStoredSequenceDatasetIndices.add(Integer.valueOf(iClickedDataSetIndex));
                        iClicked = false;
                        // Reset the clicked list.
                        iClickedList = new Vector(15, 5);
                        iClickedListDatasetIndices = new Vector(15, 5);
                        repaint();
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    if (iClicked && iClickedIndex != iHighLightIndex) {
                        // We need the current point to be stored in the previously clicked
                        // Vector and set the current one as clicked.
                        iClickedList.add(Integer.valueOf(iClickedIndex));
                        iClickedListDatasetIndices.add(Integer.valueOf(iClickedDataSetIndex));
                    }
                    iClicked = true;
                    iClickedIndex = iHighLightIndex;
                    iClickedDataSetIndex = iHighLightDatasetIndex;
                    repaint();
                }
            }
        } else {
            if (iXAxisDataMirroredSpectrum != null) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getModifiersEx() == (MouseEvent.CTRL_DOWN_MASK | MouseEvent.ALT_DOWN_MASK)) {
                    iStoredSequenceMirrored = new Vector(15, 5);
                    iStoredSequenceDatasetIndicesMirrored = new Vector(15, 5);
                    repaint();
                } else if (e.getButton() == MouseEvent.BUTTON1 && e.getModifiersEx() == MouseEvent.CTRL_DOWN_MASK) {
                    iClickedMirrored = false;
                    iClickedListMirrored = new Vector(15, 5);
                    iClickedListDatasetIndicesMirrored = new Vector(15, 5);
                    repaint();
                } else if (e.getButton() == MouseEvent.BUTTON1 && e.getModifiersEx() == MouseEvent.SHIFT_DOWN_MASK) {
                    // If the clicked point is the last one in the list of previously clicked points,
                    // remove it from the list!
                    if (iClickedListMirrored != null && iClickedListMirrored.size() > 0 && iHighLightIndexMirrored == iClickedIndexMirrored) {
                        // Retrieve the previously clicked index from the list and set the currently clicked
                        // one to that value.
                        iClickedIndexMirrored = ((Integer) iClickedListMirrored.get(iClickedListMirrored.size() - 1)).intValue();
                        iClickedDataSetIndexMirrored = ((Integer) iClickedListDatasetIndicesMirrored.get(iClickedListDatasetIndicesMirrored.size() - 1)).intValue();
                        // Remove the previously clicked index from the list.
                        iClickedListMirrored.remove(iClickedListMirrored.size() - 1);
                        iClickedListDatasetIndicesMirrored.remove(iClickedListDatasetIndicesMirrored.size() - 1);
                        // Repaint.
                        repaint();
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1 && e.getModifiersEx() == MouseEvent.ALT_DOWN_MASK) {
                    // See if there is a clicked list and if it contains any values.
                    if (iClickedListMirrored != null && iClickedListMirrored.size() > 0) {
                        // Copy the current clickedlist into the stored sequence.
                        iStoredSequenceMirrored = (Vector) iClickedListMirrored.clone();
                        iStoredSequenceMirrored.add(Integer.valueOf(iClickedIndexMirrored));
                        iStoredSequenceDatasetIndicesMirrored = (Vector) iClickedListDatasetIndicesMirrored.clone();
                        iStoredSequenceDatasetIndicesMirrored.add(Integer.valueOf(iClickedDataSetIndexMirrored));
                        iClicked = false;
                        // Reset the clicked list.
                        iClickedListMirrored = new Vector(15, 5);
                        iClickedListDatasetIndicesMirrored = new Vector(15, 5);
                        repaint();
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    if (iClickedMirrored && iClickedIndexMirrored != iHighLightIndexMirrored) {
                        // We need the current point to be stored in the previously clicked
                        // Vector and set the current one as clicked.
                        iClickedListMirrored.add(Integer.valueOf(iClickedIndexMirrored));
                        iClickedListDatasetIndicesMirrored.add(Integer.valueOf(iClickedDataSetIndexMirrored));
                    }
                    iClickedMirrored = true;
                    iClickedIndexMirrored = iHighLightIndexMirrored;
                    iClickedDataSetIndexMirrored = iHighLightDatasetIndexMirrored;
                    repaint();
                }
            }
        }
//           if (iClicked) {
//                this.highlightClicked(iClickedIndex, iHighLightDatasetIndex, this.getGraphics(), false);
//            }
    }

    /**
     * Invoked when the mouse button has been moved on a component (with no
     * buttons no down).
     */
    public void mouseMoved(int x, int y) {

        // see if we're above or below the x-axis
//                int y = e.getY();
        int xAxisYLocation = (getHeight() - currentPadding) / 2;
        boolean aboveXAxis = y < xAxisYLocation;

        if (dataSetCounterMirroredSpectra == 0) {
            aboveXAxis = true;
        }

        ArrayList<double[]> xAxisData;
        ArrayList<int[]> xAxisDataInPixels;
        ArrayList<int[]> yAxisDataInPixels;

        if (aboveXAxis) {
            xAxisData = iXAxisData;
            xAxisDataInPixels = iXAxisDataInPixels;
            yAxisDataInPixels = iYAxisDataInPixels;
        } else {
            xAxisData = iXAxisDataMirroredSpectrum;
            xAxisDataInPixels = iXAxisDataInPixelsMirroredSpectrum;
            yAxisDataInPixels = iYAxisDataInPixelsMirroredSpectrum;
        }

        if (xAxisData != null && xAxisDataInPixels != null) {

            // this variable is used to make sure that the most intense peak within range is highlighted
            int highestPeakInRange = 0;

            for (int j = 0; j < xAxisDataInPixels.size(); j++) {
                for (int i = 0; i < xAxisDataInPixels.get(j).length; i++) {
                    int delta = xAxisDataInPixels.get(j)[i] - x;
                    if (Math.abs(delta) < iPointDetectionTolerance) {
                        if (aboveXAxis) {
                            int deltaYPixels = y - yAxisDataInPixels.get(j)[i];
                            if (deltaYPixels < 0
                                    && Math.abs(deltaYPixels) < (getHeight() - yAxisDataInPixels.get(j)[i])
                                    && highestPeakInRange < (getHeight() - yAxisDataInPixels.get(j)[i])) {
                                iHighLight = true;
                                iHighLightIndex = i;
                                iHighLightDatasetIndex = j;
                                highestPeakInRange = (getHeight() - yAxisDataInPixels.get(j)[i]);
//                                        repaint();
                            }
                        } else {
                            int deltaYPixels = yAxisDataInPixels.get(j)[i] - y;
                            if (deltaYPixels < 0
                                    && Math.abs(deltaYPixels) < yAxisDataInPixels.get(j)[i]
                                    && highestPeakInRange < yAxisDataInPixels.get(j)[i]) {
                                iHighLightMirrored = true;
                                iHighLightIndexMirrored = i;
                                iHighLightDatasetIndexMirrored = j;
                                highestPeakInRange = yAxisDataInPixels.get(j)[i];
//                                        repaint();
                            }
                        }
                    } else if (delta >= iPointDetectionTolerance) {
                        break;
                    }
                }
            }
//                    repaint();
        }
    }

    /**
     * Invoked by Swing to draw components. Applications should not invoke
     * <code>paint</code> directly, but should instead use the
     * <code>repaint</code> method to schedule the component for redrawing.
     * <p>
     * This method actually delegates the work of painting to three protected
     * methods: <code>paintComponent</code>, <code>paintBorder</code>, and
     * <code>paintChildren</code>. They're called in the order listed to ensure
     * that children appear on top of component itself. Generally speaking, the
     * component and its children should not paint in the insets area allocated
     * to the border. Subclasses can just override this method, as always. A
     * subclass that just wants to specialize the UI (look and feel) delegate's
     * <code>paint</code> method should just override
     * <code>paintComponent</code>.
     *
     * @param g the <code>Graphics</code> context in which to paint
     * @see #paintComponent
     * @see #paintBorder
     * @see #paintChildren
     * @see #getComponentGraphics
     * @see #repaint
     */
    @Override
    public void paint(Graphics g) {

//        
    
super.paint(g);
        if (iXAxisData != null) {
            if (iDragged && iDragXLoc > 0) {
                g.drawLine(iStartXLoc, iStartYLoc, iDragXLoc, iStartYLoc);
                g.drawLine(iStartXLoc, iStartYLoc - 2, iStartXLoc, iStartYLoc + 2);
                g.drawLine(iDragXLoc, iStartYLoc - 2, iDragXLoc, iStartYLoc + 2);
            }

            // round the range of the x- and y-axis to integer values
            iXAxisMin = (int) Math.floor(iXAxisMin); // @TODO: increase the zooming resolution!!!
            iXAxisMax = (int) Math.ceil(iXAxisMax);
            iYAxisMin = (int) Math.floor(iYAxisMin);
            iYAxisMax = (int) Math.ceil(iYAxisMax);

            // draw the x and y axis
            drawAxes(g, iXAxisMin, iXAxisMax, 2, iYAxisMin, iYAxisMax);// @TODO: scale?        
            // add reference areas that are to be drawn in the back, if any
            drawYAxisReferenceAreas(g, false);
            drawXAxisReferenceAreas(g, false);

            // draw the peaks
            if (currentGraphicsPanelType.equals(GraphicsPanelType.profileChromatogram)
                    || currentGraphicsPanelType.equals(GraphicsPanelType.profileSpectrum)
                    || currentGraphicsPanelType.equals(GraphicsPanelType.isotopicDistributionProfile)) {
                drawFilledPolygon(g);
            } else {
                drawPeaks(g);
                if (dataSetCounterMirroredSpectra > 0) {
                    drawMirroredPeaks(g);
                }
            }

            // draw any measurement lines in the normal spectra
            if (iClicked && iHighLight && iClickedIndex != iHighLightIndex) {
                // Now we should calculate the distance based on the real values and draw a line to show this.
                this.drawMeasurementLine(iClickedIndex, iClickedDataSetIndex,
                        iHighLightIndex, iHighLightDatasetIndex, g, Color.blue, 0, false);
            }
            // draw any measurement lines in the mirrored spectra
            if (iClickedMirrored && iHighLightMirrored && iClickedIndexMirrored != iHighLightIndexMirrored) {
                // Now we should calculate the distance based on the real values and draw a line to show this.
                this.drawMeasurementLine(iClickedIndexMirrored, iClickedDataSetIndexMirrored,
                        iHighLightIndexMirrored, iHighLightDatasetIndexMirrored, g, Color.blue, 0, true);
            }
            // hihlight peaks 
            if (iHighLight) {
                this.highLightPeak(iHighLightIndex, iHighLightDatasetIndex, g, false);
                iHighLight = false;
            }

            // highlight mirrored peaks
            if (iHighLightMirrored) {
               
                this.highLightPeak(iHighLightIndexMirrored, iHighLightDatasetIndexMirrored, g, true);
                iHighLightMirrored = false;
            }
            // highlight clicked peaks
            if (iClicked) {
                this.highlightClicked(iClickedIndex, iHighLightDatasetIndex, g, false);
            }

            // highlight clicked mirrored peaks
            if (iClickedMirrored) {

               this.highlightClicked(iClickedIndexMirrored, iHighLightDatasetIndexMirrored, g, true);
            }
            // see if there are daisychain to display
            drawDaisyChain(g, iClickedList, iClickedListDatasetIndices, iClickedIndex, iClickedDataSetIndex, iStoredSequence, iStoredSequenceDatasetIndices, false);

            // see if there are daisychains to display for the mirrored spectra
            drawDaisyChain(g, iClickedListMirrored, iClickedListDatasetIndicesMirrored, iClickedIndexMirrored, iClickedDataSetIndexMirrored, iStoredSequenceMirrored, iStoredSequenceDatasetIndicesMirrored, true);

            // annotate peaks
            annotatePeaks(g, iAnnotations, false);

            // annotate mirrored peaks
            annotatePeaks(g, iAnnotationsMirroredSpectra, true);

            // add reference areas that are to be drawn on top of the data, if any{
            drawYAxisReferenceAreas(g, true);
            drawXAxisReferenceAreas(g, true);

            // (re-)draw the axes to have them appear in front of the data points
            drawAxes(g, iXAxisMin, iXAxisMax, 2, iYAxisMin, iYAxisMax); // @TODO scale.
            if (showAllPeakDetails) {
                boolean aboveXAxis = false;
                if (dataSetCounterMirroredSpectra == 0) {
                    aboveXAxis = true;
                }

                ArrayList<double[]> xAxisData;
                ArrayList<int[]> xAxisDataInPixels;
                if (aboveXAxis) {
                    xAxisData = iXAxisData;
                    xAxisDataInPixels = iXAxisDataInPixels;
                } else {
                    xAxisData = iXAxisDataMirroredSpectrum;
                    xAxisDataInPixels = iXAxisDataInPixelsMirroredSpectrum;
                }

                if (xAxisData != null && xAxisDataInPixels != null) {
                    for (int j = 0; j < xAxisDataInPixels.size(); j++) {
                        for (int i = 0; i < xAxisDataInPixels.get(j).length; i++) {
                            double xAxisPeakValue = iXAxisData.get(j)[i];
                            boolean annotatedPeak = isPeakAnnotated(xAxisPeakValue, false);
                            boolean visiblePeak = xAxisPeakValue >= this.getXAxisZoomRangeLowerValue() && xAxisPeakValue <= this.getXAxisZoomRangeUpperValue();
                            if (annotatedPeak && visiblePeak) {
                                this.highlightClicked(i, j, g, false);
                            }

                        }
                    }
//                    repaint();
                }

            }
        }
        
      
    }

    /**
     * Returns true of the given x-axis value is annotated with at least one
     * annotation.
     *
     * @param xAxisValue the x-axis value
     * @param mirrored if true checks for the mirrored peaks, false checks the
     * normal peaks
     * @return true of the given x-axis value is annotated with at least one
     * annotation
     */
    private boolean isPeakAnnotated(double xAxisValue, boolean mirrored) {

        Vector annotations;

        if (!mirrored) {
            annotations = iAnnotations;
        } else {
            annotations = iAnnotationsMirroredSpectra;
        }

        boolean annotatedPeak = false;

        for (int m = 0; m < annotations.size() && !annotatedPeak; m++) {
            Object o = annotations.get(m);
            if (o instanceof SpectrumAnnotation) {
                SpectrumAnnotation sa = (SpectrumAnnotation) o;

                double xValue = sa.getMZ();
                double error = Math.abs(sa.getErrorMargin()); // @TODO: make sure that there is only one annotated peak per annotation!
                double delta = xAxisValue - xValue;

                if (Math.abs(delta) <= error) {
                    annotatedPeak = true;
                }
            }
        }

        return annotatedPeak;
    }
   
}
