package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview;

import com.google.common.collect.Sets;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.model.core.FilteredProteins;
import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import com.uib.web.peptideshaker.presenter.core.PresenterSubViewSideBtn;
import com.uib.web.peptideshaker.presenter.core.filtercharts.RegistrableFilter;
import com.vaadin.ui.Component;

import java.util.*;

/**
 * This class represents peptideShaker presenter selection manager
 *
 * @author Yehia Farag
 */
public class SelectionManager {

    private final Map<PresenterSubViewSideBtn, Component> btnsLayoutMap;
    private final Map<String, RegistrableFilter> registeredDatasetFiltersMap;
    private final Map<String, RegistrableFilter> registeredProteinComponentsMap;
    private final Set<Comparable> fullProteinSet;
    private final List<String> datasetFilterOrderList;
    private final Map<String, Set<Comparable>> selectedModificationsMap;
    private final Map<Integer, Set<Comparable>> selectedChromosomeMap;
    private final Map<String, Set<Comparable>> selectedPIMap;
    private final Map<String, Set<Comparable>> selectedProteinValidationMap;
    private final Map<String, Set<Comparable>> registeredDatasetAppliedFiltersMap;
    private final TreeMap<Comparable, Set<Comparable>> selectedProteinPeptidesNumberMap;
    private final TreeMap<Comparable, Set<Comparable>> selectedProteinPSMNumberMap;
    private final TreeMap<Comparable, Set<Comparable>> selectedProteinCoverageMap;
    private final TreeMap<Comparable, Set<Comparable>> selectedProteinInteinsityAllPepMap;
    private final TreeMap<Comparable, Set<Comparable>> selectedProteinInteinsityUniquePepMap;
    private FilteredProteins filteredProteinsSet;
    private String selectedProteinId;
    private PeptideObject selectedPeptide;
    private boolean singleProteinsFilter = false;
    private ModificationMatrix modificationMatrix;
    private Map<Integer, Set<Comparable>> chromosomeMap;
    private Map<String, Set<Comparable>> piMap;
    private Map<String, Set<Comparable>> proteinValidationMap;
    private TreeMap<Comparable, Set<Comparable>> proteinPeptidesNumberMap;
    private TreeMap<Comparable, Set<Comparable>> proteinPSMNumberMap;
    private TreeMap<Comparable, Set<Comparable>> proteinCoverageMap;
    private TreeMap<Comparable, Set<Comparable>> proteinIntinsityAllPepMap;
    private TreeMap<Comparable, Set<Comparable>> proteinIntinsityUniquePepMap;

    public SelectionManager() {
        this.btnsLayoutMap = new LinkedHashMap<>();
        this.registeredDatasetFiltersMap = new LinkedHashMap<>();
        this.registeredProteinComponentsMap = new LinkedHashMap<>();
        this.selectedModificationsMap = new LinkedHashMap<>();
        this.selectedChromosomeMap = new LinkedHashMap<>();
        this.selectedPIMap = new LinkedHashMap<>();
        this.selectedProteinValidationMap = new LinkedHashMap<>();
        this.fullProteinSet = new LinkedHashSet<>();
        this.registeredDatasetAppliedFiltersMap = new LinkedHashMap<>();
        this.selectedProteinCoverageMap = new TreeMap<>();
        this.selectedProteinPeptidesNumberMap = new TreeMap<>();
        this.selectedProteinPSMNumberMap = new TreeMap<>();
        this.selectedProteinInteinsityAllPepMap = new TreeMap<>();
        this.selectedProteinInteinsityUniquePepMap = new TreeMap<>();
        this.datasetFilterOrderList = new ArrayList<>();
    }

    public String getSelectedProteinId() {
        return selectedProteinId;
    }

    public Set<Comparable> getFullProteinSet() {
        return fullProteinSet;
    }

    public TreeMap<Comparable, Set<Comparable>> getProteinPeptidesNumberMap() {
        if (selectedProteinPeptidesNumberMap.isEmpty()) {
            return selectedProteinPeptidesNumberMap;
        } else {
            return proteinPeptidesNumberMap;
        }
    }

    public void setProteinPeptidesNumberMap(TreeMap<Comparable, Set<Comparable>> proteinPeptidesNumberMap) {
        this.selectedProteinPeptidesNumberMap.clear();
        this.proteinPeptidesNumberMap = proteinPeptidesNumberMap;
    }

    public TreeMap<Comparable, Set<Comparable>> getProteinPSMNumberMap() {
        if (selectedProteinPSMNumberMap.isEmpty()) {
            return selectedProteinPSMNumberMap;
        } else {
            return proteinPSMNumberMap;
        }
    }

    public void setProteinPSMNumberMap(TreeMap<Comparable, Set<Comparable>> proteinPSMNumberMap) {
        this.selectedProteinPSMNumberMap.clear();
        this.proteinPSMNumberMap = proteinPSMNumberMap;
    }

    public TreeMap<Comparable, Set<Comparable>> getProteinCoverageMap() {
        if (selectedProteinCoverageMap.isEmpty()) {
            return selectedProteinCoverageMap;
        } else {
            return proteinCoverageMap;
        }
    }

    public void setProteinCoverageMap(TreeMap<Comparable, Set<Comparable>> proteinCoverageMap) {
        this.selectedProteinCoverageMap.clear();
        this.proteinCoverageMap = proteinCoverageMap;
    }

    public Map<String, Set<Comparable>> getPiMap() {
        if (selectedPIMap.isEmpty()) {
            return piMap;
        } else {
            return selectedPIMap;
        }
    }

    public void setPiMap(Map<String, Set<Comparable>> piMap) {
        this.selectedPIMap.clear();
        this.piMap = piMap;
    }

    public Map<String, Set<Comparable>> getProteinValidationMap() {
        if (selectedProteinValidationMap.isEmpty()) {
            return proteinValidationMap;
        } else {
            return selectedProteinValidationMap;
        }
    }

    public void setProteinValidationMap(Map<String, Set<Comparable>> proteinValidationMap) {
        this.selectedProteinValidationMap.clear();
        this.proteinValidationMap = proteinValidationMap;
    }

    public Map<Integer, Set<Comparable>> getChromosomeMap() {
        if (selectedChromosomeMap.isEmpty()) {
            return chromosomeMap;
        } else {
            return selectedChromosomeMap;
        }
    }

    public void setChromosomeMap(Map<Integer, Set<Comparable>> chromosomeMap) {
        this.selectedChromosomeMap.clear();
        this.chromosomeMap = chromosomeMap;
    }

    public void reset() {
        fullProteinSet.clear();
        selectedChromosomeMap.clear();
        selectedModificationsMap.clear();
        selectedPIMap.clear();
        selectedProteinValidationMap.clear();
        modificationMatrix = null;
        piMap = null;
        chromosomeMap = null;
        proteinValidationMap = null;
        registeredDatasetAppliedFiltersMap.keySet().forEach((filterId) -> {
            registeredDatasetAppliedFiltersMap.get(filterId).clear();
        });

    }

    public void resetDatasetSelection() {
        datasetFilterOrderList.forEach((filterId) -> {
            registeredDatasetAppliedFiltersMap.get(filterId).clear();
        });
        filteredProteinsSet = new FilteredProteins();
        filteredProteinsSet.setFullProteinList(new LinkedHashSet<>(fullProteinSet));
        filteredProteinsSet.setFilteredProteinList(new LinkedHashSet<>(fullProteinSet));
        SelectionChanged("dataset_filter_selection", "reset");

    }

    public void setModificationsMap(ModificationMatrix modificationMatrix) {
        selectedModificationsMap.clear();
        this.modificationMatrix = modificationMatrix;
        modificationMatrix.getCalculatedColumns().values().forEach((set) -> {
            fullProteinSet.addAll(set);
        });
    }

    public void addBtnLayout(PresenterSubViewSideBtn btn, Component layout) {
        btnsLayoutMap.put(btn, layout);
    }

    public void selectBtn(PresenterSubViewSideBtn btn) {
        btnsLayoutMap.keySet().forEach((bbt) -> {
            if (btn.getData().toString().equalsIgnoreCase(bbt.getData().toString())) {
                bbt.setSelected(true);
                btnsLayoutMap.get(bbt).removeStyleName("hidepanel");
            } else {
                bbt.setSelected(false);
                btnsLayoutMap.get(bbt).addStyleName("hidepanel");
            }
        });

    }

    public void selectBtn(int index) {
        PresenterSubViewSideBtn btn = (PresenterSubViewSideBtn) btnsLayoutMap.keySet().toArray()[index];
        selectBtn(btn);

    }

    public void RegistrDatasetsFilter(RegistrableFilter filter) {
        this.registeredDatasetFiltersMap.put(filter.getFilterId(), filter);
        this.registeredDatasetAppliedFiltersMap.put(filter.getFilterId(), new LinkedHashSet<>());
        this.datasetFilterOrderList.add(filter.getFilterId());
    }

    public void RegistrProteinInformationComponent(RegistrableFilter filter) {
        registeredProteinComponentsMap.put(filter.getFilterId(), filter);
//        this.registeredDatasetFiltersMap.put(filter.getFilterId(), filter);
//        this.registeredDatasetAppliedFiltersMap.put(filter.getFilterId(), new LinkedHashSet<>());
//        this.datasetFilterOrderList.add(filter.getFilterId());
    }

    /**
     * Set Selection in the system to update other registered listeners
     *
     * @return
     */
    public PeptideObject getSelectedPeptide() {

        return selectedPeptide;

    }

    public void setSelectedPeptide(PeptideObject selectedPeptide) {
        this.selectedPeptide = selectedPeptide;
    }

    public void setSelection(String selectionType, Set<Comparable> filteringValue, Set<Comparable> filteredItemsSet, String filterId) {
        if (selectionType.equalsIgnoreCase("dataset_filter_selection")) {
            datasetFilterOrderList.remove(filterId);
            if (filteringValue.isEmpty()) {
                datasetFilterOrderList.add(filterId);//               
            } else {
                datasetFilterOrderList.add(0, filterId);
            }

            registeredDatasetAppliedFiltersMap.put(filterId, filteringValue);
            filteredProteinsSet = filterProteinData();
        } else if (selectionType.equalsIgnoreCase("protein_selection")) {
            selectedProteinId = (String) filteringValue.toArray()[0];
        } else if (selectionType.equalsIgnoreCase("peptide_selection")) {
//            selectedPeptide = (String)filteringValue.toArray()[0];

        }

        SelectionChanged(selectionType, filterId);
    }

    private FilteredProteins filterProteinData() {

        String topFilterId = datasetFilterOrderList.get(0);
        Set<Comparable> filteredProtenSet = new LinkedHashSet<>(fullProteinSet);
        Set<Comparable> tempProtenSet = new LinkedHashSet<>();
        Integer onlyFilter = 0;
        registeredDatasetAppliedFiltersMap.keySet().stream().filter((filterId) -> !(filterId.equals(topFilterId))).forEachOrdered((filterId) -> {
            proteinFilterUtility(filterId, onlyFilter, tempProtenSet, filteredProtenSet);
        });
        FilteredProteins filteredProteinList = new FilteredProteins();
        filteredProteinList.setFullProteinList(new LinkedHashSet<>(filteredProtenSet));
        proteinFilterUtility(topFilterId, onlyFilter, tempProtenSet, filteredProtenSet);
        filteredProteinList.setFilteredProteinList(new LinkedHashSet<>(filteredProtenSet));

        singleProteinsFilter = onlyFilter == 1;
        return filteredProteinList;

    }

    private void proteinFilterUtility(String filterId, Integer onlyFilter, Set<Comparable> tempProtenSet, Set<Comparable> filteredProtenSet) {

        Set<Comparable> selectedCategories = registeredDatasetAppliedFiltersMap.get(filterId);
        if (!selectedCategories.isEmpty()) {
            onlyFilter++;
        }
        if (filterId.equalsIgnoreCase("modifications_filter") && !selectedCategories.isEmpty()) {

//            selectedCategories.stream().map((str) -> {
//                tempProtenSet.addAll(Sets.difference(filteredProtenSet, this.modificationMatrix.getCalculatedColumns().get(str.toString())));
//                return str;
//            }).map((_item) -> {
//                filteredProtenSet.removeAll(tempProtenSet);
//                return _item;
//            }).forEachOrdered((_item) -> {
//                tempProtenSet.clear();
//            });
            Set<Comparable> selectedData = new LinkedHashSet<>();
            selectedCategories.stream().filter((str) -> !(str == null)).forEachOrdered((str) -> {
                selectedData.addAll(Sets.intersection(filteredProtenSet, this.modificationMatrix.getCalculatedColumns().get(str.toString())));
            });
            tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
            filteredProtenSet.removeAll(tempProtenSet);
            tempProtenSet.clear();

        } else if (filterId.equalsIgnoreCase("chromosome_filter") && !selectedCategories.isEmpty()) {
            Set<Comparable> selectedData = new LinkedHashSet<>();
            selectedCategories.stream().filter((cat) -> !(cat == null)).forEachOrdered((cat) -> {
                selectedData.addAll(Sets.intersection(chromosomeMap.get(Integer.valueOf(cat.toString())), filteredProtenSet));
            });
            tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
            filteredProtenSet.removeAll(tempProtenSet);
            tempProtenSet.clear();

        } else if (filterId.equalsIgnoreCase("PI_filter") && !selectedCategories.isEmpty()) {
            Set<Comparable> selectedData = new LinkedHashSet<>();
            selectedCategories.stream().filter((cat) -> !(cat == null)).forEachOrdered((cat) -> {
                selectedData.addAll(Sets.intersection(piMap.get(cat.toString()), filteredProtenSet));
            });
            tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
            filteredProtenSet.removeAll(tempProtenSet);
            tempProtenSet.clear();

        } else if (filterId.equalsIgnoreCase("validation_filter") && !selectedCategories.isEmpty()) {
            Set<Comparable> selectedData = new LinkedHashSet<>();
            selectedCategories.stream().filter((cat) -> !(cat == null)).forEachOrdered((cat) -> {
                selectedData.addAll(Sets.intersection(proteinValidationMap.get(cat.toString()), filteredProtenSet));
            });
            tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
            filteredProtenSet.removeAll(tempProtenSet);
            tempProtenSet.clear();

        } else if (filterId.equalsIgnoreCase("peptidesNum_filter") && !selectedCategories.isEmpty()) {
            Set<Comparable> selectedData = new LinkedHashSet<>();
            double min = (double) selectedCategories.toArray()[0];
            double max;//= (double) selectedCategories.toArray()[1];
            if (selectedCategories.size() == 1) {
                max = min;
            } else {
                max = (double) selectedCategories.toArray()[1];
            }
            proteinPeptidesNumberMap.keySet().forEach((cat) -> {
                int key = (int) cat;
                if (key >= min && key <= max) {
                    selectedData.addAll(Sets.intersection(proteinPeptidesNumberMap.get(cat), filteredProtenSet));
                }
            });
            tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
            filteredProtenSet.removeAll(tempProtenSet);
            tempProtenSet.clear();

        } else if (filterId.equalsIgnoreCase("psmNum_filter") && !selectedCategories.isEmpty()) {
            Set<Comparable> selectedData = new LinkedHashSet<>();
            double min = (double) selectedCategories.toArray()[0];
            double max;//= (double) selectedCategories.toArray()[1];
            if (selectedCategories.size() == 1) {
                max = min;
            } else {
                max = (double) selectedCategories.toArray()[1];
            }
            proteinPSMNumberMap.keySet().forEach((cat) -> {
                int key = (int) cat;
                if (key >= min && key <= max) {
                    selectedData.addAll(Sets.intersection(proteinPSMNumberMap.get(cat), filteredProtenSet));
                }
            });
            tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
            filteredProtenSet.removeAll(tempProtenSet);
            tempProtenSet.clear();

        } else if (filterId.equalsIgnoreCase("possibleCoverage_filter") && !selectedCategories.isEmpty()) {
            Set<Comparable> selectedData = new LinkedHashSet<>();
            double min = (double) selectedCategories.toArray()[0];
            double max;//= (double) selectedCategories.toArray()[1];
            if (selectedCategories.size() == 1) {
                max = min;
            } else {
                max = (double) selectedCategories.toArray()[1];
            }
            proteinCoverageMap.keySet().forEach((cat) -> {
                int key = (int) cat;
                if (key >= min && key <= max) {
                    selectedData.addAll(Sets.intersection(proteinCoverageMap.get(cat), filteredProtenSet));
                }
            });
            tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
            filteredProtenSet.removeAll(tempProtenSet);
            tempProtenSet.clear();

        } else if (filterId.equalsIgnoreCase("intensityAllPep_filter") && !selectedCategories.isEmpty()) {
            Set<Comparable> selectedData = new LinkedHashSet<>();
            double min = (double) selectedCategories.toArray()[0];
            double max;//= (double) selectedCategories.toArray()[1];
            if (selectedCategories.size() == 1) {
                max = min;
            } else {
                max = (double) selectedCategories.toArray()[1];
            }
            proteinIntinsityAllPepMap.keySet().forEach((cat) -> {
                int key = (int) cat;
                if (key >= min && key <= max) {
                    selectedData.addAll(Sets.intersection(proteinIntinsityAllPepMap.get(cat), filteredProtenSet));
                }
            });
            tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
            filteredProtenSet.removeAll(tempProtenSet);
            tempProtenSet.clear();

        } else if (filterId.equalsIgnoreCase("intensityUniquePep_filter") && !selectedCategories.isEmpty()) {
            Set<Comparable> selectedData = new LinkedHashSet<>();
            double min = (double) selectedCategories.toArray()[0];
            double max;//= (double) selectedCategories.toArray()[1];
            if (selectedCategories.size() == 1) {
                max = min;
            } else {
                max = (double) selectedCategories.toArray()[1];
            }
            proteinIntinsityUniquePepMap.keySet().forEach((cat) -> {
                int key = (int) cat;
                if (key >= min && key <= max) {
                    selectedData.addAll(Sets.intersection(proteinIntinsityUniquePepMap.get(cat), filteredProtenSet));
                }
            });
            tempProtenSet.addAll(Sets.difference(filteredProtenSet, selectedData));
            filteredProtenSet.removeAll(tempProtenSet);
            tempProtenSet.clear();

        }

    }

    public boolean isSingleProteinsFilter() {
        return singleProteinsFilter;
    }

    public boolean isDatasetFilterApplied() {
        return (filteredProteinsSet.getFilteredProteinList().size() != fullProteinSet.size());

    }

    /**
     * Loop responsible for updating all registered listeners
     *
     * @param type     selection type
     * @param filterId filter that create the event
     */
    private void SelectionChanged(String selectionType, String actionFilterId) {

        if (selectionType.equalsIgnoreCase("dataset_filter_selection")) {
            for (int index = 0; index < datasetFilterOrderList.size(); index++) {
                String filterId = datasetFilterOrderList.get(index);
                if (index == 0) {
                    registeredDatasetFiltersMap.get(filterId).updateFilterSelection(filteredProteinsSet.getFullProteinList(), registeredDatasetAppliedFiltersMap.get(filterId), (index == 0), singleProteinsFilter, (filterId.equalsIgnoreCase(actionFilterId)));
                } else {
                    registeredDatasetFiltersMap.get(filterId).updateFilterSelection(filteredProteinsSet.getFilteredProteinList(), registeredDatasetAppliedFiltersMap.get(filterId), (index == 0), singleProteinsFilter, (filterId.equalsIgnoreCase(actionFilterId)));
                }
//                
            }

        } else if (selectionType.equalsIgnoreCase("protein_selection") || selectionType.equalsIgnoreCase("peptide_selection")) {
            registeredProteinComponentsMap.keySet().forEach((filterId) -> {
                registeredProteinComponentsMap.get(filterId).selectionChange(selectionType);
            });
        }
    }

    public Set<Comparable> getAppliedFilterCategories(String filterId) {

        return registeredDatasetAppliedFiltersMap.get(filterId);

    }

    //    public Set<Comparable> getFilteredProteinsSet() {
//        if (filteredProteinsSet == null) {
//            System.out.println("there is a null selected proted");
//        }
//        return filteredProteinsSet;
//    }
    public TreeMap<Comparable, Set<Comparable>> getProteinIntinsityAllPepMap() {
        if (selectedProteinInteinsityAllPepMap.isEmpty()) {
            return selectedProteinInteinsityAllPepMap;
        }
        return proteinIntinsityAllPepMap;
    }

    public void setProteinIntinsityAllPepMap(TreeMap<Comparable, Set<Comparable>> proteinIntinsityAllPepMap) {
        this.selectedProteinInteinsityAllPepMap.clear();
        this.proteinIntinsityAllPepMap = proteinIntinsityAllPepMap;

    }

    public TreeMap<Comparable, Set<Comparable>> getProteinIntinsityUniquePepMap() {
        if (selectedProteinInteinsityUniquePepMap.isEmpty()) {
            return selectedProteinInteinsityUniquePepMap;
        }
        return proteinIntinsityUniquePepMap;
    }

    public void setProteinIntinsityUniquePepMap(TreeMap<Comparable, Set<Comparable>> proteinIntinsityUniquePepMap) {
        this.selectedProteinInteinsityUniquePepMap.clear();
        this.proteinIntinsityUniquePepMap = proteinIntinsityUniquePepMap;
    }
}
