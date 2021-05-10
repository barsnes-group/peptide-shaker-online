package com.uib.web.peptideshaker.ui.views.subviews.proteinsview;

import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.PeptideObject;
import com.uib.web.peptideshaker.model.ChainBlock;
import com.uib.web.peptideshaker.model.PDBMatch;
import com.uib.web.peptideshaker.ui.views.subviews.proteinsview.components.ChainCoverageComponent;
import com.uib.web.peptideshaker.ui.views.subviews.proteinsview.components.LiteMOL3DComponent;
import com.vaadin.data.Property;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * This class represents 3D protein structure panel using JSMOL web service
 *
 * @author Yehia Mokhtar Farag
 */
public class Protein3DStructureView extends AbsoluteLayout {

    private final VerticalLayout LiteMolPanel;
    private final LiteMOL3DComponent liteMOL3DComponent;
    private final JsonObject peptidesQueryMap;
    private final ComboBox pdbMatchesSelect;
    private final ComboBox pdbChainsSelect;
    private final Property.ValueChangeListener pdbMatchSelectlistener;
    private final Property.ValueChangeListener pdbChainsSelectlistener;
    private final int[] defaultSelectedBlueColor = new int[]{25, 125, 255};
    private final int[] defaultUnSelectedBlueColor = new int[]{132, 191, 249};
    private final int[] defaultSelectedConfidentColor = new int[]{0, 128, 0};
    private final int[] defaultUnselectedConfidenColor = new int[]{159, 242, 159};
    private final int[] defaultSelectedDoubtfulColor = new int[]{253, 167, 8};
    private final int[] defaultUnselectedDoubtfulColor = new int[]{255, 224, 192};
    private final int[] defaultSelectedNotValidatedColor = new int[]{233, 0, 0};
    private final int[] defaultUnselectedNotValidatedColor = new int[]{233, 146, 146};
    private final int[] defaultSelectedNotAvailableColor = new int[]{211, 211, 211};
    private final int[] defaultUnselectedNotAvailableColor = new int[]{246, 246, 246};
    private final int[] basicColor = new int[]{226, 226, 226};
    private final Map<String, PeptideObject> proteinPeptidesMap;
    private final Map<String, List<ChainBlock>> pdbBlockMap;
    private final Label uniprotLabel;

    /**
     * The post translational modifications factory.
     */
    private final ModificationFactory PTM = ModificationFactory.getInstance();
    private AbsoluteLayout chainCoverageLayout;
    private int moleculeMode = 4;
    private ChainCoverageComponent lastSelectedChainCoverage;
    private Object lastSelectedAccession;
    private String lastSelectedProteinSequence;
    private String lastSelectedPeptideKey;
    private Collection<PeptideObject> proteinPeptides;
    private int proteinSequenceLength;
    private PDBMatch lastSelectedMatch;
    private final AppManagmentBean appManagmentBean;
    private final Label infoLabel;

    public Protein3DStructureView() {
        this.appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        Protein3DStructureView.this.setSizeFull();
        Protein3DStructureView.this.setStyleName("proteinStructiorpanel");

        this.pdbBlockMap = new HashMap<>();
        this.peptidesQueryMap = new JsonObject();
        this.proteinPeptidesMap = new LinkedHashMap<>();

        LiteMolPanel = new VerticalLayout();
        LiteMolPanel.setSizeFull();

        liteMOL3DComponent = new LiteMOL3DComponent();
        liteMOL3DComponent.setSizeFull();
        liteMOL3DComponent.setVisible(true);

        chainCoverageLayout = new AbsoluteLayout() {
            @Override
            public void setVisible(boolean v) {
                if (this.getParent() != null) {
                    this.getParent().setVisible(v);
                }
                super.setVisible(v);
            }
        };
        chainCoverageLayout.setSizeFull();
        LiteMolPanel.addComponent(liteMOL3DComponent);
        Protein3DStructureView.this.addComponent(LiteMolPanel);

        uniprotLabel = new Label("UniProt:");
        uniprotLabel.addStyleName("selectchain3dMenue");
//        uniprotLabel.setVisible(false);

        pdbChainsSelect = new ComboBox("Chains:");
        pdbChainsSelect.setTextInputAllowed(false);
        pdbChainsSelect.addStyleName("select3dMenue");
        pdbChainsSelect.addStyleName("selectchain3dMenue");
        pdbChainsSelect.setNullSelectionAllowed(false);
//        pdbChainsSelect.setVisible(true);
        HorizontalLayout dropdownMenuesContainer = new HorizontalLayout();
        dropdownMenuesContainer.setWidth(321, Unit.PIXELS);
        dropdownMenuesContainer.setHeight(25, Unit.PIXELS);
        dropdownMenuesContainer.setStyleName("select3dMenue");

        Protein3DStructureView.this.addComponent(dropdownMenuesContainer, "left: 10px; bottom:-12.5px");
        this.pdbChainsSelectlistener = ((Property.ValueChangeEvent event) -> {
            LiteMolPanel.setVisible(pdbChainsSelect.getValue() != null);
            if (LiteMolPanel.isVisible()) {
                lastSelectedChainCoverage.selectChain(pdbChainsSelect.getValue() + "");
                chainCoverageLayout.removeAllComponents();
                chainCoverageLayout.addComponent(lastSelectedChainCoverage.getChainCoverageWebComponent());
                contsructQueries(pdbBlockMap.get(pdbChainsSelect.getValue() + ""));
                selectPeptide(lastSelectedPeptideKey);

            }
        });
        pdbChainsSelect.addValueChangeListener(pdbChainsSelectlistener);
        pdbMatchesSelect = new ComboBox("PDB:");
        pdbMatchesSelect.setTextInputAllowed(false);
        pdbMatchesSelect.addStyleName("select3dMenue");
        pdbMatchesSelect.setNullSelectionAllowed(false);
        pdbMatchesSelect.setCaptionAsHtml(true);
//        pdbMatchesSelect.setVisible(true);
        dropdownMenuesContainer.addComponent(uniprotLabel);
        dropdownMenuesContainer.setExpandRatio(uniprotLabel, 115);
        dropdownMenuesContainer.addComponent(pdbMatchesSelect);
        dropdownMenuesContainer.setExpandRatio(pdbMatchesSelect, 108);
        dropdownMenuesContainer.addComponent(pdbChainsSelect);
        dropdownMenuesContainer.setExpandRatio(pdbChainsSelect, 98);

        this.pdbMatchSelectlistener = ((Property.ValueChangeEvent event) -> {
            //time to active d3
            pdbChainsSelect.removeValueChangeListener(pdbChainsSelectlistener);
            pdbChainsSelect.removeAllItems();
//            LiteMolPanel.setVisible(pdbMatchesSelect.getValue() != null);//
            lastSelectedMatch = appManagmentBean.getPdbUtils().updatePdbInformation(pdbMatchesSelect.getValue().toString(), lastSelectedProteinSequence, lastSelectedAccession);
            pdbBlockMap.clear();
            lastSelectedChainCoverage = reCalculateChainRange(lastSelectedMatch.getChains(), proteinSequenceLength);
            pdbChainsSelect.addItem("All");
            pdbChainsSelect.setItemCaption("All", "All");
            pdbChainsSelect.setItemIcon("All", new ExternalResource(lastSelectedChainCoverage.selectChain("All")));
            pdbBlockMap.put("All", new ArrayList<>());

            lastSelectedMatch.getChains().stream().map((chain) -> {
                if (!pdbBlockMap.containsKey(chain.getChain_id())) {
                    List<ChainBlock> blocks = new ArrayList<>();
                    pdbBlockMap.put(chain.getChain_id(), blocks);
                }
                return chain;
            }).map((chain) -> {
                pdbChainsSelect.addItem(chain.getChain_id());
                return chain;
            }).map((chain) -> {
                pdbChainsSelect.setItemCaption(chain.getChain_id(), chain.getChain_id());
                return chain;
            }).map((chain) -> {
                pdbChainsSelect.setItemIcon(chain.getChain_id(), new ExternalResource(lastSelectedChainCoverage.selectChain(chain.getChain_id())));
                return chain;
            }).map((chain) -> {
                pdbBlockMap.get(chain.getChain_id()).add(chain);
                return chain;
            }).forEachOrdered((chain) -> {
                pdbBlockMap.get("All").add(chain);
            });
            if (pdbChainsSelect.getItemIds().size() == 2) {
                pdbBlockMap.remove("All");
                pdbChainsSelect.removeItem("All");
            }
            pdbMatchesSelect.setDescription(pdbMatchesSelect.getItemCaption(pdbMatchesSelect.getValue()));
            pdbChainsSelect.addValueChangeListener(pdbChainsSelectlistener);
            pdbChainsSelect.setValue(pdbChainsSelect.getItemIds().iterator().next());

        });
        pdbMatchesSelect.addValueChangeListener(pdbMatchSelectlistener);
        this.infoLabel = new Label("<p style='color: #111318;font-weight: 200;width: 100% !important;text-align: center;font-size: 12px;-webkit-text-stroke: thin;'>Select single protein to visualize proteoforms and protein 3D structure</p>", ContentMode.HTML);
        infoLabel.setStyleName("highzindex");
        Protein3DStructureView.this.addComponent(infoLabel, "left: 0px; bottom:25px");

    }

    public void reset() {
        liteMOL3DComponent.reset();
        lastSelectedPeptideKey = null;
        pdbMatchesSelect.removeValueChangeListener(pdbMatchSelectlistener);
        pdbMatchesSelect.removeAllItems();
        this.lastSelectedAccession = null;
        this.lastSelectedProteinSequence = null;
        this.uniprotLabel.setValue("UniProt: ");
        pdbChainsSelect.removeValueChangeListener(pdbChainsSelectlistener);
        pdbChainsSelect.removeAllItems();
        pdbChainsSelect.setVisible(false);
        pdbMatchesSelect.setVisible(false);
        infoLabel.setVisible(true);
    }

    public void redrawCanvas() {
        liteMOL3DComponent.redrawCanvas();
    }

    public Object getLastSelectedAccession() {
        return lastSelectedAccession;
    }

    public void updatePanel(Object accession, String proteinSequence, Collection<PeptideObject> proteinPeptides) {
        
            pdbChainsSelect.setVisible(true);
            pdbMatchesSelect.setVisible(true);
            infoLabel.setVisible(false);
            this.lastSelectedAccession = accession;
            this.lastSelectedProteinSequence = proteinSequence;
            this.proteinPeptides = proteinPeptides;
            this.uniprotLabel.setValue("UniProt: " + lastSelectedAccession);
            loadData(lastSelectedAccession, lastSelectedProteinSequence, UI.getCurrent().getStyleName().contains("proteinpeptidessubviewstyle"));
       

    }

    private void loadData(Object accessionObject, String proteinSequence, boolean showNotifications) {
        if (accessionObject == null) {
            return;
        }
        pdbMatchesSelect.removeValueChangeListener(pdbMatchSelectlistener);
        pdbMatchesSelect.removeAllItems();
        pdbBlockMap.clear();
        String accession = accessionObject.toString();
        proteinSequenceLength = proteinSequence.length();
        Map<String, PDBMatch> pdbMachSet = appManagmentBean.getPdbUtils().getData(accession);
        if (pdbMachSet != null && !pdbMachSet.isEmpty()) {
            pdbMachSet.keySet().forEach((str) -> {
                pdbMatchesSelect.addItem(str);
                pdbMatchesSelect.setItemCaption(str, str.toUpperCase() + " " + pdbMachSet.get(str).getDescription());

            });
            if (pdbMatchesSelect.getItemIds() == null) {
                if (showNotifications) {
                    Notification.show("No visualization available ", Notification.Type.TRAY_NOTIFICATION);
                }
                reset();
                return;
            }
            pdbMatchesSelect.addValueChangeListener(pdbMatchSelectlistener);
            String pdbMachSelectValue = pdbMatchesSelect.getItemIds().toArray()[0] + "";
            pdbMatchesSelect.setValue(pdbMachSelectValue);

        } else {
            if (showNotifications) {
                Notification.show("No visualization available ", Notification.Type.TRAY_NOTIFICATION);
            }
            reset();
        }

    }

    private ChainCoverageComponent reCalculateChainRange(Collection<ChainBlock> chainBlocks, int proteinSequenceLength) {
        ChainCoverageComponent chainCoverage = new ChainCoverageComponent(proteinSequenceLength);
        chainBlocks.forEach((chain) -> {
            chainCoverage.addChainRange(chain.getChain_id(), chain.getStart_author_residue_number(), chain.getEnd_author_residue_number());
        });
        return chainCoverage;

    }

    public void selectPeptide(String peptideKey) {
        lastSelectedPeptideKey = peptideKey;
        String chainId = (pdbChainsSelect.getValue() + "");
        JsonArray entriesSet = new JsonArray();
        //color chains
        if (!chainId.equalsIgnoreCase("All")) {
            List<ChainBlock> chains = pdbBlockMap.get(pdbChainsSelect.getValue() + "");
            if (chains == null) {
                return;
            }
            chains.stream().map((selectedBlock) -> {
                int start = selectedBlock.getStart_residue_number();
                int end = selectedBlock.getEnd_residue_number();//we need color               
                JsonObject chainSeq = initSequenceMap(selectedBlock.getChain_id(), lastSelectedMatch.getEntity_id(selectedBlock.getChain_id()), start, end, "valid", false, null, selectedBlock.getUniprot_chain_sequence(), "", "");
                return chainSeq;
            }).map((chainSeq) -> {
                chainSeq.put("color", initColorMap(Color.WHITE));//selectedChainColor
                return chainSeq;
            }).forEachOrdered((chainSeq) -> {
                entriesSet.add(chainSeq);
            });
        }

        //set color based on protein overview radio btn
        switch (moleculeMode) {
            case 1:
                int[] defaultSelectedColor = null;
                int[] defaultUnselectedColor;
                if (peptideKey == null || peptideKey.contains("null")) {
                    defaultUnselectedColor = defaultSelectedBlueColor;
                } else {
                    defaultUnselectedColor = defaultUnSelectedBlueColor;
                    defaultSelectedColor = defaultSelectedBlueColor;
                }
                for (String key : peptidesQueryMap.fieldNames()) {
                    JsonArray arr = peptidesQueryMap.getJsonArray(key);
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject peptide = arr.getJsonObject(i);
                        if (peptideKey == null || peptideKey.contains("null") || !key.equals(peptideKey)) {
                            peptide.put("color", initColorMap(defaultUnselectedColor));
                        } else {
                            peptide.put("color", initColorMap(defaultSelectedColor));
                        }
                        entriesSet.add(peptide);
                    }
                }
                break;
            case 2: {
                int[] selectedConfidentColor;
                int[] selectedDoubtfulColor;
                int[] selectedNotValidatedColor;
                int[] selectedNotAvailableColor;
                if (peptideKey == null || peptideKey.contains("null")) {
                    selectedConfidentColor = defaultSelectedConfidentColor;
                    selectedDoubtfulColor = defaultSelectedDoubtfulColor;
                    selectedNotValidatedColor = defaultSelectedNotValidatedColor;
                    selectedNotAvailableColor = defaultSelectedNotAvailableColor;
                } else {
                    selectedConfidentColor = defaultUnselectedConfidenColor;
                    selectedDoubtfulColor = defaultUnselectedDoubtfulColor;
                    selectedNotValidatedColor = defaultUnselectedNotValidatedColor;
                    selectedNotAvailableColor = defaultUnselectedNotAvailableColor;
                }
                peptidesQueryMap.fieldNames().forEach((key) -> {
                    JsonArray arr = peptidesQueryMap.getJsonArray(key);
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject peptide = arr.getJsonObject(i);
                        switch (peptide.getString("validation")) {
                            case CONSTANT.VALIDATION_DOUBTFUL:
                                peptide.put("color", initColorMap(selectedDoubtfulColor));
                                break;
                            case CONSTANT.VALIDATION_CONFIDENT:
                                peptide.put("color", initColorMap(selectedConfidentColor));
                                break;
                            case CONSTANT.NO_INFORMATION:
                                peptide.put("color", initColorMap(selectedNotValidatedColor));
                                break;
                            default:
                                peptide.put("color", initColorMap(selectedNotAvailableColor));
                                break;

                        }

                        entriesSet.add(peptide);
                    }
                });
                if (peptideKey != null && peptidesQueryMap.containsKey(peptideKey)) {
                    JsonArray arr = peptidesQueryMap.getJsonArray(peptideKey);
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject peptide = arr.getJsonObject(i);
                        if (chainId.contains("All") || chainId.equalsIgnoreCase(peptide.getString("struct_asym_id"))) {
                            switch (peptide.getString("validation")) {
                                case CONSTANT.VALIDATION_DOUBTFUL:
                                    peptide.put("color", initColorMap(defaultSelectedDoubtfulColor));
                                    break;
                                case CONSTANT.VALIDATION_CONFIDENT:
                                    peptide.put("color", initColorMap(defaultSelectedConfidentColor));
                                    break;
                                case CONSTANT.NO_INFORMATION:
                                    peptide.put("color", initColorMap(defaultSelectedNotValidatedColor));
                                    break;
                                default:
                                    peptide.put("color", initColorMap(defaultSelectedNotAvailableColor));
                                    break;
                            }
                            entriesSet.add(peptide);
                        }
                    }
                }
                break;
            }
            case 3: {

                Map<String, Color> peptideOverlappingMap = new HashMap<>();
                peptidesQueryMap.fieldNames().forEach((key) -> {
                    JsonArray arr = peptidesQueryMap.getJsonArray(key);
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject peptide = arr.getJsonObject(i);
                        if ((chainId.contains("All") || chainId.equalsIgnoreCase(peptide.getString("struct_asym_id")))) {
                            JsonArray varModifications = peptide.getJsonArray("modifications");// 
                            if (!peptide.getBoolean("modified")) {
                                /**
                                 * case of select all*
                                 */
                                if (peptideKey == null || peptideKey.contains("null")) {
                                    peptide.put("color", initColorMap(new int[]{211, 211, 211}));
                                } else {
                                    peptide.put("color", initColorMap(new int[]{245, 245, 245}));
                                }
                            } else if (varModifications.size() > 0) {
                                Color c = Color.ORANGE;
                                Set<String> modificationNames = new HashSet<>();
                                for (Object mach : varModifications) {
                                    modificationNames.add(mach.toString());
                                }
                                if (modificationNames.size() == 1) {
                                    c = new Color(PTM.getColor(varModifications.getString(0)));
                                }

                                if (peptideKey == null || peptideKey.contains("null")) {
                                    peptide.put("color", initColorMap(c));
                                } else {

                                    if (c.brighter().toString().equalsIgnoreCase(c.toString())) {
                                        c = new Color(Math.max(c.getRed(), 100), Math.max(c.getGreen(), 100), Math.max(c.getBlue(), 100));
                                        peptide.put("color", initColorMap(c));
                                    } else {
                                        c = c.brighter().brighter();
                                        peptide.put("color", initColorMap(c));
                                    }
                                }
                                if (!peptideOverlappingMap.containsKey(peptide.getString("sequence_key"))) {
                                    peptideOverlappingMap.put(peptide.getString("sequence_key"), c);
                                } else {
                                    if (!peptideOverlappingMap.get(peptide.getString("sequence_key")).toString().equalsIgnoreCase(c.toString())) {
                                        if (peptideKey == null || peptideKey.contains("null")) {
                                            peptideOverlappingMap.put(peptide.getString("sequence_key"), Color.ORANGE);
                                        } else {
                                            peptideOverlappingMap.put(peptide.getString("sequence_key"), Color.ORANGE.brighter().brighter());
                                        }
                                    }
                                }
                            }
                            entriesSet.add(peptide);
                        }
                    }
                });
                for (int i = 0; i < entriesSet.size(); i++) {
                    JsonObject peptide = entriesSet.getJsonObject(i);
                    if (peptideOverlappingMap.containsKey(peptide.getString("sequence_key"))) {
                        peptide.put("color", initColorMap(peptideOverlappingMap.get(peptide.getString("sequence_key"))));
                    }
                }
                if (peptideKey != null && peptidesQueryMap.containsKey(peptideKey)) {
                    JsonArray arr = peptidesQueryMap.getJsonArray(peptideKey);
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject peptide = arr.getJsonObject(i);
                        if (chainId.contains("All") || chainId.equalsIgnoreCase(peptide.getString("struct_asym_id"))) {
                            if (!peptide.getBoolean("modified")) {
                                peptide.put("color", initColorMap(Color.GRAY.darker()));
                            } else {
                                Color c = Color.ORANGE;
                                JsonArray modArr = peptide.getJsonArray("modifications");
                                if (modArr.size() == 1) {
                                    c = new Color(PTM.getColor(modArr.getString(0)));
                                }
                                peptide.put("color", initColorMap(c));

                            }
                            entriesSet.add(peptide);
                        }
                    }
                }
                break;
            }

            case 4: {

                peptidesQueryMap.fieldNames().forEach((key) -> {
                    JsonArray arr = peptidesQueryMap.getJsonArray(key);
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject peptide = arr.getJsonObject(i);
                        if (key == null || key.contains("null")) {
                            int[] colorArr = new int[3];
                            String psmColor = peptide.getString("psm").substring(4, peptide.getString("psm").length() - 1);
                            int index = 0;
                            for (String c : psmColor.split(",")) {
                                colorArr[index++] = Integer.parseInt(c);
                            }
                            peptide.put("color", initColorMap(colorArr));

                        } else {
                            int[] colorArr = new int[3];
                            String psmColor = peptide.getString("psm").substring(4, peptide.getString("psm").length() - 1);
                            int index = 0;
                            for (String c : psmColor.split(",")) {
                                colorArr[index++] = Integer.parseInt(c);
                            }
                            Color unselected = new Color(colorArr[0], colorArr[1], colorArr[2]).brighter();

                            peptide.put("color", initColorMap(unselected));

                        }
                        entriesSet.add(peptide);
                    }

                });
                if (peptideKey != null && !peptideKey.contains("null") && peptidesQueryMap.containsKey(peptideKey)) {

                    JsonArray arr = peptidesQueryMap.getJsonArray(peptideKey);
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject peptide = arr.getJsonObject(i);
                        int[] colorArr = new int[3];
                        String psmColor = peptide.getString("psm").substring(4, peptide.getString("psm").length() - 1);
                        int index = 0;
                        for (String c : psmColor.split(",")) {
                            colorArr[index++] = Integer.parseInt(c);
                        }
                        peptide.put("color", initColorMap(colorArr));
                        entriesSet.add(peptide);
                    }

                }
                break;
            }
            case 5: {
                peptidesQueryMap.fieldNames().forEach((key) -> {
                    try {
                        JsonArray arr = peptidesQueryMap.getJsonArray(key);
                        for (int i = 0; i < arr.size(); i++) {
                            JsonObject peptide = arr.getJsonObject(i);
                            if (peptideKey == null || peptideKey.contains("null")) {

                                String intensityColor = peptide.getString("intensity").substring(4, peptide.getString("intensity").length() - 1);
                                if (intensityColor.contains("%")) {
                                    String[] colorArr = new String[3];
                                    int index = 0;
                                    for (String c : intensityColor.split(",")) {
                                        colorArr[index++] = c;
                                    }
                                    peptide.put("color", initColorMap(colorArr));
                                } else {
                                    int[] colorArr = new int[3];
                                    int index = 0;
                                    for (String c : intensityColor.split(",")) {
                                        colorArr[index++] = Integer.parseInt(c);
                                    }
                                    peptide.put("color", initColorMap(colorArr));
                                }

                            } else {

                                String intensityColor = peptide.getString("intensity").substring(4, peptide.getString("intensity").length() - 1);
                                if (intensityColor.contains("%")) {
                                    String[] colorArr = new String[3];
                                    int index = 0;
                                    for (String c : intensityColor.split(",")) {
                                        colorArr[index++] = c;
                                    }
                                    colorArr[2] = "90%";
                                    peptide.put("color", initColorMap(colorArr));
                                } else {
                                    int[] colorArr = new int[3];
                                    int index = 0;
                                    for (String c : intensityColor.split(",")) {
                                        colorArr[index++] = Integer.parseInt(c);
                                    }
                                    Color unselected = new Color(colorArr[0], colorArr[1], colorArr[2]).brighter();
                                    peptide.put("color", initColorMap(unselected));
                                }

                            }
                            entriesSet.add(peptide);

                        }

                    } catch (NumberFormatException exp) {
                        exp.printStackTrace();
                    }

                });

                if (peptideKey != null && !peptideKey.contains("null") && peptidesQueryMap.containsKey(peptideKey)) {
                    JsonArray arr = peptidesQueryMap.getJsonArray(peptideKey);
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject peptide = arr.getJsonObject(i);
                        int[] colorArr = new int[3];
                        String intensityColor = peptide.getString("intensity").substring(4, peptide.getString("intensity").length() - 1);
                        int index = 0;
                        for (String c : intensityColor.split(",")) {
                            colorArr[index++] = Integer.parseInt(c);
                        }
                        peptide.put("color", initColorMap(colorArr));
                        entriesSet.add(peptide);
                    }
                }

                break;
            }

        }
        String pdbAccession = (pdbMatchesSelect.getValue() + "").toLowerCase();
        liteMOL3DComponent.excuteQuery(pdbAccession, lastSelectedMatch.getEntity_id(chainId), chainId.toUpperCase(), initColorMap(basicColor), entriesSet);

    }

    private void contsructQueries(List<ChainBlock> selectedBlocks) {
        peptidesQueryMap.clear();
        this.proteinPeptidesMap.clear();
        this.proteinPeptides.forEach((peptide) -> {
            this.proteinPeptidesMap.put(peptide.getModifiedSequence(), peptide);
            this.peptidesQueryMap.put(peptide.getModifiedSequence(), new ArrayList<>());
            int start;
            Set<String> selectedChains = new LinkedHashSet<>();
            selectedBlocks.forEach((chain) -> {
                selectedChains.add(chain.getChain_id());
            });
            for (String chainId : selectedChains) {
                int current = 0;
                for (String seq : lastSelectedMatch.getSequence(chainId)) {
                    if (seq.toLowerCase().replace("i", "l").contains(peptide.getSequence().toLowerCase().replaceAll("i", "l"))) {
                        for (ChainBlock c : lastSelectedMatch.getChainBlocks(chainId)) {
                            start = seq.toLowerCase().replaceAll("i", "l").indexOf(peptide.getSequence().toLowerCase().replaceAll("i", "l"), current) + c.getStart_residue_number();
                            if (start == 0) {
                                continue;
                            }
                            int end = start + peptide.getSequence().length() - 1;
                            current = end;
                            ModificationMatch[] varMod = peptide.getVariableModifications();
                            peptide.setInvisibleOn3d(false);
                            peptidesQueryMap.getJsonArray(peptide.getModifiedSequence()).add(initSequenceMap(chainId, lastSelectedMatch.getEntity_id(chainId), start, end, peptide.getValidation(), (varMod.length > 0), varMod, peptide.getSequence(), peptide.getPsmColor(), peptide.getIntensityColor()));
                            break;
                        }

                    } else {
                        peptide.setInvisibleOn3d(true);
                    }
                }
            }
        });

    }

    private JsonObject initSequenceMap(String chainId, int entity_id, int start, int end, String validation, boolean modified, ModificationMatch[] modifications, String sequence, String psmNumberColor, String intensityColor) {
        JsonObject sequenceMap = new JsonObject();
        sequenceMap.put("entity_id", entity_id + "");
        sequenceMap.put("struct_asym_id", chainId.toUpperCase());
        sequenceMap.put("start_residue_number", start);
        sequenceMap.put("end_residue_number", end);
        sequenceMap.put("sequence_key", start + "_" + end);
        sequenceMap.put("validation", validation);
        sequenceMap.put("modified", modified);
        JsonArray modificationsAsJson = new JsonArray();
        if (modifications != null) {
            for (ModificationMatch mach : modifications) {
                modificationsAsJson.add(mach.getModification());
            }

        }
        sequenceMap.put("modifications", modificationsAsJson);
        sequenceMap.put("sequence", sequence);
        sequenceMap.put("psm", psmNumberColor);
        sequenceMap.put("intensity", intensityColor);
        return sequenceMap;

    }

    private JsonObject initColorMap(int[] color) {
        JsonObject colorMap = new JsonObject();
        colorMap.put("r", color[0]);
        colorMap.put("g", color[1]);
        colorMap.put("b", color[2]);
        return colorMap;
    }

    private JsonObject initColorMap(String[] color) {
        JsonObject colorMap = new JsonObject();
        colorMap.put("h", color[0]);
        colorMap.put("s", color[1]);
        colorMap.put("l", color[2]);
        return colorMap;
    }

    private JsonObject initColorMap(Color color) {
        JsonObject colorMap = new JsonObject();
        colorMap.put("r", color.getRed());
        colorMap.put("g", color.getGreen());
        colorMap.put("b", color.getBlue());
        return colorMap;
    }

    public void setMode(int moleculeMode) {
        if (this.moleculeMode == moleculeMode) {
            return;
        }
        this.moleculeMode = moleculeMode;
        selectPeptide(lastSelectedPeptideKey);
    }

    public AbsoluteLayout getChainCoverageLayout() {
        return chainCoverageLayout;
    }

}
