package com.uib.web.peptideshaker.ui.views.subviews.proteinsview.components;

import org.jfree.chart.encoders.ImageEncoder;
import org.jfree.chart.encoders.ImageEncoderFactory;
import org.jfree.chart.encoders.ImageFormat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;

/**
 * This class responsible for generate change coverage images and do all
 * calculations related to that task
 *
 * @author Yehia Mokhtar Farag
 */
public class ChainCoverageComponent {

    private final int proteinSequenceLength;
    private final Map<String, Rectangle> chainsBlocks;
    private final double iconCorrectFactor;

    private final double layoutToPercentageFactor;
    private final int[] coverageArr;
    //    private final Image chainCoverageWebComponent;
    private final Protein3DStructureCoverage chainCoverageWebComponent;
    private final DecimalFormat df = new DecimalFormat("#.#");
    private int counter = 1;
    private String lasteSelectedChain;
    private double coverage = -1;
    private Color chaincolor;
    private Color bordercolor;

    public ChainCoverageComponent(int proteinSequenceLength) {
        this.proteinSequenceLength = proteinSequenceLength;
        this.iconCorrectFactor = (double) (360) / (double) this.proteinSequenceLength;
        this.layoutToPercentageFactor = 100.0 / (double) this.proteinSequenceLength;

        this.chainsBlocks = new LinkedHashMap<>();
        this.coverageArr = new int[proteinSequenceLength];

        this.chainCoverageWebComponent = new Protein3DStructureCoverage();
        this.chainCoverageWebComponent.setSizeFull();


    }

    public void addChainRange(String chainId, int start, int end) {
        start = Math.max(start, 0);
        end = Math.min(end, coverageArr.length - 1);
        Rectangle chain = new Rectangle(start, 10, (end - start + 1), 10);
        chainsBlocks.put(chainId + "_" + (counter++), chain);
        for (int i = start; i <= end; i++) {
            coverageArr[i] = 1;
        }
    }

    public double getCoverage() {
        if (coverage != -1) {
            return coverage;
        }
        counter = 0;
        for (int i = 0; i < coverageArr.length; i++) {
            counter += coverageArr[i];
        }
        coverage = ((double) counter / (double) this.proteinSequenceLength) * 100.00;
        return coverage;
    }

    public String selectChain(String chainId) {
        return drawImage(chainId);

    }

    private String drawImage(String chainId) {
        lasteSelectedChain = chainId;
        this.chainCoverageWebComponent.setRightLabelValue(chainId);
        this.chainCoverageWebComponent.reset();

        BufferedImage icon = new BufferedImage(415, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();

        g2d.setColor(Color.GRAY);
        g2d.fillRect(5, 14, (360), 3);

        //chaine border
        chaincolor = new Color(226, 226, 226);
        bordercolor = Color.GRAY;
        for (int i = 0; i < coverageArr.length; i++) {
            if (coverageArr[i] > 0) {
                //start draw
                double start = i;
                double end = i;
                for (int y = i; (y < coverageArr.length) && coverageArr[y] > 0; y++) {
                    end++;
                    i = y;
                }

                this.chainCoverageWebComponent.addCoverageComponent((((double) start) * layoutToPercentageFactor), (((double) (end - start)) * layoutToPercentageFactor), false);
                g2d.setColor(bordercolor);
                g2d.drawRect(5 + (int) ((double) start * iconCorrectFactor), 10, (int) ((end - start) * iconCorrectFactor), 10);
                g2d.setColor(chaincolor);
                g2d.fillRect(5 + (int) ((double) start * iconCorrectFactor), 10, (int) ((end - start) * iconCorrectFactor), 10);

            }
        }

        String v;
        if (!chainId.contains("All")) {
            counter = 0;
            chainsBlocks.keySet().forEach((c) -> {
                if (c.contains(chainId)) {
//                   
                    this.chainCoverageWebComponent.addCoverageComponent(((double) (chainsBlocks.get(c).x) * layoutToPercentageFactor), (((double) chainsBlocks.get(c).width) * layoutToPercentageFactor), true);
                    g2d.setColor(Color.GRAY);
                    g2d.drawRect(5 + (int) ((double) chainsBlocks.get(c).x * iconCorrectFactor), chainsBlocks.get(c).y, (int) ((double) chainsBlocks.get(c).width * iconCorrectFactor), chainsBlocks.get(c).height);
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.fillRect(5 + (int) ((double) chainsBlocks.get(c).x * iconCorrectFactor), chainsBlocks.get(c).y, (int) ((double) chainsBlocks.get(c).width * iconCorrectFactor), chainsBlocks.get(c).height);
                    counter += (chainsBlocks.get(c).width - 1);

                }
            });
            v = df.format(((double) counter / (double) proteinSequenceLength) * 100.0);
        } else {
            double d = getCoverage();
            v = df.format(d);
        }
        //total chain coverage
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("sans-serif", Font.PLAIN, 14));
        g2d.drawString(v + "%", (365), (15) + 6);
        g2d.dispose();

        byte[] iconImageData = null;
        try {
            ImageEncoder in = ImageEncoderFactory.newInstance(ImageFormat.PNG, 1);
            iconImageData = in.encode(icon);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

        String base64 = Base64.encodeBase64String(iconImageData);
        base64 = "data:image/png;base64," + base64;

        //total chain coverage
//     
        return base64;
    }


    public Protein3DStructureCoverage getChainCoverageWebComponent() {
        return chainCoverageWebComponent;
    }
}
