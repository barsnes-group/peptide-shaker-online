package com.uib.web.peptideshaker.ui.views.subviews.proteinsview.components;

import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.PeptideObject;
import com.uib.web.peptideshaker.model.ProteinGroupObject;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.HighlightPeptide;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.PeptideLayout;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.ProteinCoverageLayout;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.ProteoformLayout;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.ProteoformModificationLayout;
import com.uib.web.peptideshaker.ui.components.RangeColorGenerator;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.util.*;

/**
 * This class represents protein coverage layout that contain the distribution
 * of peptides on the protein
 *
 * @author Yehia Farag
 */
public abstract class ProteinCoverageComponent extends AbsoluteLayout {

    private final ProteinCoverageLayout proteinCoverageLayout;
    private final AbsoluteLayout peptideDistributionLayout;
    private final AbsoluteLayout chainCoverage3dLayout;
    private final AbsoluteLayout proteoformCoverage;
    private final Set<PeptideLayout> peptideDistMap;
    private final Set<PeptideObject> peptideObjectsSet;
    private final Map<String, Set<PeptideLayout>> modificationPeptideMap;
    private final int layoutHeight;
    private final int proteinSequenceLength;
    private final ProteinGroupObject mainProteinObject;
    private final float resizeFactor;
    private int layoutHeightProteoform;
    private boolean proteoformInit;
    private boolean suspendListener;

    public ProteinCoverageComponent(ProteinGroupObject protein, Map<String, PeptideObject> peptidesNodes, RangeColorGenerator colorScale) {
        ProteinCoverageComponent.this.setWidth(100, Unit.PERCENTAGE);
        HashMap<String, String> styles = new HashMap<>();
        styles.put(CONSTANT.VALIDATION_CONFIDENT, "greenbackground");
        styles.put(CONSTANT.VALIDATION_DOUBTFUL, "orangebackground");
        styles.put(CONSTANT.VALIDATION_NOT_VALID, "redbackground");
        styles.put(CONSTANT.NO_INFORMATION, "graybackground");
        styles.put("Protein", "greenbackground");
        styles.put("Transcript", "orangebackground");
        styles.put("Homology", "seabluebackground");
        styles.put("Predicted", "purplebackground");
        styles.put("Uncertain", "redbackground");
        styles.put("Not Applicable", "lightgraybackground");
        this.mainProteinObject = protein;
        modificationPeptideMap = new LinkedHashMap<>();
        if (protein.getValidation() == null) {
            protein.setValidation(CONSTANT.NO_INFORMATION);
        }
        this.peptideObjectsSet = new LinkedHashSet<>();
        chainCoverage3dLayout = new AbsoluteLayout() {
            private boolean expanded = false;

            @Override
            public void setVisible(boolean v) {
                if (v && !expanded) {
                    ProteinCoverageComponent.this.removeAllComponents();
                    ProteinCoverageComponent.this.addComponent(chainCoverage3dLayout, "left:0; top:0px;");
                    ProteinCoverageComponent.this.addComponent(proteinCoverageLayout, "left:0; top:30px;");
                    ProteinCoverageComponent.this.addComponent(peptideDistributionLayout, "left:0; top:45px;");
                    ProteinCoverageComponent.this.setHeight(ProteinCoverageComponent.this.getHeight() + 20, Unit.PIXELS);
                    expanded = true;

                } else if (!v && expanded) {
                    ProteinCoverageComponent.this.removeAllComponents();
                    ProteinCoverageComponent.this.addComponent(chainCoverage3dLayout, "left:0; top:0px;");
                    ProteinCoverageComponent.this.addComponent(proteinCoverageLayout, "left:0; top:10px;");
                    ProteinCoverageComponent.this.addComponent(peptideDistributionLayout, "left:0; top:25px;");
                    ProteinCoverageComponent.this.setHeight(ProteinCoverageComponent.this.getHeight() - 20, Unit.PIXELS);
                    expanded = false;
                } else if (!v) {
                    ProteinCoverageComponent.this.removeAllComponents();
                    ProteinCoverageComponent.this.addComponent(chainCoverage3dLayout, "left:0; top:0px;");
                    ProteinCoverageComponent.this.addComponent(proteinCoverageLayout, "left:0; top:10px;");
                    ProteinCoverageComponent.this.addComponent(peptideDistributionLayout, "left:0; top:25px;");
                }
                if (v) {
                    proteinCoverageLayout.removeStyleName("hideinvisible");
                } else {
                    proteinCoverageLayout.addStyleName("hideinvisible");
                }
                super.setVisible(v);
            }
        };

        proteinCoverageLayout = new ProteinCoverageLayout(styles.get(protein.getValidation()), styles.get(protein.getProteinEvidence()));
        peptideDistMap = new TreeSet<>(Collections.reverseOrder());
        proteinCoverageLayout.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            resetHeighlightedProteoforms();
            selectPeptide(protein.getAccession(), null);
        });

        peptideDistributionLayout = new AbsoluteLayout();
        peptideDistributionLayout.setWidth(100, Unit.PERCENTAGE);
        peptideDistributionLayout.setHeight(100, Unit.PERCENTAGE);
        peptideDistributionLayout.addStyleName("peptidecoverage");

        LayoutEvents.LayoutClickListener peptidesListener = (LayoutEvents.LayoutClickEvent event) -> {

            if (suspendListener)
                return;
            suspendListener = true;
            Component clickedComp = event.getClickedComponent();
            if (clickedComp == null) {
                return;
            }
            if ((clickedComp.getStyleName().contains("proteoformcoverage") || clickedComp.getStyleName().contains("proteoformmodstyle") || (clickedComp.getStyleName().contains("peptidelayout") && (clickedComp instanceof VerticalLayout)))) {

            } else if (clickedComp.getStyleName().contains("peptidelayout")) {
                PeptideLayout genPeptid = (PeptideLayout) clickedComp;
                selectPeptide(protein.getAccession(), genPeptid.getPeptideId());
//                resetHeighlightedProteoforms();
            } else if (clickedComp instanceof Label) {
                Object data = ((Label) clickedComp).getData();
                if (data != null) {
                    selectPeptide(protein.getAccession(), ((Label) clickedComp).getData());
//                    resetHeighlightedProteoforms();
                }
            } else if (clickedComp instanceof VerticalLayout) {
                selectPeptide(protein.getAccession(), ((VerticalLayout) clickedComp).getData());
//                resetHeighlightedProteoforms();

            }
            suspendListener = false;
        };

        peptideDistributionLayout.addLayoutClickListener(peptidesListener);
        resizeFactor = 100f / Float.valueOf(protein.getSequence().length());
        int[] distArr = new int[protein.getSequence().length()];

        peptidesNodes.values().forEach((peptide) -> {
            if (protein.getSequence().contains(peptide.getSequence())) {
                int current = 0;
                int index;
                while (true) {
                    index = protein.getSequence().indexOf(peptide.getSequence(), current);
                    current = index + peptide.getSequence().length();
                    if (index == -1) {
                        break;
                    }
                    int startIndex = index;
                    float left = (index) * resizeFactor;
                    float width = (peptide.getSequence().length() * resizeFactor);
                    width = (width / (100f - left) * 100);
                    int topLevel = 0;
                    for (char c : peptide.getSequence().toCharArray()) {
                        distArr[index] = distArr[index] + 1;
                        topLevel = Math.max(topLevel, distArr[index]);
                        index++;
                    }
                    PeptideLayout genPeptide = new PeptideLayout(peptide, width, startIndex, left, styles.get(peptide.getValidation()), styles.get("Not Applicable"), protein.isEnymaticPeptide(peptide.getModifiedSequence()), colorScale.getColor(peptide.getPSMsNumber()));
                    peptideDistMap.add(genPeptide);
                    peptideObjectsSet.add(peptide);
                    for (ModificationMatch mod : peptide.getVariableModifications()) {
//                        mod = mod.split("\\(")[0].trim();
//                        if (mod.equalsIgnoreCase("")) {
//                            continue;
//                        }
                        if (!modificationPeptideMap.containsKey(mod.getModification())) {
                            modificationPeptideMap.put(mod.getModification(), new LinkedHashSet<>());
                        }
                        modificationPeptideMap.get(mod.getModification()).add(genPeptide);
                    }

                }

            } else if (protein.getSequence().toLowerCase().replaceAll("i", "l").contains(peptide.getSequence().toLowerCase().replaceAll("i", "l"))) {
                String tempProtSeq = protein.getSequence().toLowerCase().replaceAll("i", "l");
                String tempPeptSeq = peptide.getSequence().toLowerCase().replaceAll("i", "l");
                int current = 0;
                int index;
                while (true) {
                    index = tempProtSeq.indexOf(tempPeptSeq, current);
                    current = index + tempPeptSeq.length();
                    if (index == -1) {
                        break;
                    }
                    int startIndex = index;
                    float left = (index) * resizeFactor;
                    float width = (tempPeptSeq.length() * resizeFactor);
                    width = (width / (100f - left) * 100);
                    int topLevel = 0;
                    for (char c : tempPeptSeq.toCharArray()) {
                        distArr[index] = distArr[index] + 1;
                        topLevel = Math.max(topLevel, distArr[index]);
                        index++;
                    }
                    PeptideLayout genPeptide = new PeptideLayout(peptide, width, startIndex, left, styles.get(peptide.getValidation()), styles.get("Not Applicable"), protein.isEnymaticPeptide(peptide.getModifiedSequence()), colorScale.getColor(peptide.getPSMsNumber()));
                    peptideDistMap.add(genPeptide);
                    peptideObjectsSet.add(peptide);

                }
            }
        });

        for (int index = 0; index < distArr.length; index++) {
            if (distArr[index] > 0) {
                float left = index * resizeFactor;
                int start = index;
                float w = 0;
                for (; index < distArr.length && distArr[index] > 0; index++) {
                    w++;
                }
                String desc = protein.getSequence().substring(start, (start + (int) w));
                w = w * resizeFactor;
                w = (w / (100f - left) * 100);
                HighlightPeptide highlight = new HighlightPeptide(w, desc);
                proteinCoverageLayout.addComponent(highlight, "left:" + left + "%; bottom:0px;");
            }

        }
        proteinSequenceLength = protein.getSequence().length();
        int levelNum = initPeptideCoverageLayout();
        levelNum++;
        layoutHeight = 25 + (levelNum * 20) + 5;
        chainCoverage3dLayout.setVisible(false);
        ProteinCoverageComponent.this.setHeight(layoutHeight, Unit.PIXELS);
        layoutHeightProteoform = layoutHeight;

        proteoformCoverage = new AbsoluteLayout();
        proteoformCoverage.setWidth(100, Unit.PERCENTAGE);
        proteoformCoverage.setHeight(100, Unit.PERCENTAGE);
        proteoformCoverage.setVisible(false);
        proteoformCoverage.addStyleName("proteoformcoverage");

    }

    public void selectSubComponents(Set<String> peptidesId) {
        peptideDistMap.forEach((peptide) -> {
            peptide.setSelected(peptidesId.contains(peptide.getPeptideId()));
        });
        if (!peptidesId.isEmpty() && peptidesId.iterator().next().toString().contains(";")) {
            Iterator<Component> itr = proteoformCoverage.iterator();
            while (itr.hasNext()) {
                ProteoformLayout proteform = ((ProteoformLayout) itr.next());
                if (!peptidesId.contains(proteform.getData())) {
                    proteform.addStyleName("inactivatelayout");
                    proteform.setEnabled(false);
                    proteform.getIncludedModifications().stream().map((mod) -> {
                        mod.addStyleName("inactivatelayout");
                        return mod;
                    }).forEachOrdered((mod) -> {
                        mod.setEnabled(false);
                    });
                } else {
                    proteform.removeStyleName("inactivatelayout");
                    proteform.setEnabled(true);
                    proteform.getIncludedModifications().stream().map((mod) -> {
                        mod.removeStyleName("inactivatelayout");
                        return mod;
                    }).forEachOrdered((mod) -> {
                        mod.setEnabled(true);
                    });
                }
            }
        }
    }

    public Set<PeptideObject> getPeptideObjectsSet() {
        return peptideObjectsSet;
    }

    public void selectPeptides(Object peptideId) {
        final boolean selectAll = peptideId == null;
        peptideDistMap.forEach((PeptideLayout peptide) -> {
            peptide.setSelected(selectAll || peptide.getPeptideId().equals(peptideId));
        });

    }

    public void enable3D(Component view) {
        this.chainCoverage3dLayout.removeAllComponents();
        this.chainCoverage3dLayout.addComponent(view);
        view.setSizeFull();
        this.chainCoverage3dLayout.setHeight(30, Unit.PIXELS);
        this.chainCoverage3dLayout.setWidth(100, Unit.PERCENTAGE);
        this.chainCoverage3dLayout.setVisible(view.isVisible());

    }

    public abstract void selectPeptide(Object proteinId, Object peptideId);

    public void updateStylingMode(String style) {
        proteinCoverageLayout.updateStylingMode(style);
        final String updateStyle;
        if (style.equalsIgnoreCase("Proteoform")) {
            updateStyle = "Modifications";
            if (!proteoformInit && mainProteinObject.getProteoformsNodes() != null && !mainProteinObject.getProteoformsNodes().isEmpty()) {
                proteoformInit = true;
                int topCorrector = layoutHeight - 25;
                peptideDistributionLayout.addComponent(proteoformCoverage, "left:0px;top:" + topCorrector + "px;");
                int proteoformNum = 0;
                int top = 0;
                for (String proteoformKey : mainProteinObject.getProteoformsNodes().keySet()) {
                    Map<String, Integer> modificationMap = mainProteinObject.getProteoformsNodes().get(proteoformKey).getModificationsLocationsMap();
                    ProteoformLayout protolayout = new ProteoformLayout(++proteoformNum, top, mainProteinObject.getProteoformsNodes().get(proteoformKey).getFinalColor(), proteoformKey, (mainProteinObject.getProteoformsNodes().get(proteoformKey).getEdgesNumber() > 1)) {
                        @Override
                        public void selectProteoform(ProteoformLayout proteoform) {
                            selectProteinProteoform(proteoform);
                        }

                    };
                    protolayout.setData(proteoformKey);
                    protolayout.setDescription(mainProteinObject.getProteoformsNodes().get(proteoformKey).getDescription());
                    proteoformCoverage.addComponent(protolayout, "left:0px;top:" + top + "px;");
                    protolayout.updateHighlightedComponents(proteinCoverageLayout);
                    for (String mod : modificationMap.keySet()) {
                        ProteoformModificationLayout modLayout = new ProteoformModificationLayout(mod, modificationMap.get(mod)) {
                            @Override
                            public void selected(ProteoformModificationLayout proteoformModificationLayout) {
                                selectedProteoformModification(proteoformModificationLayout);
                            }

                        };
                        protolayout.addModificationLayout(modLayout);
                        float left = (float) modificationMap.get(mod) * resizeFactor;
                        peptideDistributionLayout.addComponent(modLayout, "left:" + left + "%; top:" + (top + topCorrector) + "px;");

                        if (modificationPeptideMap.containsKey(mod)) {
                            Set<PeptideLayout> peptides = modificationPeptideMap.get(mod);
                            peptides.forEach((peptide) -> {
                                String poStr = peptide.getPeptide().getPostion();

                                for (String st : poStr.split(";")) {
                                    int peptideStart = Integer.valueOf(st.split("\\(")[1].replace(")", "").trim());
                                    int peptideLocation = peptideStart + peptide.getPeptide().getSequence().length();
                                    int modLocation = modificationMap.get(mod);
                                    if (modLocation >= peptideStart && modLocation < peptideLocation) {
                                        modLayout.addCorrespondingPeptide(peptide);
                                    }
                                }
                            });
                        }
                    }
                    top += 20;
                    layoutHeightProteoform = (layoutHeight) + (proteoformNum * 20) + 25;

                }
            }
            ProteinCoverageComponent.this.setHeight(layoutHeightProteoform, Unit.PIXELS);
            proteoformCoverage.setVisible(true);
        } else if (style.equalsIgnoreCase("Protein-Peptide")) {
            resetHeighlightedProteoforms();
            ProteinCoverageComponent.this.setHeight(layoutHeight, Unit.PIXELS);
            return;
        } else {
            updateStyle = style;
            ProteinCoverageComponent.this.setHeight(layoutHeight, Unit.PIXELS);
            proteoformCoverage.setVisible(false);
        }
        peptideDistMap.forEach((peptide) -> {
            peptide.updateStylingMode(updateStyle);
        });

    }

    private int initPeptideCoverageLayout() {
        int levelNum = 1;
        int[] usedDistArr = new int[proteinSequenceLength];
        int level;
        int topLevel;
        for (PeptideLayout pep : peptideDistMap) {
            level = 0;
            topLevel = 0;
            for (int i = pep.getStartIndex(); i < pep.getEndIndex(); i++) {
                level = Math.max(usedDistArr[i], level);
                usedDistArr[i] = usedDistArr[i] + 1;
                topLevel = Math.max(topLevel, usedDistArr[i]);
            }
            for (int i = pep.getStartIndex(); i < pep.getEndIndex(); i++) {
                usedDistArr[i] = topLevel;
            }
            levelNum = Math.max(levelNum, topLevel);
            level = level * 20;
            peptideDistributionLayout.addComponent(pep, "left:" + pep.getX() + "%; top:" + level + "px;");
            pep.addLocation(level);
            if (pep.getPeptide().isInvisibleOn3d()) {

                AbsoluteLayout unmapped3dPeptide = new AbsoluteLayout();
                unmapped3dPeptide.setWidth(pep.getWidth(), pep.getWidthUnits());
                unmapped3dPeptide.setStyleName("invisiblepeptideon3d");
                unmapped3dPeptide.setHeight(15, Unit.PIXELS);
                VerticalLayout blackLine = new VerticalLayout();
                blackLine.setStyleName("graymiddleline");
                blackLine.setWidth(100, Unit.PERCENTAGE);
                blackLine.setHeight(2, Unit.PIXELS);
                unmapped3dPeptide.addComponent(blackLine, "left:0px;top:50%;");

                this.proteinCoverageLayout.addComponent(unmapped3dPeptide, "left:" + pep.getX() + "%; top:-24px;");
            }

        }
        return levelNum;

    }

    private void selectedProteoformModification(ProteoformModificationLayout proteoformModification) {
        if (!proteoformModification.isEnabled()) {
            return;
        }
        resetHeighlightedProteoforms();
        PeptideLayout genPeptid = proteoformModification.select();
        if (genPeptid != null) {
            selectPeptide(mainProteinObject.getAccession(), genPeptid.getPeptideId());
        }
    }

    private void resetHeighlightedProteoforms() {
        Iterator<Component> itr = peptideDistributionLayout.iterator();
        while (itr.hasNext()) {
            itr.next().removeStyleName("heighlightcorrespondingpeptide");
        }
        itr = proteoformCoverage.iterator();
        while (itr.hasNext()) {
            itr.next().removeStyleName("selectedproteoform");
        }

    }

    private void selectProteinProteoform(ProteoformLayout proteoform) {
        Iterator<Component> itr = proteoformCoverage.iterator();
        while (itr.hasNext()) {
            itr.next().removeStyleName("selectedproteoform");
        }
        proteoform.addStyleName("selectedproteoform");
    }

}
