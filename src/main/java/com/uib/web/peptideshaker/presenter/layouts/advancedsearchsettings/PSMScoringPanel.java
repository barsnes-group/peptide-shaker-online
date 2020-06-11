package com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings;

import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.experiment.identification.psm_scoring.PsmScore;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.advanced.PsmScoringParameters;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.vaadin.data.Item;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author y-mok
 */
public class PSMScoringPanel extends PopupWindow {

    private IdentificationParameters webSearchParameters;
    private Table psmScoreSelectionTable;
    private final VerticalLayout subContainer;
    private HashMap<Integer, HashSet<Integer>> spectrumMatchingScores;
    private HashMap<String, HashSet<Integer>> spectrumMatchingScoresName;

    public PSMScoringPanel() {
        super(VaadinIcons.COG.getHtml() + " PSM Scoring");
        Label title = new Label("PSM Scoring");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(500, Unit.PIXELS);
        container.setHeight(257, Unit.PIXELS);

        subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("importfiltersubcontainer");

        container.addComponent(title, "left:10px;top:10px");
        container.addComponent(subContainer, "left:10px;top:45px;right:10px;bottom:40px");

        psmScoreSelectionTable = new Table();
        psmScoreSelectionTable.setSizeFull();
        psmScoreSelectionTable.setStyleName(ValoTheme.TABLE_SMALL);
        psmScoreSelectionTable.addContainerProperty("index", Integer.class, null, "", null, Table.Align.RIGHT);
        psmScoreSelectionTable.addContainerProperty("name", String.class, null, " ", null, Table.Align.LEFT);
        psmScoreSelectionTable.addContainerProperty("native", CheckBox.class, null, "Native", null, Table.Align.CENTER);
        psmScoreSelectionTable.addContainerProperty("precursorAccuracy", CheckBox.class, null, "Precursor Accuracy", null, Table.Align.CENTER);
        psmScoreSelectionTable.addContainerProperty("hyperscore", CheckBox.class, null, "Hyperscore", null, Table.Align.CENTER);
        psmScoreSelectionTable.addContainerProperty("snr", CheckBox.class, null, "snr", null, Table.Align.CENTER);

//        psmScoreSelectionTable.setColumnWidth("index", 0);
//        psmScoreSelectionTable.setColumnWidth("name", 0);
//        psmScoreSelectionTable.setColumnWidth("native", 0);
//        psmScoreSelectionTable.setColumnWidth("precursorAccuracy", 0);
//        psmScoreSelectionTable.setColumnWidth("hyperscore", 0);
//        psmScoreSelectionTable.setColumnWidth(snr, 0);
        subContainer.addComponent(psmScoreSelectionTable);

        PSMScoringPanel.this.setContent(container);
        PSMScoringPanel.this.setClosable(true);

        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            updateParameters();
            PSMScoringPanel.this.setPopupVisible(false);

        });
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(76, Unit.PIXELS);
        cancelBtn.setHeight(20, Unit.PIXELS);
        container.addComponent(okBtn, "bottom:10px;right:96px");
        container.addComponent(cancelBtn , "bottom:10px;right:10px");
        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            PSMScoringPanel.this.setPopupVisible(false);
        });

    }
    private ArrayList<String> scoresNames;

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        PsmScoringParameters psmScoringPreferences = webSearchParameters.getPsmScoringParameters();
        //init table
        subContainer.removeAllComponents();
        psmScoreSelectionTable = new Table();
        psmScoreSelectionTable.setSizeFull();
        subContainer.addComponent(psmScoreSelectionTable);
        psmScoreSelectionTable.setStyleName(ValoTheme.TABLE_SMALL);
        psmScoreSelectionTable.addContainerProperty("index", Integer.class, null, "", null, Table.Align.RIGHT);
        psmScoreSelectionTable.addContainerProperty("name", String.class, null, " ", null, Table.Align.LEFT);

        // get all implemented scores
        PsmScore[] psmScores = PsmScore.values();
        scoresNames = new ArrayList<>(psmScores.length);
        for (PsmScore psmScore : psmScores) {
            scoresNames.add(psmScore.name);
            psmScoreSelectionTable.addContainerProperty(psmScore.name, CheckBox.class, null, psmScore.name, null, Table.Align.CENTER);

        }

        // get scores for each algorithm
        Set<Integer> advocates = psmScoringPreferences.getAdvocates();
        spectrumMatchingScores = new HashMap<>(advocates.size());
        spectrumMatchingScoresName = new HashMap<>(advocates.size());
        advocates.forEach((advocate) -> {
            HashSet<Integer> scores = new HashSet<>(psmScoringPreferences.getScoreForAlgorithm(advocate));
            spectrumMatchingScores.put(advocate, scores);
        });
        HashSet<Integer> defaultScores = new HashSet<>(psmScoringPreferences.getDefaultScores());

        // make an ordered list of the selected algorithms
        ArrayList<String> advocateNames = new ArrayList<>(spectrumMatchingScores.size());
        advocates.stream().map((advocateId) -> Advocate.getAdvocate(advocateId)).forEachOrdered((advocate) -> {
            advocateNames.add(advocate.getName());
        });
        Collections.sort(advocateNames);
        advocateNames.add("Default");
        int index = 1;
        for (String advName : advocateNames) {
            Object[] item = new Object[scoresNames.size() + 2];
            item[0] = index;
            item[1] = advName;
            Integer i;
            if (advName.equals("Default")) {
                for (int defaultScoreIndex : defaultScores) {
                    PsmScore psmScore = PsmScore.getScore(defaultScoreIndex);
                    CheckBox scoreCheckbox = new CheckBox();
                    scoreCheckbox.setValue(true);
                    scoreCheckbox.setEnabled(false);
                    item[scoresNames.indexOf(psmScore.name) + 2] = scoreCheckbox;

                }

            } else {
                Advocate advocate = Advocate.getAdvocate(advName);
                i = advocate.getIndex();
                int scoreCounter = 2;
                for (String scoreName : scoresNames) {
                    PsmScore psmScore = PsmScore.getScore(scoreName);
                    CheckBox scoreCheckbox = new CheckBox();
                    if (psmScore != null) {
                        Integer scoreIndex = psmScore.index;
                        HashSet<Integer> algorithmScores = spectrumMatchingScores.get(i);
                        if (algorithmScores == null || algorithmScores.isEmpty()) {
                            scoreCheckbox.setValue(Boolean.FALSE);
                        } else {
                            scoreCheckbox.setValue(algorithmScores.contains(scoreIndex));
                        }
                    } else {
                        scoreCheckbox.setEnabled(false);
                    }
                    item[scoreCounter] = scoreCheckbox;
                    scoreCounter++;
                }
            }

            psmScoreSelectionTable.addItem(item, index);
            index++;
        }
        super.setLabelValue(VaadinIcons.COG.getHtml() + " PSM Scoring" + "<center>" + webSearchParameters.getPsmScoringParameters().getShortDescription() + "</center>");

    }

    @Override
    public void onClosePopup() {
    }

    @Override
    public void setPopupVisible(boolean visible) {
        if (visible && webSearchParameters != null) {
            updateGUI(webSearchParameters);
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        PsmScoringParameters psmScoringPreferences = new PsmScoringParameters();
        psmScoringPreferences.clearAllScores();
        psmScoringPreferences.getDefaultScores().addAll(webSearchParameters.getPsmScoringParameters().getDefaultScores());

        Collection ids = psmScoreSelectionTable.getItemIds();
        ids.forEach((_item) -> {
            Item item = psmScoreSelectionTable.getItem(_item);
            Advocate advocate = Advocate.getAdvocate(item.getItemProperty("name").getValue().toString());

            scoresNames.forEach((scoreName) -> {
                CheckBox cb = ((CheckBox) item.getItemProperty(scoreName).getValue());
                if (cb != null) {
                    if (advocate != null) {
                        int algorithm = advocate.getIndex();
                        if (cb.getValue()) {
                            PsmScore psmScore = PsmScore.getScore(scoreName);
                            psmScoringPreferences.addScore(algorithm, psmScore.index);
                        }
                    }
                }
            });

        });
        webSearchParameters.setPsmScoringParameters(psmScoringPreferences);
        super.setLabelValue(VaadinIcons.COG.getHtml() + " PSM Scoring" + "<center>" + webSearchParameters.getPsmScoringParameters().getShortDescription() + "</center>");

//       webSearchParametersgetPsmScoringParameters().setProteinConfidenceMwPlots(Double.valueOf(proteinConfidenceMW.getSelectedValue()));
    }

    /**
     * Returns the PSM scoring preferences as set by the user.
     *
     * @return the PSM scoring preferences as set by the user
     */
    public PsmScoringParameters getPsmScoringPreferences() {
        PsmScoringParameters psmScoringPreferences = new PsmScoringParameters();
//        psmScoringPreferences.clearAllScores();
//        for (Integer algorithm : spectrumMatchingScores.keySet()) {
//            HashSet<Integer> scores = spectrumMatchingScores.get(algorithm);
//            for (Integer score : scores) {
//                psmScoringPreferences.addScore(algorithm, score);
//            }
//        }
        return psmScoringPreferences;
    }

}
